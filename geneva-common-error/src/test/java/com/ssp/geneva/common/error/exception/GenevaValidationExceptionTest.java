package com.ssp.geneva.common.error.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.ssp.geneva.common.error.model.CommonErrorCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class GenevaValidationExceptionTest {

  @Test
  void shouldSetMessageToException() {
    var exception =
        new GenevaValidationException(
            CommonErrorCodes.COMMON_BAD_REQUEST, new Object[] {1, "test"});
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
    assertNotNull(exception.getMessageParams());
    assertEquals(1, exception.getMessageParams()[0]);
    assertEquals("test", exception.getMessageParams()[1]);
  }

  @Test
  void shouldSetBindingResultToException() {
    BindingResult result = mock(BindingResult.class);
    var exception = new GenevaValidationException(result);
    assertNotNull(exception.getBindingResult());
  }
}
