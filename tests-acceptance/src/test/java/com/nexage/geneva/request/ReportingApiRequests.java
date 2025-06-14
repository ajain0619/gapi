package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.ReportingApiIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportingApiRequests {

  @Autowired private Request request;

  public Request getGetAuthenticationRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/" + RequestParams.COMPANY_ID);
  }

  public Request getUpdateReportingCredentials() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/pss/" + RequestParams.COMPANY_PID + "/reportingcredentials");
  }

  public Request getGetReportApiRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setExpectedObjectIgnoredKeys(ReportingApiIgnoredKeys.expectedObjectGet)
        .setActualObjectIgnoredKeys(ReportingApiIgnoredKeys.actualObjectGet)
        .setUrlPattern(
            "/"
                + RequestParams.COMPANY_ID
                + "/reports/"
                + RequestParams.REPORT_ID
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + RequestParams.REPORT_DRILLDOWN
                + RequestParams.REPORT_FILTER);
  }

  public Request getGetReportApiNotIgnoreBotFraudRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/"
                + RequestParams.COMPANY_ID
                + "/reports/"
                + RequestParams.REPORT_ID
                + "?start="
                + RequestParams.START
                + "&stop="
                + RequestParams.STOP
                + RequestParams.REPORT_DRILLDOWN
                + RequestParams.REPORT_FILTER);
  }

  public Request getGetAuthenticationTestRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/" + RequestParams.COMPANY_ID + "/test");
  }
}
