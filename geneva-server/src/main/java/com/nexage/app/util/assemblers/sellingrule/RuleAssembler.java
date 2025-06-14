package com.nexage.app.util.assemblers.sellingrule;

import static java.util.stream.Collectors.toSet;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleFormula;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.RuleIntendedActionRepository;
import com.nexage.admin.core.repository.RuleTargetRepository;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.assemblers.NoContextAssembler;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.context.SellingRuleContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RuleAssembler extends NoContextAssembler {
  private final RuleTargetAssembler ruleTargetAssembler;
  private final RuleAssignmentAssembler ruleAssignmentAssembler;
  private final IntendedActionAssembler intendedActionAssembler;
  private final RuleTargetRepository ruleTargetRepository;
  private final RuleIntendedActionRepository ruleIntendedActionRepository;
  private final RuleFormulaAssembler ruleFormulaAssembler;

  public static final Set<String> DEFAULT_FIELDS =
      Sets.newHashSet(
          "pid",
          "version",
          "name",
          "description",
          "status",
          "intendedActions",
          "type",
          "ownerCompanyPid",
          "targets",
          "assignments",
          "ruleFormula");

  public static final Set<String> DEPLOYED_RULE_FIELDS =
      Sets.newHashSet("pid", "version", "name", "description", "status", "type", "ownerCompanyPid");

  public SellerRuleDTO make(CompanyRule entity) {
    return make(entity, DEFAULT_FIELDS);
  }

  public SellerRuleDTO make(CompanyRule entity, Set<String> fields) {
    SellerRuleDTO.SellerRuleDTOBuilder builder = SellerRuleDTO.builder();

    for (String field : (fields != null) ? fields : DEFAULT_FIELDS) {
      switch (field) {
        case "pid":
          builder.pid(entity.getPid());
          break;
        case "version":
          builder.version(entity.getVersion());
          break;
        case "name":
          builder.name(entity.getName());
          break;
        case "description":
          builder.description(entity.getDescription());
          break;
        case "status":
          builder.status(entity.getStatus());
          break;
        case "intendedActions":
          if (entity.getRuleIntendedActions() != null
              && entity.getRuleIntendedActions().size() > 0) {
            Set<IntendedActionDTO> IntendedActionDTOs = new HashSet<>();
            for (RuleIntendedAction ruleIntendedAction : entity.getRuleIntendedActions()) {
              IntendedActionDTOs.add(intendedActionAssembler.make(ruleIntendedAction));
            }
            builder.intendedActions(IntendedActionDTOs);
          }
          break;
        case "type":
          builder.type(RuleType.valueOf(entity.getRuleType().name()));
          break;
        case "ownerCompanyPid":
          builder.ownerCompanyPid(entity.getOwnerCompanyPid());
          break;
        case "targets":
          if (entity.getRuleTargets() != null && entity.getRuleTargets().size() > 0) {
            Set<RuleTargetDTO> assignedTargets = new HashSet<>();
            for (RuleTarget ruleTarget : entity.getRuleTargets()) {
              assignedTargets.add(ruleTargetAssembler.make(ruleTarget));
            }
            builder.targets(assignedTargets);
          }
          break;
        case "assignments":
          SellingRuleContext.Builder sellingRuleContextBuilder =
              SellingRuleContext.newBuilder()
                  .withPublishersForSellingRule(entity.getDeployedCompanies())
                  .withSitesForSellingRule(entity.getDeployedSites())
                  .withPositionsForSellingRule(entity.getDeployedPositions());
          builder.assignments(ruleAssignmentAssembler.make(sellingRuleContextBuilder.build()));
          break;
        case "ruleFormula":
          if (entity.getRuleFormula() != null) {
            builder.ruleFormula(
                ruleFormulaAssembler.make(
                    NullableContext.nullableContext, entity.getRuleFormula()));
          }
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }

    return builder.build();
  }

  public CompanyRule apply(CompanyRule entity, SellerRuleDTO dto) {
    entity.setPid(dto.getPid());
    entity.setVersion(dto.getVersion());
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setStatus(dto.getStatus());
    entity.setRuleType(com.nexage.admin.core.enums.RuleType.valueOf(dto.getType().name()));
    entity.setOwnerCompanyPid(dto.getOwnerCompanyPid());

    processFormula(entity, dto);

    processTargets(entity, dto);

    processIntendedActions(entity, dto);

    if (dto.getAssignments() != null) {
      ruleAssignmentAssembler.apply(entity, dto.getAssignments());
    }

    return entity;
  }

  private void processIntendedActions(CompanyRule entity, SellerRuleDTO dto) {
    final Set<RuleIntendedAction> newIntendedActions =
        dto.getIntendedActions().stream()
            .map(
                ia ->
                    intendedActionAssembler.apply(
                        ia.getPid() != null
                            ? ruleIntendedActionRepository
                                .findById(ia.getPid())
                                .orElseThrow(
                                    () ->
                                        new GenevaValidationException(
                                            ServerErrorCodes.SERVER_INTENDED_ACTION_NOT_FOUND))
                            : new RuleIntendedAction(),
                        ia,
                        entity))
            .collect(toSet());
    entity.getRuleIntendedActions().clear();
    entity.getRuleIntendedActions().addAll(newIntendedActions);
  }

  private void processTargets(CompanyRule entity, SellerRuleDTO dto) {
    final Set<RuleTarget> newTargets =
        dto.getTargets().stream()
            .map(
                d ->
                    ruleTargetAssembler.apply(
                        d.getPid() != null
                            ? ruleTargetRepository
                                .findById(d.getPid())
                                .orElseThrow(
                                    () ->
                                        new GenevaValidationException(
                                            ServerErrorCodes.SERVER_RULE_TARGET_NOT_FOUND))
                            : new RuleTarget(),
                        d,
                        entity))
            .collect(toSet());
    entity.getRuleTargets().clear();
    entity.getRuleTargets().addAll(newTargets);
  }

  private void processFormula(CompanyRule entity, SellerRuleDTO dto) {
    if (dto.getRuleFormula() != null) {
      RuleFormula formula = entity.getRuleFormula();

      if (formula == null) {
        formula = new RuleFormula();
      }

      formula =
          ruleFormulaAssembler.apply(
              NullableContext.nullableContext, formula, dto.getRuleFormula());
      formula.setRule(entity);
      entity.setRuleFormula(formula);
    } else {
      entity.setRuleFormula(null);
    }
  }
}
