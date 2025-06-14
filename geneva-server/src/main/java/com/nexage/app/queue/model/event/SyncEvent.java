package com.nexage.app.queue.model.event;

import lombok.ToString;

@ToString
public class SyncEvent<T> {

  private T data;
  private Status status;

  public enum Status {
    CREATE,
    UPDATE,
    DELETE
  }

  public static <T> SyncEvent<T> of(T data, Status status) {
    return new SyncEvent<>(data, status);
  }

  public static <T> SyncEvent<T> createOf(T data) {
    return new SyncEvent<>(data, Status.CREATE);
  }

  private SyncEvent(T data, Status status) {
    this.data = data;
    this.status = status;
  }

  public T getData() {
    return this.data;
  }

  public Status getStatus() {
    return this.status;
  }
}
