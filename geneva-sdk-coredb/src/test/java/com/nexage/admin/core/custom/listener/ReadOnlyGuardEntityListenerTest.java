package com.nexage.admin.core.custom.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReadOnlyGuardEntityListenerTest {

  private ReadOnlyGuardEntityListener listener;
  private Object entity;

  @BeforeEach
  void setup() {
    listener = new ReadOnlyGuardEntityListener();
    entity = new Object();
  }

  @Test
  void shouldThrowException_whenPersistAttempted() {
    var ex = assertThrows(UnsupportedOperationException.class, () -> listener.prePersist(entity));

    assertEquals("java.lang.Object entity does not support the persist operation", ex.getMessage());
  }

  @Test
  void shouldThrowException_whenUpdateAttempted() {
    var ex = assertThrows(UnsupportedOperationException.class, () -> listener.preUpdate(entity));

    assertEquals("java.lang.Object entity does not support the update operation", ex.getMessage());
  }

  @Test
  void shouldThrowException_whenRemoveAttempted() {
    var ex = assertThrows(UnsupportedOperationException.class, () -> listener.preRemove(entity));

    assertEquals("java.lang.Object entity does not support the remove operation", ex.getMessage());
  }
}
