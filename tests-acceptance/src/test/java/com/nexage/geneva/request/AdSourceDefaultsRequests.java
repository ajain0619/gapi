package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.AdSourceDefaultsIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdSourceDefaultsRequests {

  @Autowired private Request request;

  public Request getGetAllMediationAdSourceDefaultsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID + "/adsourcedefaults")
        .setExpectedObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.expectedObjectGetMediation)
        .setActualObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.actualObjectGetMediation);
  }

  public Request getGetMediationAdSourceDefaultsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/adsourcedefaults/adsource/"
                + RequestParams.ADSOURCE_PID)
        .setExpectedObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.expectedObjectGetMediation)
        .setActualObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.actualObjectGetMediation);
  }

  public Request getUpdateMediationAdSourceDefaultsRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.expectedObjectUpdateMediation)
        .setActualObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.actualObjectUpdateMediation)
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/adsourcedefaults/adsource/"
                + RequestParams.ADSOURCE_PID);
  }

  public Request getCreateMediationAdSourceDefaultsRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setExpectedObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.expectedObjectCreateMediation)
        .setActualObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.actualObjectCreateMediation)
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/adsourcedefaults/adsource/"
                + RequestParams.ADSOURCE_PID);
  }

  public Request getDeleteMediationAdSourceDefaultsRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.SELLER_PID
                + "/adsourcedefaults/adsource/"
                + RequestParams.ADSOURCE_PID);
  }

  public Request getGetRtbAdSourceDefaultsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/pss/" + RequestParams.SELLER_PID);
  }

  public Request getUpdateRtbAdSourceDefaultsRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setExpectedObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.expectedObjectUpdateRtb)
        .setActualObjectIgnoredKeys(AdSourceDefaultsIgnoredKeys.actualObjectUpdateRtb)
        .setUrlPattern("/pss/" + RequestParams.SELLER_PID);
  }
}
