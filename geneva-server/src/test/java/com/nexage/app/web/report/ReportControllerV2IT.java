package com.nexage.app.web.report;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.countryservice.CountryService;
import com.ssp.geneva.server.report.report.ReportService;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueResponse;
import java.io.InputStream;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class ReportControllerV2IT extends BaseControllerItTest {
  private final String BASE_URL = "/reports";

  @Mock private ReportService reportService;

  @Mock protected UserContext userContext;

  @Mock private CountryService countryService;

  CustomViewLayerObjectMapper objectMapper = new CustomViewLayerObjectMapper();

  @InjectMocks private ReportControllerV2 reportController;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(reportController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  @SneakyThrows
  void getSellerRevenueResponseWithDim() {

    String path = "/fed87f460dfa11e598a100da44c6171d/seller";
    String data = ResourcePath.SELLER_REVENUE_DIM_DAY.getFilePath();
    String startDate = "2020-03-01T00:00:00-05:00";
    String endDate = "2020-03-03T00:00:00-05:00";

    List<SellerRevenueResponse> sellerRevenueResponses;
    try (InputStream inputStream = ResourceLoader.getResourceAsStream(data)) {
      sellerRevenueResponses = objectMapper.readValue(inputStream, new TypeReference<>() {});
      when(reportService.getSellerRevenueReport(any())).thenReturn(sellerRevenueResponses);
    }

    this.mockMvc
        .perform(
            get(BASE_URL + path)
                .param("start", startDate)
                .param("stop", endDate)
                .param("dim", "day")
                .param("tag", "1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].adRequests", is(0)))
        .andExpect(jsonPath("$[0].adServed", is(1)))
        .andExpect(jsonPath("$[0].outboundRequest", is(false)))
        .andExpect(jsonPath("$[0].interval", is("2020-03-01 00:00:00")))
        .andExpect(jsonPath("$[1].adRequests", is(0)))
        .andExpect(jsonPath("$[0].adServed", is(1)))
        .andExpect(jsonPath("$[1].outboundRequest", is(false)))
        .andExpect(jsonPath("$[1].interval", is("2020-03-02 00:00:00")))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  void getSellerRevenueResponseWithoutDim() {

    String path = "/fed87f460dfa11e598a100da44c6171d/seller";
    String data = ResourcePath.SELLER_REVENUE_NO_DIM.getFilePath();
    String startDate = "2020-03-01T00:00:00-05:00";
    String endDate = "2020-03-02T00:00:00-05:00";

    List<SellerRevenueResponse> sellerRevenueResponses;
    try (InputStream inputStream = ResourceLoader.getResourceAsStream(data)) {
      sellerRevenueResponses = objectMapper.readValue(inputStream, new TypeReference<>() {});
      when(reportService.getSellerRevenueReport(any())).thenReturn(sellerRevenueResponses);
    }

    this.mockMvc
        .perform(
            get(BASE_URL + path).param("start", startDate).param("stop", endDate).param("tag", "1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].adRequests", is(0)))
        .andExpect(jsonPath("$[0].adServed", is(1)))
        .andExpect(jsonPath("$[0].outboundRequest", is(false)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Getter
  enum ResourcePath {
    SELLER_REVENUE_DIM_DAY(
        "/data/controllers/reports/fed87f460dfa11e598a100da44c6171d_seller_dim_day_ER.json"),
    SELLER_REVENUE_NO_DIM(
        "/data/controllers/reports/fed87f460dfa11e598a100da44c6171d_seller_no_dim_ER.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
