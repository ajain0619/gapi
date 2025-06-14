package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.seller.SellerAttributesDTO;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerAttributesDTOService;
import com.nexage.app.util.CustomObjectMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerAttributesDTOControllerIT extends SpringWebConstraintValidatorFactory {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  private MockMvc mockMvc;
  @Autowired private MockServletContext servletContext;
  @InjectMocks private SellerAttributesDTOController controller;
  @Mock private SellerAttributesDTOService service;

  private ObjectMapper objectMapper;

  public SellerAttributesDTOControllerIT() {}

  @BeforeEach
  public void setUp() {
    objectMapper = new CustomObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setValidator(
                createLocalValidatorFactoryBean(new GenericWebApplicationContext(servletContext)))
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Override
  protected WebApplicationContext getWebApplicationContext() {
    return webApplicationContext;
  }

  @Test
  void getSellerAttributes_validSeller_returnSellerAttributesDTO() throws Throwable {
    Long sellerPid = RandomUtils.nextLong();
    Pageable pageable = PageRequest.of(0, 10);

    SellerAttributesDTO dto = new SellerAttributesDTO();
    dto.setSellerPid(sellerPid);
    dto.setVersion(0);

    Page<SellerAttributesDTO> pageDto = new PageImpl<>(Collections.singletonList(dto));
    when(service.getSellerAttribute(sellerPid, pageable)).thenReturn(pageDto);

    mockMvc
        .perform(get("/v1/sellers/{sellerId}/seller-attributes", sellerPid))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].sellerPid", is(sellerPid)));
  }

  @Test
  void getSellerAttributes_InvalidSeller_returnEmptyContent() throws Throwable {
    Long sellerPid = RandomUtils.nextLong();
    Pageable pageable = PageRequest.of(0, 10);
    Page<SellerAttributesDTO> dtoPage = new PageImpl<>(List.of());

    when(service.getSellerAttribute(sellerPid, pageable)).thenReturn(dtoPage);

    mockMvc
        .perform(get("/v1/sellers/{sellerId}/seller-attributes", sellerPid))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  void updateSellerAttributes_whenSellerIdDontMatch_throwException() throws Throwable {
    Long sellerPid = 1L;

    SellerAttributesDTO dto = new SellerAttributesDTO();
    dto.setSellerPid(2L);
    dto.setVersion(0);
    dto.setHumanOptOut(false);
    dto.setSmartQPSEnabled(false);
    dto.setAdStrictApproval(false);
    dto.setDefaultTransparencyMgmtEnablement(TransparencyMgmtEnablement.ENABLED);
    dto.setRevenueGroupPid(1L);
    dto.setRevenueShare(BigDecimal.ZERO);
    dto.setSellerIdAlias(1L);
    dto.setSellerNameAlias("1");
    dto.setTransparencyMode(TransparencyMode.RealName);

    mockMvc
        .perform(
            put("/v1/sellers/{sellerId}/seller-attributes", sellerPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_PIDS_MISMATCH))));
  }

  @Test
  void updateSellerAttributes_whenInvalidSeller_throwException() throws Throwable {
    Long sellerPid = RandomUtils.nextLong();

    SellerAttributesDTO dto = new SellerAttributesDTO();
    dto.setSellerPid(sellerPid);
    dto.setVersion(0);
    dto.setHumanOptOut(false);
    dto.setSmartQPSEnabled(false);
    dto.setAdStrictApproval(false);
    dto.setDefaultTransparencyMgmtEnablement(TransparencyMgmtEnablement.ENABLED);
    dto.setRevenueGroupPid(1L);
    dto.setRevenueShare(BigDecimal.ZERO);
    dto.setSellerIdAlias(1L);
    dto.setSellerNameAlias("1");
    dto.setTransparencyMode(TransparencyMode.RealName);

    when(service.updateSellerAttribute(dto))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_NON_EXISTENT_TARGET_PID));

    mockMvc
        .perform(
            put("/v1/sellers/{sellerId}/seller-attributes", sellerPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_NON_EXISTENT_TARGET_PID))));
  }

  @Test
  void updateSellerAttributes_whenValidSeller_returnSellerAttributeDTO() throws Throwable {
    Long sellerPid = RandomUtils.nextLong();

    SellerAttributesDTO dto = new SellerAttributesDTO();
    dto.setSellerPid(sellerPid);
    dto.setVersion(0);
    dto.setHumanOptOut(true);
    dto.setSmartQPSEnabled(true);
    dto.setAdStrictApproval(false);
    dto.setDefaultTransparencyMgmtEnablement(TransparencyMgmtEnablement.ENABLED);
    dto.setRevenueGroupPid(1L);
    dto.setRevenueShare(BigDecimal.ZERO);
    dto.setSellerIdAlias(1L);
    dto.setSellerNameAlias("1");
    dto.setTransparencyMode(TransparencyMode.RealName);

    when(service.updateSellerAttribute(dto)).thenReturn(dto);

    mockMvc
        .perform(
            put("/v1/sellers/{sellerId}/seller-attributes", sellerPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.humanOptOut", is(true)))
        .andExpect(jsonPath("$.smartQPSEnabled", is(true)));
  }

  private LocalValidatorFactoryBean createLocalValidatorFactoryBean(
      GenericWebApplicationContext context) {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setApplicationContext(context);
    validatorFactoryBean.setConstraintValidatorFactory(this);
    validatorFactoryBean.setProviderClass(HibernateValidator.class);
    validatorFactoryBean.afterPropertiesSet();
    return validatorFactoryBean;
  }
}
