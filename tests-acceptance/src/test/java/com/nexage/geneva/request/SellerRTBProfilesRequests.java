package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellerRTBProfilesRequests {

  @Autowired private Request request;

  public Request getRTBprofilesForSeller() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/sellers/" + RequestParams.SELLER_PID + "/rtb-profiles");
  }

  public Request getRTBprofilesFiltered() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.SELLER_PID
                + "/rtb-profiles?qf="
                + RequestParams.QF
                + "&qt="
                + RequestParams.QT);
  }

  public Request getUpdateRtbProfileRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/v1/sellers/" + RequestParams.SELLER_PID + "/rtb-profiles/" + RequestParams.RTB_PID);
  }
}
