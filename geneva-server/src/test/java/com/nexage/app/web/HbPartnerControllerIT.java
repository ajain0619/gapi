package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.dto.HbPartnerRequestDTO;
import com.nexage.app.services.HbPartnerService;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class HbPartnerControllerIT extends BaseControllerItTest {

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @InjectMocks private HbPartnerController hbPartnerController;
  @Mock private HbPartnerService hbPartnerService;
  private MockMvc mockMvc;
  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(hbPartnerController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void getAllHbPartners() throws Throwable {
    List<HbPartnerDTO> hbPartnerDTOList = TestObjectsFactory.gimme(10, HbPartnerDTO.class);
    Page hbPartnerPage = new PageImpl(hbPartnerDTOList);
    HbPartnerDTO first = hbPartnerDTOList.get(0);
    when(hbPartnerService.getHbPartners(any(HbPartnerRequestDTO.class))).thenReturn(hbPartnerPage);
    mockMvc
        .perform(get("/v1/hbpartners"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())))
        .andExpect(
            jsonPath(
                "content.[0].formattedDefaultTypeEnabled",
                is(first.isFormattedDefaultTypeEnabled())))
        .andExpect(jsonPath("content.[0].multiImpressionBid", is(first.isMultiImpressionBid())))
        .andExpect(jsonPath("content.[0].maxAdsPerPod", is(first.getMaxAdsPerPod())))
        .andExpect(jsonPath("content.[0].fillMaxDuration", is(first.isFillMaxDuration())))
        .andExpect(jsonPath("content.length()", is(10)));
  }

  @Test
  void getHbPartners() throws Throwable {
    List<HbPartnerDTO> hbPartnerDTOList = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    HbPartnerDTO first = hbPartnerDTOList.get(0);
    when(hbPartnerService.getHbPartner(any(Long.class))).thenReturn(first);
    mockMvc
        .perform(get("/v1/hbpartners/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("pid", is(first.getPid())))
        .andExpect(
            jsonPath("formattedDefaultTypeEnabled", is(first.isFormattedDefaultTypeEnabled())))
        .andExpect(jsonPath("name", is(first.getName())))
        .andExpect(jsonPath("multiImpressionBid", is(first.isMultiImpressionBid())))
        .andExpect(jsonPath("maxAdsPerPod", is(first.getMaxAdsPerPod())))
        .andExpect(jsonPath("fillMaxDuration", is(first.isFillMaxDuration())));
  }

  @Test
  void createHbPartners() throws Throwable {

    HbPartnerDTO newHbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    newHbPartnerDTO.setMultiImpressionBid(true);
    newHbPartnerDTO.setMaxAdsPerPod(8);
    String reqeustHbPartnerJSON = mapper.writeValueAsString(newHbPartnerDTO);

    when(hbPartnerService.createHbPartner(any(HbPartnerDTO.class))).thenReturn(newHbPartnerDTO);
    mockMvc
        .perform(
            post("/v1/hbpartners/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqeustHbPartnerJSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("pid", is(newHbPartnerDTO.getPid().intValue())))
        .andExpect(jsonPath("name", is(newHbPartnerDTO.getName())))
        .andExpect(jsonPath("status", is(newHbPartnerDTO.getStatus().toString())))
        .andExpect(jsonPath("version", is(newHbPartnerDTO.getVersion())))
        .andExpect(jsonPath("id", is(newHbPartnerDTO.getId())))
        .andExpect(jsonPath("partnerHandler", is(newHbPartnerDTO.getPartnerHandler())))
        .andExpect(jsonPath("description", is(newHbPartnerDTO.getDescription())))
        .andExpect(
            jsonPath(
                "formattedDefaultTypeEnabled", is(newHbPartnerDTO.isFormattedDefaultTypeEnabled())))
        .andExpect(jsonPath("multiImpressionBid", is(newHbPartnerDTO.isMultiImpressionBid())))
        .andExpect(jsonPath("maxAdsPerPod", is(newHbPartnerDTO.getMaxAdsPerPod())));
  }

  @Test
  void updateHbPartners() throws Throwable {
    HbPartnerDTO newHbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    newHbPartnerDTO.setMultiImpressionBid(true);
    newHbPartnerDTO.setFillMaxDuration(true);
    String reqeustHbPartnerJSON = mapper.writeValueAsString(newHbPartnerDTO);

    when(hbPartnerService.updateHbPartner((any(HbPartnerDTO.class)))).thenReturn(newHbPartnerDTO);
    mockMvc
        .perform(
            put("/v1/hbpartners/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqeustHbPartnerJSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("pid", is(newHbPartnerDTO.getPid().intValue())))
        .andExpect(jsonPath("name", is(newHbPartnerDTO.getName())))
        .andExpect(jsonPath("status", is(newHbPartnerDTO.getStatus().toString())))
        .andExpect(jsonPath("version", is(newHbPartnerDTO.getVersion())))
        .andExpect(jsonPath("id", is(newHbPartnerDTO.getId())))
        .andExpect(jsonPath("partnerHandler", is(newHbPartnerDTO.getPartnerHandler())))
        .andExpect(jsonPath("description", is(newHbPartnerDTO.getDescription())))
        .andExpect(
            jsonPath(
                "formattedDefaultTypeEnabled", is(newHbPartnerDTO.isFormattedDefaultTypeEnabled())))
        .andExpect(jsonPath("multiImpressionBid", is(newHbPartnerDTO.isMultiImpressionBid())))
        .andExpect(jsonPath("fillMaxDuration", is(newHbPartnerDTO.isFillMaxDuration())));
  }

  @Test
  void updateHbPartnersInvalidPid() throws Throwable {

    HbPartnerDTO newHbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    String reqeustHbPartnerJSON = mapper.writeValueAsString(newHbPartnerDTO);
    when(hbPartnerService.updateHbPartner((any(HbPartnerDTO.class)))).thenReturn(newHbPartnerDTO);
    try {
      mockMvc.perform(
          put("/v1/hbpartners/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(reqeustHbPartnerJSON));
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof GenevaValidationException, "Invalid exception");
    }
  }

  @Test
  void shouldThrowErrorWhenCreateHbPartnersInvalidMaxAdsPerPod() throws Throwable {

    HbPartnerDTO newHbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    newHbPartnerDTO.setMaxAdsPerPod(0);
    String reqeustHbPartnerJSON = mapper.writeValueAsString(newHbPartnerDTO);
    when(hbPartnerService.createHbPartner(any(HbPartnerDTO.class))).thenReturn(newHbPartnerDTO);

    mockMvc
        .perform(
            post("/v1/hbpartners/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqeustHbPartnerJSON))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.fieldErrors.maxAdsPerPod", is(ValidationMessages.WRONG_NUMBER_MIN)));
  }

  @Test
  void shouldThrowErrorWhenUpdateHbPartnersInvalidMaxAdsPerPod() throws Throwable {

    HbPartnerDTO newHbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    newHbPartnerDTO.setMaxAdsPerPod(16);
    String reqeustHbPartnerJSON = mapper.writeValueAsString(newHbPartnerDTO);
    when(hbPartnerService.updateHbPartner((any(HbPartnerDTO.class)))).thenReturn(newHbPartnerDTO);

    mockMvc
        .perform(
            put("/v1/hbpartners/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqeustHbPartnerJSON))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.fieldErrors.maxAdsPerPod", is(ValidationMessages.WRONG_NUMBER_MAX)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v1/hbpartners",
        "/v1/hbpartners?publisher=123&detail=false",
        "/v1/hbpartners?publisher=123&site=123&detail=false"
      })
  void shouldGetHbPartners(String api) throws Throwable {
    Page<HbPartnerDTO> hbPartnerDTOList =
        new PageImpl<>(TestObjectsFactory.gimme(5, HbPartnerDTO.class));
    when(hbPartnerService.getHbPartners(any(HbPartnerRequestDTO.class)))
        .thenReturn(hbPartnerDTOList);

    mockMvc
        .perform(get(api))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.totalElements", is(5)))
        .andExpect(jsonPath("$.totalPages", is(1)));
  }
}
