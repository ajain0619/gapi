package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KpiBoxesAndChartingRequests {

  @Autowired private Request request;

  public Request getGetPublisherCompleteMetricsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.PUBLISHER_PID
                + "/dashboardsummary"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP);
  }

  public Request getGetPublisherSummaryGraphMetricsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/publisher/"
                + RequestParams.PUBLISHER_PID
                + "/metrics/summarychart"
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + "&interval="
                + RequestParams.INTERVAL);
  }
}
