package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.deal.DealCacheService;
import com.ssp.geneva.common.base.annotation.Legacy;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@RestController
public class DealCacheController {

  private final DealCacheService dealCacheService;

  public DealCacheController(DealCacheService dealCacheService) {
    this.dealCacheService = dealCacheService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/dealCacheRefreshAll")
  @ResponseBody
  public boolean refreshDealCache() {
    boolean status = dealCacheService.refreshCache();
    log.info("deal cache refresh status: {}", status);
    return status;
  }
}
