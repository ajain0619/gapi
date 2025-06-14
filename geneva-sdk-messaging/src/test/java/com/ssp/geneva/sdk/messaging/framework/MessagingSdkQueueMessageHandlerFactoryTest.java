package com.ssp.geneva.sdk.messaging.framework;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@ExtendWith(MockitoExtension.class)
class MessagingSdkQueueMessageHandlerFactoryTest {

  @Spy
  private MessagingSdkProperties messagingSdkProperties =
      MessagingSdkProperties.builder().messagingPrefix("dev").build();

  @Mock private MappingJackson2MessageConverter mappingJackson2MessageConverter;

  @Mock private AmazonSQSAsync amazonSQS;

  @InjectMocks
  private MessagingSdkQueueMessageHandlerFactory messagingSdkQueueMessageHandlerFactory;

  @Test
  void testCreateQueueMessageHandlerSuccessfully() {
    messagingSdkQueueMessageHandlerFactory.setAmazonSqs(amazonSQS);
    final QueueMessageHandler queueMessageHandler =
        messagingSdkQueueMessageHandlerFactory.createQueueMessageHandler();

    assertTrue(queueMessageHandler instanceof MessagingSdkQueueMessageHandler);
  }
}
