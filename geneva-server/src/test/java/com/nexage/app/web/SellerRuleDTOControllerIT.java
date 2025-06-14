package com.nexage.app.web;

import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.INVENTORY_ATTRIBUTE;
import static com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO.OR;
import static com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO.EQUALS;
import static com.nexage.app.web.support.TestObjectsFactory.createFilterRuleIntendedActionBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTargetBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDtoBuilder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO.SellerRuleDTOBuilder;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PublisherRuleService;
import com.nexage.app.services.sellingrule.SellerRuleService;
import com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.validator.rule.queryfield.SellerRuleQueryParams;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.MultiValueSearchParamsArgumentResolver;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerRuleDTOControllerIT {

  private static final String BASE_URL = "/v1/sellers/{sellerPid}/rules";
  private static final long PUBLISHER_PID = 1000L;
  private static final String RULE_TYPE_ARGS = "BRAND_PROTECTION%2CDEAL";
  private static final String STATUS_ARGS = "ACTIVE%2CINACTIVE";

  @Mock private PublisherRuleService publisherRuleService;
  @Mock private SellerRuleService sellerRuleService;
  @Mock private BeanValidationService beanValidationService;
  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper objectMapper;
  @InjectMocks private SellerRuleDTOController sellerRuleDTOController;
  private MockMvc mockMvc;
  @Autowired private LocalValidatorFactoryBean beanValidator;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(sellerRuleDTOController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new MultiValueSearchParamsArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .setValidator(beanValidator)
            .build();
  }

  @Test
  void shouldFetchRules() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Integer pageIndex = 0;
    Integer pageSize = 4;
    String sortParam = "pid,DESC";
    Set<String> qfSet = new HashSet<>();
    qfSet.add("name");
    String qfString = "name";
    String qt = "123";
    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(0, 4, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            publisherPid, RULE_TYPE_ARGS, STATUS_ARGS, pageRequest, qfSet, qt))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL
                    + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&page={pageIndex}&size={pageSize}&sort={sortParam}&qf={qfString}&qt={qt}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                pageIndex,
                pageSize,
                sortParam,
                qfString,
                qt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(4)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)));
  }

  @Test
  void shouldFetchRulesWithoutPageable() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Set<String> qf = new HashSet<>();
    qf.add("pid");
    String qt = "123";
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable defaultPageRequest = PageRequest.of(0, 10, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, defaultPageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            publisherPid, RULE_TYPE_ARGS, STATUS_ARGS, defaultPageRequest, qf, qt))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&qf=pid&qt={qt}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                qt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(10)));
  }

  @Test
  void shouldFetchRulesWithSortOnly() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    String sortParam = "ruleType,DESC";
    Set<String> qf = new HashSet<>();
    qf.add("pid");
    String qt = "123";
    Sort sort = Sort.by(Sort.Direction.DESC, "ruleType");
    Pageable defaultPageRequest = PageRequest.of(0, 10, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, defaultPageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            publisherPid, RULE_TYPE_ARGS, STATUS_ARGS, defaultPageRequest, qf, qt))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL
                    + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&sort={sortParam}&qf=pid&qt={qt}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                sortParam,
                qt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(10)));
  }

  @Test
  void shouldFetchRulesWithPidQueryTerm() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Integer pageIndex = 0;
    Integer pageSize = 4;
    String sortParam = "pid,DESC";
    Set<String> qf = new HashSet<>();
    qf.add("pid");
    String qt = "123";
    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(0, 4, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            publisherPid, RULE_TYPE_ARGS, STATUS_ARGS, pageRequest, qf, qt))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL
                    + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&page={pageIndex}&size={pageSize}&sort={sortParam}&qf=pid&qt={qt}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                pageIndex,
                pageSize,
                sortParam,
                qt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(4)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)));
  }

  @Test
  void shouldFetchRulesWithNameQueryTerm() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Integer pageIndex = 0;
    Integer pageSize = 4;
    String sortParam = "pid,DESC";
    Set<String> qf = new HashSet<>();
    qf.add("name");
    String qt = "test";
    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(0, 4, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            publisherPid, RULE_TYPE_ARGS, STATUS_ARGS, pageRequest, qf, qt))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL
                    + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&page={pageIndex}&size={pageSize}&sort={sortParam}&qf=name&qt={qt}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                pageIndex,
                pageSize,
                sortParam,
                qt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(4)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)));
  }

  @Test
  void shouldFetchRulesWithPidAndNameQueryTerm() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Integer pageIndex = 0;
    Integer pageSize = 4;
    String sortParam = "pid,DESC";
    Set<String> qf = new HashSet<>();
    qf.add("pid");
    qf.add("name");
    String qt = "123";
    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(0, 4, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            publisherPid, RULE_TYPE_ARGS, STATUS_ARGS, pageRequest, qf, qt))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL
                    + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&page={pageIndex}&size={pageSize}&sort={sortParam}&qf=pid,name&qt={qt}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                pageIndex,
                pageSize,
                sortParam,
                qt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(4)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)));
  }

  @Test
  void shouldFetchRulesWithoutQueryTerm() throws Exception {
    Long publisherPid = RandomUtils.nextLong();
    List<SellerRuleDTO> rules =
        Lists.newArrayList(
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION),
            createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Integer pageIndex = 0;
    Integer pageSize = 4;
    String sortParam = "pid,DESC";
    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(0, 4, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, 4);

    when(publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            eq(publisherPid),
            eq(RULE_TYPE_ARGS),
            eq(STATUS_ARGS),
            eq(pageRequest),
            nullable(HashSet.class),
            nullable(String.class)))
        .thenReturn(dtoPage);
    mockMvc
        .perform(
            get(
                BASE_URL
                    + "?type={RULE_TYPE_ARGS}&status={STATUS_ARGS}&page={pageIndex}&size={pageSize}&sort={sortParam}",
                publisherPid,
                RULE_TYPE_ARGS,
                STATUS_ARGS,
                pageIndex,
                pageSize,
                sortParam))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements", is(4)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(4)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)));
  }

  @Test
  void shouldReturnNotFoundStatusWhenFetchingNonexistentRule() throws Exception {
    Long sellerPid = 1L, rulePid = 5L;
    mockMvc
        .perform(get(BASE_URL + "/{rulePid}", sellerPid, rulePid))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFetchSingleRule() throws Exception {
    Long sellerPid = 1L, rulePid = 5L;

    when(sellerRuleService.findByPidAndSellerPid(rulePid, sellerPid))
        .thenReturn(createRule(rulePid, RuleType.BRAND_PROTECTION));

    mockMvc
        .perform(get(BASE_URL + "/{rulePid}", sellerPid, rulePid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(rulePid.intValue())))
        .andExpect(jsonPath("$.type", is(RuleType.BRAND_PROTECTION.name())));
  }

  @Test
  void shouldDeleteRule() throws Exception {
    Long sellerPid = 1L, rulePid = 5L;

    when(sellerRuleService.deleteByPidAndSellerPid(rulePid, sellerPid))
        .thenReturn(SellerRuleDTO.builder().pid(5L).build());

    mockMvc
        .perform(delete(BASE_URL + "/{rulePid}", sellerPid, rulePid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(rulePid.intValue())));
  }

  @Test
  void shouldCreateRule() throws Exception {
    long publisherPid = 1000L;

    given(sellerRuleService.create(eq(publisherPid), any(SellerRuleDTO.class)))
        .willAnswer(a -> a.getArguments()[1]);

    SellerRuleDTOBuilder builder = createRuleForCreate();

    mockMvc
        .perform(
            post(BASE_URL, publisherPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.intendedActions", hasSize(1)))
        .andExpect(jsonPath("$.targets", hasSize(1)))
        .andExpect(jsonPath("$.status", equalTo("ACTIVE")));
  }

  @Test
  void shouldReturnBadRequestStatusWhenCreatingRuleWithRuleFormulaWithoutPlacementFormula()
      throws Exception {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    builder.ruleFormula(RuleFormulaDTO.builder().build());

    mockMvc
        .perform(
            post(BASE_URL, PUBLISHER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula']", equalTo("must not be null")));
  }

  @Test
  void shouldReturnBadRequestStatusWhenCreatingRuleWithRuleFormulaWithEmptyPlacementFormula()
      throws Exception {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    builder.ruleFormula(
        RuleFormulaDTO.builder().placementFormula(new PlacementFormulaDTO()).build());

    mockMvc
        .perform(
            post(BASE_URL, PUBLISHER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.groupedBy']",
                equalTo("must not be null")))
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups']",
                equalTo("must not be empty")));
  }

  @Test
  void shouldReturnBadRequestStatusWhenCreatingRuleWithRuleFormulaWithEmptyFormulaGroups()
      throws Exception {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    builder.ruleFormula(
        RuleFormulaDTO.builder().placementFormula(new PlacementFormulaDTO(OR, null)).build());

    mockMvc
        .perform(
            post(BASE_URL, PUBLISHER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups']",
                equalTo("must not be empty")));
  }

  @Test
  void shouldReturnBadRequestStatusWhenCreatingRuleWithRuleFormulaWithEmptyFormulaRules()
      throws Exception {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    builder.ruleFormula(
        RuleFormulaDTO.builder()
            .placementFormula(new PlacementFormulaDTO(OR, List.of(new FormulaGroupDTO(null))))
            .build());

    mockMvc
        .perform(
            post(BASE_URL, PUBLISHER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups[0].formulaRules']",
                equalTo("must not be empty")));
  }

  @Test
  void shouldReturnBadRequestStatusWhenCreatingRuleWithRuleFormulaWithFormulaRuleValidationErrors()
      throws Exception {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    FormulaRuleDTO formulaRule = new FormulaRuleDTO(null, null, null, null);
    FormulaGroupDTO group1 = new FormulaGroupDTO(List.of(formulaRule));
    FormulaGroupDTO group2 = new FormulaGroupDTO(null);
    builder.ruleFormula(
        RuleFormulaDTO.builder()
            .placementFormula(new PlacementFormulaDTO(OR, List.of(group1, group2)))
            .build());

    mockMvc
        .perform(
            post(BASE_URL, PUBLISHER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups[0].formulaRules[0].attribute']",
                equalTo("must not be null")))
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups[0].formulaRules[0].operator']",
                equalTo("must not be null")))
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups[0].formulaRules[0].ruleData']",
                equalTo("may not be empty")))
        .andExpect(
            jsonPath(
                "$['fieldErrors']['ruleFormula.placementFormula.formulaGroups[1].formulaRules']",
                equalTo("must not be empty")));
  }

  @Test
  void shouldCreateRuleWithValidRuleFormula() throws Exception {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    FormulaRuleDTO formulaRule = new FormulaRuleDTO(INVENTORY_ATTRIBUTE, EQUALS, "dummy11", 11L);
    FormulaGroupDTO group = new FormulaGroupDTO(List.of(formulaRule));
    builder.ruleFormula(
        RuleFormulaDTO.builder()
            .placementFormula(new PlacementFormulaDTO(OR, List.of(group)))
            .build());

    mockMvc
        .perform(
            post(BASE_URL, PUBLISHER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnBadRequestStatusWhenRuleIdDoesntMatch() throws Exception {
    SellerRuleDTOBuilder builder = createRuleForUpdate();

    mockMvc
        .perform(
            put(BASE_URL + "/{rulePid}", PUBLISHER_PID, 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(builder.build())))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$['errorMessage']", equalTo("Request parameter and body doesn't match.")));
  }

  @Test
  void shouldUpdateRule() throws Exception {
    SellerRuleDTOBuilder builder = createRuleForUpdate();

    SellerRuleDTO sellerRuleDto = builder.build();

    given(sellerRuleService.update(eq(PUBLISHER_PID), any(SellerRuleDTO.class)))
        .willAnswer(a -> a.getArguments()[1]);
    given(sellerRuleService.findByPidAndSellerPid(PUBLISHER_PID, 1L)).willReturn(sellerRuleDto);

    mockMvc
        .perform(
            put(BASE_URL + "/{rulePid}", PUBLISHER_PID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sellerRuleDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.intendedActions", hasSize(1)))
        .andExpect(jsonPath("$.targets", hasSize(1)))
        .andExpect(jsonPath("$.status", equalTo("ACTIVE")));
  }

  @Test
  void shouldReturnNotFoundStatusWhenUpdatingNonexistentRule() throws Exception {
    SellerRuleDTOBuilder builder = createRuleForUpdate();
    SellerRuleDTO sellerRuleDto = builder.build();

    given(sellerRuleService.update(eq(PUBLISHER_PID), any()))
        .willThrow(new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND));

    mockMvc
        .perform(
            put(BASE_URL + "/{rulePid}", PUBLISHER_PID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sellerRuleDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFetchRulesByQueryFieldWithNoResult() throws Exception {
    Long sellerPid = 1L;
    int pageIndex = 0;
    int pageSize = 4;
    String sortParam = "pid,DESC";
    // there are no rules with this type
    String queryField = "{type=BRAND_PROTECTION}";

    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(pageIndex, pageSize, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(List.of(), pageRequest, 0);

    when(sellerRuleService.findBySellerPidAndOtherCriteria(
            eq(sellerPid), any(SellerRuleQueryFieldParameter.class), eq(pageRequest)))
        .thenReturn(dtoPage);

    mockMvc
        .perform(
            get(
                BASE_URL + "?page={pageIndex}&size={pageSize}&sort={sortParam}&qf={queryField}",
                sellerPid,
                pageIndex,
                pageSize,
                sortParam,
                queryField))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)))
        .andExpect(jsonPath("$.totalPages", is(0)))
        .andExpect(jsonPath("$.size", is(4)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)));
  }

  @Test
  void shouldReturnBadRequestStatusWhenFetchingRulesUsingInvalidQueryParams() throws Exception {
    Long sellerPid = 1L;
    Integer pageIndex = 0;
    Integer pageSize = 1;
    String sortParam = "pid,DESC";
    // invalid type used to fail validation
    String queryField = "{type=BID_PROTECTION}";

    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(SellerRuleQueryParams.class), any());

    mockMvc
        .perform(
            get(
                BASE_URL + "?page={pageIndex}&size={pageSize}&sort={sortParam}&qf={queryField}",
                sellerPid,
                pageIndex,
                pageSize,
                sortParam,
                queryField))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldFetchRulesByQueryField() throws Exception {
    Long sellerPid = 1L;
    int pageIndex = 0;
    int pageSize = 3;
    String sortParam = "pid,DESC";
    String queryField = "{type=BRAND_PROTECTION}";

    List<SellerRuleDTO> rules =
        List.of(createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(pageIndex, pageSize, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, rules.size());

    when(sellerRuleService.findBySellerPidAndOtherCriteria(
            eq(sellerPid), any(SellerRuleQueryFieldParameter.class), eq(pageRequest)))
        .thenReturn(dtoPage);

    mockMvc
        .perform(
            get(
                BASE_URL + "?page={pageIndex}&size={pageSize}&sort={sortParam}&qf={queryField}",
                sellerPid,
                pageIndex,
                pageSize,
                sortParam,
                queryField))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(3)))
        .andExpect(jsonPath("$.pageable.pageSize", is(3)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)))
        .andExpect(jsonPath("$.content[*].type").value(containsInAnyOrder("BRAND_PROTECTION")))
        .andExpect(jsonPath("$.content[*].status").value(containsInAnyOrder("ACTIVE")));
  }

  @Test
  void shouldFetchRulesWithMissingQueryField() throws Exception {
    Long sellerPid = 1L;
    int pageIndex = 0;
    int pageSize = 3;
    String sortParam = "pid,DESC";

    List<SellerRuleDTO> rules =
        List.of(createRule(RandomUtils.nextLong(), RuleType.BRAND_PROTECTION));

    Sort sort = Sort.by(Sort.Direction.DESC, "pid");
    Pageable pageRequest = PageRequest.of(pageIndex, pageSize, sort);
    Page<SellerRuleDTO> dtoPage = new PageImpl<>(rules, pageRequest, rules.size());

    when(sellerRuleService.findBySellerPidAndOtherCriteria(
            eq(sellerPid), any(SellerRuleQueryFieldParameter.class), eq(pageRequest)))
        .thenReturn(dtoPage);

    mockMvc
        .perform(
            get(
                BASE_URL + "?page={pageIndex}&size={pageSize}&sort={sortParam}",
                sellerPid,
                pageIndex,
                pageSize,
                sortParam))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.size", is(3)))
        .andExpect(jsonPath("$.pageable.pageSize", is(3)))
        .andExpect(jsonPath("$.sort.sorted", is(true)))
        .andExpect(jsonPath("$.sort.empty", is(false)))
        .andExpect(jsonPath("$.content[*].type").value(containsInAnyOrder("BRAND_PROTECTION")))
        .andExpect(jsonPath("$.content[*].status").value(containsInAnyOrder("ACTIVE")));
  }

  private SellerRuleDTOBuilder createRuleForCreate() {
    IntendedActionDTO intendedAction =
        createFilterRuleIntendedActionBuilder().pid(null).version(null).build();
    RuleTargetDTO target =
        createRuleTargetBuilder(null, MatchType.EXCLUDE_LIST).version(null).build();

    return createSellerRuleDtoBuilder()
        .pid(null)
        .version(null)
        .type(RuleType.BRAND_PROTECTION)
        .intendedActions(Set.of(intendedAction))
        .targets(Set.of(target));
  }

  private SellerRuleDTO createRule(long pid, RuleType type) {
    SellerRuleDTO.SellerRuleDTOBuilder builder =
        SellerRuleDTO.builder()
            .name(RandomStringUtils.randomAlphanumeric(10))
            .pid(pid)
            .version(1)
            .description(RandomStringUtils.randomAlphanumeric(44))
            .type(type)
            .status(com.nexage.admin.core.enums.Status.ACTIVE);

    return builder.build();
  }

  private SellerRuleDTOBuilder createRuleForUpdate() {
    SellerRuleDTOBuilder builder = createRuleForCreate();
    return builder.pid(1L).version(0).ownerCompanyPid(PUBLISHER_PID);
  }

  private String toJson(SellerRuleDTO sellerRuleDto) throws JsonProcessingException {
    return objectMapper.writeValueAsString(sellerRuleDto);
  }
}
