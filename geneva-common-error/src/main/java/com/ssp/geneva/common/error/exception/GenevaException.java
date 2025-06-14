package com.ssp.geneva.common.error.exception;

import com.ssp.geneva.common.error.model.Error;
import com.ssp.geneva.common.error.model.ErrorCode;
import lombok.Getter;

/** Custom geneva exception which should act as a base for all geneva exceptions. */
public class GenevaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  @Getter private final Error error;

  @Getter private final ErrorCode errorCode;

  @Getter private final Object[] messageParams;

  public GenevaException(ErrorCode errorCode, Object[] messageParams) {
    this.error = new Error(errorCode);
    this.errorCode = errorCode;
    this.messageParams = messageParams;
  }

  public GenevaException(ErrorCode errorCode) {
    this(errorCode, null);
  }
}
