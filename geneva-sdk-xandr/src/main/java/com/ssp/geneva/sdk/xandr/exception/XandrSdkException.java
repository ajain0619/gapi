package com.ssp.geneva.sdk.xandr.exception;

import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class XandrSdkException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final XandrSdkErrorCodes errorCode;

  private final String[] messageParams;

  private final HttpStatus httpStatus;

  public XandrSdkException(XandrSdkErrorCodes errorCode) {
    this(errorCode, null);
  }

  public XandrSdkException(XandrSdkErrorCodes errorCode, String[] messageParams) {
    this(errorCode, messageParams, null);
  }

  public XandrSdkException(
      XandrSdkErrorCodes errorCode, String[] messageParams, HttpStatus httpStatus) {
    this.errorCode = errorCode;
    this.messageParams = messageParams;
    this.httpStatus = httpStatus;
  }
}
