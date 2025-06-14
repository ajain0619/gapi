package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Rule;
import com.nexage.admin.core.sparta.jpa.model.DealView;
import com.nexage.app.dto.AssignedInventoryType;
import com.nexage.app.mapper.deal.DealDTOMapper;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DealDTOMapperTest {

  @Test
  void shouldMapToDto() {
    var source = getDirectDealView();
    var result = DealDTOMapper.MAPPER.map(source);
    assertEquals(source.getPid(), result.getPid());
    assertEquals(source.getDealId(), result.getDealId());
    assertEquals(source.getRules().get(0).getPid(), result.getRulePid());
    assertEquals(AssignedInventoryType.FORMULA, result.getAssignedInventoryType());
  }

  private DealView getDirectDealView() {
    final long pid = 1L;
    final String dealId = UUID.randomUUID().toString();
    final String formula = UUID.randomUUID().toString();
    final long rulePid = new Random().nextLong();

    final Rule rule =
        new Rule() {
          public Long getPid() {
            return rulePid;
          }
        };
    ArrayList<Rule> rules = new ArrayList<>();
    rules.add(rule);
    var view = mock(DealView.class);
    when(view.getPid()).thenReturn(pid);
    when(view.getDealId()).thenReturn(dealId);
    when(view.getPlacementFormula()).thenReturn(formula);
    when(view.getRules()).thenReturn(rules);
    return view;
  }
}
