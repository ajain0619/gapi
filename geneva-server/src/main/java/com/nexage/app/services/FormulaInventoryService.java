package com.nexage.app.services;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormulaInventoryService {
  /**
   * Find all {@link FormulaInventoryDTO} under request criteria for a given publisher, returning a
   * paginated response.
   *
   * @param publisherPid Long
   * @param formulaDto PlacementFormulaDTO
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link FormulaInventoryDTO} instances based on parameters.
   */
  Page<FormulaInventoryDTO> getPlacementsByFormulaForPublisher(
      Long publisherPid, PlacementFormulaDTO formulaDto, Pageable pageable);

  /**
   * Find all {@link FormulaInventoryDTO} under request criteria, returning a paginated response.
   *
   * @param formulaDto PlacementFormulaDTO
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link FormulaInventoryDTO} instances based on parameters.
   */
  Page<FormulaInventoryDTO> getPlacementsByFormulaForDeals(
      PlacementFormulaDTO formulaDto, Pageable pageable);

  /**
   * Find all {@link RuleFormulaPositionView} under request criteria, returning a paginated
   * response.
   *
   * @param formulaDto PlacementFormulaDTO
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link RuleFormulaPositionView} instances based on parameters.
   */
  Page<RuleFormulaPositionView> findPlacementsByFormula(
      PlacementFormulaDTO formulaDto, Pageable pageable);
}
