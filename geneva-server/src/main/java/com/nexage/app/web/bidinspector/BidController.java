package com.nexage.app.web.bidinspector;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.util.validator.BidInspectorQueryFieldParams;
import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import com.ssp.geneva.server.bidinspector.services.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/bids")
@RestController
@RequestMapping(value = "/v1/bids")
@RequiredArgsConstructor
public class BidController {

  private final BeanValidationService beanValidationService;
  private final BidService bidService;

  @Timed
  @ExceptionMetered
  @Operation(
      summary = "Get auction details for given search params",
      description =
          "Optional filtering arguments can be passed as QueryField qf={field1=term1,field2=term2}")
  @ApiResponse(content = @Content(schema = @Schema(implementation = BidDTO.class)))
  @GetMapping
  @Parameter(name = "qf", required = false)
  public ResponseEntity<Page<BidDTO>> getBidInspectorData(
      @MultiValueSearchParams BidInspectorQueryFieldParams queryParams,
      @PageableDefault(value = 10, sort = "start", direction = Sort.Direction.DESC)
          Pageable pageable) {
    beanValidationService.validate(queryParams);
    return ResponseEntity.ok(bidService.getBidDetails(queryParams, pageable));
  }

  @Timed
  @ExceptionMetered
  @Operation(
      summary = "Get auction details for given search params",
      description =
          "Optional filtering arguments can be passed as QueryField qf={field1=term1,field2=term2}")
  @ApiResponse(content = @Content(schema = @Schema(implementation = BidDTO.class)))
  @GetMapping(value = "/{auctionRunHashId}/auction-details")
  @Parameter(name = "qf", required = false)
  public ResponseEntity<Page<AuctionDetailDTO>> getAuctionDetailsData(
      @MultiValueSearchParams BidInspectorQueryFieldParams queryParams,
      @PathVariable String auctionRunHashId,
      @PageableDefault(value = 50, sort = "start", direction = Sort.Direction.DESC)
          Pageable pageable) {
    beanValidationService.validate(queryParams);
    return ResponseEntity.ok(bidService.getAuctionDetails(queryParams, auctionRunHashId, pageable));
  }
}
