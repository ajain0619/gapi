package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.SellerSeat_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SellerSeatSpecification {

  public static Specification<SellerSeat> withPidNotIn(Long... pids) {
    return (root, query, cb) -> cb.not(root.get(SellerSeat_.PID).in(pids));
  }

  /**
   * Seller Seats with the status of ENABLED = true or DISABLED = false
   *
   * @param status status of Seller Seat
   * @return {@link Specification} object
   */
  public static Specification<SellerSeat> withStatus(boolean status) {
    return ((root, query, cb) -> cb.equal(root.get(SellerSeat_.STATUS), status));
  }

  /**
   * Includes only Seller Seats that contain sellers
   *
   * @return {@link Specification} object
   */
  public static Specification<SellerSeat> withNonEmptySellers() {
    return ((root, query, cb) -> cb.isNotEmpty(root.get(SellerSeat_.SELLERS)));
  }

  /**
   * Includes in the query results only Seller Seats with given name
   *
   * @param name the name of Seller Seat
   * @return {@link Specification} object
   */
  public static Specification<SellerSeat> withNameLike(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(SellerSeat_.NAME), String.format("%%%s%%", name));
  }
}
