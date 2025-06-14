package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.AdNetIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdNetRequests {

  @Autowired private Request request;

  public Request getGetAllAdNetsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/buyers/adsourcesummaries");
  }

  public Request getGetAllAdNetsForBuyerRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/buyers/" + RequestParams.BUYER_PID + "/adsources");
  }

  public Request getGetAdNetRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/buyers/adsources/" + RequestParams.ADSOURCE_PID);
  }

  public Request getCreateAdNetRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(AdNetIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(AdNetIgnoredKeys.actualObjectCreate)
        .setUrlPattern("/buyers/" + RequestParams.BUYER_PID + "/adsources/");
  }

  public Request getUpdateAdNetRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(AdNetIgnoredKeys.actualAndExpectedObjectUpdate)
        .setActualObjectIgnoredKeys(AdNetIgnoredKeys.actualAndExpectedObjectUpdate)
        .setUrlPattern(
            "/buyers/" + RequestParams.BUYER_PID + "/adsources/" + RequestParams.ADSOURCE_PID);
  }

  public Request getDeleteAdNetRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/buyers/adsources/" + RequestParams.ADSOURCE_PID);
  }
}
