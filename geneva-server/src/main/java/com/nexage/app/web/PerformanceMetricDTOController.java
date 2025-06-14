package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.PerformanceMetricDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/performance-metrics")
@RestController
@RequestMapping(value = "/v1/performance-metrics")
public class PerformanceMetricDTOController {

  /**
   * POST Method: Post Performance Metrics to the logs {@link PerformanceMetricDTO}. Log the
   * performance metrics that come from the UI.
   */
  @Timed
  @ExceptionMetered
  @PostMapping
  public ResponseEntity<PerformanceMetricDTO> createPerformanceMetrics(
      @RequestBody @NotNull @Valid PerformanceMetricDTO performanceMetricDTO) {
    log.warn(performanceMetricDTO.toString());
    return ResponseEntity.ok(performanceMetricDTO);
  }
}
