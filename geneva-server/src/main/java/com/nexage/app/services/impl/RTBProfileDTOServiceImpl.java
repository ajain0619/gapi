package com.nexage.app.services.impl;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.specification.PositionSpecification;
import com.nexage.app.dto.RTBProfileDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.RTBProfileDTOMapper;
import com.nexage.app.services.RTBProfileDTOService;
import com.nexage.app.util.validator.RTBProfileValidator;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service("rtbProfileDTOService")
@Transactional(readOnly = true)
public class RTBProfileDTOServiceImpl implements RTBProfileDTOService {

  private final RTBProfileRepository rtbProfileRepository;
  private final PositionRepository positionRepository;
  private final CompanyRepository companyRepository;
  private final RTBProfileValidator rtbProfileValidator;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#sellerPid)")
  public Page<RTBProfileDTO> getRTBProfiles(
      Pageable pageable, Long sellerPid, String qt, Set<String> qf) {

    validateSearchParamRequest(qf);

    if (!companyRepository.existsById(sellerPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    Long profilePid = rtbProfileRepository.getDefaultRTBProfileBySellerPid(sellerPid);
    if (Objects.isNull(profilePid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND);
    }

    Page<RTBProfileDTO> profiles;
    if (StringUtils.isNotEmpty(qt) && ObjectUtils.equals(qf, Set.of("name"))) {
      profiles =
          rtbProfileRepository
              .findByDefaultRtbProfileOwnerCompanyPidAndNameLike(
                  sellerPid, '%' + qt + '%', pageable)
              .map(RTBProfileDTOMapper.MAPPER::map);
    } else {
      profiles =
          rtbProfileRepository
              .findByDefaultRtbProfileOwnerCompanyPid(sellerPid, pageable)
              .map(RTBProfileDTOMapper.MAPPER::map);
    }

    profiles.forEach(
        profile -> {
          profile.setNumberOfEffectivePlacements(
              positionRepository.count(
                  PositionSpecification.withDefaultRtbProfiles(profile.getPid())));
          if (profile.getPid().longValue() == profilePid.longValue()) {
            profile.setPublisherDefault(true);
          }
        });
    return profiles;
  }

  /** {@inheritDoc} */
  @Override
  public RTBProfileDTO update(Long sellerPid, RTBProfileDTO rtbProfileDTO, long rtbPid) {

    Optional<RTBProfile> rtbProfile = rtbProfileRepository.findById(rtbPid);
    rtbProfileValidator.validateUpdate(sellerPid, rtbProfile);
    RTBProfileDTOMapper.MAPPER.map(rtbProfileDTO, rtbProfile.get());
    rtbProfileRepository.saveAndFlush(rtbProfile.get());
    return RTBProfileDTOMapper.MAPPER.map(rtbProfile.get());
  }

  private void validateSearchParamRequest(Set<String> qf) {
    if (!SearchRequestParamValidator.isValid(qf, RTBProfileDTO.class)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }
}
