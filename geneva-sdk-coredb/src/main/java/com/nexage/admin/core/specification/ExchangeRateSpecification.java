package com.nexage.admin.core.specification;

import static java.util.Objects.isNull;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.model.ExchangeRatePrimaryKey_;
import com.nexage.admin.core.model.ExchangeRate_;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateSpecification {

  private static final Set<String> SUPPORTED_QUERY_FIELDS =
      Set.of(ExchangeRatePrimaryKey_.CURRENCY);

  /**
   * Find all {@link ExchangeRate} objects that match the given specifications
   *
   * @param qf Query field of type {@link String}. This is used to search by a field of {@link
   *     ExchangeRate}.
   * @param qt Query term of type {@link String} that is being searched for.
   * @param latest A {@link Boolean} value that is used to decide whether to filter only latest
   *     exchange rates.
   * @return {@link Optional} {@link Specification} for {@link ExchangeRate} entities based on
   *     parameters.
   */
  public static Optional<Specification<ExchangeRate>> withQueryFieldsAndSearchTermAndLatest(
      String qf, String qt, boolean latest) {
    Specification<ExchangeRate> withCurrencySpec = null;
    Specification<ExchangeRate> withLatestSpec = null;
    boolean useQt = false;
    if (StringUtils.isNotEmpty(qf)) {
      if (!SUPPORTED_QUERY_FIELDS.contains(qf) || StringUtils.isEmpty(qt)) {
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }
      withCurrencySpec = withCurrency(qt);
      useQt = true;
    }
    if (latest) {
      withLatestSpec = withLatest(useQt, qt);
    }
    if (isNull(withCurrencySpec) && isNull(withLatestSpec)) {
      return Optional.empty();
    }
    return SpecificationUtils.conjunction(
        Optional.ofNullable(withCurrencySpec), Optional.ofNullable(withLatestSpec));
  }

  private static Specification<ExchangeRate> withLatest(boolean useQt, String currency) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get(ExchangeRate_.FOREX_ID),
            getLatestSubQuery(criteriaQuery, criteriaBuilder, useQt, currency));
  }

  private static Specification<ExchangeRate> withCurrency(String currency) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get(ExchangeRate_.ID).get(ExchangeRatePrimaryKey_.CURRENCY), currency);
  }

  private static Subquery<Long> getLatestSubQuery(
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      boolean useQt,
      String currency) {
    Subquery<Long> sub = criteriaQuery.subquery(Long.class);
    Root<ExchangeRate> subRoot = sub.from(ExchangeRate.class);
    sub.select(criteriaBuilder.max(subRoot.get(ExchangeRate_.FOREX_ID)));
    if (useQt) {
      sub.where(
          criteriaBuilder.equal(
              subRoot.get(ExchangeRate_.ID).get(ExchangeRatePrimaryKey_.CURRENCY), currency));
    }
    return sub;
  }
}
