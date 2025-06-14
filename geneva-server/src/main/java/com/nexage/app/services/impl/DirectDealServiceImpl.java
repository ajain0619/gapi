package com.nexage.app.services.impl;

import static com.nexage.admin.core.specification.DealRtbProfileViewSpecification.hasNullDefaultRtbProfileOwnerCompanyPid;
import static com.nexage.admin.core.specification.DealRtbProfileViewSpecification.isNotDeleted;
import static com.nexage.app.util.validator.deals.DealCategoryValidator.validateCreateDirectDealCategory;
import static com.nexage.app.util.validator.deals.DealCategoryValidator.validateUpdateDirectDealCategory;
import static java.util.stream.Collectors.toList;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.DealRtbProfileViewRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.DirectDealViewRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.admin.core.repository.SiteViewRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.admin.core.specification.DirectDealSpecification;
import com.nexage.admin.core.specification.SpecificationUtils;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBuyerDTO;
import com.nexage.app.dto.deal.PositionNameDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deal.SitePostionDTO;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DealDTOMapper;
import com.nexage.app.mapper.deal.DirectDealDTOMapper;
import com.nexage.app.mapper.deal.DirectDealDTOMapperWithoutSuppliersAndBidders;
import com.nexage.app.services.DealService;
import com.nexage.app.services.DirectDealService;
import com.nexage.app.util.assemblers.sellingrule.RuleAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class DirectDealServiceImpl implements DirectDealService {

  private final SiteViewRepository siteViewRepository;
  private final DealRtbProfileViewRepository dealRtbProfileViewRepository;
  private final RuleRepository ruleRepository;
  private final RuleAssembler ruleAssembler;
  private final DirectDealRepository directDealRepository;
  private final DealService dealService;

  @Autowired
  public DirectDealServiceImpl(
      SiteViewRepository siteViewRepository,
      DealRtbProfileViewRepository dealRtbProfileViewRepository,
      RuleRepository ruleRepository,
      RuleAssembler ruleAssembler,
      DirectDealRepository directDealRepository,
      DealService dealService,
      DirectDealViewRepository directDealViewRepository) {
    this.siteViewRepository = siteViewRepository;
    this.dealRtbProfileViewRepository = dealRtbProfileViewRepository;
    this.ruleRepository = ruleRepository;
    this.ruleAssembler = ruleAssembler;
    this.directDealRepository = directDealRepository;
    this.dealService = dealService;
  }

  @Override
  public DirectDealDTO createDeal(DirectDealDTO deal) {
    validateCreateDirectDealCategory(deal);
    return dealService.createDeal(deal);
  }

  @Override
  public List<DirectDealDTO> getAllDeals() {
    List<DirectDealDTO> dtos = new ArrayList<>();
    List<DirectDeal> deals = directDealRepository.findAll();
    for (DirectDeal deal : deals) {
      DirectDealDTO dto = DirectDealDTOMapperWithoutSuppliersAndBidders.MAPPER.map(deal);
      dtos.add(dto);
    }
    return dtos;
  }

  @Override
  public List<DirectDealDTO> getAllDealsWithRules() {
    List<DirectDealDTO> dtos = new ArrayList<>();
    List<DirectDeal> deals = directDealRepository.findByRulesNotNull();
    for (DirectDeal deal : deals) {
      DirectDealDTO dto = DirectDealDTOMapperWithoutSuppliersAndBidders.MAPPER.map(deal);
      dtos.add(dto);
    }
    return dtos;
  }

  @Deprecated
  @Override
  public Page<DealDTO> getPagedDealsWithRules(Optional<String> qt, Pageable pageable) {
    Optional<Specification<DirectDeal>> spec;
    Optional<String> searchTerm = Optional.of("");
    Optional<String> searchValue = Optional.of("");

    if (qt.isPresent() && qt.get().contains(":")) {
      String[] search = StringUtils.split(qt.get(), ":");
      searchTerm = Optional.of(search[0]);
      searchValue = Optional.of(search[1]);
    }

    switch (searchTerm.get()) {
      case "description":
        spec = searchValue.map(DirectDealSpecification::withDescription);
        break;
      case "tier":
        try {
          spec = searchValue.map(DirectDealSpecification::withTier);
        } catch (IllegalArgumentException e) {
          throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
        }
        break;
      default:
      case "dealId":
        spec = searchValue.map(DirectDealSpecification::withDealId);
    }
    if (spec.isPresent()) {
      return SpecificationUtils.conjunction(spec)
          .map(specs -> directDealRepository.findAll(specs, pageable))
          .orElseThrow(
              () -> new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_DEALS))
          .map(DealDTOMapper.MAPPER::map);
    } else {
      return directDealRepository
          .findAll(DirectDealSpecification.withRules(), pageable)
          .map(DealDTOMapper.MAPPER::map);
    }
  }

  @Override
  public DirectDealDTO getDeal(long dealPid) {
    DirectDeal coreDeal =
        directDealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));
    return DirectDealDTOMapper.MAPPER.map(coreDeal);
  }

  /**
   * Get sites names and pids plus positions names and pids assigned to the given {@link
   * DirectDeal}.
   *
   * @param dealPid PID of the {@link DirectDeal}
   */
  @Override
  public List<PublisherSitePositionDTO> getPublisherMapForDeal(long dealPid) {
    DirectDeal coreDeal =
        directDealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));
    return getPublisherMapForDeal(coreDeal.getSites(), coreDeal.getPositions());
  }

  /**
   * Get sites names and pids plus positions names and pids for the given deals sites and deals
   * positions.
   *
   * @param sites deals sites
   * @param positions deals positions
   */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public List<PublisherSitePositionDTO> getPublisherMapForDeal(
      List<DealSite> sites, List<DealPosition> positions) {
    List<PublisherSitePositionDTO> result = new ArrayList<>();

    // site associations
    if (!sites.isEmpty()) {
      // get site pids - assuming no duplicates, no dedup mechanism in place
      List<Long> associatedSitePids =
          sites.stream().map(DealSite::getSitePid).collect(Collectors.toList());

      // load SiteDTOs for that list of site pids
      List<SiteView> sitesDtos = new ArrayList<>();
      if (!associatedSitePids.isEmpty())
        sitesDtos = siteViewRepository.findAllById(associatedSitePids);

      // group sites by company
      Map<Company, List<SiteView>> companyToSitesMap =
          sitesDtos.stream().collect(Collectors.groupingBy(SiteView::getCompany));

      // convert the map into our desired dtos
      result.addAll(
          companyToSitesMap.entrySet().stream()
              .map(entry -> convertSitesToDTO(entry.getKey(), entry.getValue()))
              .collect(Collectors.toList()));
    }

    // position associations
    if (!positions.isEmpty()) {

      List<PositionView> associatedPositions =
          positions.stream().map(DealPosition::getPositionView).collect(Collectors.toList());

      Map<Company, List<PositionView>> companyPidToPositions =
          associatedPositions.stream()
              .collect(Collectors.groupingBy(p -> p.getSiteView().getCompany()));

      result.addAll(
          companyPidToPositions.entrySet().stream()
              .map(entry -> convertPositionsToDTO(entry.getKey(), entry.getValue()))
              .collect(Collectors.toList()));
    }

    return result;
  }

  @Override
  public DirectDealDTO updateDeal(long dealPid, DirectDealDTO dealDto) {
    DirectDeal deal = directDealRepository.findById(dealPid).orElse(null);
    validateUpdateDirectDealCategory(deal, dealDto);
    return dealService.updateDeal(dealPid, dealDto);
  }

  @Override
  public void updateDealStatus(long dealPid, DirectDeal.DealStatus status) {
    DirectDeal deal =
        directDealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));
    deal.setStatus(status);
    directDealRepository.save(deal);
  }

  @Override
  public RTBProfileDTO getSupplier(long pid) {
    DealRtbProfileViewUsingFormulas rtbProfile =
        dealRtbProfileViewRepository
            .findById(pid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND));
    return convertViewToRTBProfileDTO(rtbProfile);
  }

  @Override
  public List<RTBProfileDTO> getAllNonarchivedSuppliers() {
    List<DealRtbProfileViewUsingFormulas> profiles =
        dealRtbProfileViewRepository.findAll(
            isNotDeleted().and(hasNullDefaultRtbProfileOwnerCompanyPid()));
    List<RTBProfileDTO> profilesDTOs = new ArrayList<>();
    for (DealRtbProfileViewUsingFormulas profile : profiles) {
      var dto = convertViewToRTBProfileDTO(profile);
      profilesDTOs.add(dto);
    }
    return profilesDTOs;
  }

  @Override
  public List<DealBuyerDTO> getAllBuyers() {
    return dealService.getAllBuyers();
  }

  @Override
  public List<SellerRuleDTO> findRulesByDealPid(Long pid) {
    List<CompanyRule> rules = ruleRepository.findAllActiveDealRulesAssosiatedWithDeal(pid);
    List<SellerRuleDTO> dtos = new ArrayList<>();
    if (rules != null && !rules.isEmpty()) {
      dtos =
          rules.stream()
              .map(e -> ruleAssembler.make(e, RuleAssembler.DEFAULT_FIELDS))
              .collect(toList());
    }
    return dtos;
  }

  /**
   * Given a company and a list of positions, converts the data into PublisherSitePositionDTO
   * structure.
   *
   * @param company company object that owns the positions.
   * @param positions list of positions owned by the company passed as parameters.
   * @return PublisherSitePositionDTO
   */
  private PublisherSitePositionDTO convertPositionsToDTO(
      Company company, List<PositionView> positions) {
    PublisherSitePositionDTO.Builder pspBuilder =
        new PublisherSitePositionDTO.Builder()
            .setPublisherId(company.getPid())
            .setPublisherName(company.getName());

    // group the positions by site
    Map<SiteView, List<PositionView>> positionsPerSite =
        positions.stream()
            .collect(
                Collectors.groupingBy(
                    PositionView::getSiteView, LinkedHashMap::new, Collectors.toList()));

    pspBuilder.setSites(
        positionsPerSite.entrySet().stream()
            .map(entry -> this.convertPositionsToDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList()));

    return pspBuilder.build();
  }

  private PublisherSitePositionDTO convertSitesToDTO(Company company, List<SiteView> sites) {
    PublisherSitePositionDTO.Builder pspBuilder =
        new PublisherSitePositionDTO.Builder()
            .setPublisherId(company.getPid())
            .setPublisherName(company.getName());

    pspBuilder.setSites(sites.stream().map(this::convertSiteToDTO).collect(Collectors.toList()));

    return pspBuilder.build();
  }

  private SitePostionDTO convertSiteToDTO(SiteView siteDto) {
    return new SitePostionDTO.Builder()
        .setSiteId(siteDto.getPid())
        .setSiteName(siteDto.getName())
        .build();
  }

  /**
   * Given a site a list of positions, converts the data into SitePostionDTO structure.
   *
   * @param site site object that owns the positions.
   * @param positions list of positions that belongs to the given site
   * @return site positions DTO
   */
  private SitePostionDTO convertPositionsToDTO(SiteView site, List<PositionView> positions) {
    // converts from Position to PositionNameDTO
    List<PositionNameDTO> positionDtos =
        positions.stream().map(this::convertPositionToDTO).collect(Collectors.toList());

    // creates the site dto and attache the newly created positionDtos
    return new SitePostionDTO.Builder()
        .setSiteId(site.getPid())
        .setSiteName(site.getName())
        .setPostitionNames(positionDtos)
        .build();
  }

  private PositionNameDTO convertPositionToDTO(PositionView position) {
    return new PositionNameDTO.Builder()
        .setPositionId(position.getPid())
        .setPositionName(position.getName())
        .build();
  }

  private RTBProfileDTO convertViewToRTBProfileDTO(DealRtbProfileViewUsingFormulas rtbProfile) {
    Long pid = rtbProfile.getPid();
    RTBProfileDTO.RTBProfileDTOBuilder builder =
        RTBProfileDTO.builder()
            .pid(pid)
            .rtbProfilePid(rtbProfile.getPid())
            .description(rtbProfile.getDescription())
            .auctionType(rtbProfile.getAuctionType())
            .defaultReserve(rtbProfile.getDefaultReserve())
            .lowFloor(rtbProfile.getLowReserve())
            .pubAlias(rtbProfile.getPubAlias())
            .pubId(rtbProfile.getPubPid())
            .pubName(rtbProfile.getPubName())
            .pubNameAlias(rtbProfile.getPubNameAlias())
            .siteAlias(rtbProfile.getSiteAlias())
            .siteId(rtbProfile.getSitePid())
            .siteNameAlias(rtbProfile.getSiteNameAlias())
            .siteName(rtbProfile.getSiteName())
            .isRealName(rtbProfile.getIsRealName())
            .tagPid(rtbProfile.getTagPid())
            .tagName(rtbProfile.getTagName())
            .siteType(rtbProfile.getSiteType())
            .categories(rtbProfile.getCategories())
            .countries(rtbProfile.getCountries())
            .platform(rtbProfile.getPlatform())
            .videoSupport(rtbProfile.getVideoSupport())
            .height(rtbProfile.getHeight())
            .width(rtbProfile.getWidth())
            .placementType(rtbProfile.getPlacementType())
            .placementName(rtbProfile.getPlacementName())
            .placementPid(rtbProfile.getPlacementPid());

    return builder.build();
  }
}
