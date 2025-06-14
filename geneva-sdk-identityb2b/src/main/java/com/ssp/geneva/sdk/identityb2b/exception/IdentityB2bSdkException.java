package com.ssp.geneva.sdk.identityb2b.exception;

import lombok.Getter;

@Getter
public class IdentityB2bSdkException extends RuntimeException {

  private final IdentityB2bErrorCodes errorCode;

  public IdentityB2bSdkException(IdentityB2bErrorCodes errorCode) {
    this.errorCode = errorCode;
  }
}
