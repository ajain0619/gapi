package com.nexage.app.services.validation.sellingrule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractBidderValidator implements RuleTargetValidatorRegistry.Validator {

  protected final ObjectMapper objectMapper = new ObjectMapper();

  private final TypeReference<List<BidderSeat>> bidderSeatListType =
      new TypeReference<List<BidderSeat>>() {};

  protected List<BidderSeat> extractBidderSeat(RuleTargetDTO target) {
    List<BidderSeat> bidderSeats;
    try {
      bidderSeats = objectMapper.readValue(target.getData(), bidderSeatListType);
    } catch (IOException e) {
      if (log.isErrorEnabled()) {
        log.error(
            String.format(
                "Invalid JSON for buyer seat target data: %s, %s",
                target.getData(), e.getMessage()),
            e);
      }
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT);
    }
    return bidderSeats;
  }

  protected String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return "[Unable to write as JSON]";
    }
  }

  @Getter
  @Setter
  public static class BidderSeat {
    private Long buyerCompany;
    private Set<Long> bidders;
    private Long bidder;
    private Set<Long> buyerGroups;
    private Set<String> seats;
  }
}
