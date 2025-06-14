package com.nexage.admin.core.specification;

import static java.util.Objects.isNull;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderConfig_;
import com.nexage.admin.core.model.Company;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BidderConfigSpecification {

  private static final Set<String> SUPPORTED_QUERY_FIELDS = Set.of(BidderConfig_.NAME);

  /**
   * Compose {@link Specification} of {@link BidderConfig} based on given status.
   *
   * @param companyPid Pid of {@link Company}
   * @return {@link Specification} of class {@link BidderConfig}
   */
  public static Specification<BidderConfig> withCompanyPid(final Long companyPid) {
    if (isNull(companyPid)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(BidderConfig_.COMPANY_PID), companyPid);
  }

  /**
   * Compose {@link Specification} of {@link BidderConfig} based on given params.
   *
   * @param companyPid Pid of {@link Company}
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @return {@link Specification} of class {@link BidderConfig}
   */
  public static Specification<BidderConfig> withCompanyPidAndSearchCriteria(
      final Long companyPid, Set<String> qf, String qt) {

    CustomSearchSpecification.Builder<BidderConfig> builder =
        new CustomSearchSpecification.Builder<>(BidderConfig.class);
    if (!CollectionUtils.isEmpty(qf)) {
      if (isNull(qt) || qt.isBlank() || !SUPPORTED_QUERY_FIELDS.containsAll(qf)) {
        throw new GenevaValidationException(
            CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE);
      }
      qf.forEach(q -> builder.with(q, qt));
    }
    return SpecificationUtils.conjunction(
            Optional.ofNullable(builder.build()), Optional.ofNullable(withCompanyPid(companyPid)))
        .orElse(withCompanyPid(companyPid));
  }
}
