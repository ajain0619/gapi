package com.ssp.geneva.sdk.messaging.config;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.sdk.messaging.stub.FilteredQueueListenerStub;
import com.ssp.geneva.sdk.messaging.stub.QueueListenerStub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Configuration
public class TestConfig {

  @Container
  static final LocalStackContainer localStackContainer =
      new LocalStackContainer().withServices(SNS, SQS);

  @Bean(destroyMethod = "stop")
  public LocalStackContainer localStackContainer() {
    localStackContainer.start();
    return localStackContainer;
  }

  @Bean
  public AmazonSNS amazonSNS(LocalStackContainer localStackContainer) {
    return AmazonSNSAsyncClientBuilder.standard()
        .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SNS))
        .withCredentials(localStackContainer.getDefaultCredentialsProvider())
        .build();
  }

  @Bean
  public AmazonSQS amazonSQS(LocalStackContainer localStackContainer) {
    final AmazonSQSAsync amazonSQSAsync =
        AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SQS))
            .withCredentials(localStackContainer.getDefaultCredentialsProvider())
            .build();
    amazonSQSAsync.createQueue("geneva-messaging-dev-queue");
    amazonSQSAsync.createQueue("geneva-messaging-dev-filtered_queue");
    return amazonSQSAsync;
  }

  @Bean
  public QueueListenerStub queueListenerStub() {
    return new QueueListenerStub();
  }

  @Bean
  public FilteredQueueListenerStub filteredQueueListenerStub() {
    return new FilteredQueueListenerStub();
  }

  @Bean(destroyMethod = "stop")
  public GenericContainer redis() {
    final GenericContainer redis = new GenericContainer("redis").withExposedPorts(6379);
    redis.start();
    return redis;
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory(GenericContainer redis) {
    return new LettuceConnectionFactory(
        new RedisStandaloneConfiguration(
            redis.getContainerIpAddress(), redis.getFirstMappedPort()));
  }

  @Bean
  public MetricRegistry metricRegistry() {
    return new MetricRegistry();
  }
}
