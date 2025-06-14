package com.nexage.app.queue.producer;

import static com.nexage.app.queue.model.MessageHeadersConst.MESSAGING_TRACE_ID_MDC;
import static com.nexage.app.queue.model.MessageHeadersConst.USERNAME_MDC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import com.nexage.app.queue.model.MessageHeadersConst;
import com.nexage.app.queue.model.SyncMessage;
import com.nexage.app.queue.model.event.SyncEvent;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import org.apache.log4j.MDC;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class BaseSyncProducerTest {

  private static Set<Class<? extends BaseSyncProducer>> syncProducerClasses;

  private static final Pattern UUID_PATTERN =
      Pattern.compile("([a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12})");

  private MessagePublisher messagePublisher = mock(MessagePublisher.class);

  @BeforeAll
  public static void setup() {
    Reflections reflections = new Reflections("com.ssp");
    reflections.merge(new Reflections("com.nexage"));
    syncProducerClasses = reflections.getSubTypesOf(BaseSyncProducer.class);

    assertThat(
        "failed to find BaseSyncProducer subclasses", syncProducerClasses.size() > 1, is(true));
  }

  @Test
  void validateMandatoryHeaders() throws Exception {
    for (Class<? extends BaseSyncProducer> syncProducerClass : syncProducerClasses) {
      BaseSyncProducer syncProducer = getInstance(syncProducerClass);
      Object messagePayloadEntity = validateAndGetMockedGenericArg(syncProducerClass, 0);

      MessageHeaders headers = syncProducer.getHeadersBuilder(messagePayloadEntity).build();

      assertThat(
          String.format(
              "%s.getHeadersBuilder() method must populate [%s] header",
              syncProducerClass.getName(), MessageHeaders.OPERATION_HEADER),
          headers.getOperation(),
          not(emptyOrNullString()));

      assertThat(
          String.format(
              "%s.getHeadersBuilder() must not populate [%s] header, it is designed only for consumed messages",
              syncProducerClass.getName(), MessageHeaders.REPLY_CORRELATION_ID_HEADER),
          headers.getReplyCorrelationId(),
          emptyOrNullString());

      assertThat(
          String.format(
              "%s.getHeadersBuilder() method must populate [%s] header",
              syncProducerClass.getName(), MessageHeaders.ENTITY_ID_HEADER),
          headers.getEntityId(),
          notNullValue());
    }
  }

  private BaseSyncProducer getInstance(Class<? extends BaseSyncProducer> syncProducerClass)
      throws Exception {
    Constructor<? extends BaseSyncProducer> constructor =
        syncProducerClass.getDeclaredConstructor(MessagePublisher.class);
    constructor.setAccessible(true);
    return constructor.newInstance(messagePublisher);
  }

  private Object validateAndGetMockedGenericArg(
      Class<? extends BaseSyncProducer> syncProducerClass, int argIndex) {
    Class clazz =
        (Class<?>)
            ((ParameterizedType) syncProducerClass.getGenericSuperclass())
                .getActualTypeArguments()[argIndex];

    assertThat(
        String.format(
            "messaging infrastructure is based on jpa data objects - current class is not suitable: %s",
            clazz.getName()),
        clazz.getAnnotation(javax.persistence.Entity.class),
        notNullValue());
    return mock(clazz);
  }

  @Test
  void testMdcLog() {
    MDC.clear();
    MockProducer producer = new MockProducer(mock(MessagePublisher.class));

    // before operation
    assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), nullValue());

    producer.publish(null);

    // after operation
    assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), nullValue());

    MessageHeaders headers = producer.headers.build();
    assertThat(headers.getMessagingTraceId(), matchesRegex(UUID_PATTERN));
    assertThat(headers.getUserName(), nullValue());
  }

  @Test
  void testMdcLogWithUsername() {
    MDC.put(USERNAME_MDC, "myUser");
    MockProducer producer = new MockProducer(mock(MessagePublisher.class));

    // before operation
    assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), nullValue());

    producer.publish(null);

    // after operation
    assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), nullValue());

    MessageHeaders headers = producer.headers.build();
    assertThat(headers.getMessagingTraceId(), matchesRegex(UUID_PATTERN));
    assertThat(headers.getUserName(), is("myUser"));
  }

  @Entity
  class MockEntity {}

  class MockSyncMessage implements SyncMessage {}

  static class MockProducer
      extends BaseSyncProducer<MockEntity, SyncEvent<MockEntity>, MockSyncMessage> {

    Logger log = mock(Logger.class);
    MessageHeaders.MessageHeadersBuilder headers =
        MessageHeaders.builder()
            .withOperation(MessageHeadersConst.Operation.CREATE)
            .withEntityId(1L);

    public MockProducer(MessagePublisher messagePublisher) {
      super(messagePublisher);
    }

    @Override
    protected boolean isEnableSync() {
      return true;
    }

    @Override
    protected boolean isValid(MockEntity position) {
      return true;
    }

    @Override
    public void publishEvent(SyncEvent<MockEntity> event) {
      // logger in inner method should contain mdc
      assertThat(MDC.get(MESSAGING_TRACE_ID_MDC), is(headers.build().getMessagingTraceId()));
      assertThat((String) MDC.get(MESSAGING_TRACE_ID_MDC), matchesRegex(UUID_PATTERN));
    }

    @Override
    protected Logger getLogger() {
      return log;
    }

    @Override
    protected Topic getTopic() {
      return null;
    }

    @Override
    protected MockSyncMessage getMessage(MockEntity position) {
      return mock(MockSyncMessage.class);
    }

    @Override
    protected MessageHeaders.MessageHeadersBuilder getHeadersBuilder(MockEntity position) {
      return headers;
    }
  }
}
