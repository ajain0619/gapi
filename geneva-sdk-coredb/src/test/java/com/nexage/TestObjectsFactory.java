package com.nexage;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.type.BDRFreqCapMode;
import com.nexage.admin.core.bidder.type.BDRLineItemType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.ExternalDataProvider;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentBuyer;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentSeller;
import com.nexage.admin.core.util.UUIDGenerator;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Nick Ilkevich
 * @since 05.09.2014
 */
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
    lineItem.setFrequencyCapMode(BDRFreqCapMode.NONE);
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

  public static BDRAdvertiser createNewBdrAdvertiser(Company company) {
    BDRAdvertiser advertiser = new BDRAdvertiser();
    advertiser.setName(RandomStringUtils.randomAlphanumeric(32));
    advertiser.setCompany(company);
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

  public static String randomUrl() {
    String url = "http://";
    url += RandomStringUtils.randomAlphabetic(10);
    url += ".";
    url += RandomStringUtils.randomAlphabetic(3);
    return url;
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
    setPid(obj, randomLong());
  }

  public static void setPid(Object obj, long pid) {
    try {
      Field pidField = obj.getClass().getDeclaredField("pid");
      pidField.setAccessible(true);
      pidField.set(obj, pid);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException();
    }
  }

  public static Company createCompany(CompanyType type) {
    Company company = new Company(RandomStringUtils.randomAlphanumeric(32), type);
    company.setId(new UUIDGenerator().generateUniqueId());
    company.setPid(randomLong());
    company.setStatus(Status.ACTIVE);
    company.setWebsite(randomUrl());
    return company;
  }

  public static User createUser() {
    return createUser(Role.ROLE_ADMIN, createCompany(CompanyType.NEXAGE));
  }

  public static Site createSite() {
    Site siteDTO = new Site();
    siteDTO.setPid(randomLong());
    siteDTO.setId("testId");
    siteDTO.setStatus(Status.ACTIVE);
    siteDTO.setName("testSite");
    siteDTO.setDomain("testDomain");
    siteDTO.setPlatform(Platform.ANDROID);
    siteDTO.setType(Type.MOBILE_WEB);
    siteDTO.setDcn("test");

    return siteDTO;
  }

  public static HbPartner createHbPartner() {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(randomLong());
    hbPartner.setId("test");
    hbPartner.setName("testName");
    hbPartner.setStatus(Status.ACTIVE);
    return hbPartner;
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
      user.setSellerSeat(createSellerSeat(companies));
    }
    Arrays.stream(companies).forEach(user::addCompany);
    user.setRole(role);
    user.setEnabled(true);
    return user;
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

  public static boolean randomBoolean() {
    return new Random().nextBoolean();
  }

  public static double randomDouble() {
    return new Random().nextDouble();
  }

  public static int randomInt() {
    return new Random().nextInt();
  }

  public static long randomLong() {
    return new Random().nextLong();
  }
}
