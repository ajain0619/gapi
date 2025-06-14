package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.DealIgnoredKeys;
import com.nexage.geneva.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DealRequests {

  private final String dealsBaseUrl = "/deals/{dealPid}";
  private final String sellersBaseUrl = "/v1/sellers/{sellerPid}";
  private final String sellersDealsUrl = sellersBaseUrl + dealsBaseUrl;
  private final String dealsPublisherMap =
      dealsBaseUrl + "/publishers/{publisherPid}/publisher_map";
  private final String v1DealsUrlAll = "/v1/deals/?size=";

  @Autowired private Request request;

  public Request getCreateDealRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/deals")
        .setExpectedObjectIgnoredKeys(DealIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(DealIgnoredKeys.actualObjectCreate);
  }

  public Request getGetAllDealsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/deals");
  }

  public Request getPagedDealsBySellerPidRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            sellersBaseUrl
                + "/deals/?size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&qt="
                + RequestParams.QT
                + "&qf="
                + RequestParams.QF);
  }

  public Request getDealBySellerPidRequest() {
    return request.clear().setGetStrategy().setUrlPattern(sellersDealsUrl);
  }

  public Request getPublisherFromDeal() {
    return request.clear().setGetStrategy().setUrlPattern(dealsPublisherMap);
  }

  public Request getGetPagedDealsWithRulesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            v1DealsUrlAll
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&qt="
                + RequestParams.QT
                + "&qf="
                + RequestParams.QF);
  }

  public Request getUpdateDealRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(dealsBaseUrl)
        .setExpectedObjectIgnoredKeys(DealIgnoredKeys.expectedObjectUpdate)
        .setActualObjectIgnoredKeys(DealIgnoredKeys.actualObjectUpdate);
  }

  public Request getGetDealRequest() {
    return request.clear().setGetStrategy().setUrlPattern(dealsBaseUrl);
  }

  public Request getActivateDealRequest() {
    return request.clear().setPutStrategy().setUrlPattern(dealsBaseUrl + "/activate");
  }

  public Request getInactivateDealRequest() {
    return request.clear().setPutStrategy().setUrlPattern(dealsBaseUrl + "/inactivate");
  }

  public Request postSpecificAssignedInventory() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/deals/" + RequestParams.DEAL_PID + "/assigned-inventories");
  }

  public Request getAssignedInventory() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/deals/" + RequestParams.DEAL_PID + "/assigned-inventories");
  }

  public Request getOneDealRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/deals/" + RequestParams.DEAL_PID);
  }

  public Request postSpecificAssignedInventoryAssociatedWithSeller() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.SELLER_PID
                + "/deals/"
                + RequestParams.DEAL_PID
                + "/assigned-inventories");
  }

  public Request getCreateDealAssociatedWithSellerRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(sellersBaseUrl + "/deals")
        .setExpectedObjectIgnoredKeys(DealIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(DealIgnoredKeys.actualObjectCreate);
  }

  public Request getUpdateDealAssociatedWithSellerRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(sellersDealsUrl)
        .setExpectedObjectIgnoredKeys(DealIgnoredKeys.expectedObjectUpdate)
        .setActualObjectIgnoredKeys(DealIgnoredKeys.actualObjectUpdate);
  }

  public Request putSpecificAssignedInventoryAssociatedWithSeller() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.SELLER_PID
                + "/deals/"
                + RequestParams.DEAL_PID
                + "/assigned-inventories");
  }

  public Request getDownloadInventoryFileAssociatedWithDeal() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/deals/inventories?"
                + "dealPid="
                + RequestParams.DEAL_PID
                + "&filePid="
                + RequestParams.FILE_PID);
  }

  public Request processBulkInventoryFile(String fileName) throws Throwable {

    byte[] payload = TestUtils.getResourceAsInputStream(fileName).readAllBytes();
    return request
        .clear()
        .setPostStrategy()
        .setMultipartFile("inventoriesFile", fileName, payload)
        .setUrlPattern("/v1/deals/bulk-inventories");
  }

  public Request processInventoryFile(String file, String fileName, String fileType, String dealId)
      throws Throwable {

    return request
        .clear()
        .setPostStrategy()
        .prepareMultipartRequest(file, fileName, fileType, dealId)
        .setUrlPattern("/v1/deals/inventories");
  }
}
