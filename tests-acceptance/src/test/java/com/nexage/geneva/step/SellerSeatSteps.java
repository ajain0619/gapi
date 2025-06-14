package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SellerSeatRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class SellerSeatSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SellerSeatRequests sellerSeatRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private String sellerSeatRulePid;
  private final String nonExistingRulePid = "100000000";
  private JSONObject expectedJsonObject;

  @When("^the user searches for all seller seats$")
  public void the_user_searches_for_all_seller_seats() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        sellerSeatRequests.getGetAllSellerSeatsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user create a seller seat from the json file \"(.+?)\"$")
  public void the_user_create_a_seller_seat_from_the_json_file(String filename) throws Throwable {
    expectedJsonObject = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        sellerSeatRequests.getCreateSellerSeatRequest().setRequestPayload(expectedJsonObject);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for seller seat by pid \"(.+?)\"$")
  public void the_user_searches_for_seller_seat_by_pid(String pid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setSellerSeatPid(pid).getRequestParams();
    commonSteps.request =
        sellerSeatRequests.getGetSellerSeatRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for non existing seller seat$")
  public void the_user_searches_for_non_existing_seller_seat() throws Throwable {
    String nonExistingPid = "0";
    commonSteps.requestMap =
        new RequestParams().setSellerSeatPid(nonExistingPid).getRequestParams();
    commonSteps.request =
        sellerSeatRequests.getGetSellerSeatRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates a seller seat with pid \"(.+?)\" providing the json file \"(.+?)\"$")
  public void the_user_updates_a_seller_seat_with_pid_providing_the_json_file(
      String pid, String filename) throws Throwable {
    expectedJsonObject = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setSellerSeatPid(pid).getRequestParams();
    commonSteps.request =
        sellerSeatRequests
            .getUpdateSellerSeatRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedJsonObject);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user creates seller seat rule for seller seat \"([^\"]*)\" from the json file \"([^\"]*)\"$")
  public void theUserCreatesSellerSeatRuleForSellerSeatFromTheJsonFile(String pid, String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setSellerSeatPid(pid).getRequestParams();
    commonSteps.request =
        sellerSeatRequests
            .getCreateSellerSeatRuleRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for all seller seat rules in seller seat \"([^\"]+)\"$")
  public void the_user_retrieves_all_sellerseat_rules(String pid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setSellerSeatPid(pid).getRequestParams();
    commonSteps.request =
        sellerSeatRequests
            .getGetAllSellerSeatRulesRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user searches for all seller seat rules in seller seat \"([^\"]+)\" with query field \"([^\"]+)\" and term \"([^\"]+)\"$")
  public void the_user_retrieves_all_sellerseat_rules_with_search(String pid, String qf, String qt)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSellerSeatPid(pid).setqf(qf).setqt(qt).getRequestParams();
    commonSteps.request =
        sellerSeatRequests
            .getGetAllSellerSeatRulesWithSearchRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates the rule for seller seat \"([^\"]*)\" from the json file \"([^\"]*)\"$")
  public void theUserUpdatesTheRuleForSellerSeatFromTheJsonFile(String pid, String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSellerSeatPid(pid)
            .setSellerSeatRulePid(sellerSeatRulePid)
            .getRequestParams();
    commonSteps.request =
        sellerSeatRequests
            .getUpdateSellerSeatRuleRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user sets non-existing seller seat rule pid$")
  public void set_non_existing_rule_pid() {
    sellerSeatRulePid = nonExistingRulePid;
  }

  @When("^the user finds seller seat rule pid for name \"(.+?)\"$")
  public void the_user_finds_rule_pid_for_name(String ruleName) throws Throwable {
    sellerSeatRulePid = databaseUtils.getRulePidByName(ruleName);
    assertNotNull("Seller seat rule pid is null", sellerSeatRulePid);
  }

  @When("^the user fetches seller seat rule with \"(\\d+)\" in seller seat \"(\\d+)\"$")
  public void the_user_fetches_seller_seat_rule_by_pid(String ssrPid, String ssPid)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setSellerSeatPid(ssPid).setSellerSeatRulePid(ssrPid).getRequestParams();
    commonSteps.request =
        sellerSeatRequests.getGetSellerSeatRule().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^Get page \"(.+?)\" containing \"(.+?)\" seller summaries for seller seat \"(.+?)\" from between \"(.+?)\" and \"(.+?)\"$")
  public void getSellerSeatSummaries(
      String page, String size, String sellerSeatPid, String startDate, String endDate)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPage(page)
            .setSize(size)
            .setSellerSeatPid(sellerSeatPid)
            .setStartDate(startDate)
            .setStopDate(endDate)
            .getRequestParams();
    commonSteps.request =
        sellerSeatRequests.getSellerSeatSummariesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user searches all seller seats with query field name and query term \"([^\"]+)\" and assignable \"(.+?)\"$")
  public void the_user_searches_all_seller_seats_for_name_containing_a_string(
      String qt, String assignable) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setqt(qt).setAssignable(assignable).getRequestParams();
    commonSteps.request =
        sellerSeatRequests
            .getSellerSeatsByQueryFieldNameAndQueryTermContainingStringRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
