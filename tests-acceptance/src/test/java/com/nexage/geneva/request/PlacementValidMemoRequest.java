package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlacementValidMemoRequest {

  @Autowired private Request request;

  public Request getPlacementValidMemoRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.COMPANY_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements/valid-memos?memo="
                + RequestParams.PLACEMENT_MEMO
                + "&companyPid="
                + RequestParams.COMPANY_PID
                + "&placementMemo="
                + RequestParams.PLACEMENT_MEMO
                + "&sitePid="
                + RequestParams.SITE_PID);
  }
}
