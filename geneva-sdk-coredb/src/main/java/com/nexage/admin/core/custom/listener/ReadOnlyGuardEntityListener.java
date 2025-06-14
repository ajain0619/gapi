package com.nexage.admin.core.custom.listener;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/** An entity listener that prevents persist, update, and remove operations. */
public class ReadOnlyGuardEntityListener {

  private UnsupportedOperationException createUnsupportedOperationException(
      Object entity, String operation) {
    var message =
        String.format(
            "%s entity does not support the %s operation", entity.getClass().getName(), operation);

    return new UnsupportedOperationException(message);
  }

  /**
   * Pre persist operation callback that throws an {@link UnsupportedOperationException}
   *
   * @param entity the entity to persist
   * @throws UnsupportedOperationException to prevent persist operation.
   */
  @PrePersist
  void prePersist(Object entity) throws UnsupportedOperationException {
    throw createUnsupportedOperationException(entity, "persist");
  }

  /**
   * Pre remove operation callback that throws an {@link UnsupportedOperationException}
   *
   * @param entity the entity to remove
   * @throws UnsupportedOperationException to prevent remove operation.
   */
  @PreRemove
  void preRemove(Object entity) throws UnsupportedOperationException {
    throw createUnsupportedOperationException(entity, "remove");
  }

  /**
   * Pre update operation callback that throws an {@link UnsupportedOperationException}
   *
   * @param entity the entity to update
   * @throws UnsupportedOperationException to prevent update operation.
   */
  @PreUpdate
  void preUpdate(Object entity) throws UnsupportedOperationException {
    throw createUnsupportedOperationException(entity, "update");
  }
}
