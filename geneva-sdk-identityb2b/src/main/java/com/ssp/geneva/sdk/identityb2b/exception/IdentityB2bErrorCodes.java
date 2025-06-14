package com.ssp.geneva.sdk.identityb2b.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum IdentityB2bErrorCodes {
  IDENTITY_B2B_NOT_AUTHORIZED(1004, HttpStatus.UNAUTHORIZED);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  IdentityB2bErrorCodes(int code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return name();
  }
}
