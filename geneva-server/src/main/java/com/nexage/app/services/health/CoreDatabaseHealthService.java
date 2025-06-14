package com.nexage.app.services.health;

import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Log4j2
public class CoreDatabaseHealthService implements HealthService {

  private static final String NAME = "coreDB";
  private static final String QUERY = "SELECT 1";
  private final JdbcTemplate coreJdbcTemplate;

  @Autowired
  public CoreDatabaseHealthService(final JdbcTemplate coreJdbcTemplate) {
    this.coreJdbcTemplate = coreJdbcTemplate;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isServiceHealthy() {
    try {
      List<Map<String, Object>> results = coreJdbcTemplate.queryForList(QUERY);
      log.debug("Results={}", results);
    } catch (Exception e) {
      log.error("Exception checking service availability {}", e.getMessage());
      return false;
    }
    return true;
  }
}
