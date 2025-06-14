package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.TagView;
import com.nexage.app.dto.tag.TagDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TagDTOMapperTest {

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    TagView source = TagView.builder().pid(pid).name(id).build();
    TagDTO result = TagDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
  }
}
