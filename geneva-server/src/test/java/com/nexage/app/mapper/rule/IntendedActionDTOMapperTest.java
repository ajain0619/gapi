package com.nexage.app.mapper.rule;

import static com.nexage.app.web.support.TestObjectsFactory.createFilterRuleIntendedAction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntendedActionDTOMapperTest {

  private IntendedActionDTOMapper mapper = IntendedActionDTOMapper.MAPPER;

  @Test
  void entityDtoMappingTest() {
    RuleIntendedAction action = createFilterRuleIntendedAction();
    IntendedActionDTO mapped = mapper.map(action);

    assertEquals(action.getPid(), mapped.getPid());
    assertEquals(action.getVersion(), mapped.getVersion());
    assertEquals(action.getActionType().name(), mapped.getActionType().name());
    assertEquals(
        mapped.getActionType().translateDataFromEntityToDto(action.getActionData()),
        mapped.getActionData());
  }

  @Test
  void dtoEntityApplyTest() {
    RuleIntendedAction action = createFilterRuleIntendedAction();
    IntendedActionDTO dto = TestObjectsFactory.createFilterRuleIntendedActionDto();
    mapper.apply(dto, action);

    assertNotEquals(dto.getPid(), action.getPid());
    assertNotEquals(dto.getVersion(), action.getVersion());
    assertEquals(
        dto.getActionType().translateDataFromDtoToEntity(dto.getActionData()),
        action.getActionData());
    assertEquals(dto.getActionType().name(), action.getActionType().name());
  }

  @Test
  void dtoEntityMappingTest() {
    IntendedActionDTO dto = TestObjectsFactory.createFilterRuleIntendedActionDto();
    RuleIntendedAction mapped = mapper.map(dto);

    assertNull(mapped.getPid());
    assertNull(mapped.getVersion());
    assertNull(mapped.getRule());
    assertEquals(dto.getActionType().name(), mapped.getActionType().name());
    assertEquals(
        dto.getActionType().translateDataFromDtoToEntity(dto.getActionData()),
        mapped.getActionData());
  }
}
