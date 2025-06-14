package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.RtbProfile;
import com.nexage.geneva.model.crud.Tag;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.TagRequests;
import com.nexage.geneva.request.TagsRequest;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.ErrorHandler;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.UUIDGenerator;
import com.nexage.geneva.util.geneva.JsonField;
import com.nexage.geneva.util.geneva.TagType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

public class TagSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private TagRequests tagRequests;
  @Autowired private TagsRequest tagsRequest;
  @Autowired private DatabaseUtils databaseUtils;
  @Autowired private AdSourceDefaultsSteps adsourcedefaultsSteps;

  private Tag tag;
  private TagType tagType;
  public String txId;

  private JSONArray actualSiteTags;
  private JSONObject newTag, updateTag, tagSummaries;
  private String newTagName, updateTagName;
  private String selectedSitePid, selectedPositionPid, selectedTagPid;
  private Request currentRequest;
  private Long tagPubAliasId;
  private Long tagSiteAliasId;
  private String selectedTargetSitePid, selectedTargetPositionPid;
  private String actualTagStatus;

  private static String tagPrimaryId, includeSiteName, rtbPid;
  private static final String anyPublisher = "0000",
      anySite = "0000",
      anyPosition = "0000",
      anyTag = "0000",
      nexageUserType = "NEXAGE",
      pssUserType = "SELLER";

  private RtbProfile rtbProfile;
  public JSONArray adnetResp2;
  public JSONResource adnetResp;
  public String AdNetAdsourcePID;
  private static String PREFIX = "DSName-" + new UUIDGenerator().generateUniqueId();

  @When("^the user creates the \"(.+?)\" tag from the json file \"(.+?)\"$")
  public void the_user_creates_the_tag_from_the_json_file(String tagTypeName, String filename)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    prepareForCreatingTagFromFile(tagTypeName, filename);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates provision tag from the json file \"(.+?)\"$")
  public void create_tag_provision_api(String filename) throws Throwable {
    newTag = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests
            .getCreateProvisionTagRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates provision tag from the json file \"(.+?)\"$")
  public void update_tag_provision_api(String filename) throws Throwable {
    newTag = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTagPid(commonSteps.encodedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests
            .getUpdateProvisionTagRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^tag for provision API is deleted$")
  public void delete_tag_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTagPid(commonSteps.encodedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests
            .getDeleteProvisionTagRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^status of the tag with the tag name \"(.+?)\" is \"(.+?)\"$")
  public void check_status_by_tag_name(String tagName, String expTagStatus) throws Throwable {
    String actualStatus = databaseUtils.getStatusByTagName(tagName);
    assertEquals(
        "Tag status for the tag name " + tagName + " is not correct", expTagStatus, actualStatus);
  }

  @When("^the tag data for provision API is retrieved$")
  public void get_tag_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTagPid(commonSteps.encodedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests
            .getGetProvisionTagRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^\"(created|updated)\" tag data matches the following json file \"(.+?)\"$")
  public void tag_data_matches_the_following_json_file(String type, String filename)
      throws Throwable {
    String tagName = "created".equals(type) ? newTagName : updateTagName;
    String expectedJson = TestUtils.getResourceAsString(filename);
    JSONArray siteTags = (JSONArray) commonSteps.serverResponse.toObject().get(JsonField.TAGS);
    JSONObject modifiedTag = getModifiedTag(siteTags, tagName);
    if (tagType == TagType.EXCHANGE) {
      modifiedTag = buildModifiedTagWithRtbProfile(modifiedTag);
    }
    ResponseHandler.matchResponseWithExpectedResult(
        currentRequest, expectedJson, modifiedTag, type + " tag");
  }

  @When("^the user updates the \"(.+?)\" tag \"(.+?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_the_tag_from_the_json_file(
      String tagTypeName, String tagName, String filename) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setTagPid(getSelectedTagPid(tagName)).getRequestParams();
    prepareForUpdatingTagFromFile(tagTypeName, filename);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(updateTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the \"(.+?)\" tag \"(.+?)\"$")
  public void the_user_deletes_selected_tag(String tagTypeName, String tagName) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setTagPid(getSelectedTagPid(tagName))
            .getRequestParams();
    prepareForDeletingTag(tagTypeName);
    commonSteps.request = currentRequest.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^deal term for a tag \"(.+?)\" has \"(.+?)\" correct revenue share$")
  public void deal_term_for_tag_has_correct_revshare(String tagName, String expectedRevShare)
      throws Throwable {
    String tagPid = databaseUtils.getTagPidByName(tagName);
    String actualRevenueShare =
        databaseUtils.getLatestDealTermsFromTagPid(tagPid).getNexage_rev_share();
    assertEquals(
        expectedRevShare, actualRevenueShare, "Actual rev share does not match expected value");
  }

  @When("^deal term for a tag \"(.+?)\" has \"(.+?)\" correct rtb fee$")
  public void deal_term_for_tag_has_correct_rtbfee(String tagName, String expectedRtbFee)
      throws Throwable {
    String tagPid = databaseUtils.getTagPidByName(tagName);
    String actualRtbFee = databaseUtils.getLatestDealTermsFromTagPid(tagPid).getRtb_fee();
    assertEquals(expectedRtbFee, actualRtbFee, "Actual rtb fee does not match expected value");
  }

  @When("^revenue share for a tag \"(.+?)\" has null value$")
  public void revshare_has_null_value(String tagName) throws Throwable {
    String tagPid = databaseUtils.getTagPidByName(tagName);
    assertNull(databaseUtils.getLatestDealTermsFromTagPid(tagPid).getNexage_rev_share());
  }

  @When("^rtb fee for a tag \"(.+?)\" has null value$")
  public void rtbfee_has_null_value(String tagName) throws Throwable {
    String tagPid = databaseUtils.getTagPidByName(tagName);
    assertNull(databaseUtils.getLatestDealTermsFromTagPid(tagPid).getRtb_fee());
  }

  @Then("^the deal terms for specified tag \"(.+?)\" equals to \"(.+?)\"$")
  public void the_deal_terms_for_tag_generated_correctly(
      String tagName, String expectedDealTermCount) throws Throwable {
    String tagPid = databaseUtils.getTagPidByName(tagName);
    String actualDealTermCount = databaseUtils.countDealTermsBySitePid(tagPid);
    assertEquals(
        expectedDealTermCount,
        actualDealTermCount,
        "Count of deal terms for tag name " + tagName + " does not match expected value");
  }

  @Given("^the tag summaries are retrieved$")
  public void the_tag_summaries_are_retrieved() throws Throwable {
    retrieveTagSummaries();
  }

  @Given("^the bidder performance summaries are retrieved$")
  public void the_bidder_performance_summaries_are_retrieved() throws Throwable {
    retrieveBidderPerformanceSummaries();
  }

  @When("^the user selects the site \"([^\"]+)\" and the position \"([^\"]+)\"$")
  public void the_user_selects_the_site_and_the_position(String siteName, String positionName)
      throws Throwable {
    JSONArray sites = getSites();
    JSONObject selectedSite = getSelectedSite(siteName, sites);
    getSelectedPosition(positionName, selectedSite);
  }

  @When("^the user sets the site \"([^\"]+)\" pid and the position \"([^\"]+)\" pid$")
  public void the_user_sets_site_pid_and_the_position_pid(String siteName, String positionName)
      throws Throwable {
    selectedSitePid = databaseUtils.getSitePidByName(siteName);
    assertNotNull(selectedSitePid, "Site pid for tag is null");
    selectedPositionPid = databaseUtils.getPositionPidByName(positionName);
    assertNotNull(selectedPositionPid, "Position pid for tag is null");
  }

  @When("^the user sets the tag \"([^\"]+)\" pid$")
  public void the_user_sets_tag_pid(String tagName) throws Throwable {
    selectedTagPid = databaseUtils.getTagPidByName(tagName);
    assertNotNull(selectedTagPid, "Position pid for tag is null");
  }

  @Then("^publisher tags are retrieved$")
  public void publisher_tags_are_retrieved() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetPublisherTagsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^rtb profile owner company pid for tag \"(.+?)\" is null$")
  public void rtb_profile_owner_company_pid_null_for_rtb_tag(String tagName) throws Throwable {
    assertNull(
        databaseUtils.getRtbOwnerCompanyPidByTagName(tagName),
        "default rtb profile owner company pid is not null");
  }

  @Then("^siteAliasId is retrieved for tag \"(.+?)\"$")
  public void site_alias_id_is_retrieved_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    tagSiteAliasId = databaseUtils.getSiteAliasIdForTag(tag_pid);
    assertNotNull(tagSiteAliasId, "siteAliasId for tag is null");
  }

  @Then("^siteAliasId is removed from tag \"(.+?)\"$")
  public void site_alias_id_is_removed_from_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    assertNull(databaseUtils.getSiteAliasIdForTag(tag_pid), "siteAliasId for tag is not null");
  }

  @Then("^pubAliasId is removed from tag \"(.+?)\"$")
  public void pub_alias_id_is_removed(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    assertNull(databaseUtils.getPubAliasIdForTag(tag_pid), "pubAliasId for tag is not null");
  }

  @Then("^pubAliasId is retrieved for tag \"(.+?)\"$")
  public void pub_alias_id_is_retrieved_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    tagPubAliasId = databaseUtils.getPubAliasIdForTag(tag_pid);
    assertNotNull(tagPubAliasId, "pubAliasId for tag is null");
  }

  @Then("^siteAliasId was not generated for tag \"(.+?)\"$")
  public void site_alias_id_was_not_generated_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    assertNull(databaseUtils.getSiteAliasIdForTag(tag_pid), "siteAliasId for tag is not null");
  }

  @Then("^pubAliasId was not generated for tag \"(.+?)\"$")
  public void pub_alias_id_is_not_generated_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    assertNull(databaseUtils.getPubAliasIdForTag(tag_pid), "pubAliasId for tag is not null");
  }

  @Then("^siteAliasId was regenerated for tag \"(.+?)\"$")
  public void site_alias_id_is_regenerated_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    long newSiteAliasIdForTag = databaseUtils.getSiteAliasIdForTag(tag_pid);
    assertTrue(
        newSiteAliasIdForTag != tagSiteAliasId,
        "newSiteAliasIdForTag is the same as siteAliasIdForTag");
  }

  @Then("^siteAliasId was not regenerated for tag \"(.+?)\"$")
  public void site_alias_id_is_not_regenerated_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    long newSiteAliasIdForTag = databaseUtils.getSiteAliasIdForTag(tag_pid);
    assertTrue(
        newSiteAliasIdForTag == tagSiteAliasId,
        "newSiteAliasIdForTag is not the same as siteAliasIdForTag");
  }

  @Then("^pubAliasId was regenerated for tag \"(.+?)\"$")
  public void pub_alias_id_is_regenerated_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    long newPubAliasIdForTag = databaseUtils.getPubAliasIdForTag(tag_pid);
    assertTrue(
        newPubAliasIdForTag != tagPubAliasId,
        "newPubAliasIdForTag is the same as pubAliasIdForTag");
  }

  @Then("^pubAliasId was not regenerated for tag \"(.+?)\"$")
  public void pub_alias_id_is_not_regenerated_for_tag(String tag_name) throws Throwable {
    String tag_pid = databaseUtils.getTagPidByName(tag_name);
    long newPubAliasIdForTag = databaseUtils.getPubAliasIdForTag(tag_pid);
    assertTrue(
        newPubAliasIdForTag == tagPubAliasId,
        "newPubAliasIdForTag is not the same as pubAliasIdForTag");
  }

  @When(
      "^the user selects the site \"([^\"]*)\" and the position \"([^\"]*)\" and the tag \"([^\"]*)\"$")
  public void the_user_selects_the_site_and_the_position_and_the_tag(
      String siteName, String positionName, String tagName) throws Throwable {
    JSONArray sites = getSites();
    JSONObject selectedSite = getSelectedSite(siteName, sites);
    JSONObject selectedPosition = getSelectedPosition(positionName, selectedSite);
    getSelectedTag(tagName, selectedPosition);
  }

  @When(
      "^the user selects the site \"([^\"]*)\" and the position \"([^\"]*)\" and the non-existing tag \"([^\"]*)\"$")
  public void the_user_selects_the_site_and_the_position_and_the_non_existing_tag(
      String siteName, String positionName, String tagName) throws Throwable {
    JSONArray sites = getSites();
    JSONObject selectedSite = getSelectedSite(siteName, sites);
    getSelectedPosition(positionName, selectedSite);
    selectedTagPid = tagName;
  }

  @When(
      "^the user selects the site \"([^\"]*)\" and the position \"([^\"]*)\" and an archived tag \"([^\"]*)\"$")
  public void the_user_selects_the_site_and_the_position_and_archived_tag(
      String siteName, String positionName, String tagPid) throws Throwable {
    JSONArray sites = getSites();
    JSONObject selectedSite = getSelectedSite(siteName, sites);
    getSelectedPosition(positionName, selectedSite);
    selectedTagPid = tagPid;
  }

  @When("^the user selects the target site \"([^\"]*)\" and the target position \"([^\"]*)\"$")
  public void the_user_selects_the_target_site_and_the_target_position(
      String sitePid, String positionPid) throws Throwable {
    // TODO: Write code here that turns the phrase above into concrete actions
    selectedTargetSitePid = sitePid;
    selectedTargetPositionPid = positionPid;
  }

  @Then("^publisher tag is retrieved$")
  public void publisher_tag_is_retrieved() throws Throwable {
    executeGetPublisherTagRequest();
  }

  @Then("^publisher tag is not retrieved$")
  public void publisher_tag_is_not_retrieved() throws Throwable {
    try {
      executeGetPublisherTagRequest();
      fail("Enexpected tag retrived");
    } catch (AssertionError e) {

    }
  }

  @When("^the user creates a publisher \"(.+?)\" tag from the json file \"(.+?)\"$")
  public void the_user_creates_a_publisher_tag_from_the_json_file(
      String tagTypeName, String filename) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .getRequestParams();
    prepareForCreatingPublisherTagFromFile(tagTypeName, filename);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user creates multiple publisher \"(.+?)\" tags using multiTags call from the json file \"(.+?)\"$")
  public void the_user_creates_multiple_publisher_tags_without_name_from_the_json_file(
      String tagTypeName, String filename) throws Throwable {
    JSONArray newMultipleTag = JsonHandler.getJsonArrayFromFile(filename);

    for (int i = 0; i < newMultipleTag.length(); i++) {
      JSONObject itemArr = new JSONObject(newMultipleTag.get(i).toString());
      if (itemArr.has("name") && !itemArr.isNull("name")) {
        itemArr.put("primaryId", PREFIX);
        itemArr.put("primaryName", PREFIX);
        itemArr.put("secondaryId", PREFIX);
        itemArr.put("secondaryName", PREFIX);
        itemArr.put("secondaryName", PREFIX);
        newMultipleTag.put(i, itemArr);
        PREFIX += 1;
      } else {
        itemArr.put("name", PREFIX);
        itemArr.put("primaryId", PREFIX);
        itemArr.put("primaryName", PREFIX);
        itemArr.put("secondaryId", PREFIX);
        itemArr.put("secondaryName", PREFIX);
        itemArr.put("secondaryName", PREFIX);
        newMultipleTag.put(i, itemArr);
        PREFIX += 1;
      }
    }

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .getRequestParams();
    prepareForCreatingMultiplePublisherTagsFromFile(tagTypeName);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newMultipleTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates multiple \"(.+?)\" tags from the json file \"(.+?)\"$")
  public void the_user_creates_multiple_tags_from_the_json_file(String tagTypeName, String filename)
      throws Throwable {
    JSONArray multiTags = JsonHandler.getJsonArrayFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    prepareForCreatingMultiplePublisherTagsFromFile(tagTypeName);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(multiTags);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates multiple publisher \"(.+?)\" tags using multiTags call from the json file \"(.+?)\"$")
  public void the_user_updates_multiple_publisher_tags_without_name_from_the_json_file(
      String tagTypeName, String filename) throws Throwable {
    JSONArray newMultipleTag = JsonHandler.getJsonArrayFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .getRequestParams();
    prepareForCreatingMultiplePublisherTagsFromFile(tagTypeName);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newMultipleTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^tag created with correct values$")
  public void verify_created_tag() throws Throwable {
    verify_tag_with_request();
  }

  @When("^the user updates the publisher \"(.+?)\" tag from the json file \"(.+?)\"$")
  public void the_user_updates_the_publisher_tag_from_the_json_file(
      String tagTypeName, String filename) throws Throwable {
    prepareForUpdatingPublisherTagFromFile(tagTypeName, filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .setTagPid((selectedTagPid))
            .getRequestParams();
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(updateTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to search the publisher tag$")
  public void the_user_tries_to_search_publisher_tag() throws Throwable {
    executeGetPublisherTagRequest();
  }

  @When("^the user calls forbidden create publisher tag service$")
  public void the_user_calls_forbidden_create_publisher_tag_service() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(anyPublisher)
            .setSitePid(anySite)
            .setPositionPid(anyPosition)
            .getRequestParams();
    commonSteps.request =
        tagRequests
            .getCreatePublisherMedTagRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(new JSONObject());
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user calls forbidden get tag summaries service$")
  public void the_user_calls_forbidden_get_tag_summaries_service() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(anyPublisher)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetTagSummariesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user calls forbidden get publisher tag service$")
  public void the_user_calls_forbidden_get_publisher_tag_service() throws Throwable {
    commonSteps.companyPid = anyPublisher;
    selectedSitePid = anySite;
    selectedPositionPid = anyPosition;
    selectedTagPid = anyTag;
    executeGetPublisherTagRequest();
  }

  @When("^the PSS user tries to get deployment info for tag$")
  public void the_PSS_user_tries_to_get_deployment_info_for_tag() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setTagPid(selectedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetArchivedTagDeploymentRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to get list of available adnets$")
  public void the_PSS_user_tries_to_get_list_of_available_adents() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        tagRequests.getGetPublisherAdnetsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    // mx-463 start
    if (commonSteps.serverResponse != null) {
      adnetResp = commonSteps.serverResponse;
    }
  }

  @When("^selfServeEnablement is correct$")
  public void selfServeEnablement_is_correct() throws Throwable {
    JSONObject adnet;
    adnetResp2 = adnetResp.array();
    String adnetsse;
    String adnetpid;
    for (int i = 0; i < adnetResp2.length(); i++) {
      adnet = adnetResp2.getJSONObject(i);
      adnetpid = adnet.getString("pid");
      adnetsse = adnet.getString("selfServeEnablement");
      AdNetAdsourcePID = adsourcedefaultsSteps.ADsourceResp(adnetpid);
      if ((!adnetsse.equals("NONE"))
          && (!adnetsse.equals("ENABLED"))
          && (AdNetAdsourcePID.equals(adnetpid))) {
        assertEquals(adnetsse, "ADDITIONAL");
      }
    }
  }
  // mx-463 end

  @When("^the PSS user tries to get list of available adnets thats not their own$")
  public void the_PSS_user_tries_to_get_list_of_available_adents_thats_not_their_own()
      throws Throwable {
    String randomBuyerPidExistingInDb = "105";
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(randomBuyerPidExistingInDb).getRequestParams();
    commonSteps.request =
        tagRequests.getGetPublisherAdnetsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects the tag to audit$")
  public void the_user_selects_the_tag_to_audit() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setTagPid(selectedTagPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request = tagRequests.getTagAuditRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects the adsource id \"(.+?)\" tag$")
  public void the_user_selects_the_adsource_tag(String adsourcePid) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setAdsourcePid(adsourcePid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getAdsourceTagRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects the adsource id \"(.+?)\" tag metric$")
  public void the_user_selects_the_adsource_tag_metric(String adsourcePid) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setAdsourcePid(adsourcePid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getAdsourceTagMetricRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects a specific revision for tag \"(.+?)\"$")
  public void the_user_selects_a_specific_revision_for_tag(String revision) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setTagPid(selectedTagPid)
            .setRevisionNumber(revision)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getTagAuditRequestForRevision().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^tag \"(.+?)\" is retrieved from database$")
  public void tag_is_retrieved_from_database(String tagName) throws Throwable {
    selectedTagPid = databaseUtils.getTagPidByName(tagName);
    assertNotNull(selectedTagPid, "tag " + tagName + " not exists in database.");
  }

  @When("^AOL Mobile tag \"(.+?)\" has correct additional values$")
  public void validateAolTagAdditional(String tagName) throws Throwable {
    assertEquals("1", databaseUtils.getTagOwnerByName(tagName), "Owner has incorrect value");
    assertEquals(
        "1", databaseUtils.getTagMonetizationByName(tagName), "Monetization has incorrect value");
    assertEquals(
        "1",
        databaseUtils.getTagIncludeDomainRefByName(tagName),
        "Include domain references has incorrect value");
    assertEquals(
        "1",
        databaseUtils.getTagIncludeConsumerIdByName(tagName),
        "Include consumer id has incorrect value");
    assertEquals(
        "1",
        databaseUtils.getTagIncludeConsumerProfileByName(tagName),
        "Include consumer profile has incorrect value");
  }

  @When("^tag \"(.+?)\" has correct owner value \"(.+?)\"$")
  public void validateTagOwnership(String tagName, String owner) throws Throwable {
    assertEquals(owner, databaseUtils.getTagOwnerByName(tagName), "Owner has incorrect value");
  }

  @When("^Ad Network tag \"(.+?)\" has correct additional values$")
  public void validateAdTagAdditional(String tagName) throws Throwable {
    assertEquals("0", databaseUtils.getTagOwnerByName(tagName), "Owner has incorrect value");
    assertEquals(
        "1", databaseUtils.getTagMonetizationByName(tagName), "Monetization has incorrect value");
  }

  @When("^there is/are only \"(.+?)\" tags for tag name \"(.+?)\"$")
  public void new_tag_is_not_generated_for_name(String count, String tagName) throws Throwable {
    int expectedTagRecord = Integer.valueOf(count);
    int actualTagRecord = Integer.valueOf(databaseUtils.countTagByName(tagName));
    assertEquals(
        expectedTagRecord,
        actualTagRecord,
        "Count of tags for name " + tagName + " does not match expected value");
  }

  @When("^tag \"(.+?)\" is not retrieved from database$")
  public void tag_is_not_retrieved_from_database(String tagName) throws Throwable {
    selectedTagPid = databaseUtils.getTagPidByName(tagName);
    assertNull(selectedTagPid, "tag " + tagName + " not exists in database.");
  }

  @When("^status of the tag \"(.+?)\" equals to \"(.+?)\"$")
  public void validate_tag_status(String tagName, String expectedTagStatus) throws Throwable {
    actualTagStatus = databaseUtils.getStatusByTagName(tagName);
    assertEquals(expectedTagStatus, actualTagStatus, "Tag status is incorrect");
  }

  @When("^the user tries to search archived tag$")
  public void the_user_tries_to_search_archived_tag() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTagPid(selectedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetPublisherTagRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to get the tag details for the archived tag \"(.+?)\"$")
  public void the_user_tries_to_get_tag_details_for_the_archived_tag(String pid) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setTagPid(pid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetArchivedTagDeploymentRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to get the tag details for the archived position \"(.+?)\"$")
  public void the_user_tries_to_get_tag_details_for_the_archived_position(String pid)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(pid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetPublisherTagsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user clones a publisher \"(.+?)\" tag from the json file \"(.+?)\"$")
  public void the_user_clones_a_publisher_tag_from_the_json_file(
      String tagTypeName, String filename) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .setTagPid(selectedTagPid)
            .setTargetSite(selectedTargetSitePid)
            .setTargetPosition(selectedTargetPositionPid)
            .getRequestParams();
    prepareForCopyingPublisherTagFromFile(tagTypeName, filename);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user \"(.+?)\" updates the tag \"(.+?)\" and includeSiteName from the json file \"(.+?)\"$")
  public void the_user_updates_the_tag_and_includeSiteName_from_the_json_file(
      String user, String tagName, String filename) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setTagPid(getSelectedTagPid(tagName)).getRequestParams();
    tagType = TagType.EXCHANGE;
    prepareForUpdatingNewlyCreatedPublisherTag(filename, user);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(updateTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user \"(.+?)\" updates the publisher tag and includeSiteName from the json file \"(.+)\"$")
  public void the_user_updates_the_publisher_tag_from_the_json_file1(String user, String filename)
      throws Throwable {
    prepareForUpdatingNewlyCreatedPublisherTag(filename, user);
    tagType = TagType.EXCHANGE;
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .setTagPid((selectedTagPid))
            .getRequestParams();
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(updateTag);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^created publisher tag data matches the following json file \"(.+)\"$")
  public void created_publisher_tag_data_matches_the_following_json_file(String filename)
      throws Throwable {
    JSONObject expectedTag = JsonHandler.getJsonObjectFromFile(filename);
    JSONObject createdTag = commonSteps.serverResponse.toObject();
    tagPrimaryId = createdTag.getString(JsonField.PRIMARY_ID);
    JSONObject rtbProfile = (JSONObject) createdTag.get(JsonField.RTB_PROFILE);
    includeSiteName = rtbProfile.getString(JsonField.INCLUDE_SITE_NAME);
    rtbPid = rtbProfile.getString(JsonField.PID);
    JSONObject currentReq = (JSONObject) currentRequest.getRequestPayload();
    JSONObject rtbCurrent = (JSONObject) currentReq.get(JsonField.RTB_PROFILE);
    rtbCurrent.put(JsonField.INCLUDE_SITE_NAME, includeSiteName);
    ResponseHandler.matchResponseWithExpectedResult(
        currentRequest, expectedTag.toString(), createdTag, "created tag");
  }

  @When(
      "^the database field ‘include_site_name’ is \"(defaulted|updated|preserved)\" as \"(0|1|2)\"$")
  public void the_database_field_is_as(String type, String value) throws Throwable {
    switch (type) {
      case "defaulted":
        {
          String selectedTagIncludeSiteName = databaseUtils.getTagIncludeSiteName(newTagName);
          assertTrue(
              selectedTagIncludeSiteName.equals(value),
              "includeSiteName is not defaulted to" + value + "for newly created tag");
          break;
        }
      case "updated":
        {
          String selectedTagIncludeSiteName = databaseUtils.getTagIncludeSiteName(updateTagName);
          assertTrue(
              selectedTagIncludeSiteName.equals(value),
              "includeSiteName is not updated to" + value + "for the tag");
          break;
        }
      case "preserved":
        {
          String selectedTagIncludeSiteName = databaseUtils.getTagIncludeSiteName(updateTagName);
          assertTrue(
              selectedTagIncludeSiteName.equals(value),
              "includeSiteName is not preserved as" + value + "for the tag");
          break;
        }
    }
  }

  Map<String, String> adnets = new HashMap<>();
  String adnet, pid, pname, sid, sname;
  int responseCode = 0;

  @Given("^the adnet Id \"([^\"]*)\" and name \"([^\"]*)\"$")
  public void the_adnet_Id_and_name(String adnetId, String adnetName) throws Throwable {
    adnets.put(adnetName, adnetId);
  }

  @Given("^the user choses \"([^\"]*)\"$")
  public void the_user_choses(String adnetId) throws Throwable {
    adnet = adnetId;
  }

  @Given("^he sends \"([^\"]*)\" as value for primaryId$")
  public void he_sends_as_value_for_primaryId(String primaryId) throws Throwable {
    pid = primaryId;
  }

  @Given("^\"([^\"]*)\" as value for primaryName$")
  public void as_value_for_primaryName(String primaryName) throws Throwable {
    pname = primaryName;
  }

  @Given("^\"([^\"]*)\" as value for secondaryId$")
  public void as_value_for_secondaryId(String secondaryId) throws Throwable {
    sid = secondaryId;
  }

  @Given("^\"([^\"]*)\" as value for secondaryName$")
  public void as_value_for_secondaryName(String secondaryName) throws Throwable {
    sname = secondaryName;
  }

  @Then("^sends the request to validate$")
  public void sends_the_request_to_validate() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid("123")
            .setAdsourcePid(adnets.get(adnet))
            .setPrimaryId(pid)
            .setPrimaryName(pname)
            .setSecondaryId(sid)
            .setSecondaryName(sname)
            .getRequestParams();

    currentRequest = tagRequests.getValidateTagRequest();
    commonSteps.request = currentRequest.setRequestParams(commonSteps.requestMap);

    responseCode = commonSteps.request.executeGetAndGetResponseCode();
  }

  @Then("^he expects to get \"([^\"]*)\" as response code$")
  public void he_expects_to_get_as_response_code(String response) throws Throwable {

    assertEquals(Integer.parseInt(response), responseCode, "error creating the request");
  }

  @Then("^returned ecmp value should still be \"([^\"]*)\" and not \"([^\"]*)\" as sent$")
  public void returned_ecmp_value_should_still_be_and_not_as_sent(
      String defaultValue, String updatedValue) throws Throwable {
    JSONObject updatedTag = commonSteps.serverResponse.toObject();
    String ecmp_auto = updatedTag.getString("ecpmAuto");
    assertTrue(
        ecmp_auto.equals(defaultValue),
        "ecmpAuto value is not defaulted to " + defaultValue + "for newly created tag");
  }

  @When("^the PSS user gets performance metrics for selected tag$")
  public void the_PSS_user_gets_performance_metrics_for_selected_tag() throws Throwable {
    the_PSS_user_requests_performance_metrics_for_selected_tag();
    ErrorHandler.assertNotNull(
        commonSteps.request, commonSteps.serverResponse, "Performance metrics were not retrieved.");
    txId = commonSteps.serverResponse.toObject().getString(JsonField.TX_ID);
  }

  @When("^the PSS user requests performance metrics for selected tag$")
  public void the_PSS_user_requests_performance_metrics_for_selected_tag() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(selectedPositionPid)
            .setSitePid(selectedSitePid)
            .setTagPid(selectedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests
            .getGetTagPerformanceMetricsPssRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the_PSS_user_archives_the_selected_tag$")
  public void the_PSS_user_archives_the_selected_tag() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(selectedPositionPid)
            .setSitePid(selectedSitePid)
            .setTagPid(selectedTagPid)
            .setTxId(txId)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getTagArchivePssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user hits the tags endpoint$")
  public void the_user_hits_the_tagsummaries_endpoint() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid("248")
            .setSitePid("456")
            .setPositionPid("10206")
            .setPage("0")
            .setSize("10")
            .getRequestParams();
    commonSteps.request = tagsRequest.getTags().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^tag response doesn't contain the given tags")
  public void tag_response_not_contains_tags(DataTable tagNames) throws Throwable {
    List<JSONArray> tags = new ArrayList();
    for (int j = 0; j < commonSteps.serverResponse.array().length(); j++) {
      tags.add(commonSteps.serverResponse.array());

      if (tags != null) {
        for (String tagName : tagNames.asList()) {
          for (JSONArray tag : tags) {
            verfyNoTagsIncluded(tag, tagName);
          }
        }
      }
    }
  }

  // only look at the top level object
  @Then("^response doesn't contain the given tags")
  public void response_not_contains_tags(DataTable tagNames) throws Throwable {
    List<JSONArray> tags = new ArrayList();
    try {
      tags.add(commonSteps.serverResponse.object().getJSONArray(JsonField.TAGS));
    } catch (ClassCastException e1) {
      for (int j = 0; j < commonSteps.serverResponse.array().length(); j++) {
        JSONObject position = commonSteps.serverResponse.array().getJSONObject(j);
        if (!position.isNull("tags")) tags.add(position.getJSONArray(JsonField.TAGS));
      }

    } catch (JSONException e) {
    }
    for (String tagName : tagNames.asList()) {
      for (JSONArray tag : tags) {
        verfyNoTagsIncluded(tag, tagName);
      }
    }
  }

  @Then("^alter_reserve field in DB has correct value for tag \"([^\"]*)\"$")
  public void alter_reserve_field_in_DB_has_correct_value_for_tag(String tagName) throws Throwable {
    rtbProfile = databaseUtils.getRtbProfileByTagsPid(databaseUtils.getTagPidByName(tagName));
    assertTrue(
        rtbProfile.getAlter_reserve().equals("1"),
        "alter_reserve value in exchange_site_tag table is not updated to 1");
  }

  @When(
      "^the PSS user creates multiple publisher \"(.+?)\" tags from the json file \"(.+?)\" and set name in \"(.+?)\" with status \"(.+?)\"$")
  public void the_pss_user_creates_more_publisher_tags(
      String tagTypeName, String filename, String name, String status) throws Throwable {

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .getRequestParams();
    prepareForCreatingPublisherTagFromFileRateLimit(tagTypeName, filename, name, status);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
    Thread.sleep(1000);
  }

  @When(
      "^the user creates multiple publisher \"(.+?)\" tags from the json file \"(.+?)\" and set name in \"(.+?)\" with status \"(.+?)\"$")
  public void the_user_creates_more_publisher_tags(
      String tagTypeName, String filename, String name, String status) throws Throwable {

    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    prepareForCreatingTagFromFileRateLimit(tagTypeName, filename, name, status);
    commonSteps.request =
        currentRequest.setRequestParams(commonSteps.requestMap).setRequestPayload(newTag);
    commonFunctions.executeRequest(commonSteps);
    Thread.sleep(1000);
  }

  private void prepareForCreatingPublisherTagFromFileRateLimit(
      String tagTypeName, String filename, String name, String status) throws Throwable {
    newTag = JsonHandler.getJsonObjectFromFile(filename);
    newTag.put("name", name);
    newTag.put("status", status);
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getCreatePublisherRtbTagRequest();
      newTagName = newTag.get(JsonField.NAME).toString();
    } else if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getCreatePublisherMedTagRequest();
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForCreatingTagFromFileRateLimit(
      String tagTypeName, String filename, String name, String status) throws Throwable {
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getCreateNonExchangeTagRequest();
      newTag = JsonHandler.getJsonObjectFromFile(filename);
      newTag.put("name", name);
      newTag.put("status", status);
      newTagName = newTag.getString(JsonField.NAME);
    } else if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getCreateExchangeTagRequest();
      newTag = JsonHandler.getJsonObjectFromFile(filename);
      JSONObject tag = (JSONObject) newTag.get(JsonField.TAG);
      tag.put("name", name);
      tag.put("status", status);
      newTagName = tag.getString(JsonField.NAME);
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void verfyNoTagsIncluded(JSONArray tag, String tagName) throws Throwable {
    for (int i = 0; i < tag.length(); i++) {
      JSONObject t = tag.getJSONObject(i);
      if (!t.isNull("name"))
        assertFalse(
            tagName.equals(tag.getJSONObject(i).get("name")),
            "Tag with name " + tagName + " included in the response");
      else {
        String tagPid = databaseUtils.getTagPidByName(tagName);
        assertFalse(
            tagPid.equals(tag.getJSONObject(i).getString("pid")),
            "Tag with pid " + tagPid + " included in the response");
      }
    }
  }

  private void prepareForUpdatingNewlyCreatedPublisherTag(String filename, String user)
      throws Throwable {
    updateTag = JsonHandler.getJsonObjectFromFile(filename);
    JSONObject rtbProfile = (JSONObject) updateTag.get(JsonField.RTB_PROFILE);
    rtbProfile.put(JsonField.ID, tagPrimaryId);
    rtbProfile.put(JsonField.PID, rtbPid);
    String userType = getUserType(user);
    // String userType = commonSteps.currentUser.getString("companyType");
    switch (userType) {
      case nexageUserType:
        currentRequest = tagRequests.getUpdateExchangeTagRequest();
        JSONObject tag = (JSONObject) updateTag.get(JsonField.TAG);
        tag.put(JsonField.PRIMARY_ID, tagPrimaryId);
        tag.put(JsonField.PID, selectedTagPid);
        updateTagName = tag.get(JsonField.NAME).toString();
        break;
      case pssUserType:
        currentRequest = tagRequests.getUpdatePublisherRtbTagRequest();
        updateTag.put(JsonField.PID, selectedTagPid);
        updateTag.put(JsonField.PRIMARY_ID, tagPrimaryId);
        updateTagName = updateTag.get(JsonField.NAME).toString();
        break;
      default:
        throw new Exception(String.format("Invalid User [%s]", userType));
    }
  }

  private String getUserType(String user) throws Throwable {
    String userType = databaseUtils.getUserTypeForOneCentralUserName(user);
    return userType;
  }

  private void prepareForCreatingTagFromFile(String tagTypeName, String filename) throws Throwable {
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getCreateNonExchangeTagRequest();
      newTag = JsonHandler.getJsonObjectFromFile(filename);
      newTagName = newTag.getString(JsonField.NAME);
    } else if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getCreateExchangeTagRequest();
      newTag = JsonHandler.getJsonObjectFromFile(filename);
      JSONObject tag = (JSONObject) newTag.get(JsonField.TAG);
      newTagName = tag.getString(JsonField.NAME);
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForCreatingPublisherTagFromFile(String tagTypeName, String filename)
      throws Throwable {
    newTag = JsonHandler.getJsonObjectFromFile(filename);
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getCreatePublisherRtbTagRequest();
      newTagName = newTag.get(JsonField.NAME).toString();
    } else if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getCreatePublisherMedTagRequest();
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForCreatingMultiplePublisherTagsFromFile(String tagTypeName)
      throws Throwable {
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getCreateMultiplePublisherRtbTagRequest();
    } else if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getCreateMultiplePublisherMedTagRequest();
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForCopyingPublisherTagFromFile(String tagTypeName, String filename)
      throws Throwable {
    newTag = JsonHandler.getJsonObjectFromFile(filename);
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getCopyPublisherRtbTagRequest();
    } else if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getCopyPublisherMedTagRequest();
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForUpdatingTagFromFile(String tagTypeName, String filename) throws Throwable {
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getUpdateNonExchangeTagRequest();
      updateTag = JsonHandler.getJsonObjectFromFile(filename);
      updateTagName = updateTag.get(JsonField.NAME).toString();
    } else if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getUpdateExchangeTagRequest();
      updateTag = JsonHandler.getJsonObjectFromFile(filename);
      JSONObject tag = (JSONObject) updateTag.get(JsonField.TAG);
      updateTagName = tag.getString(JsonField.NAME);
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForDeletingTag(String tagTypeName) throws Throwable {
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getDeleteNonExchangeTagRequest();
    } else if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getDeleteExchangeTagRequest();
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  private void prepareForUpdatingPublisherTagFromFile(String tagTypeName, String filename)
      throws Throwable {
    updateTag = JsonHandler.getJsonObjectFromFile(filename);
    tagType = TagType.getTagType(tagTypeName);
    if (tagType == TagType.EXCHANGE) {
      currentRequest = tagRequests.getUpdatePublisherRtbTagRequest();
    } else if (tagType == TagType.NON_EXCHANGE) {
      currentRequest = tagRequests.getUpdatePublisherMedTagRequest();
    } else {
      throw new Exception(String.format("Invalid Tag Type [%s]", tagTypeName));
    }
  }

  public String getSelectedTagPid(String tagName) throws Throwable {
    retrieveSiteTags();
    for (int i = 0; i < actualSiteTags.length(); i++) {
      JSONObject tag = (JSONObject) actualSiteTags.get(i);
      if (tagName.equals(tag.get(JsonField.NAME))) {
        selectedTagPid = tag.getString(JsonField.PID);
        return selectedTagPid;
      }
    }
    assertNotNull(null, String.format("Can not find tag pid for tag name [%s]", tagName));

    return null;
  }

  private JSONObject getModifiedTag(JSONArray siteTags, String tagName) throws JSONException {
    JSONObject modifiedTag = null;
    for (int i = 0; i < siteTags.length(); i++) {
      JSONObject tag = (JSONObject) siteTags.get(i);
      if (tagName.equals(tag.get(JsonField.NAME))) {
        modifiedTag = tag;
        i = siteTags.length();
      }
    }
    assertNotNull(modifiedTag, "Can not find modified tag");

    return modifiedTag;
  }

  private JSONArray getSites() throws Throwable {
    retrieveTagSummaries();
    JSONArray sites = tagSummaries.getJSONArray(JsonField.SITES);
    assertTrue(sites.length() > 0, "Can not find sites in the tag summaries");

    return sites;
  }

  private JSONObject getSelectedPosition(String positionName, JSONObject selectedSite)
      throws JSONException {
    JSONArray positions = selectedSite.getJSONArray(JsonField.POSITIONS);
    assertTrue(positions.length() > 0, "Can not find positions in the tag summaries");

    JSONObject selectedPosition = getJsonObjectByName(positions, positionName);
    assertNotNull(
        selectedPosition, String.format("Can not find selected position [%s]", positionName));

    selectedPositionPid = selectedPosition.get(JsonField.PID).toString();
    return selectedPosition;
  }

  private JSONObject getSelectedSite(String siteName, JSONArray sites) throws JSONException {
    JSONObject selectedSite = getJsonObjectByName(sites, siteName);
    assertNotNull(selectedSite, String.format("Can not find selected site [%s]", siteName));

    selectedSitePid = selectedSite.get(JsonField.PID).toString();
    return selectedSite;
  }

  private JSONObject getSelectedTag(String tagName, JSONObject selectedPosition) throws Throwable {
    JSONArray tags = selectedPosition.getJSONArray(JsonField.TAGS);
    assertTrue(tags.length() > 0, "Can not find tags in the tag summaries");

    JSONObject selectedTag = getJsonObjectByName(tags, tagName);
    assertNotNull(selectedTag, String.format("Can not find selected tag [%s]", tagName));

    selectedTagPid = selectedTag.get(JsonField.PID).toString();
    return selectedTag;
  }

  private JSONObject getJsonObjectByName(JSONArray jsonArray, String name) throws JSONException {
    JSONObject jsonObject = null;
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject tag = jsonArray.getJSONObject(i);
      if (name.equals(tag.get(JsonField.NAME))) {
        jsonObject = tag;
        break;
      }
    }
    return jsonObject;
  }

  private JSONObject buildModifiedTagWithRtbProfile(JSONObject createdTag)
      throws JSONException, IOException {
    String createdRtbProfileId = createdTag.get(JsonField.PRIMARY_ID).toString();
    JSONObject createdRtbProfile = getRtbProfileFromServerResponse(createdRtbProfileId);

    JSONObject tagWithRtbProfile = new JSONObject();
    tagWithRtbProfile.put(JsonField.RTB_PROFILE, createdRtbProfile);
    tagWithRtbProfile.put(JsonField.TAG, createdTag);
    createdTag = tagWithRtbProfile;
    return createdTag;
  }

  private JSONObject getRtbProfileFromServerResponse(String createdRtbProfileId)
      throws JSONException, IOException {
    JSONArray allRtbProfiles =
        (JSONArray) commonSteps.serverResponse.toObject().get(JsonField.RTB_PROFILES);
    JSONObject createdRtbProfile = null;
    for (int i = 0; i < allRtbProfiles.length(); i++) {
      createdRtbProfile = (JSONObject) allRtbProfiles.get(i);
      if (createdRtbProfileId.equals(createdRtbProfile.get(JsonField.ID))) {
        i = allRtbProfiles.length();
      }
    }
    assertNotNull(createdRtbProfile, "Can not find created rtb profile");

    return createdRtbProfile;
  }

  private void executeGetPublisherTagRequest() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(selectedSitePid)
            .setPositionPid(selectedPositionPid)
            .setTagPid(selectedTagPid)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetPublisherTagRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void retrieveTagSummaries() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetTagSummariesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      tagSummaries = commonSteps.serverResponse.object();
    }
  }

  private void retrieveBidderPerformanceSummaries() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        tagRequests.getGetBidderPerformanceRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void retrieveSiteTags() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        tagRequests.getGetSiteTagsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    assertNotNull(commonSteps.serverResponse, "Site tags request has failed");
    actualSiteTags = commonSteps.serverResponse.toObject().getJSONArray(JsonField.TAGS);
    assertTrue(actualSiteTags.length() > 0, "Site tags are not found");
  }

  @Then("^performance metrics returned matches with database values$")
  public void performanceMetricsReturnedMatchesWithDatabaseValues() throws Throwable {
    Map<String, Object> master_values = new HashMap<>();
    BufferedReader in =
        new BufferedReader(new FileReader("src/test/resources/tag_performance_metrics.txt"));
    try {

      String line;
      while ((line = in.readLine()) != null) {
        String name_value_pair[] = line.split("=");
        master_values.put(name_value_pair[0], name_value_pair[1]);
      }

    } catch (IOException readException) {
      readException.printStackTrace();
    } finally {
      try {
        if (in != null) in.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Map<String, Object> dbValues =
        databaseUtils.getTagPerformance(Integer.parseInt((String) master_values.get("tagPid")));
    assertNotNull(dbValues, "Tag performance metric not exists in database.");
    assertEquals(
        dbValues.get("requests").toString(),
        master_values.get("requests"),
        "Tag requests do not match");
    assertEquals(
        dbValues.get("served").toString(), master_values.get("served"), "Tag serves do not match");
    assertEquals(
        dbValues.get("delivered").toString(),
        master_values.get("delivered"),
        "Tag deliveries do not match");
    assertEquals(
        dbValues.get("clicked").toString(),
        master_values.get("clicked"),
        "Tag clicks do not match");
    assertTrue(
        (new BigDecimal(dbValues.get("netRevenue").toString()))
                .compareTo(new BigDecimal(master_values.get("netRevenue").toString()))
            == 0,
        "Tag revenues do not match");
  }

  private void verify_tag_with_request() throws Throwable {
    // validate expected versus actual response
    for (int j = 0; j < commonSteps.serverResponse.array().length(); j++) {
      JSONObject responseTagArr = commonSteps.serverResponse.array().getJSONObject(j);
      String tagName = responseTagArr.getString("name");

      Tag tag = databaseUtils.getTagValuesPidByName(tagName);
      assertEquals(tag.getPid(), responseTagArr.getString("pid"), "pid");
      assertEquals(tag.getName(), responseTagArr.getString("name"), "name");
      assertEquals(tag.getPrimary_id(), responseTagArr.getString("primaryId"), "primaryId");
      assertEquals(tag.getPrimary_name(), responseTagArr.getString("primaryName"), "primaryName");
      assertEquals(tag.getSecondary_id(), responseTagArr.getString("secondaryId"), "secondaryId");
      assertEquals(
          tag.getSecondary_name(), responseTagArr.getString("secondaryName"), "secondaryName");
      assertEquals(
          tag.getBuyer_pid(), responseTagArr.getJSONObject("buyer").getString("pid"), "buyer pid");
      assertEquals(
          tag.getPosition_pid(),
          responseTagArr.getJSONObject("position").getString("pid"),
          "position");
      assertEquals(
          tag.getSite_pid(), responseTagArr.getJSONObject("site").getString("pid"), "site");
    }
  }
}
