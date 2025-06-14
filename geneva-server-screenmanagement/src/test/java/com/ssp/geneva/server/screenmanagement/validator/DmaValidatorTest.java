package com.ssp.geneva.server.screenmanagement.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.IOException;
import java.io.InputStream;
import javax.validation.ConstraintValidatorContext;
import org.apache.tika.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DmaValidatorTest {

  @Mock private DmaConstraint dmaConstraint;
  @Mock private ConstraintValidatorContext ctx;
  private DmaValidator dmaValidator;

  @BeforeEach
  public void setUp() {
    dmaValidator = new DmaValidator();
    ReflectionTestUtils.setField(dmaValidator, "dmasCsvFileName", "/ugeo/us_ca/dmas.csv");
    dmaValidator.initialize(dmaConstraint);
  }

  @Test
  void shouldThrowExceptionOnInitializeWhenErrorReadingDmaCsv() {
    GenevaAppRuntimeException genevaAppRuntimeException;
    try (MockedStatic<IOUtils> ioUtils = Mockito.mockStatic(IOUtils.class)) {
      ioUtils.when(() -> IOUtils.readLines(any(InputStream.class))).thenThrow(IOException.class);
      genevaAppRuntimeException =
          assertThrows(
              GenevaAppRuntimeException.class, () -> dmaValidator.initialize(dmaConstraint));
    }
    assertEquals(
        CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR, genevaAppRuntimeException.getErrorCode());
  }

  @Test
  void shouldReturnTrueWhenDmaIsValid() {
    assertTrue(dmaValidator.isValid("Las Vegas", ctx));
  }

  @Test
  void shouldReturnFalseWhenDmaIsInvalid() {
    assertFalse(dmaValidator.isValid("Tatooine", ctx));
  }

  @Test
  void shouldReturnTrueWhenDmaIsNull() {
    assertTrue(dmaValidator.isValid(null, ctx));
  }
}
