package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.InventoryMdmIdDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.InventoryMdmIdService;
import com.nexage.app.util.validator.InventoryMdmIdQueryFieldParams;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.MultiValueSearchParamsArgumentResolver;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class InventoryMdmIdControllerIT {

  private MockMvc mockMvc;

  @Mock private Validator validator;
  @Mock private BeanValidationService beanValidationService;
  @Mock private InventoryMdmIdService mdmIdService;

  @InjectMocks private InventoryMdmIdController inventoryMdmIdController;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(inventoryMdmIdController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new MultiValueSearchParamsArgumentResolver())
            .setValidator(validator)
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldGetMdmIdsForCurrentUser() throws Exception {
    Set<String> companyMdmIds = Set.of("100");
    Set<String> sellerSeatMdmIds = Set.of("43", "44");
    Page<InventoryMdmIdDTO> pagedResult =
        new PageImpl<>(
            List.of(
                InventoryMdmIdDTO.builder()
                    .companyMdmIds(companyMdmIds)
                    .sellerSeatMdmIds(sellerSeatMdmIds)
                    .build()));

    var mdmIdListDTO =
        InventoryMdmIdDTO.builder()
            .companyMdmIds(companyMdmIds)
            .sellerSeatMdmIds(sellerSeatMdmIds)
            .build();
    when(mdmIdService.getMdmIdsForCurrentUser()).thenReturn(mdmIdListDTO);

    String expectedJson = objectMapper.writeValueAsString(pagedResult);
    mockMvc
        .perform(get("/v1/mdmids?qf=status&qt=current"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldGetMdmIdsForAssignedSellers() throws Exception {
    Page<InventoryMdmIdDTO> pagedResults =
        new PageImpl<>(
            List.of(
                InventoryMdmIdDTO.builder().sellerPid(1L).companyMdmIds(Set.of("100")).build(),
                InventoryMdmIdDTO.builder()
                    .sellerPid(2L)
                    .sellerSeatMdmIds(Set.of("43", "44"))
                    .build()));

    when(mdmIdService.getMdmIdsForAssignedSellers(
            any(InventoryMdmIdQueryFieldParams.class), any(Pageable.class)))
        .thenReturn(pagedResults);

    String expectedJson = objectMapper.writeValueAsString(pagedResults);
    mockMvc
        .perform(get("/v1/mdmids?qf=sellerPid=14,dealPid=953"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldFailWhenServiceThrowsException() throws Exception {
    when(mdmIdService.getMdmIdsForAssignedSellers(
            any(InventoryMdmIdQueryFieldParams.class), any(Pageable.class)))
        .thenThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVENTORY_MDM_REQUEST_NOT_ALLOWED));

    mockMvc
        .perform(get("/v1/mdmids?qf=sellerPid=14,dealPid=953"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage", is("Requesting inventory MDM IDs is not allowed.")));
  }
}
