package com.ssp.geneva.sdk.dv360.seller.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import com.ssp.geneva.sdk.dv360.seller.annotation.CurrencyCode;
import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CurrencyCodeValidatorTest {
  @Mock ConstraintValidatorContext constraintValidatorContext;
  @Mock CurrencyCodeValidator currencyCodeValidator;

  @BeforeEach
  public void setUp() {
    doCallRealMethod().when(currencyCodeValidator).initialize(any());
    when(currencyCodeValidator.isValid(any(), any())).thenCallRealMethod();
    currencyCodeValidator.initialize(new CurrencyCodeTest());
  }

  @ParameterizedTest
  @CsvSource({"'USD', true", "'---', false", "'', false", "null, false", "'CAD', true"})
  void whenIsValid_thenReturn(String code, boolean valid) {
    assertEquals(valid, currencyCodeValidator.isValid(code, constraintValidatorContext));
  }

  @Test
  void testConstruct() {
    CurrencyCodeValidator currencyCodeValidator = new CurrencyCodeValidator();
    assertNotNull(currencyCodeValidator);
  }

  private class CurrencyCodeTest implements CurrencyCode {

    @Override
    public String message() {
      return "test message";
    }

    @Override
    public Class<?>[] groups() {
      return new Class[] {};
    }

    @Override
    public Class<? extends Payload>[] payload() {
      return new Class[] {};
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return CurrencyCode.class;
    }
  }
}
