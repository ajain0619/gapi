package com.ssp.geneva.common.test.junit.extension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** WireMock Extension for JUnit 5. */
public class WireMockExtension extends WireMockServer
    implements BeforeAllCallback, AfterAllCallback {

  public WireMockExtension(Options options) {
    super(options);
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    this.start();
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    this.stop();
    this.resetAll();
  }
}
