package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

class AsyncConfigTest {
  @Test
  void testThreadPoolTaskExecutor() {
    Executor executor = new AsyncConfig().threadPoolTaskExecutor();
    assertTrue(executor instanceof DelegatingSecurityContextAsyncTaskExecutor);
  }
}
