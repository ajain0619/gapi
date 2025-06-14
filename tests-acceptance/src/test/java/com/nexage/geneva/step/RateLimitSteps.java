package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.PositionPss;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.model.crud.User;
import com.nexage.geneva.request.PositionRequests;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.RtbProfileLibrariesRequests;
import com.nexage.geneva.request.SiteRequests;
import com.nexage.geneva.request.UserDTORequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.SetUpWiremockStubs;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.UUIDGenerator;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

public class RateLimitSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SiteRequests siteRequests;
  @Autowired public DatabaseUtils databaseUtils;
  @Autowired private PositionRequests positionRequests;
  @Autowired private RtbProfileLibrariesRequests requests;
  @Autowired private UserDTORequests userDTORequests;

  public JSONResource serverResponse;
  public Request request;
  private Map<String, String> requestMap;
  private Site site;
  private User user;
  private PositionPss positionPss;
  private String tag, bidderGroupName, searchUserName, blockGroupName;
  private JSONObject siteChanges, expectedPosition, expectedLibrary, actualPosition;
  private static final String PREFIX = "NexageTestUser" + new UUIDGenerator().generateUniqueId();
  private String updateSellerSite,
      updateSellerPosition,
      updateSellerAdSource,
      updateSellerCampaign,
      updateSellerCreativePerCampaign;
  private String updateSellerBidderGroup, updateSellerBlockGroup, updateSellerLimitEnabled;
  private String updateSellerUsers, sitePid, actualPositionName;
  private int updateGlobalValue, updateSellerSiteValue, updateSellerPositionValue;
  private int updateSellerAdSourceValue, updateSellerCampaignValue;
  private int updateSellerCreativePerCampaignValue;
  private int updateSellerBidderGroupValue, updateSellerBlockGroupValue;
  private int updateSellerUsersValue, updateSellerLimitEnabledValue;

  SetUpWiremockStubs setupwm = new SetUpWiremockStubs();

  @When(
      "^the PSS user creates multiple sites from the json file \"(.+?)\" and set name in \"(.+?)\" and set status \"(.+?)\"$")
  public void the_pss_user_creates_more_active_sites(String filename, String name, String status)
      throws Throwable {

    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    siteChanges.put("name", name);
    siteChanges.put("status", status);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSitePssRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user creates multiple sites from the json file \"(.+?)\" and set name in \"(.+?)\" and set status \"(.+?)\"$")
  public void the_user_creates_more_active_sites(String filename, String name, String status)
      throws Throwable {

    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    siteChanges.put("name", name);
    siteChanges.put("status", status);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSiteRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^site \"(.+?)\" can be searched in the database$")
  public void site_can_be_searched_in_the_database(String siteName) throws Throwable {
    this.site = databaseUtils.getSiteByName(siteName);
    assertNotNull(this.site.getName(), "Site cannot be searched");
  }

  @When("^site \"(.+?)\" cannot be searched in the database$")
  public void site_cannot_be_searched_in_the_database(String siteName) throws Throwable {
    sitePid = databaseUtils.getSitePidByName(siteName);
    assertNull(sitePid, "Site can be searched");
  }

  @When("^the PSS user updates the site limit to \"(.+?)\"$")
  public void user_updates_site_limit(String siteName) throws Throwable {
    this.site = databaseUtils.getSiteByName(siteName);
    assertNull(this.site.getName(), "Site cannot be searched");
  }

  @Then("^the site limit field value is preserved in the db$")
  public void site_limit_preserved_in_the_DB() throws Throwable {
    String sellerSiteLimit =
        databaseUtils.getSellerSiteLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerSiteLimit.equals("11"),
        "sitelimit in the db is not preserved as 11 after update, for the company CRUDPositionTestUpdate");
  }

  @When("^the PSS user checks site limit and remaining item is \"(.+?)\"$")
  public void pss_user_check_site_limit(String numberOfItemsExpected) throws Throwable {

    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request = siteRequests.getGetSiteLimit().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (!commonSteps.serverResponse.equals(null)) {
      String numberofItemsActual = commonSteps.serverResponse.object().getString("remainingItems");
      assertTrue(numberOfItemsExpected.equals(numberofItemsActual), "Remaining items not equal");
    }
  }

  @When(
      "^read the site limit for the company with company pid \"(.{1,20})\" using the site with the site pid \"(.{1,20})\"$")
  public void read_sellers_site_limit(String companyPid, String sitePid) throws Throwable {
    requestMap =
        new RequestParams().setCompanyPid(companyPid).setSitePid(sitePid).getRequestParams();
    commonSteps.request = siteRequests.getGetSiteLimit().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the position limit field value is preserved in the db$")
  public void position_limit_preserved_in_the_DB() throws Throwable {
    String sellerPositionLimit =
        databaseUtils.getSellerPositionLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerPositionLimit.equals("11"),
        "sitelimit in the db is not preserved as 11 after update, for the company CRUDPositionTestUpdate");
  }

  @Then("^the tag limit field value for seller adserverSellerTest8 is preserved in the db$")
  public void tag_limit_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit = databaseUtils.getSellerTagLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("1"), "taglimit in the db is not preserved as 11 after update");
  }

  @Then("^the tag limit field value for seller CRUDPositionTest is preserved in the db$")
  public void tag_limit10210_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit = databaseUtils.getSellerTagLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("6"), "taglimit in the db is not preserved as 11 after update");
  }

  @Then("^the campaign limit field value for seller adserverSellerTest8 is preserved in the db$")
  public void campaign_limit_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit =
        databaseUtils.getSellerCampaignLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("12"),
        "campaign limit in the db is not preserved as 12 after update");
  }

  @Then("^the user limit field value for seller adserverSellerTest8 is preserved in the db$")
  public void one_central_user_limit_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit =
        databaseUtils.getSellerUserLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("3"), "user limit in the db is not preserved as 3 after update");
  }

  @Then("^the user limit field value for seller CRUDPositionTest is preserved in the db$")
  public void seller_CRUDPositionTest_one_central_user_limit_preserved_in_the_DB()
      throws Throwable {
    String sellerTagLimit =
        databaseUtils.getSellerUserLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("4"), "user limit in the db is not preserved as 4 after update");
  }

  @Then(
      "^the creative campaign limit field value for seller adserverSellerTest8 is preserved in the db$")
  public void creative_campaign_limit_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit =
        databaseUtils.getSellerCreativeCampaignLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("3"), "sitelimit in the db is not preserved as 3 after update");
  }

  @Then("^the biddergroup limit field value for seller adserverSellerTest8 is preserved in the db$")
  public void biddergroup_limit_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit =
        databaseUtils.getSellerBidderGroupLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("5"), "biddergroup in the db is not preserved as 5 after update");
  }

  @Then("^the blockgroup limit field value for seller adserverSellerTest8 is preserved in the db$")
  public void blockgroup_limit_preserved_in_the_DB() throws Throwable {
    String sellerTagLimit =
        databaseUtils.getSellerBlockGroupLimitValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerTagLimit.equals("5"), "blockgroup in the db is not preserved as 5 after update");
  }

  @Then("^the limit enabled flag value is preserved in the db$")
  public void limit_enabled_flag_preserved_in_the_db() throws Throwable {
    String sellerLimitEnabledFlag =
        databaseUtils.getSellerLimitFlagValue(commonSteps.getCompany().getPid());
    assertTrue(
        sellerLimitEnabledFlag.equals("0"),
        "sitelimit in the db is not preserved as 0 after update, for the company adserverSellerTest8");
  }

  @When(
      "^the PSS user creates multiple positions from the json file \"(.+?)\" and set name in \"(.+?)\"$")
  public void the_pss_user_creates_more_active_positions(String filename, String name)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    expectedPosition.put("name", name);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      actualPositionName = commonSteps.serverResponse.object().getString(JsonField.NAME);
      if (actualPositionName.equals("Position1_inactive_banner")) {
        int updateStatus = databaseUtils.updatePositionStatus();
        assertTrue(updateStatus > 0, "No records were updated");
      }
    }
  }

  @When(
      "^the user creates multiple positions from the json file \"(.+?)\" and set name in \"(.+?)\" and set status in \"(.+?)\"$")
  public void the_user_creates_more_active_positions(String filename, String name, String status)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    expectedPosition.put("name", name);
    expectedPosition.put("status", status);
    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      getActualPositionFromResponse();
    }
  }

  @When("^position \"(.+?)\" cannot be searched in the database$")
  public void position_cannot_be_searched_in_the_database(String positionName) throws Throwable {
    String positionPssPid = databaseUtils.getPositionPidByName(positionName);
    assertNull(positionPssPid, "position " + positionName + " exists in database.");
  }

  @When("^position \"(.+?)\" can be searched in the database$")
  public void position_can_be_searched_in_the_database(String positionName) throws Throwable {
    this.positionPss =
        databaseUtils.getPositionPssByName(positionName, commonSteps.getSite().getPid());
    assertNotNull(positionPss, "position " + positionName + " not exists in database.");
  }

  @When("^tag \"(.+?)\" can be searched in the database$")
  public void tag_can_be_searched_in_the_database(String tagName) throws Throwable {
    tag = databaseUtils.getTagPidByName(tagName);
    assertNotNull(tag, "Adsource cannot be searched");
  }

  @When("^tag \"(.+?)\" cannot be searched in the database$")
  public void tag_cannot_be_searched_in_the_database(String tagName) throws Throwable {
    tag = databaseUtils.getTagPidByName(tagName);
    assertNull(tag, "Adsource can be searched");
  }

  @When("^the PSS user checks bidder library limit and remaining item is \"(.+?)\"$")
  public void pss_user_check_bidder_library_limit(String numberOfItemsExpected) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests.getGetBidderLibrariesLimit().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (!commonSteps.serverResponse.equals(null)) {
      String numberofItemsActual = commonSteps.serverResponse.object().getString("remainingItems");
      assertTrue(numberOfItemsExpected.equals(numberofItemsActual), "Remaining items not equal");
    }
  }

  @When("^the PSS user checks block library limit and remaining item is \"(.+?)\"$")
  public void pss_user_check_block_library_limit(String numberOfItemsExpected) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests.getGetBlockLibrariesLimit().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (!commonSteps.serverResponse.equals(null)) {
      String numberofItemsActual = commonSteps.serverResponse.object().getString("remainingItems");
      assertTrue(numberOfItemsExpected.equals(numberofItemsActual), "Remaining items not equal");
    }
  }

  @When(
      "^the PSS user creates bidder RTB Profile Library from the json file \"(.+?)\" with name in \"(.+?)\" and privilege level in \"(.+?)\"$")
  public void the_user_creates_bidder_RTB_Profile_Library_from_json_file(
      String filename, String name, String privilegeLevel) throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    expectedLibrary.put("name", name);
    expectedLibrary.put("privilegeLevel", privilegeLevel);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests
            .getCreateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the PSS user creates block RTB Profile Library from the json file \"(.+?)\" with name in \"(.+?)\" and privilege level in \"(.+?)\"$")
  public void the_user_creates_block_RTB_Profile_Library_from_json_file(
      String filename, String name, String privilegeLevel) throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    expectedLibrary.put("name", name);
    expectedLibrary.put("privilegeLevel", privilegeLevel);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests
            .getCreateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^biddergroup \"(.+?)\" can be searched in the database$")
  public void biddergroup_can_be_searched_in_the_database(String biddergroupName) throws Throwable {
    bidderGroupName = databaseUtils.getRTBProfileLibraryPidByName(biddergroupName);
    assertTrue(!bidderGroupName.equals(null));
  }

  @When("^biddergroup \"(.+?)\" cannot be searched in the database$")
  public void biddergroup_cannot_be_searched_in_the_database(String biddergroupName)
      throws Throwable {
    bidderGroupName = databaseUtils.getRTBProfileLibraryPidByName(biddergroupName);
    assertNull(bidderGroupName, "BidderGroup can be searched");
  }

  @When("^blockgroup \"(.+?)\" can be searched in the database$")
  public void blockgroup_can_be_searched_in_the_database(String blockgroupName) throws Throwable {
    blockGroupName = databaseUtils.getRTBProfileLibraryPidByName(blockgroupName);
    assertTrue(!blockGroupName.equals(null));
  }

  @When("^blockgroup \"(.+?)\" cannot be searched in the database$")
  public void blockgroup_cannot_be_searched_in_the_database(String blockgroupName)
      throws Throwable {
    blockGroupName = databaseUtils.getRTBProfileLibraryPidByName(blockgroupName);
    assertNull(blockGroupName, "BlockGroup can be searched");
  }

  @When(
      "^the user creates multiple 1c users from the json file \"(.+?)\" with name in \"(.+?)\" and email in \"(.+?)\" and role in \"(.+?)\" and set status in \"(.+?)\"$")
  public void the_user_creates_a_user_from_the_json_file(
      String filename, String name, String email, String role, String status) throws Throwable {
    JSONObject newUser = JsonHandler.getJsonObjectFromFile(filename);
    String wmResponseBody = "";
    newUser.put("userName", name);
    newUser.put("email", email);
    newUser.put("role", role);
    newUser.put("enabled", status);
    newUser.put("email", PREFIX + newUser.getString("email"));
    newUser.put("userName", PREFIX + newUser.getString("userName"));
    String[] names = newUser.getString("name").split(" ");
    newUser.put("firstName", names[0]);
    if (names.length == 1) {
      newUser.put("lastName", "nexageTestLastName");
      newUser.put("name", newUser.get("firstName") + " " + newUser.get("lastName"));
      wmResponseBody =
          "{\"firstName\":\""
              + newUser.getString("firstName")
              + "\",\"lastName\":\""
              + newUser.getString("lastName")
              + "\",\"email\":\""
              + newUser.getString("email")
              + "\",\"username\":\""
              + newUser.getString("userName")
              + "\"}";
    } else {
      newUser.put("lastName", names[1]);
      wmResponseBody =
          "{\"firstName\":\""
              + names[0]
              + "\",\"lastName\":\""
              + names[1]
              + "\",\"email\":\""
              + newUser.getString("email")
              + "\",\"username\":\""
              + newUser.getString("userName")
              + "\"}";
    }
    user = TestUtils.mapper.readValue(newUser.toString(), User.class);
    String wmResponseBodyList =
        "{ \"list\": [{\"firstName\":\""
            + newUser.getString("firstName")
            + "\",\"lastName\":\""
            + newUser.getString("lastName")
            + "\",\"email\":\""
            + newUser.getString("email")
            + "\",\"username\":\""
            + newUser.getString("userName")
            + "\"}], \"totalCount\": 1}";
    setupwm.setUpWireMockFind1CUserByEmail(newUser.getString("email"), wmResponseBodyList);
    setupwm.setUpWireMockCreateUser(wmResponseBody);
    commonSteps.request = userDTORequests.getCreateUserRequest().setRequestPayload(newUser);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the non sso user creates a user from the json file \"(.+?)\" with name in \"(.+?)\" and email in \"(.+?)\" and role in \"(.+?)\" and set status in \"(.+?)\"$")
  public void the_non_sso_user_creates_a_user_from_the_json_file(
      String filename, String name, String email, String role, String status) throws Throwable {

    JSONObject newUser =
        JsonHandler.getJsonObjectFromFileWithTimestamp(filename, JsonField.USERNAME);
    newUser.put("userName", name);
    newUser.put("email", email);
    newUser.put("role", role);
    newUser.put("enabled", status);
    commonSteps.request = userDTORequests.getCreateUserRequest().setRequestPayload(newUser);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^users \"(.+?)\" can be searched in the database$")
  public void users_can_be_searched_in_the_database(String userName) throws Throwable {
    searchUserName = databaseUtils.getUsers(PREFIX + userName);
    assertNotNull(searchUserName, "User cannot be searched");
  }

  @When("^non sso users \"(.+?)\" can be searched in the database$")
  public void non_sso_users_can_be_searched_in_the_database(String userName) throws Throwable {
    searchUserName = databaseUtils.getUsers(userName);
    assertNotNull(searchUserName, "User cannot be searched");
  }

  @When("^users \"(.+?)\" cannot be searched in the database$")
  public void users_cannot_be_searched_in_the_database(String userName) throws Throwable {
    searchUserName = databaseUtils.getUsers(PREFIX + userName);
    assertNull(searchUserName, "User can be searched");
  }

  @When("^non sso users \"(.+?)\" cannot be searched in the database$")
  public void non_sso_users_cannot_be_searched_in_the_database(String userName) throws Throwable {
    searchUserName = databaseUtils.getUsers(userName);
    assertNull("User can be searched", searchUserName);
  }

  // update global_config
  @When("^the user updates limit of property \"(.+?)\" global config to \"(.+?)\"$")
  public void users_updates_limit_property_global_config(String propertyName, int value)
      throws Throwable {
    updateGlobalValue = databaseUtils.updateCountGlobalConfig(propertyName, value);
    assertTrue(updateGlobalValue > 0, "No records were updated");
  }

  // update seller_attributes
  @When("^the user updates seller \"(.+?)\" site limit to \"(.+?)\"$")
  public void users_updates_seller_site_config(String sellerName, int value) throws Throwable {
    updateSellerSite = databaseUtils.getSellerName(sellerName);
    updateSellerSiteValue = databaseUtils.updateSellerSiteCount(updateSellerSite, value);
    assertTrue(updateSellerSiteValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" position limit to \"(.+?)\"$")
  public void users_updates_seller_position_config(String sellerName, int value) throws Throwable {
    updateSellerPosition = databaseUtils.getSellerName(sellerName);
    updateSellerPositionValue =
        databaseUtils.updateSellerPositionCount(updateSellerPosition, value);
    assertTrue(updateSellerPositionValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" adsource limit to \"(.+?)\"$")
  public void users_updates_seller_adsource_config(String sellerName, int value) throws Throwable {
    updateSellerAdSource = databaseUtils.getSellerName(sellerName);
    updateSellerAdSourceValue =
        databaseUtils.updateSellerAdSourceCount(updateSellerAdSource, value);
    assertTrue(updateSellerAdSourceValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" campaign limit to \"(.+?)\"$")
  public void users_updates_seller_campaign_config(String sellerName, int value) throws Throwable {
    updateSellerCampaign = databaseUtils.getSellerName(sellerName);
    updateSellerCampaignValue =
        databaseUtils.updateSellerCampaignCount(updateSellerCampaign, value);
    assertTrue(updateSellerCampaignValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" creative per campaign limit to \"(.+?)\"$")
  public void users_updates_seller_creative_per_campaign_config(String sellerName, int value)
      throws Throwable {
    updateSellerCreativePerCampaign = databaseUtils.getSellerName(sellerName);
    updateSellerCreativePerCampaignValue =
        databaseUtils.updateSellerCreativePerCampaignCount(updateSellerCreativePerCampaign, value);
    assertTrue(updateSellerCreativePerCampaignValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" bidderGroups limit to \"(.+?)\"$")
  public void users_updates_seller_bidder_groups_config(String sellerName, int value)
      throws Throwable {
    updateSellerBidderGroup = databaseUtils.getSellerName(sellerName);
    updateSellerBidderGroupValue =
        databaseUtils.updateSellerBidderGroupsCount(updateSellerBidderGroup, value);
    assertTrue(updateSellerBidderGroupValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" blockGroups limit to \"(.+?)\"$")
  public void users_updates_seller_block_groups_config(String sellerName, int value)
      throws Throwable {
    updateSellerBlockGroup = databaseUtils.getSellerName(sellerName);
    updateSellerBlockGroupValue =
        databaseUtils.updateSellerBlockGroupsCount(updateSellerBlockGroup, value);
    assertTrue(updateSellerBlockGroupValue > 0, "No records were updated");
  }

  @When("^the user updates seller \"(.+?)\" users limit to \"(.+?)\"$")
  public void users_updates_seller_users_config(String sellerName, int value) throws Throwable {
    updateSellerUsers = databaseUtils.getSellerName(sellerName);
    updateSellerUsersValue = databaseUtils.updateSellerUsersCount(updateSellerUsers, value);
    assertTrue(updateSellerUsersValue > 0, "No records were updated");
  }

  // update seller attribute limit_enable = false
  @When("^the user updates seller \"(.+?)\" limit_enabled equals \"(.+?)\"$")
  public void users_updates_limit_enabled_flag(String sellerName, int value) throws Throwable {
    updateSellerLimitEnabled = databaseUtils.getSellerName(sellerName);
    updateSellerLimitEnabledValue =
        databaseUtils.updateSellerLimitEnabledFlag(updateSellerLimitEnabled, value);
    assertTrue(updateSellerLimitEnabledValue > 0, "No records were updated");
  }

  private void getActualPositionFromResponse() throws Throwable {
    String positionName = expectedPosition.getString(JsonField.NAME);
    JSONArray positions = commonSteps.serverResponse.object().getJSONArray(JsonField.POSITIONS);
    for (int i = 0; i < positions.length(); i++) {
      if (positions.getJSONObject(i).getString(JsonField.NAME).equals(positionName)) {
        actualPosition = positions.getJSONObject(i);
        break;
      }
    }
    assertNotNull(actualPosition, "Position is not returned");
  }
}
