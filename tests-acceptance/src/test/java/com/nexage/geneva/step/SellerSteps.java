package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SellerRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class SellerSteps {
  @Autowired private SellerRequests sellerRequests;

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  private Map<String, String> requestMap;

  @When("^the user gets seller with pid \"([0-9]+)\"$")
  public void getSeller(String sellerPid) throws Throwable {
    requestMap = new RequestParams().setSellerPid(sellerPid).getRequestParams();
    commonSteps.request = sellerRequests.getSellerByPidRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^Get page \"(.+?)\" containing \"(.+?)\" sellers by seller pid \"(.+?)\"$")
  public void getSellersBySellerPid(String page, String size, String pid) throws Throwable {
    requestMap =
        new RequestParams().setPage(page).setSize(size).setqf("pid").setqt(pid).getRequestParams();
    commonSteps.request = sellerRequests.getSellersByPidRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^Get page \"(.+?)\" containing \"(.+?)\" seller summaries by seller name \"(.+?)\" and from between \"(.+?)\" and \"(.+?)\"$")
  public void getPlacementsSumariesBySitePids(
      String page, String size, String name, String startDate, String endDate) throws Throwable {
    requestMap =
        new RequestParams()
            .setPage(page)
            .setSize(size)
            .setqf("name")
            .setqt(name)
            .setStartDate(startDate)
            .setStopDate(endDate)
            .getRequestParams();
    commonSteps.request = sellerRequests.getSellerSummariesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
