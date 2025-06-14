package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SiteRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class SiteSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SiteRequests siteRequests;
  @Autowired public DatabaseUtils databaseUtils;

  private JSONObject siteChanges;
  private JSONArray sitesToUpdateDealTerms;
  private long siteAliasId;
  private long sitePubAliasId;
  private String txIdSiteUpdate;
  private Map<String, String> requestMap;
  private Map<String, Site> pssSellerSitesMap;

  @When("^the site data is retrieved$")
  public void the_site_data_is_retrieved() throws Throwable {
    requestMap = new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request = siteRequests.getGetSiteRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the site data for provision API is retrieved$")
  public void the_site_data_provision_is_retrieved() throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .getRequestParams();
    commonSteps.request = siteRequests.getGetSiteRequestProvision().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the site for provision is deleted$")
  public void the_site_provision_is_deleted() throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .getRequestParams();
    commonSteps.request = siteRequests.getDeleteSiteProvisionRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates a site using provision API from the json file \"(.+?)\"$")
  public void create_site_with_provision_api(String filename) throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setCompanyId(commonSteps.getCompany().getId()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSiteProvisionRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates a site from the json file \"(.+?)\"$")
  public void the_user_creates_a_site_from_the_json_file(String filename) throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSiteRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates a site without timestamp from the json file \"(.+?)\"$")
  public void the_user_creates_a_site_without_timestampfrom_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSiteRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user creates a site from the json file \"(.+?)\"$")
  public void the_PSS_user_creates_a_site_from_the_json_file(String filename) throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSitePssRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user creates a site with detail from the json file \"(.+?)\"$")
  public void the_PSS_user_creates_a_site_with_detail_from_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSitePssWithDetailRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user creates a site without detail from the json file \"(.+?)\"$")
  public void the_PSS_user_creates_a_site_without_detail_from_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSitePssWithoutDetailRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user creates a site without timestamp from the json file \"(.+?)\"$")
  public void the_PSS_user_creates_a_site_without_timestampfrom_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getCreateSitePssRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates site with data from the json file \"(.+?)\"$")
  public void the_user_updates_site_with_data_from_the_json_file(String filename) throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap = new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSiteRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates site provision with data from the json file \"(.+?)\"$")
  public void update_site_provision_from_json_file(String filename) throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSiteRequestProvision()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates site second call with data from the json file \"(.+?)\"$")
  public void the_user_updates_site_second_call_with_data_from_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    String txId = commonSteps.serverResponse.object().getString(JsonField.TX_ID);
    requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setTxId(txId)
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSiteSecondRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user updates site with data from the json file \"(.+?)\"$")
  public void the_PSS_user_updates_site_with_data_from_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    assertNotNull(commonSteps.serverResponse, "Server response is null");
    txIdSiteUpdate = commonSteps.serverResponse.toObject().getString(JsonField.TX_ID);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setTxId(txIdSiteUpdate)
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSitePssRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user gets site update info from the json file \"(.+?)\"$")
  public void the_PSS_user_get_site_update_info(String filename) throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getSiteUpdateInfo()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user get site update info from the json file \"(.+?)\" with details \"(.+?)\"$")
  public void the_PSS_user_gets_site_update_info_with_details(String filename, String detail)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setDetail(detail)
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getSiteUpdateInfoDetails()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user updates site with detail and data from the json file \"(.+?)\"$")
  public void the_PSS_user_updates_site_with_detail_and_data_from_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    assertNotNull(commonSteps.serverResponse, "Server response is null");
    txIdSiteUpdate = commonSteps.serverResponse.toObject().getString(JsonField.TX_ID);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setTxId(txIdSiteUpdate)
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSitePssWithDetailRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user updates site without detail and data from the json file \"(.+?)\"$")
  public void the_PSS_user_updates_site_without_detail_and_data_from_the_json_file(String filename)
      throws Throwable {
    siteChanges = JsonHandler.getJsonObjectFromFile(filename);
    assertNotNull(commonSteps.serverResponse, "Server response is null");
    txIdSiteUpdate = commonSteps.serverResponse.toObject().getString(JsonField.TX_ID);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setTxId(txIdSiteUpdate)
            .getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSitePssWithoutDetailRequest()
            .setRequestPayload(siteChanges)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates a site \"(.+?)\" by adding a default rtbProfile from the tag \"(.+?)\"$")
  public void the_user_updates_a_site_add_rtb(String siteName, String tagName) throws Throwable {
    String sitePid = databaseUtils.getSitePidByName(siteName);
    String tagPid = databaseUtils.getTagPidByName(tagName);
    String rtbPid = databaseUtils.getExchangePidByTagPid(tagPid);
    assertTrue(databaseUtils.updateSiteRtb(rtbPid, sitePid) > 0, "Site DRP was not updated");
  }

  @When("^the Seller sites for PSS user are retrieved$")
  public void the_Seller_sites_for_PSS_user_are_retrieved() throws Throwable {
    retrievePssSellerSites();
  }

  @When("^the user selects the site to audit$")
  public void the_user_selects_the_site_to_audit() throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request = siteRequests.getSiteAuditRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the user select a specific revision for site \"(.+?)\"$")
  public void the_user_select_a_specific_revision_for_site(String revision) throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setRevisionNumber(revision)
            .getRequestParams();
    commonSteps.request =
        siteRequests.getGetSiteAuditForRevisionRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user selects the site \"(.+?)\"$")
  public void the_PSS_user_selects_the_site(String siteName) throws Throwable {
    commonSteps.setSite(getPssSellerSite(siteName));
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request = siteRequests.getGetSitePssRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects a site with pid \"(.+?)\"")
  public void the_user_selects_the_site_with_pid(String pid) throws Throwable {
    String sitePid = String.valueOf(pid);
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(sitePid)
            .getRequestParams();
    commonSteps.request = siteRequests.getGetSitePssRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user reads data for the site named \"(.+?)\" with the site pid \"(.{1,20})\"$")
  public void read_sellers_site(String siteName, String sitePid) throws Throwable {
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(sitePid)
            .getRequestParams();
    commonSteps.request = siteRequests.getGetSitePssRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user select the site \"(.+?)\" with detail \"(.+?)\"$")
  public void the_PSS_user_selects_the_site_with_detail(String siteName, String detail)
      throws Throwable {
    commonSteps.setSite(getPssSellerSite(siteName));
    requestMap =
        new RequestParams()
            .setSellerPid(commonSteps.getCompany().getPid())
            .setSitePid(commonSteps.getSite().getPid())
            .setDetail(detail)
            .getRequestParams();
    commonSteps.request =
        siteRequests.getGetSitePssWithDetailRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^all site summaries are retrieved$")
  public void all_site_summaries_are_retrieved() throws Throwable {
    commonSteps.request = siteRequests.getGetAllSiteSummariesRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes site \"(.+?)\"")
  public void the_user_deletes_site(String site) throws Throwable {
    commonSteps.setSite(site);
    requestMap = new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request = siteRequests.getDeleteSiteRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for deleted site$")
  public void the_user_searches_for_deleted_site() throws Throwable {
    requestMap = new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request = siteRequests.getGetSiteRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for sites and sellers by prefix \"(.+?)\"$")
  public void the_user_searches_for_sites_and_sellers_by_prefix(String prefix) throws Throwable {
    requestMap = new RequestParams().setPrefix(prefix).getRequestParams();
    commonSteps.request = siteRequests.getGetSiteByPrefixRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the site \"(.+?)\" alias name was removed$")
  public void the_site_alias_name_was_removed(String siteName) throws Throwable {
    commonSteps.setSite(siteName);
    Site site = databaseUtils.getSiteSummaryByPid(commonSteps.getSite().getPid());
    assertNull("Alias was not removed.", site.getGlobalAliasName());
  }

  @Then("^siteAliasId is retrieved for site \"(.+?)\"$")
  public void site_alias_id_is_retrieved(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    assertNotNull(databaseUtils.getSiteAliasId(site_pid), "siteAliasId is null");
  }

  @Then("^siteAliasId is removed from site \"(.+?)\"$")
  public void site_alias_id_is_removed(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    assertNull(databaseUtils.getSiteAliasId(site_pid), "siteAliasId is not null");
  }

  @Then("^pubAliasId is removed from site \"(.+?)\"$")
  public void pub_alias_id_is_removed(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    assertNull(databaseUtils.getPubAliasIdForSite(site_pid), "siteAliasId is not null");
  }

  @Then("^pubAliasId is retrieved for site \"(.+?)\"$")
  public void pub_alias_id_is_retrieved_for_site(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    sitePubAliasId = databaseUtils.getPubAliasIdForSite(site_pid);
  }

  @Then("^siteAliasId was not generated for site \"(.+?)\"$")
  public void site_alias_id_is_not_generated(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    assertNull(databaseUtils.getSiteAliasId(site_pid), "siteAliasId is not null");
  }

  @Then("^pubAliasId was not generated for site \"(.+?)\"$")
  public void pub_alias_id_is_not_generated_for_site(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    assertNull(databaseUtils.getPubAliasIdForSite(site_pid), "pubAliasId for site is not null");
  }

  @Then("^siteAliasId was regenerated for site \"(.+?)\"$")
  public void site_alias_id_is_regenerated(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    long newSiteAliasId = databaseUtils.getSiteAliasId(site_pid);
    assertTrue(newSiteAliasId != siteAliasId, "newSiteAliasId is the same as siteAliasId");
  }

  @Then("^site name \"(.+?)\" is retrieved for dcn \"(.+?)\"$")
  public void site_name_is_retrieved_for_dcn(String expSiteName, String dcn) throws Throwable {
    String actualSiteName = databaseUtils.getSiteNameByDcn(dcn);
    assertEquals(expSiteName, actualSiteName, "Site name for the specified dcn is not correct");
  }

  @Then("^dcn is retrieved for site name \"(.+?)\"$")
  public void dcn_is_retrieved_for_site(String siteName) throws Throwable {
    assertNotNull(databaseUtils.getDcnByName(siteName), "dcn for specified site name is null");
  }

  @Then("^status of the site with the site name \"(.+?)\" is \"(.+?)\"$")
  public void status_by_site_name(String siteName, String expStatus) throws Throwable {
    String actualStatus = databaseUtils.getStatusBySiteName(siteName);
    assertEquals(
        expStatus, actualStatus, "Site status for the site name " + siteName + " is not correct");
  }

  @Then("^site with the site name \"(.+?)\" is not generated$")
  public void dcn_is_not_generated_for_site(String siteName) throws Throwable {
    assertNull(databaseUtils.getDcnByName(siteName), "dcn for specified site name is null");
  }

  @Then("^dcn was not generated for site name \"(.+?)\"$")
  public void dcn_was_not_generated_for_site(String siteName) throws Throwable {
    assertNull(databaseUtils.getDcnByName(siteName), "dcn for specified site name is null");
  }

  @Then("^site was not generated for dcn \"(.+?)\"$")
  public void site_is_not_generated_for_dcn(String dcn) throws Throwable {
    String siteName = databaseUtils.getSiteNameByDcn(dcn);
    assertNull(siteName, "site name for specified dcn is not null");
  }

  @Then("^there is only \"(.+?)\" site with specified dcn \"(.+?)\"$")
  public void site_is_not_generated_for_dcn(String count, String dcn) throws Throwable {
    int expectedSiteRecordDcn = Integer.valueOf(count);
    int actualCount = Integer.valueOf(databaseUtils.countSiteByDcn(dcn));
    assertEquals(
        expectedSiteRecordDcn,
        actualCount,
        "Count of sites for dcn " + dcn + " does not match expected value");
  }

  @Then("^there is only \"(.+?)\" site with specified name \"(.+?)\"$")
  public void site_is_not_generated_for_name(String count, String name) throws Throwable {
    int expectedSiteRecordDcn = Integer.valueOf(count);
    int actualCount = Integer.valueOf(databaseUtils.countSiteByName(name));
    assertEquals(
        expectedSiteRecordDcn,
        actualCount,
        "Count of sites for name " + name + " does not match expected value");
  }

  @Then("^pubAliasId was regenerated for site \"(.+?)\"$")
  public void pub_alias_id_is_regenerated(String site_name) throws Throwable {
    String site_pid = databaseUtils.getSitePidByName(site_name);
    long newSitePubAliasId = databaseUtils.getPubAliasIdForSite(site_pid);
    assertTrue(
        newSitePubAliasId != sitePubAliasId, "newSitePubAliasId is the same as sitePubAliasId");
  }

  @Then("^default site data is set$")
  public void default_site_data_is_set() throws Throwable {
    Site site = databaseUtils.getSiteByName(siteChanges.get(JsonField.NAME).toString());
    assertFalse(site.isAdScreening(), "site adscreening property was set to true");
    assertEquals("1", site.getStatus(), "site status was not set to Active");
  }

  @Given("^the seller pid is empty$")
  public void the_seller_pid_is_empty() throws Throwable {
    commonSteps.getCompany().setPid("");
  }

  @When("^the user gets all the site deal terms$")
  public void the_user_gets_all_the_site_deal_terms() throws Throwable {
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request = siteRequests.getGetSiteDealTermsRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the sites with default deal term from the json file \"(.+?)\"$")
  public void the_user_updates_the_sites_with_default_deal_term_from_the_json_file(String filename)
      throws Throwable {
    sitesToUpdateDealTerms = JsonHandler.getJsonArrayFromFile(filename);
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        siteRequests
            .getUpdateSiteDealTermsRequest()
            .setRequestPayload(sitesToUpdateDealTerms)
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the nexage rev share in the database for the site \"(.+?)\" equals to \"(.+?)\"$")
  public void the_nexage_rev_share_in_the_database_for_the_sitepid(
      String siteName, String expectedRevShare) throws Throwable {
    String sitePid = databaseUtils.getSitePidByName(siteName);
    String actualRevShare = databaseUtils.getNexageRevShareFromSitePid(sitePid);
    assertTrue(actualRevShare.equals(expectedRevShare), "rev share of the site is not correct");
  }

  @Then("^the rtb fee in the database for the site \"(.+?)\" equals to \"(.+?)\"$")
  public void the_rtb_fee_for_the_sitePid(String siteName, String expectedRtbFee) throws Throwable {
    String sitePid = databaseUtils.getSitePidByName(siteName);
    String actualRtbFee = databaseUtils.getRtbFeeBySitePid(sitePid);
    assertTrue(actualRtbFee.equals(expectedRtbFee), "rtb fee");
  }

  @Then("^the deal terms for specified site \"(.+?)\" equals to \"(.+?)\"$")
  public void the_deal_terms_generated_correctly(String siteName, String expectedDealTermCount)
      throws Throwable {
    String sitePid = databaseUtils.getSitePidByName(siteName);
    String actualDealTermCount = databaseUtils.countDealTermsBySitePid(sitePid);
    assertEquals(
        expectedDealTermCount,
        actualDealTermCount,
        "Count of sites for name " + siteName + " does not match expected value");
  }

  private void retrievePssSellerSites() throws Throwable {
    requestMap =
        new RequestParams().setSellerPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request = siteRequests.getGetAllSitesPssRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      pssSellerSitesMap = commonFunctions.getSites(commonSteps.serverResponse);
      assertTrue(pssSellerSitesMap.size() > 0, "Seller sites for PSS user are not found");
    }
  }

  private Site getPssSellerSite(String siteName) throws Throwable {
    retrievePssSellerSites();
    assertNotNull(pssSellerSitesMap, "Failed to get pss seller sites");

    return pssSellerSitesMap.get(siteName);
  }

  @When("^set site \"(.+?)\"$")
  public void setSiteForProvisionApi(String siteName) throws Throwable {
    commonSteps.setSite(databaseUtils.getSiteByName(siteName));
  }
}
