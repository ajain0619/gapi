package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.ExchangeRateDTO;
import com.nexage.app.services.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "/v1/exchange-rates")
@RequestMapping(value = "/v1/exchange-rates", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExchangeRateController {

  private final ExchangeRateService exchangeRateService;

  public ExchangeRateController(ExchangeRateService exchangeRateService) {
    this.exchangeRateService = exchangeRateService;
  }

  @Operation(
      summary =
          "Get all exchange rates or all latest exchanges rates or all exchange rates by currency or latest exchange rate by currency")
  @ApiResponse(content = @Content(schema = @Schema(implementation = ExchangeRateDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<ExchangeRateDTO>> getAllExchangeRates(
      @PageableDefault
          @SortDefault.SortDefaults({
            @SortDefault(sort = "forexId", direction = Sort.Direction.DESC),
            @SortDefault(sort = "id.currency", direction = Sort.Direction.ASC)
          })
          Pageable pageable,
      @RequestParam(value = "qf", required = false) String qf,
      @RequestParam(value = "qt", required = false) String qt,
      @RequestParam(value = "latest", required = false, defaultValue = "true") Boolean latest) {

    return ResponseEntity.ok(exchangeRateService.getAllExchangeRates(qf, qt, pageable, latest));
  }
}
