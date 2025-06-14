package com.nexage.app.services.health;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class DwDatabaseHealthServiceTest {

  @Mock private JdbcTemplate dwJdbcTemplate;

  @InjectMocks private DwDatabaseHealthService dwDatabaseHealthService;

  @Test
  @SneakyThrows
  void shouldNotBeHealthy() {
    DataAccessException exception = mock(DataAccessException.class);
    when(exception.getMessage()).thenReturn("Alarm! Service is not available!");
    doThrow(exception).when(dwJdbcTemplate).queryForList(anyString());
    assertFalse(dwDatabaseHealthService.isServiceHealthy());
  }

  @Test
  void shouldBeHealthy() {
    when(dwJdbcTemplate.queryForList(anyString())).thenReturn(Collections.emptyList());
    assertTrue(dwDatabaseHealthService.isServiceHealthy());
  }
}
