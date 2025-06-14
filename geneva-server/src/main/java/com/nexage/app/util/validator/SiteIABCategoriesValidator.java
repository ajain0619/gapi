package com.nexage.app.util.validator;

import com.nexage.app.dto.provisionable.ProvisionIABCategoryDTO;
import com.nexage.app.util.assemblers.provisionable.IabCategoriesUtils;
import java.util.Collection;
import javax.validation.ConstraintValidatorContext;

/** Validator contains validation logic for iadCategories field for site */
public class SiteIABCategoriesValidator
    extends BaseValidator<SiteIabCategoriesConstraint, Collection<String>> {

  @Override
  public boolean isValid(Collection<String> iabCategories, ConstraintValidatorContext context) {
    boolean result = true;
    if (iabCategories != null) {
      if (iabCategories.size() < getAnnotation().min()) {
        buildConstraintViolationWithTemplate(
            context, ValidationMessages.WRONG_ARRAY_LENGTH_TO_SMALL);
        result = false;
      } else if (iabCategories.size() > getAnnotation().max()) {
        buildConstraintViolationWithTemplate(
            context, ValidationMessages.WRONG_ARRAY_LENGTH_TO_LARGE);
        result = false;
      } else {
        for (String iabCategory : iabCategories) {
          ProvisionIABCategoryDTO targetCategory =
              IabCategoriesUtils.getCategoryDescription(iabCategory);
          if (targetCategory != null) {
            result = IabCategoriesUtils.checkParent(targetCategory, iabCategories);
            if (!result) {
              buildConstraintViolationWithTemplate(
                  context,
                  String.format(ValidationMessages.WRONG_IAB_CATEGORY_PARENT_EXIST, iabCategory));
              result = false;
              break;
            }
          } else {
            buildConstraintViolationWithTemplate(
                context,
                String.format(ValidationMessages.WRONG_IAB_CATEGORY_NOT_EXIST, iabCategory));
            result = false;
            break;
          }
        }
      }
    } else if (!getAnnotation().nullable()) {
      buildConstraintViolationWithTemplate(context, ValidationMessages.WRONG_IS_EMPTY);
      result = false;
    }
    return result;
  }
}
