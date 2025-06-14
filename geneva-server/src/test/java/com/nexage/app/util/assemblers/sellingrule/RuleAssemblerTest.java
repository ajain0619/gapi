package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.RuleIntendedActionRepository;
import com.nexage.admin.core.repository.RuleTargetRepository;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleAssemblerTest {

  public static final long SELLER_PID = 1L;
  public static final long RULE_PID = 2L;
  @Mock private IntendedActionAssembler intendedActionAssembler;
  @Mock private RuleTargetAssembler ruleTargetAssembler;
  @Mock private RuleTargetRepository ruleTargetRepository;
  @Mock private RuleIntendedActionRepository ruleIntendedActionRepository;

  @InjectMocks private RuleAssembler ruleAssembler;

  @Test
  void shouldReturnValidEntityWhenDtoHasRuleTargets() {
    // given
    CompanyRule companyRule = new CompanyRule();
    RuleTargetDTO ruleTargetDTO = RuleTargetDTO.builder().pid(RULE_PID).build();
    RuleTarget ruleTarget = new RuleTarget();
    ruleTarget.setPid(RULE_PID);

    IntendedActionDTO intendedActionDTO = IntendedActionDTO.builder().build();

    SellerRuleDTO sellerRuleDto =
        new SellerRuleDTO(
            SELLER_PID,
            null,
            null,
            null,
            null,
            Set.of(intendedActionDTO),
            RuleType.DEAL,
            null,
            Set.of(ruleTargetDTO),
            null,
            null);

    when(ruleTargetRepository.findById(ruleTargetDTO.getPid())).thenReturn(Optional.of(ruleTarget));
    when(ruleTargetAssembler.apply(ruleTarget, ruleTargetDTO, companyRule)).thenReturn(ruleTarget);

    // when
    CompanyRule result = ruleAssembler.apply(companyRule, sellerRuleDto);

    // then
    assertEquals(SELLER_PID, result.getPid());
    assertEquals(1, result.getRuleTargets().size());
    assertEquals(RULE_PID, result.getRuleTargets().iterator().next().getPid());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenDtosRuleTargetsNotFound() {
    // given
    CompanyRule companyRule = new CompanyRule();
    RuleTargetDTO ruleTargetDTO = RuleTargetDTO.builder().pid(RULE_PID).build();
    RuleTarget ruleTarget = new RuleTarget();
    ruleTarget.setPid(RULE_PID);

    SellerRuleDTO sellerRuleDto =
        new SellerRuleDTO(
            SELLER_PID,
            null,
            null,
            null,
            null,
            null,
            RuleType.DEAL,
            null,
            Set.of(ruleTargetDTO),
            null,
            null);

    when(ruleTargetRepository.findById(ruleTargetDTO.getPid())).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> ruleAssembler.apply(companyRule, sellerRuleDto));

    // then
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_NOT_FOUND, result.getErrorCode());
  }

  @Test
  void shouldReturnValidEntityWhenDtoHasValidRuleIntendedAction() {
    // given
    CompanyRule companyRule = new CompanyRule();
    RuleIntendedAction ruleIntendedAction = new RuleIntendedAction();
    ruleIntendedAction.setPid(RULE_PID);

    IntendedActionDTO intendedActionDTO = IntendedActionDTO.builder().pid(2L).build();

    SellerRuleDTO sellerRuleDto =
        new SellerRuleDTO(
            SELLER_PID,
            null,
            null,
            null,
            null,
            Set.of(intendedActionDTO),
            RuleType.DEAL,
            null,
            Set.of(),
            null,
            null);

    when(ruleIntendedActionRepository.findById(intendedActionDTO.getPid()))
        .thenReturn(Optional.of(ruleIntendedAction));
    when(intendedActionAssembler.apply(ruleIntendedAction, intendedActionDTO, companyRule))
        .thenReturn(ruleIntendedAction);

    // when
    CompanyRule result = ruleAssembler.apply(companyRule, sellerRuleDto);

    // then
    assertEquals(SELLER_PID, result.getPid());
    assertEquals(1, result.getRuleIntendedActions().size());
    assertEquals(RULE_PID, result.getRuleIntendedActions().iterator().next().getPid());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenDtosIntendedActionNotFound() {
    // given
    CompanyRule companyRule = new CompanyRule();
    RuleIntendedAction ruleIntendedAction = new RuleIntendedAction();
    ruleIntendedAction.setPid(RULE_PID);

    IntendedActionDTO intendedActionDTO = IntendedActionDTO.builder().pid(2L).build();

    SellerRuleDTO sellerRuleDto =
        new SellerRuleDTO(
            SELLER_PID,
            null,
            null,
            null,
            null,
            Set.of(intendedActionDTO),
            RuleType.DEAL,
            null,
            Set.of(),
            null,
            null);

    when(ruleIntendedActionRepository.findById(intendedActionDTO.getPid()))
        .thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> ruleAssembler.apply(companyRule, sellerRuleDto));

    // then
    assertEquals(ServerErrorCodes.SERVER_INTENDED_ACTION_NOT_FOUND, result.getErrorCode());
  }
}
