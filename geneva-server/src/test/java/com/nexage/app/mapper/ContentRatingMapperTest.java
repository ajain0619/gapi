package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.ContentRating;
import com.nexage.app.dto.ContentRatingDTO;
import org.junit.jupiter.api.Test;

class ContentRatingMapperTest {

  @Test
  void shouldMapToDTO() {
    ContentRating contentRating = new ContentRating(1L, "NG", true);
    ContentRatingDTO contentRatingDTO = ContentRatingDTOMapper.MAPPER.map(contentRating);
    assertEquals(contentRating.getPid(), contentRatingDTO.getPid());
    assertEquals(contentRating.getRating(), contentRatingDTO.getRating());
  }
}
