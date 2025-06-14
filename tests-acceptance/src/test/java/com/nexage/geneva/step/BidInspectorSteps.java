package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.FactCowboyExchange;
import com.nexage.geneva.model.crud.FactCowboyExchangeDeal;
import com.nexage.geneva.model.crud.FactCowboyTraffic;
import com.nexage.geneva.request.BidInspectorRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class BidInspectorSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private BidInspectorRequests bidInspectorRequests;
  @Autowired private DatabaseUtils databaseUtils;

  @When(
      "^the user searches for bids with page \"(.+?)\", size \"(.+?)\", qf \"(.+?)\", sort \"(.+?)\"$")
  public void the_user_searches_for_a_page_of_bids(String page, String size, String qf, String sort)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setSize(size.equalsIgnoreCase("Empty") ? " " : size)
            .setPage(page.equalsIgnoreCase("Empty") ? " " : page)
            .setqf(qf.equalsIgnoreCase("Empty") ? "" : qf)
            .setSort(sort.equalsIgnoreCase("Empty") ? "" : sort)
            .getRequestParams();
    commonSteps.request =
        bidInspectorRequests
            .getBidsForBidInspectorRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "the user searches for Auction Details auctionRunHashId \"(.+?)\" page \"(.+?)\", size \"(.+?)\", qf \"(.+?)\", sort \"(.+?)\"$")
  public void the_user_searches_for_auction_details(
      String auctionRunHashId, String page, String size, String qf, String sort) throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setAuctionRunHashId(auctionRunHashId)
            .setSize(size.equalsIgnoreCase("Empty") ? " " : size)
            .setPage(page.equalsIgnoreCase("Empty") ? " " : page)
            .setSort(sort.equalsIgnoreCase("Empty") ? "" : sort)
            .setqf(qf.equalsIgnoreCase("Empty") ? "" : qf)
            .getRequestParams();
    commonSteps.request =
        bidInspectorRequests.getAuctionDetailsRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the following fact_cowboy_traffic records are added$")
  public void add_fact_cowboy_traffic_records(DataTable dataTable) {
    List<List<String>> rows = dataTable.asLists(String.class);
    List<FactCowboyTraffic> factCowboyTrafficList =
        rows.stream()
            .map(
                row ->
                    new FactCowboyTraffic(
                        row.get(0),
                        row.get(1),
                        row.get(2),
                        Long.parseLong(row.get(3)),
                        Long.parseLong(row.get(4)),
                        Integer.parseInt(row.get(5)),
                        row.get(6),
                        row.get(7),
                        Integer.parseInt(row.get(8)),
                        Integer.parseInt(row.get(9)),
                        Integer.parseInt(row.get(10)),
                        row.get(11),
                        row.get(12),
                        row.get(13)))
            .collect(Collectors.toList());
    databaseUtils.addFactCowboyTrafficRecords(factCowboyTrafficList);
  }

  @Given("^the following fact_cowboy_exchange records are added$")
  public void add_fact_cowboy_exchange_records(DataTable dataTable) {
    List<List<String>> rows = dataTable.asLists(String.class);
    List<FactCowboyExchange> factCowboyExchangeList =
        rows.stream()
            .map(
                row ->
                    new FactCowboyExchange(
                        row.get(0),
                        row.get(1),
                        row.get(2),
                        Integer.parseInt(row.get(3)),
                        row.get(4),
                        Integer.parseInt(row.get(5)),
                        row.get(6),
                        row.get(7),
                        Integer.parseInt(row.get(8))))
            .collect(Collectors.toList());
    databaseUtils.addFactCowboyExchangeRecords(factCowboyExchangeList);
  }

  @Given("^the following fact_cowboy_exchange_deals records are added$")
  public void add_fact_cowboy_exchange_deals_records(DataTable dataTable) {
    List<List<String>> rows = dataTable.asLists(String.class);
    List<FactCowboyExchangeDeal> factCowboyExchangeDealList =
        rows.stream()
            .map(
                row ->
                    new FactCowboyExchangeDeal(
                        row.get(0),
                        row.get(1),
                        row.get(2),
                        Integer.parseInt(row.get(3)),
                        row.get(4)))
            .collect(Collectors.toList());
    databaseUtils.addFactCowboyExchangeDealRecords(factCowboyExchangeDealList);
  }

  @Given(
      "^the following dim_pre_bid_filter_reason records with id \"(.+?)\" and name \"(.+?)\" are added$")
  public void add_dim_pre_bid_filter_reason_records(String id, String name) {
    databaseUtils.addPreBidFilterReasonRecords(Integer.parseInt(id), name);
  }
}
