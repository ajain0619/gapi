package com.nexage.app.services.validation.sellingrule;

import static com.google.common.collect.Sets.newHashSet;
import static com.nexage.app.web.support.TestObjectsFactory.createCompanyRule;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleFormula;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.RuleDeployedCompanyRepository;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleDeployedSiteRepository;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleMapper;
import com.nexage.app.security.LoginUserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class SellerRuleValidatorTest {

  private static final Long RULE_PID = 123L;
  private static final Long SELLER_PID = 456L;

  @Mock protected CompanyRuleRepository companyRuleRepository;
  @Mock private LoginUserContext userContext;
  @Mock private ObjectMapper objectMapper;
  @Mock private RuleDeployedCompanyRepository companyRepository;
  @Mock private RuleDeployedSiteRepository siteRepository;
  @Mock private RuleDeployedPositionRepository positionRepository;
  @InjectMocks private SellerRuleValidator validator;

  @Test
  void shouldFailWhenContainsBucketTargetAndNotNexageUser() {
    RuleTargetDTO bucketTarget = RuleTargetDTO.builder().targetType(RuleTargetType.BUCKET).build();
    SellerRuleDTO rule = SellerRuleDTO.builder().targets(newHashSet(bucketTarget)).build();
    given(userContext.isNexageUser()).willReturn(false);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCommonPartForCreateAndUpdate(SELLER_PID, rule));
    assertEquals(
        ServerErrorCodes.SERVER_RULE_WRONG_COMBINATION_OF_RULE_TYPE_AND_RULE_TARGET,
        exception.getErrorCode());
  }

  @Test
  void shouldFailWhenSellerPidDoesNotMatchRuleCompanyPidContainingBucketTarget() {
    RuleTargetDTO bucketTarget = RuleTargetDTO.builder().targetType(RuleTargetType.BUCKET).build();
    SellerRuleDTO rule = SellerRuleDTO.builder().targets(newHashSet(bucketTarget)).build();
    given(userContext.isNexageUser()).willReturn(true);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCommonPartForCreateAndUpdate(SELLER_PID, rule));
    assertEquals(ServerErrorCodes.SERVER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenSellerPidDoesNotMatchRuleCompanyPid() {
    SellerRuleDTO rule =
        SellerRuleDTO.builder().targets(Collections.emptySet()).ownerCompanyPid(999L).build();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCommonPartForCreateAndUpdate(SELLER_PID, rule));
    assertEquals(ServerErrorCodes.SERVER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenRuleWithBuyerSeatsTargetAndBuyerCompanyIsNull()
      throws JsonProcessingException {
    RuleTargetDTO buyerSeatTarget =
        RuleTargetDTO.builder().targetType(RuleTargetType.BUYER_SEATS).data("test").build();
    SellerRuleDTO rule =
        SellerRuleDTO.builder()
            .ownerCompanyPid(SELLER_PID)
            .targets(newHashSet(buyerSeatTarget))
            .build();
    AbstractBidderValidator.BidderSeat[] bidderSeats =
        new AbstractBidderValidator.BidderSeat[] {new AbstractBidderValidator.BidderSeat()};
    given(objectMapper.readValue(anyString(), eq(AbstractBidderValidator.BidderSeat[].class)))
        .willReturn(bidderSeats);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCommonPartForCreateAndUpdate(SELLER_PID, rule));
    assertEquals(
        ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenRuleContainsBothFormulaAndAssignments() {
    InventoryAssignmentsDTO assignments = createPublisherAssignments(SELLER_PID);
    RuleFormulaDTO formula = RuleFormulaDTO.builder().build();
    SellerRuleDTO rule =
        createSellerRuleDtoBuilder()
            .assignments(assignments)
            .ruleFormula(formula)
            .ownerCompanyPid(SELLER_PID)
            .build();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateCommonPartForCreateAndUpdate(SELLER_PID, rule));
    assertEquals(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void validateCommonSuccessScenario() {
    SellerRuleDTO rule = createSellerRuleDtoBuilder().ownerCompanyPid(SELLER_PID).build();
    validator.validateCommonPartForCreateAndUpdate(SELLER_PID, rule);
  }

  @Test
  void shouldFailWhenIsDeployedToWrongCompany() {
    long wrongSellerPid = SELLER_PID + 1;
    InventoryAssignmentsDTO assignments = createPublisherAssignments(wrongSellerPid);
    SellerRuleDTO ruleDTO =
        createSellerRuleDtoBuilder().assignments(assignments).ownerCompanyPid(SELLER_PID).build();
    CompanyRule rule = RuleMapper.MAPPER.map(ruleDTO);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateDeployedTargetsAndUpdateRule(rule));
    assertEquals(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET, exception.getErrorCode());
  }

  @Test
  void validateDeployedTargetsSuccessScenario() {
    InventoryAssignmentsDTO assignments = createPublisherAssignments(SELLER_PID);
    SellerRuleDTO ruleDTO =
        createSellerRuleDtoBuilder().assignments(assignments).ownerCompanyPid(SELLER_PID).build();
    CompanyRule rule = RuleMapper.MAPPER.map(ruleDTO);
    RuleDeployedCompany company = new RuleDeployedCompany();
    company.setPid(SELLER_PID);
    given(companyRepository.findAllById(any())).willReturn(List.of(company));

    validator.validateDeployedTargetsAndUpdateRule(rule);
  }

  @Test
  void shouldFailWhenRuleHasDuplicatedName() {
    CompanyRule ruleToSave = new CompanyRule();
    ruleToSave.setPid(RULE_PID);
    CompanyRule existingRule = new CompanyRule();
    existingRule.setPid(111L);
    given(companyRuleRepository.findOne((Specification<CompanyRule>) any()))
        .willReturn(Optional.of(existingRule));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.validateDuplicateName(ruleToSave));
    assertEquals(ServerErrorCodes.SERVER_RULE_DUPLICATE_NAME, exception.getErrorCode());
  }

  @Test
  void validateDuplicateNameSuccessScenario() {
    CompanyRule ruleToSave = new CompanyRule();
    ruleToSave.setPid(RULE_PID);
    CompanyRule existingRule = new CompanyRule();
    existingRule.setPid(RULE_PID);
    given(companyRuleRepository.findOne((Specification<CompanyRule>) any()))
        .willReturn(Optional.of(existingRule));

    validator.validateDuplicateName(ruleToSave);
  }

  @Test
  void shouldFailWhenRuleWithNotAllowedTargetType() {
    CompanyRule rule = createCompanyRule();
    RuleTarget target = new RuleTarget();
    target.setRuleTargetType(RuleTargetType.GENDER);
    rule.setRuleTargets(newHashSet(target));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.validateBidManagementAPIRule(rule));
    assertEquals(
        ServerErrorCodes.SERVER_CURRENT_RULE_NOT_ALLOWED_TARGET_TYPE, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenRuleWithFormula() {
    CompanyRule rule = createCompanyRule();
    RuleFormula formula = new RuleFormula();
    rule.setRuleFormula(formula);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.validateBidManagementAPIRule(rule));
    assertEquals(
        ServerErrorCodes.SERVER_CANT_EDIT_OR_CREATE_RULES_WITH_RULE_FORMULA_THROUGH_API,
        exception.getErrorCode());
  }

  @Test
  void validateBidManagementAPIRuleSuccessScenario() {
    CompanyRule rule = createCompanyRule();
    validator.validateBidManagementAPIRule(rule);
  }

  @Test
  void shouldFailWhenRuleDTOWithNotAllowedTargetType() {
    RuleTargetDTO notAllowedTarget =
        RuleTargetDTO.builder().targetType(RuleTargetType.GENDER).build();
    SellerRuleDTO rule = SellerRuleDTO.builder().targets(newHashSet(notAllowedTarget)).build();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.validateBidManagementAPIRuleDTO(rule));
    assertEquals(
        ServerErrorCodes.SERVER_NOT_ALLOWED_TARGET_TYPE_PROVIDED, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenRuleDTOWithFormula() {
    RuleTargetDTO allowedTarget =
        RuleTargetDTO.builder().targetType(RuleTargetType.COUNTRY).build();
    RuleFormulaDTO formula = RuleFormulaDTO.builder().build();
    SellerRuleDTO rule =
        SellerRuleDTO.builder().targets(newHashSet(allowedTarget)).ruleFormula(formula).build();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.validateBidManagementAPIRuleDTO(rule));
    assertEquals(
        ServerErrorCodes.SERVER_CANT_EDIT_OR_CREATE_RULES_WITH_RULE_FORMULA_THROUGH_API,
        exception.getErrorCode());
  }

  private InventoryAssignmentsDTO createPublisherAssignments(long sellerPid) {
    return InventoryAssignmentsDTO.builder()
        .publishers(Set.of(PublisherAssignmentDTO.builder().pid(sellerPid).build()))
        .build();
  }
}
