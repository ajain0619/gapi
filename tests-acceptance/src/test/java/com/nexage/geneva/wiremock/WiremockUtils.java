package com.nexage.geneva.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nexage.geneva.config.TestConfiguration;
import com.nexage.geneva.config.properties.GenevaCrudProperties;
import com.nexage.geneva.util.CaptureStateTransformer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WiremockUtils {

  @Value("${spring.profiles.active}")
  private String[] activeProfile;

  private static Logger logger = LoggerFactory.getLogger(TestConfiguration.class);

  WireMockServer wm;

  public void startWiremock(GenevaCrudProperties gcp) {
    if (!isRemoteServer(activeProfile)) {
      logger.info("WireMockServer on port: {}", gcp.getCrudWmPort());
      wm =
          new WireMockServer(
              new WireMockConfiguration()
                  .port(Integer.parseInt(gcp.getCrudWmPort()))
                  .extensions(new CaptureStateTransformer()));
      wm.start();
      logger.info("WireMockServer was successfully started");
      WireMock.configureFor(wm.port());
    } else {
      logger.info(
          "WireMockServer on host: {} - port: {}", gcp.getCrudWmHost(), gcp.getCrudWmPort());
      WireMock.configureFor(gcp.getCrudWmHost(), Integer.parseInt(gcp.getCrudWmPort()));
      logger.info("WireMockServer was successfully connected");
    }
  }

  public void stopWiremock() {
    if (!isRemoteServer(activeProfile)) {
      if (wm != null) wm.stop();
    }
  }

  public static boolean isRemoteServer(String[] activeProfile) {
    return activeProfile != null && Arrays.asList(activeProfile).contains("aws");
  }
}
