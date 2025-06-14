package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PublisherSelfServiceUtil {

  public static Position getSitePositionByPid(Site siteDTO, long pid) {
    for (Position position : siteDTO.getPositions()) {
      if (position.getPid().equals(pid)) {
        return position;
      }
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
  }

  public static Position getSitePositionByName(Site siteDTO, String name) {
    for (Position position : siteDTO.getPositions()) {
      if (position.getName().equals(name)) {
        return position;
      }
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
  }

  public static Set<Long> getTagPidsForPosition(
      Site site, Long positionPid, boolean onlyAssignedTags) {
    Position position = getSitePositionByPid(site, positionPid);
    List<Tier> tiers = position.getTiers();

    // filter decision maker tiers
    if (onlyAssignedTags) {
      tiers =
          tiers.stream()
              .filter(tier -> tier.getTierType() != TierType.SY_DECISION_MAKER)
              .collect(Collectors.toList());
    }

    // assigned tags
    Set<Long> tagPids =
        tiers.stream()
            .flatMap(tier -> tier.getTags().stream())
            .map(Tag::getPid)
            .collect(Collectors.toSet());

    // all other tags
    if (!onlyAssignedTags) {
      for (Tag tag : site.getTags()) {
        if (tag.getPosition() != null && tag.getPosition().getPid().equals(positionPid)) {
          tagPids.add(tag.getPid());
        }
      }
    }
    return tagPids;
  }

  public static PublisherTierDTO getTierByPid(Collection<PublisherTierDTO> tiers, long tierPid) {
    return tiers.stream().filter(tier -> tier.getPid() == tierPid).findFirst().orElse(null);
  }

  public static List<PublisherTierDTO> getTiersWithTag(
      Collection<PublisherTierDTO> tiers, long tagPid) {
    return tiers == null
        ? Collections.emptyList()
        : tiers.stream()
            .filter(tier -> getTagByPid(tier.getTags(), tagPid) != null)
            .collect(Collectors.toList());
  }

  public static PublisherTagDTO getTagByPid(Collection<PublisherTagDTO> tags, long tagPid) {
    return tags == null
        ? null
        : tags.stream().filter(tag -> tag.getPid() == tagPid).findFirst().orElse(null);
  }
}
