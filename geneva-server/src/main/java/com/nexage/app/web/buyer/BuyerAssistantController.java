package com.nexage.app.web.buyer;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.buyer.BuyerTrafficConfigDTO;
import com.nexage.app.services.BuyerAssistantService;
import com.nexage.app.services.BuyerAssistantService.MetricInterval;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/buyer")
@RestController
@RequestMapping(value = "/buyer")
public class BuyerAssistantController {

  private final BuyerAssistantService buyerAssistantService;

  public BuyerAssistantController(BuyerAssistantService buyerAssistantService) {
    this.buyerAssistantService = buyerAssistantService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{companyPid}/metrics/business")
  public ResponseEntity<Map<String, Object>> getBusinessMetrics(
      @PathVariable(value = "companyPid") long companyPid,
      @RequestParam(value = "interval", required = false) MetricInterval interval) {

    if (interval == null) interval = MetricInterval.today;

    return new ResponseEntity<Map<String, Object>>(
        buyerAssistantService.getBusinessMetrics(companyPid, interval), HttpStatus.OK);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{companyPid}/config")
  public BuyerTrafficConfigDTO getBuyerTrafficConfig(
      @PathVariable(value = "companyPid") long companyPid) {

    return buyerAssistantService.getBuyerTrafficConfig(companyPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{companyPid}/config",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public BuyerTrafficConfigDTO updateBuyerTrafficConfig(
      @PathVariable(value = "companyPid") long companyPid,
      @RequestBody BuyerTrafficConfigDTO buyerTrafficConfig) {

    return buyerAssistantService.updateBuyerTrafficConfig(companyPid, buyerTrafficConfig);
  }
}
