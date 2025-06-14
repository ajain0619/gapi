package com.nexage.admin.core.specification;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.BuyerSeat_;
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
public class BuyerSeatSpecification {
  private static final Set<String> SUPPORTED_QUERY_FIELDS = Set.of(BuyerSeat_.NAME);

  /**
   * Find all {@link BuyerSeat} objects which both has a query term of {@param qt} in one of the
   * {@param qf} fields, {@param name} the search name, and has {@param companyPid} as the
   * "companyPid" attribute.
   *
   * @param name The query term to find in the fields. The term will be partially matched.
   * @param qf A set of query fields (i.e. the {@link String} names of the fields). This is used to
   *     search for through one or more {@link BuyerSeat} fields for a search term.
   * @param qt The query term to find in the fields. The term will be partially matched.
   * @param companyPid The value to match on the enabled attribute.
   * @return {@link Specification} for {@link BuyerSeat} entities based on parameters.
   */
  public static Specification<BuyerSeat> withCompanyPidAndQueryFieldsAndSearchTerm(
      Long companyPid, String name, Set<String> qf, String qt) {
    return Stream.of(
            withCompanyPid(companyPid), withName(name), withQueryFieldsAndSearchTerm(qf, qt))
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse(null);
  }

  /**
   * Find all {@link BuyerSeat} objects which have a "companyPid" field which matches the {@param
   * companyPid} value.
   *
   * @param companyPid The value to match on the {@link BuyerSeat} for the "companyPid" field.
   * @return {@link Specification} for {@link BuyerSeat} entities based on parameters.
   */
  public static Specification<BuyerSeat> withCompanyPid(Long companyPid) {
    if (companyPid == null) {
      return null;
    }

    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(BuyerSeat_.COMPANY), companyPid);
  }

  /**
   * Find all {@link BuyerSeat} objects which have a "name" field which matches the {@param name}
   * value.
   *
   * @param name The value to match on the {@link BuyerSeat} for the "name" field.
   * @return {@link Specification} for {@link BuyerSeat} entities based on parameters.
   */
  public static Specification<BuyerSeat> withName(String name) {
    if (name == null || name.equals("")) {
      return null;
    }

    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(BuyerSeat_.NAME), "%" + name + "%");
  }

  /**
   * Find all {@link BuyerSeat} objects which have a query term of {@param qt} in one of the {@param
   * qf} fields.
   *
   * @param qf A set of query fields (i.e. the {@link String} names of the fields). This is used to
   *     search for through one or more {@link BuyerSeat} fields for a search term.
   * @param qt The query term to find in the fields. The term will be partially matched.
   * @return {@link Specification} for {@link BuyerSeat} entities based on parameters.
   */
  public static Specification<BuyerSeat> withQueryFieldsAndSearchTerm(Set<String> qf, String qt) {
    if (CollectionUtils.isEmpty(qf) || StringUtils.isEmpty(qt)) {
      return null;
    }

    if (!SUPPORTED_QUERY_FIELDS.containsAll(qf)) {
      throw new GenevaValidationException(CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE);
    }

    CustomSearchSpecification.Builder<BuyerSeat> customSearchSpecificationBuilder =
        new CustomSearchSpecification.Builder<>();

    qf.forEach(queryField -> customSearchSpecificationBuilder.with(queryField, qt));

    return customSearchSpecificationBuilder.build();
  }
}
