package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerCompany_;
import com.nexage.admin.core.model.HbPartnerSite_;
import com.nexage.admin.core.model.HbPartner_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HbPartnerSpecification {

  /**
   * @param sellerPid seller pid
   * @return {@link Specification} of Hb Partner
   */
  public static Specification<HbPartner> withSellerPid(Long sellerPid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.join(HbPartner_.hbPartnerCompany).get(HbPartnerCompany_.company), sellerPid);
  }

  /**
   * @param sitePid site pid
   * @return {@link Specification} of Hb Partner
   */
  public static Specification<HbPartner> withSitePid(Long sitePid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.join(HbPartner_.hbPartnerSite).get(HbPartnerSite_.site), sitePid);
  }
}
