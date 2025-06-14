package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
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
class UpdateTrafficTypesForSellerControllerIT {

  private static final String UPDATE_POSITION_URL = "/sellers/sites/positions/{positionPID}";

  private MockMvc mockMvc;

  @Mock private SellerPositionService sellerPositionService;

  @InjectMocks private SellerController sellerController;
  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(sellerController).build();
  }

  @Test
  @SneakyThrows
  void updateTrafficTypeToSmartYieldTest() {
    String payload = getData(ResourcePath.UPDATE_TRAFFIC_TYPE_TO_SMART_YIELD_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.UPDATE_TRAFFIC_TYPE_TO_SMART_YIELD_ER.getFilePath());

    Position updatedPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();
    Site updatedSite = getSite();

    newSite.addPosition(updatedPosition);
    updatedSite.addPosition(expectedPosition);

    when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(newSite);

    MvcResult result =
        mockMvc
            .perform(
                put(UPDATE_POSITION_URL, updatedPosition.getPid())
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    assertEquals(updatedSite, resultSite);
  }

  @Test
  @SneakyThrows
  void updateTrafficTypeToMediationTest() {
    String payload = getData(ResourcePath.UPDATE_TRAFFIC_TYPE_TO_MEDIATION_PAYLOAD.getFilePath());
    String expected = getData(ResourcePath.UPDATE_TRAFFIC_TYPE_TO_MEDIATION_ER.getFilePath());

    Position updatedPosition = mapper.readValue(payload, Position.class);
    Position expectedPosition = mapper.readValue(expected, Position.class);

    Site newSite = getSite();
    Site updatedSite = getSite();

    newSite.addPosition(updatedPosition);
    updatedSite.addPosition(expectedPosition);

    when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(newSite);

    MvcResult result =
        mockMvc
            .perform(
                put(UPDATE_POSITION_URL, updatedPosition.getPid())
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Site resultSite = mapper.readValue(result.getResponse().getContentAsString(), Site.class);

    assertEquals(updatedSite, resultSite);
  }

  @Test
  @SneakyThrows
  void updateTrafficTypeIncorrectValueTest() {
    assertThrows(
        InvalidFormatException.class,
        () -> {
          String payload =
              getData(ResourcePath.UPDATE_TRAFFIC_TYPE_INCORRECT_VALUE_PAYLOAD.getFilePath());

          Position invalidPayload = mapper.readValue(payload, Position.class);

          when(sellerPositionService.updatePosition(any(Position.class)))
              .thenThrow(GenevaValidationException.class);

          mockMvc
              .perform(
                  put(UPDATE_POSITION_URL, invalidPayload.getPid())
                      .content(payload)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isBadRequest());
        });
  }

  @Test
  @SneakyThrows
  void updateTrafficTypeInvalidValueTest() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          String payload =
              getData(ResourcePath.UPDATE_TRAFFIC_TYPE_INVALID_VALUE_PAYLOAD.getFilePath());

          Position invalidPayload = mapper.readValue(payload, Position.class);

          when(sellerPositionService.updatePosition(any(Position.class)))
              .thenThrow(GenevaValidationException.class);

          mockMvc
              .perform(
                  put(UPDATE_POSITION_URL, invalidPayload.getPid())
                      .content(payload)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isBadRequest());
        });
  }

  @Test
  @SneakyThrows
  void updateTrafficTypeInvalidValueBooleanTest() {
    assertThrows(
        InvalidFormatException.class,
        () -> {
          String payload =
              getData(ResourcePath.UPDATE_TRAFFIC_TYPE_INVALID_VALUE_BOOLEAN_PAYLOAD.getFilePath());

          Position invalidPayload = mapper.readValue(payload, Position.class);

          when(sellerPositionService.updatePosition(any(Position.class)))
              .thenThrow(GenevaValidationException.class);

          mockMvc
              .perform(
                  put(UPDATE_POSITION_URL, invalidPayload.getPid())
                      .content(payload)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isBadRequest());
        });
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
    UPDATE_TRAFFIC_TYPE_TO_SMART_YIELD_PAYLOAD(
        "/data/controllers/sellers/positions/update_traffic_type/payload/UpdateTrafficTypeToSmartYield_payload.json"),
    UPDATE_TRAFFIC_TYPE_TO_MEDIATION_PAYLOAD(
        "/data/controllers/sellers/positions/update_traffic_type/payload/UpdateTrafficTypeToMediation_payload.json"),
    UPDATE_TRAFFIC_TYPE_TO_SMART_YIELD_ER(
        "/data/controllers/sellers/positions/update_traffic_type/expected_results/UpdatedTrafficTypeToSmartYield_ER.json"),
    UPDATE_TRAFFIC_TYPE_TO_MEDIATION_ER(
        "/data/controllers/sellers/positions/update_traffic_type/expected_results/UpdatedTrafficTypeToMediation_ER.json"),
    UPDATE_TRAFFIC_TYPE_INCORRECT_VALUE_PAYLOAD(
        "/data/controllers/sellers/positions/update_traffic_type/payload/UpdateTrafficTypeIncorrectValue.json"),
    UPDATE_TRAFFIC_TYPE_INVALID_VALUE_PAYLOAD(
        "data/controllers/sellers/positions/update_traffic_type/payload/UpdateTrafficTypeInvalidValue.json"),
    UPDATE_TRAFFIC_TYPE_INVALID_VALUE_BOOLEAN_PAYLOAD(
        "/data/controllers/sellers/positions/update_traffic_type/payload/UpdateTrafficTypeInvalidValueBoolean.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
