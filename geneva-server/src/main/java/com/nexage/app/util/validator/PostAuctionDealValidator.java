package com.nexage.app.util.validator;

import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostAuctionDealValidator
    implements ConstraintValidator<PostAuctionConstraint, PostAuctionDiscountDTO> {
  private final Set<PostAuctionDiscountValidation> validators;

  public PostAuctionDealValidator(Set<PostAuctionDiscountValidation> validators) {
    this.validators = validators;
  }

  @Override
  public boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext) {

    return validators.stream()
            .filter(validator -> !validator.needLazy())
            .allMatch(obj -> obj.isValid(postAuctionDiscountDTO, constraintValidatorContext))
        && validators.stream()
            .filter(PostAuctionDiscountValidation::needLazy)
            .allMatch(obj -> obj.isValid(postAuctionDiscountDTO, constraintValidatorContext));
  }
}
