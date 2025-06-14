package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.specification.HbPartnerSpecification;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.dto.HbPartnerRequestDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.HbPartnerService;
import com.nexage.app.util.assemblers.HbPartnerAssembler;
import com.nexage.app.util.validator.HbPartnerValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service("hbPartnerService")
@PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()")
@Transactional
public class HbPartnerServiceImpl implements HbPartnerService {

  private final HbPartnerRepository hbPartnerRepository;

  private final SiteRepository siteRepository;

  private final PositionRepository positionRepository;

  private final HbPartnerAssembler hbPartnerAssembler;

  private final CompanyRepository companyRepository;

  private final UserContext userContext;

  private final HbPartnerValidator hbPartnerValidator;

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public Page<HbPartnerDTO> getHbPartners(HbPartnerRequestDTO request) {
    if (request.getSiteId() != null) {
      return getHbPartnersForSite(request);
    } else if (request.getSellerId() != null) {
      return getHbPartnersForSeller(request);
    } else {
      return getAllHbPartners(request);
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public HbPartnerDTO getHbPartner(Long pid) {
    Optional<HbPartner> hbPartner = hbPartnerRepository.findById(pid);
    if (!hbPartner.isPresent()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
    }
    return hbPartnerAssembler.make(hbPartner.get());
  }

  @Override
  public HbPartnerDTO createHbPartner(HbPartnerDTO hbPartnerDTO) {
    hbPartnerValidator.isValidForCreate(hbPartnerDTO);
    HbPartner hbPartner = new HbPartner();
    try {
      hbPartner = hbPartnerAssembler.apply(hbPartner, hbPartnerDTO);
    } catch (Exception e) {
      log.error("Error, unable to load hb partners json");
      throw new RuntimeException("Unable to load hb partners json");
    }
    hbPartnerRepository.save(hbPartner);
    return hbPartnerAssembler.make(hbPartner);
  }

  @Override
  public HbPartnerDTO updateHbPartner(HbPartnerDTO hbPartnerDTO) {
    Optional<HbPartner> hbPartner = hbPartnerRepository.findById(hbPartnerDTO.getPid());
    if (!hbPartner.isPresent()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
    }
    hbPartnerValidator.isValidForUpdate(hbPartnerDTO, hbPartner.get());
    try {
      hbPartnerAssembler.apply(hbPartner.get(), hbPartnerDTO);
    } catch (Exception e) {
      log.error("Error, unable to load hb partners json");
      throw new RuntimeException("Unable to load hb partners json");
    }
    return hbPartnerAssembler.make(hbPartnerRepository.saveAndFlush(hbPartner.get()));
  }

  @Override
  public void deactivateHbPartner(Long pid) {
    Optional<HbPartner> hbPartner = hbPartnerRepository.findById(pid);
    if (!hbPartner.isPresent()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
    }
    validateHbPartnerAssociationsOnDelete(hbPartner.get().getPid());
    hbPartnerRepository.deleteById(pid);
  }

  public void validateHbPartnerAssociationsOnDelete(Long hbPartnerPid) {
    if (companyRepository.countCompaniesAssociatedToHbPartners(
            Collections.singletonList(hbPartnerPid))
        > 0) throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_DELETE_INVALID);
  }

  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or "
          + "@loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder()")
  @Override
  public List<Long> findPidsByCompanyPid(long companyPid) {
    return hbPartnerRepository.findPidsByCompanyPid(companyPid);
  }

  private Page<HbPartnerDTO> getAllHbPartners(HbPartnerRequestDTO request) {
    if (userContext.isNexageUser()) {
      return hbPartnerRepository
          .findAll(request.getPageable())
          .map(
              p ->
                  request.isDetail()
                      ? hbPartnerAssembler.make(p)
                      : hbPartnerAssembler.make(p, HbPartnerAssembler.SUMMARY_FIELDS));
    }

    log.error("User is not authorized to see all hb-partners resources");
    throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }

  private Page<HbPartnerDTO> getHbPartnersForSeller(HbPartnerRequestDTO request) {
    if (userContext.doSameOrNexageAffiliation(request.getSellerId())) {
      Map<Long, Long> defaultSites =
          partnersListToMap(siteRepository.findDefaultSitesPerPartners(request.getSellerId()));

      return hbPartnerRepository
          .findAll(
              HbPartnerSpecification.withSellerPid(request.getSellerId()), request.getPageable())
          .map(
              p -> {
                HbPartnerDTO dto =
                    request.isDetail()
                        ? hbPartnerAssembler.make(p)
                        : hbPartnerAssembler.make(p, HbPartnerAssembler.SUMMARY_FIELDS);
                dto.setDefaultSite(defaultSites.get(dto.getPid()));
                return dto;
              });
    }

    log.error("User is not authorized to see hb-partners for sellerId: {}", request.getSellerId());
    throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }

  private Page<HbPartnerDTO> getHbPartnersForSite(HbPartnerRequestDTO request) {
    if (userContext.canAccessSite(request.getSiteId())) {
      List<HbPartnersAssociationView> associations =
          positionRepository.findDefaultPositionsPerPartners(request.getSiteId());

      Map<Long, Long> defaultPositions =
          partnersListToMapForPositions(associations, AssociationType.DEFAULT);
      Map<Long, Long> defaultBannerPositions =
          partnersListToMapForPositions(associations, AssociationType.DEFAULT_BANNER);
      Map<Long, Long> defaultVideoPositions =
          partnersListToMapForPositions(associations, AssociationType.DEFAULT_VIDEO);

      return hbPartnerRepository
          .findAll(HbPartnerSpecification.withSitePid(request.getSiteId()), request.getPageable())
          .map(
              p -> {
                HbPartnerDTO dto =
                    request.isDetail()
                        ? hbPartnerAssembler.make(p)
                        : hbPartnerAssembler.make(p, HbPartnerAssembler.SUMMARY_FIELDS);
                dto.setDefaultPlacement(defaultPositions.get(dto.getPid()));
                dto.setBannerDefaultPlacement(defaultBannerPositions.get(dto.getPid()));
                dto.setVideoDefaultPlacement(defaultVideoPositions.get(dto.getPid()));

                return dto;
              });
    }

    log.error("User is not authorized to see hb-partners for site: {}", request.getSiteId());
    throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }

  private Map<Long, Long> partnersListToMap(List<HbPartnersAssociationView> associations) {
    return associations.stream()
        .filter(p -> p.getHbPartnerPid() != null && p.getPid() != null)
        .collect(
            Collectors.toMap(
                HbPartnersAssociationView::getHbPartnerPid, HbPartnersAssociationView::getPid));
  }

  private Map<Long, Long> partnersListToMapForPositions(
      List<HbPartnersAssociationView> associations, AssociationType associationType) {
    return associations.stream()
        .filter(
            p ->
                p.getHbPartnerPid() != null
                    && p.getPid() != null
                    && AssociationType.getFromValue(p.getType()).equals(associationType))
        .collect(
            Collectors.toMap(
                HbPartnersAssociationView::getHbPartnerPid, HbPartnersAssociationView::getPid));
  }
}
