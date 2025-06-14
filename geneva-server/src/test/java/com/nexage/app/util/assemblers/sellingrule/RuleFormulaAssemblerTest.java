package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.RuleFormula;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleFormulaAssemblerTest {

  @Mock private PlacementFormulaAssembler placementFormulaAssembler;
  @InjectMocks private RuleFormulaAssembler ruleFormulaAssembler;

  @Test
  void test_make() {
    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(11L);
    ruleFormula.setVersion(1);
    ruleFormula.setAutoUpdate(true);
    String formula = "{\"groupedBy\":\"OR\"}";
    ruleFormula.setFormula(formula);

    PlacementFormulaDTO expectedPlacementFormulaDto = new PlacementFormulaDTO();
    expectedPlacementFormulaDto.setGroupedBy(FormulaGroupingDTO.OR);
    when(placementFormulaAssembler.make(formula)).thenReturn(expectedPlacementFormulaDto);

    // method under test
    RuleFormulaDTO ruleFormulaDto = ruleFormulaAssembler.make(null, ruleFormula);

    assertEquals(new Long(11L), ruleFormulaDto.getPid());
    assertEquals(new Integer(1), ruleFormulaDto.getVersion());
    assertTrue(ruleFormulaDto.isAutoUpdate());
    assertEquals(
        expectedPlacementFormulaDto.getGroupedBy(),
        ruleFormulaDto.getPlacementFormula().getGroupedBy());
    assertEquals(
        expectedPlacementFormulaDto.getFormulaGroups(),
        ruleFormulaDto.getPlacementFormula().getFormulaGroups());
    assertEquals(0, ruleFormulaDto.getPlacementFormula().getFormulaGroups().size());
  }

  @Test
  void test_make_NullPlacementFormula() {
    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(11L);
    ruleFormula.setVersion(1);
    ruleFormula.setAutoUpdate(true);
    ruleFormula.setFormula(null);

    // method under test
    RuleFormulaDTO ruleFormulaDto = ruleFormulaAssembler.make(null, ruleFormula);

    assertEquals(new Long(11L), ruleFormulaDto.getPid());
    assertEquals(new Integer(1), ruleFormulaDto.getVersion());
    assertTrue(ruleFormulaDto.isAutoUpdate());
    assertNull(ruleFormulaDto.getPlacementFormula());
  }

  @Test
  void test_make_GenevaValidationException() {
    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(11L);
    ruleFormula.setVersion(1);
    ruleFormula.setAutoUpdate(true);
    String formula = "{\"dummyinvalidBy\":\"OR\"}";
    ruleFormula.setFormula(formula);

    when(placementFormulaAssembler.make(formula))
        .thenThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR));

    assertThrows(
        GenevaValidationException.class, () -> ruleFormulaAssembler.make(null, ruleFormula));
    // method under test

  }

  @Test
  void test_apply() {
    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(22L);

    PlacementFormulaDTO mockPlacementFormulaDto = mock(PlacementFormulaDTO.class);
    RuleFormulaDTO ruleFormulaDto =
        RuleFormulaDTO.builder().autoUpdate(true).placementFormula(mockPlacementFormulaDto).build();

    String formula = "{\"groupedBy\":\"OR\",\"formulaGroups\":[]}";
    when(placementFormulaAssembler.applyToString(mockPlacementFormulaDto)).thenReturn(formula);

    // method under test
    RuleFormula ruleFormulaResult = ruleFormulaAssembler.apply(null, ruleFormula, ruleFormulaDto);

    assertEquals(formula, ruleFormulaResult.getFormula());
    assertTrue(ruleFormulaResult.isAutoUpdate());

    assertEquals(new Long(22L), ruleFormulaResult.getPid());
  }

  @Test
  void test_apply_NullPlacementFormula() {
    RuleFormula ruleFormulaInParam = new RuleFormula();
    ruleFormulaInParam.setPid(22L);

    RuleFormulaDTO ruleFormulaDto =
        RuleFormulaDTO.builder().autoUpdate(true).placementFormula(null).build();

    // method under test
    RuleFormula ruleFormulaResult =
        ruleFormulaAssembler.apply(null, ruleFormulaInParam, ruleFormulaDto);

    assertNull(ruleFormulaResult.getFormula()); // not set so null
    assertFalse(ruleFormulaResult.isAutoUpdate()); // not set (to true that is in the dto)

    assertEquals(new Long(22L), ruleFormulaResult.getPid());
  }

  @Test
  void test_apply_GenevaValidationException() {
    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(22L);

    PlacementFormulaDTO mockPlacementFormulaDto = mock(PlacementFormulaDTO.class);
    RuleFormulaDTO ruleFormulaDto =
        RuleFormulaDTO.builder().autoUpdate(true).placementFormula(mockPlacementFormulaDto).build();

    when(placementFormulaAssembler.applyToString(mockPlacementFormulaDto))
        .thenThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR));

    assertThrows(
        GenevaValidationException.class,
        () -> ruleFormulaAssembler.apply(null, ruleFormula, ruleFormulaDto));
  }
}
