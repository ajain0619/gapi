package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Deal;
import com.nexage.geneva.request.DealRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.TestUtils;
import com.nexage.geneva.util.XlsUtils;
import com.nexage.geneva.util.geneva.DatabaseTable;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class DealSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DealRequests dealRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private long dealPid;
  private long sellerId;

  private JSONObject expectedDeal;
  private Map<String, Deal> dealMap;

  private String rtbProfileId;

  @When("^the user searches for all deals$")
  public void the_user_searches_for_all_deals() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        dealRequests.getGetAllDealsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    if (commonSteps.serverResponse != null) {
      JSONArray dealArray = commonSteps.serverResponse.array();
      dealMap = new HashMap<>();
      for (int i = 0; i < dealArray.length(); i++) {
        Deal deal = TestUtils.mapper.readValue(dealArray.get(i).toString(), Deal.class);
        dealMap.put(deal.getDealId(), deal);
      }
    }
  }

  @When("^the user searches for all deals associated with seller by sellerPid \"([^\"]*)\"$")
  public void the_user_searches_for_all_deals_associated_with_seller(String sellerPid)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setSellerPid(sellerPid).getRequestParams();
    commonSteps.request =
        dealRequests.getPagedDealsBySellerPidRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      JSONArray dealArray = commonSteps.serverResponse.object().getJSONArray("content");
      dealMap = new HashMap<>();
      for (int i = 0; i < dealArray.length(); i++) {
        Deal deal = TestUtils.mapper.readValue(dealArray.get(i).toString(), Deal.class);
        dealMap.put(deal.getDealId(), deal);
      }
    }
  }

  @When(
      "^the user searches for deals associated with seller by sellerPid \"([^\"]*)\" and qf \"([^\"]*)\", qt \"([^\"]*)\"$")
  public void the_user_searches_for_deals_associated_with_seller_using_qf_qt(
      String sellerPid, String qf, String qt) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setqt(qt).setqf(qf).setSellerPid(sellerPid).getRequestParams();
    commonSteps.request =
        dealRequests.getPagedDealsBySellerPidRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets deal associated with seller by sellerPid \"([^\"]*)\" and dealPid \"([^\"]*)\"$")
  public void the_user_gets_deal_associated_with_seller_by_dealPid(String sellerPid, String dealPid)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setDealPid(dealPid).setSellerPid(sellerPid).getRequestParams();
    commonSteps.request =
        dealRequests.getDealBySellerPidRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets publisher associated with deal by dealPid \"([^\"]*)\" and publisherId \"([^\"]*)\"$")
  public void the_user_gets_publisher_associated_with_deal_by_publisherPid(
      String dealPid, String publisherPid) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setDealPid(dealPid).setPublisherPid(publisherPid).getRequestParams();
    commonSteps.request =
        dealRequests.getPublisherFromDeal().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for paged deals with no rules attached$")
  public void the_user_searches_for_paged_deals_with_no_rules() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.requestMap.put(RequestParams.QF, "hasRules");
    commonSteps.requestMap.put(RequestParams.QT, "false");
    commonSteps.request =
        dealRequests.getGetPagedDealsWithRulesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for paged deals regardless of rules$")
  public void the_user_searches_for_paged_deals_regardless_of_rules() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.requestMap.put(RequestParams.QF, "all");
    commonSteps.requestMap.put(RequestParams.QT, "true");
    commonSteps.request =
        dealRequests.getGetPagedDealsWithRulesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for a page of deals with rules attached$")
  public void the_user_searches_for_a_page_of_deals_with_rules_attached() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        dealRequests.getGetPagedDealsWithRulesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for a page of deals with rules attached seaching for dealId \"(.+?)\"$")
  public void the_user_searches_for_a_page_of_deals_with_rules_attached_searching_for_dealId(
      String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf("dealId").getRequestParams();
    commonSteps.request =
        dealRequests.getGetPagedDealsWithRulesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user searches for a page of deals with rules attached searching for description \"(.+?)\"$")
  public void the_user_searches_for_a_page_of_deals_with_rules_attached_searching_for_description(
      String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf("description").getRequestParams();
    commonSteps.request =
        dealRequests.getGetPagedDealsWithRulesRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for deal by deal id \"(.+?)\"$")
  public void the_user_searches_for_deal_by_deal_id(String dealId) throws Throwable {
    assertNotNull(dealMap, "Deals are not found");

    commonSteps.requestMap =
        new RequestParams().setDealPid(dealMap.get(dealId).getPid()).getRequestParams();
    commonSteps.request = dealRequests.getGetDealRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for non existing deal$")
  public void the_user_searches_for_non_existing_deal() throws Throwable {
    String nonExistingPid = "0";
    commonSteps.requestMap = new RequestParams().setDealPid(nonExistingPid).getRequestParams();
    commonSteps.request = dealRequests.getGetDealRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user create a deal from the json file \"(.+?)\"$")
  public void the_user_create_a_deal_from_the_json_file(String filename) throws Throwable {
    expectedDeal = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = dealRequests.getCreateDealRequest().setRequestPayload(expectedDeal);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("the user update a deal with Deal id {string} from the json file {string}")
  public void the_user_update_a_deal_with_deal_id_from_the_json_file(String dealId, String filename)
      throws Throwable {
    assertNotNull(dealMap, "Deals are not found");

    expectedDeal = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setDealPid(dealMap.get(dealId).getPid()).getRequestParams();
    commonSteps.request =
        dealRequests
            .getUpdateDealRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedDeal);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("the user changes deal status from disabled to enabled for deal with Deal id \"(.+?)\"$")
  public void the_user_changes_deal_status_from_disabled_to_enabled_for_deal_with_deal_id(
      String dealId) throws Throwable {
    assertNotNull(dealMap, "Deals are not found");

    commonSteps.requestMap =
        new RequestParams().setDealPid(dealMap.get(dealId).getPid()).getRequestParams();
    commonSteps.request =
        dealRequests.getActivateDealRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user changes deal status from enabled to disabled for deal with Deal id \"(.+?)\"$")
  public void the_user_changes_deal_status_from_enabled_to_disabled_for_deal_with_deal_id(
      String dealId) throws Throwable {
    assertNotNull(dealMap, "Deals are not found");

    JSONObject anyPayload = new JSONObject("{}");
    commonSteps.requestMap =
        new RequestParams().setDealPid(dealMap.get(dealId).getPid()).getRequestParams();
    commonSteps.request =
        dealRequests
            .getInactivateDealRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then(
      "^the exchange_site_tag record with pid \"(.+?)\" now has version \"(.+?)\" and timestamp ge \"(.+?)\"$")
  public void the_exchange_site_tag_record_with_pid_now_has_version_and_timestamp_ge(
      String pid, String versionExpected, String timestampGe) throws Throwable {
    Timestamp jTimestampGe = Timestamp.valueOf(timestampGe);
    List<Map<String, Object>> results =
        databaseUtils.lookupCoreRecordsByFieldNameAndValue(
            DatabaseTable.EXCHANGE_SITE_TAG, JsonField.PID, pid);
    assertEquals(1, results.size());

    Map<String, Object> row = results.get(0);
    String actualVersion = row.get(JsonField.VERSION).toString();
    Timestamp actualTimestamp = (Timestamp) row.get(JsonField.LAST_UPDATE);

    assertFalse(
        actualTimestamp.before(jTimestampGe),
        "Exchange site tag timestamp for pid " + pid + " is not greater, then expected");
    assertEquals(
        "Exchange site tag version number for pid "
            + pid
            + " is incorrect, expected "
            + versionExpected
            + ", but was "
            + actualVersion,
        versionExpected,
        actualVersion);
  }

  @Then("^there are now \"(.+?)\" exchange_site_tag_aud records with pid \"(.+?)\"$")
  public void there_are_now_exchange_site_tag_aud_records_with_pid(String count, String pid)
      throws Throwable {
    int expectedRecordsNumber = Integer.valueOf(count);
    int actualCount =
        databaseUtils.countCoreRecordsByFieldNameAndValue(
            DatabaseTable.EXCHANGE_SITE_TAG_AUD, JsonField.PID, pid);

    assertEquals(
        expectedRecordsNumber,
        actualCount,
        "Count of exchange_site_tag audit rows for Deal pid "
            + pid
            + "does not match expected value");
  }

  @Given("^the exchange_site_tag record with pid \"(.+?)\" initially has version 0")
  public void the_exchange_site_tag_record_with_pid_initially_has_version_0(String pid)
      throws Throwable {
    databaseUtils.resetRecordVersionByPid(DatabaseTable.EXCHANGE_SITE_TAG, pid);
  }

  @Given("^there are initially no exchange_site_tag_aud records with pid \"(.+?)\"$")
  public void there_are_initially_no_exchange_site_tag_aud_records_with_pid(String pid)
      throws Throwable {
    databaseUtils.deleteCoreRecordsByFieldNameAndValue(
        DatabaseTable.EXCHANGE_SITE_TAG_AUD, JsonField.PID, pid);
  }

  @And("^the returned deal data should not contain the archived tag \"(.+?)\"$")
  public void the_returned_deal_data_should_not_contain_archived_tags(String rtbProfilePid)
      throws Throwable {
    JSONArray rtbProfiles =
        (JSONArray) commonSteps.serverResponse.toObject().get(JsonField.PROFILES);
    for (int i = 0; i < rtbProfiles.length(); i++) {
      JSONObject tag = (JSONObject) rtbProfiles.get(i);
      assertFalse(
          rtbProfilePid.equals(tag.get(JsonField.RTB_PROFILE_PID)),
          "The archived tag is showing up in the selected supplier list");
    }
  }

  @And("^the returned deal data should contain the inactive tag \"(.+?)\"$")
  public void the_returned_deal_data_should_contain_inactive_tags(String rtbProfilePid)
      throws Throwable {
    JSONArray rtbProfiles =
        (JSONArray) commonSteps.serverResponse.toObject().get(JsonField.PROFILES);
    rtbProfileId = null;
    for (int i = 0; i < rtbProfiles.length(); i++) {
      JSONObject tag = (JSONObject) rtbProfiles.get(i);
      if (rtbProfilePid.equals(tag.get(JsonField.RTB_PROFILE_PID).toString())) {
        rtbProfileId = tag.get(JsonField.RTB_PROFILE_PID).toString();
      }
    }
    assertTrue(
        rtbProfilePid.equals(rtbProfileId),
        "The inactive tag is not showing up in the selected supplier list");
  }

  @When("^the user selects deal PID (\\d+)$")
  public void select_deal(long dealPid) {
    this.dealPid = dealPid;
  }

  @When("^the user updates specific assigned inventory from JSON file \"(.+?)\"$")
  public void update_specific_inventory(String fileName) throws Throwable {
    var expectedJsonObject = JsonHandler.getJsonObjectFromFile(fileName);
    var requestMap = new RequestParams().setDealPid(Long.toString(dealPid));
    commonSteps.request =
        dealRequests
            .postSpecificAssignedInventory()
            .setRequestPayload(expectedJsonObject)
            .setRequestHeaders(
                Map.of(
                    "Content-Type",
                    "application/vnd.geneva-api.assigned-inventories-specific+json"))
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates formula assigned inventory from JSON file \"(.+?)\"$")
  public void update_formula_inventory(String fileName) throws Throwable {
    updateAssignedInventory(
        "application/vnd.geneva-api.assigned-inventories-formula+json", fileName);
  }

  @When("^the user fetches specific inventory for deal PID (\\d+)$")
  public void get_specific_inventory(int dealPid) throws Throwable {
    getAssignedInventory(
        "application/vnd.geneva-api.assigned-inventories.unpaged-specific+json", dealPid);
  }

  @When("^the user fetches formula inventory for deal PID (\\d+)$")
  public void get_formula_assigned_inventory(int dealPid) throws Throwable {
    getAssignedInventory(
        "application/vnd.geneva-api.assigned-inventories.unpaged-formula+json", dealPid);
  }

  @When("^the user fetches one Deal for deal PID \"(.+?)\"$")
  public void get_one_deal_by_pid(int dealPid) throws Throwable {
    var requestMap = new RequestParams().setDealPid(Long.toString(dealPid));
    commonSteps.request =
        dealRequests.getOneDealRequest().setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates specific assigned inventory associated with seller \"(.+?)\" from JSON file \"(.+?)\"$")
  public void update_specific_inventory_associated_with_seller(String sellerPid, String fileName)
      throws Throwable {
    var expectedJsonObject = JsonHandler.getJsonObjectFromFile(fileName);
    var requestMap = new RequestParams().setSellerPid(sellerPid).setDealPid(Long.toString(dealPid));
    commonSteps.request =
        dealRequests
            .postSpecificAssignedInventoryAssociatedWithSeller()
            .setRequestPayload(expectedJsonObject)
            .setRequestHeaders(
                Map.of(
                    "Content-Type",
                    "application/vnd.geneva-api.assigned-inventories-specific+json"))
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  private void updateAssignedInventory(String contentType, String fileName) throws Throwable {
    var expectedJsonObject = JsonHandler.getJsonObjectFromFile(fileName);
    var requestMap = new RequestParams().setDealPid(Long.toString(dealPid));
    commonSteps.request =
        dealRequests
            .postSpecificAssignedInventory()
            .setRequestPayload(expectedJsonObject)
            .setRequestHeaders(Map.of("Content-Type", contentType))
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  private void getAssignedInventory(String acceptType, int dealPid) throws Throwable {
    var requestMap = new RequestParams().setDealPid(Long.toString(dealPid));
    commonSteps.request =
        dealRequests
            .getAssignedInventory()
            .setRequestHeaders(Map.of("Accept", acceptType))
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user create a deal associated with seller \"([^\"]*)\" from the json file \"([^\"]*)\"$")
  public void the_user_create_a_deal_associated_with_seller_from_the_json_file(
      String sellerId, String filename) throws Throwable {
    expectedDeal = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setSellerPid(sellerId).getRequestParams();
    commonSteps.request =
        dealRequests
            .getCreateDealAssociatedWithSellerRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedDeal);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user update a deal associated with seller \"([^\"]*)\" with Deal id \"([^\"]*)\" from the json file \"([^\"]*)\"$")
  public void the_user_update_a_deal_with_associated_with_seller_with_deal_id_from_the_json_file(
      String sellerId, String dealId, String filename) throws Throwable {
    assertNotNull(dealMap, "Deals are not found");

    expectedDeal = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setSellerPid(sellerId)
            .setDealPid(dealMap.get(dealId).getPid())
            .getRequestParams();
    commonSteps.request =
        dealRequests
            .getUpdateDealAssociatedWithSellerRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedDeal);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates assigned inventory formula associated with seller \"(.+?)\" from JSON file \"(.+?)\"$")
  public void update_assigned_inventory_formula_associated_with_seller(
      String sellerPid, String fileName) throws Throwable {
    var expectedJsonObject = JsonHandler.getJsonObjectFromFile(fileName);
    var requestMap = new RequestParams().setSellerPid(sellerPid).setDealPid(Long.toString(dealPid));
    commonSteps.request =
        dealRequests
            .putSpecificAssignedInventoryAssociatedWithSeller()
            .setRequestPayload(expectedJsonObject)
            .setRequestHeaders(
                Map.of(
                    "Content-Type", "application/vnd.geneva-api.assigned-inventories-formula+json"))
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user downloads the deal inventory file for deal pid \"(.+?)\" and file pid \"(.+?)\"$")
  public void download_deal_inventory_file_associated_with_deal(String dealPid, String filePid)
      throws Throwable {
    var requestMap = new RequestParams().setDealPid(dealPid).setFilePid(filePid);
    commonSteps.request =
        dealRequests
            .getDownloadInventoryFileAssociatedWithDeal()
            .setRequestParams(requestMap.getRequestParams())
            .setRequestHeaders(Map.of("Accept", "*/*"));
    commonFunctions.executeRequest(commonSteps);
  }

  @And(
      "^the downloaded deal inventory file name should be \"(.+?)\" and the content should match the content of \"(.+?)\"$")
  public void the_downloaded_deal_inventory_file_should_have_the_data(
      String expectedFileName, String contentFileName) throws Throwable {
    HttpURLConnection httpURLConnection = commonSteps.serverResponse.http();
    // Validate file name
    String contentDisposition = "attachment;filename=" + expectedFileName;
    assertEquals(
        contentDisposition,
        httpURLConnection.getHeaderField("Content-Disposition"),
        "File name is not matching");

    // Validate file content
    List<String> responseList = XlsUtils.readContent(httpURLConnection.getInputStream());
    List<String> expectedList = XlsUtils.readContent(contentFileName);
    assertEquals(expectedList, responseList);
  }

  @When("^the user uploads the bulk inventory file \"(.+?)\"$")
  public void upload_deal_bulk_inventory_file(String fileName) throws Throwable {
    commonSteps.request =
        dealRequests
            .processBulkInventoryFile(fileName)
            .setRequestHeaders(Map.of("Accept", "*/*"))
            .setRequestHeaders(Map.of("Content-Type", "multipart/form-data"));
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user uploads the inventory file \"(.+?)\", file name \"(.+?)\", file type \"(.+?)\", deal id \"(.+?)\"$")
  public void upload_deal_inventory_file(
      String file, String fileName, String fileType, String dealId) throws Throwable {
    commonSteps.request =
        dealRequests
            .processInventoryFile(file, fileName, fileType, dealId)
            .setRequestHeaders(Map.of("Accept", "*/*"))
            .setRequestHeaders(Map.of("Content-Type", "multipart/form-data"));
    commonFunctions.executeRequest(commonSteps);
  }
}
