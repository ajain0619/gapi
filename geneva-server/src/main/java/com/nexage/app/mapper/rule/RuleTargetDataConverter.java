package com.nexage.app.mapper.rule;

import static java.util.Objects.isNull;

import com.google.common.collect.Sets;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.services.validation.sellingrule.AbstractBidderValidator.BidderSeat;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class RuleTargetDataConverter {

  private final CustomObjectMapper objectMapper;
  private final BidderConfigRepository bidderConfigRepository;

  public String legacyTargetDataToTargetData(String legacyData) {
    if (StringUtils.isBlank(legacyData)) {
      return null;
    }
    try {
      List<BidderSeat> buyerCompanyTargetData =
          Arrays.asList(objectMapper.readValue(legacyData, BidderSeat[].class));

      for (BidderSeat bs : buyerCompanyTargetData) {
        if (isNull(bs.getBuyerCompany())) {
          Long bidderPid = bs.getBidder();
          if (bidderPid != null) {
            bs.setBuyerCompany(bidderConfigRepository.findCompanyPidByPid(bidderPid));
            bs.setBidders(Sets.newHashSet(bidderPid));
          }
        }
      }
      legacyData = objectMapper.writeValueAsString(buyerCompanyTargetData);

    } catch (IOException e) {
      log.warn("Failed to convert string data to object : {}", legacyData);
    }

    return legacyData;
  }
}
