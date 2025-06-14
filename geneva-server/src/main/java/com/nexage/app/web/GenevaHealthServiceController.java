package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.nexage.app.services.health.CoreDatabaseHealthService;
import com.nexage.app.services.health.HealthCheckService;
import com.nexage.app.services.health.HealthStats;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Geneva Custom Health Check {@link RestController}.
 *
 * @deprecated This custom controller exposes custom behaviour for health check operations. Health
 *     checks should be performed through /actuator config and they will be replaced in a near
 *     future.
 * @see com.nexage.app.config.ActuatorConfig
 */
@Deprecated
@RestController
public class GenevaHealthServiceController {

  private static final Gson gson = new Gson();
  private final HealthCheckService healthCheckService;
  private final CoreDatabaseHealthService coreDatabaseHealthService;

  public GenevaHealthServiceController(
      HealthCheckService healthCheckService, CoreDatabaseHealthService coreDatabaseHealthService) {
    this.healthCheckService = healthCheckService;
    this.coreDatabaseHealthService = coreDatabaseHealthService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/healthCheck")
  public String getHealthStatus() {
    List<HealthStats> stats = healthCheckService.getHealthStats();
    return gson.toJson(stats);
  }

  /**
   * This operation only checks coreDB service (tier 0).
   *
   * @return {@link ResponseEntity} of type {@link Void}.
   * @see CoreDatabaseHealthService
   */
  @Timed
  @ExceptionMetered
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD},
      value = "/awsHealthCheck")
  public ResponseEntity<Void> getHealthStatusForAWS() {
    return (coreDatabaseHealthService.isServiceHealthy())
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }
}
