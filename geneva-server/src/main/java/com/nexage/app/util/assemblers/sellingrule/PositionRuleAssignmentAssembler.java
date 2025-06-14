package com.nexage.app.util.assemblers.sellingrule;

import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import com.nexage.app.util.assemblers.NoContextAssembler;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionRuleAssignmentAssembler extends NoContextAssembler {
  private final SiteRuleAssignmentAssembler siteRuleAssignmentAssembler;
  private final RuleDeployedPositionRepository ruleDeployedPositionRepository;

  public static final Set<String> DEFAULT_FIELDS =
      Set.of("pid", "name", "memo", "siteRuleAssignment");

  public PositionAssignmentDTO make(RuleDeployedPosition position) {
    return make(position, DEFAULT_FIELDS);
  }

  public PositionAssignmentDTO make(RuleDeployedPosition deployedPosition, Set<String> fields) {
    PositionAssignmentDTO.PositionAssignmentDTOBuilder builder = PositionAssignmentDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          builder.pid(deployedPosition.getPid());
          break;
        case "name":
          builder.name(deployedPosition.getName());
          break;
        case "memo":
          builder.memo(deployedPosition.getMemo());
          break;
        case "siteRuleAssignment":
          builder.siteAssignment(siteRuleAssignmentAssembler.make(deployedPosition.getSite()));
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }

    return builder.build();
  }

  public RuleDeployedPosition apply(PositionAssignmentDTO positionAssignmentDto) {
    return ruleDeployedPositionRepository.getOne(positionAssignmentDto.getPid());
  }
}
