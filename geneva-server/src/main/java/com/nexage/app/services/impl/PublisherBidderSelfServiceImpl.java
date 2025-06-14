package com.nexage.app.services.impl;

import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.nexage.app.dto.deals.DealBidderDTO;
import com.nexage.app.services.BuyerService;
import com.nexage.app.services.PublisherBidderSelfService;
import com.nexage.app.services.PublisherRtbProfileLibrarySelfService;
import com.nexage.app.services.RuleDSPService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class was originally on {@link PublisherSelfServiceImpl}. it has been decouple to deal with
 * certain circular dependency injection.
 */
@Legacy
@Log4j2
@Service("publisherSelfBidderService")
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeller() OR @loginUserContext.isOcApiSeller()")
public class PublisherBidderSelfServiceImpl implements PublisherBidderSelfService {

  private final BuyerService buyerService;
  private final RuleDSPService ruleDSPService;
  private final PublisherRtbProfileLibrarySelfService publisherRtbProfileLibrarySelfService;

  @Autowired
  public PublisherBidderSelfServiceImpl(
      BuyerService buyerService,
      RuleDSPService ruleDSPService,
      PublisherRtbProfileLibrarySelfService publisherRtbProfileLibrarySelfService) {
    this.buyerService = buyerService;
    this.ruleDSPService = ruleDSPService;
    this.publisherRtbProfileLibrarySelfService = publisherRtbProfileLibrarySelfService;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public List<BidderSummaryDTO> getBidders(long publisherPid) {
    List<BidderSummaryDTO> bidders = buyerService.getAllBidderSummaries();
    return filterForEligibleBidders(publisherPid, bidders);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public List<RuleDSPBiddersDTO> getRuleDSPBidders(long publisherPid) {
    List<RuleDSPBiddersDTO> allDSPsWithBidders = ruleDSPService.findAll();

    Set<Long> eligibleBidders =
        publisherRtbProfileLibrarySelfService.getEligibleBidders(publisherPid);
    if (eligibleBidders.isEmpty()) {
      return allDSPsWithBidders;
    }

    List<RuleDSPBiddersDTO> filteredForEligibleBidders = new ArrayList<>();
    allDSPsWithBidders.forEach(
        eachDSP -> {
          Set<DealBidderDTO> bidders = eachDSP.getBidders();
          bidders.removeIf(bidder -> !eligibleBidders.contains(bidder.getPid()));
          if (!bidders.isEmpty()) {
            filteredForEligibleBidders.add(eachDSP);
          }
        });
    return filteredForEligibleBidders;
  }

  private List<BidderSummaryDTO> filterForEligibleBidders(
      Long publisherPid, List<BidderSummaryDTO> bidders) {
    Set<Long> eligibleBidders =
        publisherRtbProfileLibrarySelfService.getEligibleBidders(publisherPid);
    if (eligibleBidders.isEmpty()) {
      return bidders;
    }
    return bidders.stream()
        .filter(e -> eligibleBidders.contains(e.getPid()))
        .collect(Collectors.toList());
  }
}
