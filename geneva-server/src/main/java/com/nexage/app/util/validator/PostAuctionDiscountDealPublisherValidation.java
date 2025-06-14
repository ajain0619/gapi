package com.nexage.app.util.validator;

import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.specification.PositionDealsSpecification;
import com.nexage.admin.core.specification.PublisherDealsSpecification;
import com.nexage.admin.core.specification.SiteDealsSpecification;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PostAuctionDiscountDealPublisherValidation implements PostAuctionDiscountValidation {

  private final DealSiteRepository dealSiteRepository;
  private final DealPositionRepository dealPositionRepository;
  private final DealPublisherRepository dealPublisherRepository;

  public PostAuctionDiscountDealPublisherValidation(
      DealSiteRepository dealSiteRepository,
      DealPositionRepository dealPositionRepository,
      DealPublisherRepository dealPublisherRepository) {
    this.dealPublisherRepository = dealPublisherRepository;
    this.dealPositionRepository = dealPositionRepository;

    this.dealSiteRepository = dealSiteRepository;
  }

  @Override
  public boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext) {

    var deals = postAuctionDiscountDTO.getDiscountDeals();
    AtomicBoolean isValid = new AtomicBoolean(true);
    if (deals != null && !deals.isEmpty()) {
      var sellers =
          postAuctionDiscountDTO.getDiscountSellers().stream()
              .map(PostAuctionDiscountSellerDTO::getPid)
              .collect(Collectors.toSet());

      deals.stream()
          .filter(
              deal ->
                  dealPositionRepository.count(
                          PositionDealsSpecification.withDealPidAndSellerIds(
                              deal.getPid(), sellers))
                      == 0)
          .filter(
              deal ->
                  dealSiteRepository.count(
                          SiteDealsSpecification.withDealPidAndSellerIds(deal.getPid(), sellers))
                      == 0)
          .filter(
              deal ->
                  dealPublisherRepository.count(
                          PublisherDealsSpecification.withDealPidAndSellerIds(
                              deal.getPid(), sellers))
                      == 0)
          .findFirst()
          .ifPresent(
              deal -> {
                isValid.set(false);
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext
                    .buildConstraintViolationWithTemplate(
                        String.format(
                            "Invalid Deal - %s for given sellers combination", deal.getPid()))
                    .addConstraintViolation();
              });
    }

    return isValid.get();
  }

  @Override
  public boolean needLazy() {
    return true;
  }
}
