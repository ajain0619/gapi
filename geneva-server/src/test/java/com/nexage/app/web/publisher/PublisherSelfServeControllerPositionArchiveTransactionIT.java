package com.nexage.app.web.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.PositionArchiveTransactionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.util.ResourceLoader;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import lombok.Getter;
import org.apache.commons.lang.math.RandomUtils;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PublisherSelfServeControllerPositionArchiveTransactionIT {

  @Mock private PublisherSelfService publisherSelfService;
  @Mock private SpringUserDetails springUserDetails;
  @Mock private UserContext userContext;
  @InjectMocks PublisherSelfServeController publisherSelfServeController;

  @Autowired ControllerExceptionHandler controllerExceptionHandler;

  private MockMvc mockMvc;
  private final ObjectMapper mapper = new ObjectMapper();

  private static final long PUBLISHER_ID = RandomUtils.nextLong();
  private static final long SITE_ID = RandomUtils.nextLong();
  private static final long POSITION_ID = RandomUtils.nextLong();

  @BeforeEach
  void setUp() {
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(publisherSelfServeController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldGetPositionPerformanceMetricsForArchive() throws Exception {
    String expectedResult =
        getData(ResourcePath.ARCHIVE_NATIVE_PLACEMENT_TAG_PERFORMANCE_RESPONSE.getFilePath());
    PositionArchiveTransactionDTO positionArchiveTransaction =
        mapper.readValue(expectedResult, PositionArchiveTransactionDTO.class);

    when(publisherSelfService.getPositionPerformanceMetricsForArchive(
            anyLong(), anyLong(), anyLong(), anyBoolean()))
        .thenReturn(positionArchiveTransaction);
    MvcResult mvcResult =
        mockMvc
            .perform(
                get(
                        "/pss/{publisher}/site/{site}/position/{position}/positionPerformanceMetrics/archiveTransaction",
                        PUBLISHER_ID,
                        SITE_ID,
                        POSITION_ID)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    assertEquals(expectedResult.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldReturnBadRequestStatusWhenGettingPositionPerformanceMetricsForArchiveFails()
      throws Exception {
    when(publisherSelfService.getPositionPerformanceMetricsForArchive(
            anyLong(), anyLong(), anyLong(), anyBoolean()))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    mockMvc
        .perform(
            get(
                    "/pss/{publisher}/site/{site}/position/{position}/positionPerformanceMetrics/archiveTransaction",
                    PUBLISHER_ID,
                    SITE_ID,
                    POSITION_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldArchivePosition() throws Exception {
    String expectedResult = getData(ResourcePath.ARCHIVE_NATIVE_PLACEMENT_RESPONSE.getFilePath());
    PublisherSiteDTO publisherSite = mapper.readValue(expectedResult, PublisherSiteDTO.class);

    when(publisherSelfService.archivePosition(anyLong(), anyLong(), anyLong(), anyString()))
        .thenReturn(publisherSite);
    MvcResult mvcResult =
        mockMvc
            .perform(
                delete(
                        "/pss/{publisher}/site/{site}/position/{position}?txid={txid}",
                        PUBLISHER_ID,
                        SITE_ID,
                        POSITION_ID,
                        "Test")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    assertEquals(expectedResult.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldReturnNotFoundStatusWhenArchivingNonexistentPosition() throws Exception {
    when(publisherSelfService.archivePosition(anyLong(), anyLong(), anyLong(), anyString()))
        .thenThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE));
    mockMvc
        .perform(
            delete(
                    "/pss/{publisher}/site/{site}/position/{position}?txid={txid}",
                    PUBLISHER_ID,
                    SITE_ID,
                    POSITION_ID,
                    "")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Getter
  private enum ResourcePath {
    ARCHIVE_NATIVE_PLACEMENT_TAG_PERFORMANCE_RESPONSE(
        "/data/publisher_self_serve_controller_it/Archive_Native_Placement_TPM_ER.json"),
    ARCHIVE_NATIVE_PLACEMENT_RESPONSE(
        "/data/publisher_self_serve_controller_it/Archive_Native_Placement_ER.json");

    private final String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }

  private String getData(String name) throws IOException {
    return ResourceLoader.getResource(
        PublisherSelfServeControllerPositionArchiveTransactionIT.class, name);
  }
}
