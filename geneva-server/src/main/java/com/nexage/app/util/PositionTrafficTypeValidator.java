package com.nexage.app.util;

import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class was extracted from {@link PublisherPositionAssembler} to avoid circular dependencies,
 * please, do not consider this class as a good example for new code.
 */
@Service
@Transactional
public class PositionTrafficTypeValidator {

  private final AdSourceValidator adSourceValidator;

  @Autowired
  public PositionTrafficTypeValidator(AdSourceValidator adSourceValidator) {
    this.adSourceValidator = adSourceValidator;
  }

  /**
   * @param tiers a @{link Set} of type {@link PublisherTierDTO}
   * @param site {@link Site}
   * @param position {@link Position}
   */
  public void validatePositionTiers(Set<PublisherTierDTO> tiers, Site site, Position position) {
    if (CollectionUtils.isNotEmpty(tiers)) {
      for (PublisherTierDTO publisherTier : tiers) {
        this.validatePositionTrafficType(site, position, publisherTier, true);
      }
    }
  }

  /**
   * @param site {@link Site}
   * @param position {@link Position}
   * @param publisherTier {@link PublisherTierDTO}
   * @param isPositionUpdateCall true if it is update call, false otherwise
   */
  public void validatePositionTrafficType(
      Site site, Position position, PublisherTierDTO publisherTier, boolean isPositionUpdateCall) {
    if (site == null || position == null || publisherTier == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    if (!TrafficType.SMART_YIELD.equals(position.getTrafficType())
        && Arrays.asList(TierType.SUPER_AUCTION, TierType.SY_DECISION_MAKER)
            .contains(publisherTier.getTierType())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION);
    } else if (TrafficType.SMART_YIELD.equals(position.getTrafficType())) {
      if (publisherTier.getTags() != null && !publisherTier.getTags().isEmpty()) {
        adSourceValidator.validateAdSourceAssignedToTiers(
            site, position, publisherTier, isPositionUpdateCall);
      }
    }
  }
}
