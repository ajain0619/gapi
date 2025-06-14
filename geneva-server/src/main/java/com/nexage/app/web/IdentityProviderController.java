package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.IdentityProviderDTO;
import com.nexage.app.services.IdentityProviderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/dsps/identity-providers", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityProviderController {

  private final IdentityProviderService identityProviderService;

  public IdentityProviderController(IdentityProviderService identityProviderService) {
    this.identityProviderService = identityProviderService;
  }

  @Operation(summary = "Get all Identity Providers")
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<IdentityProviderDTO>> getPagedIdentityProviders(
      @PageableDefault(value = 10) Pageable pageable) {
    return ResponseEntity.ok(identityProviderService.getAllIdentityProviders(pageable));
  }
}
