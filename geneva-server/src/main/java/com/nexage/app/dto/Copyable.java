package com.nexage.app.dto;

public interface Copyable<T> {
  T copy(T original);
}
