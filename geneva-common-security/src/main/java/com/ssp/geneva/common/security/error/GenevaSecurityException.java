package com.ssp.geneva.common.security.error;

import com.ssp.geneva.common.error.exception.GenevaException;
import com.ssp.geneva.common.error.model.ErrorCode;

public class GenevaSecurityException extends GenevaException {

  public GenevaSecurityException(ErrorCode errorCode) {
    super(errorCode);
  }
}
