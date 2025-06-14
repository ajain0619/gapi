package com.nexage.app.util.assemblers.sellingrule;

import static com.nexage.admin.core.enums.RuleTargetType.BUYER_SEATS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.services.validation.sellingrule.AbstractBidderValidator.BidderSeat;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.assemblers.NoContextAssembler;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RuleTargetAssembler extends NoContextAssembler {

  private final CustomObjectMapper objectMapper;
  private final BidderConfigRepository bidderConfigRepository;

  @Autowired
  public RuleTargetAssembler(
      CustomObjectMapper objectMapper, BidderConfigRepository bidderConfigRepository) {
    this.objectMapper = objectMapper;
    this.bidderConfigRepository = bidderConfigRepository;
  }

  public static final Set<String> DEFAULT_FIELDS =
      Set.of("pid", "version", "status", "matchType", "targetType", "data");

  public RuleTargetDTO make(RuleTarget entity) {
    return make(entity, DEFAULT_FIELDS);
  }

  public RuleTargetDTO make(RuleTarget entity, Set<String> fields) {
    RuleTargetDTO.RuleTargetDTOBuilder builder = RuleTargetDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          builder.pid(entity.getPid());
          break;
        case "version":
          builder.version(entity.getVersion());
          break;
        case "status":
          builder.status(entity.getStatus());
          break;
        case "matchType":
          builder.matchType(entity.getMatchType());
          break;
        case "targetType":
          builder.targetType(entity.getRuleTargetType());
          break;
        case "data":
          if (entity.getRuleTargetType() == BUYER_SEATS) {
            builder.data(enrichWithNewFields(entity.getData()));
          } else {
            builder.data(entity.getData());
          }
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }
    return builder.build();
  }

  public RuleTarget apply(RuleTarget ruleTargetEntity, RuleTargetDTO dto, CompanyRule ruleEntity) {
    ruleTargetEntity.setPid(dto.getPid());
    ruleTargetEntity.setRuleTargetType(dto.getTargetType());
    ruleTargetEntity.setMatchType(dto.getMatchType());
    ruleTargetEntity.setStatus(dto.getStatus());
    ruleTargetEntity.setData(dto.getData());
    ruleTargetEntity.setRule(ruleEntity);
    return ruleTargetEntity;
  }

  private String enrichWithNewFields(String buyerCompanytargetData) {

    if (StringUtils.isBlank(buyerCompanytargetData)) {
      return null;
    }
    try {
      List<BidderSeat> buyerCompanyTargetData =
          Arrays.asList(objectMapper.readValue(buyerCompanytargetData, BidderSeat[].class));

      for (BidderSeat bs : buyerCompanyTargetData) {
        Long bidderPid = bs.getBidder();
        Long buyerCompanyPid = bs.getBuyerCompany();

        if (buyerCompanyPid == null) {
          Set<Long> bidders = new HashSet<>();
          if (bidderPid != null) {
            bs.setBuyerCompany(bidderConfigRepository.findCompanyPidByPid(bidderPid));
          }
          bidders.add(bidderPid);
          bs.setBidders(bidders);
        }
      }
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      buyerCompanytargetData = objectMapper.writeValueAsString(buyerCompanyTargetData);

    } catch (IOException e) {
      log.warn("Failed to convert string data to object : {}", buyerCompanytargetData);
    }

    return buyerCompanytargetData;
  }
}
