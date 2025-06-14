package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.nexage.app.dto.PerformanceMetricDTO;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PerformanceMetricDTOControllerIT {

  @InjectMocks private PerformanceMetricDTOController controller;
  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  private MockMvc mockMvc;
  @Autowired private CustomViewLayerObjectMapper mapper;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void createPreformanceMetricsTest() throws Throwable {
    PerformanceMetricDTO performanceMetricDTO =
        new PerformanceMetricDTO(
            123,
            "Manager",
            "potato",
            123,
            "SELLER",
            "v1/potato",
            "v1/yam",
            new Date(),
            new Date(),
            156,
            "Chrome");

    MvcResult result =
        mockMvc
            .perform(
                post("/v1/performance-metrics")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(performanceMetricDTO)))
            .andReturn();
    PerformanceMetricDTO actualResult =
        mapper.readValue(result.getResponse().getContentAsString(), PerformanceMetricDTO.class);

    assertEquals(actualResult, performanceMetricDTO);
  }
}
