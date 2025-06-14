package com.nexage.app.services.validation;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.RuleTargetRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.DirectDealDTO.AuctionType;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.validation.sellingrule.ZeroCostDealValidator;
import com.nexage.app.util.validator.deals.DealPlacementFormulaAttributesValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealValidator {

  private static final double MAX_DEAL_FLOOR = 1000.0;

  private final BeanValidationService beanValidationService;
  private final CompanyRuleRepository companyRuleRepository;
  private final ZeroCostDealValidator zeroCostDealValidator;
  private final RuleTargetRepository ruleTargetRepository;
  private final DealPlacementFormulaAttributesValidator dealPlacementFormulaAttributesValidator;

  public void validateAndFixDeal(DirectDealDTO deal) {
    validateDealDtoFloorBounds(deal);

    if (Objects.nonNull(deal.getDealCategory())
        && deal.getDealCategory() == DealCategory.S2S_PLACEMENT_DEAL.asInt()) {
      validateS2SPlacementDeal(deal);
    }

    // strictly limit at max only 1 rule can be associated to a deal
    validateDealHasOneRule(deal);

    validateDealRuleDetails(deal);

    validatePacing(deal);

    if (deal.getPriorityType() == null) {
      DealPriorityType priorityType = getDealPriorityType(deal.getAuctionType());
      deal.setPriorityType(priorityType);
    }

    if (deal.getPriorityType() != null) {
      validateImpressionFields(deal);
    }

    beanValidationService.validate(deal);
  }

  private void validateS2SPlacementDeal(DirectDealDTO dealDto) {
    for (DealRuleDTO dealRuleDto : dealDto.getRules()) {
      if (BooleanUtils.isTrue(ruleTargetRepository.hasRuleTarget(dealRuleDto.getRulePid()))) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DEAL_CATEGORY);
      }
    }
  }

  public void validateForFormula(DirectDealDTO dealDto) {
    dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
        dealDto.getPlacementFormula());

    if (dealDto.getPlacementFormula() != null) {
      if ((dealDto.getPositions() != null && !dealDto.getPositions().isEmpty())
          || (dealDto.getSellers() != null && !dealDto.getSellers().isEmpty())
          || (dealDto.getSites() != null && !dealDto.getSites().isEmpty())) {

        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEAL_CANNOT_USE_FORMULA_AND_EXPLICIT_ASSIGNMENT);
      }
      if (dealDto.getAutoUpdate() == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEAL_AUTO_UPDATE_FLAG_CANNOT_BE_NULL);
      }
    }
    if (dealDto.getPlacementFormula() == null && dealDto.getAutoUpdate() != null) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DEAL_AUTO_UPDATE_FLAG_CANNOT_BE_USED_WITHOUT_FORMULA);
    }
  }

  public void validateTarget(DealTargetDTO dealTargetDTO) {
    if (dealTargetDTO.getTargetType() == null
        || dealTargetDTO.getRuleType() == null
        || StringUtils.isBlank(dealTargetDTO.getData())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_MANDATORY_TARGET_FIELDS_MISSING);
    }
  }

  private void validateDealHasOneRule(DirectDealDTO deal) {
    if (deal.getRules() != null && deal.getRules().size() > 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_HAS_MORE_THAN_ONE_RULE);
    }
  }

  private void validateDealRuleDetails(DirectDealDTO deal) {
    if (deal.getRules() != null && !deal.getRules().isEmpty()) {
      DealRuleDTO dealRule = deal.getRules().iterator().next();
      companyRuleRepository
          .findById(dealRule.getRulePid())
          .ifPresent(
              companyRule -> {
                if (deal.getStatus().asInt() != companyRule.getStatus().asInt()) {
                  throw new GenevaValidationException(
                      ServerErrorCodes.SERVER_DEAL_AND_RULE_STATUS_NOT_THE_SAME);
                }
              });
    }
  }

  private void validateDealDtoFloorBounds(DirectDealDTO dealDto) {
    if (dealDto.getFloor() != null
        && (dealDto.getFloor().doubleValue() < 0.0
            || dealDto.getFloor().doubleValue() > MAX_DEAL_FLOOR)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID);
    }
  }

  private void validateImpressionFields(DirectDealDTO dealDTO) {
    zeroCostDealValidator.validateZeroCostDeals(dealDTO);
  }

  private DealPriorityType getDealPriorityType(AuctionType auctionType) {
    return DealPriorityType.OPEN;
  }

  private void validatePacing(DirectDealDTO deal) {
    if (Boolean.TRUE.equals(deal.getPacingEnabled())) {
      if (deal.getPacingStrategy() == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_PACING_ENABLED_REQUIRE_PACING_STRATEGY);
      }
    } else {
      if (deal.getPacingStrategy() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_PACING_STRATEGY_REQUIRE_PACING_ENABLED);
      }
    }
  }

  public void validateTargetsAllowedForExternalDeal(DirectDeal deal) {
    if (CollectionUtils.isEmpty(deal.getRules())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND);
    }

    Long rulePid = deal.getRules().get(0).getRulePid();

    if (BooleanUtils.isTrue(
        ruleTargetRepository.hasRuleTargetOtherThanProvided(rulePid, RuleTargetType.BUYER_SEATS)))
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVALID_EXTERNAL_DEAL_TARGET_DATA);
  }
}
