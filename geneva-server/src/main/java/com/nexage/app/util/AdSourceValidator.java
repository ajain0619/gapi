package com.nexage.app.util;

import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.AdSource.BidEnabled;
import com.nexage.admin.core.model.AdSource.DecisionMakerEnabled;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.AdSourceService;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class was extracted from {@link PublisherPositionAssembler} to avoid circular dependencies,
 * please, do not consider this class as a good example for new code.
 */
@Component
@Transactional
public class AdSourceValidator {

  private final AdSourceService adSourceService;

  @Autowired
  public AdSourceValidator(AdSourceService adSourceService) {
    this.adSourceService = adSourceService;
  }

  public void validateAdSourceAssignedToTiers(
      Site site, Position pos, PublisherTierDTO publisherTier, boolean isPositionUpdateCall) {
    if (publisherTier.getTags() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    Map<Long, Long> buyerPids = new HashMap<>();
    List<Long> pubTagPids =
        publisherTier.getTags().stream().map(PublisherTagDTO::getPid).collect(Collectors.toList());

    // all the PublisherTags in the PublisherTier, should have been created and exists for the site,
    // at the time of tier creation
    // get the buyerPids(adsource pids) from tags for each publisherTag in the PublisherTier
    if (!pubTagPids.isEmpty()) {
      site.getTags()
          .forEach(
              tag -> {
                if (pubTagPids.contains(tag.getPid())) {
                  buyerPids.put(tag.getPid(), tag.getBuyerPid());
                }
              });
    }

    Map<Long, AdSource> pubTagPidAdSourcesMap =
        adSourceService.getAdSourcesUsedForTierTags(buyerPids);

    if (TierType.SY_DECISION_MAKER.equals(publisherTier.getTierType())) {
      validateDecisionMakerTier(publisherTier, isPositionUpdateCall);

      for (PublisherTagDTO tag : publisherTier.getTags()) {
        validatePublisherTagDecisionMaker(pubTagPidAdSourcesMap, tag);
      }
    } else if (TierType.SUPER_AUCTION.equals(publisherTier.getTierType())) {
      verifyDecisionMakerTierIsPresent(pos);

      for (PublisherTagDTO tag : publisherTier.getTags()) {
        validatePublisherTagBid(pubTagPidAdSourcesMap, tag);
      }
    }
  }

  private void verifyDecisionMakerTierIsPresent(Position pos) {
    if (pos.getTiers().stream()
        .noneMatch(t -> t.getTierType().equals(TierType.SY_DECISION_MAKER))) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_TIER_DECISION_MAKER_DOES_NOT_EXIST);
    }
  }

  private void validateDecisionMakerTier(
      PublisherTierDTO publisherTier, boolean isPositionUpdateCall) {
    if (isPositionUpdateCall && publisherTier.getLevel() != 0) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DECISION_MAKER_TIER_SHOULD_BE_LEVEL_ZERO);
    }

    if (publisherTier.getTags().size() > 1) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DECISION_MAKER_TIER_SHOULD_CONTAIN_ONE_TAG);
    }
  }

  private void validatePublisherTagBid(
      Map<Long, AdSource> pubTagPidAdSourcesMap, PublisherTagDTO tag) {
    if (pubTagPidAdSourcesMap == null || !pubTagPidAdSourcesMap.containsKey(tag.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND);
    } else if (BidEnabled.YES != pubTagPidAdSourcesMap.get(tag.getPid()).getBidEnabled()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED);
    }
  }

  private void validatePublisherTagDecisionMaker(
      Map<Long, AdSource> pubTagPidAdSourcesMap, PublisherTagDTO tag) {
    if (pubTagPidAdSourcesMap == null || !pubTagPidAdSourcesMap.containsKey(tag.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND);
    } else if (DecisionMakerEnabled.YES
        != pubTagPidAdSourcesMap.get(tag.getPid()).getDecisionMakerEnabled()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED);
    }
  }
}
