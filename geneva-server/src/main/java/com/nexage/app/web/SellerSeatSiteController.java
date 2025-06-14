package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.services.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/seller-seats")
@RestController
@RequestMapping(
    value = "/v1/seller-seats/{sellerSeatPid}/sites",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SellerSeatSiteController {

  private final SiteService siteService;

  @Operation(summary = "Get sites for a seller seat")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SiteDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<SiteDTO>> getSites(
      @PageableDefault Pageable pageable,
      @PathVariable(value = "sellerSeatPid") long sellerSeatPid,
      @Parameter(name = "The term that is being searched") @RequestParam("qt") Optional<String> qt,
      @Parameter(
              name =
                  "The terms we want to filter on. Allowed values are MOBILE_WEB, ANDROID, IOS, DESKTOP, WEBSITE, CTV_OTT",
              content =
                  @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
          @RequestParam(value = "siteType")
          Optional<List<String>> siteType,
      @Parameter(name = "The status, can be one of DELETED, INACTIVE, ACTIVE")
          @RequestParam(name = "status")
          Optional<List<String>> status,
      @Parameter(
              name = "Fetch type 'limited' returns filtered fields. If empty, returns all fields")
          @RequestParam(name = "fetch")
          Optional<String> fetch,
      @Parameter(name = "Requests only minimal site data to be returned.")
          @RequestParam(name = "minimal", defaultValue = "false")
          Optional<Boolean> minimal) {
    if (minimal.orElse(false)) {
      return ResponseEntity.ok(
          siteService.getSiteMinimalDataForSellerSeat(sellerSeatPid, qt.orElse(""), pageable));
    }
    return ResponseEntity.ok(
        siteService.getSitesForSellerSeat(sellerSeatPid, pageable, qt, siteType, status, fetch));
  }
}
