package com.nexage.app.services.impl;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.specification.SellerDealSpecification;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DirectDealDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.DealDTOService;
import com.nexage.app.services.DealService;
import com.nexage.app.services.DirectDealService;
import com.nexage.app.services.SellerDealService;
import com.nexage.app.services.validation.SellerDealValidator;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SellerDealServiceImpl implements SellerDealService {

  private final DirectDealRepository directDealRepository;
  private final DirectDealService directDealService;
  private final SiteRepository siteRepository;
  private final PositionRepository positionRepository;
  private final DealService dealService;
  private final DealDTOService dealDTOService;
  private final UserContext userContext;
  private final SellerDealValidator sellerDealValidator;

  @Autowired
  public SellerDealServiceImpl(
      DirectDealRepository directDealRepository,
      DirectDealService directDealService,
      SiteRepository siteRepository,
      PositionRepository positionRepository,
      DealService dealService,
      DealDTOService dealDTOService,
      UserContext userContext,
      SellerDealValidator sellerDealValidator) {
    this.directDealRepository = directDealRepository;
    this.directDealService = directDealService;
    this.siteRepository = siteRepository;
    this.positionRepository = positionRepository;
    this.dealService = dealService;
    this.dealDTOService = dealDTOService;
    this.userContext = userContext;
    this.sellerDealValidator = sellerDealValidator;
  }

  private static final String DEAL_CATEGORY = "dealCategory";

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) && @loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public Page<DirectDeal> getPagedDealsAssociatedWithSeller(
      Long sellerId, Set<String> qf, String qt, Pageable pageable) {
    var paramMap = dealDTOService.createMultiValueMap(qf);
    var userDetail = userContext.isNexageUser();
    if (paramMap.isPresent()) {
      validateSearchParamRequest(paramMap.get().keySet(), DirectDealDTO.class);
    } else {
      validateFilterParameters(qf, qt);
    }
    if (qf != null && paramMap.isPresent() && paramMap.get().containsKey(DEAL_CATEGORY)) {
      dealDTOService.validateDealCategoryIfPresent(paramMap.get());
    }

    return paramMap.isPresent()
        ? directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(sellerId, paramMap.get(), userDetail),
            pageable)
        : directDealRepository.findAll(
            SellerDealSpecification.buildSpecification(sellerId, qf, qt, userDetail), pageable);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) && @loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public DirectDealDTO getDealAssociatedWithSeller(Long sellerId, Long pid) {
    var userDetails = userContext.isNexageUser();
    DirectDealDTO directDeal =
        directDealRepository
            .findOne(SellerDealSpecification.buildSpecification(sellerId, pid, userDetails))
            .map(DirectDealDTOMapper.MAPPER::map)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));

    filterOutSitesWhichDoNotBelongToSeller(directDeal, sellerId);
    filterOutPositionsWhichDoNotBelongToSeller(directDeal, sellerId);
    filterOutPublishersWithPubIdThatDoNotMatchGivenSellerId(directDeal, sellerId);
    return directDeal;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public List<PublisherSitePositionDTO> getPublisherMapForDeal(Long sellerId, Long dealPid) {
    var userDetails = userContext.isNexageUser();
    DirectDeal directDeal =
        directDealRepository
            .findOne(SellerDealSpecification.buildSpecification(sellerId, dealPid, userDetails))
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));

    Set<Long> sellerSites =
        siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(Set.of(sellerId));

    List<DealSite> dealSites =
        directDeal.getSites().stream()
            .filter(site -> sellerSites.contains(site.getSitePid()))
            .collect(Collectors.toList());
    List<DealPosition> dealPositions =
        directDeal.getPositions().stream()
            .filter(
                position ->
                    position.getPositionView().getSiteView().getCompanyPid().equals(sellerId))
            .collect(Collectors.toList());

    return directDealService.getPublisherMapForDeal(dealSites, dealPositions);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()) && @loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public DirectDealDTO createDealAssociatedWithSeller(Long sellerId, DirectDealDTO directDealDTO) {
    validate(sellerId, directDealDTO);
    return dealService.createDeal(directDealDTO);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()) && @loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public DirectDealDTO updateDealAssociatedWithSeller(
      Long sellerId, Long pid, DirectDealDTO directDealDTO) {
    if (directDealDTO != null && directDealDTO.getDealCategory() == null) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SELLER_DEAL_CATEGORY_CANNOT_BE_NULL);
    }
    validate(sellerId, directDealDTO);
    return dealService.updateDeal(pid, directDealDTO);
  }

  private void validate(Long sellerId, DirectDealDTO directDealDTO) {
    if (sellerId != null && directDealDTO != null) {
      sellerDealValidator.validateSeller(sellerId, directDealDTO);
      sellerDealValidator.areAllSellerSitesAllowedForUser(sellerId, directDealDTO);
      sellerDealValidator.validateDealCategory(directDealDTO);
      sellerDealValidator.validateVisibility(directDealDTO);
    } else {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private void validateFilterParameters(Set<String> qf, String qt) {
    if (!CollectionUtils.isEmpty(qf)
        && (!SellerDealSpecification.QUERYABLE_FIELDS.containsAll(qf) || StringUtils.isBlank(qt))) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private void validateSearchParamRequest(Set<String> qf, Class classType) {
    if (!(SearchRequestParamValidator.isValid(qf, classType))) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private void filterOutPublishersWithPubIdThatDoNotMatchGivenSellerId(
      DirectDealDTO directDealDTO, Long sellerId) {
    if (directDealDTO.getSellers() != null && !directDealDTO.getSellers().isEmpty()) {
      directDealDTO
          .getSellers()
          .removeIf(dealPublisherDTO -> !dealPublisherDTO.getPublisherPid().equals(sellerId));
    }
  }

  private void filterOutSitesWhichDoNotBelongToSeller(DirectDealDTO directDealDTO, Long sellerId) {
    if (directDealDTO.getSites() != null && !directDealDTO.getSites().isEmpty()) {
      Set<Long> sellerSites =
          siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(Set.of(sellerId));
      directDealDTO
          .getSites()
          .removeIf(dealSiteDTO -> !sellerSites.contains(dealSiteDTO.getSitePid()));
    }
  }

  private void filterOutPositionsWhichDoNotBelongToSeller(
      DirectDealDTO directDealDTO, Long sellerId) {
    if (directDealDTO.getPositions() != null && !directDealDTO.getPositions().isEmpty()) {
      Set<Long> sellerPositions = positionRepository.findPidsByCompanyPid(sellerId);
      directDealDTO
          .getPositions()
          .removeIf(dealPositionDTO -> !sellerPositions.contains(dealPositionDTO.getPositionPid()));
    }
  }
}
