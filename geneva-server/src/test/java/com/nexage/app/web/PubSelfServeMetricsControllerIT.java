package com.nexage.app.web;

import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.publisher.PublisherMetricsDTO;
import com.nexage.app.services.PubSelfServeMetricsService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PubSelfServeMetricsControllerIT {

  private MockMvc mockMvc;

  @InjectMocks private PubSelfServeMetricsController pubSelfServeMetricsController;

  @Mock private PubSelfServeMetricsService publisherMetricsService;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(pubSelfServeMetricsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getMetricsTest() throws Throwable {
    PublisherMetricsDTO publisherMetrics = TestObjectsFactory.createPublisherMetrics();
    when(publisherMetricsService.getMetrics(
            nullable(Long.class),
            nullable(String.class),
            nullable(String.class),
            nullable(String.class)))
        .thenReturn(publisherMetrics);
    mockMvc
        .perform(get("/publisher/1/metrics/summarychart?start=\"\"&stop=\"\""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("delivered.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("requests.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("served.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("revenue.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("fillRate.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("ctr.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("rpm.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("ecpm.[0]").value(isA(Map.class)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/publisher/1/adSourceMetrics?start=\"\"&stop=\"\"&adsource=1",
        "/publisher/1/site/1/adSourceMetrics?start=\"\"&stop=\"\"&adsource=1",
        "/publisher/1/site/1/placement/1/tag/1/adSourceMetrics?start=\"\"&stop=\"\"&adsource=1"
      })
  void shouldGetAdSourceMetrics(String api) throws Throwable {
    PublisherMetricsDTO publisherMetrics = TestObjectsFactory.createPublisherMetrics();
    when(publisherMetricsService.getAdSourceMetrics(
            nullable(Long.class),
            nullable(Long.class),
            nullable(Long.class),
            nullable(String.class),
            nullable(Long.class),
            nullable(String.class),
            nullable(String.class),
            nullable(String.class)))
        .thenReturn(publisherMetrics);
    mockMvc
        .perform(get(api))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("delivered.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("requests.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("served.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("revenue.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("fillRate.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("ctr.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("rpm.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("ecpm.[0]").value(isA(Map.class)));
  }

  @Test
  void getAdSourceMetricsSiteAndPlacementTest() throws Throwable {
    PublisherMetricsDTO publisherMetrics = TestObjectsFactory.createPublisherMetrics();
    when(publisherMetricsService.getAdSourceMetrics(
            nullable(Long.class),
            nullable(Long.class),
            nullable(Long.class),
            nullable(String.class),
            any(),
            nullable(String.class),
            nullable(String.class),
            nullable(String.class)))
        .thenReturn(publisherMetrics);
    mockMvc
        .perform(
            get("/publisher/1/site/1/placement/1/adSourceMetrics?start=\"\"&stop=\"\"&adsource=1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("requests.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("served.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("revenue.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("fillRate.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("ctr.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("rpm.[0]").value(isA(Map.class)))
        .andExpect(jsonPath("ecpm.[0]").value(isA(Map.class)));
  }
}
