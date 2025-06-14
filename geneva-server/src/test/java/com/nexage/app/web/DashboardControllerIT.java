package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.services.DashboardService;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric.BuyerDashboardMetrics;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric.SellerDashboardMetrics;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardSummary;
import com.nexage.dw.geneva.util.ISO8601Util;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class DashboardControllerIT {

  private static final Long REQUESTS = 21505235206L;
  private static final Long SERVED = 1629521481L;
  private static final Long CLICKS = 34510980L;
  private static final BigDecimal REVENUE = new BigDecimal("1938901.60795610");
  private static final Long DISPLAYED = 1454288882L;

  private MockMvc mockMvc;

  @Mock private DashboardService dashboardService;

  @InjectMocks private DashboardController dashboardController;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
  }

  @Test
  void testDashboardController() throws Exception {
    String start = "2013-12-12T08:15:30-05:00";
    String stop = "2013-12-01T08:15:30-05:00";
    boolean trend = false;

    when(dashboardService.getSellerDashboard(
            ISO8601Util.parse(start), ISO8601Util.parse(stop), trend))
        .thenReturn(getSellerData());
    mockMvc
        .perform(
            get("/dashboard")
                .param("type", "SELLER")
                .param("start", start)
                .param("stop", stop)
                .param("trend", "false"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.sellerMetrics.requests.summary", is(REQUESTS)))
        .andExpect(jsonPath("$.sellerMetrics.clicks.summary", is(CLICKS.intValue())));
  }

  private DashboardMetric getSellerData() {
    SellerDashboardSummary sellerMetric =
        new SellerDashboardSummary(REQUESTS, SERVED, CLICKS, DISPLAYED, REVENUE);
    BuyerDashboardSummary buyerMetrics =
        new BuyerDashboardSummary(0L, 0L, 0L, 0L, new BigDecimal("0.0"));

    SellerDashboardMetrics seller =
        new SellerDashboardMetrics(sellerMetric, new ArrayList<SellerDashboardDetail>());
    BuyerDashboardMetrics buyer =
        new BuyerDashboardMetrics(buyerMetrics, new ArrayList<BuyerDashboardDetail>());

    return new DashboardMetric(seller, buyer);
  }
}
