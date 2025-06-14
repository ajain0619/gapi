package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RevenueGroupsRequests {

  @Autowired private Request request;

  public Request getGetRevenueGroupsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/revenue-groups");
  }

  public Request getGetRevenueGroupsRequestWithPaging() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/revenue-groups?page=%s&size=%s", RequestParams.PAGE, RequestParams.SIZE));
  }
}
