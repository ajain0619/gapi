package com.nexage.app.mapper.deal;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DealTarget;
import com.nexage.admin.core.sparta.jpa.model.DealBidder;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealProfile;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBidderDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.Utils;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface DirectDealExtensionDTOMapper {

  @Named("convertPublishers")
  default List<DealPublisherDTO> convertPublishers(List<DealPublisher> publishers) {
    if (publishers == null) {
      return List.of();
    }
    return publishers.stream()
        .map(
            publisher -> {
              DealPublisherDTO.Builder builder = new DealPublisherDTO.Builder();
              builder.setPid(publisher.getPid()).setPublisherPid(publisher.getPubPid());
              return builder.build();
            })
        .collect(Collectors.toList());
  }

  @Named("convertDealRules")
  default Set<DealRuleDTO> convertDealRules(List<DealRule> rules) {
    if (rules == null) {
      return Set.of();
    }
    return rules.stream()
        .map(DirectDealExtensionDTOMapper::dealRuleToDTO)
        .collect(Collectors.toSet());
  }

  @Named("convertAuctionType")
  default DirectDealDTO.AuctionType convertAuctionType(Integer auctionType) {
    return auctionType == null
        ? DirectDealDTO.AuctionType.NONE
        : DirectDealDTO.AuctionType.fromInt(auctionType);
  }

  @Named("convertDealPositions")
  default List<DealPositionDTO> convertDealPositions(List<DealPosition> positions) {
    if (positions == null) {
      return List.of();
    }
    return positions.stream()
        .map(DirectDealExtensionDTOMapper::dealPositionToDTO)
        .collect(Collectors.toList());
  }

  @Named("convertSites")
  default List<DealSiteDTO> convertSites(List<DealSite> sites) {
    if (sites == null) {
      return List.of();
    }
    return sites.stream()
        .map(
            site -> {
              DealSiteDTO.Builder builder = new DealSiteDTO.Builder();
              builder.setPid(site.getPid()).setSitePid(site.getSitePid());
              return builder.build();
            })
        .collect(Collectors.toList());
  }

  @Named("filterAndConvertProfilesToDtos")
  default List<RTBProfileDTO> filterAndConvertProfilesToDtos(List<DealProfile> profiles) {
    return profiles.stream()
        .filter(this::hasViewForArchivedProfile)
        .map(profile -> convertViewToRTBProfileDTO(profile.getPid(), profile.getRtbProfile()))
        .collect(Collectors.toList());
  }

  @Named("convertBidders")
  default List<DealBidderDTO> convertBidders(List<DealBidder> bidders) {
    if (bidders == null) {
      return List.of();
    }
    return bidders.stream()
        .map(
            bidder -> {
              DealBidderDTO.Builder builder = new DealBidderDTO.Builder();
              builder
                  .setPid(bidder.getPid())
                  .setBidderPid(bidder.getBidderConfig().getPid())
                  .setFilterSeats(Utils.getListFromCommaSeparatedString(bidder.getFilterSeats()))
                  .setFilterAdomains(
                      Utils.getListFromCommaSeparatedString(bidder.getFilterAdomains()));
              return builder.build();
            })
        .collect(Collectors.toList());
  }

  @Named("convertTargets")
  default Set<DealTargetDTO> convertTargets(Set<DealTarget> dealTargets) {
    if (dealTargets != null && !dealTargets.isEmpty()) {
      return convertDealTargets(dealTargets);
    }
    return Set.of();
  }

  static PlacementFormulaDTO placementFormulaMake(String placementFormulaString) {
    CustomObjectMapper objectMapper = new CustomObjectMapper();
    if (placementFormulaString == null) {
      return null;
    }
    try {
      return objectMapper.readValue(placementFormulaString, PlacementFormulaDTO.class);
    } catch (IOException e) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }

  private boolean hasViewForArchivedProfile(DealProfile profile) {
    DealRtbProfileViewUsingFormulas view = profile.getRtbProfile();
    return view.getStatus() == null
        || view.getStatus() == Status.ACTIVE.asInt()
        || view.getStatus() == Status.INACTIVE.asInt();
  }

  private static DealRuleDTO dealRuleToDTO(DealRule rule) {
    return new DealRuleDTO.Builder().setRulePid(rule.getRulePid()).setPid(rule.getPid()).build();
  }

  private static DealPositionDTO dealPositionToDTO(DealPosition p) {
    return new DealPositionDTO.Builder()
        .setPositionPid(p.getPositionPid())
        .setPid(p.getPid())
        .build();
  }

  private static RTBProfileDTO convertViewToRTBProfileDTO(
      Long dealProfilePid, DealRtbProfileViewUsingFormulas rtbProfile) {
    Long pid = (dealProfilePid != null ? dealProfilePid : rtbProfile.getPid());

    RTBProfileDTO rtbProfileDTO = RTBProfileDTOMapper.MAPPER.map(rtbProfile);
    rtbProfileDTO.setPid(pid);

    return rtbProfileDTO;
  }

  private Set<DealTargetDTO> convertDealTargets(Set<DealTarget> targets) {
    return targets.stream()
        .map(
            target -> {
              DealTargetDTO.Builder builder = new DealTargetDTO.Builder();
              builder
                  .setPid(target.getPid())
                  .setTargetType(target.getTargetType())
                  .setRuleType(target.getRuleType())
                  .setData(target.getData())
                  .setParamName(target.getParamName());
              return builder.build();
            })
        .collect(Collectors.toSet());
  }
}
