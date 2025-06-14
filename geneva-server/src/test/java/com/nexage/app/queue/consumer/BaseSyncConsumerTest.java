package com.nexage.app.queue.consumer;

import static com.nexage.app.queue.model.MessageHeadersConst.MESSAGING_TRACE_ID_MDC;
import static com.nexage.app.queue.model.MessageHeadersConst.USERNAME_MDC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import com.nexage.app.queue.model.SyncMessage;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.MDC;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

class BaseSyncConsumerTest {

  private static final List<Class> REQUIRED_ARG_ANNOTATIONS =
      Stream.of(Payload.class, Headers.class).collect(Collectors.toList());
  private static Set<Class<? extends BaseSyncConsumer>> syncConsumerClasses;

  @BeforeAll
  public static void setup() {
    Reflections reflections = new Reflections("com.ssp");
    reflections.merge(new Reflections("com.nexage"));
    syncConsumerClasses = reflections.getSubTypesOf(BaseSyncConsumer.class);

    assertThat(
        "failed to find BaseSyncConsumer subclasses", syncConsumerClasses.size() > 1, is(true));
  }

  @Test
  void validateConsumerAnnotations() {
    for (Class<? extends BaseSyncConsumer> syncConsumerClass : syncConsumerClasses) {
      validateMessagingArgAnnotationsExist(syncConsumerClass);
    }
  }

  private void validateMessagingArgAnnotationsExist(
      Class<? extends BaseSyncConsumer> syncConsumerClass) {
    boolean methodWithRequiredAnnotationsExists = false;
    Method[] consumerMethods = syncConsumerClass.getDeclaredMethods();
    for (Method method : consumerMethods) {
      Annotation[][] annotations = method.getParameterAnnotations();
      List<Class> argsAnnotations =
          Stream.of(method.getParameterAnnotations())
              .flatMap(x -> Stream.of(x))
              .map(an -> an.annotationType())
              .collect(Collectors.toList());
      if (argsAnnotations.containsAll(REQUIRED_ARG_ANNOTATIONS)) {
        methodWithRequiredAnnotationsExists = true;
        break;
      }
    }
    assertThat(
        String.format(
            "consumer %s should have a method with args annotated with %s",
            syncConsumerClass.getName(), REQUIRED_ARG_ANNOTATIONS),
        methodWithRequiredAnnotationsExists,
        is(true));
  }

  @Test
  void testMdcLog() {
    MockConsumer consumer = new MockConsumer();

    // before operation
    assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), nullValue());
    assertThat(MDC.get(USERNAME_MDC), nullValue());

    MessageHeaders headers =
        MessageHeaders.builder()
            .withMessagingTraceId(UUID.randomUUID().toString())
            .withUserName("myUser")
            .build();
    consumer.doProcess(null, headers.toMap());

    // after operation
    assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), nullValue());
    assertThat(MDC.get(USERNAME_MDC), nullValue());
  }

  static class MockConsumer extends BaseSyncConsumer<SyncMessage> {

    Logger log = mock(Logger.class);

    @Override
    void processMessage(@Payload SyncMessage message, @Headers Map<String, Object> headers) {
      // logger in inner method should contain mdc
      assertThat(
          MDC.get(MESSAGING_TRACE_ID_MDC), is(headers.get(MessageHeaders.MESSAGING_TRACE_HEADER)));
      assertThat(MDC.get(USERNAME_MDC), is(headers.get(MessageHeaders.USERNAME_HEADER)));
    }

    @Override
    Logger getLogger() {
      return log;
    }

    @Override
    void process(SyncMessage message, Map<String, Object> headers) {}

    @Override
    protected boolean isSyncEnabled() {
      return true;
    }
  }
}
