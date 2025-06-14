package com.nexage.admin.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Advertiser;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.Campaign.CampaignModel;
import com.nexage.admin.core.model.Campaign.CampaignStatus;
import com.nexage.admin.core.model.Campaign.CampaignType;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Creative;
import com.nexage.admin.core.model.Creative.CreativeAdType;
import com.nexage.admin.core.model.Creative.CreativeStatus;
import com.nexage.admin.core.model.ExternalDataProvider;
import com.nexage.admin.core.model.ExternalDataProvider.EnablementStatus;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Target;
import com.nexage.admin.core.model.Target.TargetRule;
import com.nexage.admin.core.model.Target.TargetType;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.TimeZone;

public class TestUtil {

  public static final String DB1_PREFIX = "db1.";

  public static final String DB2_PREFIX = "db2.";

  public static final String TEST_PREFIX = "test.";

  private static Properties prop = new Properties();

  static {
    try {
      prop.load(
          Thread.currentThread()
              .getContextClassLoader()
              .getResourceAsStream("validate_data.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static User getTestUser() {
    User testUser = new User();
    testUser.setVersion(1);
    testUser.setName(getValue("test.user.name"));
    testUser.setEmail(getValue("test.user.email"));
    testUser.setUserName(getValue("test.user.userName"));
    testUser.setRole(Role.ROLE_USER);
    testUser.setContactNumber(getValue("test.user.contactNumber"));
    testUser.setEnabled(Boolean.valueOf(getValue("test.user.isEnabled")));
    testUser.setTitle(getValue("test.user.title"));
    testUser.setGlobal(false);
    return testUser;
  }

  public static User getTestUserForCompany(Company c) {
    User testUser = getTestUser();
    testUser.addCompany(c);
    return testUser;
  }

  public static Company getTestCompany() {
    Company company =
        new Company(
            getValue("test.company.name"), CompanyType.valueOf(getValue("test.company.type")));
    company.setId("guid");
    company.setPid(3L);
    company.setWebsite(getValue("test.company.url"));
    company.setDescription(getValue("test.company.description"));
    company.setStatus(Status.ACTIVE);
    company.setDhReportingId(getValue("test.company.dhReportingId"));
    return company;
  }

  public static Campaign getTestCampaign() {
    Campaign campaign = new Campaign();
    campaign.setPid(400L);
    campaign.setAdvertiserId(200L);
    campaign.setName("Test Campaign 1");
    campaign.setBias(BigDecimal.TEN);
    campaign.setCap24(11);
    campaign.setCapHour(3);
    campaign.setCapLife(877366363);
    campaign.setDaily(7439L);
    campaign.setExternalId("439204383sajkjfdjsajsfsjfs");
    campaign.setGoal(8473928788977897L);
    campaign.setLastUpdate(new java.sql.Date(1332866100000L));
    campaign.setPrice(BigDecimal.ONE);
    campaign.setAdvertiserId(87);
    campaign.setSellerId(17);
    campaign.setModel(CampaignModel.CPM);
    campaign.setStatus(CampaignStatus.INACTIVE);
    campaign.setStart(new java.sql.Date(1332866100000L));
    campaign.setStop(new java.sql.Date(1332866100000L));
    campaign.setType(CampaignType.REMNANT);
    campaign.setAdvertiserName("Test Advertiser");
    return campaign;
  }

  public static Advertiser getTestAdvertiser() {
    Advertiser advertiser = new Advertiser(200L);
    advertiser.setName("Test Advertiser 1");
    advertiser.setSellerId(17);
    advertiser.setStatus(Advertiser.AdvertiserStatus.ACTIVE);
    return advertiser;
  }

  public static Advertiser getTestAdvertiserForCompany(Company c) {
    Advertiser advertiser = getTestAdvertiser();
    advertiser.setSellerId(c.getPid());
    return advertiser;
  }

  public static Creative getTestCreative() {
    Creative creative = new Creative();
    creative.setSellerId(37L);
    creative.setAdvertiserId(61L);
    creative.setName("Test Creative 1");
    creative.setAdType(CreativeAdType.TEXT_ONLY);
    creative.setWidth(7);
    creative.setHeight(50);
    creative.setBanner("banner");
    creative.setMma120x20("mma120x20");
    creative.setMma168x28("mma168x28");
    creative.setMma216x36("mma216x36");
    creative.setMma300x50("mma300x50");
    creative.setMma320x50("mma320x50");
    creative.setBannerAlt("bannerAlt");
    creative.setAdText("adText");
    creative.setLandingUrl("landingUrl");
    creative.setTrackingUrl("trackingUrl");
    creative.setLastUpdate(new java.sql.Date(1332866100000L));
    creative.setStatus(CreativeStatus.NOT_DELETED);
    creative.setTemplateId(1L);
    return creative;
  }

  public static BidderConfig getTestBidderConfig() {
    BidderConfig bc = new BidderConfig();
    bc.setPid(2L);
    bc.setId("Bidder-Config-2");
    bc.setName("DEFAULTNAME");
    bc.setTrafficStatus(false);
    bc.setBidRequestUrl("http://bidder1.com/getBid");
    bc.setNoticeUrl("http://bidder1.com/notifyWon");
    bc.setBidderSubscriptions(new HashSet<>());
    bc.setRequestRateFilter(-1);
    bc.setBidRequestCpm(BigDecimal.ZERO);
    return bc;
  }

  public static ExternalDataProvider getTestExternalDataProvider() {
    ExternalDataProvider dp = new ExternalDataProvider();
    dp.setName("DSP-Test-1");
    dp.setBaseUrl("foo.com");
    dp.setEnablementStatus(EnablementStatus.ACTIVE);
    dp.setDescription("DSP Test description");
    dp.setDataProviderImplClass("com.nexage.FOO1");
    dp.setFilterRequestRate(100);
    dp.setCreationDate(new Date());
    return dp;
  }

  public static Creative getTestCreativeForCompanyAndAdvertiser(
      Long companyPid, Long advertiserPid) {
    Creative creative = getTestCreative();
    creative.setSellerId(companyPid);
    creative.setAdvertiserId(advertiserPid);
    return creative;
  }

  public static Target getTestTarget(Campaign campaign) {
    Target target = new Target(campaign);
    target.setType(TargetType.ZONE);
    target.setRule(TargetRule.IS_ANY_OF);
    target.setFilter("site_1/zone_1");
    target.setLastUpdate(new java.sql.Date(1332866100000L));
    return target;
  }

  /**
   * This doesn't validate fields 1. lastUpdate since this is volatile and cannot be matched against
   * some test time
   *
   * @param dbUser user
   */
  public static void validateUser(User dbUser, String prefix) {
    assertNotNull(dbUser);
    assertNotNull(dbUser.getId());
    assertNotNull(dbUser.getCreationDate().toString());
    // assertEquals(getValue(prefix + "user.contactNumber"), dbUser.getContactNumber());
    assertEquals(getValue(prefix + "user.role"), dbUser.getRole().toString());
    assertEquals(getValue(prefix + "user.title"), dbUser.getTitle());
    assertEquals(getValue(prefix + "user.email"), dbUser.getEmail());
    assertEquals(getValue(prefix + "user.userName"), dbUser.getUserName());
    assertEquals(Boolean.valueOf(getValue(prefix + "user.isEnabled")), dbUser.isEnabled());

    // Assuming only UserManagerTestCase will validate user object
    validateCompany(dbUser.getCompany(), prefix);
    // assertEquals(String.valueOf(dbUser.getCompany().getUsers().size()), getValue(prefix +
    // "company.numUsers"));
  }

  public static void validateUsers(User user1, User user2) {
    assertNotNull(user2);
    assertNotNull(user2.getId());
    assertNotNull(user2.getCompany());

    assertEquals(user1.getCreationDate(), user2.getCreationDate());
    assertEquals(user1.getContactNumber(), user2.getContactNumber());
    assertEquals(user1.getLastUpdate(), user2.getLastUpdate());
    assertEquals(user1.getRole(), user2.getRole());
    assertEquals(user1.getTitle(), user2.getTitle());
    assertEquals(user1.getEmail(), user2.getEmail());
    assertEquals(user1.getUserName(), user2.getUserName());
    assertEquals(user1.isEnabled(), user2.isEnabled());

    assertNotNull(user2.getCompany());
    assertNotNull(user2.getCompany().getId());

    assertEquals(user1.getCompany().getName(), user2.getCompany().getName());
    assertEquals(user1.getCompany().getDescription(), user2.getCompany().getDescription());
    assertEquals(user1.getCompany().getWebsite(), user2.getCompany().getWebsite());
    assertEquals(user1.getCompany().getType(), user2.getCompany().getType());
    // assertEquals(user1.getCompany().getUsers().size(), user2.getCompany().getUsers().size());
  }

  // This doesn't validate test sites and user contact (use other overloaded method to include them)
  public static void validateCompany(Company dbCompany, String prefix) {
    validateCompany(dbCompany, prefix, false);
  }

  public static void validateCompany(Company dbCompany, String prefix, boolean assertContact) {
    assertNotNull(dbCompany);
    assertNotNull(dbCompany.getId());

    assertEquals(getValue(prefix + "company.name"), dbCompany.getName());
    assertEquals(getValue(prefix + "company.description"), dbCompany.getDescription());
    assertEquals(getValue(prefix + "company.url"), dbCompany.getWebsite());
    assertEquals(getValue(prefix + "company.type"), dbCompany.getType().toString());
    assertEquals(getValue(prefix + "company.dhReportingId"), dbCompany.getDhReportingId());

    // TODO Write test cases to get SITEs for company...
    //		if (prefix.equals(DB1_PREFIX))
    //			assertEquals(dbCompany.getSites().size(), 0);
    //		else if (prefix.equals(DB2_PREFIX))
    //			assertEquals(dbCompany.getSites().size(), 2);

    if (assertContact) {
      assertNotNull(dbCompany.getContact());
      assertEquals(getValue(prefix + "company.contact.name"), dbCompany.getContact().getName());
    }
  }

  public static void validateSite(Site site, String prefix) { // only minimal validation now
    validateSite(site, prefix, true);
  }

  public static void validateSite(Site site, String prefix, boolean validateCompany) {
    assertNotNull(site);
    assertNotNull(site.getDcn());

    assertEquals(getValue(prefix + "site.name"), site.getName());
    assertEquals(getValue(prefix + "site.description"), site.getDescription());
    assertEquals("MOBILE_WEB", String.valueOf(site.getType()));
    // assertEquals(1, site.getStatus().asInt());
    assertEquals(getValue(prefix + "site.url"), site.getUrl());
    assertNotNull(site.getCompany());

    if (validateCompany) validateCompany(site.getCompany(), DB2_PREFIX);

    // Values not populated for v1.0
    // assertEquals(getValue(prefix + "site.shortName"), site.getShortName());
    // assertEquals(getValue(prefix + "site.channelId"), site.getChannelID());
  }

  public static void validateCampaign(Campaign campaign, String prefix) {
    assertNotNull(campaign);
    assertNotNull(campaign.getPid());

    assertEquals(getValue(prefix + "campaign." + "name"), campaign.getName());
    assertEquals(getValue(prefix + "campaign." + "bias"), String.valueOf(campaign.getBias()));
    assertEquals(getValue(prefix + "campaign." + "cap_24"), String.valueOf(campaign.getCap24()));
    assertEquals(
        getValue(prefix + "campaign." + "cap_hour"), String.valueOf(campaign.getCapHour()));
    assertEquals(
        getValue(prefix + "campaign." + "cap_life"), String.valueOf(campaign.getCapLife()));
    assertEquals(getValue(prefix + "campaign." + "daily"), String.valueOf(campaign.getDaily()));
    assertEquals(getValue(prefix + "campaign." + "goal"), String.valueOf(campaign.getGoal()));
    // assertEquals(getValue(prefix + "campaign." + "last_update"),
    // String.valueOf(campaign.getLastUpdate()));
    assertEquals(getValue(prefix + "campaign." + "price"), String.valueOf(campaign.getPrice()));
    assertEquals(
        getValue(prefix + "campaign." + "advertiser_id"),
        String.valueOf(campaign.getAdvertiserId()));
    assertEquals(
        getValue(prefix + "campaign." + "seller_id"), String.valueOf(campaign.getSellerId()));
    assertEquals(getValue(prefix + "campaign." + "model"), campaign.getModel().toString());
    assertEquals(getValue(prefix + "campaign." + "status"), campaign.getStatus().toString());
    assertDateEquals(prefix + "campaign." + "start", campaign.getStart());
    assertDateEquals(prefix + "campaign." + "stop", campaign.getStop());
    assertEquals(getValue(prefix + "campaign." + "type"), campaign.getType().toString());
  }

  public static void validateCreative(Creative creative, String prefix) {
    assertNotNull(creative);
    assertNotNull(creative.getPid());

    assertEquals(getValue(prefix + "creative." + "name"), creative.getName());
    assertEquals(getValue(prefix + "creative." + "ad_type"), String.valueOf(creative.getAdType()));
    assertEquals(getValue(prefix + "creative." + "banner"), creative.getBanner());
    assertEquals(getValue(prefix + "creative." + "mma_120x20"), creative.getMma120x20());
    assertEquals(getValue(prefix + "creative." + "mma_168x28"), creative.getMma168x28());
    assertEquals(getValue(prefix + "creative." + "mma_216x36"), creative.getMma216x36());
    assertEquals(getValue(prefix + "creative." + "mma_300x50"), creative.getMma300x50());
    assertEquals(getValue(prefix + "creative." + "mma_320x50"), creative.getMma320x50());
    assertEquals(getValue(prefix + "creative." + "banner_alt"), creative.getBannerAlt());
    assertEquals(getValue(prefix + "creative." + "ad_text"), creative.getAdText());
    assertEquals(getValue(prefix + "creative." + "landing_url"), creative.getLandingUrl());
    assertEquals(getValue(prefix + "creative." + "tracking_url"), creative.getTrackingUrl());
    // assertEquals(getValue(prefix + "creative." + "last_update"),
    // String.valueOf(creative.getLastUpdate()));
    assertEquals(getValue(prefix + "creative." + "status"), creative.getStatus().toString());
    assertEquals(
        getValue(prefix + "creative." + "template_id"), String.valueOf(creative.getTemplateId()));
  }

  public static String getValue(String key) {
    return prop.getProperty(key);
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:hsqldb:mem:coreDS", "sa", "sa");
  }

  public static String formatSiteInsert(
      String dcn,
      Long pid,
      int version,
      Status status,
      String description,
      String name,
      Type type,
      String url,
      Long company_pid,
      String domain,
      Platform platform) {
    String datestring = "2013-01-07 00:00:00";
    String template =
        "insert into site (dcn, pid, version, status, description, name, type, url, company_pid, domain,"
            + "ad_screening, consumer_profile_contributed, consumer_profile_used, creation_date, days_free, filter_bots, integration, last_update, live,"
            + "override_ip, platform, report_batch_size, report_frequency, revenue_launch_date, rules_update_frequency, buyer_timeout, send_ids, total_timeout, traffic_throttle, id, coppa_restricted, enable_groups, ad_truth)"
            + " values ('%s', %d, %d, %d, '%s', '%s', '%s', '%s', %d, '%s', 0, 0, 0, '%s', 0, 0, '%s', '%s', 1, 0, '%s', 10, 1, '%s', 2, 0, 100, 1, 500, 0, '%s', 0, 0, 0)";
    String statement =
        String.format(
            template,
            dcn,
            pid,
            version,
            status.asInt(),
            description,
            name,
            type,
            url,
            company_pid,
            domain,
            datestring,
            datestring,
            platform,
            datestring,
            dcn);

    return statement;
  }

  protected static void assertDateEquals(final String expectedDateKey, final Date actualDate) {
    String expectedDate = getValue(expectedDateKey);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    try {
      assertEquals(sdf.parse(expectedDate), actualDate);
    } catch (Exception ex) {
      assertFalse(
          true,
          "Value set in property \""
              + expectedDateKey
              + "\" is not a valid EST date: "
              + expectedDate);
    }
  }
}
