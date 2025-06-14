package com.nexage.app.dto.queryfield;

import java.util.Objects;

/**
 * An Enum to store information about type of a value which is stored in particular field from a
 * query field parameter.
 */
public enum QueryFieldValueType {
  STRING(String.class) {
    @Override
    public boolean valid(String value) {
      return Objects.nonNull(value);
    }

    @Override
    public Object convert(String value) {
      return value;
    }
  },
  BOOL(Boolean.class) {
    @Override
    public boolean valid(String value) {
      return Objects.requireNonNull(value).matches("(?i:true|false)");
    }

    @Override
    public Object convert(String value) {
      return Boolean.valueOf(value);
    }
  },
  LONG(Long.class) {
    @Override
    public boolean valid(String value) {
      return Objects.requireNonNull(value).matches("^-?\\d+$");
    }

    @Override
    public Object convert(String value) {
      return Long.valueOf(value);
    }
  },
  INTEGER(Integer.class) {
    @Override
    public boolean valid(String value) {
      return Objects.requireNonNull(value).matches("^-?\\d+$");
    }

    @Override
    public Object convert(String value) {
      return Integer.valueOf(value);
    }
  };

  private final Class<?> typeClass;

  QueryFieldValueType(Class<?> typeClass) {
    this.typeClass = typeClass;
  }

  public abstract boolean valid(String value);

  public abstract Object convert(String value);

  public Class<?> typeClass() {
    return typeClass;
  }
}
