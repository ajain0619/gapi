package com.ssp.geneva.server.screenmanagement.validator;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

@Log4j
public class Iso2CountryCodeValidator
    implements ConstraintValidator<Iso2CountryCodeConstraint, String> {

  @Value("${geneva.server.ugeo.iso2.country.codes.file}")
  private String countriesCsvFileName;

  private Set<String> countries = new HashSet<>();

  @Override
  public void initialize(Iso2CountryCodeConstraint annotation) {
    try {
      countries =
          new HashSet<>(IOUtils.readLines(getClass().getResourceAsStream(countriesCsvFileName)));
    } catch (IOException ioException) {
      log.error(
          String.format("Cannot load %s for %s", countriesCsvFileName, getClass().getName()),
          ioException);
      throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR);
    }
  }

  @Override
  public boolean isValid(String country, ConstraintValidatorContext countryConstraint) {
    return null == country || countries.contains(country);
  }
}
