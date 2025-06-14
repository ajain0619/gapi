package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.RevenueGroupDTO;
import com.nexage.app.services.RevenueGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/revenue-groups")
@RestController
@RequestMapping(value = "/v1/revenue-groups")
@RequiredArgsConstructor
public class RevenueGroupController {

  private final RevenueGroupService revenueGroupService;

  /**
   * GET endpoint to fetch available revenue groups
   *
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link ResponseEntity} of type {@link Page} {@link RevenueGroupDTO}
   */
  @GetMapping
  @Timed
  @ExceptionMetered
  public ResponseEntity<Page<RevenueGroupDTO>> getRevenueGroups(
      @PageableDefault(sort = "pid") Pageable pageable) {
    return ResponseEntity.ok(revenueGroupService.getRevenueGroups(pageable));
  }
}
