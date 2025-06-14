package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.pub.self.serve.PubSelfServeDashboardMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeMediationRuleMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeSiteMetrics;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.PubSelfServeSummaryService;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.text.ParseException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@RestController
public class PubSelfServeSummaryController {

  private final PubSelfServeSummaryService service;

  public PubSelfServeSummaryController(PubSelfServeSummaryService service) {
    this.service = service;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{pub}/dashboardsummary")
  public PubSelfServeDashboardMetricsDTO getDashboardSummary(
      @PathVariable(value = "pub") long pubId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String start,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String stop) {
    StopWatch watch = new StopWatch();
    watch.start();
    try {
      return service.getDashboardSummary(pubId, ISO8601Util.parse(start), ISO8601Util.parse(stop));
    } catch (ParseException e) {
      log.error("Invalid dates: {} {} {}", e, start, stop);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
    } finally {
      watch.stop();
      log.debug("Get dashboard metrics: {}", watch.getTime());
    }
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value = "/publisher/{pub}/tagsummary",
      params = {"start", "stop"})
  public PubSelfServeMediationRuleMetricsDTO getTagSummary(
      @PathVariable(value = "pub") long pubId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String start,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String stop) {
    StopWatch watch = new StopWatch();
    watch.start();
    try {
      return service.getTagSummary(pubId, ISO8601Util.parse(start), ISO8601Util.parse(stop));
    } catch (ParseException e) {
      log.error("Invalid dates: {} {} {}", e, start, stop);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
    } finally {
      watch.stop();
      log.debug("Get tag metrics: {}", watch.getTime());
    }
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value = "/publisher/{pub}/tagsummary",
      params = {"start", "stop", "site"})
  public PubSelfServeSiteMetrics getTagSummaryForSite(
      @PathVariable(value = "pub") long pubId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String start,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String stop,
      @RequestParam(value = "site", required = true) Long site) {
    StopWatch watch = new StopWatch();
    watch.start();
    try {
      return service.getTagSummary(pubId, site, ISO8601Util.parse(start), ISO8601Util.parse(stop));
    } catch (ParseException e) {
      log.error("Invalid dates: {} {} {}", e, start, stop);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
    } finally {
      watch.stop();
      log.debug("Get tag metrics for site: {}", watch.getTime());
    }
  }
}
