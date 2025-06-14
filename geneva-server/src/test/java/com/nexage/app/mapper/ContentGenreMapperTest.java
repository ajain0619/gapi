package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.ContentGenre;
import com.nexage.app.dto.ContentGenreDTO;
import org.junit.jupiter.api.Test;

class ContentGenreMapperTest {

  @Test
  void shouldMapToDTO() {
    ContentGenre contentGenre = new ContentGenre(1L, "Action", true);
    ContentGenreDTO contentGenreDTO = ContentGenreDTOMapper.MAPPER.map(contentGenre);
    assertEquals(contentGenre.getPid(), contentGenreDTO.getPid());
    assertEquals(contentGenre.getGenre(), contentGenreDTO.getGenre());
  }
}
