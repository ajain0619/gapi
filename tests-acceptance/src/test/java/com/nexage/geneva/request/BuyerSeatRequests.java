package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.BuyerSeatIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyerSeatRequests {

  @Autowired private Request request;

  public Request getCreateBuyerSeatRequest() {
    return request
        .clear()
        .setExpectedObjectIgnoredKeys(BuyerSeatIgnoredKeys.expectedObjectCreate)
        .setPostStrategy()
        .setUrlPattern("/buyers/" + RequestParams.COMPANY_PID + "/buyerseats/");
  }

  public Request getGetAllBuyerSeatsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(BuyerSeatIgnoredKeys.expectedObjectGet)
        .setExpectedObjectIgnoredKeys(BuyerSeatIgnoredKeys.expectedObjectGet)
        .setUrlPattern("/buyers/" + RequestParams.COMPANY_PID + "/buyerseats/");
  }

  public Request getGetAllBuyerSeatsRequestByQFQT() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(BuyerSeatIgnoredKeys.expectedObjectGet)
        .setExpectedObjectIgnoredKeys(BuyerSeatIgnoredKeys.expectedObjectGet)
        .setUrlPattern(
            String.format(
                "/buyers/" + RequestParams.COMPANY_PID + "/buyerseats?qf=%s&qt=%s",
                RequestParams.QF,
                RequestParams.QT));
  }

  public Request getUpdateBuyerSeatRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(BuyerSeatIgnoredKeys.expectedObjectUpdate)
        .setUrlPattern(
            "/buyers/"
                + RequestParams.COMPANY_PID
                + "/buyerseats/"
                + RequestParams.BUYER_SEAT_PID
                + "/");
  }
}
