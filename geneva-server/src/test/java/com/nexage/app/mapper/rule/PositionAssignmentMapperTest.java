package com.nexage.app.mapper.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PositionAssignmentMapperTest {

  private final PositionAssignmentMapper mapper = Mappers.getMapper(PositionAssignmentMapper.class);
  private final PositionAssignmentDTO unsafeDTO =
      PositionAssignmentDTO.builder()
          .pid(TestObjectsFactory.randomLong())
          .siteAssignment(TestObjectsFactory.createSiteAssignmentDto())
          .name("<script>alert('name')</script>")
          .memo("<script>alert('memo')</script>")
          .build();

  @Test
  void shouldSanitizeDTO() {
    // when
    RuleDeployedPosition output = mapper.map(unsafeDTO);

    // then
    assertEquals("&lt;script&gt;alert('name')&lt;/script&gt;", output.getName());
    assertEquals("&lt;script&gt;alert('memo')&lt;/script&gt;", output.getMemo());
  }

  @Test
  void shouldNotSanitizeIfThereIsNoHtmlContent() {
    // given
    PositionAssignmentDTO safeDTO =
        PositionAssignmentDTO.builder()
            .pid(TestObjectsFactory.randomLong())
            .siteAssignment(TestObjectsFactory.createSiteAssignmentDto())
            .name("test&")
            .memo("test&test&")
            .build();

    // when
    RuleDeployedPosition output = mapper.map(safeDTO);

    // then
    assertEquals("test&", output.getName());
    assertEquals("test&test&", output.getMemo());
  }
}
