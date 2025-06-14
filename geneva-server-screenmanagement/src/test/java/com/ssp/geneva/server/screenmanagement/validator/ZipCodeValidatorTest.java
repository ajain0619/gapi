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
class ZipCodeValidatorTest {

  @Mock private ZipCodeConstraint zipCodeConstraint;
  @Mock private ConstraintValidatorContext ctx;
  private ZipCodeValidator zipCodeValidator;

  @BeforeEach
  public void setUp() {
    zipCodeValidator = new ZipCodeValidator();
    ReflectionTestUtils.setField(
        zipCodeValidator, "zipCodesCsvFileName", "/ugeo/us_ca/zipcodes.csv");
    zipCodeValidator.initialize(zipCodeConstraint);
  }

  @Test
  void shouldThrowExceptionOnInitializeWhenErrorReadingZipCodeCsv() {
    GenevaAppRuntimeException genevaAppRuntimeException;
    try (MockedStatic<IOUtils> ioUtils = Mockito.mockStatic(IOUtils.class)) {
      ioUtils.when(() -> IOUtils.readLines(any(InputStream.class))).thenThrow(IOException.class);
      genevaAppRuntimeException =
          assertThrows(
              GenevaAppRuntimeException.class,
              () -> zipCodeValidator.initialize(zipCodeConstraint));
    }
    assertEquals(
        CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR, genevaAppRuntimeException.getErrorCode());
  }

  @Test
  void shouldReturnTrueWhenZipCodeIsValid() {
    assertTrue(zipCodeValidator.isValid("21224", ctx));
  }

  @Test
  void shouldReturnFalseWhenZipCodeIsInvalid() {
    assertFalse(zipCodeValidator.isValid("abc123", ctx));
  }

  @Test
  void shouldReturnTrueWhenZipIsNull() {
    assertTrue(zipCodeValidator.isValid(null, ctx));
  }
}
