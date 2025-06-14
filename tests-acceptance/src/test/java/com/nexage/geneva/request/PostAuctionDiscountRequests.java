package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.PostAuctionDiscountIgnoreKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostAuctionDiscountRequests {

  private Request request;

  @Autowired
  public PostAuctionDiscountRequests(Request request) {
    this.request = request;
  }

  public Request getGetAllPostAuctionDiscountsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/post-auction-discounts");
  }

  public Request getGetAllPagedQfQtEnabledPostAuctionDiscountsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts?page=%s&size=%s&qf=%s&qt=%s&enabled=%s",
                RequestParams.PAGE,
                RequestParams.SIZE,
                RequestParams.QF,
                RequestParams.QT,
                RequestParams.POST_AUCTION_DISCOUNT_ENABLED));
  }

  public Request getGetAllPagedQfQtPostAuctionDiscountsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts?page=%s&size=%s&qf=%s&qt=%s",
                RequestParams.PAGE, RequestParams.SIZE, RequestParams.QF, RequestParams.QT));
  }

  public Request getGetAllPagedPostAuctionDiscountsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts?page=%s&size=%s",
                RequestParams.PAGE, RequestParams.SIZE));
  }

  public Request getGetAllQfQtEnabledPostAuctionDiscountsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts?qf=%s&qt=%s&enabled=%s",
                RequestParams.QF, RequestParams.QT, RequestParams.POST_AUCTION_DISCOUNT_ENABLED));
  }

  public Request getGetAllQfQtPostAuctionDiscountsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts?qf=%s&qt=%s", RequestParams.QF, RequestParams.QT));
  }

  public Request getGetAllEnabledPostAuctionDiscountsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts?enabled=%s",
                RequestParams.POST_AUCTION_DISCOUNT_ENABLED));
  }

  public Request getGetPostAuctionDiscountRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/post-auction-discounts/%s", RequestParams.POST_AUCTION_DISCOUNT_PID));
  }

  public Request getCreatePostAuctionDiscountRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/post-auction-discounts")
        .setExpectedObjectIgnoredKeys(
            PostAuctionDiscountIgnoreKeys.expectedObjectCreateOrUpdateIgnoreKeys)
        .setActualObjectIgnoredKeys(
            PostAuctionDiscountIgnoreKeys.actualObjectCreateOrUpdateIgnoreKeys);
  }

  public Request getUpdatePostAuctionDiscountRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            String.format("/v1/post-auction-discounts/%s", RequestParams.POST_AUCTION_DISCOUNT_PID))
        .setExpectedObjectIgnoredKeys(
            PostAuctionDiscountIgnoreKeys.expectedObjectCreateOrUpdateIgnoreKeys)
        .setActualObjectIgnoredKeys(
            PostAuctionDiscountIgnoreKeys.actualObjectCreateOrUpdateIgnoreKeys);
  }
}
