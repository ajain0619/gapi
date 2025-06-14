package com.nexage.app.mapper.rule;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO.SiteAssignmentDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SiteAssignmentMapper {

  default SiteAssignmentDTO map(RuleDeployedSite entity) {
    if (isNull(entity)) {
      return null;
    }
    SiteAssignmentDTOBuilder builder = SiteAssignmentDTO.builder();
    builder.pid(entity.getPid());
    builder.name(entity.getName());
    if (nonNull(entity.getCompany())) {
      builder.publisherAssignment(
          PublisherAssignmentDTO.builder()
              .pid(entity.getCompany().getPid())
              .name(entity.getCompany().getName())
              .build());
    } else {
      builder.publisherAssignment(
          PublisherAssignmentDTO.builder().pid(entity.getCompanyPid()).build());
    }
    return builder.build();
  }

  @Mapping(target = "companyPid", source = "publisherAssignment.pid")
  @Mapping(target = "company", source = "publisherAssignment")
  RuleDeployedSite map(SiteAssignmentDTO dto);
}
