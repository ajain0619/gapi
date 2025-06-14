package com.nexage.app.web.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Contains unit integration tests for position related endpoints in {@link
 * PublisherSelfServeController}.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PublisherSelfServeControllerPositionIT {

  private static final String POSITION_CREATE_URL_TEMPLATE =
      "/pss/{publisher}/site/{siteId}/position";
  private static final String POSITION_GET_URL_TEMPLATE =
      "/pss/{publisher}/site/{siteId}/position/{positionId}";
  private static final String POSITION_DETAILED_URL_TEMPLATE =
      "/pss/{publisher}/site/{site}/position/{position}/detailedPosition";

  private static final long MOCK_SITE_ID = 123;
  private static final long MOCK_PUBLISHER_ID = 456;
  private static final long MOCK_POSITION_ID = 10330;

  private MockMvc mockMvc;

  @Mock private PublisherSelfService publisherSelfService;
  @Mock private UserContext userContext;
  @Mock private SellerLimitService sellerLimitService;
  @Mock private SpringUserDetails springUserDetails;
  @Mock private BeanValidationService beanValidationService;

  @InjectMocks private PublisherSelfServeController publisherSelfServeController;

  @Autowired
  @Qualifier("jsonConverter")
  private MappingJackson2HttpMessageConverter converter;

  @Autowired private CustomViewLayerObjectMapper mapper;
  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @BeforeEach
  public void setUp() throws Exception {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(publisherSelfServeController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setMessageConverters(converter)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldCreatePositionSuccess() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_CREATE_PAYLOAD_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITION_CREATE_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(userContext.isNexageUser()).thenReturn(false);
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(false);
    when(publisherSelfService.createPosition(
            eq(MOCK_SITE_ID), any(PublisherPositionDTO.class), eq(false)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                post(POSITION_CREATE_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID)
                    .content(positionPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isCreated())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldCreatePositionValidationFail() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_CREATE_PAYLOAD_JSON.filePath);

    when(userContext.isNexageUser()).thenReturn(false);
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(true);
    when(sellerLimitService.canCreatePositionsInSite(MOCK_PUBLISHER_ID, MOCK_SITE_ID))
        .thenReturn(false);

    mockMvc
        .perform(
            post(POSITION_CREATE_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID)
                .content(positionPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .param("detail", "false"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldGetPositionTest() throws Exception {
    String expectedResponse = getData(ResourcePath.POSITION_CREATE_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.getPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID, false))
        .thenReturn(output);

    mockMvc
        .perform(
            get(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .param("detail", "false"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldGetPositionWithVideoTest() throws Exception {
    String expectedResponse = getData(ResourcePath.GET_POSITION_WITH_VIDEO_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.getPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID, false))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldGetPositionWithVideoAndCompanionTest() throws Exception {
    String expectedResponse =
        getData(ResourcePath.GET_POSITION_WITH_VIDEO_AND_COMPANION_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.getPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID, false))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldGetPositionsWithVideoTest() throws Exception {
    String firstPosition = getData(ResourcePath.GET_POSITION_WITH_VIDEO_ER_JSON.filePath);
    String secondPosition =
        getData(ResourcePath.GET_POSITION_WITH_VIDEO_AND_COMPANION_ER_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITIONS_WITH_VIDEO_ER_JSON.filePath);

    List<PublisherPositionDTO> output = new ArrayList<>();
    PublisherPositionDTO output1 = mapper.readValue(firstPosition, PublisherPositionDTO.class);
    PublisherPositionDTO output2 = mapper.readValue(secondPosition, PublisherPositionDTO.class);
    output.add(output1);
    output.add(output2);
    when(publisherSelfService.getPositions(MOCK_PUBLISHER_ID, MOCK_SITE_ID, false))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(POSITION_CREATE_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldUpdatePositionSuccess() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_UPDATE_PAYLOAD_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITION_UPDATE_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.updatePosition(
            eq(MOCK_PUBLISHER_ID), eq(MOCK_SITE_ID), any(PublisherPositionDTO.class), eq(false)))
        .thenReturn(output);
    long positionPid = 100329;

    MvcResult mvcRsult =
        mockMvc
            .perform(
                put(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, positionPid)
                    .content(positionPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldUpdatePositionInvalidPid() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_UPDATE_PAYLOAD_JSON.filePath);

    mockMvc
        .perform(
            put(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                .content(positionPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .param("detail", "false"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCopyPositionSuccess() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_UPDATE_PAYLOAD_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITION_UPDATE_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);
    long targetSiteId = 100;

    when(sellerLimitService.canCreatePositionsInSite(MOCK_PUBLISHER_ID, targetSiteId))
        .thenReturn(true);
    when(publisherSelfService.copyPosition(
            eq(MOCK_PUBLISHER_ID),
            eq(MOCK_SITE_ID),
            eq(MOCK_POSITION_ID),
            eq(targetSiteId),
            any(PublisherPositionDTO.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                post(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .content(positionPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("operation", "clone")
                    .param("targetSite", Long.toString(targetSiteId)))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldCopyPositionInvalidOperation() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_UPDATE_PAYLOAD_JSON.filePath);
    long targetSiteId = 100;

    mockMvc
        .perform(
            post(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                .content(positionPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .param("operation", "invalid")
                .param("targetSite", Long.toString(targetSiteId)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCopyPositionSuccessOldDTO() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_CLONE_OLD_DTO_PAYLOAD_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITION_CLONE_OLD_DTO_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);
    long targetSiteId = 100;

    when(sellerLimitService.canCreatePositionsInSite(MOCK_PUBLISHER_ID, targetSiteId))
        .thenReturn(true);
    when(publisherSelfService.copyPosition(
            eq(MOCK_PUBLISHER_ID),
            eq(MOCK_SITE_ID),
            eq(MOCK_POSITION_ID),
            eq(targetSiteId),
            any(PublisherPositionDTO.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                post(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .content(positionPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("operation", "clone")
                    .param("targetSite", Long.toString(targetSiteId)))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldCopyPositionSuccessNewDTO() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_CLONE_PAYLOAD_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITION_CLONE_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);
    long targetSiteId = 100;

    when(sellerLimitService.canCreatePositionsInSite(MOCK_PUBLISHER_ID, targetSiteId))
        .thenReturn(true);
    when(publisherSelfService.copyPosition(
            eq(MOCK_PUBLISHER_ID),
            eq(MOCK_SITE_ID),
            eq(MOCK_POSITION_ID),
            eq(targetSiteId),
            any(PublisherPositionDTO.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                post(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .content(positionPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("operation", "clone")
                    .param("targetSite", Long.toString(targetSiteId)))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldCopyPositionInvalidOperationNew() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_CLONE_PAYLOAD_JSON.filePath);
    long targetSiteId = 100;

    mockMvc
        .perform(
            post(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                .content(positionPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .param("operation", "invalid")
                .param("targetSite", Long.toString(targetSiteId)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCopyPositionWithLongformVideoSuccess() throws Exception {
    String positionPayload = getData(ResourcePath.POSITION_CLONE_LONGFORM_PAYLOAD_JSON.filePath);
    String expectedResponse = getData(ResourcePath.POSITION_CLONE_LONGFORM_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);
    long targetSiteId = 100;

    when(sellerLimitService.canCreatePositionsInSite(MOCK_PUBLISHER_ID, targetSiteId))
        .thenReturn(true);
    when(publisherSelfService.copyPosition(
            eq(MOCK_PUBLISHER_ID),
            eq(MOCK_SITE_ID),
            eq(MOCK_POSITION_ID),
            eq(targetSiteId),
            any(PublisherPositionDTO.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                post(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .content(positionPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("operation", "clone")
                    .param("targetSite", Long.toString(targetSiteId)))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldGetdetailedPositionWithoutVideoTagTest() throws Exception {
    String expectedResponse = getData(ResourcePath.POSITION_CREATE_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.detailedPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(
                        POSITION_DETAILED_URL_TEMPLATE,
                        MOCK_PUBLISHER_ID,
                        MOCK_SITE_ID,
                        MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldGetdetailedPositionWithVideoTagTest() throws Exception {
    String expectedResponse = getData(ResourcePath.DETAILED_POSITION_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.detailedPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID))
        .thenReturn(output);
    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(
                        POSITION_DETAILED_URL_TEMPLATE,
                        MOCK_PUBLISHER_ID,
                        MOCK_SITE_ID,
                        MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  void shouldGetdetailedPositionWithLongformVideoTagTest() throws Exception {
    String expectedResponse = getData(ResourcePath.DETAILED_POSITION_LONGFORM_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.detailedPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID))
        .thenReturn(output);
    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(
                        POSITION_DETAILED_URL_TEMPLATE,
                        MOCK_PUBLISHER_ID,
                        MOCK_SITE_ID,
                        MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  @SneakyThrows
  void shouldNotAllowedToCreateDapPublisherPositionWithoutVideoPlacementType() {
    String positionPayload =
        getData(ResourcePath.CREATE_PLACEMENT_WITHOUT_VIDEO_PLACEMENT_TYPE_PAYLOAD_JSON.filePath);
    long sitePid = 10000174;
    long publisherPid = 10201;
    when(userContext.isNexageUser()).thenReturn(true);

    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(PublisherPositionDTO.class), any(), any());

    mockMvc
        .perform(
            post(POSITION_CREATE_URL_TEMPLATE, publisherPid, sitePid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(positionPayload))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldCreatePublisherPositionWithDapParams200OK() {
    String positionPayload =
        getData(ResourcePath.CREATE_PLACEMENT_WITH_DAP_PARAMS_PAYLOAD_JSON.filePath);
    long sitePid = 10000174;
    long publisherPid = 10201;
    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            post(POSITION_CREATE_URL_TEMPLATE, publisherPid, sitePid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(positionPayload))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @SneakyThrows
  void shouldNotAllowedToUpdatePublisherPositionWithInvalidDapParam() {
    String positionPayload =
        getData(ResourcePath.UPDATE_DAP_PLACEMENT_WITH_INVALID_DAP_PARAMS_PAYLOAD_JSON.filePath);
    long positionPid = 100251;
    long sitePid = 10000174;
    long publisherPid = 10201;
    when(userContext.isNexageUser()).thenReturn(true);

    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), any(), any());

    mockMvc
        .perform(
            put(POSITION_GET_URL_TEMPLATE, publisherPid, sitePid, positionPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(positionPayload))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldUpdatePublisherPositionWithDapParam() {
    String positionPayload =
        getData(ResourcePath.UPDATE_DAP_PLACEMENT_WITH_VALID_DAP_PARAMS_PAYLOAD_JSON.filePath);
    long positionPid = 100251;
    long sitePid = 10000174;
    long publisherPid = 10201;
    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            put(POSITION_GET_URL_TEMPLATE, publisherPid, sitePid, positionPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(positionPayload))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @SneakyThrows
  void shouldCopyPublisherPositionWithDapParams200OK() {
    String positionPayload =
        getData(ResourcePath.CREATE_PLACEMENT_WITH_DAP_PARAMS_PAYLOAD_JSON.filePath);
    long positionPid = 100251;
    long sitePid = 10000174;
    long publisherPid = 10201;
    when(userContext.isNexageUser()).thenReturn(true);

    when(sellerLimitService.canCreatePositionsInSite(publisherPid, sitePid)).thenReturn(true);
    mockMvc
        .perform(
            post(POSITION_GET_URL_TEMPLATE, publisherPid, sitePid, positionPid)
                .contentType(MediaType.APPLICATION_JSON)
                .param("operation", "clone")
                .param("targetSite", Long.toString(sitePid))
                .content(positionPayload))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @SneakyThrows
  void detailedPositionWithDapParams200OK() {
    String expectedResponse = getData(ResourcePath.GET_DAP_PLACEMENT_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.detailedPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID))
        .thenReturn(output);
    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(
                        POSITION_DETAILED_URL_TEMPLATE,
                        MOCK_PUBLISHER_ID,
                        MOCK_SITE_ID,
                        MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  @SneakyThrows
  void getPositionWithDapParams200OK() {
    String expectedResponse = getData(ResourcePath.GET_DAP_PLACEMENT_ER_JSON.filePath);
    PublisherPositionDTO output = mapper.readValue(expectedResponse, PublisherPositionDTO.class);

    when(publisherSelfService.getPosition(MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID, false))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(POSITION_GET_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID, MOCK_POSITION_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  @Test
  @SneakyThrows
  void getPositionsWithDapParams200OK() {
    String firstPosition = getData(ResourcePath.GET_FIRST_DAP_PLACEMENT_ER_JSON.filePath);
    String secondPosition = getData(ResourcePath.GET_SECOND_DAP_PLACEMENT_ER_JSON.filePath);
    String expectedResponse = getData(ResourcePath.GET_MULTIPLE_DAP_PLACEMENT_ER_JSON.filePath);

    List<PublisherPositionDTO> output = new ArrayList<>();
    PublisherPositionDTO output1 = mapper.readValue(firstPosition, PublisherPositionDTO.class);
    PublisherPositionDTO output2 = mapper.readValue(secondPosition, PublisherPositionDTO.class);
    output.add(output1);
    output.add(output2);
    when(publisherSelfService.getPositions(MOCK_PUBLISHER_ID, MOCK_SITE_ID, false))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(
                get(POSITION_CREATE_URL_TEMPLATE, MOCK_PUBLISHER_ID, MOCK_SITE_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("detail", "false"))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcRsult.getResponse().getContentAsString();
    assertEquals(expectedResponse.replaceAll("[\\t\\n\\s]+", ""), content);
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(PublisherSelfServeControllerIT.class, name), StandardCharsets.UTF_8);
  }

  private enum ResourcePath {
    POSITION_CREATE_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/CreatePositionPayload.json"),
    POSITION_CREATE_ER_JSON("/data/publisher_self_serve_controller_it/CreatePositionER.json"),
    POSITION_UPDATE_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/UpdatePositionPayload.json"),
    POSITION_UPDATE_ER_JSON("/data/publisher_self_serve_controller_it/UpdatePosition_ER.json"),
    DETAILED_POSITION_ER_JSON("/data/publisher_self_serve_controller_it/DetailedPositionER.json"),
    POSITION_CLONE_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/ClonedPositionPayload.json"),
    POSITION_CLONE_ER_JSON("/data/publisher_self_serve_controller_it/ClonedPositionER.json"),
    POSITION_CLONE_LONGFORM_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/ClonedPositionLongformPayload.json"),
    POSITION_CLONE_LONGFORM_ER_JSON(
        "/data/publisher_self_serve_controller_it/ClonedPositionLongformER.json"),
    POSITION_CLONE_OLD_DTO_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/ClonedPositionOldDTOPayload.json"),
    POSITION_CLONE_OLD_DTO_ER_JSON(
        "/data/publisher_self_serve_controller_it/ClonedPositionOldDTOER.json"),
    GET_POSITION_WITH_VIDEO_ER_JSON(
        "/data/publisher_self_serve_controller_it/GetPositionWithVideo_ER.json"),
    GET_POSITION_WITH_VIDEO_AND_COMPANION_ER_JSON(
        "/data/publisher_self_serve_controller_it/GetPositionWithVideoAndCompanion_ER.json"),
    POSITIONS_WITH_VIDEO_ER_JSON(
        "/data/publisher_self_serve_controller_it/positionsWithVideo_ER.json"),
    DETAILED_POSITION_LONGFORM_ER_JSON(
        "/data/publisher_self_serve_controller_it/DetailedPositionLongformER.json"),

    CREATE_PLACEMENT_WITH_DAP_PARAMS_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/CreateDapVideoPlacementVideo_payload.json"),

    CREATE_PLACEMENT_WITH_INVALID_DAP_PARAMS_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/CreateDapPlacementWithInvalidPlacementCategory_payload.json"),

    UPDATE_DAP_PLACEMENT_WITH_INVALID_DAP_PARAMS_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/UpdateDapVideoPlacementVideoWithInvalidPlacementCategory_payload.json"),

    UPDATE_DAP_PLACEMENT_WITH_VALID_DAP_PARAMS_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/UpdateDapVideoPlacementVideo_payload.json"),

    GET_DAP_PLACEMENT_ER_JSON(
        "/data/publisher_self_serve_controller_it/GetDapVideoPlacement_ER.json"),

    GET_FIRST_DAP_PLACEMENT_ER_JSON(
        "/data/publisher_self_serve_controller_it/GetDapVideoPlacementFirst_ER.json"),

    GET_SECOND_DAP_PLACEMENT_ER_JSON(
        "/data/publisher_self_serve_controller_it/GetDapVideoPlacementSecond_ER.json"),

    GET_MULTIPLE_DAP_PLACEMENT_ER_JSON(
        "/data/publisher_self_serve_controller_it/GetMultipleDapVideoPlacements_ER.json"),

    CREATE_PLACEMENT_WITHOUT_VIDEO_PLACEMENT_TYPE_PAYLOAD_JSON(
        "/data/publisher_self_serve_controller_it/CreateDapPlacementWithoutVideoPlacementType_payload.json");

    private final String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
