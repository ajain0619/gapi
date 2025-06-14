package com.nexage.app.services.health;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Log4j2
public class DwDatabaseHealthService implements HealthService {

  private static final String NAME = "dwDB";
  private static final String QUERY = "SELECT 1 FROM dual";
  private final JdbcTemplate dwJdbcTemplate;

  @Autowired
  public DwDatabaseHealthService(final JdbcTemplate dwJdbcTemplate) {
    this.dwJdbcTemplate = dwJdbcTemplate;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isServiceHealthy() {
    try {
      dwJdbcTemplate.queryForList(QUERY);
    } catch (Exception e) {
      log.error("Exception checking service availability {}", e.getMessage());
      return false;
    }
    return true;
  }
}
