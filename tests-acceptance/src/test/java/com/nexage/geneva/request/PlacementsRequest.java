package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.PlacementIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlacementsRequest {

  @Autowired private Request request;

  public Request getPlacementRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements?size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&placementType="
                + RequestParams.PLACEMENT_TYPE
                + "&status="
                + RequestParams.STATUS);
  }

  public Request getPlacementRequestMinimal() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements?size="
                + RequestParams.SIZE
                + "&minimal="
                + RequestParams.MIMIMAL
                + "&page="
                + RequestParams.PAGE);
  }

  public Request getPlacementRequestMinimalQT() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements?size="
                + RequestParams.SIZE
                + "&minimal="
                + RequestParams.MIMIMAL
                + "&page="
                + RequestParams.PAGE
                + "&qt="
                + RequestParams.QT);
  }

  public Request getPlacementQueryRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/placements?size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&qt="
                + RequestParams.QT
                + "&placementType="
                + RequestParams.PLACEMENT_TYPE
                + "&status="
                + RequestParams.STATUS);
  }

  public Request getPlacementSummaryRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements/summaries?size="
                + RequestParams.SIZE
                + "&page="
                + RequestParams.PAGE
                + "&startDate="
                + RequestParams.START_DATE
                + "&stopDate="
                + RequestParams.STOP_DATE);
  }

  public Request getCreatePlacementRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.SELLER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements")
        .setActualObjectIgnoredKeys(PlacementIgnoredKeys.actualObjectCreatePlacement)
        .setExpectedObjectIgnoredKeys(PlacementIgnoredKeys.expectedObjectCreatePlacement);
  }

  public Request getUpdatePlacementRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.SELLER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements/"
                + RequestParams.PLACEMENT_ID)
        .setActualObjectIgnoredKeys(PlacementIgnoredKeys.actualObjectCreatePlacement)
        .setExpectedObjectIgnoredKeys(PlacementIgnoredKeys.expectedObjectCreatePlacement);
  }
}
