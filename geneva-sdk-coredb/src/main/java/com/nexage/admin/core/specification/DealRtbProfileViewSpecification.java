package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DealRtbProfileViewSpecification {

  private static final String STATUS = "status";
  private static final int STATUS_DELETED = -1;

  public static Specification<DealRtbProfileViewUsingFormulas> isNotDeleted() {
    return (rtbProfileView, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.notEqual(rtbProfileView.get(STATUS), STATUS_DELETED);
  }

  public static Specification<DealRtbProfileViewUsingFormulas>
      hasNullDefaultRtbProfileOwnerCompanyPid() {
    return (rtbProfileView, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.isNull(rtbProfileView.get("defaultRtbProfileOwnerCompanyPid"));
  }
}
