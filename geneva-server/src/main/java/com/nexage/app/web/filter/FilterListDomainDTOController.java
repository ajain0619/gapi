package com.nexage.app.web.filter;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListDomainDTO;
import com.nexage.app.services.filter.FilterListDomainDTOService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/buyers/{buyerId}/filter-lists/{filterListId}/domains")
@RestController
@RequestMapping(value = "/v1/buyers/{buyerId}/filter-lists/{filterListId}/domains")
public class FilterListDomainDTOController {

  private final FilterListDomainDTOService filterListDomainDTOService;

  public FilterListDomainDTOController(FilterListDomainDTOService filterListDomainDTOService) {
    this.filterListDomainDTOService = filterListDomainDTOService;
  }

  /**
   * Delete the specified {@link FilterListDTO} with buyerId and filterListId
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @return {@link ResponseEntity}
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<Page<FilterListDomainDTO>> getFilterListDomains(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer filterListId,
      @PageableDefault Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {
    return ResponseEntity.ok(
        filterListDomainDTOService.getFilterListDomains(buyerId, filterListId, pageable, qf, qt));
  }

  /**
   * Delete the specified {@link FilterListDomainDTO} objects with pids
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @return {@link ResponseEntity}
   */
  @Timed
  @ExceptionMetered
  @DeleteMapping
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<List<FilterListDomainDTO>> deleteFilterListDomainPIDs(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer filterListId,
      @RequestBody Set<Integer> filterListDomainPIDs) {
    return ResponseEntity.ok(
        filterListDomainDTOService.deleteFilterListDomains(
            buyerId, filterListId, filterListDomainPIDs));
  }
}
