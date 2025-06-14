package com.nexage.app.mapper;

import com.nexage.admin.core.model.TagView;
import com.nexage.app.dto.tag.TagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagDTOMapper {

  TagDTOMapper MAPPER = Mappers.getMapper(TagDTOMapper.class);

  TagDTO map(TagView source);
}
