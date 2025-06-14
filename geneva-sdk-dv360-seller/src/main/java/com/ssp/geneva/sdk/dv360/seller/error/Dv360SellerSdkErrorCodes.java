package com.ssp.geneva.sdk.dv360.seller.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum Dv360SellerSdkErrorCodes {
  DV360_SELLER_SDK_DOUBLECLICK_BID_MANAGER_ERROR(3700, HttpStatus.INTERNAL_SERVER_ERROR),
  DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR(3701, HttpStatus.INTERNAL_SERVER_ERROR),
  DV360_SELLER_SDK_HTTP_CLIENT_ERROR(3702, HttpStatus.INTERNAL_SERVER_ERROR);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  Dv360SellerSdkErrorCodes(int code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return name();
  }
}
