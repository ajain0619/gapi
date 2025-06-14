package com.nexage.app.web.placement;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.PlacementValidMemoDTO;
import com.nexage.app.services.PlacementValidMemoDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Retrieve a valid memo with formatter for a given placement")
@RequestMapping(value = "/v1/sellers/{sellerId}", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlacementValidMemoDTOController {

  private final PlacementValidMemoDTOService placementValidMemoDTOService;

  public PlacementValidMemoDTOController(
      PlacementValidMemoDTOService placementValidMemoDTOService) {
    this.placementValidMemoDTOService = placementValidMemoDTOService;
  }

  /**
   * GET valid copy name to based on original placement memo.
   *
   * @param memo placement memo
   * @return {@link ResponseEntity} of type {@link PlacementValidMemoDTO}.
   */
  @Operation(summary = "Get next valid copy placement memo for given sellerId and siteId")
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sites/{siteId}/placements/valid-memos")
  public ResponseEntity<PlacementValidMemoDTO> getValidPlacementMemo(
      @PathVariable Long sellerId,
      @PathVariable Long siteId,
      @Parameter(name = "memo") @RequestParam String memo) {
    return ResponseEntity.ok(
        placementValidMemoDTOService.getValidPlacementMemo(siteId, sellerId, memo));
  }
}
