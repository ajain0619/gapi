package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerCreativeContentService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
class SellerCreativeContentControllerIT {

  private static final String CREATIVE_TESTING_ENABLED = "geneva.server.creative.testing.enabled";
  private MockMvc mockMvc;

  @Mock private Environment environment;
  @Mock private SellerCreativeContentService sellerCreativeContentService;
  @InjectMocks private SellerCreativeContentController sellerCreativeContentController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerCreativeContentController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldFetchCreativeWithCrsId() throws Throwable {
    when(environment.getProperty(CREATIVE_TESTING_ENABLED)).thenReturn("true");
    String creativeContent = "content";
    when(sellerCreativeContentService.getCreativeContent(any(Long.class), any(String.class)))
        .thenReturn(creativeContent);

    MvcResult result =
        mockMvc
            .perform(get("/v1/sellers/1/creatives/1"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        SellerCreativeContentController.SecureHttpHeader.CSP.getName(),
                        SellerCreativeContentController.SecureHttpHeader.CSP.getValue()))
            .andReturn();

    String fetchedCreativeContent = result.getResponse().getContentAsString();
    assertEquals(creativeContent, fetchedCreativeContent);
  }

  @Test
  void shouldFailCreativeNotFound() throws Throwable {
    when(environment.getProperty(CREATIVE_TESTING_ENABLED)).thenReturn("true");
    when(sellerCreativeContentService.getCreativeContent(any(Long.class), any(String.class)))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND));

    MvcResult result =
        mockMvc
            .perform(get("/v1/sellers/1/creatives/1"))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Not Found"))
            .andExpect(
                header()
                    .string(
                        SellerCreativeContentController.SecureHttpHeader.CSP.getName(),
                        (String) null))
            .andReturn();

    String fetchedCreativeContent = result.getResponse().getContentAsString();
    assertTrue(fetchedCreativeContent.isEmpty());
  }

  @Test
  void shouldFailCreativeTestingDisabled() throws Throwable {
    when(environment.getProperty(CREATIVE_TESTING_ENABLED)).thenReturn("false");
    String creativeContent = "content";
    when(sellerCreativeContentService.getCreativeContent(any(Long.class), any(String.class)))
        .thenReturn(creativeContent);

    MvcResult result =
        mockMvc
            .perform(get("/v1/sellers/1/creatives/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("Unauthorized"))
            .andExpect(
                header()
                    .string(
                        SellerCreativeContentController.SecureHttpHeader.CSP.getName(),
                        (String) null))
            .andReturn();

    String fetchedCreativeContent = result.getResponse().getContentAsString();
    assertTrue(fetchedCreativeContent.isEmpty());
  }

  @Test
  void shouldThrowAnyException() throws Throwable {
    when(environment.getProperty(CREATIVE_TESTING_ENABLED)).thenReturn("true");
    when(sellerCreativeContentService.getCreativeContent(any(Long.class), any(String.class)))
        .thenThrow(new RuntimeException());

    MvcResult result =
        mockMvc
            .perform(get("/v1/sellers/1/creatives/1"))
            .andExpect(status().isInternalServerError())
            .andExpect(status().reason("Internal Server Error"))
            .andExpect(
                header()
                    .string(
                        SellerCreativeContentController.SecureHttpHeader.CSP.getName(),
                        (String) null))
            .andReturn();

    String fetchedCreativeContent = result.getResponse().getContentAsString();
    assertTrue(fetchedCreativeContent.isEmpty());
  }
}
