package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.IsoLanguageDTO;
import com.nexage.app.services.IsoLanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(value = "/v1/iso-languages", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IsoLanguageController {
  private final IsoLanguageService isoLanguageService;

  @Timed
  @ExceptionMetered
  @GetMapping
  @Operation(summary = "Get list of ISO Languages")
  @ApiResponse(content = @Content(schema = @Schema(implementation = IsoLanguageDTO.class)))
  public ResponseEntity<Page<IsoLanguageDTO>> getIsoLanguages(
      @Parameter(name = "Query term for search") @RequestParam(value = "qt", required = false)
          String qt,
      @Parameter(name = "Query field for search") @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @PageableDefault(value = 10, sort = "pid") Pageable pageable) {
    return ResponseEntity.ok(isoLanguageService.findAll(qt, qf, pageable));
  }
}
