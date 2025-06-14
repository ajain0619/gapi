package com.nexage.admin.core.specification;

import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher_;
import java.util.Collection;
import org.springframework.data.jpa.domain.Specification;

public class PublisherDealsSpecification {
  private PublisherDealsSpecification() {}

  public static Specification<DealPublisher> withDealPidAndSellerIds(
      Long dealPid, Collection<Long> sellersIds) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      var dealPredicate = root.get(DealPublisher_.DEAL).in(dealPid);
      var sellerPredicate = root.get(DealPublisher_.PUB_PID).in(sellersIds);
      criteriaQuery.select(root.get(DealPublisher_.DEAL));
      return criteriaBuilder.and(dealPredicate, sellerPredicate);
    };
  }
}
