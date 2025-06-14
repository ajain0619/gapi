package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentBuyerDTO;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentSellerDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.FeeAdjustmentService;
import com.nexage.app.web.feeadjustment.FeeAdjustmentController;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.List;
import java.util.Set;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class FeeAdjustmentControllerIT extends BaseControllerItTest {

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private MockServletContext servletContext;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Mock private FeeAdjustmentService feeAdjustmentServiceMock;

  @InjectMocks private FeeAdjustmentController feeAdjustmentController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

    localValidatorFactoryBean.setApplicationContext(
        new GenericWebApplicationContext(servletContext));
    localValidatorFactoryBean.setConstraintValidatorFactory(
        new SpringWebConstraintValidatorFactory() {
          @Override
          public WebApplicationContext getWebApplicationContext() {
            return webApplicationContext;
          }
        });
    localValidatorFactoryBean.setProviderClass(HibernateValidator.class);
    localValidatorFactoryBean.afterPropertiesSet();

    mockMvc =
        MockMvcBuilders.standaloneSetup(feeAdjustmentController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setValidator(localValidatorFactoryBean)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void testCreateFeeAdjustment() throws Exception {
    FeeAdjustmentDTO inboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(null)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(null)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName(null).build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName(null).build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName(null).build()))
            .build();
    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(1)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();
    String expectedOutboundJson = objectMapper.writeValueAsString(outboundFeeAdjustmentDTO);

    when(feeAdjustmentServiceMock.create(any(FeeAdjustmentDTO.class)))
        .thenReturn(outboundFeeAdjustmentDTO);

    mockMvc
        .perform(
            post("/v1/fee-adjustments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(inboundFeeAdjustmentDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void testUpdateFeeAdjustment() throws Exception {
    FeeAdjustmentDTO inboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(1)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName(null).build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName(null).build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName(null).build()))
            .build();
    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(2)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();
    String expectedOutboundJson = objectMapper.writeValueAsString(outboundFeeAdjustmentDTO);

    when(feeAdjustmentServiceMock.update(any(FeeAdjustmentDTO.class)))
        .thenReturn(outboundFeeAdjustmentDTO);

    mockMvc
        .perform(
            put("/v1/fee-adjustments/1234")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(inboundFeeAdjustmentDTO)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void testUpdateFeeAdjustmentUrlPidMismatch() throws Exception {
    FeeAdjustmentDTO inboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(1)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName(null).build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName(null).build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName(null).build()))
            .build();
    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(2)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();
    String expectedOutboundJson = objectMapper.writeValueAsString(inboundFeeAdjustmentDTO);

    when(feeAdjustmentServiceMock.update(any(FeeAdjustmentDTO.class)))
        .thenReturn(outboundFeeAdjustmentDTO);

    mockMvc
        .perform(
            put("/v1/fee-adjustments/4321")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(expectedOutboundJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_PIDS_MISMATCH))));
  }

  @Test
  void testGetFeeAdjustment() throws Exception {
    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1234")
            .inclusive(true)
            .demandFeeAdjustment(0.1234)
            .version(4321)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();
    String expectedOutboundJson = objectMapper.writeValueAsString(outboundFeeAdjustmentDTO);

    when(feeAdjustmentServiceMock.get(outboundFeeAdjustmentDTO.getPid()))
        .thenReturn(outboundFeeAdjustmentDTO);

    mockMvc
        .perform(get("/v1/fee-adjustments/1234"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void testGetAllFeeAdjustments() throws Exception {
    Page<FeeAdjustmentDTO> outboundFeeAdjustmentDTOs =
        new PageImpl(
            List.of(
                FeeAdjustmentDTO.builder()
                    .pid(1L)
                    .name("fee-adjustment-1")
                    .inclusive(true)
                    .demandFeeAdjustment(0.1)
                    .version(0)
                    .enabled(true)
                    .description("A test fee adjustment.")
                    .entityName("buyer 1, buyer 2")
                    .build(),
                FeeAdjustmentDTO.builder()
                    .pid(2L)
                    .name("fee-adjustment-2")
                    .inclusive(false)
                    .demandFeeAdjustment(0.2)
                    .version(1)
                    .enabled(false)
                    .description("A test fee adjustment.")
                    .entityName("buyer 1, buyer 2")
                    .build()));
    String expectedOutboundJson = objectMapper.writeValueAsString(outboundFeeAdjustmentDTOs);

    when(feeAdjustmentServiceMock.getAll(
            nullable(Set.class),
            nullable(String.class),
            nullable(Boolean.class),
            nullable(Pageable.class)))
        .thenReturn(outboundFeeAdjustmentDTOs);

    mockMvc
        .perform(get("/v1/fee-adjustments"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void testGetAllPagedQfQtEnabledFeeAdjustments() throws Exception {
    Page<FeeAdjustmentDTO> outboundFeeAdjustmentDTOs =
        new PageImpl<>(
            List.of(
                FeeAdjustmentDTO.builder()
                    .pid(1234L)
                    .name("fee-adjustment-test-1")
                    .inclusive(true)
                    .demandFeeAdjustment(0.1)
                    .version(0)
                    .enabled(true)
                    .description("A test fee adjustment.")
                    .entityName("buyer 1, buyer 2")
                    .build()));
    String expectedOutboundJson = objectMapper.writeValueAsString(outboundFeeAdjustmentDTOs);

    when(feeAdjustmentServiceMock.getAll(
            nullable(Set.class),
            nullable(String.class),
            nullable(Boolean.class),
            nullable(Pageable.class)))
        .thenReturn(outboundFeeAdjustmentDTOs);

    mockMvc
        .perform(
            get(
                String.format(
                    "/v1/fee-adjustments?page=%s&size=%s&qf=%s&qt=%s&enabled=%s",
                    "0", "10", "name", "test-1", "true")))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void testDeleteFeeAdjustment() throws Exception {
    FeeAdjustmentDTO outboundFeeAdjustmentDTO = FeeAdjustmentDTO.builder().pid(1234L).build();
    String expectedOutboundJson = objectMapper.writeValueAsString(outboundFeeAdjustmentDTO);

    when(feeAdjustmentServiceMock.delete(1234L)).thenReturn(outboundFeeAdjustmentDTO);

    mockMvc
        .perform(delete("/v1/fee-adjustments/1234"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }
}
