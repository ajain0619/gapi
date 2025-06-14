package com.nexage.app.web;

import static com.nexage.app.web.support.TestObjectsFactory.createFilterRuleIntendedActionBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTargetBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRuleBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRuleDto;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.services.SellerSeatRuleService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerSeatRuleControllerIT {

  private static final String BASE_URL = "/v1/seller-seats/{sellerSeatPid}/rules";
  private static final long DEFAULT_SELLER_SEAT_ID = 12345L;
  private static final long DEFAULT_RULE_ID = 23456L;
  private static final SellerSeatRuleDTO DEFAULT_SELLER_SEAT_RULE =
      createSellerSeatRuleDto(DEFAULT_RULE_ID, DEFAULT_SELLER_SEAT_ID);

  private MockMvc mockMvc;

  @Mock private SellerSeatRuleService sellerSeatRuleService;
  @InjectMocks private SellerSeatRuleController controller;

  @Autowired LocalValidatorFactoryBean beanValidator;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(beanValidator)
            .build();
  }

  @Test
  void shouldGetAllSellerSeatRules() throws Exception {
    List<SellerSeatRuleDTO> rules = Lists.newArrayList(DEFAULT_SELLER_SEAT_RULE);

    given(
            sellerSeatRuleService.findRulesInSellerSeat(
                eq(DEFAULT_SELLER_SEAT_ID), eq(null), eq(null), eq(null), eq(null), any()))
        .willReturn(new PageImpl(rules));

    mockMvc
        .perform(get(BASE_URL, DEFAULT_SELLER_SEAT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("content.[0].pid", is(DEFAULT_SELLER_SEAT_RULE.getPid().intValue())));
  }

  @Test
  void shouldGetSelectedSellerSeatRule() throws Exception {
    given(sellerSeatRuleService.findSellerSeatRule(DEFAULT_SELLER_SEAT_ID, DEFAULT_RULE_ID))
        .willReturn(DEFAULT_SELLER_SEAT_RULE);

    mockMvc
        .perform(
            get(BASE_URL.concat("/{sellerSeatRulePid}"), DEFAULT_SELLER_SEAT_ID, DEFAULT_RULE_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.pid", is(DEFAULT_SELLER_SEAT_RULE.getPid().intValue())));
  }

  @Test
  void shouldCreateASellerSeatRule() throws Exception {
    IntendedActionDTO intendedAction =
        createFilterRuleIntendedActionBuilder().pid(null).version(null).build();
    RuleTargetDTO target =
        createRuleTargetBuilder(null, MatchType.EXCLUDE_LIST).version(null).build();
    SellerSeatRuleDTO sellerSeatRule =
        createSellerSeatRuleBuilder(null, DEFAULT_SELLER_SEAT_ID, null, RuleType.BRAND_PROTECTION)
            .targets(Set.of(target))
            .intendedActions(Set.of(intendedAction))
            .build();
    String payload = new ObjectMapper().writeValueAsString(sellerSeatRule);

    given(sellerSeatRuleService.save(eq(DEFAULT_SELLER_SEAT_ID), any()))
        .willReturn(DEFAULT_SELLER_SEAT_RULE);

    mockMvc
        .perform(
            post(BASE_URL, DEFAULT_SELLER_SEAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("pid", is(DEFAULT_SELLER_SEAT_RULE.getPid().intValue())));
  }

  @Test
  void shouldUpdateASellerSeatRule() throws Exception {
    String payload = new ObjectMapper().writeValueAsString(DEFAULT_SELLER_SEAT_RULE);

    given(sellerSeatRuleService.update(eq(DEFAULT_SELLER_SEAT_ID), eq(DEFAULT_RULE_ID), any()))
        .willReturn(DEFAULT_SELLER_SEAT_RULE);

    mockMvc
        .perform(
            put(BASE_URL.concat("/{sellerSeatRulePid}"), DEFAULT_SELLER_SEAT_ID, DEFAULT_RULE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("pid", is(DEFAULT_SELLER_SEAT_RULE.getPid().intValue())));
  }

  @Test
  void shouldDeleteASellerSeatRule() throws Exception {
    given(sellerSeatRuleService.delete(DEFAULT_SELLER_SEAT_ID, DEFAULT_RULE_ID))
        .willReturn(SellerSeatRuleDTO.builder().pid(DEFAULT_RULE_ID).build());

    mockMvc
        .perform(
            delete(
                BASE_URL.concat("/{sellerSeatRulePid}"), DEFAULT_SELLER_SEAT_ID, DEFAULT_RULE_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("pid", is((int) DEFAULT_RULE_ID)));
  }
}
