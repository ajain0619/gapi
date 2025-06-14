package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PositionVideoSellerControllerIT {

  private static final String CREATE_POSITION_URL = "/sellers/sites/{sitePID}/positions";
  private static final String UPDATE_POSITION_URL = "/sellers/sites/positions/{positionPID}";

  private static final long SITE_PID = 10000174;
  private static final long POSITION_PID = 100250;

  private MockMvc mockMvc;

  @Mock private SellerPositionService sellerService;

  @InjectMocks private SellerController sellerController;
  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(sellerController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void createPositionVideoBannerInvalidVideoAttrLinearity() {
    String invalidJson =
        getData(
            ResourcePath.CREATE_VIDEO_BANNER_INVALID_VIDEO_ATTR_LINEARITY_PAYLOAD.getFilePath());

    when(sellerService.createPosition(anyLong(), any(Position.class)))
        .thenThrow(GenevaValidationException.class);

    mockMvc
        .perform(
            put(CREATE_POSITION_URL, SITE_PID)
                .content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void createPositionVideoInvalidScreenLocation() {
    String invalidJson =
        getData(ResourcePath.CREATE_VIDEO_INVALID_SCREEN_LOCATION_PAYLOAD.getFilePath());

    when(sellerService.createPosition(anyLong(), any(Position.class)))
        .thenThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_SCREEN_LOCATION_NOT_FOUND));

    mockMvc
        .perform(
            put(CREATE_POSITION_URL, SITE_PID)
                .content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_SCREEN_LOCATION_NOT_FOUND))));
  }

  @Test
  @SneakyThrows
  void createVideoLinearAllPopulatedTest() {
    String payload = getData(ResourcePath.CREATE_VIDEO__LINEAR_ALL_POPULATED_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_VIDEO__LINEAR_ALL_POPULATED_ER.getFilePath());

    Position newPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(newPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPosition, resultPosition);
  }

  @Test
  @SneakyThrows
  void createVideoAndBannerLinearAllAttrPopulatedTest() {
    String payload =
        getData(
            ResourcePath.CREATE_VIDEO_AND_BANNER_LINEAR_ALL_ATTR_POPULATED_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_VIDEO_AND_BANNER_LINEAR_ALL_ATTR_POPULATED_ER.getFilePath());

    Position newPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(newPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPosition, resultPosition);
  }

  @Test
  @SneakyThrows
  void createVideoLinearOtherAttributesNullTest() {
    String payload =
        getData(ResourcePath.CREATE_VIDEO_LINEAR_OTHER_ATTRIBUTES_NULL_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_VIDEO_LINEAR_OTHER_ATTRIBUTES_NULL_ER.getFilePath());

    Position newPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(newPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPosition, resultPosition);
  }

  @Test
  @SneakyThrows
  void createVideoAndBannerNonLinearAllAttrNullTest() {
    String payload =
        getData(
            ResourcePath.CREATE_VIDEO_AND_BANNER_NON_LINEAR_ALL_ATTR_NULL_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_VIDEO_AND_BANNER_NON_LINEAR_ALL_ATTR_NULL_ER.getFilePath());

    Position newPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(newPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPosition, resultPosition);
  }

  @Test
  @SneakyThrows
  void createVideoWithoutAdvMraidTrackingTest() {
    String payload =
        getData(ResourcePath.CREATE_VIDEO_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_VIDEO_WITHOUT_ADV_MRAID_TRACKING_ER.getFilePath());

    Position createdPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPosition, resultPosition);
  }

  @Test
  @SneakyThrows
  void createVideoForDefaultTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_VIDEO_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_VIDEO_FOR_DEFAULT_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPayload);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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
  void createVideoForMediationTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_VIDEO_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_VIDEO_FOR_MEDIATION_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPayload);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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
  void createVideoForSmartYieldTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_VIDEO_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_VIDEO_FOR_SMART_YIELD_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPayload);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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
  void updateVideoToVideoAndBannerTest() {
    String payload = getData(ResourcePath.UPDATE_VIDEO_TO_VIDEO_AND_BANNER_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.UPDATE_VIDEO_TO_VIDEO_AND_BANNER_ER.getFilePath());

    Position updatedPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();
    Site updatedSite = getSite();

    newSite.addPosition(updatedPosition);
    updatedSite.addPosition(expectedPosition);

    when(sellerService.updatePosition(any(Position.class))).thenReturn(newSite);

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
  void updateVideoAndBannerToBannerTest() {
    String payload = getData(ResourcePath.UPDATE_VIDEO_AND_BANNER_TO_BANNER_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.UPDATE_VIDEO_AND_BANNER_TO_BANNER_ER.getFilePath());

    Position updatedPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();
    Site updatedSite = getSite();

    newSite.addPosition(updatedPosition);
    updatedSite.addPosition(expectedPosition);

    when(sellerService.updatePosition(any(Position.class))).thenReturn(newSite);

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
  void updateOnePositionTest() {
    String payload = getData(ResourcePath.UPDATE_ONE_POSITION_PAYLOAD.getFilePath());

    Position updatedPosition = mapper.readValue(payload, Position.class);

    Site newSite = getSite();

    updatedPosition.setVersion(1);

    newSite.getPositions().add(updatedPosition);

    when(sellerService.updatePosition(any(Position.class))).thenReturn(newSite);

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, updatedPosition.getPid())
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.positions[0].version", is(updatedPosition.getVersion())));
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
    return ResourceLoader.getResource(SellerControllerIT.class, name);
  }

  @Getter
  enum ResourcePath {
    CREATE_VIDEO_BANNER_INVALID_VIDEO_ATTR_LINEARITY_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoBannerInvalidVideoAttrLinearity.json"),
    CREATE_VIDEO_INVALID_SCREEN_LOCATION_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoInvalidScreenLocation.json"),
    CREATE_VIDEO_AND_BANNER_LINEAR_ALL_ATTR_POPULATED_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoAndBannerLinearAllAttrPopulated_payload.json"),
    CREATE_VIDEO_AND_BANNER_NON_LINEAR_ALL_ATTR_NULL_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoAndBannerNonLinearAllAttrNull_payload.json"),
    CREATE_VIDEO__LINEAR_ALL_POPULATED_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoLinearAllPopulated_payload.json"),
    CREATE_VIDEO_LINEAR_OTHER_ATTRIBUTES_NULL_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoLinearOtherAttributesNull_payload.json"),
    CREATE_VIDEO_AND_BANNER_LINEAR_ALL_ATTR_POPULATED_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoAndBannerLinearAllAttrPopulated_ER.json"),
    CREATE_VIDEO_AND_BANNER_NON_LINEAR_ALL_ATTR_NULL_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoAndBannerNonLinearAllAttrNull_ER.json"),
    CREATE_VIDEO__LINEAR_ALL_POPULATED_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoLinearAllPopulated_ER.json"),
    CREATE_VIDEO_LINEAR_OTHER_ATTRIBUTES_NULL_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoLinearOtherAttributesNull_ER.json"),
    CREATE_VIDEO_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoWithoutAdvMraidTracking_payload.json"),
    CREATE_VIDEO_WITHOUT_ADV_MRAID_TRACKING_ER(
        "/data/controllers/sellers/positions/video/create/CreateVideoAdvMraidTracking_ER.json"),
    CREATE_VIDEO_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoForDefaultTrafficType_payload.json"),
    CREATE_VIDEO_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoForMediationTrafficType_payload.json"),
    CREATE_VIDEO_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/video/create/CreateVideoForSmartYieldTrafficType_payload.json"),
    CREATE_VIDEO_FOR_DEFAULT_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoForDefaultTrafficType_ER.json"),
    CREATE_VIDEO_FOR_MEDIATION_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoForMediationTrafficType_ER.json"),
    CREATE_VIDEO_FOR_SMART_YIELD_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/video/expected_results/CreateVideoForSmartYieldTrafficType_ER.json"),
    UPDATE_VIDEO_TO_VIDEO_AND_BANNER_PAYLOAD(
        "/data/controllers/sellers/positions/video/update/UpdateVideoToVideoAndBanner_payload.json"),
    UPDATE_VIDEO_AND_BANNER_TO_BANNER_PAYLOAD(
        "/data/controllers/sellers/positions/video/update/UpdateVideoAndBannerToBanner_payload.json"),
    UPDATE_VIDEO_TO_VIDEO_AND_BANNER_ER(
        "/data/controllers/sellers/positions/video/expected_results/UpdateVideoToVideoAndBanner_ER.json"),
    UPDATE_VIDEO_AND_BANNER_TO_BANNER_ER(
        "/data/controllers/sellers/positions/video/expected_results/UpdateVideoAndBannerToBanner_ER.json"),
    UPDATE_ONE_POSITION_PAYLOAD(
        "/data/controllers/sellers/positions/video/update/UpdateOnePosition.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
