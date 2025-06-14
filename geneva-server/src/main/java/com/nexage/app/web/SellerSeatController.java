package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.SellerSeatService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/seller-seats")
@RestController
@RequestMapping(value = "/v1/seller-seats", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerSeatController {

  private final SellerSeatService sellerSeatService;
  private final BeanValidationService beanValidationService;

  public SellerSeatController(
      SellerSeatService sellerSeatService, BeanValidationService beanValidationService) {
    this.sellerSeatService = sellerSeatService;
    this.beanValidationService = beanValidationService;
  }

  /**
   * Retrieve a single seller seat by its ID
   *
   * @param sellerSeatPid seller seat's ID
   * @return requested {@link SellerSeat} instance
   */
  @Operation(summary = "Get a single seller seat by its ID")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping("/{sellerSeatPid}")
  public ResponseEntity<SellerSeatDTO> findById(@PathVariable Long sellerSeatPid) {
    SellerSeatDTO sellerSeatDTO = sellerSeatService.getSellerSeat(sellerSeatPid);
    return ResponseEntity.ok(sellerSeatDTO);
  }

  @Operation(summary = "Update a single seller seat by its ID")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping("/{sellerSeatPid}")
  public ResponseEntity<SellerSeatDTO> updateById(
      @PathVariable Long sellerSeatPid, @RequestBody SellerSeatDTO sellerSeat) {
    beanValidationService.validate(sellerSeat);
    if (!sellerSeatPid.equals(sellerSeat.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND);
    }
    return ResponseEntity.ok(sellerSeatService.updateSellerSeat(sellerSeatPid, sellerSeat));
  }

  @Operation(summary = "Create a single seller seat")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping
  public ResponseEntity<SellerSeatDTO> createSellerSeat(@RequestBody SellerSeatDTO sellerSeat) {
    beanValidationService.validate(sellerSeat);
    SellerSeatDTO sellerSeatDTO = sellerSeatService.createSellerSeat(sellerSeat);
    return ResponseEntity.ok(sellerSeatDTO);
  }

  @Operation(summary = "Get all seller seats")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<SellerSeatDTO>> findAll(
      @RequestParam(defaultValue = "false") boolean assignable,
      @RequestParam(required = false, name = "qf") Set<String> queryFields,
      @RequestParam(required = false, name = "qt") String queryTerm,
      @PageableDefault(sort = "name") Pageable pageable) {
    return ResponseEntity.ok(
        sellerSeatService.findAll(assignable, queryFields, queryTerm, pageable));
  }
}
