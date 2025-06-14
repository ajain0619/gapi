package com.nexage.admin.core.enums;

/** <code>HasInt</code> */
public interface HasInt<T> {
  int asInt();

  T fromInt(int i);
}
