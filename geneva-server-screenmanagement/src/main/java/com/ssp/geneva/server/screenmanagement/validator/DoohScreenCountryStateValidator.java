package com.ssp.geneva.server.screenmanagement.validator;

import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;

public class DoohScreenCountryStateValidator
    implements ConstraintValidator<DoohScreenCountryStateConstraint, DoohScreenDTO> {

  private static final String REQ_COUNTRY = "US";

  @Override
  public boolean isValid(
      DoohScreenDTO doohScreenDTO, ConstraintValidatorContext constraintValidatorContext) {

    if (StringUtils.isEmpty(doohScreenDTO.getState())
        && REQ_COUNTRY.equals(doohScreenDTO.getCountry())) {
      return false;
    }
    return true;
  }
}
