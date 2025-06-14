package com.nexage.app.mapper;

import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.TagView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagViewMapper {

  TagViewMapper MAPPER = Mappers.getMapper(TagViewMapper.class);

  Tag map(TagView tagView);
}
