package com.nexage.admin.core.specification;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment_;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeeAdjustmentSpecification {

  private static final Set<String> SUPPORTED_QUERY_FIELDS = Set.of(FeeAdjustment_.NAME);

  /**
   * Find all {@link FeeAdjustment} objects which both has a query term of {@param qt} in one of the
   * {@param qf} fields and has {@param enabled} as the "enabled" attribute.
   *
   * @param qf A set of query fields (i.e. the {@link String} names of the fields). This is used to
   *     search for through one or more {@link FeeAdjustment} fields for a search term.
   * @param qt The query term to find in the fields. The term will be partially matched.
   * @param enabled The value to match on the enabled attribute.
   * @return {@link Specification} for {@link FeeAdjustment} entities based on parameters.
   */
  public static Specification<FeeAdjustment> withQueryFieldsAndSearchTermAndEnabled(
      Set<String> qf, String qt, Boolean enabled) {
    return Stream.of(withQueryFieldsAndSearchTerm(qf, qt), withEnabled(enabled))
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse(null);
  }

  /**
   * Find all {@link FeeAdjustment} objects which have a query term of {@param qt} in one of the
   * {@param qf} fields.
   *
   * @param qf A set of query fields (i.e. the {@link String} names of the fields). This is used to
   *     search for through one or more {@link FeeAdjustment} fields for a search term.
   * @param qt The query term to find in the fields. The term will be partially matched.
   * @return {@link Specification} for {@link FeeAdjustment} entities based on parameters.
   */
  public static Specification<FeeAdjustment> withQueryFieldsAndSearchTerm(
      Set<String> qf, String qt) {
    if (CollectionUtils.isEmpty(qf) || StringUtils.isEmpty(qt)) {
      return null;
    }

    if (!SUPPORTED_QUERY_FIELDS.containsAll(qf)) {
      throw new GenevaValidationException(CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE);
    }

    CustomSearchSpecification.Builder<FeeAdjustment> customSearchSpecificationBuilder =
        new CustomSearchSpecification.Builder<>();

    qf.forEach(queryField -> customSearchSpecificationBuilder.with(queryField, qt));

    return customSearchSpecificationBuilder.build();
  }

  /**
   * Find all {@link FeeAdjustment} objects which have an "enabled" field which matches the {@param
   * enabled} value.
   *
   * @param enabled The value to match on the {@link FeeAdjustment} for the "enabled" field.
   * @return {@link Specification} for {@link FeeAdjustment} entities based on parameters.
   */
  public static Specification<FeeAdjustment> withEnabled(Boolean enabled) {
    if (enabled == null) {
      return null;
    }

    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(FeeAdjustment_.ENABLED), enabled);
  }
}
