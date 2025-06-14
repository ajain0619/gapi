package com.nexage.app.util.validator;

import static com.nexage.admin.core.specification.RuleTargetDealsSpecification.withDealPidAndTargetType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.RuleTargetRepository;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.services.validation.sellingrule.AbstractBidderValidator;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PostAuctionDiscountDealDspAndBuyerSeatValidation
    implements PostAuctionDiscountValidation {

  private final RuleTargetRepository ruleTargetRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public PostAuctionDiscountDealDspAndBuyerSeatValidation(
      RuleTargetRepository ruleTargetRepository) {
    this.ruleTargetRepository = ruleTargetRepository;
  }

  @Override
  public boolean isValid(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      ConstraintValidatorContext constraintValidatorContext) {

    var deals = postAuctionDiscountDTO.getDiscountDeals();
    var discountDSPs = postAuctionDiscountDTO.getDiscountDSPs();

    var covertDiscountDsp = convertDTOToMap(discountDSPs);

    var isValid = true;
    if (deals != null && !deals.isEmpty()) {

      isValid =
          deals.stream()
              .map(DirectDealViewDTO::getPid)
              .map(
                  dealPid ->
                      ruleTargetRepository.findOne(
                          withDealPidAndTargetType(dealPid, RuleTargetType.BUYER_SEATS)))
              .map(ruleTarget -> ruleTarget.orElse(null))
              .anyMatch(
                  target -> {
                    if (target == null) {
                      return false;
                    }
                    var modalData = convertObject(target.getData());
                    if (target.getMatchType() == MatchType.EXCLUDE_LIST) {
                      return validateExclude(covertDiscountDsp, modalData);
                    } else {
                      return validateInclude(covertDiscountDsp, modalData);
                    }
                  });
    }

    if (!isValid) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate("Invalid Deal for given DSP/Buyer Seat combination")
          .addConstraintViolation();
    }
    return isValid;
  }

  private List<AbstractBidderValidator.BidderSeat> convertObject(String data) {
    try {
      return Arrays.asList(
          objectMapper.readValue(data, AbstractBidderValidator.BidderSeat[].class));
    } catch (JsonProcessingException e) {
      return Collections.emptyList();
    }
  }

  private boolean validateInclude(
      Map<String, Set<String>> discountDSPs,
      List<AbstractBidderValidator.BidderSeat> dataBaseBidderSeats) {

    return dataBaseBidderSeats.stream()
        .filter(
            bidderSeat -> discountDSPs.containsKey(String.valueOf(bidderSeat.getBuyerCompany())))
        .anyMatch(
            bidderSeat ->
                bidderSeat.getSeats() == null
                    || bidderSeat
                        .getSeats()
                        .containsAll(
                            discountDSPs.get(String.valueOf(bidderSeat.getBuyerCompany()))));
  }

  private boolean validateExclude(
      Map<String, Set<String>> discountDSPs,
      List<AbstractBidderValidator.BidderSeat> dataBaseBidderSeats) {

    return dataBaseBidderSeats.stream()
        .filter(
            bidderSeat -> discountDSPs.containsKey(String.valueOf(bidderSeat.getBuyerCompany())))
        .noneMatch(
            bidderSeat ->
                bidderSeat.getSeats() == null
                    || Collections.disjoint(
                        bidderSeat.getSeats(),
                        discountDSPs.get(String.valueOf(bidderSeat.getBuyerCompany()))));
  }

  private HashMap<String, Set<String>> convertDTOToMap(
      List<PostAuctionDiscountDspDTO> discountDSPs) {
    return discountDSPs.stream()
        .reduce(
            new HashMap<String, Set<String>>(),
            (acc, discountDsp) -> {
              var buyerSeats =
                  discountDsp.getDspSeats().stream()
                      .map(buyerSeat -> String.valueOf(buyerSeat.getPid()))
                      .collect(Collectors.toSet());
              acc.put(String.valueOf(discountDsp.getCompanyPid()), buyerSeats);
              return acc;
            },
            (map, mapIdentity) -> map);
  }

  @Override
  public boolean needLazy() {
    return true;
  }
}
