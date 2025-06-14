package com.nexage.app.services.sellingrule.formula;

import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;

/** Handles rule formula processing */
public interface RuleFormulaService {

  /**
   * Processes rule formula that was received in the web request
   *
   * @param inputFormula rule formula to process
   * @param sellerPid pid of a seller that request refers to
   * @return assignments that were calculated by the formula
   */
  InventoryAssignmentsDTO processFormula(RuleFormulaDTO inputFormula, Long sellerPid);
}
