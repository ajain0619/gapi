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
public class RestrictionsValidator
    implements ConstraintValidator<RestrictionsConstraint, Set<String>> {

  @Value("${geneva.server.dooh.screen.restrictions.file}")
  private String restrictionsCsvFileName;

  private Set<String> restrictionCategories = new HashSet<>();

  @Override
  public void initialize(RestrictionsConstraint annotation) {
    try {
      restrictionCategories =
          new HashSet<>(IOUtils.readLines(getClass().getResourceAsStream(restrictionsCsvFileName)));
    } catch (IOException ioException) {
      log.error(
          "Cannot load %s for %s".formatted(restrictionsCsvFileName, getClass().getName()),
          ioException);
      throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR);
    }
  }

  @Override
  public boolean isValid(
      Set<String> restrictions, ConstraintValidatorContext constraintValidatorContext) {

    if (restrictions == null) {
      return true;
    }

    return restrictionCategories.containsAll(restrictions);
  }
}
