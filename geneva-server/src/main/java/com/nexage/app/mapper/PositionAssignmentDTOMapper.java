package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionAssignmentDTOMapper {

  PositionAssignmentDTOMapper MAPPER = Mappers.getMapper(PositionAssignmentDTOMapper.class);

  PositionAssignmentDTO map(RuleDeployedPosition ruleDeployedPosition);
}
