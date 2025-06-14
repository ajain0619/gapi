package com.nexage.app.util.validator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseValidatorTest {
  @Mock protected ConstraintValidatorContext ctx;
  @Mock protected NodeBuilderCustomizableContext nodeBuilder;
  @Mock protected ConstraintViolationBuilder violationBuilder;

  @BeforeEach
  public void init() {
    initializeContext();
    initializeConstraint();
  }

  protected void initializeContext() {
    lenient()
        .when(ctx.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(violationBuilder);
    lenient().when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
  }

  protected abstract void initializeConstraint();
}
