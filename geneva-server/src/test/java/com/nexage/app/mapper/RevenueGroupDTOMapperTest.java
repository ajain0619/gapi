package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.RevenueGroup;
import com.nexage.app.dto.RevenueGroupDTO;
import org.junit.jupiter.api.Test;

class RevenueGroupDTOMapperTest {

  @Test
  void shouldMapFromEntityToDTO() {
    // given
    RevenueGroup input = new RevenueGroup(123L, "foo", "testName", Status.ACTIVE, 456, null, null);
    RevenueGroupDTO expectedOutput =
        new RevenueGroupDTO(
            input.getPid(), input.getId(), input.getName(), input.getStatus(), input.getVersion());

    // when
    RevenueGroupDTO output = RevenueGroupDTOMapper.MAPPER.map(input);

    // then
    assertEquals(expectedOutput, output);
  }
}
