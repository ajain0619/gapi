package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.DoohScreen;
import com.nexage.admin.core.model.DoohScreen_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DoohScreenSpecification {
  /**
   * Includes only the rule with the specified pid
   *
   * @param sellerPid to get all matching dooh screens
   * @return {@link Specification} return matching dooh screens
   */
  public static Specification<DoohScreen> withSellerPid(long sellerPid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(DoohScreen_.SELLER_PID), sellerPid);
  }
}
