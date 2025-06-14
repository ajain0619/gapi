package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.sparta.jpa.model.TagView;
import org.junit.jupiter.api.Test;

class TagViewMapperTest {

  @Test
  void testMapping() {
    var input = new TagView();
    input.setPid(1L);
    input.setName("tag name");
    input.setNoAdRegex("regex");
    input.setStatus(Status.ACTIVE);

    var out = TagViewMapper.MAPPER.map(input);
    assertAll(
        "tag view mapper",
        () -> assertEquals(input.getPid(), out.getPid()),
        () -> assertEquals(input.getName(), out.getName()),
        () -> assertEquals(input.getNoAdRegex(), out.getNoAdRegex()),
        () -> assertEquals(input.getStatus(), out.getStatus()));
  }
}
