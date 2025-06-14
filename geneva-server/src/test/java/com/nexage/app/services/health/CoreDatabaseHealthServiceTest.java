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
class CoreDatabaseHealthServiceTest {

  @Mock private JdbcTemplate coreJdbcTemplate;

  @InjectMocks private CoreDatabaseHealthService coreDatabaseHealthService;

  @Test
  @SneakyThrows
  void shouldNotBeHealthy() {
    DataAccessException exception = mock(DataAccessException.class);
    when(exception.getMessage()).thenReturn("Alarm! Service is not available!");
    doThrow(exception).when(coreJdbcTemplate).queryForList(anyString());
    assertFalse(coreDatabaseHealthService.isServiceHealthy());
  }

  @Test
  void shouldBeHealthy() {
    when(coreJdbcTemplate.queryForList(anyString())).thenReturn(Collections.emptyList());
    assertTrue(coreDatabaseHealthService.isServiceHealthy());
  }
}
