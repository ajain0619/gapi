package com.ssp.geneva.common.security.oauth2.oidc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@ExtendWith(MockitoExtension.class)
class OpenIdDatabaseCredentialsConfigTest {

  @Mock NamedParameterJdbcTemplate coreNamedJdbcTemplate;

  @InjectMocks OpenIdDatabaseCredentialsConfig openIDDatabaseCredentialsConfig;

  @Test
  void shouldReturnClientId() {
    final String clientId = UUID.randomUUID().toString();
    when(coreNamedJdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenReturn(clientId);
    assertEquals(openIDDatabaseCredentialsConfig.getClientId(), clientId);
  }

  @Test
  void shouldReturnClientSecret() {
    final String encodedClientSecret = "N1/S53HYmjDgO0m01olhAPsh0HJOTUNYtUurVSeQGdU=";
    final String decodedClientSecret = "lwP6tFEXZ9uu2S720OW69A";
    when(coreNamedJdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenReturn(encodedClientSecret);
    assertEquals(openIDDatabaseCredentialsConfig.getClientSecret(), decodedClientSecret);
  }

  @Test
  void shouldFailWithInvalidClientSecret() {
    String randomEncryptedValue = UUID.randomUUID().toString();
    var exception =
        assertThrows(
            GenevaAppRuntimeException.class,
            () -> {
              openIDDatabaseCredentialsConfig.decrypt(randomEncryptedValue);
            });

    assertEquals(CommonErrorCodes.COMMON_CRYPTO_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldReturnEmptyClientId() {
    DataAccessException exception = mock(DataAccessException.class);
    when(coreNamedJdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenThrow(exception);
    assertEquals("", openIDDatabaseCredentialsConfig.getClientId());
  }

  @Test
  void shouldReturnEmptyClientSecret() {
    DataAccessException exception = mock(DataAccessException.class);
    when(coreNamedJdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenThrow(exception);
    assertEquals("", openIDDatabaseCredentialsConfig.getClientSecret());
  }
}
