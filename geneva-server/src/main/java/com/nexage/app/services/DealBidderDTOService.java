package com.nexage.app.services;

import com.nexage.app.dto.deals.DealBidderDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface DealBidderDTOService {

  /**
   * Find all {@link DealBidderDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link DealBidderDTO} instances based on parameters.
   */
  Page<DealBidderDTO> findAll(Set<String> qf, String qt, Pageable pageable);
}
