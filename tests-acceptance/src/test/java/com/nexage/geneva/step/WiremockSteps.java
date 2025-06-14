package com.nexage.geneva.step;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nexage.geneva.config.TestConfiguration;
import com.nexage.geneva.config.properties.GenevaCrudProperties;
import com.nexage.geneva.util.CaptureStateTransformer;
import com.nexage.geneva.wiremock.WiremockUtils;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WiremockSteps {
  private static Logger logger = LoggerFactory.getLogger(TestConfiguration.class);

  @Autowired private GenevaCrudProperties genevaCrudProperties;
  @Autowired private WiremockUtils wiremockUtils;

  @When("^the wiremock server is restarted$")
  public void restart_wiremock_server() throws InterruptedException {
    wiremockUtils.stopWiremock();
    Thread.sleep(3000);
    WireMockServer wm =
        new WireMockServer(
            new WireMockConfiguration()
                .port(Integer.parseInt(genevaCrudProperties.getCrudWmHost()))
                .extensions(new CaptureStateTransformer()));
    wm.start();
    WireMock.configureFor(
        genevaCrudProperties.getCrudWmHost(),
        Integer.parseInt(genevaCrudProperties.getCrudWmPort()));
    logger.info("Wiremock server was successfully restarted");
  }
}
