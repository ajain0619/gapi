package com.ssp.geneva.common.error.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Placeholder for all generic error codes that can be used across modules. */
public enum CommonErrorCodes implements ErrorCode {
  COMMON_USER_NOT_FOUND(1010, HttpStatus.NOT_FOUND),
  COMMON_CRYPTO_ERROR(1120, HttpStatus.INTERNAL_SERVER_ERROR),
  COMMON_OPTIMISTIC_LOCK(2001, HttpStatus.CONFLICT),
  COMMON_BAD_REQUEST(2004, HttpStatus.BAD_REQUEST),
  COMMON_RESPONSE_WRITE_FAILED(2005, HttpStatus.INTERNAL_SERVER_ERROR),
  COMMON_UNKNOWN(3000, HttpStatus.INTERNAL_SERVER_ERROR),
  COMMON_INTERNAL_SYSTEM_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR),
  COMMON_INSUFFICIENT_AUTHENTICATION_DATA(2526, HttpStatus.BAD_REQUEST);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  CommonErrorCodes(int code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return name();
  }
}
