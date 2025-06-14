package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.Campaign;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CampaignSpecification {

  public static Specification<Campaign> isNotDeleted() {
    return (campaign, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.notEqual(campaign.get("status"), 4);
  }

  public static Specification<Campaign> hasCompanyPid(Long pid) {
    return (campaign, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(campaign.get("sellerId"), pid);
  }
}
