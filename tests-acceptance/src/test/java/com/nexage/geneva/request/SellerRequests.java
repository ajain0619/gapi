package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellerRequests {

  @Autowired private Request request;

  public Request getSellerByPidRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.SELLER_PID);
  }

  public Request getSellerSummariesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/sellers/summaries?size="
                    + RequestParams.SIZE
                    + "&page="
                    + RequestParams.PAGE
                    + "&qf="
                    + RequestParams.QF
                    + "&qt="
                    + RequestParams.QT
                    + "&startDate="
                    + RequestParams.START_DATE
                    + "&stopDate="
                    + RequestParams.STOP_DATE));
  }

  public Request getSellersByPidRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/sellers?size="
                    + RequestParams.SIZE
                    + "&page="
                    + RequestParams.PAGE
                    + "&qf="
                    + RequestParams.QF
                    + "&qt="
                    + RequestParams.QT));
  }
}
