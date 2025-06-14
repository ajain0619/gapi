package com.nexage.app.services;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerDealService {

  /**
   * Find all {@link DirectDeal}s associated with a seller via their assigned inventory. Assigned
   * inventory can include a direct association to a publisher (see {@link DealPublisher}, an
   * association between the deal and a publisher's site (see {@link DealSite}) or an association
   * between the deal and a site's position (see {@link DealPosition}).
   *
   * @param sellerId PID of the {@link Company}
   * @param qf {@link Set} of fields to query by. May include 'dealId' or 'description' only
   * @param qt Term to use in query when qf parameter is specified
   * @param pageable {@link Pageable} to use for pagination
   */
  Page<DirectDeal> getPagedDealsAssociatedWithSeller(
      Long sellerId, Set<String> qf, String qt, Pageable pageable);

  /**
   * Find the {@link DirectDeal} associated with a seller via their assigned inventory.
   *
   * @param sellerId PID of the {@link Company}
   * @param pid PID of the {@link DirectDeal}
   * @return the {@link DirectDealDTO} that is returned from the call
   */
  DirectDealDTO getDealAssociatedWithSeller(Long sellerId, Long pid);

  /**
   * Get sites names and pids plus positions names and pids assigned to the given {@link DirectDeal}
   * and belonging to the sellers {@link Company}.
   *
   * @param sellerId PID of the {@link Company}
   * @param dealId PID of the {@link DirectDeal}
   */
  List<PublisherSitePositionDTO> getPublisherMapForDeal(Long sellerId, Long dealId);

  /**
   * Create a {@link DirectDealDTO} associated with a seller.
   *
   * @param sellerId PID of the {@link Company}
   * @body directDealDTO {@DirectDealDTO}
   * @return the {@link DirectDealDTO} that is returned from the call
   */
  DirectDealDTO createDealAssociatedWithSeller(Long sellerId, DirectDealDTO directDealDTO);

  /**
   * Create a {@link DirectDealDTO} associated with a seller.
   *
   * @param sellerId PID of the {@link Company}
   * @param pid PID of the {@link DirectDeal}
   * @body directDealDTO {@DirectDealDTO}
   * @return the {@link DirectDealDTO} that is returned from the call
   */
  DirectDealDTO updateDealAssociatedWithSeller(
      Long sellerId, Long pid, DirectDealDTO directDealDTO);
}
