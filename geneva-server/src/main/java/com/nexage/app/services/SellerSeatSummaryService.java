package com.nexage.app.services;

import com.nexage.app.dto.seller.SellerSummaryDTO;
import java.util.Date;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerSeatSummaryService {

  /**
   * Find all {@link SellerSummaryDTO} associated with seller seat under request criteria, returning
   * a paginated response.
   *
   * @param sellerSeatPid
   * @param startDate
   * @param stopDate
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SellerSummaryDTO} instances based on parameters.
   */
  Page<SellerSummaryDTO> findSummary(
      Long sellerSeatPid,
      Date startDate,
      Date stopDate,
      Set<String> qf,
      String qt,
      Pageable pageable);
}
