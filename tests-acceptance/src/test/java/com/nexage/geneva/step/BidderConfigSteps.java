package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.BidderConfig;
import com.nexage.geneva.request.BidderConfigRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import us.monoid.json.JSONObject;

public class BidderConfigSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private BidderConfigRequests bidderConfigRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private String firstBidderConfigPid;

  private static final String ANY_COMPANY_PID = "0000";
  private static final String ANY_BIDDER_CONFIG_PID = "000";

  @When("^the user gets all bidder configs$")
  public void the_user_gets_all_bidder_configs() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setBuyerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getGetAllBidderConfigsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets first bidder config$")
  public void the_user_gets_first_bidder_config() throws Throwable {
    firstBidderConfigPid = getFirstBidderConfigPid();
    assertFalse(
        StringUtils.isEmpty(firstBidderConfigPid), "First bidder config cannot be selected");

    getFirstBidderConfigData();
  }

  @When("^the user gets bidder config with id \"(.*?)\"$")
  public void the_user_gets_bidder_config(String bidderId) throws Throwable {
    String bidderPid =
        String.valueOf(databaseUtils.getBidderConfigCookieSyncParamaters(bidderId).getPid());
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(commonSteps.companyPid)
            .setBidderConfigPid(bidderPid)
            .getRequestParams();
    commonSteps.request =
        bidderConfigRequests.getGetBidderConfigRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets bidder config summaries with companyPid \"(.*?)\" and qf \"(.*?)\" and qt \"(.*?)\" and page \"(.*?)\" and size \"(.*?)\"$")
  public void the_user_gets_bidder_config_summaries_with_companyPid(
      String companyPid, String qf, String qt, String page, String size) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(companyPid)
            .setBuyerPid(companyPid)
            .setqf(qf)
            .setqt(qt)
            .setPage(page)
            .setSize(size)
            .getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getGetBidderConfigSummariesRequestWithSearchTermsAndPagination()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates bidder config from the json file \"(.*?)\"$")
  public void the_user_creates_bidder_config_from_json_file(String filename) throws Throwable {
    JSONObject expectedBidderConfig = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setBuyerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getCreateBidderConfigRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedBidderConfig);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates bidder config using null payload$")
  public void the_user_creates_bidder_config_using_null_payload() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setBuyerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getCreateBidderConfigRequestNullPayload()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates first bidder config from the json file \"(.*?)\"$")
  public void the_user_updates_the_bidder_config_from_json_file(String filename) throws Throwable {
    JSONObject expectedBidderConfig = JsonHandler.getJsonObjectFromFile(filename);
    firstBidderConfigPid = getFirstBidderConfigPid();
    assertFalse(
        StringUtils.isEmpty(firstBidderConfigPid), "First bidder config cannot be selected");

    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(commonSteps.companyPid)
            .setBidderConfigPid(firstBidderConfigPid)
            .getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getUpdateBidderConfigRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedBidderConfig);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes first bidder config$")
  public void the_user_deletes_first_bidder_config() throws Throwable {
    firstBidderConfigPid = getFirstBidderConfigPid();
    assertFalse(
        StringUtils.isEmpty(firstBidderConfigPid), "First bidder config cannot be selected");

    commonSteps.requestMap =
        new RequestParams().setBidderConfigPid(firstBidderConfigPid).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getDeleteBidderConfigRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^deleted bidder config cannot be searched out$")
  public void deleted_bidder_config_cannot_be_searched_out() throws Throwable {
    getFirstBidderConfigData();

    assertNotNull("RTB Profile library can be searched out.", commonSteps.exceptionMessage);
  }

  @When("^the user gets list of publishers$")
  public void the_user_gets_list_of_publishers() throws Throwable {
    commonSteps.request = bidderConfigRequests.getGetListOfPublishersRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets list of publisher sites$")
  public void the_user_gets_list_of_publisher_sites() throws Throwable {
    commonSteps.request = bidderConfigRequests.getGetListOfPublisherSitesRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets list of bidder id to name mappings$")
  public void the_user_gets_list_of_bidder_id_to_name_mappings() throws Throwable {
    commonSteps.request = bidderConfigRequests.getGetListOfIdNameMappingsRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets list of ad sizes$")
  public void the_user_gets_list_of_ad_sizes() throws Throwable {
    commonSteps.request = bidderConfigRequests.getGetListOfAdSizes();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to get all bidder configs$")
  public void the_user_tries_to_get_all_bidder_configs() throws Throwable {
    commonSteps.requestMap = new RequestParams().setBuyerPid(ANY_COMPANY_PID).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getGetAllBidderConfigsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to get any bidder configs$")
  public void the_user_tries_to_get_any_bidder_configs() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(ANY_COMPANY_PID)
            .setBidderConfigPid(ANY_BIDDER_CONFIG_PID)
            .getRequestParams();
    commonSteps.request =
        bidderConfigRequests.getGetBidderConfigRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to create bidder config from the json file \"(.*?)\"$")
  public void the_user_tries_to_create_bidder_config(String filename) throws Throwable {
    JSONObject expectedBidderConfig = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setBuyerPid(ANY_COMPANY_PID).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getCreateBidderConfigRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedBidderConfig);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to update bidder config from the json file \"(.*?)\"$")
  public void the_user_tries_to_update_bidder_config_from_json_file(String filename)
      throws Throwable {
    JSONObject expectedBidderConfig = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(ANY_COMPANY_PID)
            .setBidderConfigPid(ANY_BIDDER_CONFIG_PID)
            .getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getUpdateBidderConfigRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedBidderConfig);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to get list of publishers$")
  public void the_user_tries_to_get_list_of_publishers() throws Throwable {
    commonSteps.request = bidderConfigRequests.getGetListOfPublishersRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to get list of publisher sites$")
  public void the_user_tries_to_get_list_of_publisher_sites() throws Throwable {
    commonSteps.request = bidderConfigRequests.getGetListOfPublisherSitesRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @Then(
      "^the response parameters matches cookie sync parameters in the database for the bidder id \"(.*?)\"$")
  public void verifyResponsCookieParametersWithDBbidderValues(String bidderId) throws Throwable {
    BidderConfig bidderCookieConfig = databaseUtils.getBidderConfigCookieSyncParamaters(bidderId);
    BidderConfig bidderCookieConfigAudit =
        databaseUtils.getBidderConfigCookieSyncAuditParamaters(bidderId);
    assertEquals(
        bidderCookieConfig.getId(), commonSteps.serverResponse.toObject().get("id"), "Bidder id");
    assertEquals(
        bidderCookieConfigAudit.getId(),
        commonSteps.serverResponse.toObject().get("id"),
        "Bidder id");
    assertEquals(
        bidderCookieConfig.getPid(),
        commonSteps.serverResponse.toObject().get("pid"),
        "Bidder pid");
    assertEquals(
        bidderCookieConfigAudit.getPid(),
        commonSteps.serverResponse.toObject().get("pid"),
        "Bidder pid");

    if (bidderCookieConfig.getBidrequestUseridPreference() == 0)
      assertEquals(
          "NO_ID_RESTRICTION",
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder cookie user id preference");
    else if (bidderCookieConfig.getBidrequestUseridPreference() == 1)
      assertEquals(
          "DEVICE_ID",
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder cookie user id preference");
    else if (bidderCookieConfig.getBidrequestUseridPreference() == 2)
      assertEquals(
          "EXCHANGE_USER_ID_OR_DEVICE_ID",
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder cookie user id preference");
    else if (bidderCookieConfig.getBidrequestUseridPreference() == 3)
      assertEquals(
          "MATCHED_USER_ID_OR_DEVICE_ID",
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder cookie user id preference");
    else if (bidderCookieConfig.getBidrequestUseridPreference() == 4)
      assertEquals(
          "MATCHED_USER_ID",
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder cookie user id preference");
    else throw new Exception("Invalid user id preference value");
    assertEquals(
        bidderCookieConfig.getBidrequestUseridPreference(),
        bidderCookieConfigAudit.getBidrequestUseridPreference(),
        "Bidder cookie user id preference");
  }

  @Then("^the response parameters matches cookie sync parameters in the request$")
  public void verifyCookieSyncResponseParametersAgainstRequest() throws Throwable {
    assertEquals(
        ((JSONObject) commonSteps.request.getRequestPayload()).get("id"),
        commonSteps.serverResponse.toObject().get("id"),
        "Bidder id");
    if (((JSONObject) commonSteps.request.getRequestPayload())
        .get("userIdPreference")
        .equals(JSONObject.NULL))
      assertEquals(
          "NO_ID_RESTRICTION",
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder user id preference");
    else
      assertEquals(
          ((JSONObject) commonSteps.request.getRequestPayload()).get("userIdPreference"),
          commonSteps.serverResponse.toObject().get("userIdPreference"),
          "Bidder user id preference");
  }

  public String getFirstBidderConfigPid() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setBuyerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getGetAllBidderConfigsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      JSONObject firstPosition = commonSteps.serverResponse.array().getJSONObject(0);
      return firstPosition.getString(JsonField.PID);
    }
    return "";
  }

  private void getFirstBidderConfigData() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setBuyerPid(commonSteps.companyPid)
            .setBidderConfigPid(firstBidderConfigPid)
            .getRequestParams();
    commonSteps.request =
        bidderConfigRequests.getGetBidderConfigRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all bidder configs for newly created company$")
  public void the_user_gets_all_the_bidder_cofigs_for_newly_created_company() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setBuyerPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        bidderConfigRequests
            .getGetAllBidderConfigsForNewCompany()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
