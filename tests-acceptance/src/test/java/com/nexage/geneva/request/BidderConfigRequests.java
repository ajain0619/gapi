package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.BidderConfigIgnoredKeys;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BidderConfigRequests {

  @Autowired private Request request;

  public Request getGetAllBidderConfigsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/buyers/" + RequestParams.BUYER_PID + "/bidderconfigs");
  }

  public Request getGetBidderConfigRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/dsps/"
                + RequestParams.BUYER_PID
                + "/bidder-configs/"
                + RequestParams.BIDDER_CONFIG_PID);
  }

  public Request getGetBidderConfigSummariesRequestWithSearchTermsAndPagination() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/dsps/"
                    + RequestParams.BUYER_PID
                    + "/bidder-configs/?qf=%s&qt=%s&page=%s&size=%s",
                RequestParams.QF,
                RequestParams.QT,
                RequestParams.PAGE,
                RequestParams.SIZE));
  }

  public Request getCreateBidderConfigRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/dsps/" + RequestParams.BUYER_PID + "/bidder-configs")
        .setExpectedObjectIgnoredKeys(BidderConfigIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(BidderConfigIgnoredKeys.actualObjectCreate);
  }

  public Request getCreateBidderConfigRequestNullPayload() {
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put("Content-Type", "application/json");
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/dsps/" + RequestParams.BUYER_PID + "/bidder-configs")
        .setRequestHeaders(requestHeaders)
        .setRequestPayload("null")
        .setExpectedObjectIgnoredKeys(new String[] {"id"})
        .setActualObjectIgnoredKeys(new String[] {"id"});
  }

  public Request getUpdateBidderConfigRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/v1/dsps/"
                + RequestParams.BUYER_PID
                + "/bidder-configs/"
                + RequestParams.BIDDER_CONFIG_PID);
  }

  public Request getDeleteBidderConfigRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/buyers/bidderconfigs/" + RequestParams.BIDDER_CONFIG_PID);
  }

  public Request getGetListOfPublishersRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/publishers");
  }

  public Request getGetListOfPublisherSitesRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/publishers/sites");
  }

  public Request getGetListOfIdNameMappingsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/buyers/bidders/getIdNameMap");
  }

  public Request getGetListOfAdSizes() {
    return request.clear().setGetStrategy().setUrlPattern("/buyers/bidderconfigs/adsizes");
  }

  public Request getGetAllBidderConfigsForNewCompany() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/buyers/" + RequestParams.BUYER_PID + "/bidderconfigs")
        .setExpectedObjectIgnoredKeys(BidderConfigIgnoredKeys.expectedObjectCreateFromNewCompany)
        .setActualObjectIgnoredKeys(BidderConfigIgnoredKeys.actualObjectCreateFromNewCompany);
  }
}
