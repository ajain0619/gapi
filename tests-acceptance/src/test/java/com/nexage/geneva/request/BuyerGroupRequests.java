package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.BuyerGroupIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyerGroupRequests {

  @Autowired private Request request;

  public Request getCreateBuyerGroupRequest() {
    return request
        .clear()
        .setExpectedObjectIgnoredKeys(BuyerGroupIgnoredKeys.expectedObject)
        .setPostStrategy()
        .setUrlPattern("/buyers/" + RequestParams.COMPANY_PID + "/buyergroups/");
  }

  public Request getCreateBuyerGroupV1Request() {
    return request
        .clear()
        .setExpectedObjectIgnoredKeys(BuyerGroupIgnoredKeys.expectedObject)
        .setPostStrategy()
        .setUrlPattern("/v1/dsps/" + RequestParams.COMPANY_PID + "/buyer-groups/");
  }

  public Request getUpdateBuyerGroupV1Request() {
    return request
        .clear()
        .setExpectedObjectIgnoredKeys(BuyerGroupIgnoredKeys.expectedObject)
        .setPutStrategy()
        .setUrlPattern(
            "/v1/dsps/"
                + RequestParams.COMPANY_PID
                + "/buyer-groups/"
                + RequestParams.BUYER_GROUP_PID);
  }

  public Request getOneBuyerGroupV1Request() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(BuyerGroupIgnoredKeys.expectedObject)
        .setUrlPattern("/v1/dsps/buyer-groups/" + RequestParams.BUYER_GROUP_PID + "/");
  }

  public Request getGetAllBuyerGroupsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(BuyerGroupIgnoredKeys.expectedObject)
        .setUrlPattern("/buyers/" + RequestParams.COMPANY_PID + "/buyergroups/");
  }

  public Request getUpdateBuyerGroupRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(BuyerGroupIgnoredKeys.expectedObject)
        .setUrlPattern(
            "/buyers/"
                + RequestParams.COMPANY_PID
                + "/buyergroups/"
                + RequestParams.BUYER_GROUP_PID
                + "/");
  }
}
