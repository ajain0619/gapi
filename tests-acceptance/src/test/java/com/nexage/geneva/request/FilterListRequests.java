package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterListRequests {

  private final String filterListBaseUrl =
      "/v1/buyers/" + RequestParams.BUYER_PID + "/filter-lists";
  private final String filterListDomainBaseUrl =
      filterListBaseUrl + "/" + RequestParams.FILTER_LIST_ID + "/domains";
  @Autowired private Request request;

  public Request getGetFilterListsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            filterListBaseUrl
                + "/?qt="
                + RequestParams.QT
                + "&qf="
                + RequestParams.QF
                + "&filterListType="
                + RequestParams.FILTER_LIST_TYPE);
  }

  public Request getGetFilterListRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(filterListBaseUrl + "/" + RequestParams.FILTER_LIST_ID);
  }

  public Request getCreateFilterListRequest() {
    return request.clear().setPostStrategy().setUrlPattern(filterListBaseUrl);
  }

  public Request getDeleteFilterListRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(filterListBaseUrl + "/" + RequestParams.FILTER_LIST_ID);
  }

  public Request getGetFilterListDomainsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            filterListDomainBaseUrl + "/?qt=" + RequestParams.QT + "&qf=" + RequestParams.QF);
  }
}
