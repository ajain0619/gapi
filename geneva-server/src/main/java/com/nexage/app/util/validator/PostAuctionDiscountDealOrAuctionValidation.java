package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PostAuctionDiscountDealOrAuctionValidation implements PostAuctionDiscountValidation {

  @Override
  public boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext) {

    var dealsEnabled =
        postAuctionDiscountDTO.getDealsSelected() != null
            && !PostAuctionDealsSelected.NONE.equals(postAuctionDiscountDTO.getDealsSelected());

    var isValid = postAuctionDiscountDTO.getOpenAuctionEnabled() || dealsEnabled;

    if (!isValid) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate(
              "Either dealsEnabled or openAuctionEnabled should be true")
          .addConstraintViolation();
    }
    return isValid;
  }
}
