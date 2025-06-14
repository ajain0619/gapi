package com.nexage.app.services.impl;

import com.nexage.app.services.SessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@PreAuthorize("@loginUserContext.isOcAdminNexage()")
public class SessionServiceImpl implements SessionService {

  private final RedisTemplate<String, Object> redisTemplate;

  public SessionServiceImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /** {@inheritDoc} */
  @Override
  public void deleteSessions() {

    var sessionKeys = redisTemplate.keys("spring:session:*");
    redisTemplate.delete(sessionKeys);
  }
}
