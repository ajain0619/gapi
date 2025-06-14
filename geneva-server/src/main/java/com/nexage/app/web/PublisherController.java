package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.ExchangePublisherDTO;
import com.nexage.app.dto.ExchangeSiteDTO;
import com.nexage.app.services.PublisherService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
public class PublisherController {

  private final PublisherService publisherService;

  public PublisherController(PublisherService publisherService) {
    this.publisherService = publisherService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publishers")
  public List<ExchangePublisherDTO> getExchangePublishers() {
    return publisherService.getExchangePublishers();
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publishers/sites")
  public List<ExchangeSiteDTO> getExchangeSites() {
    return publisherService.getExchangeSites();
  }
}
