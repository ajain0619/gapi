package com.nexage.app.services;

import com.nexage.app.dto.seller.SellerDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerDTOService {

  /**
   * Find all {@link SellerDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SellerDTO} instances based on parameters.
   */
  Page<SellerDTO> findAll(Set<String> qf, String qt, boolean isRtbEnabled, Pageable pageable);
  /**
   * GET resource to retrieve a single {@link SellerDTO} based on request.
   *
   * @return {@link SellerDTO}.
   */
  SellerDTO findOne(Long sellerPid);
}
