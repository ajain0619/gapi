package com.nexage.app.web.deal;

import static com.nexage.app.web.deal.DealFormulaAssignedInventoryController.FORMULA_INVENTORY_CONTENT_TYPE;
import static com.nexage.app.web.deal.DealFormulaAssignedInventoryController.FORMULA_INVENTORY_UNPAGED_CONTENT_TYPE;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.deal.impl.FormulaAssignedInventoryDTOServiceImpl;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
class DealFormulaAssignedInventoryControllerIT {

  private static final Long DEAL_PID = 2L;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper mapper;
  @Mock private FormulaAssignedInventoryDTOServiceImpl formulaAssignedInventoryDTOService;

  @InjectMocks private DealFormulaAssignedInventoryController dealAssignedInventoryController;
  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(dealAssignedInventoryController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void updateFormulaInventorySuccess() throws Exception {
    var payload = getData(ResourcePaths.FORMULA_PAYLOAD.filePath);
    var dto = mapper.readValue(payload, FormulaAssignedInventoryListDTO.class);

    when(formulaAssignedInventoryDTOService.updateAssignedInventory(
            DEAL_PID, dto.getContent().iterator().next()))
        .thenReturn(dto);
    mockMvc
        .perform(
            post("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
                .content(payload)
                .contentType(FORMULA_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateFormulaAssignedInventoryWithDomainAttribute() throws Exception {
    var payload = getData(ResourcePaths.FORMULA_PAYLOAD_WITH_DOMAIN_ATTRIBUTE.filePath);
    var dto = mapper.readValue(payload, FormulaAssignedInventoryListDTO.class);

    when(formulaAssignedInventoryDTOService.updateAssignedInventory(
            DEAL_PID, dto.getContent().iterator().next()))
        .thenReturn(dto);
    mockMvc
        .perform(
            post("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
                .content(payload)
                .contentType(FORMULA_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isCreated());
  }

  @Test
  void updateFormulaInvalidJson() throws Exception {
    var payload = getData(ResourcePaths.FORMULA_INVALID_PAYLOAD.filePath);
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR))
        .when(formulaAssignedInventoryDTOService)
        .updateAssignedInventory(eq(DEAL_PID), ArgumentMatchers.any());

    mockMvc
        .perform(
            post("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
                .content(payload)
                .contentType(FORMULA_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR))));
  }

  @Test
  void getFormulaForDeal() throws Exception {
    var response = getData(ResourcePaths.FORMULA_PAYLOAD.filePath);
    var expected = mapper.readValue(response, FormulaAssignedInventoryListDTO.class);
    when(formulaAssignedInventoryDTOService.getAssignedInventory(DEAL_PID)).thenReturn(expected);

    mockMvc
        .perform(
            get("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
                .accept(FORMULA_INVENTORY_UNPAGED_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(this.getClass(), name), Charset.forName("UTF-8"));
  }

  private enum ResourcePaths {
    FORMULA_PAYLOAD("/data/assignedInventory/formula_assigned_inventory_payload.json"),
    FORMULA_PAYLOAD_WITH_DOMAIN_ATTRIBUTE(
        "/data/assignedInventory/formula_assigned_inventory_with_domain_attribute_payload.json"),
    FORMULA_INVALID_PAYLOAD("/data/assignedInventory/formula_inventory_invalid.json");

    private String filePath;

    ResourcePaths(String filePath) {
      this.filePath = filePath;
    }
  }
}
