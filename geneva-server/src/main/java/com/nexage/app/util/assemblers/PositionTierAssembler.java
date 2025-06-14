package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Tier;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Comparator;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * This class was extracted from {@link PublisherPositionAssembler} to avoid circular dependencies,
 * please, do not consider this class as a good example for new code.
 */
@Component
public class PositionTierAssembler {

  /**
   * Build the given tiers on the given position.
   *
   * @param position {@link Position}
   * @param inputTiers a @{link Set} of type {@link PublisherTierDTO}
   */
  public void handleTiers(Position position, Set<PublisherTierDTO> inputTiers) {
    if (CollectionUtils.isNotEmpty(inputTiers)) {
      buildPositionTiers(position, inputTiers);
      makePositionTiersSequential(position);
    }
  }

  private void buildPositionTiers(Position position, Set<PublisherTierDTO> inputTiers) {
    for (PublisherTierDTO currentTier : inputTiers) {
      Tier tier = getTierByPid(position, currentTier.getPid());
      tier.setLevel(currentTier.getLevel());
    }
  }

  private void makePositionTiersSequential(Position position) {
    // sort by level and ensure levels are sequential, otherwise the server will break
    // order tiers by level and update level if duplicates found
    position.getTiers().sort(Comparator.comparingInt(Tier::getLevel));
    int level = 0;
    for (Tier tr : position.getTiers()) {
      tr.setLevel(level);
      level++;
    }
  }

  private Tier getTierByPid(Position position, long pid) {
    for (Tier tier : position.getTiers()) {
      if (tier.getPid().equals(pid)) {
        return tier;
      }
    }
    throw new GenevaValidationException(ServerErrorCodes.SERVER_TIER_NOT_FOUND_IN_POSITION);
  }
}
