package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.DealTerm;
import com.nexage.geneva.model.crud.PositionNonPss;
import com.nexage.geneva.model.crud.PositionPss;
import com.nexage.geneva.model.crud.RevenueShare;
import com.nexage.geneva.model.crud.RtbProfile;
import com.nexage.geneva.model.crud.Tag;
import com.nexage.geneva.model.crud.TagRule;
import com.nexage.geneva.model.crud.Tier;
import com.nexage.geneva.request.PositionRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SiteRequests;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.ErrorHandler;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class PositionSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private PositionRequests positionRequests;
  @Autowired private SiteRequests siteRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private JSONObject expectedPosition, actualPosition;
  private String actualPositionPid;
  private String expectedPositionPid;
  private String encodedCompanyPid;
  private String expectedCompanyPid;
  private String actualPositionVersion;
  private String expectedPositionVersion;
  public String txId;

  private PositionNonPss actualNonPssPositionFromDb;
  private PositionNonPss expectedNonPssPositionFromDb;
  private PositionPss actualPssPositionFromDb;

  private static final String ANY_POSITION_PID = "0000";
  private static final String ANY_SITE_PID = "000";
  private static final String ANY_PUBLISHER_PID = "00";
  private static final String TRUE = "true";
  private static final String FALSE = "false";
  public List<String> tags = new ArrayList<>();
  public List<String> tiers = new ArrayList<>();

  public List<Tag> selectedTagsListFromDb = new ArrayList<>();
  public List<Tag> returnedTagsListFromDb = new ArrayList<>();
  public List<RtbProfile> selectedrtbProfileListFromDb = new ArrayList<>();
  public List<RtbProfile> returnedrtbProfileListFromDb = new ArrayList<>();
  public HashMap<Tag, Tag> tagClones = new HashMap<>();

  @When("^the PSS user gets all positions$")
  public void the_PSS_user_gets_all_positions() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetAllPositionsPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user gets data for selected position$")
  public void the_PSS_user_gets_data_for_selected_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user gets data for position with detail \"(.+?)\"$")
  public void the_PSS_user_gets_data_for_position_with_detail(String detail) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setDetail(detail)
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionPssDetailRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user gets detailed data for selected position$")
  public void the_PSS_user_gets_detailed_data_for_selected_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetDetaledPositionPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user gets performance metrics for selected position$")
  public void the_PSS_user_gets_performance_metrics_for_selected_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getGetPositionPerformanceMetricsPssRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    ErrorHandler.assertNotNull(
        commonSteps.request, commonSteps.serverResponse, "Performance metrics were not retrieved.");

    txId = commonSteps.serverResponse.toObject().getString(JsonField.TX_ID);
  }

  @When("^the user creates position from the json file \"(.+?)\"$")
  public void the_user_creates_position_from_the_json_file(String filename) throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);

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

  @When("^the user creates position first call from the json file \"(.+?)\"$")
  public void the_user_creates_position_first_call_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionRequestNexageUser()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates position second call from the json file \"(.+?)\"$")
  public void the_user_creates_position_second_call_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    String txId = commonSteps.serverResponse.object().getString(JsonField.TX_ID);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setTxId(txId)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionSecondRequestRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user creates position from the json file \"(.+?)\"$")
  public void the_PSS_user_creates_position_from_the_json_file(String filename) throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setDetail(TRUE)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      actualPositionPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  @When("^the user creates position on pss api with limited response from the json file \"(.+?)\"$")
  public void the_user_creates_position_from_the_json_file_on_pss_limited_response(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setDetail(FALSE)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates position using provision API from the json file \"(.+?)\"$")
  public void create_position_provision_api(String filename) throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionProvisionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates position using provision API from the json file \"(.+?)\"$")
  public void update_provision_position_from_json_file(String filename) throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionProvisionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the position data for provision API is retrieved$")
  public void position_data_is_retrieved() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^try to get non existing provision$")
  public void try_get_non_existing_provision() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(commonSteps.getCompany().getId())
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionProvisionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the position for provision is deleted$")
  public void position_provision_is_deleted() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(encodedCompanyPid)
            .setSiteDcn(commonSteps.getSite().getDcn())
            .setPositionPid(commonSteps.encodedPositionPid)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getDeletePositionProvisionRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates selected position from the json file \"(.+?)\"$")
  public void the_user_updates_selected_position_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    expectedPosition.put(JsonField.PID, commonSteps.selectedPosition.getString(JsonField.PID));

    commonSteps.requestMap =
        new RequestParams()
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      getActualPositionFromResponse();
    }
  }

  @When("^the user updates selected position first call from the json file \"(.+?)\"$")
  public void the_user_updates_selected_position_first_call_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionRequestNexageUser()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates mediation rules setup from the json file \"(.+?)\"$")
  public void the_user_updates_mediation_rules_setup_from_the_json_file(String filename)
      throws Throwable {
    JSONArray expectedPositionArray = JsonHandler.getJsonArrayFromFile(filename);

    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionMediationRulesSetup()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPositionArray);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates selected position second call from the json file \"(.+?)\"$")
  public void the_user_updates_selected_position_second_call_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    String txId = commonSteps.serverResponse.object().getString(JsonField.TX_ID);
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setTxId(txId)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionSecondRequestRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user updates selected position from the json file \"(.+?)\"$")
  public void the_PSS_user_updates_selected_position_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      actualPositionPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  @When(
      "^the PSS user updates position with detail from the json file \"(.+?)\" with detail \"(.+?)\"$")
  public void the_PSS_user_updates_position_with_detail(String filename, String detail)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setDetail(detail)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionWithDetail()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);
    if (commonSteps.serverResponse != null) {
      actualPositionPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  @When("^the user deletes selected position$")
  public void the_user_deletes_selected_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests.getDeletePositionRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to update not existing position from the json file \"(.+?)\"$")
  public void the_user_tries_to_update_not_existing_position_from_the_json_file(String filename)
      throws Throwable {
    JSONObject anyPayload = JsonHandler.getJsonObjectFromFile(filename);
    anyPayload.put(JsonField.PID, ANY_POSITION_PID);

    commonSteps.requestMap =
        new RequestParams().setPositionPid(ANY_POSITION_PID).getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to update not existing position from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_update_not_existing_position_from_the_json_file(String filename)
      throws Throwable {
    JSONObject anyPayload = JsonHandler.getJsonObjectFromFile(filename);
    anyPayload.put(JsonField.PID, ANY_POSITION_PID);

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to get not existing position$")
  public void the_PSS_user_tries_to_get_not_existing_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to create position of any publisher from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_create_position_of_any_publisher_from_the_json_file(
      String filename) throws Throwable {
    JSONObject anyPayload = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setSitePid(ANY_SITE_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to update position of any publisher from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_update_position_of_any_publisher_from_the_json_file(
      String filename) throws Throwable {
    JSONObject anyPayload = JsonHandler.getJsonObjectFromFile(filename);
    anyPayload.put(JsonField.PID, ANY_POSITION_PID);

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setSitePid(ANY_SITE_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to get position of any publisher$")
  public void the_PSS_user_tries_to_get_position_of_any_publisher() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(ANY_PUBLISHER_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to get all positions")
  public void unauthorized_user_tries_to_get_all_positions() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setSitePid(ANY_SITE_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getGetAllPositionsPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setFollowRedirects(false);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to get position$")
  public void unauthorized_user_tries_to_get_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setSitePid(ANY_SITE_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getGetPositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setFollowRedirects(false);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to create position from the json file \"(.+?)\"$")
  public void unauthorized_user_tries_to_create_position_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setSitePid(ANY_SITE_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getCreatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition)
            .setFollowRedirects(false);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to update position from the json file \"(.+?)\"$")
  public void unauthorized_user_tries_to_update_position_from_the_json_file(String filename)
      throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    expectedPosition.put(JsonField.PID, ANY_POSITION_PID);

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_PUBLISHER_PID)
            .setSitePid(ANY_SITE_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getUpdatePositionPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition)
            .setFollowRedirects(false);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^unauthorized user tries to delete position with follow redirect is \"(.+?)\"$")
  public void unauthorized_user_tries_to_delete_position(String redirect) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSitePid(ANY_PUBLISHER_PID)
            .setPositionPid(ANY_POSITION_PID)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getDeletePositionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setFollowRedirects(Boolean.valueOf(redirect));
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^returned position data matches the following json file \"(.+?)\"$")
  public void returned_position_data_matches_the_following_json_file(String filename)
      throws Throwable {
    actualPositionPid = actualPosition.getString(JsonField.PID);
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request, expectedPosition.toString(), actualPosition, "position");
  }

  @Then("^returned pss position version is \"(.+?)\"$")
  public void returned_pss_position_version_is_correct(String expectedPositionVersion)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    actualPositionVersion = commonSteps.selectedPosition.getString(JsonField.VERSION);
    assertTrue(
        actualPositionVersion.equals(expectedPositionVersion),
        "Expected version does not match actual version");
  }

  @Then("^returned non-pss position version is \"(.+?)\"$")
  public void returned_non_pss_position_version_is_correct(String expectedPositionVersion)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSitePid(commonSteps.getSite().getPid()).getRequestParams();
    commonSteps.request = siteRequests.getGetSiteRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    actualPositionVersion = commonSteps.selectedPosition.getString(JsonField.VERSION);
    assertTrue(
        actualPositionVersion.equals(expectedPositionVersion),
        "Expected version does not match actual version");
  }

  @When("^the selected position can be searched out in database$")
  public void the_selected_position_can_be_searched_out_in_database() throws Throwable {
    expectedNonPssPositionFromDb = databaseUtils.getPositionNonPssByPid(expectedPositionPid);
    assertNotNull(expectedNonPssPositionFromDb, "Returned position doesn't exists in DB");
  }

  @Then("^status of the position with the position name \"(.+?)\" is \"(.+?)\"$")
  public void status_by_position_name(String positionName, String expStatus) throws Throwable {
    String actualStatus = databaseUtils.getStatusByPositionName(positionName);
    assertEquals(
        "Position status for the position name " + positionName + " is not correct",
        expStatus,
        actualStatus);
  }

  @When("^position pid is retrieved for name \"(.+?)\"$")
  public void position_pid_is_retrieved(String name) throws Throwable {
    assertNotNull(
        "Position pid is null",
        databaseUtils.getPositionPidByNameSitePid(name, commonSteps.getSite().getPid()));
  }

  @When("^set company pid for name \"(.+?)\"$")
  public void set_company_pid_by_name(String name) throws Throwable {
    expectedCompanyPid = databaseUtils.getCompanyPidByName(name);
    encodedCompanyPid = new String(Base64.encodeBase64(expectedCompanyPid.getBytes()));
    assertNotNull("Position pid is null", encodedCompanyPid);
  }

  @When("^position pid is not retrieved for name \"(.+?)\"$")
  public void position_pid_is_not_retrieved(String name) throws Throwable {
    assertNull(
        "Position pid is null",
        databaseUtils.getPositionPidByNameSitePid(name, commonSteps.getSite().getPid()));
  }

  @Then("^returned position can be searched out in database$")
  public void returned_position_can_be_searched_out_in_database() throws Throwable {
    actualNonPssPositionFromDb = databaseUtils.getPositionNonPssByPid(actualPositionPid);
    assertNotNull(actualNonPssPositionFromDb, "Returned position doesn't exists in DB");
  }

  @Then("^the returned cloned position can be searched out in database$")
  public void the_returned_cloned_position_can_be_searched_out_in_database() throws Throwable {
    actualNonPssPositionFromDb = databaseUtils.getPositionNonPssByPid(actualPositionPid);
    assertNotNull(actualNonPssPositionFromDb, "Returned position doesn't exists in DB");
  }

  @Then("^returned pss position can be searched out in database$")
  public void returned_pss_position_can_be_searched_out_in_database() throws Throwable {
    assertNotNull("Position is not selected", actualPositionPid);

    actualPssPositionFromDb = databaseUtils.getPositionPssByPid(actualPositionPid);
    assertNotNull(actualPssPositionFromDb, "Returned pss position doesn't exists in DB");
  }

  @Then("^position data in database is correct$")
  public void position_data_in_database_is_correct() throws Throwable {
    PositionNonPss expectedNonPssPosition =
        TestUtils.mapper.readValue(expectedPosition.toString(), PositionNonPss.class);
    expectedNonPssPosition.setPid(actualNonPssPositionFromDb.getPid());
    assertTrue(
        expectedNonPssPosition.equals(actualNonPssPositionFromDb),
        "Position data in database is not correct.");
  }

  @Then("^pss position data in database is correct$")
  public void pss_position_data_in_database_is_correct() throws Throwable {
    PositionPss expectedPssPosition =
        TestUtils.mapper.readValue(expectedPosition.toString(), PositionPss.class);
    expectedPssPosition.setPid(actualPssPositionFromDb.getPid());
    assertTrue(
        expectedPssPosition.equals(actualPssPositionFromDb),
        "Pss position data in database is not correct.");
  }

  @Then("^advanced mraid tracking defaults to \"([^\"]*)\" in the db$")
  public void advanced_mraid_tracking_default_is_set_correctly_in_db(
      boolean expectedPositionAdvMraidTracking) throws Throwable {
    String positionPid = commonSteps.selectedPosition.getString(JsonField.PID);
    boolean result = databaseUtils.getAdvancedMraidTracking(positionPid);
    if (expectedPositionAdvMraidTracking == true) {
      assertTrue(result == true, "mraid advanced tracking does not default to true");
    } else if (expectedPositionAdvMraidTracking == false) {
      assertTrue(result == false, "mraid advanced tracking does not default to false");
    }
  }

  @Then("^deleted position cannot be searched out in database$")
  public void deleted_position_cannot_be_searched_out_in_database() throws Throwable {
    String deletedPositionPid = commonSteps.selectedPosition.getString(JsonField.PID);
    try {
      databaseUtils.getPositionNonPssByPid(deletedPositionPid);
      assertTrue(false, "Position exists in DB.");
    } catch (EmptyResultDataAccessException ex) {
      getLogger(PositionSteps.class).info(ex.getMessage());
    }
  }

  @When("^the user selects the position to audit$")
  public void the_user_selects_the_position_to_audit() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionAuditRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user select a specific revision for position \"(.+?)\"$")
  public void the_user_select_a_specific_revision_for_position(String revision) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.getCompany().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setRevisionNumber(revision)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getPositionAuditRequestForRevision()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^position \"(.+?)\" is retrieved from database$")
  public void position_is_retrieved_from_database(String positionName) throws Throwable {
    PositionPss positionPss =
        databaseUtils.getPositionPssByName(positionName, commonSteps.getSite().getPid());
    assertNotNull(positionPss, "position " + positionName + " not exists in database.");

    commonSteps.selectedPosition = new JSONObject(TestUtils.convertToJson(positionPss));
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

  @When("^position by pid \"(.+?)\" is retrieved from database$")
  public void position_by_pid_is_retrieved_from_database(String positionPid) throws Throwable {
    PositionPss positionPss = databaseUtils.getPositionPssByPid(positionPid);
    assertNotNull(positionPss, "position by Pid " + positionPid + " not exists in database.");

    commonSteps.selectedPosition = new JSONObject(TestUtils.convertToJson(positionPss));
  }

  @When("^the PSS user clones position to target site \"(.+?)\" from the json file \"(.+?)\"$")
  public void the_PSS_user_clones_position_to_target_site_from_the_json_file(
      String targetsite, String filename) throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    expectedPositionPid = commonSteps.selectedPosition.getString(JsonField.PID);

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .setTargetSite(targetsite)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getClonePositionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      actualPositionPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  @When("^the selected tags can be searched in database$")
  public void the_selected_tags_can_be_searched_in_database() throws Throwable {
    selectedTagsListFromDb = databaseUtils.getTagsByPositionPid(expectedPositionPid);
    assertNotNull(selectedTagsListFromDb, "Selected tags for position doesn't exists in DB");
  }

  @When("^the returned tags can be searched in database$")
  public void the_retured_tags_can_be_searched_in_database() throws Throwable {
    returnedTagsListFromDb = databaseUtils.getTagsByPositionPid(actualPositionPid);
    assertNotNull(returnedTagsListFromDb, "Selected tags for position doesn't exists in DB");
  }

  @Then("^the selected position and returned position data in databse is correct$")
  public void the_selected_position_and_returned_position_data_in_database_is_correct()
      throws Throwable {
    assertTrue(
        expectedNonPssPositionFromDb.compareClones(actualNonPssPositionFromDb),
        "Position data in database do not match.");
  }

  @Then("^the selected and returned tags data in databse is correct for that position$")
  public void the_selected_and_returned_tags_data_in_database_is_correct_for_that_position()
      throws Throwable {
    List<Tag> tmpReturnedTagList = new ArrayList();
    for (Tag selectedTag : selectedTagsListFromDb) {
      for (Tag returnedTag : returnedTagsListFromDb) {
        if (selectedTag.compareClones(returnedTag)) {
          tmpReturnedTagList.add(returnedTag);
          tagClones.put(selectedTag, returnedTag);
        }
      }
    }
    if (tmpReturnedTagList.size() != returnedTagsListFromDb.size()) {
      assertTrue(false, "tag data in database do not match.");
    }
  }

  @Then("^the selected and returned mediation tags data in databse is correct for that position$")
  public void
      the_selected_and_returned_mediation_tags_data_in_database_is_correct_for_that_position()
          throws Throwable {
    List<Tag> tmpReturnedTagList = new ArrayList();
    for (Tag selectedTag : selectedTagsListFromDb) {
      for (Tag returnedTag : returnedTagsListFromDb) {
        if (selectedTag.compareClonesMediationTags(returnedTag)) {
          tmpReturnedTagList.add(returnedTag);
          tagClones.put(selectedTag, returnedTag);
        }
      }
    }
    if (tmpReturnedTagList.size() != returnedTagsListFromDb.size()) {
      assertTrue(false, "tag data in database do not match.");
    }
  }

  @Then("^the selected and returned rtb profile data in database is correct$")
  public void the_selected_and_returned_rtb_profile_data_in_database_is_correct_()
      throws Throwable {
    RtbProfile tempRtb;
    for (Tag selectedTag : selectedTagsListFromDb) {
      tempRtb = databaseUtils.getRtbProfileByTagsPid(selectedTag.getPid().toString());
      if (tempRtb != null) {
        selectedrtbProfileListFromDb.add(tempRtb);
      }
    }
    for (Tag returnedTag : returnedTagsListFromDb) {
      tempRtb = databaseUtils.getRtbProfileByTagsPid(returnedTag.getPid().toString());
      if (tempRtb != null) {
        returnedrtbProfileListFromDb.add(tempRtb);
      }
    }
    List<RtbProfile> tmpReturednrtbProfileList = new ArrayList();
    for (RtbProfile selectedRtb : selectedrtbProfileListFromDb) {
      for (RtbProfile returnedRtb : returnedrtbProfileListFromDb) {
        if (selectedRtb.compareClones(returnedRtb)) {
          tmpReturednrtbProfileList.add(returnedRtb);
        }
      }
    }
    if (tmpReturednrtbProfileList.size() != returnedrtbProfileListFromDb.size()) {
      assertTrue(false, "rtb profile data in database do not match.");
    }
  }

  @When(
      "^the PSS user clones position with id \"(.+?)\" to target site \"(.+?)\" from the json file \"(.+?)\"$")
  public void the_PSS_user_clones_position_with_id_to_target_site_from_the_json_file(
      String positionid, String targetsite, String filename) throws Throwable {
    expectedPosition = JsonHandler.getJsonObjectFromFile(filename);
    expectedPositionPid = positionid;

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(positionid)
            .setTargetSite(targetsite)
            .getRequestParams();
    commonSteps.request =
        positionRequests
            .getClonePositionRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedPosition);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      actualPositionPid = commonSteps.serverResponse.object().getString(JsonField.PID);
    }
  }

  @When("^the non PSS user gets data for selected position$")
  public void the_non_PSS_user_gets_data_for_selected_position() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setSitePid(commonSteps.getSite().getPid())
            .setPositionPid(commonSteps.selectedPosition.getString(JsonField.PID))
            .getRequestParams();
    commonSteps.request =
        positionRequests.getGetPositionPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Before("@updateFactRevenueAdnetDatesBefore")
  @Given("^update fact_revenue_adnet start and end dates$")
  public void update_fact_reveue_adnet_table() throws Throwable {
    databaseUtils.updateFactRevenueAdnetStartEndDates();
  }

  @When("^the selected and returned deal terms are correct$")
  public void the_selected_and_returned_deal_terms_are_correct() throws Throwable {

    for (Tag key : tagClones.keySet()) {
      DealTerm dealTermSelected = databaseUtils.getLatestDealTermsFromTagPid(key.getPid());
      DealTerm dealTermReturned =
          databaseUtils.getLatestDealTermsFromTagPid(tagClones.get(key).getPid());
      if (dealTermSelected != null
          && dealTermReturned
              != null) { // tags when created through seller admin have no deal terms, but tags
        // created through pss have deal terms with "0" default value
        assertTrue(
            dealTermSelected.compareClones(dealTermReturned),
            "Selected dealterm for tag ID "
                + key.getPid()
                + " does not match with returned dealterm with tag ID "
                + tagClones.get(key).getPid());
      }
    }
  }

  @When("^the selected and returned revenue share and rtb fee values are the same$")
  public void the_selected_and_returned_revenue_share_and_rtb_fee_values_are_the_same()
      throws Throwable {

    List<RevenueShare> revenueShareList = databaseUtils.getRevenueShares();

    for (Tag key : tagClones.keySet()) {

      String selectedPid = key.getPid();
      String clonedPid = tagClones.get(key).getPid();

      for (RevenueShare revenueShare : revenueShareList) {
        if (revenueShare.getTag_pid() == selectedPid) {
          for (RevenueShare revenueSharenext : revenueShareList) {
            if (revenueSharenext.getTag_pid() == clonedPid) {

              assertTrue(
                  revenueShare.getNexage_rev_share().equals(revenueSharenext.getNexage_rev_share()),
                  "Revenue share for tag id "
                      + revenueShare.getTag_pid()
                      + "and tag id "
                      + revenueSharenext.getTag_pid()
                      + " are not equal");
              assertTrue(
                  revenueShare.getNexage_rtb_fee().equals(revenueSharenext.getNexage_rtb_fee()),
                  "Revenue share for tag id "
                      + revenueShare.getTag_pid()
                      + "and tag id "
                      + revenueSharenext.getTag_pid()
                      + " are not equal");
            }
          }
        }
      }
    }
  }

  @When("^the selected and returned tag rules are equal$")
  public void the_selected_and_returned_tag_rules_are_equal() throws Throwable {

    for (Tag key : tagClones.keySet()) {
      TagRule tagRuleSelected = databaseUtils.getTagRuleFromTagPid(key.getPid());
      TagRule tagRuleReturned = databaseUtils.getTagRuleFromTagPid(tagClones.get(key).getPid());
      if (tagRuleSelected != null && tagRuleSelected != null) {
        assertTrue(
            tagRuleSelected.compareClones(tagRuleReturned),
            "Selected tag rule for tag ID "
                + key.getPid()
                + " does not match with returned tag rule with tag ID"
                + tagClones.get(key).getPid());
      }
    }
  }

  @When("^the selected and returned tag tiers are equal$")
  public void the_selected_and_returned_tag_tiers_are_equal() throws Throwable {

    for (Tag key : tagClones.keySet()) {
      Tier tierSelected = databaseUtils.getTierTagFromTag(key);
      Tier tierReturned = databaseUtils.getTierTagFromTag(tagClones.get(key));
      if (tierSelected != null && tierReturned != null) {
        assertTrue(
            tierSelected.compareClones(tierReturned),
            "Selected tag rule for tag ID"
                + key
                + " does not match with returned tag rule with tag ID"
                + tagClones.get(key));
      }
    }
  }
}
