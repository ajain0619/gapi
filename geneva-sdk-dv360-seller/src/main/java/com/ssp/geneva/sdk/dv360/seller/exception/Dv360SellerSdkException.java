package com.ssp.geneva.sdk.dv360.seller.exception;

import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class Dv360SellerSdkException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Dv360SellerSdkErrorCodes errorCode;

  private final Object[] messageParams;

  private final HttpStatus httpStatus;

  public Dv360SellerSdkException(Dv360SellerSdkErrorCodes errorCode) {
    this(errorCode, null);
  }

  public Dv360SellerSdkException(Dv360SellerSdkErrorCodes errorCode, Object[] messageParams) {
    this(errorCode, messageParams, null);
  }

  public Dv360SellerSdkException(
      Dv360SellerSdkErrorCodes errorCode, Object[] messageParams, HttpStatus httpStatus) {
    this.errorCode = errorCode;
    this.messageParams = messageParams;
    this.httpStatus = httpStatus;
  }
}
