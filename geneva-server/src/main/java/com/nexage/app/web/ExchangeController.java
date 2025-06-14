package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.bidder.model.BDRExchange;
import com.nexage.app.services.ExchangeService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/exchanges")
@RestController
@RequestMapping(value = "/exchanges")
public class ExchangeController {

  private final ExchangeService exchangeService;

  public ExchangeController(ExchangeService exchangeService) {
    this.exchangeService = exchangeService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public List<BDRExchange> getAllExchanges() {
    return exchangeService.findAll();
  }
}
