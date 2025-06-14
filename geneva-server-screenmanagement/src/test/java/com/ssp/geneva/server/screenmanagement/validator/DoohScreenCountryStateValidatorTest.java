package com.ssp.geneva.server.screenmanagement.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoohScreenCountryStateValidatorTest {

  @Mock private DoohScreenCountryStateConstraint doohScreenCountryStateConstraint;
  @Mock private ConstraintValidatorContext ctx;

  @BeforeEach
  public void setUp() {
    lenient()
        .when(doohScreenCountryStateConstraint.message())
        .thenReturn("Country must have a state");
    lenient().when(doohScreenCountryStateConstraint.field()).thenReturn("state");
  }

  @Test
  void shouldReturnTrueWhenRequiredCountryHasState() {
    DoohScreenCountryStateValidator doohScreenCountryStateValidator =
        new DoohScreenCountryStateValidator();
    doohScreenCountryStateValidator.initialize(doohScreenCountryStateConstraint);
    var doohScreenDTO = new DoohScreenDTO();
    doohScreenDTO.setState("MD");
    doohScreenDTO.setCountry("US");
    assertTrue(doohScreenCountryStateValidator.isValid(doohScreenDTO, ctx));
  }

  @Test
  void shouldReturnFalseWhenRequiredCountryIsMissingState() {
    DoohScreenCountryStateValidator doohScreenCountryStateValidator =
        new DoohScreenCountryStateValidator();
    doohScreenCountryStateValidator.initialize(doohScreenCountryStateConstraint);
    var doohScreenDTO = new DoohScreenDTO();
    doohScreenDTO.setCountry("US");
    assertFalse(doohScreenCountryStateValidator.isValid(doohScreenDTO, ctx));
  }

  @Test
  void shouldReturnTrueWhenMissingStateButCountryDoesNotRequire() {
    DoohScreenCountryStateValidator doohScreenCountryStateValidator =
        new DoohScreenCountryStateValidator();
    doohScreenCountryStateValidator.initialize(doohScreenCountryStateConstraint);
    var doohScreenDTO = new DoohScreenDTO();
    doohScreenDTO.setCountry("CA");
    assertTrue(doohScreenCountryStateValidator.isValid(doohScreenDTO, ctx));
  }
}
