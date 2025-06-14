package com.ssp.geneva.sdk.onecentral.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum OneCentralErrorCodes {
  ONECENTRAL_UNABLE_TO_GET_TOKEN(2013, HttpStatus.INTERNAL_SERVER_ERROR),
  ONECENTRAL_INTERNAL_ERROR(2016, HttpStatus.INTERNAL_SERVER_ERROR),
  ONECENTRAL_NULL_RESPONSE(2017, HttpStatus.FAILED_DEPENDENCY),
  ONECENTRAL_CONSTRAINT_VIOLATION(2019, HttpStatus.BAD_REQUEST),
  ONECENTRAL_USER_NOT_FOUND(3800, HttpStatus.UNAUTHORIZED),

  ONECENTRAL_USER_INACTIVE(3801, HttpStatus.UNAUTHORIZED);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  OneCentralErrorCodes(int code, HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
    this.code = code;
  }

  @Override
  public String toString() {
    return name();
  }
}
