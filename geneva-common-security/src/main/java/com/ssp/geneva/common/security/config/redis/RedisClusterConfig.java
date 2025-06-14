package com.ssp.geneva.common.security.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Profile("aws")
@Configuration
@EnableRedisHttpSession
public class RedisClusterConfig {

  @Value("${jetty.session.redis.host}")
  private String host;

  @Value("${jetty.session.redis.port}")
  private int port;

  @Value("${jetty.session.redis.ssl}")
  private boolean useSSL;

  @Value("${jetty.session.redis.maxInactiveIntervalInSeconds:1800}")
  private int maxInactiveIntervalInSeconds;

  @Bean
  public RedisHttpSessionConfiguration redisHttpSessionConfiguration() {
    var redisClusterConfig = new RedisHttpSessionConfiguration();
    redisClusterConfig.setMaxInactiveIntervalInSeconds(maxInactiveIntervalInSeconds);
    return redisClusterConfig;
  }

  @Bean
  @Primary
  public LettuceConnectionFactory redisConnectionFactory() {
    var config = new RedisClusterConfiguration();
    config.clusterNode(new RedisNode(host, port));
    var lettuce = new LettuceConnectionFactory(config);
    if (useSSL) {
      lettuce.setVerifyPeer(false);
      lettuce.setUseSsl(useSSL);
    }
    return lettuce;
  }

  @Bean
  public static ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }
}
