package com.ssp.geneva.common.error.exception;

import com.ssp.geneva.common.error.model.ErrorCode;

/** Custom geneva exception which is thrown on database related failures. */
public class GenevaDatabaseException extends GenevaException {

  public GenevaDatabaseException(ErrorCode errorCode) {
    super(errorCode);
  }
}
