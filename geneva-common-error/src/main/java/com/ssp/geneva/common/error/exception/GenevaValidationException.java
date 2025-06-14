package com.ssp.geneva.common.error.exception;

import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.error.model.ErrorCode;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/** Custom geneva exception which is thrown on validation failures. */
public class GenevaValidationException extends GenevaException {
  private static final long serialVersionUID = 1L;

  @Getter private final BindingResult bindingResult;

  public GenevaValidationException(ErrorCode errorCode) {
    super(errorCode);
    this.bindingResult = null;
  }

  public GenevaValidationException(ErrorCode errorCode, Object[] messageParams) {
    super(errorCode, messageParams);
    this.bindingResult = null;
  }

  public GenevaValidationException(BindingResult bindingResult) {
    super(CommonErrorCodes.COMMON_BAD_REQUEST);
    this.bindingResult = bindingResult;
  }
}
