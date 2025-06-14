package com.nexage.app.services.impl;

import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.DOMAIN;
import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.INVENTORY_ATTRIBUTE;
import static com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO.OR;
import static com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO.EQUALS;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTarget;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.app.dto.deals.FormulaAssignedInventoryDTO;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.deal.impl.FormulaAssignedInventoryDTOServiceImpl;
import com.nexage.app.services.support.DealServiceSupport;
import com.nexage.app.services.validation.SellerDealValidator;
import com.nexage.app.services.validation.sellingrule.ZeroCostDealValidator;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.validator.deals.DealPlacementFormulaAttributesValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FormulaAssignedInventoryDTOServiceImplTest {

  private static final Long DEAL_PID = 1L;
  private static final Long SELLER_ID = 1L;

  @Mock private CustomObjectMapper objectMapper;
  @Mock private DirectDealRepository dealRepository;
  @Mock private DealPositionRepository dealPositionRepository;
  @Mock private CompanyRuleRepository companyRuleRepository;
  @Mock private SellerDealValidator sellerDealValidator;
  @Mock private ZeroCostDealValidator zeroCostDealValidator;
  @Mock private DealServiceSupport dealServiceSupport;
  @Mock private DealPlacementFormulaAttributesValidator dealPlacementFormulaAttributesValidator;

  private FormulaAssignedInventoryDTOServiceImpl formulaAssignedInventoryDTOService;
  private FormulaAssignedInventoryDTO exampleFormula = buildFormula();

  @BeforeEach
  public void setup() {
    formulaAssignedInventoryDTOService =
        new FormulaAssignedInventoryDTOServiceImpl(
            objectMapper,
            dealRepository,
            companyRuleRepository,
            zeroCostDealValidator,
            sellerDealValidator,
            dealServiceSupport,
            dealPlacementFormulaAttributesValidator);
  }

  @Test
  void updateFormulaInventorySuccess() throws Exception {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    var savedDeal = mock(DirectDeal.class);
    when(dealRepository.save(any(DirectDeal.class))).thenReturn(savedDeal);
    var formula = buildFormula();
    var formulaString = new ObjectMapper().writeValueAsString(formula.getPlacementFormula());
    when(objectMapper.writeValueAsString(formula.getPlacementFormula())).thenReturn(formulaString);

    formulaAssignedInventoryDTOService.updateAssignedInventory(DEAL_PID, formula);
    verify(dealRepository).save(any(DirectDeal.class));
    verifyNoInteractions(dealPositionRepository);
  }

  @Test
  void shouldUpdateFormulaInventoryHavingDomainAttributeSuccessfully() throws Exception {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    when(dealRepository.save(any(DirectDeal.class))).thenReturn(new DirectDeal());
    var formula = buildFormulaWithDomainAttribute();
    var formulaString = new ObjectMapper().writeValueAsString(formula.getPlacementFormula());
    when(objectMapper.writeValueAsString(formula.getPlacementFormula())).thenReturn(formulaString);

    formulaAssignedInventoryDTOService.updateAssignedInventory(DEAL_PID, formula);
    verify(dealRepository).save(any(DirectDeal.class));
    verifyNoInteractions(dealPositionRepository);
  }

  @Test
  void updateFormulaEmptyFormulaBasicValidationSuccessThenFailDueToBadData() throws Exception {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    when(dealRepository.save(any(DirectDeal.class)))
        .thenThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_RULE_PLACEMENT_FORMULA_BAD_DATA));
    var formula = buildEmptyFormula();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> formulaAssignedInventoryDTOService.updateAssignedInventory(DEAL_PID, formula));
    assertEquals(ServerErrorCodes.SERVER_RULE_PLACEMENT_FORMULA_BAD_DATA, exception.getErrorCode());
  }

  @Test
  void updateFormulaDealNotFound() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(Optional.empty());

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                formulaAssignedInventoryDTOService.updateAssignedInventory(
                    DEAL_PID, exampleFormula));
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void updateFormulaDealValidBecauseNoRulesOnDeal() throws Exception {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDealNoFormula());
    when(dealRepository.save(any(DirectDeal.class)))
        .thenThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_RULE_PLACEMENT_FORMULA_BAD_DATA));
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                formulaAssignedInventoryDTOService.updateAssignedInventory(
                    DEAL_PID, exampleFormula));
    assertEquals(ServerErrorCodes.SERVER_RULE_PLACEMENT_FORMULA_BAD_DATA, exception.getErrorCode());
  }

  @Test
  void updateFormulaInvalidData() throws Exception {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    when(objectMapper.writeValueAsString(exampleFormula.getPlacementFormula()))
        .thenThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR));

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                formulaAssignedInventoryDTOService.updateAssignedInventory(
                    DEAL_PID, exampleFormula));
    assertEquals(
        ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR, exception.getErrorCode());
  }

  @Test
  void updateAssignedInventoryShouldPassWhenPlacementsInAllowListForZeroCostDeals()
      throws Exception {
    var directDeal = testDeal().orElseThrow();
    var formula = buildFormula();
    var formulaString = new ObjectMapper().writeValueAsString(formula.getPlacementFormula());

    directDeal.setPid(DEAL_PID);
    when(dealRepository.findById(DEAL_PID)).thenReturn(Optional.of(directDeal));
    when(dealRepository.save(any(DirectDeal.class))).thenReturn(directDeal);
    when(objectMapper.writeValueAsString(formula.getPlacementFormula())).thenReturn(formulaString);

    formulaAssignedInventoryDTOService.updateAssignedInventory(DEAL_PID, formula);
    verify(zeroCostDealValidator).validateZeroCostDeals(directDeal, formula);
  }

  @Test
  void updateAssignedInventoryShouldThrowWhenZeroCostDealValidatorThrows() throws Exception {
    var directDeal = testDeal().orElseThrow();
    var formula = buildFormula();

    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED))
        .when(zeroCostDealValidator)
        .validateZeroCostDeals(directDeal, formula);

    directDeal.setPid(DEAL_PID);
    when(dealRepository.findById(DEAL_PID)).thenReturn(Optional.of(directDeal));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> formulaAssignedInventoryDTOService.updateAssignedInventory(DEAL_PID, formula));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
    verify(zeroCostDealValidator).validateZeroCostDeals(directDeal, formula);
  }

  @Test
  void getFormulaSuccess() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    var out = formulaAssignedInventoryDTOService.getAssignedInventory(DEAL_PID);
    assertEquals(1, out.getContent().size());
    var formula = out.getContent().iterator().next();
    assertTrue(formula.getAutoUpdate());
    assertEquals(DEAL_PID, formula.getDealPid());
  }

  @Test
  void shouldThrowNotFoundWhenDealDoesNotExist() {
    // when
    when(dealRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> formulaAssignedInventoryDTOService.getAssignedInventory(DEAL_PID));
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdateSellerFormulaInventoryWithSuccess() throws Exception {

    FormulaAssignedInventoryListDTO result =
        formulaAssignedInventoryDTOService.updateAssignedInventoryForSeller(
            SELLER_ID,
            DEAL_PID,
            buildFormulaAssignedInventoryDTOList(setupUpdateFormulaInventory()));
    verify(dealRepository).save(any(DirectDeal.class));
    verifyNoInteractions(dealPositionRepository);
    assertEquals(
        "dummy11",
        result
            .getContent()
            .iterator()
            .next()
            .getPlacementFormula()
            .getFormulaGroups()
            .get(0)
            .getFormulaRules()
            .get(0)
            .getRuleData());
  }

  @Test
  void shouldFailSellerUpdateDueToInvalidSellerFormulaRule() throws Exception {
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA))
        .when(sellerDealValidator)
        .validateSellerFormulaRuleDTOs(any(), eq(SELLER_ID));
    var formulaAssignedInventoryListDTO = buildFormulaAssignedInventoryDTOList(buildFormula());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                formulaAssignedInventoryDTOService.updateAssignedInventoryForSeller(
                    SELLER_ID, DEAL_PID, formulaAssignedInventoryListDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA, exception.getErrorCode());
  }

  @Test
  void shouldFailSellerUpdateDueToEmptySellerFormulaRule() throws Exception {
    var formulaAssignedInventoryListDTO = new FormulaAssignedInventoryListDTO();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                formulaAssignedInventoryDTOService.updateAssignedInventoryForSeller(
                    SELLER_ID, DEAL_PID, formulaAssignedInventoryListDTO));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  private Optional<DirectDeal> testDeal() {
    var deal = new DirectDeal();
    deal.setPid(10L);
    deal.setPlacementFormula(
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"INVENTORY_ATTRIBUTE\",\"operator\":\"EQUALS\",\"ruleData\":\"dummy11\",\"attributePid\":11}]}]} ");
    deal.setAutoUpdate(true);
    return Optional.of(deal);
  }

  private Optional<DirectDeal> testDealNoFormula() {
    var deal = new DirectDeal();
    deal.setPid(10L);
    deal.setPlacementFormula(null);
    deal.setAutoUpdate(true);
    return Optional.of(deal);
  }

  private Optional<DirectDeal> testDealWithDealRuleNoFormula() {
    var deal = new DirectDeal();
    deal.setPid(10L);
    deal.setPlacementFormula(null);
    deal.setAutoUpdate(true);
    DealRule dealRule = new DealRule();
    dealRule.setDeal(deal);
    dealRule.setRulePid(23L);
    deal.getRules().add(dealRule);
    return Optional.of(deal);
  }

  private CompanyRule testNonKvpRule() {
    CompanyRule rule = new CompanyRule();
    rule.setPid(23L);
    rule.setRuleType(com.nexage.admin.core.enums.RuleType.DEAL);
    rule.setRuleTargets(Collections.singleton(createRuleTarget()));
    return rule;
  }

  private FormulaAssignedInventoryDTO buildFormula() {
    var formulaInventory = new FormulaAssignedInventoryDTO();
    formulaInventory.setAutoUpdate(true);
    FormulaRuleDTO rule1 = new FormulaRuleDTO(INVENTORY_ATTRIBUTE, EQUALS, "dummy11", 11L);
    FormulaGroupDTO group1 = new FormulaGroupDTO(List.of(rule1));
    PlacementFormulaDTO pf = new PlacementFormulaDTO(OR, List.of(group1));
    formulaInventory.setPlacementFormula(pf);
    return formulaInventory;
  }

  private FormulaAssignedInventoryDTO buildFormulaWithDomainAttribute() {
    var formulaInventory = new FormulaAssignedInventoryDTO();
    formulaInventory.setAutoUpdate(true);
    FormulaRuleDTO rule1 =
        new FormulaRuleDTO(
            DOMAIN,
            EQUALS,
            "{\"pid\":1,\"fileName\":\"domains.csv\",\"fileType\":\"DOMAIN\"}",
            11L);
    FormulaGroupDTO group1 = new FormulaGroupDTO(List.of(rule1));
    PlacementFormulaDTO pf = new PlacementFormulaDTO(OR, List.of(group1));
    formulaInventory.setPlacementFormula(pf);
    return formulaInventory;
  }

  private FormulaAssignedInventoryDTO buildEmptyFormula() {
    var formulaInventory = new FormulaAssignedInventoryDTO();
    formulaInventory.setAutoUpdate(true);
    formulaInventory.setPlacementFormula(null);
    return formulaInventory;
  }

  private List<RuleFormulaPositionView> matchingPositions() {
    var mockView1 = mock(RuleFormulaPositionView.class);
    var mockView2 = mock(RuleFormulaPositionView.class);

    return List.of(mockView1, mockView2);
  }

  private FormulaAssignedInventoryDTO setupUpdateFormulaInventory() throws Exception {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    var savedDeal = mock(DirectDeal.class);
    when(dealRepository.save(any(DirectDeal.class))).thenReturn(savedDeal);
    var formula = buildFormula();
    var formulaString = new ObjectMapper().writeValueAsString(formula.getPlacementFormula());
    when(objectMapper.writeValueAsString(formula.getPlacementFormula())).thenReturn(formulaString);

    return formula;
  }

  private FormulaAssignedInventoryListDTO buildFormulaAssignedInventoryDTOList(
      FormulaAssignedInventoryDTO formula) throws Exception {
    List<FormulaAssignedInventoryDTO> formulaListDTO = new ArrayList();
    formulaListDTO.add(formula);
    FormulaAssignedInventoryListDTO formulalist = new FormulaAssignedInventoryListDTO();
    formulalist.setContent(formulaListDTO);
    return formulalist;
  }

  private static Stream<Arguments> providePlacementFormulaStatusUpdateValues() {
    return Stream.of(
        Arguments.of(PlacementFormulaStatus.NEW, PlacementFormulaStatus.NEW),
        Arguments.of(PlacementFormulaStatus.UPDATE, PlacementFormulaStatus.UPDATE),
        Arguments.of(PlacementFormulaStatus.IN_QUEUE, PlacementFormulaStatus.IN_QUEUE),
        Arguments.of(PlacementFormulaStatus.IN_PROGRESS, PlacementFormulaStatus.UPDATE),
        Arguments.of(PlacementFormulaStatus.DONE, PlacementFormulaStatus.NEW),
        Arguments.of(PlacementFormulaStatus.ERROR, PlacementFormulaStatus.NEW));
  }
}
