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
public class ZipCodeValidator implements ConstraintValidator<ZipCodeConstraint, String> {

  @Value("${geneva.server.ugeo.zipcodes.file}")
  public String zipCodesCsvFileName;

  private Set<String> zips = new HashSet<>();

  @Override
  public void initialize(ZipCodeConstraint annotation) {
    try {
      zips = new HashSet<>(IOUtils.readLines(getClass().getResourceAsStream(zipCodesCsvFileName)));
    } catch (IOException ioException) {
      log.error(
          String.format("Cannot load %s for %s", zipCodesCsvFileName, getClass().getName()),
          ioException);
      throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR);
    }
  }

  @Override
  public boolean isValid(String zipCode, ConstraintValidatorContext zipCodeConstraint) {
    return null == zipCode || zips.contains(zipCode);
  }
}
