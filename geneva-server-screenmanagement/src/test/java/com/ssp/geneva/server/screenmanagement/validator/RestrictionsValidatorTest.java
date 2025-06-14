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
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.apache.tika.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class RestrictionsValidatorTest {

  @Mock private ConstraintValidatorContext ctx;
  @Mock private RestrictionsConstraint restrictionsConstraint;
  private RestrictionsValidator restrictionsValidator = new RestrictionsValidator();

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(
        restrictionsValidator, "restrictionsCsvFileName", "/dooh-restrictions.csv");
    restrictionsValidator.initialize(restrictionsConstraint);
  }

  @Test
  void shouldThrowExceptionOnInitializeWhenErrorReadingStatesCsv() {
    GenevaAppRuntimeException genevaAppRuntimeException;
    try (MockedStatic<IOUtils> ioUtils = Mockito.mockStatic(IOUtils.class)) {
      ioUtils.when(() -> IOUtils.readLines(any(InputStream.class))).thenThrow(IOException.class);
      genevaAppRuntimeException =
          assertThrows(
              GenevaAppRuntimeException.class,
              () -> restrictionsValidator.initialize(restrictionsConstraint));
    }
    assertEquals(
        CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR, genevaAppRuntimeException.getErrorCode());
  }

  @Test
  void shouldReturnTrueWhenRestrictionsIsNull() {
    assertTrue(restrictionsValidator.isValid(null, ctx));
  }

  @Test
  void shouldReturnFalseWhenSingleInvalidRestriction() {
    assertFalse(restrictionsValidator.isValid(Set.of("invalid"), ctx));
  }

  @Test
  void shouldReturnTrueWhenSingleValidRestriction() {
    assertTrue(restrictionsValidator.isValid(Set.of("IAB1-1"), ctx));
  }

  @Test
  void shouldReturnTrueWhenMultipleValidRestrictions() {
    assertTrue(restrictionsValidator.isValid(Set.of("IAB1-4", "IAB1-5", "IAB2-17"), ctx));
  }

  @Test
  void shouldReturnFalseWhenMultipleValidRestrictionsAndAnInvalidRestriction() {
    assertFalse(
        restrictionsValidator.isValid(Set.of("IAB1-4", "IAB1-5", "IAB-INVALID", "IAB2-17"), ctx));
  }
}
