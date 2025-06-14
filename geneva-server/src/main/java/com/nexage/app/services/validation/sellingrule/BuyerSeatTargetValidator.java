package com.nexage.app.services.validation.sellingrule;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.BuyerSeatRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@Log4j2
@Component
public class BuyerSeatTargetValidator extends AbstractBidderValidator
    implements RuleTargetValidatorRegistry.Validator {

  private final GlobalConfigService globalConfigService;
  private final CompanyRepository companyRepository;
  private final BuyerSeatRepository buyerSeatRepository;
  private final BuyerGroupRepository buyerGroupRepository;
  private final BidderConfigRepository bidderConfigRepository;

  private boolean validateSeatExistence = false;

  public BuyerSeatTargetValidator(
      BidderConfigRepository bidderConfigRepository,
      GlobalConfigService globalConfigService,
      CompanyRepository companyRepository,
      BuyerSeatRepository buyerSeatRepository,
      BuyerGroupRepository buyerGroupRepository) {
    this.bidderConfigRepository = bidderConfigRepository;
    this.globalConfigService = globalConfigService;
    this.companyRepository = companyRepository;
    this.buyerSeatRepository = buyerSeatRepository;
    this.buyerGroupRepository = buyerGroupRepository;
  }

  @Override
  public void accept(RuleTargetDTO target) {
    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(target);
    List<BidderSeat> bidderSeats = extractBidderSeat(target);
    validateBidders(bidderSeats);
    bidderSeats.forEach(this::validateBuyerGroupsAndSeats);
  }

  private void validateBuyerGroupsAndSeats(BidderSeat bidderSeat) {
    Set<Long> buyerGroups = bidderSeat.getBuyerGroups();
    if (buyerGroups != null) {
      if (buyerGroups.isEmpty()) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_GROUP_EMPTY_LIST);
      }
      long buyerGroupCount =
          (bidderSeat.getBuyerCompany() == null)
              ? bidderConfigRepository.countByPidAndCompany_buyerGroups_pidIn(
                  bidderSeat.getBidder(), buyerGroups)
              : buyerGroupRepository.countByCompanyPidAndPidIn(
                  bidderSeat.getBuyerCompany(), buyerGroups);
      if (buyerGroupCount < buyerGroups.size()) {
        if (log.isErrorEnabled()) {
          log.error("Some buyer groups do not exist: {}", toJson(bidderSeat));
        }
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_GROUP_NOT_FOUND);
      }
    }

    Set<String> seats = bidderSeat.getSeats();
    if (seats != null) {
      if (seats.isEmpty()) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_SEAT_EMPTY_LIST);
      }
      if (validateSeatExistence) {
        long seatCount =
            (bidderSeat.getBuyerCompany() == null)
                ? bidderConfigRepository.countByPidAndCompany_buyerSeats_seatIn(
                    bidderSeat.getBidder(), seats)
                : buyerSeatRepository.countByCompanyPidAndSeatIn(
                    bidderSeat.getBuyerCompany(), bidderSeat.getSeats());
        if (seatCount < seats.size()) {
          if (log.isErrorEnabled()) {
            log.error("Some seats do not exist: {}", toJson(bidderSeat));
          }
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_SEAT_NOT_FOUND);
        }
      }
    }
  }

  private void validateBidders(List<BuyerSeatTargetValidator.BidderSeat> bidderSeats) {
    if (bidderSeats == null || bidderSeats.isEmpty()) {
      log.error("Buyer seat target has no bidders or seats");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_EMPTY_LIST);
    }

    Set<Long> companyPids =
        bidderSeats.stream()
            .map(BidderSeat::getBuyerCompany)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    if (!companyPids.isEmpty()) {
      long companyCount = companyRepository.countByPids(companyPids);
      if (companyCount < companyPids.size()) {
        log.error(
            "Some company pids do not exist. Count: {}, expected: {}",
            companyCount,
            companyPids.size());
        throw new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND);
      }
    }

    if (companyPids.isEmpty()) {
      Set<Long> bidderPids =
          bidderSeats.stream()
              .map(
                  bidderSeat -> {
                    if (bidderSeat.getBidder() == null) {
                      if (log.isErrorEnabled()) {
                        log.error("Bidder pid required but missing: {}", toJson(bidderSeat));
                      }
                      throw new GenevaValidationException(
                          ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_NULL_BIDDER);
                    }
                    return bidderSeat.getBidder();
                  })
              .collect(Collectors.toSet());

      if (bidderPids.size() < bidderSeats.size()) {
        log.error(
            "Some bidders are not unique. Count: {}, expected: {}",
            bidderPids.size(),
            bidderSeats.size());
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_DUPLICATED_BIDDER);
      }

      long bidderCount = bidderConfigRepository.countByPidIn(bidderPids);
      if (bidderCount < bidderPids.size()) {
        log.error(
            "Some bidders do not exist. Count: {}, expected: {}", bidderCount, bidderPids.size());
        throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_NOT_FOUND);
      }
    }

    bidderSeats.forEach(
        bidderSeat -> {
          if (bidderSeat.getBidders() != null
              && !bidderSeat.getBidders().isEmpty()
              && bidderSeat.getBuyerCompany() != null) {
            Set<Long> bidderPids =
                bidderConfigRepository.findPidsByCompanyPid(bidderSeat.getBuyerCompany());
            if (bidderPids != null && !bidderPids.isEmpty()) {
              bidderSeat.getBidders().stream()
                  .filter(inboundPid -> !bidderPids.contains(inboundPid))
                  .forEach(
                      pid -> {
                        log.error(
                            "bidders pids in DB : {} and bidder pids sent by client {} do not belong to buyer company with pid: {}",
                            bidderPids,
                            bidderSeat.getBidders(),
                            bidderSeat.getBuyerCompany());
                        throw new GenevaValidationException(
                            ServerErrorCodes.SERVER_BIDDER_PID_DOES_NOT_BELONG_TO_BUYER_COMPANY);
                      });
            }
          }
        });
  }

  @PostConstruct
  public void init() {
    validateSeatExistence =
        Boolean.TRUE.equals(
            globalConfigService.getBooleanValue(
                GlobalConfigProperty.BUYER_SEAT_EXISTENCE_VALIDATION_ENABLED));

    log.info("Registering buyer seat target validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.BUYER_SEATS, this);
  }
}
