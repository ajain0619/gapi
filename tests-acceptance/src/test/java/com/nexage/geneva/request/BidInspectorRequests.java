package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BidInspectorRequests {

  @Autowired private Request request;

  public Request getBidsForBidInspectorRequest() {
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/bids?qf="
                + RequestParams.QF
                + "&size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&sort="
                + RequestParams.SORT);
  }

  public Request getAuctionDetailsRequest() {
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/bids/"
                + RequestParams.AUCTION_RUN_HASH_ID
                + "/auction-details?"
                + "&size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&sort="
                + RequestParams.SORT
                + "&qf="
                + RequestParams.QF);
  }
}
