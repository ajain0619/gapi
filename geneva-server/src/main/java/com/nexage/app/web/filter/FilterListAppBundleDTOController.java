package com.nexage.app.web.filter;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.services.filter.FilterListAppBundleDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/buyers/{buyerId}/filter-lists/{filterListId}/app-bundles")
@RestController
@RequestMapping(value = "/v1/buyers/{buyerId}/filter-lists/{filterListId}/app-bundles")
public class FilterListAppBundleDTOController {

  private final FilterListAppBundleDTOService filterListAppBundleDTOService;

  public FilterListAppBundleDTOController(
      FilterListAppBundleDTOService filterListAppBundleDTOService) {
    this.filterListAppBundleDTOService = filterListAppBundleDTOService;
  }

  /**
   * Delete the specified {@link FilterListDTO} with buyerId and filterListId
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @return {@link ResponseEntity}
   */
  @Operation(summary = "Get list of App bundles for a given Filter List Id")
  @ApiResponse(content = @Content(schema = @Schema(implementation = FilterListAppBundleDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<FilterListAppBundleDTO>> getFilterListAppBundles(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer filterListId,
      @PageableDefault Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {
    return ResponseEntity.ok(
        filterListAppBundleDTOService.getFilterListAppBundles(
            buyerId, filterListId, pageable, qf, qt));
  }

  /**
   * Delete the specified {@link FilterListAppBundleDTO} objects with pids
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @return {@link ResponseEntity}
   */
  @Operation(summary = "Get list of Deleted App bundles for a given Filter List Id")
  @ApiResponse(content = @Content(schema = @Schema(implementation = FilterListAppBundleDTO.class)))
  @Timed
  @ExceptionMetered
  @DeleteMapping
  public ResponseEntity<List<FilterListAppBundleDTO>> deleteFilterListAppBundlePIDs(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer filterListId,
      @RequestBody Set<Integer> filterListAppBundlePIDs) {
    return ResponseEntity.ok(
        filterListAppBundleDTOService.deleteFilterListAppBundles(
            buyerId, filterListId, filterListAppBundlePIDs));
  }
}
