package com.ssp.geneva.common.error.exception;

import com.ssp.geneva.common.error.model.ErrorCode;

/** Custom geneva exception which is thrown on management layer failures. */
public class GenevaAppRuntimeException extends GenevaException {

  public GenevaAppRuntimeException(ErrorCode errorCode) {
    super(errorCode);
  }
}
