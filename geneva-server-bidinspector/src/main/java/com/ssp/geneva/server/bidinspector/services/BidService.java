package com.ssp.geneva.server.bidinspector.services;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BidService {

  /**
   * Find all {@link BidDTO} under request criteria
   *
   * @param multiValueQueryParams multi value query params passed for filtering
   * @param pageable the pagination
   * @return {@link Page} of {@link BidDTO} instance based on parameters
   */
  Page<BidDTO> getBidDetails(MultiValueQueryParams multiValueQueryParams, Pageable pageable);

  /**
   * Find all {@link AuctionDetailDTO} under request criteria
   *
   * @param multiValueQueryParams multi value query params passed for filtering
   * @param pageable the pagination
   * @param auctionRunId the auctionRunId
   * @return {@link Page} of {@link AuctionDetailDTO} instance based on parameters
   */
  Page<AuctionDetailDTO> getAuctionDetails(
      MultiValueQueryParams multiValueQueryParams, String auctionRunId, Pageable pageable);
}
