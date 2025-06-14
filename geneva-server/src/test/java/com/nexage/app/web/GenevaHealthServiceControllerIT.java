package com.nexage.app.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.services.health.CoreDatabaseHealthService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
class GenevaHealthServiceControllerIT {

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Mock private CoreDatabaseHealthService coreDatabaseHealthService;

  private MockMvc mockMvc;

  @InjectMocks private GenevaHealthServiceController genevaHealthServiceController;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(genevaHealthServiceController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void shouldReturnOkOnAwsRequest() {
    when(coreDatabaseHealthService.isServiceHealthy()).thenReturn(true);
    mockMvc
        .perform(get(UrlPath.AWS_HEALTH_CHECK.getUrlPath()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void shouldReturnServiceUnavailableOnAwsRequest() {
    when(coreDatabaseHealthService.isServiceHealthy()).thenReturn(false);
    mockMvc
        .perform(get(UrlPath.AWS_HEALTH_CHECK.getUrlPath()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isServiceUnavailable());
  }

  @Getter
  enum UrlPath {
    HEALTH_CHECK("/healthCheck"),
    AWS_HEALTH_CHECK("/awsHealthCheck");

    private final String UrlPath;

    UrlPath(String UrlPath) {
      this.UrlPath = UrlPath;
    }
  }
}
