package com.nexage.app.services;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.util.validator.PostAuctionConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public interface PostAuctionDiscountService {

  /**
   * Retrieve a page of {@link PostAuctionDiscountDTO} available objects based on the query/page
   * parameters. Note that this service is intended to provide a mechanism to discover any available
   * {@link PostAuctionDiscount} entities which are available.
   *
   * @param qf A unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param discountStatus The value to match on the "enabled" field.
   * @param pageable The {@link Pageable} pagination parameters.
   * @return {@link Page} of {@link FeeAdjustmentDTO} objects based on the query/page parameters.
   */
  Page<PostAuctionDiscountDTO> getAll(
      Set<String> qf, String qt, Boolean discountStatus, Pageable pageable);

  /**
   * Retrieve the {@link PostAuctionDiscountDTO} object for the {@link PostAuctionDiscount} JPA
   * persistence entity with the specified pid.
   *
   * @param postAuctionDiscountPid The pid of the {@link PostAuctionDiscount} entity to retrieve.
   * @return A {@link PostAuctionDiscountDTO} object representing the state of the {@link
   *     PostAuctionDiscount} JPA persistence entity.
   */
  PostAuctionDiscountDTO get(Long postAuctionDiscountPid);

  /**
   * Create a {@link PostAuctionDiscount} JPA persistence entity for this {@link
   * PostAuctionDiscountDTO} object.
   *
   * @param postAuctionDiscountDTO
   * @return A new {@link FeeAdjustmentDTO} object representing the new {@link PostAuctionDiscount}
   *     JPA persistence entity.
   */
  PostAuctionDiscountDTO create(
      @PostAuctionConstraint PostAuctionDiscountDTO postAuctionDiscountDTO);

  /**
   * Update the {@link PostAuctionDiscount} JPA persistence entity corresponding to this {@link
   * PostAuctionDiscountDTO} object (looks up the entity using the pid field of the {@link
   * PostAuctionDiscountDTO} object).
   *
   * @param postAuctionDiscountDTO
   * @return A new {@link PostAuctionDiscountDTO} object representing the new state of the {@link
   *     PostAuctionDiscount} JPA persistence entity.
   */
  PostAuctionDiscountDTO update(
      @PostAuctionConstraint PostAuctionDiscountDTO postAuctionDiscountDTO);
}
