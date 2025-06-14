package com.nexage.app.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.services.FormulaInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/formula-inventories")
@RestController
@RequestMapping(value = "/v1/formula-inventories")
public class FormulaInventoryController {

  private final FormulaInventoryService formulaInventoryService;

  public FormulaInventoryController(FormulaInventoryService formulaInventoryService) {
    this.formulaInventoryService = formulaInventoryService;
  }

  /**
   * Fetch resource to retrieve paginated {@link FormulaInventoryDTO} based on input formula. We are
   * using POST instead of GET here as we need json input body in the request. WARNING: This
   * approach should be used only when you need json input in request to fetch the data.
   *
   * @param pageable {@link Pageable}
   * @param formulaDto Query fields {@link PlacementFormulaDTO}
   * @param unpaged Query terms {@link boolean}
   * @return @return {@link ResponseEntity} of type {@link Page} {@link FormulaInventoryDTO} returns
   *     list of FormulaInventoryDTO
   */
  @Timed
  @ExceptionMetered
  @Operation(
      summary = "Fetch All FormulaInventoryDTO matching placement formula",
      description = "Optional unpaged argument can be passed to get the list")
  @ApiResponse(content = @Content(schema = @Schema(implementation = FormulaInventoryDTO.class)))
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<FormulaInventoryDTO>> fetchPagePlacementsByFormulaForDeals(
      @RequestBody PlacementFormulaDTO formulaDto,
      @RequestParam(name = "unpaged", required = false) boolean unpaged,
      @PageableDefault(sort = "pid", direction = Sort.Direction.ASC) Pageable pageable) {
    if (unpaged) {
      pageable = Pageable.unpaged();
    }
    return ResponseEntity.ok(
        formulaInventoryService.getPlacementsByFormulaForDeals(formulaDto, pageable));
  }

  /**
   * Fetch resource to retrieve paginated {@link FormulaInventoryDTO} based on input formula for a
   * given publisher. We are using POST instead of GET here as we need json input body in the
   * request. WARNING: This approach should be used only when you need json input in request to
   * fetch the data.
   *
   * @param pageable {@link Pageable}
   * @param formulaDto Query fields {@link PlacementFormulaDTO}
   * @param unpaged Query terms {@link boolean}
   * @return @return {@link ResponseEntity} of type {@link Page} {@link FormulaInventoryDTO} returns
   *     list of FormulaInventoryDTO
   */
  @Timed
  @ExceptionMetered
  @Operation(
      summary = "Fetch All FormulaInventoryDTO matching placement formula",
      description = "Optional unpaged argument can be passed to get the list")
  @ApiResponse(content = @Content(schema = @Schema(implementation = FormulaInventoryDTO.class)))
  @PostMapping(
      value = "/{publisherPid}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<FormulaInventoryDTO>> fetchPlacementsByFormulaForPublisher(
      @PathVariable(value = "publisherPid") Long publisherPid,
      @RequestBody PlacementFormulaDTO formulaDto,
      @RequestParam(name = "unpaged", required = false) boolean unpaged,
      @PageableDefault(sort = "pid", direction = Sort.Direction.ASC) Pageable pageable) {
    if (unpaged) {
      pageable = Pageable.unpaged();
    }
    return ResponseEntity.ok(
        formulaInventoryService.getPlacementsByFormulaForPublisher(
            publisherPid, formulaDto, pageable));
  }
}
