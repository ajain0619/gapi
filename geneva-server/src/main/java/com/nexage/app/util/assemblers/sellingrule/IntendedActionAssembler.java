package com.nexage.app.util.assemblers.sellingrule;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleActionType;
import com.nexage.app.util.assemblers.NoContextAssembler;
import java.util.Set;
import org.springframework.stereotype.Component;

/** <code>IntendedActionAssembler</code> */
@Component
public class IntendedActionAssembler extends NoContextAssembler {
  public static final Set<String> DEFAULT_FIELDS =
      Set.of("pid", "version", "actionType", "actionData");

  public IntendedActionDTO make(RuleIntendedAction entity) {
    return make(entity, DEFAULT_FIELDS);
  }

  public IntendedActionDTO make(RuleIntendedAction entity, Set<String> fields) {
    final IntendedActionDTO.IntendedActionDTOBuilder builder = IntendedActionDTO.builder();

    for (String field : (fields != null) ? fields : DEFAULT_FIELDS) {
      switch (field) {
        case "pid":
          builder.pid(entity.getPid());
          break;
        case "version":
          builder.version(entity.getVersion());
          break;
        case "actionType":
          builder.actionType(RuleActionType.valueOf(entity.getActionType().name()));
          break;
        case "actionData":
          RuleActionType ruleActionType = RuleActionType.valueOf(entity.getActionType().name());
          builder.actionData(ruleActionType.translateDataFromEntityToDto(entity.getActionData()));
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }
    return builder.build();
  }

  public RuleIntendedAction apply(
      RuleIntendedAction entity, IntendedActionDTO dto, CompanyRule rule) {
    entity.setPid(dto.getPid());
    entity.setVersion(dto.getVersion());
    entity.setRule(rule);
    entity.setActionType(
        com.nexage.admin.core.enums.RuleActionType.valueOf(dto.getActionType().name()));
    entity.setActionData(dto.getActionType().translateDataFromDtoToEntity(dto.getActionData()));
    return entity;
  }
}
