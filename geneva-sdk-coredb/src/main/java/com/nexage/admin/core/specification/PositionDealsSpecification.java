package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.BaseModel_;
import com.nexage.admin.core.model.DirectDeal_;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteView_;
import com.nexage.admin.core.model.Site_;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPosition_;
import com.nexage.admin.core.sparta.jpa.model.PositionView_;
import java.util.Collection;
import org.springframework.data.jpa.domain.Specification;

public class PositionDealsSpecification {
  private PositionDealsSpecification() {}

  public static Specification<DealPosition> withDealPidAndSellerIds(
      Long dealPid, Collection<Long> sellersIds) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      var subQuery = criteriaQuery.subquery(Long.class);
      var siteRoot = subQuery.from(Site.class);
      var siteCompanyJoin = siteRoot.join(Site_.company);
      subQuery.select(siteRoot.get(Site_.pid)).distinct(true);
      subQuery.where(siteCompanyJoin.get(BaseModel_.pid).in(sellersIds));
      var sitePositionJoin = root.join(DealPosition_.positionView);
      var positionPredicate =
          criteriaBuilder
              .in(sitePositionJoin.get(PositionView_.siteView).get(SiteView_.pid))
              .value(subQuery);
      var dealPredicate =
          criteriaBuilder.equal(root.get(DealPosition_.deal).get(DirectDeal_.pid), dealPid);
      return criteriaBuilder.and(positionPredicate, dealPredicate);
    };
  }
}
