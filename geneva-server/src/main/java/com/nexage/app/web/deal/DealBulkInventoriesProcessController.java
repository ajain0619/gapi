package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.services.deal.DealSpecificAssignedInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "/v1/deals/bulk-inventories")
@RestController
@RequestMapping(value = "/v1/deals/bulk-inventories")
public class DealBulkInventoriesProcessController {

  private final DealSpecificAssignedInventoryService specificAssignedInventoryDTOService;

  public DealBulkInventoriesProcessController(
      DealSpecificAssignedInventoryService specificAssignedInventoryDTOService) {
    this.specificAssignedInventoryDTOService = specificAssignedInventoryDTOService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Process the file content and generate specific assigned inventory")
  @ApiResponse(
      content = @Content(schema = @Schema(implementation = SpecificAssignedInventoryDTO.class)))
  public ResponseEntity<SpecificAssignedInventoryDTO> processBulkInventoriesFile(
      @RequestParam("inventoriesFile") MultipartFile inventoriesFile) {
    return ResponseEntity.ok(
        specificAssignedInventoryDTOService.processBulkInventories(inventoriesFile));
  }
}
