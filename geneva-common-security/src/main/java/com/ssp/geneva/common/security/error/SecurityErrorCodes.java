package com.ssp.geneva.common.security.error;

import com.ssp.geneva.common.error.model.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum SecurityErrorCodes implements ErrorCode {
  SECURITY_NOT_AUTHORIZED(1004, HttpStatus.UNAUTHORIZED),
  SECURITY_BAD_CREDENTIALS(1027, HttpStatus.UNAUTHORIZED),
  SECURITY_SESSION_EXPIRED(2003, HttpStatus.UNAUTHORIZED),
  SECURITY_LOGIN_NOT_SUPPORTED(2020, HttpStatus.UNAUTHORIZED),
  SECURITY_UNKNOWN_FAILURE(3000, HttpStatus.UNAUTHORIZED),
  SECURITY_BAD_PRINCIPAL(3001, HttpStatus.UNAUTHORIZED);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  SecurityErrorCodes(int code, HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
    this.code = code;
  }

  @Override
  public String toString() {
    return name();
  }
}
