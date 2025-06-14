package com.nexage.app.web.deal;

import static com.nexage.app.web.deal.DealSpecificAssignedInventoryController.SPECIFIC_INVENTORY_CONTENT_TYPE;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.services.deal.DealSpecificAssignedInventoryService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerDealSpecificAssignedInventoryControllerIT {

  private static final Long DEAL_PID = 2L;
  private static final Long SELLER_ID = 10212L;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper mapper;
  @Mock private DealSpecificAssignedInventoryService dealSpecificAssignedInventoryService;

  @InjectMocks
  private SellerDealSpecificAssignedInventoryController
      sellerDealSpecificAssignedInventoryController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerDealSpecificAssignedInventoryController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithSeller() throws Exception {
    var payload = getData(ResourcePaths.UPDATE_SPECIFIC_PAYLOAD.filePath);
    var dto = mapper.readValue(payload, SpecificAssignedInventoryDTO.class);
    when(dealSpecificAssignedInventoryService.createNewAssignedInventoryAssociatedWithSeller(
            DEAL_PID, SELLER_ID, dto))
        .thenReturn(dto);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/deals/{dealPid}/assigned-inventories", SELLER_ID, DEAL_PID)
                .content(payload)
                .contentType(SPECIFIC_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithPositionDuplicate() throws Exception {
    var payload = getData(ResourcePaths.UPDATE_SPECIFIC_PAYLOAD_WITH_DUPLICATE_PLACEMENT.filePath);
    var dto = mapper.readValue(payload, SpecificAssignedInventoryDTO.class);
    when(dealSpecificAssignedInventoryService.createNewAssignedInventoryAssociatedWithSeller(
            DEAL_PID, SELLER_ID, dto))
        .thenReturn(dto);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/deals/{dealPid}/assigned-inventories", SELLER_ID, DEAL_PID)
                .content(payload)
                .contentType(SPECIFIC_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldThrowBadRequestWhenUpdateSpecificInventoryAssociatedWithSeller() throws Exception {
    var payload = getData(ResourcePaths.UPDATE_SPECIFIC_INVALID_PAYLOAD.filePath);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/deals/{dealPid}/assigned-inventories", SELLER_ID, DEAL_PID)
                .content(payload)
                .contentType(SPECIFIC_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        CommonErrorCodes.COMMON_BAD_REQUEST))));
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(this.getClass(), name), Charset.forName("UTF-8"));
  }

  private enum ResourcePaths {
    UPDATE_SPECIFIC_PAYLOAD("/data/assignedInventory/specific_assigned_inventory_payload.json"),
    UPDATE_SPECIFIC_INVALID_PAYLOAD(
        "/data/assignedInventory/specific_assigned_inventory_invalid_payload.json"),
    UPDATE_SPECIFIC_PAYLOAD_WITH_DUPLICATE_PLACEMENT(
        "/data/assignedInventory/specific_assigned_inventory_with_duplicate_positions_payload.json");

    private String filePath;

    ResourcePaths(String filePath) {
      this.filePath = filePath;
    }
  }
}
