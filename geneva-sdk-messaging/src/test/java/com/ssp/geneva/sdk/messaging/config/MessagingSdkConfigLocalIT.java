package com.ssp.geneva.sdk.messaging.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import com.codahale.metrics.MetricRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("messaging-local")
@ContextConfiguration(
    classes = {
      MessagingSdkConfig.class,
      MessagingSdkConfigLocalIT.Config.class,
      AmazonSnsSqsConfigLocal.class
    })
@TestPropertySource("classpath:application-test.properties")
class MessagingSdkConfigLocalIT {

  @Autowired private ApplicationContext context;

  @Test
  void testAmazonLocalServicesUp() {
    final AmazonSQS amazonSQS = context.getBean(AmazonSQS.class);
    final AmazonSNS amazonSNS = context.getBean(AmazonSNS.class);

    assertNotNull(amazonSNS);
    assertNotNull(amazonSQS);
  }

  @Configuration
  static class Config {
    @Bean
    MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
