package com.nexage.app.web.deal;

import static com.nexage.app.web.deal.DealFormulaAssignedInventoryController.FORMULA_INVENTORY_CONTENT_TYPE;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.deal.DealFormulaAssignedInventoryService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
class SellerDealFormulaAssignedInventoryControllerIT {

  private static final Long DEAL_PID = 10L;
  private static final Long SELLER_ID = 10227L;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper mapper;
  @Mock private DealFormulaAssignedInventoryService dealFormulaAssignedInventoryService;

  @InjectMocks
  private SellerDealFormulaAssignedInventoryController sellerDealAssignedInventoryController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerDealAssignedInventoryController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldUpdateFormulaInventory() throws Exception {
    var payload = getData(ResourcePaths.FORMULA_PAYLOAD.filePath);
    var dto = mapper.readValue(payload, FormulaAssignedInventoryListDTO.class);

    when(dealFormulaAssignedInventoryService.updateAssignedInventoryForSeller(any(), any(), any()))
        .thenReturn(dto);
    mockMvc
        .perform(
            put("/v1/sellers/{sellerPid}/deals/{dealPid}/assigned-inventories", SELLER_ID, DEAL_PID)
                .content(payload)
                .contentType(FORMULA_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  @Test
  void shouldFailUpdateWithInvalidJson() throws Exception {
    var payload = getData(ResourcePaths.FORMULA_INVALID_PAYLOAD.filePath);
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA))
        .when(dealFormulaAssignedInventoryService)
        .updateAssignedInventoryForSeller(eq(SELLER_ID), eq(DEAL_PID), any());
    mockMvc
        .perform(
            put("/v1/sellers/{sellerPid}/deals/{dealPid}/assigned-inventories", SELLER_ID, DEAL_PID)
                .content(payload)
                .contentType(FORMULA_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA))));
  }

  private String getData(String name) throws IOException {
    return Resources.toString(Resources.getResource(this.getClass(), name), StandardCharsets.UTF_8);
  }

  private enum ResourcePaths {
    FORMULA_PAYLOAD("/data/assignedInventory/seller_formula_assigned_inventory_payload.json"),
    FORMULA_INVALID_PAYLOAD("/data/assignedInventory/seller_formula_inventory_invalid.json");

    private String filePath;

    ResourcePaths(String filePath) {
      this.filePath = filePath;
    }
  }
}
