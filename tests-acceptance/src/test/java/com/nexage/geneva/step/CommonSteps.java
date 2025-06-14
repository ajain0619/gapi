package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Company;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.request.CompaniesRequests;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SiteRequests;
import com.nexage.geneva.response.ResponseCode;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.CsvUtils;
import com.nexage.geneva.util.ErrorHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.geneva.CompanyType;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

@Log4j2
public class CommonSteps {

  @Autowired public CommonFunctions commonFunctions;
  @Autowired public SiteRequests siteRequests;
  @Autowired public CompaniesRequests companiesRequests;
  @Autowired private CsvUtils csvUtils;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<String, Company> companyMap;
  private Map<String, Site> siteMap;
  public Map<String, String> requestMap = new HashMap<>();
  public Request request;
  private Company company;
  private Site site;
  public CompanyType companyType;
  public JSONResource serverResponse;
  public JSONObject selectedPosition;
  public String expectedPositionPid;
  public String expectedTagPid;
  public String encodedPositionPid;
  public String encodedTagPid;
  public JSONObject selectedTag;
  public String exceptionMessage = "", dateFrom, dateTo, companyPid;
  public String ETagSecond;
  public String ETag;
  public String rulePid;

  private String defaultO2PlayerId = "5bb26ab96614d13012e1fbc8";
  private String defaultO2PlaylistId = "5bb7a2fe1b8535000179657b";
  private String defaultYahooPlayListId = "818fdeb0-9885-11e5-afe5-6b9d1fd155d3";

  @Then("^nothing else to be done")
  public void do_nothing() {
    log.info("Nothing is done.");
  }

  @Given("^\"(.+?)\" companies are retrieved$")
  public void companies_are_retrieved(String type) throws Throwable {
    retrieveCompany(type);
  }

  @When("^\"(.+?)\" publisher is retrieved")
  public void publisher_is_retrieved(String pubId) throws Throwable {
    retrievePublisher(pubId);
  }

  @Given("^the user selects the \"(.+?)\" company \"(.+?)\"$")
  public void the_user_selects_the_company(String type, String companyName) throws Throwable {
    retrieveCompany(type);
    assertNotNull(companyMap, "No companies are returned");

    setCompany(companyMap.get(companyName));
    assertNotNull(getCompany(), String.format("Company [%s] is not found", companyName));

    companyPid = getCompany().getPid();
  }

  @Given("^the seller site is retrieved$")
  public void the_seller_site_is_retrieved() throws Throwable {
    requestMap = new RequestParams().setSitePid(getSite().getPid()).getRequestParams();
    request = siteRequests.getGetSiteAfterArchiveRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(this);
  }

  @Given("^the seller site summaries are retrieved$")
  public void the_seller_sites_summaries_are_retrieved() throws Throwable {
    retrieveSellerSiteSummaries();
  }

  @Given("^the user selects the site \"([^\"]+)\"$")
  public void the_user_selects_the_site(String siteName) throws Throwable {
    the_user_selects_the_site(siteName, companyPid);
  }

  @Given(
      "^the user selects the site with the site name \"(.+)\" and the company with the company pid \"(.{1,20})\"$")
  public void the_user_selects_the_site(String siteName, String companyPid) throws Throwable {
    if (!"All".equals(siteName)) {
      setSite(companyPid, siteName);
    } else {
      site = new Site();
      site.setPid(" ");
    }
  }

  @Given("^the user selects the position \"([^\"]+)\"$")
  public void the_user_selects_the_position(String position) throws Throwable {
    if (!"All".equals(position)) {
      setSite(position);
    } else {
      site = new Site();
      site.setPid(" ");
    }
  }

  @Given("^the user specifies the date range from \"([^\"]*)\" to \"([^\"]*)\"$")
  public void the_user_specifies_the_date_range_from_to(String dateFrom, String dateTo)
      throws Throwable {
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  @Given("^the test specifies the date range from \"([^\"]*)\" to \"([^\"]*)\"$")
  public void the_test_specify_the_date_range_from_to(String dateFrom, String dateTo)
      throws Throwable {
    LocalDate today = LocalDate.now();
    this.dateFrom = today.minusYears(1).toString().concat("T00:00:00-05:00");
    this.dateTo = today.plusDays(1).toString().concat("T00:00:00-05:00");
  }

  @Given("^the user specifies the date range as three days before today$")
  public void the_user_specifies_the_date_range_as_three_last_days() throws Throwable {
    LocalDate today = LocalDate.now();
    this.dateFrom = today.minusDays(3).toString().concat("T00:00:00-05:00");
    this.dateTo = today.minusDays(1).toString().concat("T00:00:00-05:00");
  }

  @Given("^the user specifies the date range as \"(.+?)\" last days$")
  public void the_user_specifies_the_date_range_as_few_last_days(Long days) throws Throwable {
    LocalDate today = LocalDate.now();
    this.dateFrom = today.minusDays(days).toString().concat("T00:00:00-05:00");
    this.dateTo = today.toString().concat("T00:00:00-05:00");
  }

  @When("^position with name \"(.+?)\" is selected")
  public void position_with_name_is_selected(String positionName) throws Throwable {
    requestMap = new RequestParams().setSitePid(getSite().getPid()).getRequestParams();
    request = siteRequests.getGetSiteRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(this);

    assertNotNull(serverResponse, "Error occurred while getting sites");

    JSONArray positions = serverResponse.object().getJSONArray(JsonField.POSITIONS);
    for (int i = 0; i < positions.length(); i++) {
      JSONObject position = positions.getJSONObject(i);
      if (position.getString(JsonField.NAME).equals(positionName)) {
        selectedPosition = position;
        break;
      }
    }
    assertNotNull(
        selectedPosition, String.format("Position with name [%s] doesn`t exist", positionName));
  }

  @When("^set position pid for name \"(.+?)\"$")
  public void set_position_pid_by_name(String name) throws Throwable {
    expectedPositionPid = databaseUtils.getPositionPidByName(name);
    encodedPositionPid = new String(Base64.encodeBase64(expectedPositionPid.getBytes()));
    assertNotNull("Position pid is null", encodedPositionPid);
  }

  @When("^set tag pid for name \"(.+?)\"$")
  public void set_tag_pid_by_name(String name) throws Throwable {
    expectedTagPid = databaseUtils.getTagPidByName(name);
    encodedTagPid = new String(Base64.encodeBase64(expectedTagPid.getBytes()));
    assertNotNull("Tag pid is null", encodedTagPid);
  }

  @When("^set non-existing tag pid \"(.+?)\"")
  public void set_non_existing_tag_pid(String nonExistingTagPid) {
    encodedTagPid = new String(Base64.encodeBase64(nonExistingTagPid.getBytes()));
  }

  @When("^tag with name \"(.+?)\" is selected")
  public void tag_with_name_is_selected(String tagName) throws Throwable {
    requestMap = new RequestParams().setSitePid(getSite().getPid()).getRequestParams();
    request = siteRequests.getGetSiteRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(this);

    assertNotNull(serverResponse, "Error occurred while getting sites");

    JSONArray tags = serverResponse.object().getJSONArray(JsonField.TAGS);
    for (int i = 0; i < tags.length(); i++) {
      JSONObject tag = tags.getJSONObject(i);
      if (tag.getString(JsonField.NAME).equals(tagName)) {
        selectedTag = tag;
        break;
      }
    }
    assertNotNull(selectedPosition, String.format("Tag with name [%s] doesn`t exist", tagName));
  }

  @Then("^request passed successfully$")
  public void request_passed_successfully() throws Throwable {
    ErrorHandler.assertNotEmpty(request, exceptionMessage);
    ResponseHandler.verifySuccessfulResponseCode(request, serverResponse, ResponseCode.OK);
  }

  @Then("^request passed successfully with code \"(\\d+)\"$")
  public void request_passed_successfully_with_code(int code) throws Throwable {
    ErrorHandler.assertNotEmpty(request, exceptionMessage);
    ResponseHandler.verifySuccessfulResponseCode(request, serverResponse, code);
  }

  @Then("^response does not contain the given data")
  public void tag_response_not_contains_tags(DataTable dataNames) throws Throwable {
    for (String dataName : dataNames.asList()) {
      assertFalse(
          serverResponse.object().toString().contains(dataName),
          "the given data: " + dataName + " is contained in the api response");
    }
  }

  @Then("^request passed with \"(.*?)\" response code$")
  public void request_passed_with_response_code(String code) throws Throwable {
    ErrorHandler.assertNotEmpty(request, exceptionMessage);
    ResponseHandler.verifySuccessfulResponseCode(request, serverResponse, Integer.parseInt(code));
  }

  @Then("^request passed without errors$")
  public void request_passed_without_errors() {
    ErrorHandler.assertNotEmpty(request, exceptionMessage);
  }

  @Then("^\"(.*?)\" failed with \"(.*?)\" response code$")
  public void failed_with_response_code(String message, String code) throws Throwable {
    if (StringUtils.isEmpty(exceptionMessage)) {
      ErrorHandler.assertNotEmpty(
          request, message + " passed but should fail with " + code + " response code");
    }
    ResponseHandler.verifyFailedResponseCode(request, message, exceptionMessage, code);
  }

  @Then("^redirect to authenticate page$")
  public void redirect_to_login_page() throws Throwable {
    if (StringUtils.hasText(exceptionMessage)) {
      ErrorHandler.assertNotEmpty(request, exceptionMessage);
    }
    ResponseHandler.verifyRedirectResponse(request, serverResponse);
  }

  @Then("^\"(.*?)\" failed with \"(.*?)\" response message$")
  public void failed_with_response_message(String message, String expectedErrorMessage)
      throws Throwable {
    if (StringUtils.isEmpty(exceptionMessage)) {
      ErrorHandler.assertNotEmpty(
          request, message + " passed but should fail with" + message + "response message");
    }
    ResponseHandler.verifyFailedResponseMessage(
        request, message, exceptionMessage, expectedErrorMessage);
  }

  @Then("^\"(.*?)\" failed with \"(.*?)\" response code and error message \"(.*?)\"$")
  public void failed_with_response_code(String message, String code, String errorMessage)
      throws Throwable {
    if (StringUtils.isEmpty(exceptionMessage)) {
      ErrorHandler.assertNotEmpty(
          request, message + " passed but should fail with" + message + "response message");
    }
    ResponseHandler.verifyFailedResponseCode(request, message, exceptionMessage, code);
    ResponseHandler.verifyFailedResponseMessage(request, message, exceptionMessage, errorMessage);
  }

  @Then(
      "^response failed with \"(.*?)\" response code, error message \"(.*?)\" and field errors \"(.*?)\"$")
  public void failed_code_message_field_errors(String code, String errorMessage, String fieldErrors)
      throws Throwable {
    if (StringUtils.isEmpty(exceptionMessage)) {
      ErrorHandler.assertNotEmpty(
          request, "request passed but should fail with" + errorMessage + "response message");
    }
    ResponseHandler.verifyFailedResponseCode(request, "request", exceptionMessage, code);
    ResponseHandler.verifyFailedResponseMessage(request, "request", exceptionMessage, errorMessage);
    ResponseHandler.verifyFailedFieldErrors(request, exceptionMessage, fieldErrors);
  }

  @Then("^response failed with \"(.*?)\" field error or with this one \"(.*?)\"$")
  public void failed_possible_field_errors(String firstFieldError, String secondFieldError) {
    ResponseHandler.verifyBothFieldErrors(
        request, exceptionMessage, firstFieldError, secondFieldError);
  }

  @Then(
      "^response failed with \"(.*?)\" response code, error message \"(.*?)\" and without field errors.$")
  public void failed_code_message_without_field_errors(String code, String errorMessage)
      throws Throwable {
    if (StringUtils.isEmpty(exceptionMessage)) {
      ErrorHandler.assertNotEmpty(
          request, "request passed but should fail with" + errorMessage + "response message");
    }
    ResponseHandler.verifyFailedResponseCode(request, "request", exceptionMessage, code);
    ResponseHandler.verifyFailedResponseMessage(request, "request", exceptionMessage, errorMessage);
    ResponseHandler.verifyEmptyFieldErrors(request, exceptionMessage);
  }

  @Then("^returned \"(.+?)\" data matches the following json file \"(.+?)\"$")
  public void returned_request_data_matches_the_following_json_file(String message, String filename)
      throws Throwable {
    String expectedJson = TestUtils.getResourceAsString(filename);
    ResponseHandler.matchResponseWithExpectedResult(request, serverResponse, message, expectedJson);
  }

  @Then("^returned \"(.+?)\" data matches the following csv file \"(.+?)\"$")
  public void returned_request_data_matches_the_following_string(String message, String filename)
      throws Throwable {
    String expectedString = TestUtils.getResourceAsString(filename);
    ResponseHandler.matchResponseWithExpectedResult(
        request, expectedString, serverResponse.getUrlConnection().getInputStream(), message);
  }

  @Then("^returned \"(.+?)\" data is contained in following json file \"(.+?)\"$")
  public void returned_request_data_is_contained_in_following_json_file(
      String message, String filename) throws Throwable {
    String expectedJson = TestUtils.getResourceAsString(filename);
    ResponseHandler.matchResponseWithExpectedResultset(
        request, serverResponse, message, expectedJson);
  }

  @Then("^returned \"(.+?)\" data matches the following json string \"(.+?)\"$")
  public void returned_request_data_matches_the_following_json_string(
      String message, String expectedJson) throws Throwable {
    assertNotNull("Expected json string cannot be null", expectedJson);
    ResponseHandler.matchResponseWithExpectedResult(request, serverResponse, message, expectedJson);
  }

  @Then("^returned \"(.+?)\" data has the key \"(.+?)\" and value \"(.+?)\"$")
  public void response_json_has_key_and_value(String message, String key, String value)
      throws Throwable {
    ResponseHandler.checkResponseForValue(key, value, serverResponse, message);
  }

  @Then("^returned tier is removed$")
  public void returned_tier_is_removed() throws Throwable {
    assertEquals(200, serverResponse.http().getResponseCode());
  }

  @Then("^returned \"(.+?)\" field is \"(.+?)\"$")
  public void returned_field_is(String fieldName, String expectedValue) throws Throwable {
    String actualValue = serverResponse.object().getString(fieldName);
    assertEquals(
        actualValue,
        expectedValue,
        "The field "
            + fieldName
            + " actual value doesn't correspond to expected value: "
            + expectedValue);
  }

  @Then("the request failed with http status {string} errorcode {string} and message {string}")
  public void then_request_failure_with_http_status_errorcode_message(
      String httpStatus, String errorCode, String errorMessage) {
    assertFalse(StringUtils.isEmpty(this.exceptionMessage), "Request did not fail");
    String errorMsg =
        String.format(
            "{\"httpResponse\":%1$s,\"errorCode\":%2$s,\"errorMessage\":\"%3$s\",\"guid\":",
            httpStatus, errorCode, errorMessage);
    assertTrue(this.exceptionMessage.contains(errorMsg), "Request Error message");
  }

  @Then(
      "the request failed with http status {string} errorcode {string} and message contains {string}")
  public void then_request_failure_with_http_status_errorcode_message_contains(
      String httpStatus, String errorCode, String errorMessage) {
    String status = String.format("\"httpResponse\":%1$s", httpStatus);
    String code = String.format("\"errorCode\":%1$s", errorCode);
    assertFalse(StringUtils.isEmpty(this.exceptionMessage), "Request did not fail");
    assertTrue(this.exceptionMessage.contains(status), "Request Status");
    assertTrue(this.exceptionMessage.contains(code), "Request Error code");
    assertTrue(this.exceptionMessage.contains(errorMessage), "Request Error message");
  }

  @Then(
      "^the request failed with http status \"(.+?)\" errorcode \"(.+?)\" and message \"(.+?)\" and field errors \"(.*?)\"$")
  public void position_request_failure_with_field_errors(
      int httpStatus, int errorCode, String errorMessage, String fieldErrors) throws Throwable {
    assertFalse(StringUtils.isEmpty(this.exceptionMessage), "Request did not fail");
    String errorMsg =
        String.format(
            "{\"httpResponse\":%1$d,\"errorCode\":%2$d,\"errorMessage\":\"%3$s\",\"guid\":",
            httpStatus, errorCode, errorMessage);
    assertTrue(this.exceptionMessage.contains(errorMsg), "Request Error message");
    ResponseHandler.verifyFailedFieldErrors(request, exceptionMessage, fieldErrors);
  }

  public void setSite(String siteName) throws Throwable {
    setSite(companyPid, siteName);
  }

  public void setSite(String companyPid, String siteName) throws Throwable {
    retrieveSellerSiteSummaries(companyPid);
    assertNotNull(siteMap, "Failed to get sites");
    site = siteMap.get(siteName);
  }

  public void setSite(Site site) {
    this.site = site;
  }

  public Site getSite() {
    assertNotNull(site, "No site is selected");

    return site;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public CompanyType getCompanyType() {
    return companyType;
  }

  public void setCompanyType(CompanyType companyType) {
    this.companyType = companyType;
  }

  public void retrieveCompany(String type) throws Throwable {
    companyType = CompanyType.getCompanyType(type);
    assertNotNull(companyType, String.format("Invalid company type [%s]", type));

    switch (companyType) {
      case SELLER:
        request = companiesRequests.getGetAllSellersRequest();
        break;
      case BUYER:
        request = companiesRequests.getGetAllBuyersRequest();
        break;
      case SEATHOLDER:
        request = companiesRequests.getGetAllSeatHoldersRequest();
        break;
    }
    commonFunctions.executeRequest(this);

    if (serverResponse != null) {
      companyMap = commonFunctions.getCompanies(this);
    }
  }

  @When("^the user sets the company type \"(.+?)\"$")
  public void setCompanyType(String type) {
    companyType = CompanyType.getCompanyType(type);
  }

  /**
   * Retrieve company information using PSS Api
   *
   * @throws Throwable - throws error
   */
  public void retrieveCompanyPss() throws Throwable {
    request = companiesRequests.getGetPssCompanyRequest();
    request.setRequestParams(requestMap);
    commonFunctions.executeRequest(this);
  }

  public void retrievePublisher(String companyPid) throws Throwable {
    requestMap = new RequestParams().setCompanyPid(companyPid).getRequestParams();
    request = companiesRequests.getGetPublisherRequest();
    request.setRequestParams(requestMap);
    commonFunctions.executeRequest(this);
  }

  private void retrieveSellerSiteSummaries() throws Throwable {
    retrieveSellerSiteSummaries(companyPid);
  }

  private void retrieveSellerSiteSummaries(String companyPid) throws Throwable {
    requestMap = new RequestParams().setSellerPid(companyPid).getRequestParams();
    request = siteRequests.getGetSellerSiteSummariesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(this);
    if (serverResponse != null) {
      siteMap = commonFunctions.getSites(serverResponse);
      assertTrue(siteMap.size() > 0, "Seller sites are not found");
    }
  }

  @Given("^the datawarehouse table \"(.+)\" is populated from the data in the file \"(.+)\"$")
  public void populateDataWarehouseTableFromCsvFile(String tableName, String file)
      throws Throwable {
    databaseUtils.insertDwDataToTable(tableName, csvUtils.readCsvFile(file));
  }

  @Given("^the core table \"(.+)\" is populated from the data in the file \"(.+)\"$")
  public void populateCoreTableFromCsvFile(String tableName, String file) throws Throwable {
    databaseUtils.insertCoreDataToTable(tableName, csvUtils.readCsvFile(file));
  }

  @Given("^the datawarehouse table \"(.+)\" is cleared$")
  public void clearDWTable(String tableName) throws Throwable {
    databaseUtils.clearDWTable(tableName);
    assertEquals(0, databaseUtils.getDWTableRowCount(tableName));
  }

  // MX-349 Start
  @When("^ETag value will be saved$")
  public String getFirstETagHeaders() throws Throwable {
    ETag = serverResponse.http().getHeaderField("ETag");
    assertNotNull(ETag, "Failed to get ETag for the first Seller List Retrieval");
    assertEquals(200, serverResponse.http().getResponseCode());
    return ETag;
  }

  @When("^ETag values are the same$")
  public String getSecondETagHeaders() throws Throwable {
    ETagSecond = serverResponse.http().getHeaderField("ETag");

    if (ETagSecond != null && (ETagSecond.equals(ETag))) {
      assertEquals(304, serverResponse.http().getResponseCode());
    }
    return ETagSecond;
  }

  @When("^json content should NOT be present$")
  public void json_content_should_NOT_be_present() throws Throwable {
    if (ETagSecond != null && (ETagSecond == ETag)) {
      assertNull(serverResponse.http().getHeaderField("Content-Length"));
      assertNull(serverResponse.http().getHeaderField("Content-Type"));
    }
  }

  @When("^ETag values are NOT the same$")
  public String ETag_values_are_NOT_the_same() throws Throwable {
    String ETagThird = serverResponse.http().getHeaderField("ETag");

    if (ETagThird != null && (ETagThird != ETagSecond)) {
      assertEquals(200, serverResponse.http().getResponseCode());
    }
    return ETagThird;
  }

  @Then("^the returned data doesn't contain the following fields \"([^\"]*)\"$")
  public void returned_request_data_does_not_contain(String fields) throws Throwable {
    assertObjectDoesNotContainsFields(serverResponse.object(), fields.split(","));
  }

  private void assertObjectDoesNotContainsFields(JSONObject object, String[] fields) {
    for (String field : fields) {
      assertTrue(object.isNull(field), "field " + field + " should be null or not there");
    }
  }

  @Then("^the returned array data doesn't contain the following fields \"([^\"]*)\"$")
  public void returned_request_array_data_does_not_contain(String fields) throws Throwable {
    String[] fieldArray = fields.split(",");
    JSONArray tagData = serverResponse.array();
    for (int i = 0; i < tagData.length(); i++) {
      assertObjectDoesNotContainsFields(tagData.getJSONObject(i), fieldArray);
    }
  }

  @When("^the user finds rule pid for name \"(.+?)\"$")
  public void the_user_finds_rule_pid_for_name(String ruleName) throws Throwable {
    rulePid = databaseUtils.getRulePidByName(ruleName);
    assertNotNull(rulePid, "Rule pid is null");
  }

  @When("^the user sets non-existing rule pid$")
  public void set_non_existing_rule_pid() {
    rulePid = "100000000";
  }

  @Then("^compare dap o2 default player param values \"(.+?)\"$")
  public void compare_dap_o2_default_player_param_values(String name) throws Throwable {
    String returnedO2PlayerIdFromDb = databaseUtils.getDapPlayerId(name);
    assertEquals(returnedO2PlayerIdFromDb, defaultO2PlayerId);
    String returnedO2PlayListIdFromDb = databaseUtils.getDapPlayListId(name);
    assertEquals(returnedO2PlayListIdFromDb, defaultO2PlaylistId);
  }

  @Then("^compare dap yahoo default player param values \"(.+?)\"$")
  public void compare_dap_yahoo_default_player_param_values(String name) throws Throwable {
    String returnedYahooPlayerIdFromDb = databaseUtils.getDapPlayerId(name);
    assertNull("For Dap Yahoo player type player id should be null", returnedYahooPlayerIdFromDb);
    String returnedYahooPlayListIdFromDb = databaseUtils.getDapPlayListId(name);
    assertEquals(returnedYahooPlayListIdFromDb, defaultYahooPlayListId);
  }
  // MX-349 End

  @Then("^the datawarehouse table \"(.+)\" contains records:$")
  public void the_datawarehouse_table_contains_records(String tableName, DataTable dataTable) {
    DataTable expectedDataTable =
        dataTable.rows(1, dataTable.height()); // the first row is the column labels.
    DataTable actualDataTable = databaseUtils.getDwDataTable(tableName, dataTable.row(0));

    Set<List<String>> expectedRowSet = new HashSet<>(expectedDataTable.asLists());
    List<List<String>> actualRowList =
        actualDataTable.asLists().stream()
            .filter(expectedRowSet::contains)
            .collect(Collectors.toList());

    expectedDataTable.unorderedDiff(DataTable.create(actualRowList));
  }
}
