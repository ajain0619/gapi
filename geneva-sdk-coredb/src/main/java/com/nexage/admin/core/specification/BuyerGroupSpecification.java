package com.nexage.admin.core.specification;

import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerGroup_;
import com.nexage.admin.core.model.Company;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuyerGroupSpecification {

  /**
   * Compose {@link Specification} of {@link BuyerGroup} based on given status.
   *
   * @param companyPid Pid of {@link Company}
   * @return {@link Specification} of class {@link BuyerGroup}
   */
  public static Specification<BuyerGroup> withCompanyPid(final Long companyPid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(BuyerGroup_.COMPANY), companyPid);
  }

  /**
   * Compose {@link Specification} of {@link BuyerGroup} based on given params.
   *
   * @param companyPid Pid of {@link Company}
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @return {@link Specification} of class {@link BuyerGroup}
   */
  public static Specification<BuyerGroup> withCompanyPidAndSearchCriteria(
      final Long companyPid, Set<String> qf, String qt) {
    CustomSearchSpecification.Builder<BuyerGroup> builder =
        new CustomSearchSpecification.Builder<>();
    if (nonNull(qt) && !CollectionUtils.isEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    return SpecificationUtils.conjunction(
            Optional.ofNullable(builder.build()), Optional.ofNullable(withCompanyPid(companyPid)))
        .orElse(withCompanyPid(companyPid));
  }
}
