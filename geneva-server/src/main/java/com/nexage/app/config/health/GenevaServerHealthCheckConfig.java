package com.nexage.app.config.health;

import com.nexage.app.services.health.CoreDatabaseHealthService;
import com.nexage.app.services.health.DwDatabaseHealthService;
import com.nexage.app.services.health.HealthCheckService;
import com.nexage.app.services.health.HealthService;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Log4j2
@Configuration
public class GenevaServerHealthCheckConfig {

  public GenevaServerHealthCheckConfig() {
    log.info("geneva.auto-config:GenevaServerHealthCheckConfig");
  }

  @Bean("coreDbHealthCheck")
  @ConditionalOnClass({JdbcTemplate.class})
  public CoreDatabaseHealthService coreDbHealthCheck(@Autowired JdbcTemplate coreJdbcTemplate) {
    return new CoreDatabaseHealthService(coreJdbcTemplate);
  }

  @Bean("dwDbHealthCheck")
  @ConditionalOnClass({JdbcTemplate.class})
  public DwDatabaseHealthService dwDbHealthCheck(@Autowired JdbcTemplate dwJdbcTemplate) {
    return new DwDatabaseHealthService(dwJdbcTemplate);
  }

  @Bean("healthCheckService")
  @ConditionalOnClass({HealthService.class})
  public HealthCheckService healthCheckService(
      @Autowired HealthService coreDbHealthCheck, @Autowired HealthService dwDbHealthCheck) {
    return new HealthCheckService(List.of(coreDbHealthCheck, dwDbHealthCheck));
  }
}
