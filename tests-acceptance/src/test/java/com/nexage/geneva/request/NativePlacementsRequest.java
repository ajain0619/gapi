package com.nexage.geneva.request;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NativePlacementsRequest {

  private static final Map<String, String> NATIVE_PLACEMENT_HEADERS =
      new HashMap<>(
          ImmutableMap.of(
              "Content-type",
              "application/vnd.geneva-api.native+json",
              "Accept",
              "application/vnd.geneva-api.native+json"));

  @Autowired private Request request;

  public Request createRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements")
        .setRequestHeaders(NATIVE_PLACEMENT_HEADERS);
  }

  public Request getRequest() {
    return request
        .setGetStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements/"
                + RequestParams.POSITION_ID)
        .setRequestHeaders(NATIVE_PLACEMENT_HEADERS);
  }

  public Request updateRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/v1/sellers/"
                + RequestParams.PUBLISHER_PID
                + "/sites/"
                + RequestParams.SITE_PID
                + "/placements/"
                + RequestParams.POSITION_ID)
        .setRequestHeaders(NATIVE_PLACEMENT_HEADERS);
  }
}
