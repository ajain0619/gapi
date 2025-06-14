package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.KpiBoxesAndChartingRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class KpiBoxesAndChartingSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private KpiBoxesAndChartingRequests kpiBoxesAndChartingRequests;
  @Autowired private DatabaseUtils databaseUtils;
  @Autowired private AccountServiceSteps accountServiceSteps;

  private String interval = "";

  @When("^the chart interval is \"(.*?)\"$")
  public void the_chart_interval_is(String interval) throws Throwable {
    this.interval = interval;
  }

  @When("^the user retrieves publisher complete metrics$")
  public void the_user_retrieves_publisher_complete_metrics() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo)
            .getRequestParams();
    commonSteps.request =
        kpiBoxesAndChartingRequests
            .getGetPublisherCompleteMetricsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user retrieves publisher summary graph metrics$")
  public void the_user_retrieves_publisher_summary_graph_metrics() throws Throwable {
    RequestParams requestParams =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setDateFrom(commonSteps.dateFrom)
            .setDateTo(commonSteps.dateTo);
    if (!StringUtils.isEmpty(interval)) {
      requestParams.setInterval(interval);
    }
    commonSteps.requestMap = requestParams.getRequestParams();
    commonSteps.request =
        kpiBoxesAndChartingRequests
            .getGetPublisherSummaryGraphMetricsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
