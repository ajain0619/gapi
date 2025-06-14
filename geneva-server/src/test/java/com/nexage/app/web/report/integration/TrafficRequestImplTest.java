package com.nexage.app.web.report.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficRestrictedReportDimension;
import org.junit.jupiter.api.Test;

/**
 * This unit test class tries to verify interface overriding behaviour into the different types of
 * TrafficRequest and its inheritance.
 */
class TrafficRequestImplTest {

  @Test
  void shouldGetProperDimOnTrafficRequest() {
    TrafficRequestImpl trafficRequest = new TrafficRequestImpl();
    TrafficReportDimension trafficReportDimension = TrafficReportDimension.adsource;
    trafficRequest.setDim(trafficReportDimension);

    assertNotNull(trafficRequest.getDim());
    assertEquals(trafficRequest.getDim(), trafficReportDimension);
  }

  @Test
  void shouldGetProperDimOnTrafficRestrictedRequest() {
    TrafficRestrictedRequestImpl trafficRestrictedRequest = new TrafficRestrictedRequestImpl();
    TrafficRestrictedReportDimension trafficRestrictedReportDimension =
        TrafficRestrictedReportDimension.day;
    trafficRestrictedRequest.setDim(trafficRestrictedReportDimension);

    assertNotNull(trafficRestrictedRequest.getDim());
    assertEquals(trafficRestrictedRequest.getDim(), trafficRestrictedReportDimension);
  }
}
