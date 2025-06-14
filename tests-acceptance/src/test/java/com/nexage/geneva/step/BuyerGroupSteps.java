package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.BuyerGroupRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class BuyerGroupSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private BuyerGroupRequests buyerGroupRequests;
  @Autowired private DatabaseUtils databaseUtils;

  @When("^the user creates buyer group from the json file \"([^\"]*)\"$")
  public void the_user_creates_buyer_group_from_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getCreateBuyerGroupRequest().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates buyer group using V1 api from the json file \"([^\"]*)\"$")
  public void the_user_creates_buyer_group_using_V1_api_from_the_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getCreateBuyerGroupV1Request().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates buyer group with pid \"([^\"]*)\" using V1 api from the json file \"([^\"]*)\"$")
  public void the_user_updates_buyer_group_with_pid_using_V1_api_from_the_json_file(
      String pid, String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setBuyerGroupPid(pid)
            .getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getUpdateBuyerGroupV1Request().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets one buyer group using V1 api with pid \"(.*?)\"$")
  public void the_user_gets_one_buyer_group_using_V1_api_with_pid(String pid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setBuyerGroupPid(pid).getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getOneBuyerGroupV1Request().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all buyer groups$")
  public void the_user_gets_all_buyer_groups() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getGetAllBuyerGroupsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all buyer groups for a buyer \"([^\"]*)\"$")
  public void the_user_gets_all_buyer_groups(String buyer) throws Throwable {
    commonSteps.requestMap = new RequestParams().setCompanyPid(buyer).getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getGetAllBuyerGroupsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates buyer group named \"([^\"]*)\" using the json file \"([^\"]*)\"$")
  public void the_user_updates_a_BuyerGroup_from_the_json_file(
      String buyerGroupName, String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    String buyerGroupPid = databaseUtils.getBuyerGroupPid(buyerGroupName, commonSteps.companyPid);

    sendUpdateGroupRequest(buyerGroupPid, payload);
  }

  @When("^the user attempts to send a buyer group update request with the json file \"([^\"]*)\"$")
  public void the_user_attempts_an_update_with_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);

    String dummyBuyerGroupPid = "1";

    sendUpdateGroupRequest(dummyBuyerGroupPid, payload);
  }

  private void sendUpdateGroupRequest(String buyerGroupPid, JSONObject payload) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setBuyerGroupPid(buyerGroupPid)
            .getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getUpdateBuyerGroupRequest().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates buyer group named \"([^\"]*)\" using companyPid \"([^\"]*)\" and the json file \"([^\"]*)\"$")
  public void the_user_updates_a_BuyerGroup_with_another_companyPid(
      String buyerGroupName, String anotherCompanyPid, String filename) throws Throwable {
    assertFalse(
        Objects.equals(commonSteps.companyPid, anotherCompanyPid),
        "For this test the provided companyPid should be different from the user selected companyPid");

    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    String buyerGroupPid = databaseUtils.getBuyerGroupPid(buyerGroupName, commonSteps.companyPid);

    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(anotherCompanyPid)
            .setBuyerGroupPid(buyerGroupPid)
            .getRequestParams();
    commonSteps.request =
        buyerGroupRequests.getUpdateBuyerGroupRequest().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }
}
