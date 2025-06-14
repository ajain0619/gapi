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
public class Iso2StateCodeValidator
    implements ConstraintValidator<Iso2StateCodeConstraint, String> {

  @Value("${geneva.server.ugeo.iso2.state.codes.file}")
  private String statesCsvFileName;

  private Set<String> states = new HashSet<>();

  @Override
  public void initialize(Iso2StateCodeConstraint annotation) {
    try {
      states = new HashSet<>(IOUtils.readLines(getClass().getResourceAsStream(statesCsvFileName)));
    } catch (IOException ioException) {
      log.error(
          String.format("Cannot load %s for %s", statesCsvFileName, getClass().getName()),
          ioException);
      throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR);
    }
  }

  @Override
  public boolean isValid(String state, ConstraintValidatorContext stateValidator) {
    return null == state || states.contains(state);
  }
}
