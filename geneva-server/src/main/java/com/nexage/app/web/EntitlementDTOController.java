package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.EntitlementService;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/entitlements")
@RestController
@RequestMapping(value = "/v1/entitlements")
public class EntitlementDTOController {

  private final EntitlementService entitlementService;

  @Autowired
  public EntitlementDTOController(EntitlementService entitlementService) {
    this.entitlementService = entitlementService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<Entitlement>> getCurrentUserEntitlements(
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @PageableDefault(value = 10) Pageable pageable) {
    return ResponseEntity.ok(entitlementService.getEntitlements(qt, qf, pageable));
  }
}
