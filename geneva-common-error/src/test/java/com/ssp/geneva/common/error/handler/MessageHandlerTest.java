package com.ssp.geneva.common.error.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

  @Mock private MessageSource messageSource;

  @InjectMocks private MessageHandler messageHandler;

  @Test
  void shouldReturnExpectedMessageByKey() {
    final String key = "Whatever";
    final String expectedMessage = "Whoever";
    when(messageSource.getMessage(eq(key), any(), any())).thenReturn(expectedMessage);
    assertEquals(expectedMessage, messageHandler.getMessage(key), "Return expected message");
  }

  @Test
  void shouldReturnExpectedMessageByKeyAndArgs() {
    final String key = "Whatever";
    final Object[] args = {};
    final String expectedMessage = "Whoever";
    when(messageSource.getMessage(eq(key), eq(args), any())).thenReturn(expectedMessage);
    assertEquals(expectedMessage, messageHandler.getMessage(key, args), "Return expected message");
  }
}
