package com.nexage.app.util.validator;

import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import javax.validation.ConstraintValidatorContext;

public interface PostAuctionDiscountValidation {
  boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext);

  default boolean needLazy() {
    return false;
  }
}
