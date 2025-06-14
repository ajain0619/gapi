package com.nexage.app.services.impl;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DealTarget;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DealBidderRepository;
import com.nexage.admin.core.repository.DealRtbProfileViewRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.DealBidder;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealProfile;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.admin.core.specification.CompanySpecification;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.DirectDealDTO.AuctionType;
import com.nexage.app.dto.deal.DealBidderDTO;
import com.nexage.app.dto.deal.DealBuyerDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DirectDealDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.DealService;
import com.nexage.app.services.deal.DealCacheService;
import com.nexage.app.services.support.DealServiceSupport;
import com.nexage.app.services.validation.DealValidator;
import com.nexage.app.util.Utils;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DealServiceImpl implements DealService {

  private final DirectDealRepository directDealRepository;
  private final SiteRepository siteRepository;
  private final UserContext userContext;
  private final BidderConfigRepository bidderConfigRepository;
  private final PositionViewRepository positionViewRepository;
  private final PlacementFormulaAssembler placementFormulaAssembler;
  private final DealBidderRepository dealBidderRepository;
  private final DealCacheService dealCacheService;
  private final CompanyRepository companyRepository;
  private final DealServiceSupport dealServiceSupport;
  private final DealValidator dealValidator;
  private final DealRtbProfileViewRepository dealRtbProfileViewRepository;

  public DealServiceImpl(
      DirectDealRepository directDealRepository,
      SiteRepository siteRepository,
      UserContext userContext,
      BidderConfigRepository bidderConfigRepository,
      PositionViewRepository positionViewRepository,
      PlacementFormulaAssembler placementFormulaAssembler,
      DealBidderRepository dealBidderRepository,
      DealCacheService dealCacheService,
      CompanyRepository companyRepository,
      DealServiceSupport dealServiceSupport,
      DealValidator dealValidator,
      DealRtbProfileViewRepository dealRtbProfileViewRepository) {
    this.directDealRepository = directDealRepository;
    this.siteRepository = siteRepository;
    this.userContext = userContext;
    this.bidderConfigRepository = bidderConfigRepository;
    this.positionViewRepository = positionViewRepository;
    this.placementFormulaAssembler = placementFormulaAssembler;
    this.dealBidderRepository = dealBidderRepository;
    this.dealCacheService = dealCacheService;
    this.companyRepository = companyRepository;
    this.dealServiceSupport = dealServiceSupport;
    this.dealValidator = dealValidator;
    this.dealRtbProfileViewRepository = dealRtbProfileViewRepository;
  }

  @Override
  public DirectDealDTO createDeal(DirectDealDTO deal) {
    dealValidator.validateAndFixDeal(deal);

    if (directDealRepository.findByDealId(deal.getDealId()).isPresent()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_DEAL_ID);
    }

    DirectDeal newDeal = new DirectDeal();
    newDeal.setDealId(deal.getDealId());
    newDeal.setCurrency(deal.getCurrency());
    newDeal.setPriorityType(deal.getPriorityType());
    newDeal.setDescription(deal.getDescription());
    newDeal.setStart(deal.getStart());
    newDeal.setStop(deal.getStop());

    if (deal.getAuctionType() != null && isFixedNonOpen(deal) && deal.getFloor() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID);
    }

    newDeal.setFloor(deal.getFloor());
    newDeal.setStatus(deal.getStatus());
    newDeal.setAuctionType(convertAuctionTypeEnumToInteger(deal.getAuctionType()));
    newDeal.setVisibility(deal.isVisibility());
    newDeal.setDealCategory(
        deal.getDealCategory() == null ? Integer.valueOf(1) : deal.getDealCategory());

    newDeal.setViewability(deal.getViewability());

    List<DealBidder> newBidders = new ArrayList<>();
    String seatList;
    String adomainList;
    if (deal.isAllBidders()) {
      newBidders.addAll(getAllBidders(newDeal));
    } else {
      for (DealBidderDTO bidder : deal.getBidders()) {
        DealBidder newBidder = new DealBidder();

        seatList = convertListToString(bidder.getWseat());
        newBidder.setFilterSeats(StringUtils.isNotEmpty(seatList) ? seatList : null);

        adomainList = convertListToString(bidder.getAdomains());
        newBidder.setFilterAdomains(StringUtils.isNotEmpty(adomainList) ? adomainList : null);

        BidderConfig bidderConfig =
            bidderConfigRepository
                .findById(bidder.getBidderPid())
                .orElseThrow(
                    () ->
                        new GenevaValidationException(
                            ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND));
        newBidder.setBidderConfig(bidderConfig);
        newBidder.setDeal(newDeal);
        newBidders.add(newBidder);
      }
    }
    newDeal.setAllBidders(deal.isAllBidders());
    newDeal.setBidders(newBidders);

    List<DealProfile> newProfiles = new ArrayList<>();
    for (RTBProfileDTO profile : deal.getProfiles()) {
      DealProfile newProfile = new DealProfile();
      DealRtbProfileViewUsingFormulas rtbProfile =
          dealRtbProfileViewRepository
              .findById(profile.getRtbProfilePid())
              .orElseThrow(
                  () ->
                      new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND));
      newProfile.setRtbProfile(rtbProfile);
      newProfile.setDeal(newDeal);
      newProfiles.add(newProfile);
    }
    newDeal.setProfiles(newProfiles);

    List<DealPublisher> newPublishers = new ArrayList<>();
    if (deal.isAllSellers()) {
      List<DealPublisher> sellers = getAllRtbEnabledPublishers(newDeal);
      newPublishers.addAll(sellers);
    } else if (deal.getSellers() != null) {
      for (DealPublisherDTO publisher : deal.getSellers()) {
        var newPublisher = new DealPublisher();
        newPublisher.setPubPid(publisher.getPublisherPid());
        newPublisher.setDeal(newDeal);
        newPublishers.add(newPublisher);
      }
    }
    newDeal.setAllSellers(deal.isAllSellers());
    newDeal.setPublishers(newPublishers);
    newDeal.setExternal(deal.isExternal());

    if (deal.getRules() != null) {
      List<DealRule> newRules = new ArrayList<>(deal.getRules().size());
      for (DealRuleDTO rule : deal.getRules()) {
        DealRule newRule = new DealRule();
        newRule.setDeal(newDeal);
        newRule.setRulePid(rule.getRulePid());
        newRules.add(newRule);
      }
      newDeal.setRules(newRules);
    }

    List<DealSite> newSites = new ArrayList<>();
    if (!deal.getSites().isEmpty()) {
      List<Long> pids = getSitePids(deal.getSites());
      newSites =
          siteRepository.findBySitePidIn(pids).stream()
              .map(siteView -> new DealSite(siteView.getPid(), newDeal))
              .collect(toList());
    }
    newDeal.setSites(newSites);

    dealValidator.validateForFormula(deal);
    if (deal.getPlacementFormula() != null) {
      newDeal.setPlacementFormula(
          placementFormulaAssembler.applyToString(deal.getPlacementFormula()));
      newDeal.setPlacementFormulaStatus(PlacementFormulaStatus.NEW);
      newDeal.setAutoUpdate(deal.getAutoUpdate());
    } else {
      List<DealPosition> newPositions =
          (deal.getPositions() != null)
              ? new ArrayList<>(deal.getPositions().size())
              : new ArrayList<>();
      if (deal.getPositions() != null) {
        addNewPositions(deal.getPositions(), newDeal, newPositions);
      }
      newDeal.setPositions(newPositions);
      newDeal.setPlacementFormulaStatus(PlacementFormulaStatus.DONE);
    }

    if (deal.getTargets() != null) {
      populateTargets(deal, newDeal);
    }

    if (deal.isExternal()) {
      dealValidator.validateTargetsAllowedForExternalDeal(newDeal);
    }

    newDeal.setPacingEnabled(deal.getPacingEnabled());
    newDeal.setPacingStrategy(1);

    SpringUserDetails user = userContext.getCurrentUser();
    newDeal.setCreatedBy(user.getPid());

    var createdDeal = directDealRepository.save(newDeal);
    return DirectDealDTOMapper.MAPPER.map(createdDeal);
  }

  @Override
  public DirectDealDTO updateDeal(long dealPid, DirectDealDTO dealDto) {
    if (dealPid != dealDto.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    dealValidator.validateAndFixDeal(dealDto);

    DirectDeal original =
        directDealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));

    String newDealId = dealDto.getDealId();

    if (StringUtils.isBlank(newDealId) || newDealId.length() > DirectDeal.DEAL_ID_MAX_LENGTH) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DEAL_ID);
    }

    // it's ok to update a deal id, but it has to be unique
    if (!original.getDealId().equals(newDealId)
        && directDealRepository.countByDealId(newDealId) > 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_DEAL_ID);
    }

    original.setDescription(dealDto.getDescription());
    original.setStart(dealDto.getStart());
    original.setStop(dealDto.getStop());
    original.setPriorityType(dealDto.getPriorityType());
    original.setExternal(dealDto.isExternal());

    if (dealDto.getAuctionType() != null && isFixedNonOpen(dealDto) && dealDto.getFloor() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID);
    }
    original.setDealCategory(dealDto.getDealCategory());
    original.setFloor(dealDto.getFloor());
    original.setStatus(dealDto.getStatus());
    original.setDealId(dealDto.getDealId());
    original.setCurrency(dealDto.getCurrency());
    if (!original.isVisibility() == dealDto.isVisibility()) {
      dealCacheService.removeDeal(dealDto.getDealId());
    }
    original.setVisibility(dealDto.isVisibility());
    original.setAuctionType(convertAuctionTypeEnumToInteger(dealDto.getAuctionType()));
    if (dealDto.isAllBidders()) {
      dealDto.setBidders(convertBidders(getAllBidders(original)));
    }
    original.setAllBidders(dealDto.isAllBidders());
    convertToCoreBidders(original, original.getBidders(), dealDto.getBidders());
    convertToCoreProfiles(original, original.getProfiles(), dealDto.getProfiles());

    if (dealDto.getRules() != null) {
      original.setRules(convertToCoreRules(original, original.getRules(), dealDto.getRules()));
    }

    dealValidator.validateForFormula(dealDto);
    if (dealDto.getPlacementFormula() != null) {
      original.setPlacementFormulaStatus(
          dealServiceSupport.updateDealPlacementFormulaStatus(
              dealPid, original.getPlacementFormula(), dealDto.getPlacementFormula()));
      original.setPlacementFormula(
          placementFormulaAssembler.applyToString(dealDto.getPlacementFormula()));
      original.setAutoUpdate(dealDto.getAutoUpdate());
    } else {
      if (dealDto.getPositions() != null) {
        original.setPositions(
            convertToCorePositions(original, original.getPositions(), dealDto.getPositions()));
      }
      if (dealDto.getSites() != null) {
        original.setSites(
            convertDTOsToCoreSites(original, original.getSites(), dealDto.getSites()));
      }
      if (dealDto.isAllSellers()) {
        // if all publishers is set for new deal, fetch all from DB
        dealDto.setSellers(convertPublishers(getAllRtbEnabledPublishers(original)));
      }
      original.setAllSellers(dealDto.isAllSellers());
      convertToCorePublishers(original, original.getPublishers(), dealDto.getSellers());

      original.setPlacementFormula(null);
      original.setPlacementFormulaStatus(PlacementFormulaStatus.DONE);
      original.setAutoUpdate(null);
    }

    original.setGuaranteedImpressionGoal(null);
    original.setDailyImpressionCap(null);

    if (dealDto.isExternal()) {
      dealValidator.validateTargetsAllowedForExternalDeal(original);
    }

    original.setPacingEnabled(dealDto.getPacingEnabled());
    original.setPacingStrategy(1);

    original.setViewability(dealDto.getViewability());

    convertToCoreDealTarget(original, original.getDealTargets(), dealDto.getTargets());

    var updated = directDealRepository.save(original);
    return DirectDealDTOMapper.MAPPER.map(updated);
  }

  @Override
  public List<DealBuyerDTO> getAllBuyers() {
    List<DealBidderConfigView> views = dealBidderRepository.findAll();
    return views.stream()
        .map(view -> new DealBuyerDTO(view.getCompanyPid(), view.getPid(), view.getName()))
        .collect(toList());
  }

  private DealPublisher mapToPublisher(Company company, DirectDeal deal) {
    var publisher = new DealPublisher();
    publisher.setPubPid(company.getPid());
    publisher.setDeal(deal);
    return publisher;
  }

  private DealBidder mapToBidder(DealBuyerDTO buyer, DirectDeal deal) {
    DealBidder bidder = new DealBidder();
    BidderConfig config =
        bidderConfigRepository
            .findById(buyer.getBidderPid())
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND));
    bidder.setBidderConfig(config);
    bidder.setDeal(deal);
    return bidder;
  }

  private List<DealBidder> getAllBidders(DirectDeal deal) {
    List<DealBuyerDTO> allBidders = getAllBuyers();
    return allBidders.stream().map(dto -> mapToBidder(dto, deal)).collect(Collectors.toList());
  }

  private List<DealPublisher> getAllRtbEnabledPublishers(DirectDeal newDeal) {
    List<Company> allSelers = companyRepository.findAll(CompanySpecification.ofRtbEnabledSellers());
    return allSelers.stream()
        .map(seller -> mapToPublisher(seller, newDeal))
        .collect(Collectors.toList());
  }

  private boolean isFixedNonOpen(DirectDealDTO deal) {
    return AuctionType.FIXED.equals(deal.getAuctionType())
        || !DealPriorityType.OPEN.equals(deal.getPriorityType());
  }

  private List<DealBidderDTO> convertBidders(List<DealBidder> bidders) {
    List<DealBidderDTO> dtos = new ArrayList<>();
    for (DealBidder bidder : bidders) {
      DealBidderDTO.Builder builder = new DealBidderDTO.Builder();
      builder
          .setPid(bidder.getPid())
          .setBidderPid(bidder.getBidderConfig().getPid())
          .setFilterSeats(Utils.getListFromCommaSeparatedString(bidder.getFilterSeats()))
          .setFilterAdomains(Utils.getListFromCommaSeparatedString(bidder.getFilterAdomains()));
      dtos.add(builder.build());
    }
    return dtos;
  }

  private List<DealPublisherDTO> convertPublishers(List<DealPublisher> publishers) {
    int publisherSize = (publishers != null) ? publishers.size() : 0;
    List<DealPublisherDTO> dtos = new ArrayList<>(publisherSize);

    if (publishers != null) {
      for (DealPublisher publisher : publishers) {
        DealPublisherDTO.Builder builder = new DealPublisherDTO.Builder();
        builder.setPid(publisher.getPid()).setPublisherPid(publisher.getPubPid());
        dtos.add(builder.build());
      }
    }
    return dtos;
  }

  private List<Long> getPositionPids(List<DealPositionDTO> positions) {
    return positions.stream().map(DealPositionDTO::getPositionPid).collect(Collectors.toList());
  }

  private void applyBidderChanges(DealBidder bidder, DealBidderDTO dto) {
    String seatList = convertListToString(dto.getWseat());
    if (!seatList.equals(bidder.getFilterSeats())) {
      bidder.setFilterSeats(StringUtils.isNotEmpty(seatList) ? seatList : null);
    }
    String adomainList = convertListToString(dto.getAdomains());
    if (!adomainList.equals(bidder.getFilterAdomains())) {
      bidder.setFilterAdomains(StringUtils.isNotEmpty(adomainList) ? adomainList : null);
    }
  }

  private void populateTargets(DirectDealDTO deal, DirectDeal newDeal) {
    Set<DealTarget> newDealTargets = new HashSet<>();
    deal.getTargets()
        .forEach(
            dealTargetDTO -> newDealTargets.add(createDealTargetFromDto(dealTargetDTO, newDeal)));
    newDeal.setDealTargets(newDealTargets);
  }

  private String getFormattedTargetData(String data) {
    return StringUtils.deleteWhitespace(data).toLowerCase();
  }

  private boolean applyTargetChanges(DealTarget target, DealTargetDTO dto) {
    boolean isDirty = false;
    if (dto.getParamName() != null && !dto.getParamName().equals(target.getParamName())) {
      target.setParamName(dto.getParamName());
      isDirty = true;
    }
    if (!dto.getTargetType().equals(target.getTargetType())) {
      target.setTargetType(dto.getTargetType());
      isDirty = true;
    }
    if (!dto.getRuleType().equals(target.getRuleType())) {
      target.setRuleType(dto.getRuleType());
      isDirty = true;
    }
    if (!dto.getData().equals(target.getData())) {
      target.setData(getFormattedTargetData(dto.getData()));
      isDirty = true;
    }
    return isDirty;
  }

  private void convertToCoreDealTarget(
      DirectDeal deal, Set<DealTarget> coreDealTargets, Set<DealTargetDTO> dtos) {

    Map<Long, DealTargetDTO> dtoMap = new HashMap<>();
    if (dtos == null || dtos.isEmpty()) {
      coreDealTargets.clear();
      return;
    }
    for (DealTargetDTO dto : dtos) {
      dealValidator.validateTarget(dto);
      dtoMap.put(dto.getPid(), dto);
    }
    checkDubiousInputPids(dtoMap, coreDealTargets);

    Iterator<DealTarget> coreTargetsIt = coreDealTargets.iterator();
    DealTarget target;
    boolean whereTargetsUpdated = false;
    while (coreTargetsIt.hasNext()) {
      target = coreTargetsIt.next();
      if (dtoMap.containsKey(target.getPid())) {
        whereTargetsUpdated = applyTargetChanges(target, dtoMap.get(target.getPid()));
      } else {
        coreTargetsIt.remove();
        whereTargetsUpdated = true;
      }
    }

    for (DealTargetDTO dto : dtos) {
      // add any new targets
      if (null == dto.getPid()) {
        coreDealTargets.add(createDealTargetFromDto(dto, deal));
      }
    }

    // make the deal object dirty by updating "updated_on" field so that envers can increment the
    // REV
    // consistently across target->deal; ONLY when there is an actual change in deal target data.
    if (coreDealTargets.size() != dtos.size() || whereTargetsUpdated) {
      deal.setUpdatedOn(new Date());
    }
  }

  private DealTarget createDealTargetFromDto(DealTargetDTO dto, DirectDeal deal) {
    var newDealTarget = new DealTarget();
    dealValidator.validateTarget(dto);
    newDealTarget.setTargetType(dto.getTargetType());
    newDealTarget.setRuleType(dto.getRuleType());
    newDealTarget.setData(getFormattedTargetData(dto.getData()));
    newDealTarget.setParamName(dto.getParamName());
    newDealTarget.setDeal(deal);
    return newDealTarget;
  }

  private void checkDubiousInputPids(
      Map<Long, DealTargetDTO> dtoMap, Set<DealTarget> coreDealTargets) {
    Map<Long, DealTarget> coreMap = new HashMap<>();
    for (DealTarget coreDealTarget : coreDealTargets) {
      coreMap.put(coreDealTarget.getPid(), coreDealTarget);
    }
    Set<Long> dtoPids = dtoMap.keySet();
    Set<Long> corePids = coreMap.keySet();

    for (Long dtoPid : dtoPids) {
      if (dtoPid != null && !corePids.contains(dtoPid)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_NON_EXISTENT_TARGET_PID);
      }
    }
  }

  private void convertToCoreBidders(
      DirectDeal deal, List<DealBidder> coreBidders, List<DealBidderDTO> dtos) {
    // make the deal object dirty by updating "updated_on" field so that envers can increment the
    // REV
    // consistently across deal_bidder->deal
    deal.setUpdatedOn(new Date());

    Map<Long, DealBidderDTO> dtoMap = new HashMap<>();
    for (DealBidderDTO dto : dtos) {
      dtoMap.put(dto.getPid(), dto);
    }

    Iterator<DealBidder> coreBiddersIt = coreBidders.iterator();
    while (coreBiddersIt.hasNext()) {
      DealBidder bidder = coreBiddersIt.next();

      if (dtoMap.containsKey(bidder.getPid())) {
        // updates
        applyBidderChanges(bidder, dtoMap.get(bidder.getPid()));
      } else {
        // deletions
        coreBiddersIt.remove();
      }
    }

    for (DealBidderDTO dto : dtos) {
      // add any new bidders
      if (null == dto.getPid()) {
        DealBidder b = new DealBidder();
        b.setDeal(deal);
        Long bidderPid = dto.getBidderPid();
        BidderConfig bc =
            bidderConfigRepository
                .findById(bidderPid)
                .orElseThrow(
                    () ->
                        new GenevaValidationException(
                            ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND));
        b.setBidderConfig(bc);
        applyBidderChanges(b, dto);
        coreBidders.add(b);
      }
    }
  }

  private List<DealSite> convertDTOsToCoreSites(
      final DirectDeal deal, List<DealSite> coreSites, final List<DealSiteDTO> dtos) {
    // make the deal object dirty by updating "updated_on" field so that envers can increment the
    // REV consistently across deal_site->deal
    deal.setUpdatedOn(new Date());

    Map<Long, DealSiteDTO> dtoMap = new HashMap<>();
    for (DealSiteDTO dto : dtos) {
      dtoMap.put(dto.getPid(), dto);
    }

    coreSites.removeIf(site -> !dtoMap.containsKey(site.getPid()));

    for (DealSiteDTO dto : dtos) {
      // add any new sites
      if (null == dto.getPid()) {
        var ds = new DealSite();
        ds.setDeal(deal);
        ds.setSitePid(dto.getSitePid());
        coreSites.add(ds);
      }
    }

    return coreSites;
  }

  private List<Long> getSitePids(List<DealSiteDTO> sites) {
    return sites.stream().map(DealSiteDTO::getSitePid).collect(Collectors.toList());
  }

  private void convertToCoreProfiles(
      DirectDeal deal, List<DealProfile> coreProfiles, List<RTBProfileDTO> dtos) {
    // make the deal object dirty by updating "updated_on" field so that envers can increment the
    // REV consistently across deal_profile->deal
    deal.setUpdatedOn(new Date());

    Map<Long, RTBProfileDTO> dtoMap = new HashMap<>();
    for (RTBProfileDTO dto : dtos) {
      dtoMap.put(dto.getPid(), dto);
    }

    coreProfiles.removeIf(profile -> !dtoMap.containsKey(profile.getPid()));

    for (RTBProfileDTO dto : dtos) {
      // add any new bidders
      if (null == dto.getPid()) {
        DealProfile dp = new DealProfile();
        dp.setDeal(deal);
        DealRtbProfileViewUsingFormulas pr =
            dealRtbProfileViewRepository
                .findById(dto.getRtbProfilePid())
                .orElseThrow(
                    () ->
                        new GenevaValidationException(
                            ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND));
        dp.setRtbProfile(pr);
        coreProfiles.add(dp);
      }
    }
  }

  private List<DealRule> convertToCoreRules(
      final DirectDeal deal, List<DealRule> coreRules, final Set<DealRuleDTO> dtos) {
    // Date is updated so that the Deal is reloaded by the cache
    deal.setUpdatedOn(new Date());

    Map<Long, DealRuleDTO> dtoMap = new HashMap<>();
    for (DealRuleDTO dto : dtos) {
      dtoMap.put(dto.getPid(), dto);
    }

    coreRules.removeIf(rule -> !dtoMap.containsKey(rule.getPid()));

    Set<DealRuleDTO> newDTOs = getNewDealRuleDTOs(dtos);
    for (DealRuleDTO dto : newDTOs) {
      DealRule newRule = new DealRule();
      newRule.setDeal(deal);
      newRule.setRulePid(dto.getRulePid());
      coreRules.add(newRule);
    }

    return coreRules;
  }

  private void convertToCorePublishers(
      DirectDeal deal, List<DealPublisher> corePublishers, List<DealPublisherDTO> dtos) {
    if (dtos != null) {
      deal.setUpdatedOn(new Date());

      Map<Long, DealPublisherDTO> dtoMap = new HashMap<>();
      for (DealPublisherDTO dto : dtos) {
        dtoMap.put(dto.getPid(), dto);
      }

      corePublishers.removeIf(publisher -> !dtoMap.containsKey(publisher.getPid()));

      for (DealPublisherDTO dto : dtos) {
        // add any new publishers
        if (null == dto.getPid()) {
          var dp = new DealPublisher();
          dp.setDeal(deal);
          dp.setPubPid(dto.getPublisherPid());
          corePublishers.add(dp);
        }
      }
    }
  }

  private List<DealPosition> convertToCorePositions(
      final DirectDeal deal, List<DealPosition> corePositions, final List<DealPositionDTO> dtos) {
    // Date is updated so that the Deal is reloaded by the cache
    deal.setUpdatedOn(new Date());

    Map<Long, DealPositionDTO> dtoMap = new HashMap<>();
    for (DealPositionDTO dto : dtos) {
      dtoMap.put(dto.getPid(), dto);
    }

    corePositions.removeIf(position -> !dtoMap.containsKey(position.getPid()));

    List<DealPositionDTO> newDTOs = getNewDealPositionDTOs(dtos);
    addNewPositions(newDTOs, deal, corePositions);

    return corePositions;
  }

  private void addNewPositions(
      List<DealPositionDTO> positionDTOS, DirectDeal deal, List<DealPosition> positions) {
    List<Long> pids = getPositionPids(positionDTOS);
    if (!pids.isEmpty()) {
      List<PositionView> ps = positionViewRepository.findAllById(pids);
      for (PositionView p : ps) {
        var newPosition = new DealPosition();
        newPosition.setDeal(deal);
        newPosition.setPositionView(p);
        newPosition.setPositionPid(p.getPid());
        positions.add(newPosition);
      }
    }
  }

  private Set<DealRuleDTO> getNewDealRuleDTOs(Set<DealRuleDTO> dtos) {
    return dtos.stream().filter(dto -> dto.getPid() == null).collect(Collectors.toSet());
  }

  private List<DealPositionDTO> getNewDealPositionDTOs(List<DealPositionDTO> dtos) {
    return dtos.stream().filter(dto -> dto.getPid() == null).collect(Collectors.toList());
  }

  private String convertListToString(List<String> strings) {
    return Joiner.on(",").join(trimSpaces(strings));
  }

  private Integer convertAuctionTypeEnumToInteger(AuctionType at) {
    if (at == null || at.equals(AuctionType.NONE)) return null;
    return at.asInt();
  }

  private List<String> trimSpaces(List<String> strings) {
    List<String> trimmedArray = new ArrayList<>();
    for (String s : strings) {
      trimmedArray.add(s.trim());
    }
    return trimmedArray;
  }
}
