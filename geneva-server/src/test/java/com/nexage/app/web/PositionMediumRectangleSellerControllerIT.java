package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.ResourceLoader;
import java.io.IOException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PositionMediumRectangleSellerControllerIT {

  private static final String CREATE_POSITION_URL = "/sellers/sites/{sitePID}/positions";

  private static final long SITE_PID = 10000174;

  private MockMvc mockMvc;

  @Mock private SellerPositionService sellerService;

  @InjectMocks private SellerController sellerController;
  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(sellerController).build();
  }

  @Test
  @SneakyThrows
  void createMediumRectangleTest() {
    String payload = getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_ER.getFilePath());

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
  void createMediumRectangleWithAdvMraidTrackingTest() {
    String payload =
        getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_WITH_ADV_MRAID_TRACKING_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_WITH_ADV_MRAID_TRACKING_ER.getFilePath());

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
  void createMediumRectangleWithoutAdvMraidTrackingTest() {
    String payload =
        getData(
            ResourcePath.CREATE_MEDIUM_RECTANGLE_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_WITHOUT_ADV_MRAID_TRACKING_ER.getFilePath());

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
  void createMediumRectangleForDefaultTrafficTypeTest() {
    String payload =
        getData(
            ResourcePath.CREATE_MEDIUM_RECTANGLE_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_FOR_DEFAULT_TRAFFIC_TYPE_ER.getFilePath());

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
  void createMediumRectangleForMediationTrafficTypeTest() {
    String payload =
        getData(
            ResourcePath.CREATE_MEDIUM_RECTANGLE_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD.getFilePath());
    String expected =
        getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_FOR_MEDIATION_TRAFFIC_TYPE_ER.getFilePath());

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
  void createMediumRectangleForSmartYieldTrafficTypeTest() {
    String payload =
        getData(
            ResourcePath.CREATE_MEDIUM_RECTANGLE_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD
                .getFilePath());
    String expected =
        getData(ResourcePath.CREATE_MEDIUM_RECTANGLE_FOR_SMART_YIELD_TRAFFIC_TYPE_ER.getFilePath());

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
    CREATE_MEDIUM_RECTANGLE_PAYLOAD(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangle_payload.json"),
    CREATE_MEDIUM_RECTANGLE_ER(
        "/data/controllers/sellers/positions/medium_rectangle/expected_results/CreateMediumRectangle_ER.json"),
    CREATE_MEDIUM_RECTANGLE_WITH_ADV_MRAID_TRACKING_PAYLOAD(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleWithAdvMraidTracking_payload.json"),
    CREATE_MEDIUM_RECTANGLE_WITHOUT_ADV_MRAID_TRACKING_PAYLOAD(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleWithoutAdvMraidTracking_payload.json"),
    CREATE_MEDIUM_RECTANGLE_WITH_ADV_MRAID_TRACKING_ER(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleWithAdvMraidTracking_ER.json"),
    CREATE_MEDIUM_RECTANGLE_WITHOUT_ADV_MRAID_TRACKING_ER(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleWithoutAdvMraidTracking_ER.json"),
    CREATE_MEDIUM_RECTANGLE_FOR_DEFAULT_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleForDefaultTrafficType_payload.json"),
    CREATE_MEDIUM_RECTANGLE_FOR_MEDIATION_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleForMediationTrafficType_payload.json"),
    CREATE_MEDIUM_RECTANGLE_FOR_SMART_YIELD_TRAFFIC_TYPE_PAYLOAD(
        "/data/controllers/sellers/positions/medium_rectangle/create/CreateMediumRectangleForSmartYieldTrafficType_payload.json"),
    CREATE_MEDIUM_RECTANGLE_FOR_DEFAULT_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/medium_rectangle/expected_results/CreateMediumRectangleForDefaultTrafficType_ER.json"),
    CREATE_MEDIUM_RECTANGLE_FOR_MEDIATION_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/medium_rectangle/expected_results/CreateMediumRectangleForMediationTrafficType_ER.json"),
    CREATE_MEDIUM_RECTANGLE_FOR_SMART_YIELD_TRAFFIC_TYPE_ER(
        "/data/controllers/sellers/positions/medium_rectangle/expected_results/CreateMediumRectangleForSmartYieldTrafficType_ER.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
