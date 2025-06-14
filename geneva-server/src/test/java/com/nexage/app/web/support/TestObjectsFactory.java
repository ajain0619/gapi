package com.nexage.app.web.support;

import static com.google.common.collect.Sets.newHashSet;
import static com.nexage.admin.core.enums.MatchType.INCLUDE_LIST;
import static com.nexage.admin.core.enums.RuleTargetType.REVGROUP;
import static com.nexage.admin.core.enums.RuleType.BRAND_PROTECTION;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.vavr.collection.List.range;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BDRTarget;
import com.nexage.admin.core.bidder.model.BDRTargetGroupCreative;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.type.BDRFreqCapMode;
import com.nexage.admin.core.bidder.type.BDRLineItemType;
import com.nexage.admin.core.bidder.type.BDRRule;
import com.nexage.admin.core.bidder.type.BDRTargetType;
import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.FeeType;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.MediaType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTagValues;
import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.ExternalDataProvider;
import com.nexage.admin.core.model.GeoSegment;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RevenueGroup;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.TagView;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentBuyer;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentSeller;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.admin.dw.util.ReportDefEnums.Interval;
import com.nexage.app.dto.BidderConfigDTOView;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.dto.deals.DealBidderDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.publisher.PublisherMetricsDTO;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.dto.seller.SellerDTO;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.dto.sellingrule.FilterType;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.IntendedActionDTO.IntendedActionDTOBuilder;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.RuleActionType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO.RuleTargetDTOBuilder;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO.SellerSeatRuleDTOBuilder;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import com.nexage.app.dto.tag.TagDTO;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.dto.user.UserDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class TestObjectsFactory {
  public static BdrInsertionOrder createBdrInsertionOrder(int lineItemCount, int targetGroupCount) {
    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    randomPid(insertionOrder);
    insertionOrder.setName(RandomStringUtils.randomAlphanumeric(32));
    insertionOrder.setAdvertiserPid(randomLong());
    insertionOrder.setLineItems(
        Sets.newHashSet(createBdrLineItems(insertionOrder, lineItemCount, targetGroupCount)));
    return insertionOrder;
  }

  public static Collection<BdrInsertionOrder> createBdrInsertionOrders(
      int count, int lineItemCount, int targetGroupCount) {
    List<BdrInsertionOrder> insertionOrders = Lists.newArrayList();
    for (int i = 0; i < count; i++) {
      insertionOrders.add(createBdrInsertionOrder(lineItemCount, targetGroupCount));
    }
    return insertionOrders;
  }

  public static BDRLineItem createBdrLineItem(
      BdrInsertionOrder insertionOrder, int targetGroupCount) {
    BDRLineItem lineItem = new BDRLineItem();
    randomPid(lineItem);
    lineItem.setName(RandomStringUtils.randomAlphanumeric(32));
    lineItem.setInsertionOrder(insertionOrder);
    lineItem.setTargetGroups(Sets.newHashSet(createBdrTargetGroups(lineItem, targetGroupCount)));
    lineItem.setType(BDRLineItemType.IMPRESSION);
    lineItem.setFrequencyCapMode(BDRFreqCapMode.ALWAYS);
    lineItem.setStartDate(DateUtils.addDays(new Date(), new Random().nextInt(5)));
    return lineItem;
  }

  public static Collection<BDRLineItem> createBdrLineItems(
      BdrInsertionOrder insertionOrder, int count, int targetGroupCount) {
    List<BDRLineItem> lineItems = Lists.newArrayList();
    for (int i = 0; i < count; i++) {
      lineItems.add(createBdrLineItem(insertionOrder, targetGroupCount));
    }
    return lineItems;
  }

  public static BdrTargetGroup createBdrTargetGroup(BDRLineItem lineItem) {
    BdrTargetGroup targetGroup = new BdrTargetGroup();
    randomPid(targetGroup);
    targetGroup.setName(RandomStringUtils.randomAlphanumeric(32));
    targetGroup.setMaxPrice(BigDecimal.valueOf(randomDouble()));
    targetGroup.setLineItem(lineItem);
    return targetGroup;
  }

  public static BdrTargetGroup createBdrTargetGroup() {
    BdrTargetGroup targetGroup = new BdrTargetGroup();
    randomPid(targetGroup);
    targetGroup.setName(RandomStringUtils.randomAlphanumeric(32));
    targetGroup.setMaxPrice(BigDecimal.valueOf(randomDouble()));
    targetGroup.setTargets(Sets.newHashSet(createCountyTarget(targetGroup)));
    return targetGroup;
  }

  public static BDRTarget createCountyTarget(BdrTargetGroup targetGroup) {
    BDRTarget bdrTarget = new BDRTarget();
    bdrTarget.setRule(BDRRule.ANYOF);
    bdrTarget.setTargetType(BDRTargetType.COUNTRY);
    bdrTarget.setData("AFG");
    bdrTarget.setTargetGroup(targetGroup);
    return bdrTarget;
  }

  public static Collection<BdrTargetGroup> createBdrTargetGroups(BDRLineItem lineItem, int count) {
    List<BdrTargetGroup> targetGroups = Lists.newArrayList();
    for (int i = 0; i < count; i++) {
      targetGroups.add(createBdrTargetGroup(lineItem));
    }
    return targetGroups;
  }

  public static BdrCreative createBdrCreative() {
    BdrCreative creative = new BdrCreative();
    randomPid(creative);
    creative.setName(RandomStringUtils.randomAlphanumeric(32));
    return creative;
  }

  public static Collection<BdrCreative> createBdrCreatives(int count) {
    List<BdrCreative> creatives = Lists.newArrayList();
    for (int i = 0; i < count; i++) {
      creatives.add(createBdrCreative());
    }
    return creatives;
  }

  public static BDRAdvertiser createBdrAdvertiser(CompanyType companyType) {
    BDRAdvertiser advertiser = new BDRAdvertiser();
    randomPid(advertiser);
    advertiser.setName(RandomStringUtils.randomAlphanumeric(32));
    advertiser.setCompany(createCompany(companyType));
    return advertiser;
  }

  public static BidderConfig createBidderConfig() {
    BidderConfig bidderConfig = new BidderConfig();
    randomPid(bidderConfig);
    return bidderConfig;
  }

  public static String randomEmail() {
    String email = RandomStringUtils.randomAlphanumeric(10);
    email += "@";
    email += RandomStringUtils.randomAlphanumeric(8);
    email += RandomStringUtils.randomAlphanumeric(3);
    return email;
  }

  public static ExternalDataProvider createExternalDataProvider() {
    ExternalDataProvider externalDataProvider = new ExternalDataProvider(randomLong());
    externalDataProvider.setName(RandomStringUtils.randomAlphanumeric(32));
    externalDataProvider.setBidderAliasRequired(randomBoolean());
    externalDataProvider.setBaseUrl(RandomStringUtils.randomAlphanumeric(32));
    externalDataProvider.setFilterRequestRate(randomInt());
    return externalDataProvider;
  }

  public static Collection<ExternalDataProvider> createExternalDataProviders(int count) {
    List<ExternalDataProvider> list = Lists.newArrayList();
    for (int i = 0; i < count; i++) {
      list.add(createExternalDataProvider());
    }
    return list;
  }

  public static void randomPid(Object obj) {
    setFieldReflectively(obj, "pid", randomLong());
  }

  public static void setFieldReflectively(Object obj, String fieldName, Object value) {
    try {
      Field pidField = obj.getClass().getDeclaredField(fieldName);
      pidField.setAccessible(true);
      pidField.set(obj, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException();
    }
  }

  public static Company createCompany(CompanyType type) {
    Company company = new Company(RandomStringUtils.randomAlphanumeric(32), type);
    company.setPid(randomLong());
    company.setStatus(Status.ACTIVE);
    company.setWebsite(RandomStringUtils.randomAlphanumeric(10));
    return company;
  }

  public static User createUser(Role role, Company... companies) {
    Preconditions.checkNotNull(companies);
    if (companies.length > 1) {
      Preconditions.checkArgument(
          Arrays.stream(companies).map(Company::getType).allMatch(CompanyType.SELLER::equals));
    }
    User user = new User();
    user.setPid(randomLong());
    user.setEmail(randomEmail());
    user.setUserName(RandomStringUtils.randomAlphanumeric(12));
    if (companies.length > 1) {
      final SellerSeat sellerSeat = createSellerSeat(companies);
      user.setSellerSeat(sellerSeat);
    }
    Arrays.stream(companies).forEach(user::addCompany);
    user.setRole(role);
    user.setEnabled(true);
    return user;
  }

  public static UserDTO getUserDtoFromUser(User user) {
    UserDTO userDTO = new UserDTO();
    fillCommonUserFields(userDTO, user);

    Set<Company> userCompanies = user.getCompanies();

    Set<CompanyViewDTO> companyViewDTOS = new HashSet<>();
    for (Company userCompany : userCompanies) {
      companyViewDTOS.add(createCompanyViewDto(userCompany));
    }

    userDTO.setCompanies(companyViewDTOS);

    SellerSeat sellerSeat = user.getSellerSeat();
    if (sellerSeat != null) {
      SellerSeatDTO sellerSeatDTO = new SellerSeatDTO();
      sellerSeatDTO.setPid(sellerSeat.getPid());
      sellerSeatDTO.setName(sellerSeat.getName());
      userDTO.setSellerSeat(sellerSeatDTO);
    }

    return userDTO;
  }

  private static void fillCommonUserFields(UserDTO userDTO, User user) {
    userDTO.setPid(user.getPid());
    userDTO.setUserName(user.getUserName());
    userDTO.setEmail(user.getEmail());
    userDTO.setRole(user.getRole());
    userDTO.setEnabled(user.isEnabled());
    userDTO.setFirstName(user.getFirstName());
    userDTO.setLastName(user.getLastName());
    userDTO.setContactName(user.getContactName());
    userDTO.setContactEmail(user.getContactEmail());
    userDTO.setVersion(user.getVersion());
  }

  public static CompanyViewDTO createCompanyViewDto(Company company) {
    return new CompanyViewDTO(company.getPid(), company.getName(), company.getType(), false);
  }

  public static SellerSeat createSellerSeat(Company... companies) {
    Preconditions.checkNotNull(companies);
    SellerSeat sellerSeat = new SellerSeat();
    Arrays.stream(companies).forEach(sellerSeat::addSeller);
    sellerSeat.setPid(randomLong());
    sellerSeat.setStatus(true);
    sellerSeat.setName(RandomStringUtils.randomAlphanumeric(12));
    sellerSeat.setDescription(RandomStringUtils.randomAlphanumeric(40));
    return sellerSeat;
  }

  public static User createSellerSeatUser() {
    User user = createUser();
    SellerSeat sellerSeat = createSellerSeat(createCompany(CompanyType.SELLER));
    user.setSellerSeat(sellerSeat);
    return user;
  }

  public static User createUser() {
    return createUser(Role.ROLE_ADMIN, createCompany(CompanyType.NEXAGE));
  }

  public static boolean randomBoolean() {
    return new Random().nextBoolean();
  }

  public static double randomDouble() {
    return new Random().nextDouble();
  }

  public static int randomInt() {
    return new Random().nextInt();
  }

  public static int randomInt(int min, int max) {
    return new Random().nextInt((max - min) + 1) + min;
  }

  public static long randomLong() {
    return Math.abs(new Random().nextLong());
  }

  public static long randomPid() {
    return (long) new Random().nextInt(1000000);
  }

  public static BigDecimal randomBigDecimal() {
    return new BigDecimal(randomDouble());
  }

  public static Set<BDRTargetGroupCreative> createTargetGroupCreatives(
      BdrTargetGroup tg, BdrCreative creative, int count) {
    Set<BDRTargetGroupCreative> targetGroupCreatives = Sets.newHashSet();
    for (int i = 0; i < count; i++) {
      targetGroupCreatives.add(createTargetGroupCreative(tg, creative));
    }
    return targetGroupCreatives;
  }

  public static BDRTargetGroupCreative createTargetGroupCreative(
      BdrTargetGroup tg, BdrCreative creative) {
    BDRTargetGroupCreative targetGroupCreative = new BDRTargetGroupCreative();
    targetGroupCreative.setTargetGroup(tg);
    targetGroupCreative.setCreative(creative);
    targetGroupCreative.setWeight(RandomUtils.nextDouble());
    return targetGroupCreative;
  }

  public static AdSource createAdSource() {
    AdSource adSource = new AdSource();
    adSource.setPid(randomPid());
    adSource.setStatus(Status.ACTIVE);
    return adSource;
  }

  public static AdSourceSummaryDTO createAdSourceSummary() {
    AdSourceSummaryDTO adSourceSummary = new AdSourceSummaryDTO();
    adSourceSummary.setPid(randomPid());
    adSourceSummary.setStatus(Status.ACTIVE);
    adSourceSummary.setBidEnabled(AdSource.BidEnabled.NO);
    adSourceSummary.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.NO);
    adSourceSummary.setSelfServeEnablement(AdSource.SelfServeEnablement.PUBLISHER);
    adSourceSummary.setReportAuthType(AdSource.AuthenticationType.NONE);
    return adSourceSummary;
  }

  public static BuyerGroup createBuyerGroup(
      Company company, String currency, String billingCountry) {
    String name = RandomStringUtils.randomAlphanumeric(32);
    String sfdcLineId = RandomStringUtils.randomAlphanumeric(12);
    String sfdcIoId = RandomStringUtils.randomAlphanumeric(12);
    boolean billable = true;
    BuyerGroup buyerGroup =
        new BuyerGroup(name, sfdcLineId, sfdcIoId, currency, billingCountry, billable);
    randomPid(buyerGroup);
    setFieldReflectively(buyerGroup, "version", 0);
    buyerGroup.setCompany(company);
    return buyerGroup;
  }

  public static BuyerSeat createBuyerSeat(
      String seat,
      String name,
      boolean enabled,
      BuyerGroup buyerGroup,
      Company company,
      Boolean buyerTransparencyFeedEnabled,
      Long buyerTransparencyDataFeedPid) {
    BuyerSeat buyerSeat = new BuyerSeat(seat, name, enabled);
    randomPid(buyerSeat);
    setFieldReflectively(buyerSeat, "version", 0);
    buyerSeat.setBuyerGroup(buyerGroup);
    buyerSeat.setCompany(company);
    buyerSeat.setBuyerTransparencyFeedEnabled(buyerTransparencyFeedEnabled);
    buyerSeat.setBuyerTransparencyDataFeedPid(buyerTransparencyDataFeedPid);
    return buyerSeat;
  }

  public static BidderConfigDTOView createBidderConfigDTOView(Long companyPid, String name) {
    BidderConfigDTOView bidderConfigDTOView = new BidderConfigDTOView();
    bidderConfigDTOView.setId(RandomStringUtils.randomAlphanumeric(12));
    bidderConfigDTOView.setPid(randomLong());
    bidderConfigDTOView.setCompanyPid(companyPid);
    bidderConfigDTOView.setName(name);
    return bidderConfigDTOView;
  }

  public static List<GeoSegment> createGeoSegments(int count) {
    List<GeoSegment> geoSegmentList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      GeoSegment geoSegment = new GeoSegment();
      geoSegment.setPid(randomLong());
      geoSegment.setName(RandomStringUtils.randomAlphanumeric(32));
      geoSegment.setSegmentId(randomLong());
      geoSegment.setIso3Code("USA");
      geoSegment.setType(randomLong());
      geoSegment.setLastUpdateOn(DateUtils.addDays(new Date(), new Random().nextInt(5)));
      geoSegmentList.add(geoSegment);
    }
    return geoSegmentList;
  }

  public static <T> List<T> gimme(int n, Class<T> clazz, String... excludeFields) {
    return range(0, n).map(__ -> random(clazz, excludeFields)).toJavaList();
  }

  public static DirectDealDTO createBasicDirectDealDTO(Long pid) {
    return DirectDealDTO.builder()
        .pid(pid)
        .dealId("123")
        .description("This is a test description")
        .build();
  }

  public static DirectDealDTO createDealWithDealRule(Long dealPid, Long rulePid) {
    DealRuleDTO dealRule = new DealRuleDTO.Builder().setPid(dealPid).setRulePid(rulePid).build();
    return DirectDealDTO.builder()
        .pid(dealPid)
        .dealId("123")
        .description("This is a test description")
        .rules(Collections.singleton(dealRule))
        .build();
  }

  public static DirectDeal createBasicDirectDeal(Long pid) {
    DirectDeal deal = new DirectDeal();
    deal.setPid(pid);
    deal.setDealId("12345");
    deal.setAllBidders(true);
    deal.setAuctionType(1);
    deal.setCreatedBy(67L);
    return deal;
  }

  public static List<Site> createSiteDTO(int count) {
    List<Site> siteDTODTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Site siteDTO = new Site();
      siteDTO.setPid(randomLong());
      siteDTO.setStatus(Status.ACTIVE);
      siteDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      siteDTODTOS.add(siteDTO);
    }
    return siteDTODTOS;
  }

  public static SiteView createSiteView() {
    Company company = createCompany(CompanyType.NEXAGE);
    SiteView siteView = new SiteView();
    siteView.setPid(randomLong());
    siteView.setStatus(Status.ACTIVE);
    siteView.setName(RandomStringUtils.randomAlphanumeric(32));
    siteView.setCompany(company);
    return siteView;
  }

  public static List<SiteDTO> createSite(int count) {
    List<SiteDTO> siteDTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      SiteDTO siteDTO = new SiteDTO();
      siteDTO.setPid(randomLong());
      siteDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      siteDTOS.add(siteDTO);
    }
    return siteDTOS;
  }

  public static List<PlacementDTO> createPlacements(int count) {
    List<PlacementDTO> placementDTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      PlacementDTO placementDTO = new PlacementDTO();
      placementDTO.setPid(randomLong());
      placementDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      placementDTOS.add(placementDTO);
    }
    return placementDTOS;
  }

  public static List<SellerDTO> createSellers(int count) {
    List<SellerDTO> sellerDTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      SellerDTO sellerDTO = new SellerDTO();
      sellerDTO.setPid(randomLong());
      sellerDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      sellerDTOS.add(sellerDTO);
    }
    return sellerDTOS;
  }

  public static List<SellerSummaryDTO> createSellerSummaries(int count) {
    List<SellerSummaryDTO> sellerSummaryDTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      SellerSummaryDTO sellerSummaryDTO = new SellerSummaryDTO();
      sellerSummaryDTO.setPid(randomLong());
      sellerSummaryDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      sellerSummaryDTO.setAdRequested(randomLong());
      sellerSummaryDTOS.add(sellerSummaryDTO);
    }
    return sellerSummaryDTOS;
  }

  public static List<TagDTO> createTagDTOs(int count) {
    List<TagDTO> tagDTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      TagDTO tagDTO = new TagDTO();
      tagDTO.setPid(randomLong());
      tagDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      tagDTOS.add(tagDTO);
    }
    return tagDTOS;
  }

  public static List<DealBidderDTO> createBidders(int count) {
    List<DealBidderDTO> dealBidderDTOS = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      DealBidderDTO dealBidderDTO = new DealBidderDTO();
      dealBidderDTO.setPid(randomLong());
      dealBidderDTO.setCompanyPid(randomLong());
      dealBidderDTO.setName(RandomStringUtils.randomAlphanumeric(32));
      dealBidderDTOS.add(dealBidderDTO);
    }
    return dealBidderDTOS;
  }

  public static List<Position> createPositions(int count) {
    List<Position> positions = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Position position = new Position();
      position.setPid(randomLong());
      position.setName(RandomStringUtils.randomAlphanumeric(32));
      positions.add(position);
    }
    return positions;
  }

  public static List<PositionMetricsAggregation> createPositionMetricsAggregation(int count) {
    List<PositionMetricsAggregation> positions = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      PositionMetricsAggregation position =
          new PositionMetricsAggregation(
              randomLong(),
              RandomStringUtils.randomAlphanumeric(32),
              RandomStringUtils.randomAlphanumeric(32),
              Status.ACTIVE,
              PlacementCategory.BANNER,
              randomLong(),
              TrafficType.MEDIATION,
              randomLong(),
              randomLong(),
              randomLong(),
              randomDouble(),
              randomDouble(),
              randomDouble(),
              randomDouble(),
              randomDouble());
      positions.add(position);
    }
    return positions;
  }

  public static List<Tag> createTags(int count) {
    List<Tag> tags = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Tag tag = new Tag();
      tag.setPid(randomLong());
      tag.setName(RandomStringUtils.randomAlphanumeric(32));
      tags.add(tag);
    }
    return tags;
  }

  public static List<TagView> createTagAggregation(int count) {
    List<TagView> tags = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      TagView tag =
          new TagView(
              randomLong(),
              RandomStringUtils.randomAlphanumeric(32),
              RandomStringUtils.randomAlphabetic(32),
              Status.ACTIVE,
              randomLong(),
              randomDouble(),
              randomLong(),
              randomLong(),
              RandomStringUtils.randomAlphabetic(32));
      tags.add(tag);
    }
    return tags;
  }

  public static BrandProtectionTag createBrandProtectionTag() {
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(123L);
    tag.setName("Test BP Tag");
    tag.setFreeTextTag(false);
    tag.setRtbId("RTB-1");
    tag.setUpdateDate(new Date());
    return tag;
  }

  public static BrandProtectionTagValues createBrandProtectionTagValues() {
    BrandProtectionTagValues tagValues = new BrandProtectionTagValues();
    tagValues.setPid(234L);
    tagValues.setTag(createBrandProtectionTag());
    tagValues.setName("Test BP Tag Values");
    tagValues.setUpdateDate(new Date());
    return tagValues;
  }

  public static BrandProtectionCategory createBrandProtectionCategory() {
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setPid(345L);
    category.setName("Test BP Category");
    category.setUpdateDate(new Date());
    return category;
  }

  public static CrsTagMapping createCrsTagMapping() {
    CrsTagMapping tagMapping = new CrsTagMapping();
    tagMapping.setPid(456L);
    tagMapping.setCrsTagId(789L);
    tagMapping.setCrsTagAttributeId(890L);
    return tagMapping;
  }

  public static HbPartnerDTO createNewHbPartnerDTO() {
    return HbPartnerDTO.builder()
        .pid(Long.valueOf(123))
        .version(0)
        .name("test_name")
        .id("abc_123")
        .partnerHandler("google")
        .status(Status.ACTIVE)
        .description("test description")
        .feeType(FeeType.PERCENTAGE)
        .fee(new BigDecimal(0.15))
        .formattedDefaultTypeEnabled(true)
        .build();
  }

  public static HbPartner createHbPartner() {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(randomLong());
    hbPartner.setId("test");
    hbPartner.setName("testName");
    hbPartner.setStatus(Status.ACTIVE);
    return hbPartner;
  }

  public static PublisherMetricsDTO createPublisherMetrics() {
    PublisherMetricsDTO publisherMetrics = new PublisherMetricsDTO(Interval.DAILY);
    publisherMetrics.addData(
        "2019-12-12T00:00:00-04:00",
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE,
        BigDecimal.ONE);
    return publisherMetrics;
  }

  public static HbPartnerCompany createHbPartnerCompany(
      Company company, HbPartner hbPartner, String externalId) {
    HbPartnerCompany hbPartnerCompany = new HbPartnerCompany();
    hbPartnerCompany.setCompany(company);
    hbPartnerCompany.setHbPartner(hbPartner);
    hbPartnerCompany.setExternalPubId(externalId);
    return hbPartnerCompany;
  }

  public static HbPartnerSite createHbPartnerSite(
      Site site, HbPartner hbPartner, String externalSiteId) {
    HbPartnerSite hbPartnerSite = new HbPartnerSite();
    hbPartnerSite.setSite(site);
    hbPartnerSite.setHbPartner(hbPartner);
    hbPartnerSite.setExternalSiteId(externalSiteId);
    return hbPartnerSite;
  }

  public static List<HbPartnersAssociationView> createDummyDefaultInventoriesPerHbPartners() {
    return Lists.newArrayList(
        new DummyHbPartnersAssociation(
            123L, 34L, Status.INACTIVE, AssociationType.DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(
            456L, 56L, Status.INACTIVE, AssociationType.DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(
            789L, 98L, Status.INACTIVE, AssociationType.DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(
            6L, 42L, Status.INACTIVE, AssociationType.DEFAULT_BANNER.ordinal()),
        new DummyHbPartnersAssociation(
            24L, 42L, Status.INACTIVE, AssociationType.DEFAULT_VIDEO.ordinal()));
  }

  public static List<HbPartnersAssociationView>
      createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition() {
    return Lists.newArrayList(
        new DummyHbPartnersAssociation(1L, 34L, Status.ACTIVE, AssociationType.DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(456L, 56L, Status.ACTIVE, AssociationType.DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(789L, 98L, Status.ACTIVE, AssociationType.DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(
            1L, 42L, Status.ACTIVE, AssociationType.DEFAULT_BANNER.ordinal()),
        new DummyHbPartnersAssociation(
            6L, 96L, Status.ACTIVE, AssociationType.DEFAULT_BANNER.ordinal()));
  }

  public static List<HbPartnersAssociationView>
      createDummyInventoriesPerHbPartnersWithNonDefaultPosition() {
    return Lists.newArrayList(
        new DummyHbPartnersAssociation(
            1L, 34L, Status.INACTIVE, AssociationType.NON_DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(
            456L, 56L, Status.INACTIVE, AssociationType.NON_DEFAULT.ordinal()),
        new DummyHbPartnersAssociation(
            789L, 98L, Status.INACTIVE, AssociationType.NON_DEFAULT.ordinal()));
  }

  public static List<HbPartner> createHbPartners() {
    return Lists.newArrayList(
        instantiateHbPartner(123L, "Google"),
        instantiateHbPartner(456L, "Amazon"),
        instantiateHbPartner(789L, "MoPub"),
        instantiateHbPartner(6L, "Google EB"),
        instantiateHbPartner(24L, "Amazon TAM"));
  }

  private static HbPartner instantiateHbPartner(Long pid, String name) {
    HbPartner partner = new HbPartner();
    partner.setPid(pid);
    partner.setName(name);
    return partner;
  }

  private static class DummyHbPartnersAssociation implements HbPartnersAssociationView {
    private final long partnerPid, inventoryPid;
    private Status status;
    private final int type;

    public DummyHbPartnersAssociation(long partnerPid, long inventoryPid, Status status, int type) {
      this.partnerPid = partnerPid;
      this.inventoryPid = inventoryPid;
      this.status = status;
      this.type = type;
    }

    @Override
    public Long getPid() {
      return inventoryPid;
    }

    @Override
    public Long getHbPartnerPid() {
      return partnerPid;
    }

    @Override
    public Status getPositionStatus() {
      return status;
    }

    @Override
    public Integer getType() {
      return type;
    }
  }

  public static SellerSeat createSellerSeat(
      long pid, String seatName, String seatDesc, Boolean seatStatus, Set<Company> sellers) {
    SellerSeat sellerSeat = createSellerSeat(pid, seatName, seatDesc, seatStatus);
    sellerSeat.setSellers(sellers);
    return sellerSeat;
  }

  public static SellerSeat createSellerSeat(
      long pid, String seatName, String seatDesc, Boolean seatStatus) {
    SellerSeat sellerSeat = new SellerSeat();
    sellerSeat.setPid(pid);
    sellerSeat.setName(seatName);
    sellerSeat.setStatus(seatStatus);
    sellerSeat.setDescription(seatDesc);
    return sellerSeat;
  }

  public static SellerSeatDTO createSellerSeatDTO(Long pid, Boolean status) {
    return createSellerSeatDTO(
        pid, status, "Default Seller Seat Name", "Default Seller Seat Description");
  }

  public static SellerSeatDTO createSellerSeatDTO(
      Long pid, Boolean status, String seatName, String seatDesc) {
    SellerSeatDTO sellerSeatDTO = new SellerSeatDTO();
    sellerSeatDTO.setPid(pid);
    sellerSeatDTO.setName(seatName);
    sellerSeatDTO.setDescription(seatDesc);
    sellerSeatDTO.setVersion(-1);
    sellerSeatDTO.setStatus(status);
    return sellerSeatDTO;
  }

  public static SellerSeatDTO createSellerSeatDTOWithSellers(
      String seatName, String seatDesc, Boolean seatStatus) {
    SellerSeatDTO sellerSeat = createSellerSeatDTO(null, seatStatus, seatName, seatDesc);
    Set<CompanyViewDTO> sellers = new HashSet<>();
    CompanyViewDTO companyViewDTO = new CompanyViewDTO(1L, "Company 1", CompanyType.SELLER, false);
    sellers.add(companyViewDTO);
    sellerSeat.setSellers(sellers);
    companyViewDTO = new CompanyViewDTO(2L, "Company 2", CompanyType.SELLER, false);
    sellers.add(companyViewDTO);
    return sellerSeat;
  }

  public static IntendedActionDTO createFilterRuleIntendedActionDto() {
    return createRuleIntendedActionDto(RuleActionType.FILTER, FilterType.BLOCKLIST.name());
  }

  public static IntendedActionDTOBuilder createFilterRuleIntendedActionBuilder() {
    return createRuleIntendedActionBuilder(RuleActionType.FILTER, FilterType.BLOCKLIST.name());
  }

  public static IntendedActionDTO createFloorRuleIntendedActionDto() {
    return createRuleIntendedActionDto(RuleActionType.FLOOR, "1");
  }

  private static IntendedActionDTO createRuleIntendedActionDto(
      RuleActionType actionType, String actionData) {
    return IntendedActionDTO.builder()
        .pid(randomLong())
        .version(randomInt())
        .actionType(actionType)
        .actionData(actionData)
        .build();
  }

  public static IntendedActionDTOBuilder createRuleIntendedActionBuilder(
      RuleActionType actionType, String actionData) {
    return IntendedActionDTO.builder()
        .pid(randomLong())
        .version(randomInt())
        .actionType(actionType)
        .actionData(actionData);
  }

  public static RuleIntendedAction createFilterRuleIntendedAction() {
    RuleIntendedAction ria = new RuleIntendedAction();
    ria.setPid(randomLong());
    ria.setVersion(1);
    ria.setActionType(com.nexage.admin.core.enums.RuleActionType.FILTER);
    ria.setActionData("1");
    return ria;
  }

  public static RuleIntendedAction createFloorRuleIntendedAction() {
    RuleIntendedAction ria = new RuleIntendedAction();
    ria.setPid(randomLong());
    ria.setVersion(1);
    ria.setActionType(com.nexage.admin.core.enums.RuleActionType.FLOOR);
    ria.setActionData("1.00");
    return ria;
  }

  public static RuleTarget createRuleTarget() {
    RuleTarget entity = new RuleTarget();
    entity.setPid(1000L);
    entity.setVersion(1);
    entity.setRuleTargetType(RuleTargetType.BUYER_SEATS);
    entity.setMatchType(MatchType.INCLUDE_LIST);
    entity.setStatus(Status.ACTIVE);
    entity.setData("[{\"bidder\":1000}, {\"buyerCompany\":20}]");
    entity.setRule(new CompanyRule());

    return entity;
  }

  public static RuleTargetDTO createRuleTargetDto() {
    return createRuleTargetDto(1001L, MatchType.EXCLUDE_LIST);
  }

  public static RuleTargetDTO createRuleTargetDto(Long pid, MatchType matchType) {
    return createRuleTargetBuilder(pid, matchType).build();
  }

  public static RuleTargetDTOBuilder createRuleTargetBuilder(Long pid, MatchType matchType) {
    return RuleTargetDTO.builder()
        .pid(pid)
        .version(2)
        .targetType(RuleTargetType.BIDDER)
        .matchType(matchType)
        .status(Status.INACTIVE)
        .data("{\"w\":1000, \"h\":1000}");
  }

  public static RuleTargetDTO createRuleTargetDto(RuleTargetType targetType, String data) {
    return RuleTargetDTO.builder().targetType(targetType).data(data).build();
  }

  public static SellerSeatRule createSellerSeatRule() {
    SellerSeatRule rule = new SellerSeatRule();
    rule.setPid(2000L);
    rule.setVersion(1);
    rule.setStatus(Status.ACTIVE);
    rule.setRuleIntendedActions(newHashSet(createFilterRuleIntendedAction()));
    rule.setName("seller seat rule");
    rule.setRuleTargets(newHashSet(createRuleTarget()));
    rule.setRuleType(BRAND_PROTECTION);
    rule.setDescription("rule for seller seat");
    rule.setSellerSeatPid(5L);
    return rule;
  }

  public static SellerSeatRuleDTO createSellerSeatRuleDto() {
    return createSellerSeatRuleDto(2001L, 5L, 2, RuleType.BRAND_PROTECTION);
  }

  public static SellerSeatRuleDTO createSellerSeatRuleDto(Long ruleId) {
    return createSellerSeatRuleDto(ruleId, 5L, 2, RuleType.BRAND_PROTECTION);
  }

  public static SellerSeatRuleDTO createSellerSeatRuleDto(Long ruleId, Long sellerSeatId) {
    return createSellerSeatRuleDto(ruleId, sellerSeatId, 2, RuleType.BRAND_PROTECTION);
  }

  public static SellerSeatRuleDTO createSellerSeatRuleDto(
      Long ruleId, Long sellerSeatId, Integer version) {
    return createSellerSeatRuleDto(ruleId, sellerSeatId, version, RuleType.BRAND_PROTECTION);
  }

  public static SellerSeatRuleDTO createSellerSeatRuleDto(SellerSeatRule rule) {
    return SellerSeatRuleDTO.builder()
        .pid(rule.getPid())
        .version(rule.getVersion())
        .status(rule.getStatus())
        .intendedActions(newHashSet(createFilterRuleIntendedActionDto()))
        .name("seller seat rule dto")
        .targets(newHashSet(createRuleTargetDto()))
        .type(RuleType.BRAND_PROTECTION)
        .description("rule dto for seller seat")
        .sellerSeatPid(rule.getSellerSeatPid())
        .build();
  }

  public static SellerSeatRuleDTO createSellerSeatRuleDto(
      Long ruleId, Long sellerSeatId, Integer version, RuleType type) {
    return createSellerSeatRuleBuilder(ruleId, sellerSeatId, version, type).build();
  }

  public static SellerSeatRuleDTOBuilder createSellerSeatRuleBuilder(
      Long ruleId, Long sellerSeatId, Integer version, RuleType type) {
    return SellerSeatRuleDTO.builder()
        .pid(ruleId)
        .version(version)
        .status(Status.ACTIVE)
        .intendedActions(newHashSet(createFilterRuleIntendedActionDto()))
        .name("seller seat rule dto")
        .targets(newHashSet(createRuleTargetDto()))
        .type(type)
        .description("rule dto for seller seat")
        .sellerSeatPid(sellerSeatId);
  }

  public static SellerRuleDTO.SellerRuleDTOBuilder createSellerRuleDtoBuilder() {
    return SellerRuleDTO.builder()
        .name(RandomStringUtils.randomAlphanumeric(10))
        .pid(3000L)
        .version(1)
        .ownerCompanyPid(20000L)
        .description(RandomStringUtils.randomAlphanumeric(44))
        .type(RuleType.BRAND_PROTECTION)
        .status(com.nexage.admin.core.enums.Status.ACTIVE)
        .intendedActions(newHashSet(createFilterRuleIntendedActionDto()))
        .targets(newHashSet(createRuleTargetDto()));
  }

  public static SellerRuleDTO createSellerRuleDto(RuleType ruleType) {
    return createSellerRuleDtoBuilder().type(ruleType).build();
  }

  public static SellerRuleDTO createSellerRuleDto(
      RuleType ruleType, Set<RuleTargetDTO> ruleTarget) {
    return createSellerRuleDtoBuilder().type(ruleType).targets(ruleTarget).build();
  }

  public static SellerRuleDTO createSellerRuleDto() {
    SellerRuleDTO.SellerRuleDTOBuilder builder = createSellerRuleDtoBuilder();
    return builder.build();
  }

  public static SellerRuleDTO createSellerRuleDto(Long rulePid) {
    SellerRuleDTO.SellerRuleDTOBuilder builder = createSellerRuleDtoBuilder();
    builder.pid(rulePid);
    return builder.build();
  }

  public static SellerRuleDTO createSellerRuleDto(int version) {
    SellerRuleDTO.SellerRuleDTOBuilder builder = createSellerRuleDtoBuilder();
    builder.version(version);
    return builder.build();
  }

  public static SellerRuleDTO createSellerRuleDtoWithOwnerCompanyPid(Long ownerCompanyPid) {
    SellerRuleDTO.SellerRuleDTOBuilder builder = createSellerRuleDtoBuilder();
    builder.ownerCompanyPid(ownerCompanyPid);
    return builder.build();
  }

  public static PublisherAssignmentDTO createPublisherAssignmentDto() {
    return PublisherAssignmentDTO.builder()
        .pid(randomLong())
        .name(RandomStringUtils.randomAlphanumeric(10))
        .build();
  }

  public static RuleDeployedCompany createRuleDeployedCompany() {
    RuleDeployedCompany rdc = new RuleDeployedCompany();
    rdc.setPid(randomLong());
    rdc.setName(RandomStringUtils.randomAlphanumeric(10));
    return rdc;
  }

  public static RuleDeployedCompany createRuleDeployedCompany(Long pid) {
    RuleDeployedCompany rdc = createRuleDeployedCompany();
    rdc.setPid(pid);
    return rdc;
  }

  public static SiteAssignmentDTO createSiteAssignmentDto() {
    return SiteAssignmentDTO.builder()
        .pid(randomLong())
        .name(RandomStringUtils.randomAlphanumeric(10))
        .publisherAssignment(createPublisherAssignmentDto())
        .build();
  }

  public static RuleDeployedSite createRuleDeployedSite() {
    RuleDeployedSite rds = new RuleDeployedSite();
    rds.setPid(randomLong());
    rds.setName(RandomStringUtils.randomAlphanumeric(10));
    return rds;
  }

  public static RuleDeployedSite createRuleDeployedSite(Long companyPid) {
    RuleDeployedSite rds = createRuleDeployedSite();
    rds.setCompanyPid(companyPid);
    return rds;
  }

  public static PositionAssignmentDTO createPositionAssignmentDto() {
    return PositionAssignmentDTO.builder()
        .pid(randomLong())
        .name(RandomStringUtils.randomAlphanumeric(10))
        .siteAssignment(createSiteAssignmentDto())
        .build();
  }

  public static RuleDeployedPosition createRuleDeployedPosition() {
    RuleDeployedPosition rdp = new RuleDeployedPosition();
    rdp.setPid(randomLong());
    rdp.setName(RandomStringUtils.randomAlphanumeric(10));
    return rdp;
  }

  public static RuleDeployedPosition createRuleDeployedPositionWithSiteOwnerCompanyPid(
      Long companyPid) {
    RuleDeployedPosition rdp = createRuleDeployedPosition();
    rdp.setSite(createRuleDeployedSite(companyPid));
    return rdp;
  }

  public static InventoryAssignmentsDTO createInventoryAssignmentsDto() {
    return InventoryAssignmentsDTO.builder()
        .publishers(Sets.newHashSet(createPublisherAssignmentDto()))
        .sites(Sets.newHashSet(createSiteAssignmentDto()))
        .positions(Sets.newHashSet(createPositionAssignmentDto()))
        .build();
  }

  public static CompanyRule createCompanyRule() {
    CompanyRule rule = new CompanyRule();
    Long ownerCompanyPid = randomLong();
    rule.setName(RandomStringUtils.randomAlphanumeric(10));
    rule.setDescription(RandomStringUtils.randomAlphanumeric(10));
    rule.setPid(randomLong());
    rule.setOwnerCompanyPid(ownerCompanyPid);
    rule.setVersion(1);
    rule.setRuleIntendedActions(Sets.newHashSet(createFilterRuleIntendedAction()));
    rule.setDeployedPositions(
        Sets.newHashSet(createRuleDeployedPositionWithSiteOwnerCompanyPid(ownerCompanyPid)));
    rule.setDeployedSites(Sets.newHashSet(createRuleDeployedSite(ownerCompanyPid)));
    rule.setDeployedCompanies(Sets.newHashSet(createRuleDeployedCompany(ownerCompanyPid)));
    rule.setRuleType(BRAND_PROTECTION);
    return rule;
  }

  public static CompanyRule createCompanyRule(Long pid) {
    CompanyRule rule = createCompanyRule();
    rule.setPid(pid);
    return rule;
  }

  public static CompanyRule createCompanyRule(int version) {
    CompanyRule rule = createCompanyRule();
    rule.setVersion(version);
    return rule;
  }

  public static CompanyRule createCompanyRule(
      Long ownerCompanyPid,
      Set<RuleDeployedCompany> deployedCompanies,
      Set<RuleDeployedSite> deployedSites,
      Set<RuleDeployedPosition> deployedPositions) {
    CompanyRule rule = createCompanyRule();
    rule.setOwnerCompanyPid(ownerCompanyPid);
    rule.setDeployedCompanies(deployedCompanies);
    rule.setDeployedPositions(deployedPositions);
    rule.setDeployedSites(deployedSites);
    return rule;
  }

  public static class FeeAdjustmentBuilder {
    private FeeAdjustment feeAdjustment;

    public FeeAdjustmentBuilder(
        Long pid,
        String name,
        Boolean inclusive,
        Double demandFeeAdjustment,
        Integer version,
        Boolean enabled,
        String description,
        Date creationDate) {
      FeeAdjustment feeAdjustment = new FeeAdjustment();

      feeAdjustment.setPid(pid);
      feeAdjustment.setName(name);
      feeAdjustment.setInclusive(inclusive);
      feeAdjustment.setDemandFeeAdjustment(demandFeeAdjustment);
      feeAdjustment.setVersion(version);
      feeAdjustment.setVersion(version);
      feeAdjustment.setEnabled(enabled);
      feeAdjustment.setDescription(description);
      feeAdjustment.setLastUpdate(creationDate);
      feeAdjustment.setCreationDate(creationDate);

      this.feeAdjustment = feeAdjustment;
    }

    public FeeAdjustmentBuilder addSeller(Long pid, FeeAdjustmentCompanyView seller) {
      FeeAdjustmentSeller feeAdjustmentSeller = new FeeAdjustmentSeller();

      feeAdjustmentSeller.setPid(pid);
      feeAdjustmentSeller.setFeeAdjustment(feeAdjustment);
      feeAdjustmentSeller.setSeller(seller);

      feeAdjustment.getFeeAdjustmentSellers().add(feeAdjustmentSeller);
      return this;
    }

    public FeeAdjustmentBuilder addBuyer(Long pid, FeeAdjustmentCompanyView buyer) {
      FeeAdjustmentBuyer feeAdjustmentBuyer = new FeeAdjustmentBuyer();

      feeAdjustmentBuyer.setPid(pid);
      feeAdjustmentBuyer.setFeeAdjustment(feeAdjustment);
      feeAdjustmentBuyer.setBuyer(buyer);

      feeAdjustment.getFeeAdjustmentBuyers().add(feeAdjustmentBuyer);
      return this;
    }

    public FeeAdjustment getInstance() {
      return feeAdjustment;
    }
  }

  public static PlacementVideoDTO createDefaultPlacementVideoDTO() {
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setPlayerHeight(320);
    placementVideoDTO.setPlayerWidth(480);
    placementVideoDTO.setLongform(false);
    return placementVideoDTO;
  }

  public static PlacementVideoDTO createDefaultLongformPlacementVideoDTO() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    placementVideoDTO.setPlayerBrand("test_player");
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);

    return placementVideoDTO;
  }

  public static PlacementVideo createDefaultPlacementVideo() {
    PlacementVideo placementVideo = new PlacementVideo();
    placementVideo.setPid(1L);
    placementVideo.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideo.setPlayerRequired(true);
    placementVideo.setPlayerHeight(320);
    placementVideo.setPlayerWidth(480);

    return placementVideo;
  }

  public static PlacementVideoDTO createDefaultPlacementVideoDTOWithPlaylistInfo() {
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setPlayerHeight(320);
    placementVideoDTO.setPlayerWidth(480);
    placementVideoDTO.setLongform(false);
    placementVideoDTO.setDapPlayerType(DapPlayerType.YVAP);

    PlacementVideoPlaylistDTO placementVideoPlaylistDTO =
        createDefaultPlacementVideoPlaylistDTO(placementVideoDTO.getPid());
    placementVideoDTO.setPlaylistInfo(Arrays.asList(placementVideoPlaylistDTO));

    return placementVideoDTO;
  }

  public static PlacementVideoPlaylistDTO createDefaultPlacementVideoPlaylistDTO(
      Long placementVideoPid) {
    PlacementVideoPlaylistDTO placementVideoPlaylistDTO = new PlacementVideoPlaylistDTO();
    placementVideoPlaylistDTO.setPid(1L);
    placementVideoPlaylistDTO.setMediaType(MediaType.VIDEO_MP4);
    placementVideoPlaylistDTO.setFallbackURL("someurl.mp4");
    placementVideoPlaylistDTO.setPlacementVideoPid(placementVideoPid);

    return placementVideoPlaylistDTO;
  }

  public static PlacementVideoPlaylist createDefaultPlacementVideoPlaylist(
      PlacementVideo placementVideoPid) {
    PlacementVideoPlaylist placementVideoPlaylist = new PlacementVideoPlaylist();
    placementVideoPlaylist.setPid(1L);
    placementVideoPlaylist.setMediaType(MediaType.VIDEO_MP4);
    placementVideoPlaylist.setFallbackURL("someurl.mp4");
    placementVideoPlaylist.setPlacementVideoPid(placementVideoPid);

    return placementVideoPlaylist;
  }

  public static PlacementVideoCompanionDTO createDefaultPlacementVideoCompanionDTO() {
    PlacementVideoCompanionDTO placementVideoCompanionDTO = new PlacementVideoCompanionDTO();
    placementVideoCompanionDTO.setHeight(320);
    placementVideoCompanionDTO.setWidth(480);
    return placementVideoCompanionDTO;
  }

  public static PlacementVideoCompanion createDefaultPlacementVideoCompanion() {
    PlacementVideoCompanion placementVideoCompanion = new PlacementVideoCompanion();
    placementVideoCompanion.setHeight(320);
    placementVideoCompanion.setWidth(480);
    return placementVideoCompanion;
  }

  public static PositionView createPositionView() {
    PositionView positionView = new PositionView();
    positionView.setPid(randomLong());
    positionView.setName(RandomStringUtils.randomAlphanumeric(32));
    return positionView;
  }

  public static RuleTargetDTO.RuleTargetDTOBuilder getRuleTargetDTOBuilder() {
    return RuleTargetDTO.builder()
        .matchType(INCLUDE_LIST)
        .targetType(REVGROUP)
        .data("12,13,14")
        .status(Status.ACTIVE);
  }

  public static RuleTarget createRevgroupRuleTarget() {
    RuleTarget ruleTarget = new RuleTarget();
    ruleTarget.setPid(1L);
    ruleTarget.setVersion(0);
    ruleTarget.setMatchType(INCLUDE_LIST);
    ruleTarget.setRuleTargetType(REVGROUP);
    ruleTarget.setData("12,13,14");
    return ruleTarget;
  }

  public static RevenueGroup createRevenueGroup() {
    return new RevenueGroup(
        randomPid(),
        RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(16),
        Status.ACTIVE,
        randomInt(),
        null,
        null);
  }
}
