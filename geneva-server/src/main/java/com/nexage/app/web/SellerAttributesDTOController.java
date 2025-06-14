package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.seller.SellerAttributesDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerAttributesDTOService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/sellers/{sellerPid}/seller-attributes")
@RestController
@RequestMapping(
    value = "/v1/sellers/{sellerPid}/seller-attributes",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerAttributesDTOController {

  private final SellerAttributesDTOService sellerAttributesDTOService;

  public SellerAttributesDTOController(SellerAttributesDTOService sellerAttributesDTOService) {
    this.sellerAttributesDTOService = sellerAttributesDTOService;
  }

  /**
   * Get Seller Attribute paginated {@link SellerAttributesDTO}
   *
   * @param sellerPid {@link Long}
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link ResponseEntity} of type {@link SellerAttributesDTO}
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  @Operation(
      summary = "Get Seller Attribute",
      description = "Even though a Page is returned, a seller can have only one seller attribute.")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerAttributesDTO.class)))
  public ResponseEntity<Page<SellerAttributesDTO>> getSellerAttribute(
      @PathVariable("sellerPid") @NotNull Long sellerPid, @PageableDefault Pageable pageable) {
    return ResponseEntity.ok(sellerAttributesDTOService.getSellerAttribute(sellerPid, pageable));
  }

  /**
   * Update an existing Seller Attribute {@link SellerAttributesDTO}
   *
   * @param sellerPid {@link Long}
   * @param sellerAttributesDTO {@link SellerAttributesDTO}
   * @return {@link ResponseEntity} of type {@link SellerAttributesDTO}
   */
  @Timed
  @ExceptionMetered
  @PutMapping
  @Operation(summary = "Update a Seller Attribute")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerAttributesDTO.class)))
  public ResponseEntity<SellerAttributesDTO> updateSellerAttribute(
      @PathVariable("sellerPid") @NotNull Long sellerPid,
      @RequestBody @Validated(value = {Default.class, UpdateGroup.class})
          SellerAttributesDTO sellerAttributesDTO) {
    if (!sellerPid.equals(sellerAttributesDTO.getSellerPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }

    return ResponseEntity.ok(sellerAttributesDTOService.updateSellerAttribute(sellerAttributesDTO));
  }
}
