package com.nexage.app.services;

import com.nexage.app.dto.BidderConfigDTO;
import com.nexage.app.dto.BidderConfigDTOView;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Defines business logic operations for interacting with {@link BidderConfigDTO}. */
public interface BidderConfigDTOService {

  /**
   * Create {@link BidderConfigDTO} belonging to the dsp
   *
   * @param dspPid pid of the dsp
   * @param bidderConfigDTO create payload
   * @return the created {@link BidderConfigDTO}
   */
  BidderConfigDTO create(Long dspPid, BidderConfigDTO bidderConfigDTO);

  /**
   * Get {@link BidderConfigDTO} by pid
   *
   * @param dspPid pid of the dsp the bidder config belongs to
   * @param bidderConfigPid pid of the bidder config to find
   * @return the found {@link BidderConfigDTO}
   */
  BidderConfigDTO get(Long dspPid, Long bidderConfigPid);

  /**
   * Update {@link BidderConfigDTO} belonging to the dsp
   *
   * @param dspPid pid of the dsp
   * @param bidderConfigPid pid of the bidder config
   * @param bidderConfigDTO update payload
   * @return the updated {@link BidderConfigDTO}
   */
  BidderConfigDTO update(Long dspPid, Long bidderConfigPid, BidderConfigDTO bidderConfigDTO);

  /**
   * Find all {@link BidderConfigDTOView} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link BidderConfigDTOView} instances based on parameters.
   */
  Page<BidderConfigDTOView> findAllBidderConfigs(
      Long dspPid, Set<String> qf, String qt, Pageable pageable);
}
