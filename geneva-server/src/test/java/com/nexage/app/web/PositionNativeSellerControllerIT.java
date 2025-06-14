package com.nexage.app.web;

import static org.hamcrest.CoreMatchers.is;
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
class PositionNativeSellerControllerIT {

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
  void createNativeTest() {
    String payload = getData(ResourcePath.CREATE_NATIVE_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_NATIVE_ER.getFilePath());

    Position newPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(newPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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
  void createNativeWithoutAdvMraidTrackingTest() {
    String payload =
        getData(ResourcePath.CREATE_NATIVE_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_NATIVE_WITHOUT_ADV_MRAID_TRACKING_ER.getFilePath());

    Position createdPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPosition);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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
  void createPositionNativeInvalidVideoAttrMaxDur() {
    String invalidJson =
        getData(ResourcePath.CREATE_NATIVE_INVALID_VIDEO_ATTR_MAX_DUR_PAYLOAD.getFilePath());

    when(sellerService.createPosition(anyLong(), any(Position.class)))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_NATIVE_REQUEST_INVALID));

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
                        ServerErrorCodes.SERVER_NATIVE_REQUEST_INVALID))));
  }

  @Test
  @SneakyThrows
  void createPositionNativeInvalidVideoAttrStartDelay() {
    String invalidJson =
        getData(ResourcePath.CREATE_NATIVE_INVALID_VIDEO_ATTR_START_DELAY_PAYLOAD.getFilePath());

    when(sellerService.createPosition(anyLong(), any(Position.class)))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_NATIVE_REQUEST_INVALID));

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
                        ServerErrorCodes.SERVER_NATIVE_REQUEST_INVALID))));
  }

  @Test
  @SneakyThrows
  void updateNativeToVideoTest() {
    String payload = getData(ResourcePath.UPDATE_NATIVE_TO_VIDEO_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.UPDATE_NATIVE_TO_VIDEO_ER.getFilePath());

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
  void createNativeForDefaultTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_NATIVE_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_NATIVE_FOR_DEFAULT_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPayload);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createNativeForMediationTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_NATIVE_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_NATIVE_FOR_MEDIATION_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPayload);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPayload, resultPosition);
  }

  @Test
  @SneakyThrows
  void createNativeForSmartYieldTrafficTypeTest() {
    String payload =
        getData(ResourcePath.CREATE_NATIVE_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_NATIVE_FOR_SMART_YIELD_TRAFFIC_TYPE_ER.getFilePath());

    Position createdPayload = mapper.readValue(payload, Position.class);
    Position expectedPayload = mapper.readValue(expected, Position.class);

    Site newSite = getSite();

    newSite.addPosition(createdPayload);

    when(sellerService.createPosition(anyLong(), any(Position.class))).thenReturn(newSite);

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

    assertEquals(expectedPayload, resultPosition);
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
    CREATE_NATIVE_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNative_payload.json"),
    CREATE_NATIVE_ER(
        "/data/controllers/sellers/positions/native/expected_results/CreateNative_ER.json"),
    CREATE_NATIVE_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNativeWithoutAdvMraidTracking_payload.json"),
    CREATE_NATIVE_WITHOUT_ADV_MRAID_TRACKING_ER(
        "/data/controllers/sellers/positions/native/create/CreateNativeAdvMraidTracking_ER.json"),
    CREATE_NATIVE_INVALID_VIDEO_ATTR_MAX_DUR_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNativeInvalidVideoAttrMaxDur.json"),
    CREATE_NATIVE_INVALID_VIDEO_ATTR_START_DELAY_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNativeInvalidVideoAttrStartDelay.json"),
    UPDATE_NATIVE_TO_VIDEO_PAYLOAD(
        "/data/controllers/sellers/positions/native/update/UpdateNativeToVideo_payload.json"),
    UPDATE_NATIVE_TO_VIDEO_ER(
        "/data/controllers/sellers/positions/native/expected_results/UpdateNativeToVideo_ER.json"),
    CREATE_NATIVE_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNativeDefaultTrafficType_payload.json"),
    CREATE_NATIVE_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNativeForMediationTrafficType_payload.json"),
    CREATE_NATIVE_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/native/create/CreateNativeForSmartYieldTrafficType_payload.json"),
    CREATE_NATIVE_FOR_DEFAULT_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/native/expected_results/CreateNativeDefaultTrafficType_ER.json"),
    CREATE_NATIVE_FOR_MEDIATION_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/native/expected_results/CreateNativeForMediationTrafficType_ER.json"),
    CREATE_NATIVE_FOR_SMART_YIELD_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/native/expected_results/CreateNativeForSmartYieldTrafficType_ER.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
