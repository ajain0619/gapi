package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.BaseTarget;
import com.nexage.admin.core.model.BaseTarget.TargetType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.DirectDeal.DealStatus;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.RuleTargetRepository;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.validation.DealValidator;
import com.nexage.app.util.validator.deals.DealPlacementFormulaAttributesValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DealValidatorTest {

  private static final double MAX_DEAL_FLOOR = 1000.0;
  private static final Long TEST_DEAL_PID = 11L;
  private static final String TEST_DEAL_ID = "test-deal-id";

  @Mock private BeanValidationService beanValidationService;
  @Mock private CompanyRuleRepository companyRuleRepository;
  @Mock private ZeroCostDealValidator zeroCostDealValidator;
  @Mock private RuleTargetRepository ruleTargetRepository;
  @Mock private DealPlacementFormulaAttributesValidator dealPlacementFormulaAttributesValidator;
  @InjectMocks private DealValidator dealValidator;

  @Test
  void shouldPassDealTargetValidationWhenDealTargetIsValid() {
    // given
    DealTargetDTO dealTargetDTO =
        new DealTargetDTO.Builder()
            .setTargetType(TargetType.SDK_VERSION)
            .setRuleType(BaseTarget.RuleType.POSITIVE)
            .setData("aaa")
            .build();

    // when/then
    assertDoesNotThrow(() -> dealValidator.validateTarget(dealTargetDTO));
  }

  @ParameterizedTest(name = "{index}: {0}")
  @MethodSource("getInvalidDealTargetDtos")
  void shouldFailDealTargetValidationForInvalidDealTargets(DealTargetDTO dealTargetDTO) {
    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateTarget(dealTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_MANDATORY_TARGET_FIELDS_MISSING, result.getErrorCode());
  }

  @Test
  void shouldFailValidationWhenRuleStatusAndDealStatusAreNotTheSame() {
    // given
    var rulePid = 123L;
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .rules(Set.of(new DealRuleDTO.Builder().setRulePid(rulePid).build()))
            .status(DealStatus.Active)
            .build();
    CompanyRule rule = mock(CompanyRule.class);
    given(rule.getStatus()).willReturn(Status.INACTIVE);
    given(companyRuleRepository.findById(rulePid)).willReturn(Optional.of(rule));

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateAndFixDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_DEAL_AND_RULE_STATUS_NOT_THE_SAME, result.getErrorCode());
  }

  @Test
  void shouldFailValidationWhenDtoHasFloorHigherThanMaxFloor() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .floor(new BigDecimal(MAX_DEAL_FLOOR).add(new BigDecimal(1)))
            .build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateAndFixDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID, result.getErrorCode());
  }

  @Test
  void shouldFailValidationWhenDtoHasMoreThanOneRule() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .rules(
                Set.of(
                    new DealRuleDTO.Builder().setRulePid(1L).build(),
                    new DealRuleDTO.Builder().setRulePid(2L).build()))
            .build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateAndFixDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_DEAL_HAS_MORE_THAN_ONE_RULE, result.getErrorCode());
  }

  @Test
  void shouldFailValidationForFormulaWhenDtoHasFormulaAndAutoUpdateIsNull() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .autoUpdate(null)
            .placementFormula(new PlacementFormulaDTO())
            .build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateForFormula(directDealDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_AUTO_UPDATE_FLAG_CANNOT_BE_NULL, result.getErrorCode());
  }

  @Test
  void shouldPassValidationForFormulaWhenDtoHasNoFormulaAndAutoUpdateIsNull() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().autoUpdate(null).placementFormula(null).build();

    // when/then
    assertDoesNotThrow(() -> dealValidator.validateForFormula(directDealDTO));
  }

  @Test
  void shouldFailValidationForFormulaWhenDtoHasNoFormulaAndAutoUpdateIsTrue() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().autoUpdate(true).placementFormula(null).build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateForFormula(directDealDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_AUTO_UPDATE_FLAG_CANNOT_BE_USED_WITHOUT_FORMULA,
        result.getErrorCode());
  }

  @Test
  void
      shouldPassValidationForFormulaWhenDtoHasFormulaAndPositionSellerAndSiteAssignmentsSetToNull() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .positions(null)
            .sites(null)
            .sellers(null)
            .autoUpdate(true)
            .placementFormula(new PlacementFormulaDTO())
            .build();

    // when/then
    assertDoesNotThrow(() -> dealValidator.validateForFormula(directDealDTO));
  }

  @Test
  void shouldFailValidationForFormulaWhenDtoHasFormulaAndPositionAssignments() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .positions(List.of(new DealPositionDTO()))
            .autoUpdate(true)
            .placementFormula(new PlacementFormulaDTO())
            .build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateForFormula(directDealDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_CANNOT_USE_FORMULA_AND_EXPLICIT_ASSIGNMENT,
        result.getErrorCode());
  }

  @Test
  void shouldFailValidationForFormulaWhenDtoHasFormulaAndSiteAssignments() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .sites(List.of(new DealSiteDTO()))
            .autoUpdate(true)
            .placementFormula(new PlacementFormulaDTO())
            .build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateForFormula(directDealDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_CANNOT_USE_FORMULA_AND_EXPLICIT_ASSIGNMENT,
        result.getErrorCode());
  }

  @Test
  void shouldFailValidationForFormulaWhenDtoHasFormulaAndPublisherAssignments() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .sellers(List.of(new DealPublisherDTO()))
            .autoUpdate(true)
            .placementFormula(new PlacementFormulaDTO())
            .build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateForFormula(directDealDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_CANNOT_USE_FORMULA_AND_EXPLICIT_ASSIGNMENT,
        result.getErrorCode());
  }

  @Test
  void shouldFailValidationWhenNegativeDealFloor() {
    // given
    DirectDealDTO directDealDTO = DirectDealDTO.builder().floor(BigDecimal.valueOf(-0.01)).build();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateAndFixDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID, result.getErrorCode());
  }

  @Test
  void shouldPassValidationWhenSellersAndSitesAndPlacementsInAllowListForZeroCostDeals() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .guaranteedImpressionGoal(10000L)
            .dailyImpressionCap(10000L)
            .start(new Date(0))
            .stop(new Date(1))
            .priorityType(DealPriorityType.OPEN)
            .autoUpdate(null)
            .rules(Set.of(new DealRuleDTO()))
            .build();

    // when
    dealValidator.validateAndFixDeal(directDealDTO);

    // then
    verify(zeroCostDealValidator).validateZeroCostDeals(directDealDTO);
  }

  @Test
  void shouldFailValidationWhenZeroCostDealValidatorThrows() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .guaranteedImpressionGoal(10000L)
            .dailyImpressionCap(10000L)
            .start(new Date(0))
            .stop(new Date(1))
            .priorityType(DealPriorityType.OPEN)
            .build();
    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED))
        .when(zeroCostDealValidator)
        .validateZeroCostDeals(directDealDTO);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateAndFixDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, result.getErrorCode());
  }

  @Test
  void shouldPassValidationForDealRuleDetailsAndNoRules() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().dealId("deal1").rules(null).floor(null).priorityType(null).build();

    // when
    dealValidator.validateAndFixDeal(directDealDTO);
  }

  @Test
  void shouldPassValidationForValidateDealRuleDetailsAndValidKVPRule() {
    // given
    DealRuleDTO dealRule = new DealRuleDTO.Builder().setRulePid(33L).build();
    CompanyRule companyRule = new CompanyRule();
    companyRule.setStatus(Status.ACTIVE);
    companyRule.setRuleType(RuleType.DEAL);
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .placementFormula(null)
            .status(DirectDeal.DealStatus.Active)
            .dealId("deal1")
            .rules(Set.of(dealRule))
            .floor(null)
            .priorityType(null)
            .build();
    given(companyRuleRepository.findById(anyLong())).willReturn(Optional.of(companyRule));

    // when
    dealValidator.validateAndFixDeal(directDealDTO);
  }

  @Test
  void shouldFailValidationWhenS2SDealHasTarget() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .dealCategory(DealCategory.S2S_PLACEMENT_DEAL.asInt())
            .rules(Set.of(new DealRuleDTO.Builder().setRulePid(2L).build()))
            .build();

    given(ruleTargetRepository.hasRuleTarget(any())).willReturn(true);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> dealValidator.validateAndFixDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_DEAL_CATEGORY, result.getErrorCode());
  }

  @Test
  void shouldPassValidationWhenS2SDealHasNoTarget() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .dealCategory(DealCategory.S2S_PLACEMENT_DEAL.asInt())
            .rules(Set.of(new DealRuleDTO.Builder().setRulePid(2L).build()))
            .build();

    given(ruleTargetRepository.hasRuleTarget(any())).willReturn(false);

    // then
    assertDoesNotThrow(() -> dealValidator.validateAndFixDeal(directDealDTO));
  }

  @Test
  void shouldFailValidationForExternalDealWhenDealHasNoRule() {
    // given
    DirectDeal directDeal = new DirectDeal();
    directDeal.setStop(new Date(1));
    directDeal.setStart(new Date(0));
    directDeal.setAutoUpdate(null);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> dealValidator.validateTargetsAllowedForExternalDeal(directDeal));

    // then
    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, result.getErrorCode());
  }

  @Test
  void shouldFailValidationWhenExternalDealHasTargetOtherThanBuyerSeats() {
    // given
    DirectDeal directDeal = new DirectDeal();
    directDeal.setStop(new Date(1));
    directDeal.setStart(new Date(0));
    directDeal.setAutoUpdate(null);
    directDeal.setRules(List.of(new DealRule()));
    given(ruleTargetRepository.hasRuleTargetOtherThanProvided(any(), any())).willReturn(true);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> dealValidator.validateTargetsAllowedForExternalDeal(directDeal));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_EXTERNAL_DEAL_TARGET_DATA, result.getErrorCode());
  }

  @Test
  void shouldPassValidationWhenExternalDealHasTargetOtherThanBuyerSeat() {
    // given
    DirectDeal directDeal = new DirectDeal();
    directDeal.setStop(new Date(1));
    directDeal.setStart(new Date(0));
    directDeal.setAutoUpdate(null);
    directDeal.setRules(List.of(new DealRule()));
    given(ruleTargetRepository.hasRuleTargetOtherThanProvided(any(), any())).willReturn(false);

    // then
    assertDoesNotThrow(() -> dealValidator.validateTargetsAllowedForExternalDeal(directDeal));
  }

  private DirectDealDTO getDirectDeal() {
    return DirectDealDTO.builder()
        .pid(TEST_DEAL_PID)
        .dealId(TEST_DEAL_ID)
        .stop(new Date(2))
        .start(new Date(1))
        .rules(Set.of(new DealRuleDTO()))
        .build();
  }

  private static Stream<Arguments> getInvalidDealTargetDtos() {
    return Stream.of(
        Arguments.of(
            Named.of(
                "Rule type blank",
                new DealTargetDTO.Builder()
                    .setTargetType(TargetType.SDK_VERSION)
                    .setRuleType(BaseTarget.RuleType.POSITIVE)
                    .setData(" ")
                    .build())),
        Arguments.of(
            Named.of(
                "Data null",
                new DealTargetDTO.Builder()
                    .setTargetType(TargetType.SDK_VERSION)
                    .setRuleType(BaseTarget.RuleType.POSITIVE)
                    .setData(null)
                    .build())),
        Arguments.of(
            Named.of(
                "Rule type null",
                new DealTargetDTO.Builder()
                    .setTargetType(TargetType.SDK_VERSION)
                    .setRuleType(null)
                    .setData("aaa")
                    .build())),
        Arguments.of(
            Named.of(
                "Target type null",
                new DealTargetDTO.Builder()
                    .setTargetType(null)
                    .setRuleType(BaseTarget.RuleType.POSITIVE)
                    .setData("aaa")
                    .build())));
  }
}
