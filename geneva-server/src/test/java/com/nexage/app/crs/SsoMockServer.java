package com.nexage.app.crs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;

public class SsoMockServer {
  private static final String ACCESS_TOKEN = "3f94eb47-a295-4977-a375-e27bea5c828b";

  private WireMockServer wm;

  public SsoMockServer(int port) {
    wm = new WireMockServer(port);
    wm.stubFor(
        post(urlEqualTo("/identity/oauth2/access_token"))
            .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded;charset=UTF-8"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(getResponseBody())));
  }

  public void start() {
    wm.start();
  }

  public void shutdown() {
    wm.shutdown();
  }

  private String getResponseBody() {
    return "{\"access_token\": \""
        + ACCESS_TOKEN
        + "\", \"scope\": \"one\", \"token_type\": \"Bearer\", \"expires_in\": 599}";
  }
}
