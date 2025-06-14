package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import com.nexage.app.services.SellerInventoryAttributeValueService;
import com.nexage.app.util.validator.SellerInventoryAttributeValuesValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "/v1/sellers/{sellerPid}/inventory-attributes/{attributePid}/inventory-attribute-values")
@RestController
@RequestMapping(
    value =
        "/v1/sellers/{sellerPid}/inventory-attributes/{attributePid}/inventory-attribute-values")
@Validated
public class SellerInventoryAttributeValueDTOController {

  private final SellerInventoryAttributeValuesValidator validator;

  private final SellerInventoryAttributeValueService sellerInventoryAttributeService;

  public SellerInventoryAttributeValueDTOController(
      SellerInventoryAttributeValuesValidator validator,
      SellerInventoryAttributeValueService sellerInventoryAttributeService) {
    this.validator = validator;
    this.sellerInventoryAttributeService = sellerInventoryAttributeService;
  }

  /**
   * Endpoint to update inventory attribute value for given seller's inventory attribute.
   *
   * @param sellerPid seller PID
   * @param attributePid inventory atrribute PID
   * @param inventoryAttributeValuePid inventory attribute value PID
   * @param inventoryAttributeValueDto DTO containing new values
   * @return
   */
  @Operation(summary = "Update inventory attribute value for seller")
  @Timed
  @ExceptionMetered
  @PutMapping(value = "/{pid}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InventoryAttributeValueDTO> updateInventoryAttributeValue(
      @PathVariable Long sellerPid,
      @PathVariable Long attributePid,
      @PathVariable("pid") Long inventoryAttributeValuePid,
      @RequestBody InventoryAttributeValueDTO inventoryAttributeValueDto) {
    validator.validateForUpdate(
        attributePid, inventoryAttributeValuePid, inventoryAttributeValueDto);
    return ResponseEntity.ok(
        sellerInventoryAttributeService.updateInventoryAttributeValue(
            sellerPid, attributePid, inventoryAttributeValuePid, inventoryAttributeValueDto));
  }

  /**
   * Fetches paged list of all inventory attribute values for given seller and attribute.
   *
   * @param sellerPid seller PID
   * @param attributePid attribute PID
   * @param pageable pageable instance
   * @return paged list of inventory attribute values
   */
  @Operation(summary = "Get all values for inventory attribute")
  @Timed
  @ExceptionMetered
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<InventoryAttributeValueDTO>> getInventoryAttributeValues(
      @PathVariable Long sellerPid, @PathVariable Long attributePid, Pageable pageable) {
    validator.validateForFetch(sellerPid, attributePid);
    return ResponseEntity.ok(
        sellerInventoryAttributeService.getAllValuesForInventoryAttribute(
            sellerPid, attributePid, pageable));
  }

  @Operation(summary = "Create new inventory attribute value")
  @Timed
  @ExceptionMetered
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InventoryAttributeValueDTO> createInvnetoryAttributeValue(
      @PathVariable Long sellerPid,
      @PathVariable Long attributePid,
      @RequestBody InventoryAttributeValueDTO inventoryAttributeValueDto) {
    validator.validateForCreate(sellerPid, attributePid, inventoryAttributeValueDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            sellerInventoryAttributeService.createInventoryAttributeValue(
                sellerPid, attributePid, inventoryAttributeValueDto));
  }
}
