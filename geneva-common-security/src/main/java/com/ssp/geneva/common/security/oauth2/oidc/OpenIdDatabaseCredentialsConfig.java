package com.ssp.geneva.common.security.oauth2.oidc;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@Log4j2
public class OpenIdDatabaseCredentialsConfig implements OpenIdCredentialsConfigHandler {

  private final NamedParameterJdbcTemplate coreNamedJdbcTemplate;

  public OpenIdDatabaseCredentialsConfig(NamedParameterJdbcTemplate coreNamedJdbcTemplate) {
    this.coreNamedJdbcTemplate = coreNamedJdbcTemplate;
  }

  public String getClientId() {
    return getConfigValue(CLIENT_ID_KEY);
  }

  public String getClientSecret() {
    return decrypt(getConfigValue(CLIENT_SECRET_KEY));
  }

  private String getConfigValue(String configKey) {
    String result = "";
    String sql = "SELECT config_value from phonecast_configuration where config_key = :config_key";
    SqlParameterSource namedParameters = new MapSqlParameterSource("config_key", configKey);
    try {
      result = coreNamedJdbcTemplate.queryForObject(sql, namedParameters, String.class);
    } catch (DataAccessException e) {
      log.warn(
          "Unable to find config_value in phonecast_configuration for config_key {}", configKey);
    }
    return result;
  }
}
