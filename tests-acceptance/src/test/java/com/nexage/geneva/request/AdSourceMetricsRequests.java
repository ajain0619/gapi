package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdSourceMetricsRequests {

  @Autowired private Request request;

  public Request getGetAdSourceMetricsForAdSourceRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.SELLER_PID
                + "/adSourceMetrics?adsource="
                + RequestParams.ADSOURCE_PID
                + "&start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + "&interval="
                + RequestParams.INTERVAL);
  }

  public Request getGetAdSourceMetricsForSiteAndAdSourceRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/adSourceMetrics?adsource="
                + RequestParams.ADSOURCE_PID
                + "&start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + "&interval="
                + RequestParams.INTERVAL);
  }

  public Request getGetAdSourceMetricsForSiteAndPositionAndAdSourceRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/placement/"
                + RequestParams.PLACEMENT_NAME
                + "/adSourceMetrics?adsource="
                + RequestParams.ADSOURCE_PID
                + "&start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + "&interval="
                + RequestParams.INTERVAL);
  }

  public Request getGetAdSourceMetricsForSiteAndPositionAndTagAndAdSourceRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.SELLER_PID
                + "/site/"
                + RequestParams.SITE_PID
                + "/placement/"
                + RequestParams.PLACEMENT_NAME
                + "/tag/"
                + RequestParams.TAG_PID
                + "/adSourceMetrics?adsource="
                + RequestParams.ADSOURCE_PID
                + "&start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + "&interval="
                + RequestParams.INTERVAL);
  }
}
