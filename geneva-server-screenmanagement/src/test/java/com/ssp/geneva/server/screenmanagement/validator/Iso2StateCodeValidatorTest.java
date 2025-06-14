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
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class Iso2StateCodeValidatorTest {

  @Mock private ConstraintValidatorContext ctx;
  @Mock private Iso2StateCodeConstraint iso2StateCodeConstraint;

  private Iso2StateCodeValidator iso2StateCodeValidator;

  @BeforeEach
  public void setUp() {
    iso2StateCodeValidator = new Iso2StateCodeValidator();
    ReflectionTestUtils.setField(
        iso2StateCodeValidator, "statesCsvFileName", "/ugeo/us_ca/iso2_state_codes.csv");
    iso2StateCodeValidator.initialize(iso2StateCodeConstraint);
  }

  @Test
  void shouldThrowExceptionOnInitializeWhenErrorReadingStatesCsv() {
    GenevaAppRuntimeException genevaAppRuntimeException;
    try (MockedStatic<IOUtils> ioUtils = Mockito.mockStatic(IOUtils.class)) {
      ioUtils.when(() -> IOUtils.readLines(any(InputStream.class))).thenThrow(IOException.class);
      genevaAppRuntimeException =
          assertThrows(
              GenevaAppRuntimeException.class,
              () -> iso2StateCodeValidator.initialize(iso2StateCodeConstraint));
    }
    assertEquals(
        CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR, genevaAppRuntimeException.getErrorCode());
  }

  @Test
  void shouldReturnTrueWhenStateIsValid() {
    assertTrue(iso2StateCodeValidator.isValid("MD", ctx));
  }

  @Test
  void shouldReturnFalseWhenStateIsInvalid() {
    assertFalse(iso2StateCodeValidator.isValid("Dela-where", ctx));
  }

  @Test
  void shouldReturnTrueWhenStateIsNull() {
    assertTrue(iso2StateCodeValidator.isValid(null, ctx));
  }
}
