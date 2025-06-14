package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.ReportIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportRequests {
  @Autowired private Request request;

  public Request getGetMetadataRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/reports/metadata");
  }

  public Request getGetReportRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(ReportIgnoredKeys.expectedObjectGet)
        .setActualObjectIgnoredKeys(ReportIgnoredKeys.actualObjectGet)
        .setUrlPattern(
            "/reports/"
                + RequestParams.REPORT_METADATA_ID
                + "/"
                + RequestParams.REPORT_CATEGORY
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + RequestParams.REPORT_DRILLDOWN
                + RequestParams.REPORT_FILTER
                + RequestParams.REPORT_COMPANY_FILTER);
  }

  public Request getGetReportRequestNoIgnoreBotFraud() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/reports/"
                + RequestParams.REPORT_METADATA_ID
                + "/"
                + RequestParams.REPORT_CATEGORY
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + RequestParams.REPORT_DRILLDOWN
                + RequestParams.REPORT_FILTER
                + RequestParams.REPORT_COMPANY_FILTER);
  }

  public Request getGetHelperReportRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/reports/"
                + RequestParams.REPORT_METADATA_ID
                + "/"
                + RequestParams.REPORT_CATEGORY
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + RequestParams.REPORT_DRILLDOWN
                + RequestParams.REPORT_COMPANY_FILTER);
  }
}
