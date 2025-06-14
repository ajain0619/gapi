package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.BuyerSeatRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.geneva.DatabaseTable;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class BuyerSeatSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private BuyerSeatRequests buyerSeatRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private String updateOpGroupPid;

  private static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2011, 11, 11, 11, 11, 11);
  private static final String DB_FIELD_COMPANY_PID = "company_pid";
  private static final String DB_FIELD_SEAT = "seat";
  private static final String DB_FIELD_DATE_CREATED = "creation_date";
  private static final String DB_FIELD_DATE_UPDATED = "last_updated_date";

  @When("^the user creates buyer seat from the json file \"([^\"]*)\"$")
  public void the_user_creates_buyer_seat_from_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        buyerSeatRequests.getCreateBuyerSeatRequest().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^these buyer groups that belong to the selected company$")
  public void insert_buyer_groups(DataTable tbl) throws Throwable {
    int inserted = 0;
    int expectedInserted = tbl.asLists().size() - 1;
    List<List<String>> raw = tbl.asLists();
    for (List<String> row : cells(raw, 1)) {
      String name = row.get(0);
      String sfdcLineId = row.get(1);
      String sfdcIoId = row.get(2);
      String currency = row.get(3);
      String billingCountry = row.get(4);
      String billable = row.get(5);
      String version = row.get(6);

      inserted +=
          databaseUtils.insertBuyerGroup(
              name,
              commonSteps.companyPid,
              sfdcLineId,
              sfdcIoId,
              currency,
              billingCountry,
              billable,
              version);
    }
    assertTrue(
        inserted == expectedInserted,
        "Expected " + expectedInserted + " records added, but added " + inserted);
  }

  @When("^these buyer seats that belong to the selected company and buyer group \"([^\"]*)\"$")
  public void insert_buyer_seats(String buyerGroupName, DataTable tbl) throws Throwable {
    String buyerGroupPid = databaseUtils.getBuyerGroupPid(buyerGroupName, commonSteps.companyPid);

    int inserted = 0;
    int expectedInserted = tbl.asLists().size() - 1;
    List<List<String>> raw = tbl.asLists();
    for (List<String> row : cells(raw, 1)) {
      String name = row.get(0);
      String seat = row.get(1);
      String enabled = row.get(2);
      String version = row.get(3);

      inserted +=
          databaseUtils.insertBuyerSeat(
              commonSteps.companyPid,
              name,
              seat,
              buyerGroupPid,
              enabled,
              DEFAULT_DATE.toString(),
              version);
    }
    assertTrue(
        inserted == expectedInserted,
        "Expected " + expectedInserted + " records added, but added " + inserted);
  }

  @When("^the user gets all buyer seats$")
  public void the_user_gets_all_buyer_seats() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        buyerSeatRequests.getGetAllBuyerSeatsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all buyer seats matching qf \"(.+?)\" with qt \"(.+?)\"$")
  public void the_user_gets_all_post_auction_dicsounts_matching_qf_and_qt(String qf, String qt)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setqf(qf)
            .setqt(qt)
            .getRequestParams();
    commonSteps.request =
        buyerSeatRequests
            .getGetAllBuyerSeatsRequestByQFQT()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all buyer seats for a buyer \"([^\"]*)\"$")
  public void the_user_gets_all_buyer_seats(String buyer) throws Throwable {
    commonSteps.requestMap = new RequestParams().setCompanyPid(buyer).getRequestParams();
    commonSteps.request =
        buyerSeatRequests.getGetAllBuyerSeatsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates buyer seat with ID \"([^\"]*)\" using the json file \"([^\"]*)\"$")
  public void user_updates_buyer_seat_using_json_file(String seatID, String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    updateOpGroupPid = databaseUtils.getBuyerGroupPidForSeat(seatID, commonSteps.companyPid);

    updateSeat(seatID, payload, updateOpGroupPid);
  }

  @When(
      "^the user moves buyer seat with ID \"([^\"]*)\" to group \"([^\"]*)\" using the json file \"([^\"]*)\"$")
  public void user_moves_buyer_seat_using_json_file(
      String seatID, String targetGroupName, String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    updateOpGroupPid = databaseUtils.getBuyerGroupPid(targetGroupName, commonSteps.companyPid);

    updateSeat(seatID, payload, updateOpGroupPid);
  }

  private void updateSeat(String seatID, JSONObject payload, String buyerGroupPid)
      throws Throwable {
    // ensure json contains the correct group pid in case someone has added more records to the
    // buyer_group
    // table, causing the generated pid to be different from what it is now
    payload.put("buyerGroupPid", Long.valueOf(buyerGroupPid));

    String buyerSeatPid = databaseUtils.getBuyerSeatPid(seatID, commonSteps.companyPid);
    sendUpdateSeatRequest(buyerSeatPid, payload);
  }

  private void sendUpdateSeatRequest(String buyerSeatPid, JSONObject payload) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setBuyerSeatPid(buyerSeatPid)
            .getRequestParams();
    commonSteps.request =
        buyerSeatRequests.getUpdateBuyerSeatRequest().setRequestParams(commonSteps.requestMap);
    commonSteps.request.setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("the user attempts to send a buyer seat update request with the json file {string}")
  public void the_user_attempts_an_update_with_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    String dummyBuyerSeatPid = "1";

    sendUpdateSeatRequest(dummyBuyerSeatPid, payload);
  }

  @When("^seat with ID \"([^\"]*)\" remains in group \"([^\"]*)\"$")
  public void seat_remains_in_group(String seatId, String buyerGroupName) throws Throwable {
    String buyerGroupPidOfSeat =
        databaseUtils.getBuyerGroupPidForSeat(seatId, commonSteps.companyPid);
    assertNotNull(buyerGroupPidOfSeat);
    String expectedBuyerGroupPid =
        databaseUtils.getBuyerGroupPid(buyerGroupName, commonSteps.companyPid);
    assertNotNull(expectedBuyerGroupPid);
    assertEquals(
        "Seat has been moved to another group", expectedBuyerGroupPid, buyerGroupPidOfSeat);
  }

  @When("^seat with ID \"([^\"]*)\"'s enabled status remains \"([^\"]*)\"$")
  public void seat_enabled_status_remains(String seatId, String expectedStatus) throws Throwable {
    String seatStatus = databaseUtils.getBuyerSeatEnabledStatus(seatId, commonSteps.companyPid);
    assertNotNull(seatStatus);
    assertEquals("Seat's status has been changed", expectedStatus, seatStatus);
  }

  @Then("^returned buyer seat update data matches the following json file \"(.+?)\"$")
  public void returned_request_data_matches_the_following_json_file(String filename)
      throws Throwable {
    JSONObject expectedObject = new JSONObject(TestUtils.getResourceAsString(filename));

    expectedObject.put("buyerGroupPid", Long.valueOf(updateOpGroupPid));

    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request,
        commonSteps.serverResponse,
        "buyer seat update",
        expectedObject.toString());
  }

  @Then("^date fields are not empty for \"([^\"]*)\"$")
  public void date_fields_are_not_empty(String filename) throws Throwable {
    JSONObject expectedObject = new JSONObject(TestUtils.getResourceAsString(filename));

    List<Map<String, Object>> results =
        databaseUtils.lookupCoreRecordsByFieldNameAndValue(
            DatabaseTable.BUYER_SEAT,
            DB_FIELD_SEAT,
            ("'" + expectedObject.getString(JsonField.SEAT) + "'"),
            DB_FIELD_COMPANY_PID,
            (expectedObject.getString(JsonField.COMPANY_PID)));
    assertEquals(1, results.size());

    assertNotNull(
        results.get(0).get(DB_FIELD_DATE_CREATED),
        "creation_date is null during creation of buyer seat");
    assertNotNull(
        results.get(0).get(DB_FIELD_DATE_UPDATED),
        "last_updated_date is null during creation of buyer seat");
  }

  @Then("^update date is recorded for \"([^\"]*)\"$")
  public void update_date_is_recorded(String filename) throws Throwable {
    JSONObject expectedObject = new JSONObject(TestUtils.getResourceAsString(filename));

    List<Map<String, Object>> results =
        databaseUtils.lookupCoreRecordsByFieldNameAndValue(
            DatabaseTable.BUYER_SEAT,
            DB_FIELD_SEAT,
            ("'" + expectedObject.getString(JsonField.SEAT) + "'"),
            DB_FIELD_COMPANY_PID,
            (expectedObject.getString(JsonField.COMPANY_PID)));
    assertEquals(1, results.size());
    assertTrue(
        DEFAULT_DATE.compareTo((LocalDateTime) results.get(0).get(DB_FIELD_DATE_UPDATED)) < 0,
        "last_updated_date failed to update during update of buyer seat");
  }

  private List<List<String>> cells(List<List<String>> raw, int firstRow) {
    return raw.subList(firstRow, raw.size());
  }
}
