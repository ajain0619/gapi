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
class Iso2CountryCodeValidatorTest {

  @Mock private Iso2CountryCodeConstraint iso2CountryCodeConstraint;
  @Mock private ConstraintValidatorContext ctx;
  private Iso2CountryCodeValidator iso2CountryCodeValidator;

  @BeforeEach
  public void setUp() {
    iso2CountryCodeValidator = new Iso2CountryCodeValidator();
    ReflectionTestUtils.setField(
        iso2CountryCodeValidator, "countriesCsvFileName", "/ugeo/us_ca/iso2_country_codes.csv");
    iso2CountryCodeValidator.initialize(iso2CountryCodeConstraint);
  }

  @Test
  void shouldThrowExceptionOnInitializeWhenErrorReadingCountryCsv() {
    GenevaAppRuntimeException genevaAppRuntimeException;
    try (MockedStatic<IOUtils> ioUtils = Mockito.mockStatic(IOUtils.class)) {
      ioUtils.when(() -> IOUtils.readLines(any(InputStream.class))).thenThrow(IOException.class);
      genevaAppRuntimeException =
          assertThrows(
              GenevaAppRuntimeException.class,
              () -> iso2CountryCodeValidator.initialize(iso2CountryCodeConstraint));
    }
    assertEquals(
        CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR, genevaAppRuntimeException.getErrorCode());
  }

  @Test
  void shouldReturnTrueWhenCountryIsValid() {
    assertTrue(iso2CountryCodeValidator.isValid("US", ctx));
  }

  @Test
  void shouldReturnFalseWhenCountryIsInvalid() {
    assertFalse(iso2CountryCodeValidator.isValid("USA", ctx));
  }

  @Test
  void shouldReturnTrueWhenCountryIsNull() {
    assertTrue(iso2CountryCodeValidator.isValid(null, ctx));
  }
}
