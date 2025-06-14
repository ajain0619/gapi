package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellerSeatRequests {

  @Autowired private Request request;

  public Request getGetAllSellerSeatsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/seller-seats?assignable=false");
  }

  public Request getCreateSellerSeatRequest() {
    return request.clear().setPostStrategy().setUrlPattern("/v1/seller-seats");
  }

  public Request getGetSellerSeatRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/seller-seats/" + RequestParams.SELLERSEAT_PID);
  }

  public Request getUpdateSellerSeatRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/v1/seller-seats/" + RequestParams.SELLERSEAT_PID);
  }

  public Request getCreateSellerSeatRuleRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/seller-seats/" + RequestParams.SELLERSEAT_PID + "/rules");
  }

  public Request getUpdateSellerSeatRuleRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/v1/seller-seats/"
                + RequestParams.SELLERSEAT_PID
                + "/rules/"
                + RequestParams.SELLERSEATRULE_PID);
  }

  public Request getGetAllSellerSeatRulesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/seller-seats/" + RequestParams.SELLERSEAT_PID + "/rules");
  }

  public Request getGetAllSellerSeatRulesWithSearchRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/seller-seats/"
                + RequestParams.SELLERSEAT_PID
                + "/rules"
                + "?qf="
                + RequestParams.QF
                + "&qt="
                + RequestParams.QT);
  }

  public Request getGetSellerSeatRule() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/seller-seats/"
                + RequestParams.SELLERSEAT_PID
                + "/rules/"
                + RequestParams.SELLERSEATRULE_PID);
  }

  public Request getSellerSeatSummariesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/seller-seats/"
                    + RequestParams.SELLERSEAT_PID
                    + "/summaries?size="
                    + RequestParams.SIZE
                    + "&page="
                    + RequestParams.PAGE
                    + "&startDate="
                    + RequestParams.START_DATE
                    + "&stopDate="
                    + RequestParams.STOP_DATE));
  }

  public Request getSellerSeatsByQueryFieldNameAndQueryTermContainingStringRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/seller-seats?page=0&size=1000&qf=name"
                + "&qt="
                + RequestParams.QT
                + "&assignable="
                + RequestParams.ASSIGNABLE);
  }
}
