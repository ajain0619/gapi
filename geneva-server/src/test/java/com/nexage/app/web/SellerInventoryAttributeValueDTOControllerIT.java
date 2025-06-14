package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerInventoryAttributeValueService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.validator.SellerInventoryAttributeValuesValidator;
import com.nexage.app.web.support.BaseControllerItTest;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class SellerInventoryAttributeValueDTOControllerIT extends BaseControllerItTest {

  private static final Long SELLER_ID = 100L;
  private static final Long ATTRIBUTE_ID = 200L;
  private static final Long ATTRIBUTE_VALUE_ID = 300L;
  private static final String UPDATE_URL_PATTERN =
      "/v1/sellers/{sellerId}/inventory-attributes/{attributePid}/inventory-attribute-values/{pid}";
  private static final String GET_VALUES_URL_PATTERN =
      "/v1/sellers/{sellerId}/inventory-attributes/{attributePid}/inventory-attribute-values";

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper mapper;
  @Mock private SellerInventoryAttributeValueService sellerInventoryAttributeService;
  @Mock private SellerInventoryAttributeValuesValidator validator;
  @InjectMocks private SellerInventoryAttributeValueDTOController controller;

  private InventoryAttributeValueDTO valueData;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();

    valueData = new InventoryAttributeValueDTO();
    valueData.setPid(ATTRIBUTE_VALUE_ID);
    valueData.setVersion(2);
    valueData.setEnabled(true);
    valueData.setValue("newvalue");
  }

  @Test
  void testAttributeValueUpdate() throws Exception {
    when(sellerInventoryAttributeService.updateInventoryAttributeValue(
            anyLong(), anyLong(), anyLong(), any(InventoryAttributeValueDTO.class)))
        .thenReturn(valueData);
    doNothing()
        .when(validator)
        .validateForUpdate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));

    mockMvc
        .perform(
            put(UPDATE_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isOk());
  }

  @Test
  void updateFailWhenPidIsNull() throws Exception {
    valueData.setPid(null);
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NULL))
        .when(validator)
        .validateForUpdate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));
    mockMvc
        .perform(
            put(UPDATE_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NULL))));
  }

  @Test
  void updateFailWhenPidMismatch() throws Exception {
    long invalidPid = 1L;
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_UPDATE_PID_MISMATCH))
        .when(validator)
        .validateForUpdate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));
    mockMvc
        .perform(
            put(UPDATE_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID, invalidPid)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_UPDATE_PID_MISMATCH))));
  }

  @Test
  void updateFailAttributeNotExist() throws Exception {
    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST))
        .when(validator)
        .validateForUpdate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));
    mockMvc
        .perform(
            put(UPDATE_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST))));
  }

  @Test
  void updateFailAttributeValueNotExist() throws Exception {
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_DOES_NOT_EXIST))
        .when(validator)
        .validateForUpdate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));
    mockMvc
        .perform(
            put(UPDATE_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_DOES_NOT_EXIST))));
  }

  @Test
  void getAllInventoryAttributeValuesSuccess() throws Exception {
    String expectedResponse = getData(ResourcePaths.GET_ATTRIBUTE_VALUES_ER.filePath);
    when(sellerInventoryAttributeService.getAllValuesForInventoryAttribute(
            anyLong(), anyLong(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(valueData)));
    doNothing().when(validator).validateForFetch(anyLong(), anyLong());

    var result =
        mockMvc
            .perform(
                get(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

    var content = result.getResponse().getContentAsString();
    assertEquals(mapper.readTree(expectedResponse), mapper.readTree(content));
  }

  @Test
  void getAllInventoryAttributeValuesSellerMismatch() throws Exception {
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH))
        .when(validator)
        .validateForFetch(anyLong(), anyLong());

    mockMvc
        .perform(
            get(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH))));
  }

  @Test
  void getAllInventoryAttributeValuesMissinAttribute() throws Exception {
    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST))
        .when(validator)
        .validateForFetch(anyLong(), anyLong());
    mockMvc
        .perform(
            get(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST))));
  }

  @Test
  void createInventoryAttributeValueSuccess() throws Exception {
    String expectedResponse = getData(ResourcePaths.CREATE_ATTRIBUTE_VALUE_ER.filePath);
    var payload = new InventoryAttributeValueDTO();
    payload.setEnabled(true);
    payload.setValue("some value");

    doNothing()
        .when(validator)
        .validateForCreate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));
    valueData.setPid(1L);
    valueData.setVersion(0);
    valueData.setValue(payload.getValue());
    valueData.setEnabled(payload.isEnabled());
    when(sellerInventoryAttributeService.createInventoryAttributeValue(
            anyLong(), anyLong(), any(InventoryAttributeValueDTO.class)))
        .thenReturn(valueData);

    var result =
        mockMvc
            .perform(
                post(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andReturn();
    var content = result.getResponse().getContentAsString();
    assertEquals(mapper.readTree(expectedResponse), mapper.readTree(content));
  }

  @Test
  void createInventoryAttributeValueWrongPid() throws Exception {
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NOT_NULL))
        .when(validator)
        .validateForCreate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));

    mockMvc
        .perform(
            post(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NOT_NULL))));
  }

  @Test
  void createInventoryAttributeValueMissingAttribute() throws Exception {
    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST))
        .when(validator)
        .validateForCreate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));

    mockMvc
        .perform(
            post(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST))));
  }

  @Test
  void createInventoryAttributeWrongOwner() throws Exception {
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH))
        .when(validator)
        .validateForCreate(anyLong(), anyLong(), any(InventoryAttributeValueDTO.class));

    mockMvc
        .perform(
            post(GET_VALUES_URL_PATTERN, SELLER_ID, ATTRIBUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(valueData)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH))));
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(SellerInventoryAttributeValueDTOControllerIT.class, name),
        Charset.forName("UTF-8"));
  }

  private enum ResourcePaths {
    GET_ATTRIBUTE_VALUES_ER(
        "/data/seller_inventory_attribute_values/get_all_attribute_values_response.json"),
    CREATE_ATTRIBUTE_VALUE_ER(
        "/data/seller_inventory_attribute_values/create_inventory_atribute_value_ER.json");

    private String filePath;

    ResourcePaths(String filePath) {
      this.filePath = filePath;
    }
  }
}
