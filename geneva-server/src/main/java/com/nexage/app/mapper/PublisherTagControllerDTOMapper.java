package com.nexage.app.mapper;

import com.nexage.admin.core.sparta.jpa.model.TagController;
import com.nexage.app.dto.publisher.PublisherTagControllerDTO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PublisherTagControllerDTOMapper {

  PublisherTagControllerDTOMapper MAPPER = Mappers.getMapper(PublisherTagControllerDTOMapper.class);

  PublisherTagControllerDTO map(TagController tagController);

  @BeforeMapping
  default void checkEntityForNull(
      @MappingTarget TagController tagController, PublisherTagControllerDTO dto) {
    if (tagController == null) {
      throw new IllegalArgumentException("Object to be updated cannot be null: tagController");
    }
  }

  TagController map(@MappingTarget TagController tagController, PublisherTagControllerDTO dto);
}
