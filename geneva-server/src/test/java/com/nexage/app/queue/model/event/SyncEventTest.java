package com.nexage.app.queue.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SyncEventTest {

  @Test
  void shouldCreateSuccessfully() {
    String primitiveData = "Hello";

    var syncEvent1 = SyncEvent.createOf(primitiveData);

    assertNotNull(syncEvent1);
    assertEquals(primitiveData, syncEvent1.getData());
    assertEquals(SyncEvent.Status.CREATE, syncEvent1.getStatus());

    BigDecimal beanData = BigDecimal.ONE;

    var syncEvent2 = SyncEvent.createOf(beanData);

    assertNotNull(syncEvent2);
    assertEquals(beanData, syncEvent2.getData());
    assertEquals(SyncEvent.Status.CREATE, syncEvent2.getStatus());

    var syncEvent3 = SyncEvent.of(primitiveData, SyncEvent.Status.DELETE);

    assertNotNull(syncEvent3);
    assertEquals(primitiveData, syncEvent3.getData());
    assertEquals(SyncEvent.Status.DELETE, syncEvent3.getStatus());
  }
}
