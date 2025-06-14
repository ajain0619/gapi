package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.openMocks;

import com.ssp.geneva.common.security.config.redis.RedisClusterConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RedisClusterConfigTest {

  String host = "host";
  int port = 1234;
  boolean useSSL = true;
  int maxInactiveIntervalInSeconds = 1000;

  RedisClusterConfig redisClusterConfig;

  @BeforeEach
  public void before() {
    openMocks(this);
    redisClusterConfig = new RedisClusterConfig();
    ReflectionTestUtils.setField(redisClusterConfig, "host", host);
    ReflectionTestUtils.setField(redisClusterConfig, "port", port);
    ReflectionTestUtils.setField(redisClusterConfig, "useSSL", useSSL);
    ReflectionTestUtils.setField(
        redisClusterConfig, "maxInactiveIntervalInSeconds", maxInactiveIntervalInSeconds);
  }

  @Test
  void testRedisHttpSessionConfiguration() {
    RedisHttpSessionConfiguration config = redisClusterConfig.redisHttpSessionConfiguration();
    assertNotNull(config);
    assertEquals(
        maxInactiveIntervalInSeconds,
        (int) ReflectionTestUtils.getField(config, "maxInactiveIntervalInSeconds"));
  }

  @Test
  void redisConnectionFactoryUseSsl() {
    LettuceConnectionFactory factory = redisClusterConfig.redisConnectionFactory();
    assertNotNull(factory);
    assertEquals(useSSL, factory.isUseSsl());
    assertEquals(!useSSL, factory.isVerifyPeer());

    RedisClusterConfiguration config = factory.getClusterConfiguration();
    assertNotNull(config);

    RedisNode node = config.getClusterNodes().stream().findFirst().get();
    assertNotNull(node);
    assertEquals(host, node.getHost());
    assertEquals(port, node.getPort().intValue());
  }

  @Test
  void redisConnectionFactoryNotUseSsl() {
    ReflectionTestUtils.setField(redisClusterConfig, "useSSL", !useSSL);
    LettuceConnectionFactory factory = redisClusterConfig.redisConnectionFactory();
    assertNotNull(factory);
    assertEquals(!useSSL, factory.isUseSsl());
    assertEquals(useSSL, factory.isVerifyPeer());

    RedisClusterConfiguration config = factory.getClusterConfiguration();
    assertNotNull(config);

    RedisNode node = config.getClusterNodes().stream().findFirst().get();
    assertNotNull(node);
    assertEquals(host, node.getHost());
    assertEquals(port, node.getPort().intValue());
  }
}
