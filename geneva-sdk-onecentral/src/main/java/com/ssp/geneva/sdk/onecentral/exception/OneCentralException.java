package com.ssp.geneva.sdk.onecentral.exception;

import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse;
import lombok.Getter;

public class OneCentralException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  @Getter private final OneCentralErrorCodes errorCode;

  @Getter private final OneCentralSdkErrorResponse oneCentralErrorResponse;

  public OneCentralException(OneCentralErrorCodes errorCode) {
    this.errorCode = errorCode;
    this.oneCentralErrorResponse = null;
  }

  public OneCentralException(
      OneCentralErrorCodes errorCode, OneCentralSdkErrorResponse oneCentralErrorResponse) {
    this.errorCode = errorCode;
    this.oneCentralErrorResponse = oneCentralErrorResponse;
  }
}
