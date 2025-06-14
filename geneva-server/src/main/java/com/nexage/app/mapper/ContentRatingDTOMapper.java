package com.nexage.app.mapper;

import com.nexage.admin.core.model.ContentRating;
import com.nexage.app.dto.ContentRatingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContentRatingDTOMapper {
  ContentRatingDTOMapper MAPPER = Mappers.getMapper(ContentRatingDTOMapper.class);

  ContentRatingDTO map(ContentRating source);
}
