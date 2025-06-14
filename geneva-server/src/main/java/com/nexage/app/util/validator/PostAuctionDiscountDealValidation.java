package com.nexage.app.util.validator;

import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PostAuctionDiscountDealValidation implements PostAuctionDiscountValidation {

  private final DirectDealRepository directDealRepository;

  public PostAuctionDiscountDealValidation(DirectDealRepository directDealRepository) {
    this.directDealRepository = directDealRepository;
  }

  @Override
  public boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext) {
    var isValid = true;
    var deals = postAuctionDiscountDTO.getDiscountDeals();
    if (deals != null && !deals.isEmpty()) {
      var dealIds = deals.stream().map(DirectDealViewDTO::getPid).collect(Collectors.toSet());
      isValid = directDealRepository.countByPidIn(dealIds) == dealIds.size();
    }

    if (!isValid) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate("Invalid Deals selected for discount")
          .addConstraintViolation();
    }
    return isValid;
  }
}
