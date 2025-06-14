package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionRuleAssignmentAssemblerTest {

  private static final long PID = 1L;

  @Mock RuleDeployedPositionRepository ruleDeployedPositionRepository;
  @InjectMocks PositionRuleAssignmentAssembler positionRuleAssignmentAssembler;

  @Test
  void shouldMapDtoToEntity() {
    // given
    RuleDeployedPosition ruleDeployedPosition = new RuleDeployedPosition();
    ruleDeployedPosition.setPid(PID);

    PositionAssignmentDTO positionAssignmentDTO = PositionAssignmentDTO.builder().pid(PID).build();

    given(ruleDeployedPositionRepository.getOne(PID)).willReturn(ruleDeployedPosition);

    // when
    RuleDeployedPosition result = positionRuleAssignmentAssembler.apply(positionAssignmentDTO);

    // then
    assertEquals(PID, result.getPid());
  }
}
