package com.nexage.app.util.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

public class SearchRequestParameterValidator
    implements ConstraintValidator<SearchRequestParamConstraint, Set<String>> {
  private String[] allowedParams;

  @Override
  public void initialize(SearchRequestParamConstraint constraintAnnotation) {
    this.allowedParams = constraintAnnotation.allowedParams();
  }

  @Override
  public boolean isValid(Set<String> qp, ConstraintValidatorContext constraintValidatorContext) {
    if (CollectionUtils.isEmpty(qp)) return true;
    final List<String> searchParams = Arrays.asList(this.allowedParams);
    final Set<String> filterItems =
        qp.stream()
            .filter(queryParam -> !searchParams.contains(queryParam))
            .collect(Collectors.toSet());
    if (!filterItems.isEmpty()) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate(
              String.format(
                  "Search request has invalid %1$s parameter", String.join(",", filterItems)))
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
