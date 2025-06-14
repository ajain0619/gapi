package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.TierRequests;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class TierSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private TierRequests tierRequests;
  @Autowired public DatabaseUtils databaseUtils;

  private JSONArray tiers, positionsWithChangedTiers, actualPositions;
  private JSONObject expectedTier;
  private String firstTierPid;
  private String secondTierPid;
  private String thirdTierPid;
  private String fourthTierPid;
  private String encodedTierPid;
  private String resource;

  private static final String NON_EXISTING_PUBLISHER_PID = "0";
  private static final String ANY_SITE_PID = "1";
  private static final String ANY_PUBLISHER_PID = "2";
  private static final String ANY_POSITION_PID = "3";
  private static final String ANY_TIER_PID = "4";

  @When("^the user creates tiers from the json file \"(.+?)\"$")
  public void the_user_creates_tiers_from_the_json_file(String filename) throws Throwable {
    modifyTierNonPss(filename);
  }

  @When("^the user updates tiers from the json file \"(.+?)\"$")
  public void the_user_updates_tiers_from_the_json_file(String filename) throws Throwable {
    modifyTierNonPss(filename);
  }

  @When("^the user deletes tiers from the json file \"(.+?)\"$")
  public void the_user_deletes_tiers_from_the_json_file(String filename) throws Throwable {
    modifyTierNonPss(filename);
  }

  @When("^the user gets all publisher tiers$")
  public void the_user_gets_all_publisher_tiers() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetAllTiersPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      tiers = commonSteps.serverResponse.array();
    }
  }

  @When("^the user gets the publisher tier$")
  public void the_user_gets_the_publisher_tier() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(firstTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetTierPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets the tier using provision api$")
  public void the_user_get_tier_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTierPid(encodedTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetTierProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all tiers using provision api$")
  public void the_user_get_all_tiers_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetAllTiersProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    if (commonSteps.serverResponse != null) {
      tiers = commonSteps.serverResponse.array();
    }
  }

  @When("^the user gets all decision maker publisher tiers$")
  public void the_user_gets_all_decision_maker_publisher_tiers() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetDecisionMakerTierPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    assertNotNull(commonSteps.serverResponse, "Tiers not retrieved.");
  }

  @When("^the user creates publisher tier from the json file \"(.+?)\"$")
  public void the_user_creates_publisher_tier_from_the_json_file(String filename) throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates tier using provision api from the json file \"(.+?)\"$")
  public void the_user_creates_tier_provision_api(String filename) throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierProvisionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the publisher tier from the json file \"(.+?)\"$")
  public void the_user_updates_the_publisher_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    expectedTier.put(JsonField.PID, firstTierPid);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(firstTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getUpdateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the tier using provision api from the json file \"(.+?)\"$")
  public void the_user_updates_tier_provision_api(String filename) throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTierPid(encodedTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getUpdateTierProvisionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the decision maker publisher tier from the json file \"(.+?)\"$")
  public void the_user_updates_the_decision_maker_publisher_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getUpdateDecisionMakerTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the publisher waterfall tier from the json file \"(.+?)\"$")
  public void the_user_updates_the_publisher_waterfall_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    expectedTier.put(JsonField.PID, thirdTierPid);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(thirdTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getUpdateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the publisher second waterfall tier from the json file \"(.+?)\"$")
  public void the_user_updates_the_publisher_second_waterfall_tier_from_the_json_file(
      String filename) throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    expectedTier.put(JsonField.PID, fourthTierPid);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(fourthTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getUpdateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the publisher super auction tier from the json file \"(.+?)\"$")
  public void the_user_updates_the_publisher_superauction_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    expectedTier.put(JsonField.PID, secondTierPid);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(secondTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getUpdateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the publisher tier from the json file \"(.+?)\"$")
  public void the_user_deletes_the_publisher_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    expectedTier.put(JsonField.PID, firstTierPid);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(firstTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getDeleteTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the publisher super auction tier from the json file \"(.+?)\"$")
  public void the_user_deletes_the_publisher_SAtier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(secondTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getDeleteTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the tier using provision api$")
  public void the_user_deletes_tier_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTierPid(encodedTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getDeleteTierProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("the user assign tag to the tier using provision api")
  public void the_user_assign_tag_tier_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTagPid(commonSteps.encodedTagPid)
            .setTierPid(encodedTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getAssignTagProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("the user unassign tag from the tier using provision api")
  public void the_user_unassign_tag_tier_provision_api() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .setTagPid(commonSteps.encodedTagPid)
            .setTierPid(encodedTierPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getUnassignTagProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user sort tiers using provision api from the json file \"(.+?)\"$")
  public void the_user_sort_tiers_provision_api(String filename) throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getSortTiersProvisionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^tier pid \"(.+?)\" cannot be searched in the database$")
  public void tier_cannot_be_searched_in_the_database(String tierPid) throws Throwable {
    String tierPssPid = databaseUtils.getTierByPid(tierPid);
    assertTrue(tierPssPid.equals("0"), "Count is not 0");
  }

  @When("^tier pid \"(.+?)\" can be searched in the database$")
  public void tier_can_be_searched_in_the_database(String tierPid) throws Throwable {
    String tierPssPid = databaseUtils.getTierByPid(tierPid);
    assertTrue(tierPssPid.equals("1"), "Count is not 1");
  }

  @When("^tier count for position \"(.+?)\" equals to \"(.+?)\"$")
  public void tier_count_position_pid(String positionName, String expectedTierCount)
      throws Throwable {
    String actualTierCount = databaseUtils.getTierCountByPositionPid(positionName);
    assertTrue(actualTierCount.equals(expectedTierCount), "Count is incorrect");
  }

  @When("^tier level for tier pid \"(.+?)\" equals to \"(.+?)\"$")
  public void tier_level_validate_tier_pid(String tierPid, String expectedTierLevel)
      throws Throwable {
    String actualTierLelel = databaseUtils.getTierLevelByTierPid(tierPid);
    assertTrue(actualTierLelel.equals(expectedTierLevel), "Tier level is incorrect");
  }

  @When("^tag pid for the \"(.+?)\" tier pid cannot be searched in the database$")
  public void tag_pid_for_tier_cannot_be_searched_in_the_database(String tierPid) throws Throwable {
    String tagPidCount = databaseUtils.getTagCountByTierPid(tierPid);
    assertTrue(tagPidCount.equals("0"), "Count is not 0");
  }

  @When("^tag pid count for the \"(.+?)\" equals to \"(.+?)\"$")
  public void tag_pid_count_by_tier_pid(String tierPid, String expectedTagCount) throws Throwable {
    String actualTagPidCount = databaseUtils.getTagCountByTierPid(tierPid);
    assertTrue(actualTagPidCount.equals(expectedTagCount), "Count is not correct");
  }

  @When("^the user gets non existing publisher tier$")
  public void the_user_gets_non_existing_publisher_tier() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTierPid(NON_EXISTING_PUBLISHER_PID)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetTierPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user calls forbidden create publisher tier service$")
  public void the_user_calls_forbidden_create_publisher_tier_service() throws Throwable {
    JSONObject anyPayload = new JSONObject("{}");
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(ANY_SITE_PID)
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user calls forbidden get publisher tier service$")
  public void the_user_calls_forbidden_get_publisher_tier_service() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(ANY_SITE_PID)
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setPositionPid(ANY_POSITION_PID)
            .setTierPid(ANY_TIER_PID)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetTierPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user calls forbidden get all publisher tier service$")
  public void the_user_calls_forbidden_get_all_publisher_tier_service() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(ANY_SITE_PID)
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        tierRequests.getGetAllTiersPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^set specific tier pid as \"(.+?)\"$")
  public void set_specific_tier_pid(String tierPid) throws Throwable {
    encodedTierPid = new String(Base64.encodeBase64(tierPid.getBytes()));
  }

  @When("^first publisher tier is retrieved$")
  public void first_publisher_tier_is_retrieved() throws Throwable {
    assertNotNull(tiers, "Tiers are not retrieved");
    firstTierPid = tiers.getJSONObject(0).getString(JsonField.PID);
  }

  @When("^second publisher tier is retrieved$")
  public void second_publisher_tier_is_retrieved() throws Throwable {
    assertNotNull(tiers, "Tiers are not retrieved");

    secondTierPid = tiers.getJSONObject(1).getString(JsonField.PID);
  }

  @When("^third publisher tier is retrieved$")
  public void third_publisher_tier_is_retrieved() throws Throwable {
    assertNotNull(tiers, "Tiers are not retrieved");

    thirdTierPid = tiers.getJSONObject(2).getString(JsonField.PID);
  }

  @When("^fourth publisher tier is retrieved$")
  public void fourth_publisher_tier_is_retrieved() throws Throwable {
    assertNotNull(tiers, "Tiers are not retrieved");

    fourthTierPid = tiers.getJSONObject(3).getString(JsonField.PID);
  }

  @Then("^returned tier data matches the following json file \"(.+?)\"$")
  public void returned_tier_data_matches_the_following_json_file(String filename) throws Throwable {
    assertNotNull(actualPositions, "Positions are not retrieved");
    String expectedJson = TestUtils.getResourceAsString(filename);
    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request, expectedJson, actualPositions, JsonField.POSITIONS);
  }

  private void modifyTierNonPss(String filename) throws Throwable {
    positionsWithChangedTiers = JsonHandler.getJsonArrayFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        tierRequests
            .getModifyTierNonPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(positionsWithChangedTiers);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      actualPositions = commonSteps.serverResponse.object().getJSONArray(JsonField.POSITIONS);
    }
  }

  @When("^the user creates sy_decision_maker tier from the json file \"([^\"]*)\"$")
  public void the_user_creates_sy_decision_maker_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates super_auction tier from the json file \"([^\"]*)\"$")
  public void the_user_creates_super_auction_tier_from_the_json_file(String filename)
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^a user creates super_auction tier using json file \"([^\"]*)\"$")
  public void a_user_creates_super_auction_tier_using_json_file(String filename) throws Throwable {
    resource = filename;
  }

  @When("^create payload contains valid tag$")
  public void create_payload_contains_valid_tag() throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^create payload contains invalid tag$")
  public void create_payload_contains_invalid_tag() throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^sy_decision_maker tier does not exists$")
  public void sy_decision_maker_tier_does_not_exists() throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^sy_decision_maker tier already exists$")
  public void sy_decision_maker_tier_already_exists() throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^a user create sy_decision_maker tier at non zero level using json file \"([^\"]*)\"$")
  public void a_user_create_sy_decision_maker_tier_at_non_zero_level_using_json_file(
      String filename) throws Throwable {
    resource = filename;
  }

  @Given("^a user create sy_decision_maker tier request input \"([^\"]*)\"$")
  public void a_user_create_sy_decision_maker_tier_using_json_file(String filename)
      throws Throwable {
    resource = filename;
  }

  @Given("^a user creates waterfall tier using json file \"([^\"]*)\"$")
  public void a_user_creates_waterfall_tier_using_json_file(String filename) throws Throwable {
    resource = filename;
  }

  @When("^create sy_decision_maker tier request contains no tag$")
  public void create_sy_decision_maker_tier_request_contains_no_tag() throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    int numberOfTags = expectedTier.getJSONArray("tags").length();
    assertTrue((numberOfTags == 0), "request does not contain single tag");
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^create sy_decision_maker tier request contains a tag which is decision maker enabled$")
  public void create_sy_decision_maker_tier_request_contains_a_tag_which_is_decision_maker_enabled()
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^create new sy_decision_maker tier request from the json file \"(.+?)\"$")
  public void
      create_sy_decision_maker_new_tier_request_contains_a_tag_which_is_decision_maker_enabled(
          String filename) throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateDecisionMakerTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^create sy_decision_maker tier request contains one tag$")
  public void create_sy_decision_maker_tier_request_contains_one_decision_maker_enabled_tag()
      throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    int numberOfTags = expectedTier.getJSONArray("tags").length();
    assertTrue((numberOfTags == 1), "request does not contain single tag");
  }

  @When("^create sy_decision_maker tier request contains more than one tag$")
  public void create_sy_decision_maker_tier_request_contains_more_than_one_tag() throws Throwable {
    expectedTier = JsonHandler.getJsonObjectFromFile(resource);
    int numberOfTags = expectedTier.getJSONArray("tags").length();
    assertTrue((numberOfTags > 1), "request does not contain single tag");
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPublisherPid(commonSteps.companyPid)
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        tierRequests
            .getCreateTierPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedTier);
    commonFunctions.executeRequest(commonSteps);
  }
}
