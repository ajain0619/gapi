package com.nexage.app.services.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import com.nexage.app.util.validator.SearchRequestParameterValidator;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchRequestParameterValidatorTest {

  private SearchRequestParameterValidator validator;

  @Mock private ConstraintValidatorContext context;
  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder builder;

  @BeforeEach
  void setup() {
    validator = new SearchRequestParameterValidator();
    validator.initialize(createAnnotation(new String[] {"name"}));
  }

  @Test
  void testInvalidSearchParameter() {
    Set<String> set = ImmutableSet.of("name", "pid");
    doNothing().when(context).disableDefaultConstraintViolation();
    when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(null);
    boolean isValid = validator.isValid(set, context);
    assertTrue(!isValid);
  }

  @Test
  void testValidSearchParameter() {
    Set<String> set = ImmutableSet.of("name");
    boolean isValid = validator.isValid(set, context);
    assertTrue(isValid);
  }

  @Test
  void testEmptySetSearchParameters() {
    Set<String> set = Collections.emptySet();
    boolean isValid = validator.isValid(set, context);
    assertTrue(isValid);
  }

  private SearchRequestParamConstraint createAnnotation(String[] allowedValues) {
    return new SearchRequestParamConstraint() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public String message() {
        return "Please provide a valid search param";
      }

      @Override
      public Class<?>[] groups() {
        return new Class[0];
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }

      @Override
      public String[] allowedParams() {
        return allowedValues;
      }
    };
  }
}
