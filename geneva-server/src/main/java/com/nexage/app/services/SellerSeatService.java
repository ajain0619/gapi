package com.nexage.app.services;

import com.nexage.admin.core.model.SellerSeat;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SellerSeatService {
  /**
   * Retrieve information about a single seller seat
   *
   * @param pid Seller seat ID
   * @return requested {@link SellerSeat} instance
   */
  SellerSeatDTO getSellerSeat(Long pid);

  /**
   * Creates a new seller seat in the system.
   *
   * @param sellerSeatDTO Seller seat DTO instance having updated state (to be persisted)
   * @return newly created seller seat instance
   */
  SellerSeatDTO createSellerSeat(SellerSeatDTO sellerSeatDTO);

  /**
   * @param pid seller seat pid
   * @param modifiedSellerSeat State of seller seat instance having values for field to be updated.
   * @return updated seller seat
   */
  SellerSeatDTO updateSellerSeat(Long pid, SellerSeatDTO modifiedSellerSeat);

  /**
   * Retrieve information about all seller seats or all enabled seller seats with sellers
   *
   * @param assignable Filtration based on assignable
   * @param queryFields search fields allows name
   * @param queryTerm search term string
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SellerSeat} instances based on parameters.
   */
  Page<SellerSeatDTO> findAll(
      boolean assignable,
      @SearchRequestParamConstraint(allowedParams = "name") Set<String> queryFields,
      String queryTerm,
      Pageable pageable);
}
