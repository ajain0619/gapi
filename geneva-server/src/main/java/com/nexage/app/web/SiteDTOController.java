package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.SiteService;
import com.nexage.app.util.validator.site.SiteQueryParams;
import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "/v1/sites")
@RequestMapping(value = "/v1/sites", produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteDTOController {

  private final SiteService siteService;
  private final BeanValidationService beanValidationService;

  public SiteDTOController(SiteService siteService, BeanValidationService beanValidationService) {
    this.siteService = siteService;
    this.beanValidationService = beanValidationService;
  }

  /**
   * Find {@link Page} of {@link SiteDTO} based on QF and QT values
   *
   * @param qf single query field
   * @param qt list of query terms
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteDTO} instances based on parameters.
   */
  @Operation(summary = "Get All Sites for a given list of Ids")
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<SiteDTO>> getSites(
      @PageableDefault(sort = "pid") Pageable pageable,
      @RequestParam(value = "qf") String qf,
      @RequestParam(value = "qt") Set<Long> qt) {
    return ResponseEntity.ok(siteService.getSites(qf, qt, pageable));
  }

  @Operation(summary = "Get All Sites by multiSearch values")
  @Timed
  @ExceptionMetered
  @GetMapping(params = {"multiSearch"})
  public ResponseEntity<Page<SiteDTO>> getSitesMultiSearchParams(
      @PageableDefault(sort = "pid") Pageable pageable,
      @MultiValueSearchParams SiteQueryParams queryParams) {
    beanValidationService.validate(queryParams);

    Page<SiteDTO> result = siteService.getSites(queryParams, pageable);

    return ResponseEntity.ok(result);
  }
}
