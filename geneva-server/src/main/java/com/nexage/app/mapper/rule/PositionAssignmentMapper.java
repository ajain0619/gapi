package com.nexage.app.mapper.rule;

import static com.nexage.app.util.HtmlSanitizerUtil.sanitizeHtmlElement;

import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    uses = {SiteAssignmentMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionAssignmentMapper {

  @Mapping(source = "site", target = "siteAssignment")
  PositionAssignmentDTO map(RuleDeployedPosition entity);

  @InheritInverseConfiguration
  RuleDeployedPosition map(PositionAssignmentDTO dto);

  @InheritConfiguration
  void apply(@MappingTarget RuleDeployedPosition entity, PositionAssignmentDTO dto);

  @AfterMapping
  default void sanitizeInput(@MappingTarget RuleDeployedPosition target) {
    target.setName(sanitizeHtmlElement(target.getName()));
    target.setMemo(sanitizeHtmlElement(target.getMemo()));
  }
}
