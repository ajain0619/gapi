package com.ssp.geneva.sdk.xandr.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum XandrSdkErrorCodes {
  XANDR_SDK_HTTP_CLIENT_ERROR(3721, HttpStatus.INTERNAL_SERVER_ERROR),
  XANDR_SDK_CREDENTIALS_ERROR(3722, HttpStatus.INTERNAL_SERVER_ERROR);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  XandrSdkErrorCodes(int code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return name();
  }
}
