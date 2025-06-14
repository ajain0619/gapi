package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.PostAuctionDiscountRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class PostAuctionDiscountSteps {

  private CommonFunctions commonFunctions;
  private CommonSteps commonSteps;
  private PostAuctionDiscountRequests postAuctionDiscountRequests;
  private DatabaseUtils databaseUtils;

  @Autowired
  public PostAuctionDiscountSteps(
      CommonFunctions commonFunctions,
      CommonSteps commonSteps,
      PostAuctionDiscountRequests postAuctionDiscountRequests,
      DatabaseUtils databaseUtils) {
    this.commonFunctions = commonFunctions;
    this.commonSteps = commonSteps;
    this.postAuctionDiscountRequests = postAuctionDiscountRequests;
    this.databaseUtils = databaseUtils;
  }

  @When("^the user gets all post auction discounts$")
  public void the_user_gets_all_post_auction_discounts() throws Throwable {
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets all post auction discounts both matching qf \"(.+?)\" with qt \"(.+?)\" with enabled \"(.+?)\" and in page \"(.+?)\" with size \"(.+?)\"$")
  public void
      the_user_gets_all_post_auction_discounts_of_page_with_size_matching_qf_with_qt_with_enabled(
          String qf, String qt, String enabled, String page, String size) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPage(page)
            .setSize(size)
            .setqf(qf)
            .setqt(qt)
            .setPostAuctionDiscountEnabled(enabled)
            .getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllPagedQfQtEnabledPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets all post auction discounts both matching qf \"(.+?)\" with qt \"(.+?)\" and in page \"(.+?)\" with size \"(.+?)\"$")
  public void the_user_gets_all_post_auction_discounts_of_page_with_size_matching_qf_with_qt(
      String qf, String qt, String page, String size) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPage(page).setSize(size).setqf(qf).setqt(qt).getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllPagedQfQtPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all post auction discounts of page \"(.+?)\" with size \"(.+?)\"$")
  public void the_user_gets_all_post_auction_discounts_of_page_with_size(String page, String size)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setPage(page).setSize(size).getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllPagedPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user gets all post auction discounts matching qf \"(.+?)\" with qt \"(.+?)\" with enabled \"(.+?)\"$")
  public void the_user_gets_all_post_auction_discounts_matching_qf_with_qt_with_enabled(
      String qf, String qt, String enabled) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setqf(qf)
            .setqt(qt)
            .setPostAuctionDiscountEnabled(enabled)
            .getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllQfQtEnabledPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all post auction discounts matching qf \"(.+?)\" with qt \"(.+?)\"$")
  public void the_user_gets_all_post_auction_dicsounts_matching_qf_with_qt(String qf, String qt)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setqf(qf).setqt(qt).getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllQfQtPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all post auction discounts matching enabled \"(.+?)\"$")
  public void the_user_gets_all_post_auction_discounts_matching_enabled(String enabled)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPostAuctionDiscountEnabled(enabled).getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetAllEnabledPostAuctionDiscountsRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets post auction discount with pid \"(.*?)\"$")
  public void the_user_gets_post_auction_discount_with_pid(String pid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setPostAuctionDiscountPid(pid).getRequestParams();
    commonSteps.request =
        postAuctionDiscountRequests
            .getGetPostAuctionDiscountRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates post auction discount from the json file \"(.+?)\"$")
  public void the_user_creates_post_auction_discount_from_the_json_file(String filename)
      throws Throwable {
    commonSteps.request =
        postAuctionDiscountRequests
            .getCreatePostAuctionDiscountRequest()
            .setRequestPayload(JsonHandler.getJsonObjectFromFile(filename));
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates post auction discount with pid \"(.*?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_post_auction_discount_from_the_json_file(String pid, String filename)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setPostAuctionDiscountPid(pid).getRequestParams();
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        postAuctionDiscountRequests
            .getUpdatePostAuctionDiscountRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^insert deal publisher data of deal_pid \"(.*?)\" and pub_pid \"(.*?)\"$")
  public void insertData(String ids, String names) {
    AtomicInteger index = new AtomicInteger(0);
    final String[] strData = names.split(",");
    Map<Long, Long> data =
        Arrays.stream(ids.split(","))
            .collect(
                Collectors.toMap(Long::valueOf, str -> Long.valueOf(strData[index.getAndAdd(1)])));

    databaseUtils.insertDealPublisher(data);
  }

  @Given("valid deal data for company pid {long} and deal pid {long} exists")
  public void valid_deal_sync_data_for_company_pid_and_deal_pid_exists(
      Long companyPid, Long dealPid) {
    databaseUtils.insertDealDataForDv360DealSync(companyPid, dealPid);
  }
}
