package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Company;
import com.nexage.geneva.request.CompaniesRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.response.ResponseCode;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class CompaniesSteps {

  @Autowired private DatabaseUtils databaseUtils;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private CommonSteps commonSteps;
  @Autowired private CompaniesRequests companiesRequests;

  private String createdCompanyPid;
  private String companyName;
  private Long pubAliasId;
  private JSONObject companyPayload, updatedCompany;
  private Company companyDbActual;
  protected String nonExistingPid = "00000000";

  @When("^the user creates a \"(.+?)\" company from the json file \"(.+?)\"$")
  public void the_user_creates_a_company_from_the_json_file(String type, String filename)
      throws Throwable {
    commonSteps.retrieveCompany(type);
    companyPayload = JsonHandler.getJsonObjectFromFile(filename);
    createCompany();
  }

  @When("^the user creates a company using pss from the json file \"(.+?)\"$")
  public void the_user_creates_a_company_with_pss_from_the_json_file(String filename)
      throws Throwable {
    commonSteps.retrieveCompanyPss();
    companyPayload = JsonHandler.getJsonObjectFromFile(filename);
    createCompanyPss();
  }

  @When("^the user updates a company \"(.+?)\" wtih rtbProfile belongs to the tag \"(.+?)\"$")
  public void the_user_updates_a_company_add_rtb(String companyName, String rtbTagName)
      throws Throwable {
    String companyPid = databaseUtils.getCompanyPidByName(companyName);
    String tagPid = databaseUtils.getTagPidByName(rtbTagName);
    String rtbPid = databaseUtils.getExchangePidByTagPid(tagPid);
    assertTrue(
        databaseUtils.updateSellerAttributesRtb(rtbPid, companyPid) > 0,
        "Seller DRP was not updated");
    assertTrue(
        databaseUtils.updateExchangeRtbProfile(companyPid, rtbPid) > 0,
        "Exchange DRP was not updated with a publisher");
  }

  @When("^the user updates position \"(.+?)\" status to deleted$")
  public void the_user_updates_position_status_deleted(String positionName) throws Throwable {
    databaseUtils.updatePositionStatusDeleted(positionName);
  }

  @When("^the user updates site \"(.+?)\" status to deleted$")
  public void the_user_updates_site_status_deleted(String siteName) throws Throwable {
    databaseUtils.updateSiteStatusDeleted(siteName);
  }

  @When("^updates a company defaultRtbProfilesEnabled to \"(.+?)\"$")
  public void the_user_updates_a_company_default_rtb_profiles_enabled(
      String defaultRtbProfilesEnabled) {
    int flag = Boolean.parseBoolean(defaultRtbProfilesEnabled) ? 1 : 0;
    databaseUtils.setDefaultRTBProfileEnablementFlag(commonSteps.getCompany().getPid(), flag);
  }

  @When("^the defaultRTBProfilesFlag for company \"(.+?)\" is set to \"(.+?)\"$")
  public void the_company_is_enabled_for_rtb_profiles(String companyName, String enablement)
      throws Throwable {
    String companyPid =
        Objects.isNull(commonSteps.getCompany())
            ? databaseUtils.getCompanyPidByName(companyName)
            : commonSteps.getCompany().getPid();
    int flag = Boolean.parseBoolean(enablement) ? 1 : 0;
    databaseUtils.setDefaultRTBProfileEnablementFlag(companyPid, flag);
  }

  @When("^there are \"(.+?)\" rtb profiles for the company \"(.+?)\"$")
  public void validate_rtb_profile_by_company_pid(String expectedCount, String companyName)
      throws Throwable {
    String companyPid = databaseUtils.getCompanyPidByName(companyName);
    String actualCount = databaseUtils.countRtbProfileByCompanyPid(companyPid);
    assertEquals(
        expectedCount,
        actualCount,
        "Count of sites for company " + companyName + " does not match expected value");
  }

  @When("^quantity of the rtb profiles with name \"(.+?)\" is \"(.+?)\"$")
  public void validate_rtb_profile_by_name(String rtbName, String expectedCount) throws Throwable {
    String actualCount = databaseUtils.countRtbProfileByRtbName(rtbName);
    assertEquals(
        expectedCount,
        actualCount,
        "Count of rtb profiles with name " + rtbName + " does not match expected value");
  }

  @When("^the company data is retrieved$")
  public void the_company_data_is_retrieved() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    getCompany();
  }

  @When("^the new company data is retrieved$")
  public void the_new_company_data_is_retrieved() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    getNewCompany();
  }

  @When("^publisher data is retrieved$")
  public void publisher_data_is_retrieved() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    getPublisher();
  }

  @When("^company data is retrieved using pss$")
  public void the_company_data_is_retrieved_using_pss() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.retrieveCompanyPss();
  }

  @When("^the user selects non existing company using pss$")
  public void the_user_selects_non_existing_company_using_pss() throws Throwable {
    commonSteps.requestMap = new RequestParams().setCompanyPid(nonExistingPid).getRequestParams();
    commonSteps.retrieveCompanyPss();
  }

  @When("^the user selects non existing \"(.+?)\" company$")
  public void the_user_selects_non_existing_company(String type) throws Throwable {
    commonSteps.retrieveCompany(type);
    commonSteps.requestMap = new RequestParams().setCompanyPid(nonExistingPid).getRequestParams();
    getCompany();
  }

  @Then("^company can be searched out in database$")
  public void company_can_be_searched_out_in_database() throws Throwable {
    assertNotNull(createdCompanyPid, "Created company response doesn't contain pid");
    companyDbActual = databaseUtils.getCompanyByPid(commonSteps.companyType, createdCompanyPid);
    assertNotNull(companyDbActual, "company returned from DB is NULL");
  }

  @Then("^company can not be searched out in database$")
  public void company_can_not_be_searched_out_in_database() throws Throwable {
    companyDbActual =
        databaseUtils.getCompanyByPid(commonSteps.companyType, commonSteps.getCompany().getPid());
    assertNull(companyDbActual, "company returned from DB is not NULL");
  }

  @When("^company \"(.+?)\" cannot be searched out in database$")
  public void company_cannot_be_searched_in_the_database(String companyName) throws Throwable {
    String company = databaseUtils.getCompanyPidByName(companyName);
    assertNull(company, "Company can be searched");
  }

  @When("^region id for company \"(.+?)\" equals to \"(.+?)\"$")
  public void region_id_is_correct(String companyName, String expectedRegionId) throws Throwable {
    assertEquals(
        expectedRegionId,
        databaseUtils.getRegionIdByName(companyName),
        "Incorrect region id for specified company");
  }

  @When("^set company \"(.+?)\"$")
  public void external_company_id_can_be_set(String companyName) throws Throwable {
    assertNotNull(databaseUtils.getCompanyByName(companyName), "No companies are found");
    commonSteps.setCompany(databaseUtils.getCompanyByName(companyName));
    commonSteps.companyPid = commonSteps.getCompany().getPid();
  }

  @When("^set company non-existing pid$")
  public void set_nonexisting_pid() {
    commonSteps.companyPid = nonExistingPid;
  }

  @When("^company \"(.+?)\" can be searched out in database$")
  public void company_can_be_searched_in_the_database(String companyName) throws Throwable {
    String company = databaseUtils.getCompanyPidByName(companyName);
    assertNotNull(company, "Company cannot be searched");
  }

  @Then("^company data in database is correct$")
  public void company_data_in_database_is_correct() throws Throwable {
    Company companyExpected = TestUtils.mapper.readValue(companyPayload.toString(), Company.class);
    companyExpected.setPid(createdCompanyPid);
    companyExpected.setName(companyName);
    assertTrue(
        companyExpected.equals(companyDbActual), "company does not return proper data from DB");
  }

  @When("^the user updates a company from the json file \"(.+?)\"$")
  public void the_user_updates_a_company_from_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    switch (commonSteps.companyType) {
      case BUYER:
      case SELLER:
        commonSteps.request = companiesRequests.getUpdateCompanyRequest();
        break;
    }
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      updatedCompany = commonSteps.serverResponse.object();
    }
  }

  @When("^the user updates a new company from the json file \"(.+?)\"$")
  public void the_user_updates_a_new_company_from_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = companiesRequests.getNewUpdateCompanyRequest();
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      updatedCompany = commonSteps.serverResponse.object();
    }
  }

  // maria
  @When("^the seller user updates a company from the json file \"(.+?)\"$")
  public void the_seller_user_updates_a_company_from_the_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = companiesRequests.getUpdateCompanyRequest();
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      updatedCompany = commonSteps.serverResponse.object();
    }
  }

  @When("^the user updates a publisher from the json file \"(.+?)\"$")
  public void the_user_updates_a_publisher_from_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = companiesRequests.getUpdatePublisherRequest();
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      updatedCompany = commonSteps.serverResponse.object();
    }
  }

  @When("^the user updates a company using pss from the json file \"(.+?)\"$")
  public void the_user_updates_a_company_using_pss_from_the_json_file(String filename)
      throws Throwable {
    companyPayload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = companiesRequests.getUpdatePssCompanyRequest();
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(companyPayload);
    commonFunctions.executeRequest(commonSteps);
    if (commonSteps.serverResponse != null) {
      updatedCompany = commonSteps.serverResponse.object();
    }
  }

  @When("^the user updates a non-existing company using pss from the json file \"(.+?)\"$")
  public void the_user_updates_a_non_existing_company_using_pss(String filename) throws Throwable {
    companyPayload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = companiesRequests.getUpdatePssCompanyRequest();
    commonSteps.requestMap = new RequestParams().setCompanyPid(nonExistingPid).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(companyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the selected company$")
  public void the_user_deletes_the_selected_company() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    switch (commonSteps.companyType) {
      case BUYER:
      case SELLER:
        commonSteps.request = companiesRequests.getDeleteCompanyRequest();
        break;
    }
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the seller user deletes the selected company$")
  public void the_seller_user_deletes_the_selected_company() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request = companiesRequests.getDeleteCompanyRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes a company using pss$")
  public void the_user_deletes_company_using_pss() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request = companiesRequests.getDeletePssCompanyRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes a non-existing company using pss$")
  public void the_user_deletes_non_existing_company_pss() throws Throwable {
    commonSteps.requestMap = new RequestParams().setCompanyPid(nonExistingPid).getRequestParams();
    commonSteps.request = companiesRequests.getDeletePssCompanyRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches \"(.+?)\" companies by prefix \"(.+?)\"$")
  public void the_user_searches_companies_by_prefix(String type, String prefix) throws Throwable {
    commonSteps.retrieveCompany(type);
    commonSteps.requestMap = new RequestParams().setPrefix(prefix).getRequestParams();

    switch (commonSteps.companyType) {
      case SELLER:
        commonSteps.request = companiesRequests.getGetSellerByPrefix();
        break;
      case BUYER:
        commonSteps.request = companiesRequests.getGetBuyersByPrefix();
        break;
    }
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the revenueGroup data has been added to db")
  public void add_revenue_group_info() throws Throwable {
    databaseUtils.insertRevenueGroup();
  }

  @Then("^the company alias name was removed$")
  public void the_company_alias_name_was_removed() throws Throwable {
    assertNotNull(updatedCompany, "Updated company is null");
    assertNull(updatedCompany.getString("globalAliasName"), "Alias was not removed successfully");
  }

  @Then("^the company status \"(.+?)\" is correct for company \"(.+?)\"$")
  public void the_company_status_correct_after_delete(String status, String companyName)
      throws Throwable {
    String actualStatus = databaseUtils.getStatusByCompanyName(companyName);
    assertTrue(actualStatus.equals(status));
  }

  @Then("^pubAliasId was generated after create publisher$")
  public void pub_alias_id_was_generated_after_create_company() throws Throwable {
    assertNotNull(databaseUtils.getPubAliasId(createdCompanyPid), "pubAliasId is null");
  }

  @Then("^pubAliasId was not generated after create publisher$")
  public void pub_alias_id_was_not_generated_after_create_company() throws Throwable {
    assertNull(databaseUtils.getPubAliasId(createdCompanyPid), "pubAliasId is not null");
  }

  @Then("^pubAliasId is retrieved for \"(.+?)\" publisher$")
  public void pub_alias_id_is_successfully_retrieved(String company_name) throws Throwable {
    String company_pid = databaseUtils.getCompanyPidByName(company_name);
    assertNotNull(databaseUtils.getPubAliasId(company_pid), "pubAliasId is null");
  }

  @Then("^pubAliasId is not set for \"(.+?)\" publisher$")
  public void pub_alias_id_is_not_retrieved(String company_name) throws Throwable {
    String company_pid = databaseUtils.getCompanyPidByName(company_name);
    pubAliasId = databaseUtils.getPubAliasId(company_pid);
    assertNull(pubAliasId, "pubAliasId is not null");
  }

  @Then("^pubAliasId was regenerated for \"(.+?)\" publisher$")
  public void pub_alias_id_was_regenerated(String company_name) throws Throwable {
    String company_pid = databaseUtils.getCompanyPidByName(company_name);
    Long newPubAliasId = databaseUtils.getPubAliasId(company_pid);
    assertTrue(newPubAliasId != pubAliasId, "newPubAliasId is equal to original pubAliasId");
  }

  @Then("^transparency is disabled for publisher \"(.+?)\"$")
  public void user_disables_transparency_for_publisher(String publisher_name) throws Throwable {
    String publisher_pid = databaseUtils.getCompanyPidByName(publisher_name);
    int result = databaseUtils.disableTransparencyForPublisher(publisher_pid);
    assertTrue(result == 1, "query to disable transparency failed");
  }

  @Then("^deleted company can not be searched out$")
  public void deleted_company_can_not_be_searched_out() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    getCompany();

    assertFalse(
        commonSteps.exceptionMessage.isEmpty()
            & commonSteps.exceptionMessage.contains(String.valueOf(ResponseCode.NOT_FOUND)),
        "The company is not deleted.");
  }

  private void getCompany() throws Throwable {
    switch (commonSteps.companyType) {
      case BUYER:
      case SELLER:
        commonSteps.request = companiesRequests.getGetCompanyRequest();
        break;
    }
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void getNewCompany() throws Throwable {
    commonSteps.request = companiesRequests.getGetNewCompanyRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void getPublisher() throws Throwable {
    commonSteps.request = companiesRequests.getGetPublisherRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void createCompany() throws Throwable {
    switch (commonSteps.companyType) {
      case BUYER:
        commonSteps.request = companiesRequests.getCreateBuyerRequest();
        break;
      case SELLER:
        commonSteps.request = companiesRequests.getCreateSellerRequest();
        break;
    }

    commonSteps.request.setRequestPayload(companyPayload);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      createdCompanyPid = commonSteps.serverResponse.toObject().getString(JsonField.PID);
      companyName = commonSteps.serverResponse.toObject().getString(JsonField.NAME);
    }
  }

  private void createCompanyPss() throws Throwable {
    commonSteps.request = companiesRequests.getCreatePssCompanyRequest();
    commonSteps.request.setRequestPayload(companyPayload);
    commonFunctions.executeRequest(commonSteps);
    if (commonSteps.serverResponse != null) {
      createdCompanyPid = commonSteps.serverResponse.toObject().getString(JsonField.PID);
      companyName = commonSteps.serverResponse.toObject().getString(JsonField.NAME);
    }
  }

  @Then("^the hbthrottle fields are preserved in the db$")
  public void the_hbthrottle_fields_are_preserved_in_the_db() {
    String hbTrottleEnabled =
        databaseUtils.getHBThrottleValues(commonSteps.getCompany().getPid(), "enabled");
    assertTrue(
        hbTrottleEnabled.equals("1"),
        "hbthrottle in the db is not preserved as 1 after update, for the company adserverSellerTest8update");
    String hbTrottlePercentage =
        databaseUtils.getHBThrottleValues(commonSteps.getCompany().getPid(), "percentage");
    assertTrue(
        hbTrottlePercentage.equals("50"),
        "hbTrottlePercentage in the db is not preserved as 50 after update, for the company adserverSellerTest8update");
  }

  @Then("^the hbPricePreference field is preserved in the db$")
  public void the_hbPricePreference_field_is_preserved_in_the_db() throws Throwable {
    String hbPricePreference =
        databaseUtils.gethbPricePreference(commonSteps.getCompany().getPid());
    assertTrue(
        hbPricePreference.equals("1"),
        "hbPricePreference in the db is not preserved as 1 after update, for the company adserverSellerTest8update");
  }

  @When("^Seller selects the company with id \"(.+?)\" and type \"(.+?)\"$")
  public void seller_selects_the_company_with_id(String company_pid, String type) {
    // commonSteps.retrieveCompany(type);
    commonSteps.requestMap = new RequestParams().setCompanyPid(company_pid).getRequestParams();
  }

  @When(
      "^the user updates a company without permissions for the user from the json file \"(.+?)\"$")
  public void the_user_updates_a_company_without_permissions_for_the_user_from_the_json_file(
      String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = companiesRequests.getUpdateCompanyRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the hasHeaderBiddingSites \"(.+?)\" is correct for the seller$")
  public void the_hasHeaderBiddingSites_value_is_correct_for_the_seller(int value) {
    int count = databaseUtils.getHeaderBiddingEnabledSiteCount(commonSteps.getCompany().getPid());
    if (commonSteps.getCompany().getName().equals("Reports Seller 1")) {
      assertTrue(
          commonSteps.getCompany().getHasHeaderBiddingSites().equals("true"),
          "The given seller has at least one site with headerBidding enabled");
      assertTrue(count > 0, "The given seller does not have any header bidding enabled sites");
    } else {
      assertTrue(
          commonSteps.getCompany().getHasHeaderBiddingSites().equals("false"),
          "The given seller has at least one site with headerBidding enabled");
      assertTrue(count == 0, "The given seller does not have any header bidding enabled sites");
    }
  }

  @Then("^the rtbProfile alter reserves for company equals \"(.+?)\"$")
  public void the_rtbProfile_alterReserves_for_company_equals_in_the_db(int alterReserve) {
    List<Integer> alterReserves =
        databaseUtils.getRtbProfileAlterReservesForCompany(commonSteps.getCompany().getPid());
    assertTrue(
        alterReserves.isEmpty()
            || (alterReserves.stream().allMatch(alterReserves.get(0)::equals)
                && alterReserves.get(0) == alterReserve),
        "not all alter reserves equals " + alterReserve);
  }

  @And("^the currency for company is set to \"([^\"]*)\"$")
  public void theCurrencyForCompanyIsSetTo(String currency) {
    databaseUtils.setCompanyCurrency(commonSteps.getCompany().getPid(), currency);
  }

  @And("^company has currency set to \"([^\"]*)\"$")
  public void companyHasCurrencySetTo(String currency) {
    assertEquals(currency, companyDbActual.getCurrency());
  }

  @When(
      "^the user searches all type \"(.+?)\" companies with query field \"([^\"]+)\" and query term \"([^\"]+)\"$")
  public void
      the_user_searches_all_companies_by_types_with_query_field_and_query_term_containing_a_string(
          String type, String qf, String qt) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyType(type).setqf(qf).setqt(qt).getRequestParams();
    commonSteps.request =
        companiesRequests
            .getCompaniesByTypeWithQueryFieldNameAndQueryTermContainingStringRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}
