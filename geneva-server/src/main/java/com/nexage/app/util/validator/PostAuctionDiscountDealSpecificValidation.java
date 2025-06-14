package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class PostAuctionDiscountDealSpecificValidation implements PostAuctionDiscountValidation {

  @Override
  public boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext) {
    var isValid =
        postAuctionDiscountDTO.getDealsSelected() != PostAuctionDealsSelected.SPECIFIC
            || CollectionUtils.isNotEmpty(postAuctionDiscountDTO.getDiscountDeals());

    if (!isValid) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate(
              "Specific deals are selected but no deals found in discountDeals property")
          .addConstraintViolation();
    }
    return isValid;
  }
}
