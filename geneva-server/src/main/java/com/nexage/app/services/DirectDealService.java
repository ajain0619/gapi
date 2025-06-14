package com.nexage.app.services;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBuyerDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DirectDealService {

  DirectDealDTO createDeal(DirectDealDTO deal);

  List<DirectDealDTO> getAllDeals();

  List<DirectDealDTO> getAllDealsWithRules();

  Page<DealDTO> getPagedDealsWithRules(Optional<String> qt, Pageable pageable);

  DirectDealDTO getDeal(long dealPid);

  List<PublisherSitePositionDTO> getPublisherMapForDeal(long dealPid);

  List<PublisherSitePositionDTO> getPublisherMapForDeal(
      List<DealSite> sites, List<DealPosition> positions);

  DirectDealDTO updateDeal(long dealPid, DirectDealDTO deal);

  /**
   * Update status of existing deal
   *
   * @param dealPid a deal pid
   * @param status a new status
   */
  void updateDealStatus(long dealPid, DirectDeal.DealStatus status);

  RTBProfileDTO getSupplier(long pid);

  List<RTBProfileDTO> getAllNonarchivedSuppliers();

  List<DealBuyerDTO> getAllBuyers();

  List<SellerRuleDTO> findRulesByDealPid(Long pid);
}
