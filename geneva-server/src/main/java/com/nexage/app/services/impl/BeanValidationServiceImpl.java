package com.nexage.app.services.impl;

import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.services.BeanValidationService;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class BeanValidationServiceImpl implements BeanValidationService {

  private final Validator beanValidator;

  public BeanValidationServiceImpl(Validator beanValidator) {
    this.beanValidator = beanValidator;
  }

  public void validate(Object entity, Class<?>... groups) {
    Set<ConstraintViolation<Object>> validationErrors = beanValidator.validate(entity, groups);
    if (!validationErrors.isEmpty()) {
      throw new EntityConstraintViolationException(validationErrors);
    }
  }
}
