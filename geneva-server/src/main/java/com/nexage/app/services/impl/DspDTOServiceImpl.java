package com.nexage.app.services.impl;

import com.google.common.base.Strings;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.specification.CompanySpecification;
import com.nexage.app.dto.dsp.DspDTO;
import com.nexage.app.dto.dsp.DspSummaryDTO;
import com.nexage.app.mapper.DspDTOMapper;
import com.nexage.app.mapper.DspSummaryDTOMapper;
import com.nexage.app.services.DspDTOService;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
public class DspDTOServiceImpl implements DspDTOService {

  private final CompanyRepository companyRepository;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeller() OR @loginUserContext.isOcUserSellerSeat()")
  public Page<DspDTO> findAll(
      final Set<String> qf, final String qt, final Pageable pageable, final boolean isRtbEnabled) {
    validateSearchParamRequest(qf, Company.class);
    return companyRepository
        .findAll(CompanySpecification.ofBuyerTypeWith(qf, qt, isRtbEnabled), pageable)
        .map(DspDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeller() OR @loginUserContext.isOcUserSellerSeat()")
  public Page<DspSummaryDTO> findAllSummary(
      Set<String> qf, String qt, Pageable pageable, boolean isRtbEnabled) {
    validateSearchParamRequest(qf, Company.class);
    if (!CollectionUtils.isEmpty(qf) && !Strings.isNullOrEmpty(qt)) {
      if (qf.contains(Company_.NAME)) {
        return companyRepository
            .findAll(CompanySpecification.ofBuyerTypeWith(qf, qt, isRtbEnabled), pageable)
            .map(DspSummaryDTOMapper.MAPPER::map);
      } else {
        log.info("Unable to perform operation for qf={}", qf);
      }
    }
    return companyRepository
        .findAll(CompanySpecification.ofBuyerTypeWith(qf, qt, isRtbEnabled), pageable)
        .map(DspSummaryDTOMapper.MAPPER::map);
  }

  private void validateSearchParamRequest(Set<String> qf, Class classType) {
    if (!SearchRequestParamValidator.isValid(qf, classType)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }
}
