package com.nexage.admin.core.error;

import com.ssp.geneva.common.error.model.ErrorCode;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Placeholder for all error codes specific to module geneva-sdk-coredb. */
public enum CoreDBErrorCodes implements ErrorCode {
  CORE_DB_EXCHANGE_ID_MISSING(114, HttpStatus.NOT_FOUND),
  CORE_DB_MMBUYER_ID_MISSING(134, HttpStatus.NOT_FOUND),
  CORE_DB_DUPLICATE_POSITION_NAME(135, HttpStatus.BAD_REQUEST),
  CORE_DB_INVALID_BATCH_STATEMENT(137, HttpStatus.INTERNAL_SERVER_ERROR),
  CORE_DB_DUPLICATE_POSITION(138, HttpStatus.BAD_REQUEST),
  CORE_DB_DUPLICATE_EMAIL(1019, HttpStatus.BAD_REQUEST),
  CORE_DB_DUPLICATE_SITE_NAME(1020, HttpStatus.BAD_REQUEST),
  CORE_DB_DUPLICATE_USER_NAME(1021, HttpStatus.BAD_REQUEST),
  CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE(1328, HttpStatus.BAD_REQUEST);

  @Getter private final int code;

  @Getter private final HttpStatus httpStatus;

  CoreDBErrorCodes(int code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return name();
  }

  public static List<CoreDBErrorCodes> getConstraintViolationErrorCodes() {
    return List.of(
        CORE_DB_DUPLICATE_USER_NAME, CORE_DB_DUPLICATE_EMAIL, CORE_DB_DUPLICATE_SITE_NAME);
  }
}
