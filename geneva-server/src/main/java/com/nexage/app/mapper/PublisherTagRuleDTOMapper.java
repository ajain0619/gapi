package com.nexage.app.mapper;

import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.app.dto.publisher.PublisherTagRuleDTO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PublisherTagRuleDTOMapper {

  PublisherTagRuleDTOMapper MAPPER = Mappers.getMapper(PublisherTagRuleDTOMapper.class);

  @Mapping(target = "tag", ignore = true)
  @Mapping(target = "data", source = "target")
  PublisherTagRuleDTO map(TagRule rule);

  @BeforeMapping
  default void checkEntityForNull(
      @MappingTarget TagRule tagRule, PublisherTagRuleDTO publisherTagRuleDTO) {
    if (tagRule == null) {
      throw new IllegalArgumentException("Object to be updated cannot be null: tagRule");
    }
  }

  @Mapping(target = "tag", ignore = true)
  @Mapping(target = "pid", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "target", source = "data")
  TagRule map(@MappingTarget TagRule tagRule, PublisherTagRuleDTO publisherTagRuleDTO);
}
