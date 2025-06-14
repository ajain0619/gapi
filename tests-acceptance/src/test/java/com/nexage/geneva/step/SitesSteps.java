package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SitesRequest;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class SitesSteps {
  @Autowired private SitesRequest sitesRequest;

  @Autowired private CommonSteps commonSteps;

  @Autowired private CommonFunctions commonFunctions;

  private String queryField;
  private String queryFieldOperator;

  private Map<String, String> requestMap;

  @When("^the seller pid is passed in to grab \"(.+?)\" sites$")
  public void the_seller_pid_is_passed_in_to_grab_all_sites(String size) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSize(size)
            .getRequestParams();
    commonSteps.request =
        sitesRequest.getSellersSitesRequestWithQueryTerms().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the seller pid is passed in to get all sites with fetch field value \"(.+?)\"$")
  public void the_seller_pid_is_passed_in_to_get_all_sites_with_fetch_value(String fetch)
      throws Throwable {
    requestMap =
        new RequestParams().setPublisherPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        sitesRequest.getSellersSitesRequestWithFetchParam(fetch).setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^grab the second page of \"(.+?)\" sites \"(.+?)\" page$")
  public void grab_the_second_page(String size, String page) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSize(size)
            .setPage(page)
            .getRequestParams();
    commonSteps.request =
        sitesRequest.getSellersSitesRequestWithQueryTerms().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^make a request to read this company's sites$")
  public void read_companys_sites() throws Throwable {
    read_companys_sites(commonSteps.getCompany().getPid());
  }

  @When("^make a request to read sites for the company with company pid \"(.{1,20})\"$")
  public void read_companys_sites(final String companyPid) throws Throwable {
    requestMap = new RequestParams().setPublisherPid(companyPid).getRequestParams();
    commonSteps.request = sitesRequest.getSellersSitesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^make a request to read this company's sites with the query term \"(.+)\"$")
  public void read_companys_sites_with_query_term(final String queryTerm) throws Throwable {
    read_companys_sites_with_query_term(commonSteps.getCompany().getPid(), queryTerm);
  }

  @When(
      "^make a request to read sites for the company with company pid \"(.{1,20})\" and the query term \"(.+)\"$")
  public void read_companys_sites_with_query_term(final String companyPid, final String queryTerm)
      throws Throwable {
    requestMap =
        new RequestParams().setPublisherPid(companyPid).setqt(queryTerm).getRequestParams();
    commonSteps.request =
        sitesRequest.getSellersSitesRequestWithQueryTerms().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^a request is made to list the sites for this company and site type \"(.+)\" and status \"(.+)\"$")
  public void read_company_sites_with_filter_terms(final String siteType, final String status)
      throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setSiteType(siteType)
            .setStatus(status)
            .getRequestParams();
    commonSteps.request =
        sitesRequest.getSellersSitesRequestWithSiteFilters().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^get sites of sellers \"(.+)\" by \"(.+)\" with size \"(.+)\" and page \"(.+)\"$")
  public void get_sites_of_sellers_with_size_and_page(
      final String sellerIds, final String qf, final String size, final String page)
      throws Throwable {
    requestMap =
        new RequestParams()
            .setqf(qf)
            .setqt(sellerIds)
            .setSize(size)
            .setPage(page)
            .getRequestParams();
    commonSteps.request =
        sitesRequest.getSellersSitesRequestForSeats().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user is searching for sites with query field \"([^\"]+)\" and field operator \"(.+?)\"$")
  public void the_user_is_searching_for_sites_with_query_field_and_operator(
      String queryField, String queryFieldOperator) {
    assertNotNull(queryField, "Query field is null");
    assertNotNull(queryFieldOperator, "Query field operator is null");
    assertTrue(
        queryFieldOperator.matches("(?i:and|or)"),
        "Query field operator must match 'and' or 'or' ignoring case");
    this.queryField = queryField;
    this.queryFieldOperator = queryFieldOperator;
  }

  @When("^sites were retrieved using query field criteria$")
  public void sites_were_retrieved() throws Throwable {
    requestMap = new RequestParams().setqf(queryField).setqo(queryFieldOperator).getRequestParams();
    commonSteps.request = sitesRequest.getSitesWithMultiSearch().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^Get page \"(.+?)\" containing \"(.+?)\" sites summaries from between \"(.+?)\" and \"(.+?)\"$")
  public void getPlacementsSumaries(String page, String size, String startDate, String endDate)
      throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setPage(page)
            .setSize(size)
            .setStartDate(startDate)
            .setStopDate(endDate)
            .getRequestParams();
    commonSteps.request = sitesRequest.getSitesSummariesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^Get page \"(.+?)\" containing \"(.+?)\" sites summaries by site name \"(.+?)\" and from between \"(.+?)\" and \"(.+?)\"$")
  public void getPlacementsSumariesBySiteName(
      String page, String size, String name, String startDate, String endDate) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setPage(page)
            .setSize(size)
            .setStartDate(startDate)
            .setStopDate(endDate)
            .setName(name)
            .getRequestParams();
    commonSteps.request = sitesRequest.getSitesSummariesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^Get page \"(.+?)\" containing \"(.+?)\" sites summaries by site pids \"(.+?)\" and from between \"(.+?)\" and \"(.+?)\"$")
  public void getPlacementsSumariesBySitePids(
      String page, String size, String pids, String startDate, String endDate) throws Throwable {
    requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.getCompany().getPid())
            .setPage(page)
            .setSize(size)
            .setStartDate(startDate)
            .setStopDate(endDate)
            .setPids(pids)
            .getRequestParams();
    commonSteps.request = sitesRequest.getSitesSummariesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^returned sites data matches the following json file \"(.+?)\"$")
  public void returned_sites_data_matches_the_following_json_file(String filename)
      throws Throwable {
    String expectedJson = TestUtils.getResourceAsString(filename);
    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request, commonSteps.serverResponse, "site", expectedJson);
  }

  @When("^get sites for seller seat ID \"(.+)\"$")
  public void get_sites_for_seller_seat_pid(final String sellerSeatPid) throws Throwable {
    requestMap =
        new RequestParams()
            .setSellerSeatPid(sellerSeatPid)
            .setSize("10")
            .setPage("0")
            .getRequestParams();
    commonSteps.request = sitesRequest.getSitesForSellerSeatRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
