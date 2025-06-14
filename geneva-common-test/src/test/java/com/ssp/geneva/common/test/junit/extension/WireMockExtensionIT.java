package com.ssp.geneva.common.test.junit.extension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class WireMockExtensionIT {

  @RegisterExtension
  static WireMockExtension wireMockExtension =
      new WireMockExtension(options().bindAddress("127.0.0.1").port(9999));

  HttpClient client = HttpClient.newHttpClient();

  @Test
  void testExtension() throws IOException, InterruptedException {
    final int statusCode = 200;
    final String message = "pong";
    wireMockExtension.stubFor(
        get(urlPathMatching("/ping"))
            .willReturn(aResponse().withStatus(statusCode).withBody(message)));
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:9999/ping"))
            .timeout(Duration.ofMinutes(1))
            .GET()
            .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    assertEquals(statusCode, response.statusCode(), "Response Status code is the expected one");
    assertEquals(message, response.body(), "Response Message is the expected one");
  }
}
