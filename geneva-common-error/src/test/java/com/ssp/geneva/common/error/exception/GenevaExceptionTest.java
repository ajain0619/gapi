package com.ssp.geneva.common.error.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class GenevaExceptionTest {

  @ParameterizedTest
  @MethodSource("getExceptions")
  void shouldSetErrorViaGenevaException(GenevaException exception) {
    assertNotNull(exception.getError());
    assertEquals(
        CommonErrorCodes.COMMON_BAD_REQUEST.getCode(), exception.getError().getErrorCode());
    assertEquals(
        CommonErrorCodes.COMMON_BAD_REQUEST.getHttpStatus(), exception.getError().getHttpStatus());
    assertEquals(
        CommonErrorCodes.COMMON_BAD_REQUEST.getHttpStatus().value(),
        exception.getError().getHttpResponse());
    assertNotNull(exception.getErrorCode());
  }

  private static Stream<GenevaException> getExceptions() {
    return Stream.of(
        new GenevaAppRuntimeException(CommonErrorCodes.COMMON_BAD_REQUEST),
        new GenevaDatabaseException(CommonErrorCodes.COMMON_BAD_REQUEST),
        new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
  }
}
