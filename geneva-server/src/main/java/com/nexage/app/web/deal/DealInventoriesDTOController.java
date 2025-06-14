package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.services.deal.DealInventoriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Produces;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Tag(name = "/v1/deals/inventories")
@RestController
@RequestMapping(value = "/v1/deals/inventories")
public class DealInventoriesDTOController {

  private final DealInventoriesService dealInventoriesService;

  public DealInventoriesDTOController(DealInventoriesService dealInventoriesService) {
    this.dealInventoriesService = dealInventoriesService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Saves CSV/XLS/XLSX file containing Domains, App bundles, App Alias to S3")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DealInventoriesDTO.class)))
  public ResponseEntity<DealInventoriesDTO> uploadDealInventoriesFile(
      @RequestParam("fileName") @NotNull String fileName,
      @RequestParam("fileType") @NotNull DealInventoryType fileType,
      @RequestParam("inventoriesFile") MultipartFile inventoriesFile,
      @RequestParam("dealId") Long dealId) {

    return new ResponseEntity<>(
        dealInventoriesService.uploadDealInventories(fileName, fileType, inventoriesFile, dealId),
        HttpStatus.CREATED);
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  @Produces({MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @Operation(summary = "downloads XLS file containing Domains, App bundles, App Alias")
  @ApiResponse(content = @Content(schema = @Schema(implementation = ByteArrayResource.class)))
  public ResponseEntity<ByteArrayResource> downloadDealInventoriesFile(
      @RequestParam("dealPid") @NotNull long dealPid,
      @RequestParam("filePid") @NotNull long filePid) {

    var dealInventoryDownloadResponseDTO =
        dealInventoriesService.downloadDealInventories(filePid, dealPid);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment;filename=" + dealInventoryDownloadResponseDTO.getFileName())
        .contentLength(
            dealInventoryDownloadResponseDTO.getInventoryFileByteResource().contentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(dealInventoryDownloadResponseDTO.getInventoryFileByteResource());
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping
  @Operation(summary = "Deletes a Deal Inventory file by fileId")
  @ApiResponse(responseCode = "204", description = "No Content")
  public ResponseEntity<Void> deleteDealInventoriesFile(
      @RequestParam("filePid") @NotNull long filePid, @RequestParam("dealId") Long dealId) {

    dealInventoriesService.deleteDealInventories(filePid, dealId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
