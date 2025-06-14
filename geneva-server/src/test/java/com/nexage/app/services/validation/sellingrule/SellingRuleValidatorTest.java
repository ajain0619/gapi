package com.nexage.app.services.validation.sellingrule;

import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateDeloyedToCorrectAssignments;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateDuplicateTarget;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateIntendedActionsAndActionType;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateMaxTargetCategoryLevel;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateRuleExistence;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateRulePidAndVersionAreNull;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateRulePidsForUpdateAreSame;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateTargetsAndIntendedActions;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateUpdateAnotherRuleIntendedActions;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateUpdateAnotherRuleTargets;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateVersionsAreSame;
import static com.nexage.app.web.support.TestObjectsFactory.createCompanyRule;
import static com.nexage.app.web.support.TestObjectsFactory.createFilterRuleIntendedActionBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createFilterRuleIntendedActionDto;
import static com.nexage.app.web.support.TestObjectsFactory.createFloorRuleIntendedActionDto;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleDeployedCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleDeployedPositionWithSiteOwnerCompanyPid;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleDeployedSite;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTargetDto;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDto;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDtoBuilder;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDtoWithOwnerCompanyPid;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRuleDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.RuleTargetCategory;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleActionType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SellingRuleValidatorTest {

  @BeforeEach
  void setUp() {
    RuleTargetValidatorHelper.clearValidators();
  }

  @Test
  void whenRuleDtoHasDuplicatedTargetsThanValidationFails() {
    HashSet<RuleTargetDTO> targetDtos =
        Sets.newHashSet(createRuleTargetDto(), createRuleTargetDto());
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validateDuplicateTarget(targetDtos));

    assertEquals(ServerErrorCodes.SERVER_RULE_DUPLICATE_TARGET_TYPE, exception.getErrorCode());
  }

  @Test
  void whenRuleDtoHasCorrectTargetsAndIntendedActionsValidationPasses() {
    SellerSeatRuleDTO sellerSeatRuleDto = createSellerSeatRuleDto();
    validateTargetsAndIntendedActions(sellerSeatRuleDto);
  }

  @Test
  void shouldBeBadRequestWhenNonAdSourceManagementRuleHasPlaylistRenderingCapabilityTarget() {
    var playlistRenderingCapability =
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.PLAYLIST_RENDERING_CAPABILITY)
            .data("mraid2.0")
            .build();
    var intendedAction =
        IntendedActionDTO.builder().actionType(RuleActionType.FLOOR).actionData("0.01").build();
    var rule =
        SellerRuleDTO.builder()
            .type(RuleType.BRAND_PROTECTION)
            .targets(Set.of(playlistRenderingCapability))
            .intendedActions(Set.of(intendedAction))
            .build();

    var ex =
        assertThrows(
            GenevaValidationException.class, () -> validateTargetsAndIntendedActions(rule));

    assertEquals(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET, ex.getErrorCode());
  }

  @Test
  void whenRuleDtoHasWrongIntendedActionValidationFails() {
    SellerSeatRuleDTO sellerSeatRuleDto = createSellerSeatRuleDto();
    IntendedActionDTO IntendedActionDTO = sellerSeatRuleDto.getIntendedActions().iterator().next();
    ReflectionTestUtils.setField(IntendedActionDTO, "actionType", RuleActionType.FLOOR);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateTargetsAndIntendedActions(sellerSeatRuleDto));

    assertEquals(
        ServerErrorCodes.SERVER_RULE_WRONG_COMBINATION_OF_RULE_TYPE_AND_RULE_INTENDED_ACTION,
        exception.getErrorCode());
  }

  @Test
  void whenRuleDtoHasPidSetValidationFails() {
    SellerSeatRuleDTO sellerSeatRuleDto = createSellerSeatRuleDto();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateRulePidAndVersionAreNull(sellerSeatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_RULE_PID_IS_NOT_NULL, exception.getErrorCode());
  }

  @Test
  void whenRuleDtoHasVersionSetValidationFails() {
    SellerSeatRuleDTO sellerSeatRuleDto = createSellerSeatRuleDto(null, 1L, 1);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateRulePidAndVersionAreNull(sellerSeatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_RULE_VERSION_IS_NOT_NULL, exception.getErrorCode());
  }

  @Test
  void whenRuleDtoDoesntHaveVersionAndPidSetValidationPasses() {
    SellerSeatRuleDTO sellerSeatRuleDto = createSellerSeatRuleDto(null, 1L, null, null);
    validateRulePidAndVersionAreNull(sellerSeatRuleDto);
  }

  @Test
  void whenRulePidsForUpdateAreSameValidationFails() {
    SellerRuleDTO sellerRuleDTO = createSellerRuleDto(2L);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateRulePidsForUpdateAreSame(sellerRuleDTO, 1L));

    assertEquals(ServerErrorCodes.SERVER_RULE_PIDS_ARENT_SAME, exception.getErrorCode());
  }

  @Test
  void whenRulePidsForUpdateAreSameValidationPasses() {
    validateRulePidsForUpdateAreSame(createSellerRuleDto(1L), 1L);
  }

  @Test
  void whenVersionAreSameValidationFails() {
    CompanyRule companyRule = createCompanyRule(2);
    SellerRuleDTO sellerRuleDTO = createSellerRuleDto(1);
    assertThrows(
        StaleStateException.class, () -> validateVersionsAreSame(companyRule, sellerRuleDTO));
  }

  @Test
  void whenVersionAreSameValidationPasses() {
    validateVersionsAreSame(createCompanyRule(1), createSellerRuleDto(1));
  }

  @Test
  void whenRuleDoesNotExistValidationFails() {
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validateRuleExistence(null));

    assertEquals(ServerErrorCodes.SERVER_RULE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void whenTestRuleExistsValidationPasses() {
    validateRuleExistence(createCompanyRule());
  }

  @Test
  void whenDeloyedToCorrectAssignmentsValidationFailsDueToOwnerCompanyPidsNotSame() {
    CompanyRule companyRule = createCompanyRule(2L, Set.of(), Set.of(), Set.of());
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoWithOwnerCompanyPid(1L);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateDeloyedToCorrectAssignments(sellerRuleDTO, companyRule));

    assertEquals(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void whenDeloyedToCorrectAssignmentsValidationFailsDueToDeployedCompanyPidNotMatch() {
    Set<RuleDeployedCompany> deployedCompanies = ImmutableSet.of(createRuleDeployedCompany(2L));

    CompanyRule companyRule = createCompanyRule(1L, deployedCompanies, Set.of(), Set.of());
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoWithOwnerCompanyPid(1L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateDeloyedToCorrectAssignments(sellerRuleDTO, companyRule));

    assertEquals(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void whenDeloyedToCorrectAssignmentsValidationFailsDueToDeployedSitePidNotMatch() {
    Set<RuleDeployedSite> deployedSites = ImmutableSet.of(createRuleDeployedSite(2L));

    CompanyRule companyRule = createCompanyRule(1L, Set.of(), deployedSites, Set.of());
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoWithOwnerCompanyPid(1L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateDeloyedToCorrectAssignments(sellerRuleDTO, companyRule));

    assertEquals(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void whenDeloyedToCorrectAssignmentsValidationFailsDueToDeployedPositionPidNotMatch() {
    Set<RuleDeployedPosition> deployedPositions =
        ImmutableSet.of(createRuleDeployedPositionWithSiteOwnerCompanyPid(2L));

    CompanyRule companyRule = createCompanyRule(1L, Set.of(), Set.of(), deployedPositions);
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoWithOwnerCompanyPid(1L);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateDeloyedToCorrectAssignments(sellerRuleDTO, companyRule));

    assertEquals(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void whenDeloyedToCorrectAssignmentsValidationPasses() {
    CompanyRule companyRule = createCompanyRule(1L, Set.of(), Set.of(), Set.of());
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoWithOwnerCompanyPid(1L);
    validateDeloyedToCorrectAssignments(sellerRuleDTO, companyRule);
  }

  @Test
  void shouldFailValidationWhenUpdatingAnotherRuleIntendedActions() {
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoBuilder().build();
    CompanyRule companyRule = RuleMapper.MAPPER.map(sellerRuleDTO);
    companyRule
        .getRuleIntendedActions()
        .iterator()
        .next()
        .setPid(sellerRuleDTO.getIntendedActions().iterator().next().getPid() + 1L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateUpdateAnotherRuleIntendedActions(companyRule, sellerRuleDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INTENDED_ACTION_FROM_ANOTHER_RULE, exception.getErrorCode());
  }

  @Test
  void shouldPassValidationWhenUpdatingSameRuleIntendedActions() {
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoBuilder().build();
    CompanyRule companyRule = RuleMapper.MAPPER.map(sellerRuleDTO);

    validateUpdateAnotherRuleIntendedActions(companyRule, sellerRuleDTO);
  }

  @Test
  void shouldFailValidationWhenUpdatingAnotherRuleTargets() {
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoBuilder().build();
    CompanyRule companyRule = RuleMapper.MAPPER.map(sellerRuleDTO);
    companyRule
        .getRuleTargets()
        .iterator()
        .next()
        .setPid(sellerRuleDTO.getTargets().iterator().next().getPid() + 1L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateUpdateAnotherRuleTargets(companyRule, sellerRuleDTO));
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_FROM_ANOTHER_RULE, exception.getErrorCode());
  }

  @Test
  void shouldPassValidationWhenUpdatingSameRuleTargets() {
    SellerRuleDTO sellerRuleDTO = createSellerRuleDtoBuilder().build();
    CompanyRule companyRule = RuleMapper.MAPPER.map(sellerRuleDTO);

    validateUpdateAnotherRuleTargets(companyRule, sellerRuleDTO);
  }

  @Test
  void shouldPassValidationWhenIntendedActionTypeCorrect() {
    IntendedActionDTO actionDTO = createFilterRuleIntendedActionDto();

    assertDoesNotThrow(
        () -> validateIntendedActionsAndActionType(Set.of(actionDTO), RuleActionType.FILTER));
  }

  @Test
  void shouldThrowExceptionWhenMultipleIntendedActions() {
    Set<IntendedActionDTO> intendedActions =
        Set.of(createFilterRuleIntendedActionDto(), createFloorRuleIntendedActionDto());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateIntendedActionsAndActionType(intendedActions, RuleActionType.FILTER));
    assertEquals(ServerErrorCodes.SERVER_MORE_THAN_ONE_INTENDED_ACTION, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenWrongIntendedActionType() {
    Set<IntendedActionDTO> intendedActions = Set.of(createFloorRuleIntendedActionDto());
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateIntendedActionsAndActionType(intendedActions, RuleActionType.FILTER));
    assertEquals(
        ServerErrorCodes.SERVER_RULE_WRONG_COMBINATION_OF_RULE_TYPE_AND_RULE_INTENDED_ACTION,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenWrongIntendedActionData() {
    IntendedActionDTO actionDTO = createFilterRuleIntendedActionBuilder().actionData("0.5").build();
    Set<IntendedActionDTO> intendedActions = Set.of(actionDTO);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validateIntendedActionsAndActionType(intendedActions, RuleActionType.FILTER));
    assertEquals(ServerErrorCodes.SERVER_ACTION_DATA_INVALID_FILTER_TYPE, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenTargetCategoryLevelIsHigherThanAllowed() {
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validateMaxTargetCategoryLevel(RuleTargetCategory.BID, RuleTargetCategory.SUPPLY));
    assertEquals(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET, exception.getErrorCode());
  }
}
