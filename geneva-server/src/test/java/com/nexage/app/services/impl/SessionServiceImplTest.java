package com.nexage.app.services.impl;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

  @Mock private RedisTemplate redisTemplate;
  @InjectMocks SessionServiceImpl sessionService;

  @Test
  void shouldDeleteSessions() {
    sessionService.deleteSessions();
    verify(redisTemplate, times(1)).delete(anyCollection());
  }
}
