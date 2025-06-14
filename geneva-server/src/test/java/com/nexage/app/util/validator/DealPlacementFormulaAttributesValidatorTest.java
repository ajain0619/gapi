package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.repository.DealInventoryRepository;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.deals.DealPlacementFormulaAttributesValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DealPlacementFormulaAttributesValidatorTest {

  @Mock private DealInventoryRepository dealInventoryRepository;

  @InjectMocks
  private DealPlacementFormulaAttributesValidator dealPlacementFormulaAttributesValidator;

  @Test
  void shouldNotThrowWhenPlacementFormulaIsNull() {
    assertDoesNotThrow(
        () -> dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(null));
  }

  @Test
  void shouldThrowWhenPlacementFormulaWithoutAttribute() {
    // given
    String ruleData = "{\"pid\":3,\"fileName\":\"domain.csv\",\"fileType\":\"DOMAIN\"}";
    PlacementFormulaDTO placementFormulaDTO =
        createPlacementFormulaDTO(null, FormulaOperatorDTO.EQUALS, ruleData);

    // when
    GenevaValidationException e =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
                    placementFormulaDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_ATTRIBUTE_DATA, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenPlacementFormulaWithoutOperator() {
    // given
    String ruleData = "{\"pid\":3,\"fileName\":\"domain.csv\",\"fileType\":\"DOMAIN\"}";
    PlacementFormulaDTO placementFormulaDTO =
        createPlacementFormulaDTO(FormulaAttributeDTO.DOMAIN, null, ruleData);

    // when
    GenevaValidationException e =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
                    placementFormulaDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_ATTRIBUTE_DATA, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenPlacementFormulaWithoutRule() {
    // given
    PlacementFormulaDTO placementFormulaDTO =
        createPlacementFormulaDTO(FormulaAttributeDTO.DOMAIN, FormulaOperatorDTO.EQUALS, null);

    // when
    GenevaValidationException e =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
                    placementFormulaDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_ATTRIBUTE_DATA, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenSameAttributeDomainHasDifferentOperators() {
    // given
    String domainRuleDataEquals =
        "{\"pid\":1,\"fileName\":\"domainEquals.csv\",\"fileType\":\"DOMAIN\"}";
    String domainRuleDataNotEquals =
        "{\"pid\":2,\"fileName\":\"domainNotEquals.csv\",\"fileType\":\"DOMAIN\"}";

    FormulaRuleDTO domainRuleEquals = new FormulaRuleDTO();
    domainRuleEquals.setAttribute(FormulaAttributeDTO.DOMAIN);
    domainRuleEquals.setOperator(FormulaOperatorDTO.EQUALS);
    domainRuleEquals.setRuleData(domainRuleDataEquals);

    FormulaRuleDTO domainRuleNotEquals = new FormulaRuleDTO();
    domainRuleNotEquals.setAttribute(FormulaAttributeDTO.DOMAIN);
    domainRuleNotEquals.setOperator(FormulaOperatorDTO.NOT_EQUALS);
    domainRuleNotEquals.setRuleData(domainRuleDataNotEquals);

    FormulaGroupDTO formulaGroupDTO =
        new FormulaGroupDTO(List.of(domainRuleEquals, domainRuleNotEquals));
    PlacementFormulaDTO placementFormulaDTO =
        new PlacementFormulaDTO(FormulaGroupingDTO.OR, List.of(formulaGroupDTO));

    given(dealInventoryRepository.existsByPidAndFileName(any(), any())).willReturn(true);

    // when + then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
                    placementFormulaDTO));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_LIST_EXISTS, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenSameAttributeAppAliasHasDifferentOperators() {
    // given
    String appAliasRuleDataEquals =
        "{\"pid\":1,\"fileName\":\"appAliasEquals.csv\",\"fileType\":\"APP_ALIAS\"}";
    String appAliasRuleDataNotEquals =
        "{\"pid\":2,\"fileName\":\"appAliasNotEquals.csv\",\"fileType\":\"APP_ALIAS\"}";

    FormulaRuleDTO appAliasRuleEquals = new FormulaRuleDTO();
    appAliasRuleEquals.setAttribute(FormulaAttributeDTO.APP_ALIAS);
    appAliasRuleEquals.setOperator(FormulaOperatorDTO.EQUALS);
    appAliasRuleEquals.setRuleData(appAliasRuleDataEquals);

    FormulaRuleDTO appAliasRuleNotEquals = new FormulaRuleDTO();
    appAliasRuleNotEquals.setAttribute(FormulaAttributeDTO.APP_ALIAS);
    appAliasRuleNotEquals.setOperator(FormulaOperatorDTO.NOT_EQUALS);
    appAliasRuleNotEquals.setRuleData(appAliasRuleDataNotEquals);

    FormulaGroupDTO formulaGroupDTO =
        new FormulaGroupDTO(List.of(appAliasRuleEquals, appAliasRuleNotEquals));
    PlacementFormulaDTO placementFormulaDTO =
        new PlacementFormulaDTO(FormulaGroupingDTO.OR, List.of(formulaGroupDTO));

    given(dealInventoryRepository.existsByPidAndFileName(any(), any())).willReturn(true);

    // when + then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
                    placementFormulaDTO));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_LIST_EXISTS, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenSameAttributeAppBundleHasDifferentOperators() {
    // given
    String appBundleRuleDataEquals =
        "{\"pid\":1,\"fileName\":\"appBundleEquals.csv\",\"fileType\":\"APP_BUNDLE\"}";
    String appAliasRuleDataNotEquals =
        "{\"pid\":2,\"fileName\":\"appBundleNotEquals.csv\",\"fileType\":\"APP_BUNDLE\"}";

    FormulaRuleDTO appBundleRuleEquals = new FormulaRuleDTO();
    appBundleRuleEquals.setAttribute(FormulaAttributeDTO.APP_BUNDLE);
    appBundleRuleEquals.setOperator(FormulaOperatorDTO.EQUALS);
    appBundleRuleEquals.setRuleData(appBundleRuleDataEquals);

    FormulaRuleDTO appBundleRuleNotEquals = new FormulaRuleDTO();
    appBundleRuleNotEquals.setAttribute(FormulaAttributeDTO.APP_BUNDLE);
    appBundleRuleNotEquals.setOperator(FormulaOperatorDTO.NOT_EQUALS);
    appBundleRuleNotEquals.setRuleData(appAliasRuleDataNotEquals);

    FormulaGroupDTO formulaGroupDTO =
        new FormulaGroupDTO(List.of(appBundleRuleEquals, appBundleRuleNotEquals));
    PlacementFormulaDTO placementFormulaDTO =
        new PlacementFormulaDTO(FormulaGroupingDTO.OR, List.of(formulaGroupDTO));

    given(dealInventoryRepository.existsByPidAndFileName(any(), any())).willReturn(true);

    // when + then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
                    placementFormulaDTO));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_LIST_EXISTS, exception.getErrorCode());
  }

  private PlacementFormulaDTO createPlacementFormulaDTO(
      FormulaAttributeDTO formulaAttributeDTO,
      FormulaOperatorDTO formulaOperatorDTO,
      String ruleData) {

    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setAttribute(formulaAttributeDTO);
    formulaRuleDTO.setOperator(formulaOperatorDTO);
    formulaRuleDTO.setRuleData(ruleData);

    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO(List.of(formulaRuleDTO));
    return new PlacementFormulaDTO(FormulaGroupingDTO.OR, List.of(formulaGroupDTO));
  }
}
