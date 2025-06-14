package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.IOException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
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
class PositionBannerSellerControllerIT {
  private static final String CREATE_POSITION_URL = "/sellers/sites/{sitePID}/positions";
  private static final String UPDATE_POSITION_URL = "/sellers/sites/positions/{positionPID}";
  private static final String DELETE_POSITION_URL =
      "/sellers/sites/{sitePID}/positions/{positionPID}";
  private static final String GET_SITE_URL = "/sellers/sites/{sitePID}";

  private static final Long SITE_PID = 10000174L;
  private static final Long POSITION_PID = 100250L;

  private MockMvc mockMvc;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Mock private SellerPositionService sellerPositionService;

  @Mock private SellerSiteService sellerSiteService;

  @InjectMocks private SellerController sellerController;
  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void createBannerTest() {
    String payload = getData(ResourcePath.CREATE_BANNER_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_BANNER_ER.getFilePath());

    Position newPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(newPosition);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPosition, resultPosition);

    MvcResult resultGet =
        mockMvc.perform(get(GET_SITE_URL, SITE_PID)).andExpect(status().isOk()).andReturn();

    Site resultGetSite = mapper.readValue(resultGet.getResponse().getContentAsString(), Site.class);

    Position resultGetPos = resultGetSite.getPositions().iterator().next();

    assertEquals(expectedPosition, resultGetPos);
  }

  @Test
  @SneakyThrows
  void createBannerForDefaultTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_BANNER_FOR_DEFAULT_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPayload);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createBannerForMediationNumTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_FOR_MEDIATION_NUM_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_BANNER_FOR_MEDIATION_NUM_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPayload);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createBannerForMediationTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_BANNER_FOR_MEDIATION_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPayload);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createBannerForSmartYieldNumTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_FOR_SMART_YIELD_NUM_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_BANNER_FOR_SMART_YIELD_NUM_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPayload);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createBannerForSmartYieldTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_BANNER_FOR_SMART_YIELD_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPayload);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createPositionInvalidJsonTest() {
    String invalidJson = getData(ResourcePath.CREATE_INVALID_JSON_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void createPositionBannerInvalidEmptyName() {
    String invalidJson =
        getData(ResourcePath.CREATE_BANNER_INVALID_EMPTY_NAME_JSON_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void createPositionBannerInvalidHeight() {
    String invalidJson = getData(ResourcePath.CREATE_BANNER_INVALID_HEIGHT_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void createPositionBannerInvalidVideoAttrLinearity() {
    String invalidJson =
        getData(ResourcePath.CREATE_BANNER_INVALID_VIDEO_ATTR_LINEARITY_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void createPositionBannerInvalidVideoAttrPlaybackMethod() {
    String invalidJson =
        getData(
            ResourcePath.CREATE_BANNER_INVALID_VIDEO_ATTR_PLAYBACK_METHOD_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void createPositionBannerInvalidWidth() {
    String invalidJson = getData(ResourcePath.CREATE_BANNER_INVALID_WIDTH_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void createPositionLongPositionName() {
    String invalidJson =
        getData(ResourcePath.CREATE_BANNER_LONG_POSITION_NAME_PAYLOAD.getFilePath());

    testInvalidPositionCreation(invalidJson);
  }

  @Test
  @SneakyThrows
  void updateBannerToNativeTest() {
    String payload = getData(ResourcePath.UPDATE_BANNER_TO_NATIVE_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.UPDATE_BANNER_TO_NATIVE_ER.getFilePath());

    Position updatedPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site site = getSite();
    Site updatedSite = getSite();

    site.addPosition(updatedPosition);
    updatedSite.addPosition(expectedPosition);

    when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(UPDATE_POSITION_URL, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    assertEquals(updatedSite, resultSite);
  }

  @Test
  @SneakyThrows
  void updatePositionDoesNotExist() {
    String payload = getData(ResourcePath.UPDATE_BANNER_TO_NATIVE_PAYLOAD.getFilePath());

    when(sellerPositionService.updatePosition(any(Position.class)))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS));

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, POSITION_PID)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                CoreMatchers.is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_POSITION_NOT_EXISTS))));
  }

  @Test
  @SneakyThrows
  void deletePositionAndBeUnableToSearchItAfter() {
    String payload = getData(ResourcePath.TEST_POSITION_TO_DELETE.getFilePath());

    Position deletablePosition = mapper.readValue(payload, Position.class);

    Site site = getSite();
    site.addPosition(deletablePosition);

    when(sellerPositionService.deletePosition(anyLong(), anyLong())).thenReturn(site);
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    mockMvc
        .perform(get(GET_SITE_URL, site.getPid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.positions[0].name", is(deletablePosition.getName())));

    site.getPositions().remove(deletablePosition);

    mockMvc
        .perform(delete(DELETE_POSITION_URL, SITE_PID, deletablePosition.getPid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.positions.length()", is(0)));

    mockMvc
        .perform(get(GET_SITE_URL, site.getPid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.positions.length()", is(0)));
  }

  @Test
  @SneakyThrows
  void createBannerWithAdvMraidTrackingTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_WITH_ADV_MRAID_TRACKING_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_BANNER_WITH_ADV_MRAID_TRACKING_ER.getFilePath());

    Position createdPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPosition);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPosition, resultPosition);
  }

  @Test
  @SneakyThrows
  void createBannerWithoutAdvMraidTrackingTest() {
    String payload =
        getData(ResourcePath.CREATE_BANNER_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_BANNER_WITHOUT_ADV_MRAID_TRACKING_ER.getFilePath());

    Position createdPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site site = getSite();

    site.addPosition(createdPosition);

    when(sellerPositionService.createPosition(anyLong(), any(Position.class))).thenReturn(site);

    MvcResult result =
        mockMvc
            .perform(
                put(CREATE_POSITION_URL, SITE_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    Position resultPosition = resultSite.getPositions().iterator().next();

    assertEquals(expectedPosition, resultPosition);
  }

  private Site getSite() {
    Site site = new Site();
    site.setId("test-id");
    site.setPid(1L);
    site.setName("geneva-test");
    site.setGroupsEnabled(true);
    site.setStatusVal(0);
    return site;
  }

  private String getData(String name) throws IOException {
    return ResourceLoader.getResource(PositionBannerSellerControllerIT.class, name);
  }

  @SneakyThrows
  private void testInvalidPositionCreation(String invalidJson) {
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    mockMvc
        .perform(
            put(CREATE_POSITION_URL, SITE_PID)
                .content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode", is(CommonErrorCodes.COMMON_BAD_REQUEST.getCode())));
  }

  @Getter
  enum ResourcePath {
    CREATE_BANNER_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBanner_payload.json"),
    CREATE_BANNER_ER(
        "/data/controllers/sellers/positions/banner/expected_results/CreateBanner_ER.json"),
    CREATE_BANNER_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerForDefaultTrafficType_payload.json"),
    CREATE_BANNER_FOR_MEDIATION_NUM_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerForMediationNumTrafficType_payload.json"),
    CREATE_BANNER_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerForMediationTrafficType_payload.json"),
    CREATE_BANNER_FOR_SMART_YIELD_NUM_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerForSmartYieldNumTrafficType_payload.json"),
    CREATE_BANNER_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerForSmartYieldTrafficType_payload.json"),
    CREATE_BANNER_FOR_DEFAULT_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/banner/expected_results/CreateBannerForDefaultTrafficType_ER.json"),
    CREATE_BANNER_FOR_MEDIATION_NUM_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/banner/expected_results/CreateBannerForMediationNumTrafficType_ER.json"),
    CREATE_BANNER_FOR_MEDIATION_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/banner/expected_results/CreateBannerForMediationTrafficType_ER.json"),
    CREATE_BANNER_FOR_SMART_YIELD_NUM_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/banner/expected_results/CreateBannerForSmartYieldNumTrafficType_ER.json"),
    CREATE_BANNER_FOR_SMART_YIELD_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/banner/expected_results/CreateBannerForSmartYieldTrafficType_ER.json"),
    CREATE_INVALID_JSON_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateInvalidJson.json"),
    CREATE_BANNER_INVALID_EMPTY_NAME_JSON_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerInvalidEmptyName.json"),
    CREATE_BANNER_INVALID_HEIGHT_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerInvalidHeight.json"),
    CREATE_BANNER_INVALID_VIDEO_ATTR_LINEARITY_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerInvalidVideoAttrLinearity.json"),
    CREATE_BANNER_INVALID_VIDEO_ATTR_PLAYBACK_METHOD_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerInvalidVideoAttrPlaybackMethod.json"),
    CREATE_BANNER_INVALID_WIDTH_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerInvalidWidth.json"),
    CREATE_BANNER_LONG_POSITION_NAME_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerLongPositionName.json"),
    UPDATE_BANNER_TO_NATIVE_PAYLOAD(
        "/data/controllers/sellers/positions/banner/update/UpdateBannerToNative_payload.json"),
    UPDATE_BANNER_TO_NATIVE_ER(
        "/data/controllers/sellers/positions/banner/expected_results/UpdateBannerToNative_ER.json"),
    TEST_POSITION_TO_DELETE(
        "/data/controllers/sellers/positions/banner/create/CreateTestPositionToDelete_payload.json"),
    CREATE_BANNER_WITH_ADV_MRAID_TRACKING_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerWithAdvMraidTracking_payload.json"),
    CREATE_BANNER_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD(
        "/data/controllers/sellers/positions/banner/create/CreateBannerWithoutAdvMraidTracking_payload.json"),
    CREATE_BANNER_WITH_ADV_MRAID_TRACKING_ER(
        "/data/controllers/sellers/positions/banner/create/CreateBannerWithAdvMraidTracking_ER.json"),
    CREATE_BANNER_WITHOUT_ADV_MRAID_TRACKING_ER(
        "/data/controllers/sellers/positions/banner/create/CreateBannerWithoutAdvMraidTracking_ER.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
