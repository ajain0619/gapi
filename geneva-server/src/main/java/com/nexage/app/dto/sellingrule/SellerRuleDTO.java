package com.nexage.app.dto.sellingrule;

import com.nexage.admin.core.enums.Status;
import com.nexage.app.util.validator.rule.RuleFormulaConstraint;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class SellerRuleDTO extends RuleDTO {

  @Setter(AccessLevel.PUBLIC)
  @Valid
  private InventoryAssignmentsDTO assignments;

  @RuleFormulaConstraint @Valid private RuleFormulaDTO ruleFormula;
  @NotNull private Long ownerCompanyPid;

  @Builder
  public SellerRuleDTO(
      Long pid,
      Integer version,
      String name,
      String description,
      Status status,
      Set<IntendedActionDTO> intendedActions,
      RuleType type,
      Long ownerCompanyPid,
      Set<RuleTargetDTO> targets,
      RuleFormulaDTO ruleFormula,
      InventoryAssignmentsDTO assignments) {
    super(pid, version, name, description, status, intendedActions, type, targets);
    this.assignments = assignments;
    this.ownerCompanyPid = ownerCompanyPid;
    this.ruleFormula = ruleFormula;
  }
}
