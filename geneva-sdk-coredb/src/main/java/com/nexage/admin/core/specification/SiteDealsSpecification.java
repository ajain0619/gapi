package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.SiteView_;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.DealSite_;
import java.util.Collection;
import org.springframework.data.jpa.domain.Specification;

public class SiteDealsSpecification {
  private SiteDealsSpecification() {}

  public static Specification<DealSite> withDealPidAndSellerIds(
      Long dealPid, Collection<Long> sellersIds) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      var join = root.join(DealSite_.siteView);
      var dealPredicate = root.get(DealSite_.DEAL).in(dealPid);

      var sellerPredicate = join.get(SiteView_.companyPid).in(sellersIds);
      return criteriaBuilder.and(sellerPredicate, dealPredicate);
    };
  }
}
