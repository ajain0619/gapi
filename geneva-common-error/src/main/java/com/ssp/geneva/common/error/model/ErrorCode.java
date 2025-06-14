package com.ssp.geneva.common.error.model;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

/** The interface that defines how the error-codes across geneva modules should be. */
public interface ErrorCode extends Serializable {

  /** @return HTTP status code corresponding to the error code. */
  HttpStatus getHttpStatus();

  /** @return Error code. */
  int getCode();

  /**
   * Overrides the default toString in case there is a need to use different implementation
   * otherwise name of the error code enum will be returned.
   *
   * @return name of the enum.
   */
  String toString();
}
