package com.nexage.geneva.step;

import com.nexage.geneva.request.EstimatedRevenueRequests;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class estimatedRevenueSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private EstimatedRevenueRequests estimatedRevenueRequests;

  private Request currentRequest;
  // private JSONObject expectedRevenue;

  @When("^the user selects estimated report for seller")
  public void the_user_selects_estimated_report_for_seller() throws Throwable {

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        estimatedRevenueRequests.getEstimatedRevenue().setRequestParams(commonSteps.requestMap);

    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects estimated report with adnet drilldown")
  public void the_user_selects_estimated_report_with_adnet_drilldown() throws Throwable {

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        estimatedRevenueRequests
            .getEstimatedRevenueAdnetDrillDown()
            .setRequestParams(commonSteps.requestMap);

    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects estimated report with advertiser drilldown")
  public void the_user_selects_estimated_report_with_advertiser_drilldown() throws Throwable {

    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        estimatedRevenueRequests
            .getEstimatedRevenueAdvertiserDrillDown()
            .setRequestParams(commonSteps.requestMap);

    commonFunctions.executeRequest(commonSteps);
  }
}
