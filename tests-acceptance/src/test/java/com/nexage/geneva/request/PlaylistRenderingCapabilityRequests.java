package com.nexage.geneva.request;

import org.springframework.stereotype.Component;

@Component
public class PlaylistRenderingCapabilityRequests {

  private static final String PATH = "/v1/playlist-rendering-capabilities";

  private final Request request;

  PlaylistRenderingCapabilityRequests(Request request) {
    this.request = request;
  }

  public Request getSdkCapabilities() {
    return request.clear().setGetStrategy().setUrlPattern(PATH);
  }

  public Request getSdkCapabilitiesWithCustomPaging() {
    String url = String.format("%s?page=%s&size=%s", PATH, RequestParams.PAGE, RequestParams.SIZE);
    return request.clear().setGetStrategy().setUrlPattern(url);
  }
}
