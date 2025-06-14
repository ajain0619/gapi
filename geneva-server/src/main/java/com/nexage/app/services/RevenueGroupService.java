package com.nexage.app.services;

import com.nexage.app.dto.RevenueGroupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RevenueGroupService {

  /**
   * Fetch all revenue groups
   *
   * @param pageable page values
   * @return Paged revenue groups
   */
  Page<RevenueGroupDTO> getRevenueGroups(Pageable pageable);
}
