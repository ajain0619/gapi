package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SiteAssignmentDTOMapper {

  SiteAssignmentDTOMapper MAPPER = Mappers.getMapper(SiteAssignmentDTOMapper.class);

  SiteAssignmentDTO map(RuleDeployedSite ruleDeployedSite);
}
