package com.nexage.app.web.deal;

import static com.nexage.app.web.deal.DealSpecificAssignedInventoryController.SPECIFIC_INVENTORY_CONTENT_TYPE;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.services.deal.impl.SpecificAssignedInventoryDTOServiceImpl;
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
class DealSpecificAssignedInventoryControllerIT {

  private static final Long DEAL_PID = 2L;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper mapper;
  @Mock private SpecificAssignedInventoryDTOServiceImpl specificAssignedInventoryDTOService;
  @InjectMocks private DealSpecificAssignedInventoryController dealAssignedInventoryController;
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
  void updateSpecificInventorySuccess() throws Exception {
    var payload = getData(ResourcePaths.UPDATE_SPECIFIC_PAYLOAD.filePath);
    var dto = mapper.readValue(payload, SpecificAssignedInventoryDTO.class);
    when(specificAssignedInventoryDTOService.createNewAssignedInventory(DEAL_PID, dto))
        .thenReturn(dto);

    mockMvc
        .perform(
            post("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
                .content(payload)
                .contentType(SPECIFIC_INVENTORY_CONTENT_TYPE))
        .andExpect(status().isCreated());
  }

  @Test
  void updateSpecificInventoryInvalidContent() throws Exception {
    var payload = getData(ResourcePaths.UPDATE_SPECIFIC_INVALID_PAYLOAD.filePath);

    mockMvc
        .perform(
            post("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
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

  @Test
  void getSpecificInventory() throws Exception {
    var response = getData(ResourcePaths.UPDATE_SPECIFIC_PAYLOAD.filePath);
    var expected = mapper.readValue(response, SpecificAssignedInventoryDTO.class);
    when(specificAssignedInventoryDTOService.getAssignedInventory(DEAL_PID)).thenReturn(expected);

    mockMvc
        .perform(
            get("/v1/deals/{dealPid}/assigned-inventories", DEAL_PID)
                .accept(
                    DealSpecificAssignedInventoryController
                        .SPECIFIC_INVENTORY_UNPAGED_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(this.getClass(), name), Charset.forName("UTF-8"));
  }

  private enum ResourcePaths {
    UPDATE_SPECIFIC_PAYLOAD("/data/assignedInventory/specific_assigned_inventory_payload.json"),
    UPDATE_SPECIFIC_INVALID_PAYLOAD(
        "/data/assignedInventory/specific_assigned_inventory_invalid_payload.json");

    private String filePath;

    ResourcePaths(String filePath) {
      this.filePath = filePath;
    }
  }
}
