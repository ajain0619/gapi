package com.nexage.app.web.filter;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import com.nexage.app.services.filter.FilterListService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "/v1/buyers/{buyerId}/filter-lists")
@RestController
@RequestMapping(value = "/v1/buyers/{buyerId}/filter-lists")
public class FilterListDTOController {

  private final FilterListService filterListService;

  public FilterListDTOController(FilterListService filterListService) {
    this.filterListService = filterListService;
  }

  /**
   * Create {@link FilterListDTO}
   *
   * @param buyerId {@link Long}
   * @param filterListDTO {@link FilterListDTO}
   * @return {@link ResponseEntity<FilterListDTO>}
   */
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<FilterListDTO> createFilterList(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @RequestBody @Valid FilterListDTO filterListDTO) {
    filterListDTO.setBuyerId(buyerId);
    return ResponseEntity.ok(filterListService.createFilterList(filterListDTO));
  }

  /**
   * Get a single {@link FilterListDTO} by buyerId and pid
   *
   * @param buyerId {@link Long}
   * @param pid {@link Integer}
   * @return {@link ResponseEntity<FilterListDTO>}
   */
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{filterListId}")
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<FilterListDTO> getFilterList(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer pid) {
    return ResponseEntity.ok(filterListService.getFilterList(buyerId, pid));
  }

  /**
   * Get Paginated {@link FilterListDTO} by buyerId and optional filterList name
   *
   * @param buyerId {@link Long}
   * @param pageable {@link Pageable}
   * @param qf {@link Set<String>} query fields
   * @param qt {@link String} query term
   * @return {@link ResponseEntity<Page<FilterListDTO>>}
   */
  @Timed
  @ExceptionMetered
  @GetMapping()
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<Page<FilterListDTO>> getFilterLists(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PageableDefault Pageable pageable,
      @RequestParam(value = "qf") Optional<Set<String>> qf,
      @RequestParam(value = "qt") Optional<String> qt,
      @RequestParam(value = "filterListType") Optional<FilterListTypeDTO> filterListTypeDTO,
      @RequestParam(value = "filterListUploadStatus")
          Optional<FilterListUploadStatusDTO> filterListUploadStatusDTO) {
    return ResponseEntity.ok(
        filterListService.getFilterLists(
            buyerId, pageable, qf, qt, filterListTypeDTO, filterListUploadStatusDTO));
  }

  /**
   * Delete the specified {@link FilterListDTO} with buyerId and pid
   *
   * @param buyerId {@link Long}
   * @param pid {@link Integer}
   * @return {@link ResponseEntity}
   */
  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{filterListId}")
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<FilterListDTO> deleteFilterList(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer pid) {
    return ResponseEntity.ok(filterListService.deleteFilterList(buyerId, pid));
  }

  /**
   * Adds domains in CSV file to the filterList
   *
   * @param buyerId {@link Long}
   * @param pid {@link Integer}
   * @param filterListCsv {@link MultipartFile}
   * @return {@link ResponseEntity<FilterListDTO>}
   */
  @Timed
  @ExceptionMetered
  @PutMapping(value = "/{filterListId}")
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public ResponseEntity<FilterListDTO> addFilterListCsv(
      @PathVariable("buyerId") @NotNull Long buyerId,
      @PathVariable("filterListId") @NotNull Integer pid,
      @RequestParam("filterListCSV") @NotNull MultipartFile filterListCsv) {
    return ResponseEntity.ok(filterListService.addFilterListCsv(buyerId, pid, filterListCsv));
  }
}
