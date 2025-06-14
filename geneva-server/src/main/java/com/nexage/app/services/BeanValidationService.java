package com.nexage.app.services;

public interface BeanValidationService {

  void validate(Object entity, Class<?>... groups);
}
