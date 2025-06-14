package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EstimatedRevenueRequests {

  @Autowired private Request request;

  public Request getEstimatedRevenue() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/estimatedRevenueReport?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getEstimatedRevenueAdnetDrillDown() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/estimatedRevenueReport/drilldown=adnet?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getEstimatedRevenueAdvertiserDrillDown() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/estimatedRevenueReport/drilldown=advertiser?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }
}
