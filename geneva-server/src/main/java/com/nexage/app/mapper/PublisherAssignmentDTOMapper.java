package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PublisherAssignmentDTOMapper {

  PublisherAssignmentDTOMapper MAPPER = Mappers.getMapper(PublisherAssignmentDTOMapper.class);

  PublisherAssignmentDTO map(RuleDeployedCompany ruleDeployedCompany);
}
