package com.nexage.app.mapper;

import com.nexage.admin.core.model.ContentGenre;
import com.nexage.app.dto.ContentGenreDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContentGenreDTOMapper {
  ContentGenreDTOMapper MAPPER = Mappers.getMapper(ContentGenreDTOMapper.class);

  ContentGenreDTO map(ContentGenre source);
}
