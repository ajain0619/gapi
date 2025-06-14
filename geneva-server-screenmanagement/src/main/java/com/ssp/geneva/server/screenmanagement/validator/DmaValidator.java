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
public class DmaValidator implements ConstraintValidator<DmaConstraint, String> {

  @Value("${geneva.server.ugeo.dmas.file}")
  private String dmasCsvFileName;

  private Set<String> dmas = new HashSet<>();

  @Override
  public void initialize(DmaConstraint annotation) {
    try {
      dmas = new HashSet<>(IOUtils.readLines(getClass().getResourceAsStream(dmasCsvFileName)));
    } catch (IOException ioException) {
      log.error(
          String.format("Cannot load %s for %s", dmasCsvFileName, getClass().getName()),
          ioException);
      throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR);
    }
  }

  @Override
  public boolean isValid(String dma, ConstraintValidatorContext dmaConstraint) {
    return null == dma || dmas.contains(dma);
  }
}
