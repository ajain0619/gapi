package com.nexage.app.services.health;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;

@Log4j2
public class HealthCheckService {

  private final List<HealthService> healthCheckServices;

  public HealthCheckService(List<HealthService> healthCheckServices) {
    this.healthCheckServices = healthCheckServices;
  }

  /**
   * Return a list of {@link HealthStats} for each {@link HealthService}
   *
   * @return {@link List} of {@link HealthStats}
   */
  public List<HealthStats> getHealthStats() {

    List<HealthStats> healthStats = new ArrayList<>();

    for (HealthService service : healthCheckServices) {
      log.debug("Health Stats for service {}", service.getName());
      long time = -1;
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      boolean isAlive = service.isServiceHealthy();
      long elapsed = stopWatch.getTime();
      if (isAlive) {
        time = elapsed;
      }
      HealthStats stat =
          HealthStats.builder().name(service.getName()).alive(isAlive).millis(time).build();
      log.debug("Health Stats {}", stat);
      healthStats.add(stat);
    }

    return healthStats;
  }
}
