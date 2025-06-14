package com.nexage.app.web;

import static com.nexage.app.web.support.TestObjectsFactory.createBasicDirectDeal;
import static com.nexage.app.web.support.TestObjectsFactory.createBasicDirectDealDTO;
import static com.nexage.app.web.support.TestObjectsFactory.createCompanyRule;
import static com.nexage.app.web.support.TestObjectsFactory.createDealWithDealRule;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTarget;
import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.impl.DealServiceImpl;
import com.nexage.app.services.impl.DirectDealServiceImpl;
import com.nexage.app.util.CustomObjectMapper;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class DirectDealControllerIT {

  @Mock private Validator mockValidator;
  @Mock private UserContext userContext;
  @Mock private CompanyRuleRepository companyRuleRepository;
  @Mock private DirectDealServiceImpl directDealService;
  @Mock private DirectDealRepository directDealRepository;
  @InjectMocks private DealServiceImpl dealService;
  @InjectMocks private DirectDealController directDealController;

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new CustomObjectMapper();

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(directDealController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldCreateDealWithNoPlacementFormula() {
    DirectDealDTO dealDTO = createDealWithDealRule(33L, 55L);
    CompanyRule ruleDTO = createCompanyRule(55L);
    ReflectionTestUtils.setField(ruleDTO, "ruleType", com.nexage.admin.core.enums.RuleType.DEAL);
    ReflectionTestUtils.setField(ruleDTO, "status", Status.ACTIVE);
    ReflectionTestUtils.setField(ruleDTO, "ruleTargets", singleton(createRuleTarget()));
    ReflectionTestUtils.setField(dealDTO, "placementFormula", null);
    ReflectionTestUtils.setField(dealDTO, "status", DirectDeal.DealStatus.Active);
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);
    dealDTO
        .getSellers()
        .add(new DealPublisherDTO.Builder().setPid(33L).setPublisherPid(3L).build());
    when(companyRuleRepository.findById(anyLong())).thenReturn(Optional.of(ruleDTO));
    directDealController.createDeal(dealDTO);
    verify(directDealService).createDeal(dealDTO);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/deals/1", "/deals", "/deals/1/publisher_map"})
  void shouldReturnOk(String api) throws Exception {
    DirectDeal deal = createBasicDirectDeal(1L);
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    when(directDealRepository.findById(1L)).thenReturn(Optional.of(deal));
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "guaranteedImpressionGoal", 100L);
    ReflectionTestUtils.setField(dealDTO, "dailyImpressionCap", 100L);
    ReflectionTestUtils.setField(dealDTO, "start", new Date(1620792000000L));
    ReflectionTestUtils.setField(dealDTO, "stop", new Date(1620964800000L));
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc.perform(get(api)).andExpect(status().isOk());
  }

  @Test
  void shouldBeAbleToGetRulesAssociatedWithDeal() throws Exception {
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    when(directDealRepository.findByPid(1L)).thenReturn(null);
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "guaranteedImpressionGoal", 100L);
    ReflectionTestUtils.setField(dealDTO, "dailyImpressionCap", 100L);
    ReflectionTestUtils.setField(dealDTO, "start", new Date(1620792000000L));
    ReflectionTestUtils.setField(dealDTO, "stop", new Date(1620964800000L));
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc.perform(get("/deals/1/rule")).andExpect(status().isOk());
  }

  @Test
  void shouldSuccessfullyCreateGuaranteedDeal() throws Exception {
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "guaranteedImpressionGoal", 100L);
    ReflectionTestUtils.setField(dealDTO, "dailyImpressionCap", 100L);
    ReflectionTestUtils.setField(dealDTO, "start", new Date(1620792000000L));
    ReflectionTestUtils.setField(dealDTO, "stop", new Date(1620964800000L));
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldSuccessfullyUpdateGuaranteedDeal() throws Exception {
    DirectDeal deal = createBasicDirectDeal(1L);
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    when(directDealRepository.findById(1L)).thenReturn(Optional.of(deal));
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "guaranteedImpressionGoal", 100L);
    ReflectionTestUtils.setField(dealDTO, "dailyImpressionCap", 100L);
    ReflectionTestUtils.setField(dealDTO, "start", new Date(1620792000000L));
    ReflectionTestUtils.setField(dealDTO, "stop", new Date(1620964800000L));
    ReflectionTestUtils.setField(dealDTO, "viewability", 0.32f);

    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            put("/deals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldNullifyImpressionGoalsWhenUpdatedPriorityIsNotGuaranteed() throws Exception {
    DirectDeal deal = createBasicDirectDeal(1L);
    deal.setPriorityType(DealPriorityType.OPEN);
    deal.setGuaranteedImpressionGoal(100L);
    deal.setDailyImpressionCap(100L);
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    when(directDealRepository.findById(1L)).thenReturn(Optional.of(deal));
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "start", new Date(1620792000000L));
    ReflectionTestUtils.setField(dealDTO, "stop", new Date(1620964800000L));
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            put("/deals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldSuccessfullyCreatePacingEnabledDeal() throws Exception {
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    addDirectDealCommonData(dealDTO);
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "guaranteedImpressionGoal", 100L);
    ReflectionTestUtils.setField(dealDTO, "dailyImpressionCap", 100L);
    ReflectionTestUtils.setField(dealDTO, "pacingEnabled", true);
    ReflectionTestUtils.setField(dealDTO, "pacingStrategy", 1);
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldSuccessfullyUpdatePacingEnabledDeal() throws Exception {
    DirectDeal deal = createBasicDirectDeal(1L);
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    addDirectDealCommonData(dealDTO);
    when(directDealRepository.findById(1L)).thenReturn(Optional.of(deal));
    ReflectionTestUtils.setField(dealDTO, "priorityType", DealPriorityType.OPEN);
    ReflectionTestUtils.setField(dealDTO, "guaranteedImpressionGoal", 100L);
    ReflectionTestUtils.setField(dealDTO, "dailyImpressionCap", 100L);
    ReflectionTestUtils.setField(dealDTO, "pacingEnabled", true);
    ReflectionTestUtils.setField(dealDTO, "pacingStrategy", 1);

    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            put("/deals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldSuccessfullyCreateDealWithExternalDealId() throws Exception {
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    ReflectionTestUtils.setField(dealDTO, "external", true);
    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldSuccessfullyUpdateDealWithExternalDealId() throws Exception {
    DirectDeal deal = createBasicDirectDeal(1L);
    DirectDealDTO dealDTO = createBasicDirectDealDTO(1L);
    when(directDealRepository.findById(1L)).thenReturn(Optional.of(deal));
    ReflectionTestUtils.setField(dealDTO, "external", false);

    SpringUserDetails u = mock(SpringUserDetails.class);
    when(u.getPid()).thenReturn(67L);
    when(userContext.getCurrentUser()).thenReturn(u);

    mockMvc
        .perform(
            put("/deals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealDTO)))
        .andExpect(status().isOk());
  }

  private void addDirectDealCommonData(DirectDealDTO dealDTO) {
    ReflectionTestUtils.setField(dealDTO, "start", new Date(1620792000000L));
    ReflectionTestUtils.setField(dealDTO, "stop", new Date(1620964800000L));
  }
}
