package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.ReportingApiRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.reportingapi.ReportDrillDown;
import com.nexage.geneva.util.reportingapi.ReportFilter;
import com.nexage.geneva.util.reportingapi.ReportType;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class ReportingApiSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private ReportingApiRequests reportingApiRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private ReportType report;
  private String dateFrom, dateTo;
  private String selectedCompanyId;

  private StringBuilder filterQuery = new StringBuilder();
  private StringBuilder drillDownQuery = new StringBuilder();
  private JSONObject updatedReportingApiCredentials;
  private String hashValueOrig;

  @Given("^the company \"(.+?)\" is selected$")
  public void the_company_is_selected(String companyId) throws Throwable {
    this.selectedCompanyId = companyId;
  }

  @Given("^the company authenticated using access key \"(.+?)\" and secret key \"(.+?)\"$")
  public void the_company_authenticated_using_access_key_and_secret_key(
      String accessKey, String secretKey) throws Throwable {
    commonSteps.requestMap = new RequestParams().setCompanyId(selectedCompanyId).getRequestParams();
    commonSteps.request =
        reportingApiRequests.getGetAuthenticationRequest().setRequestParams(commonSteps.requestMap);
    commonSteps.request.authenticate(accessKey, secretKey);
  }

  @When("^the original secret key hash value is retrieved")
  public void the_original_secret_key_value_is_retrieved() throws Throwable {
    hashValueOrig = databaseUtils.getSecretKeyHash(commonSteps.companyPid);
  }

  @Then("^secret key hash should not be changed")
  public void secret_key_hash_should_not_be_changed() throws Throwable {
    String updatedHash = databaseUtils.getSecretKeyHash(commonSteps.companyPid);
    assertTrue(hashValueOrig.equals(updatedHash), "hash value was changed");
  }

  @Given("^\"(.+?)\" report is selected for the date range from \"(.+?)\" to \"(.+?)\"$")
  public void report_is_selected_for_date_range_from_to(
      String reportName, String dateFrom, String dateTo) throws Throwable {
    setDateInterval(dateFrom, dateTo);
    selectReport(reportName);
  }

  @When("^report is drilled down by \"([^\"]+)\"$")
  public void report_is_drilled_down_by(String dimension) throws Throwable {
    drillDownReportBy(dimension);
    retrieveReport();
  }

  @When("^report is drilled down not ignore bot and fraud by \"([^\"]+)\"$")
  public void report_is_drilled_down_notignore_botfraud_by(String dimension) throws Throwable {
    drillDownReportBy(dimension);
    retrieveNotIgnoreBotFraudReport();
  }

  @When("^report is drilled down by \"([^\"]+)\" and filtered by \"(.*?)\"$")
  public void report_is_drilled_down_by_and_filtered_by(String dimension, String filter)
      throws Throwable {
    drillDownReportBy(dimension);
    filterReportBy(filter);
    retrieveReport();
  }

  @When(
      "^report is drilled down not ignore bot and fraud by \"([^\"]+)\" and filtered by \"(.*?)\"$")
  public void report_is_drilled_down_notignore_botfraud_by_and_filtered_by(
      String dimension, String filter) throws Throwable {
    drillDownReportBy(dimension);
    filterReportBy(filter);
    retrieveNotIgnoreBotFraudReport();
  }

  @When("^the report is retrieved$")
  public void the_report_is_retrieved() throws Throwable {
    retrieveReport();
  }

  @When("^authentication test result is retrieved")
  public void authentication_test_result_is_retrieved() throws Throwable {
    commonSteps.requestMap = new RequestParams().setCompanyId(selectedCompanyId).getRequestParams();
    commonSteps.request =
        reportingApiRequests
            .getGetAuthenticationTestRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Before("@disableReportingApiBefore")
  @Given("^reporting API is disabled$")
  public void reporting_api_is_disabled() {
    databaseUtils.setReportingApiAccess(false);
  }

  @After("@enableReportingApiAfter")
  @Given("^reporting API is enabled$")
  public void reporting_api_is_enabled() {
    databaseUtils.setReportingApiAccess(true);
  }

  private void setDateInterval(String dateFrom, String dateTo) {
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  private void selectReport(String reportName) {
    this.report = ReportType.getReportType(reportName);
    assertNotNull(report, String.format("report name [%s] is not valid", reportName));
  }

  private void filterReportBy(String filter) {
    Map<String, String> queryParams = TestUtils.splitQuery(filter);
    for (String key : queryParams.keySet()) {
      ReportFilter reportFilter = ReportFilter.getReportFilter(key);
      assertNotNull(reportFilter, String.format("filter [%s] is not a valid filter value", key));

      String value = queryParams.get(key);
      setRequestParameterValue(filterQuery, reportFilter.getRequestParameter(), value);
    }
  }

  private void drillDownReportBy(String dimension) {
    if (!dimension.contains(ReportDrillDown.NOTHING.getName())) {
      ReportDrillDown reportDrillDown = ReportDrillDown.getReportDrillDown(dimension);
      assertNotNull(reportDrillDown, String.format("drill down [%s] not a valid value", dimension));

      setRequestParameterValue(
          drillDownQuery,
          reportDrillDown.getDrillDownUrlPart(),
          reportDrillDown.getRequestParameter());
    }
  }

  private void retrieveReport() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(selectedCompanyId)
            .setDateFrom(dateFrom)
            .setDateTo(dateTo)
            .setReportId(report.getRequestParam())
            .setReportDrilldown(String.valueOf(drillDownQuery))
            .setReportFilter(String.valueOf(filterQuery))
            .getRequestParams();
    commonSteps.request =
        reportingApiRequests.getGetReportApiRequest().setRequestParams(commonSteps.requestMap);

    commonFunctions.executeRequest(commonSteps);
  }

  private void retrieveNotIgnoreBotFraudReport() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyId(selectedCompanyId)
            .setDateFrom(dateFrom)
            .setDateTo(dateTo)
            .setReportId(report.getRequestParam())
            .setReportDrilldown(String.valueOf(drillDownQuery))
            .setReportFilter(String.valueOf(filterQuery))
            .getRequestParams();
    commonSteps.request =
        reportingApiRequests
            .getGetReportApiNotIgnoreBotFraudRequest()
            .setRequestParams(commonSteps.requestMap);

    commonFunctions.executeRequest(commonSteps);
  }

  private void setRequestParameterValue(StringBuilder queryString, String parameter, String value) {
    queryString.append("&").append(parameter).append("=").append(value);
  }
}
