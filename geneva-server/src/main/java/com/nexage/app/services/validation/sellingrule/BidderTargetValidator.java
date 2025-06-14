package com.nexage.app.services.validation.sellingrule;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Arrays;
import java.util.List;
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
public class BidderTargetValidator extends AbstractBidderValidator
    implements RuleTargetValidatorRegistry.Validator {

  private final BidderConfigRepository bidderConfigRepository;

  public BidderTargetValidator(BidderConfigRepository bidderConfigRepository) {
    this.bidderConfigRepository = bidderConfigRepository;
  }

  @Override
  public void accept(RuleTargetDTO target) {
    RuleTargetValidatorRegistry.COMMA_SEPARATED_NUMBERS_VALIDATOR.accept(target);
    List<Long> bidderPids =
        Arrays.stream(target.getData().trim().split(","))
            .map(t -> Long.valueOf(t))
            .collect(Collectors.toList());
    Set<Long> uniqueBidderPids = Sets.newHashSet(bidderPids);
    long bidderCount = bidderConfigRepository.countByPidIn(uniqueBidderPids);

    if (bidderCount < uniqueBidderPids.size()) {
      log.error(
          "Some bidders do not exist. Count: {}, expected: {}",
          uniqueBidderPids,
          bidderPids.size());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_NOT_FOUND);
    }

    if (uniqueBidderPids.size() != bidderPids.size()) {
      log.error(
          "Some bidder pids are not uniqe. Count: {}, expected: {}", bidderPids, uniqueBidderPids);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_PID_NOT_UNIQUE);
    }
  }

  @PostConstruct
  public void init() {
    log.info("Registering bidder target validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.BIDDER, this);
  }
}
