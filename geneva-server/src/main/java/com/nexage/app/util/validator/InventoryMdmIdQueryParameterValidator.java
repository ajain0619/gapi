package com.nexage.app.util.validator;

import com.nexage.app.util.InventoryMdmIdQueryFieldParameter;
import com.nexage.app.util.validator.rule.queryfield.AbstractQueryFieldParameterValidator;
import java.util.Arrays;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.MultiValueMap;

public class InventoryMdmIdQueryParameterValidator
    extends AbstractQueryFieldParameterValidator<
        InventoryMdmIdQueryFieldParameter,
        InventoryMdmIdQueryFieldParameterConstraint,
        InventoryMdmIdQueryFieldParams> {

  protected InventoryMdmIdQueryParameterValidator() {
    super(InventoryMdmIdQueryFieldParameter.values());
  }

  @Override
  public boolean isValid(
      InventoryMdmIdQueryFieldParams params, ConstraintValidatorContext context) {
    return validate(params.getFields(), context) && areAllKnownParams(params.getFields());
  }

  private boolean areAllKnownParams(MultiValueMap<String, String> fields) {
    return Arrays.stream(InventoryMdmIdQueryFieldParameter.values())
        .map(InventoryMdmIdQueryFieldParameter::getName)
        .anyMatch(fields::containsKey);
  }
}
