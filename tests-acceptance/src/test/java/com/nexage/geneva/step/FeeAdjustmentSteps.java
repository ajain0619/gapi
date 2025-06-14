package com.nexage.geneva.step;

import com.nexage.geneva.request.FeeAdjustmentRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class FeeAdjustmentSteps {

  @Autowired private CommonFunctions commonFunctions;
  @Autowired private CommonSteps commonSteps;
  @Autowired private FeeAdjustmentRequests feeAdjustmentRequests;

  @When("^the user creates fee adjustment from the json file \"(.+?)\"$")
  public void the_user_creates_fee_adjustment_from_the_json_file(String filename) throws Throwable {
    commonSteps.request =
        feeAdjustmentRequests
            .getCreateFeeAdjustmentRequest()
            .setRequestPayload(JsonHandler.getJsonObjectFromFile(filename));
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates fee adjustment with pid \"(.*?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_fee_adjustment_from_the_json_file(String pid, String filename)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setFeeAdjustmentPid(pid).getRequestParams();
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        feeAdjustmentRequests
            .getUpdateFeeAdjustmentRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets fee adjustment with pid \"(.*?)\"$")
  public void the_user_gets_fee_adjustment_with_pid(String pid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setFeeAdjustmentPid(pid).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests.getGetFeeAdjustmentRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all fee adjustments$")
  public void the_user_gets_all_fee_adjustments() throws Throwable {
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets all fee adjustments both matching qf \"(.+?)\" with qt \"(.+?)\" with enabled \"(.+?)\" and in page \"(.+?)\" with size \"(.+?)\"$")
  public void the_user_gets_all_fee_adjustments_of_page_with_size_matching_qf_with_qt_with_enabled(
      String qf, String qt, String enabled, String page, String size) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPage(page)
            .setSize(size)
            .setqf(qf)
            .setqt(qt)
            .setFeeAdjustmentEnabled(enabled)
            .getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllPagedQfQtEnabledFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets all fee adjustments both matching qf \"(.+?)\" with qt \"(.+?)\" and in page \"(.+?)\" with size \"(.+?)\"$")
  public void the_user_gets_all_fee_adjustments_of_page_with_size_matching_qf_with_qt(
      String qf, String qt, String page, String size) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPage(page).setSize(size).setqf(qf).setqt(qt).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllPagedQfQtFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all fee adjustments of page \"(.+?)\" with size \"(.+?)\"$")
  public void the_user_gets_all_fee_adjustments_of_page_with_size(String page, String size)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setPage(page).setSize(size).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllPagedFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets all fee adjustments matching qf \"(.+?)\" with qt \"(.+?)\" with enabled \"(.+?)\"$")
  public void the_user_gets_all_fee_adjustments_matching_qf_with_qt_with_enabled(
      String qf, String qt, String enabled) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setqf(qf).setqt(qt).setFeeAdjustmentEnabled(enabled).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllQfQtEnabledFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all fee adjustments matching qf \"(.+?)\" with qt \"(.+?)\"$")
  public void the_user_gets_all_fee_adjustments_matching_qf_with_qt(String qf, String qt)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setqf(qf).setqt(qt).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllQfQtFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all fee adjustments matching enabled \"(.+?)\"$")
  public void the_user_gets_all_fee_adjustments_matching_enabled(String enabled) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setFeeAdjustmentEnabled(enabled).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getGetAllEnabledFeeAdjustmentsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes fee adjustment with pid \"(.*?)\"$")
  public void the_user_deletes_fee_adjustment_with_pid(String pid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setFeeAdjustmentPid(pid).getRequestParams();
    commonSteps.request =
        feeAdjustmentRequests
            .getDeleteFeeAdjustmentRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
