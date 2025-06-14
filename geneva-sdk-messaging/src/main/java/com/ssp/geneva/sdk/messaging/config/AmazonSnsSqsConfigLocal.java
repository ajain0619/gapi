package com.ssp.geneva.sdk.messaging.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("messaging-local")
public class AmazonSnsSqsConfigLocal {

  private final AWSStaticCredentialsProvider credentialsProvider =
      new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")); // NOSONAR local logging

  @Bean
  public AmazonSQSAsync amazonSQS(
      @Value("${messaging.sqs.service.endpoint}") String localSqsServiceEndpoint,
      @Value("${messaging.region}") String localRegion) {
    return AmazonSQSAsyncClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(localSqsServiceEndpoint, localRegion))
        .withCredentials(credentialsProvider)
        .build();
  }

  @Bean
  public AmazonSNSAsync amazonSNS(
      @Value("${messaging.sns.service.endpoint}") String localSnsServiceEndpoint,
      @Value("${messaging.region}") String localRegion) {
    return AmazonSNSAsyncClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(localSnsServiceEndpoint, localRegion))
        .withCredentials(credentialsProvider)
        .build();
  }
}
