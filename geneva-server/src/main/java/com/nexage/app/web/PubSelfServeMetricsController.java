package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.nexage.app.dto.publisher.PublisherMetricsDTO;
import com.nexage.app.dto.publisher.PublisherMetricsViewDTO;
import com.nexage.app.services.PubSelfServeMetricsService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/publisher/{pubPID}")
@RestController
@RequestMapping(value = "/publisher/{pubPID}")
public class PubSelfServeMetricsController {

  private final PubSelfServeMetricsService publisherMetricsService;

  public PubSelfServeMetricsController(PubSelfServeMetricsService publisherMetricsService) {
    this.publisherMetricsService = publisherMetricsService;
  }

  @Timed
  @ExceptionMetered
  @JsonView(PublisherMetricsViewDTO.Summary.class)
  @GetMapping(value = "/metrics/summarychart")
  public PublisherMetricsDTO getMetrics(
      @PathVariable(value = "pubPID") long pubPid,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String startDate,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String endDate,
      @RequestParam(value = "interval", required = false) String interval) {

    return publisherMetricsService.getMetrics(pubPid, startDate, endDate, interval);
  }

  @Timed
  @ExceptionMetered
  @JsonView(PublisherMetricsViewDTO.SummaryWithClicks.class)
  @GetMapping(value = "/adSourceMetrics")
  public PublisherMetricsDTO getAdSourceMetrics(
      @PathVariable(value = "pubPID") long pubPid,
      @RequestParam(value = "adsource", required = true) long adSourceId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String startDate,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String endDate,
      @RequestParam(value = "interval", required = false) String interval) {

    return publisherMetricsService.getAdSourceMetrics(
        pubPid, adSourceId, null, null, null, startDate, endDate, interval);
  }

  @Timed
  @ExceptionMetered
  @JsonView(PublisherMetricsViewDTO.SummaryWithClicks.class)
  @GetMapping(value = "/site/{siteId}/adSourceMetrics")
  public PublisherMetricsDTO getAdSourceMetrics(
      @PathVariable(value = "pubPID") long pubPid,
      @PathVariable(value = "siteId") long sitePid,
      @RequestParam(value = "adsource", required = true) long adSourceId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String startDate,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String endDate,
      @RequestParam(value = "interval", required = false) String interval) {

    return publisherMetricsService.getAdSourceMetrics(
        pubPid, adSourceId, sitePid, null, null, startDate, endDate, interval);
  }

  @Timed
  @ExceptionMetered
  @JsonView(PublisherMetricsViewDTO.SummaryWithClicks.class)
  @GetMapping(value = "/site/{siteId}/placement/{placement}/adSourceMetrics")
  public PublisherMetricsDTO getAdSourceMetrics(
      @PathVariable(value = "pubPID") long pubPid,
      @PathVariable(value = "siteId") long sitePid,
      @PathVariable(value = "placement") String position,
      @RequestParam(value = "adsource", required = true) long adSourceId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String startDate,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String endDate,
      @RequestParam(value = "interval", required = false) String interval) {
    return publisherMetricsService.getAdSourceMetrics(
        pubPid, adSourceId, sitePid, position, null, startDate, endDate, interval);
  }

  @Timed
  @ExceptionMetered
  @JsonView(PublisherMetricsViewDTO.SummaryWithClicks.class)
  @GetMapping(value = "/site/{siteId}/placement/{placement}/tag/{tagId}/adSourceMetrics")
  public PublisherMetricsDTO getAdSourceMetrics(
      @PathVariable(value = "pubPID") long pubPid,
      @PathVariable(value = "siteId") long sitePid,
      @PathVariable(value = "placement") String position,
      @PathVariable(value = "tagId") long tagPid,
      @RequestParam(value = "adsource", required = true) long adSourceId,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String startDate,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String endDate,
      @RequestParam(value = "interval", required = false) String interval) {
    return publisherMetricsService.getAdSourceMetrics(
        pubPid, adSourceId, sitePid, position, tagPid, startDate, endDate, interval);
  }
}
