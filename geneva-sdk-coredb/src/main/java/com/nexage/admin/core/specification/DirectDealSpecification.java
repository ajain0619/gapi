package com.nexage.admin.core.specification;

import static java.util.Objects.nonNull;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.DirectDeal_;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

/** The type Direct deal specification. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectDealSpecification {

  public static final String QF_HAS_RULES = "hasRules";
  public static final String QF_ALL = "all";

  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal}
   *
   * @param qf the query field
   * @param qt the query term to match
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> of(Set<String> qf, String qt) {
    var requireRules = sanitizeParams(qf, qt, QF_HAS_RULES, true);
    var allDeals = sanitizeParams(qf, qt, QF_ALL, false);

    CustomSearchSpecification.Builder<DirectDeal> builder =
        new CustomSearchSpecification.Builder<>();

    if (CollectionUtils.isNotEmpty(qf) && qf.contains(DirectDeal_.PRIORITY_TYPE)) {
      return withTier(qt);
    }

    if (nonNull(qt) && CollectionUtils.isNotEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    var ruleSpec = requireRules ? withRules() : withoutRules();
    if (allDeals) {
      return builder.build();
    }
    return SpecificationUtils.conjunction(
            Optional.ofNullable(builder.build()), Optional.ofNullable(ruleSpec))
        .orElse(ruleSpec);
  }

  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal}
   *
   * @param queryParams the query field
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> of(Map<String, List<String>> queryParams) {
    var qf = queryParams.keySet();
    boolean dealId = queryParams.containsKey("dealId");
    boolean dealdesc = queryParams.containsKey("description");
    boolean dealCategory = queryParams.containsKey("dealCategory");
    var requireRules =
        sanitizeParams(
            qf,
            queryParams.containsKey(QF_HAS_RULES) ? queryParams.get(QF_HAS_RULES).get(0) : null,
            QF_HAS_RULES,
            true);
    var allDeals =
        sanitizeParams(
            qf,
            queryParams.containsKey(QF_ALL) ? queryParams.get(QF_ALL).get(0) : null,
            QF_ALL,
            false);

    CustomSearchSpecification.Builder<DirectDeal> builder =
        new CustomSearchSpecification.Builder<>();

    if (CollectionUtils.isNotEmpty(qf) && qf.contains(DirectDeal_.PRIORITY_TYPE)) {
      return withTier(queryParams.get(DirectDeal_.PRIORITY_TYPE).get(0));
    }
    if (CollectionUtils.isNotEmpty(qf)
        && qf.contains(DirectDeal_.DEAL_CATEGORY)
        && !dealId
        && !dealdesc) {
      return withTierDealCategoryType(queryParams.get(DirectDeal_.DEAL_CATEGORY));
    }
    if (dealId && dealCategory) {
      return withDealIdAndDealCategory(
          queryParams.get(DirectDeal_.DEAL_CATEGORY), queryParams.get(DirectDeal_.DEAL_ID));
    }
    if (dealdesc && dealCategory) {
      return withDealDescAndDealCategory(
          queryParams.get(DirectDeal_.DEAL_CATEGORY), queryParams.get(DirectDeal_.DESCRIPTION));
    }
    if (CollectionUtils.isNotEmpty(qf)) {
      qf.forEach(q -> builder.with(q, queryParams.get(q).get(0)));
    }
    var ruleSpec = requireRules ? withRules() : withoutRules();
    if (allDeals) {
      return builder.build();
    }
    return SpecificationUtils.conjunction(
            Optional.ofNullable(builder.build()), Optional.ofNullable(ruleSpec))
        .orElse(ruleSpec);
  }

  private static boolean sanitizeParams(
      Set<String> qf, String qt, String name, boolean defaultValue) {
    boolean contains = defaultValue;
    if (nonNull(qf) && qf.contains(name)) {
      contains = Boolean.parseBoolean(qt);
      qf.remove(name);
    }
    return contains;
  }

  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal} based on dealId
   *
   * @param dealId the deal id
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> withDealId(String dealId) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(root.get(DirectDeal_.dealId), String.format("%%%s%%", dealId)),
            criteriaBuilder.isNotEmpty(root.get(DirectDeal_.rules)));
  }

  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal} based on description
   *
   * @param description the description
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> withDescription(String description) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(
                root.get(DirectDeal_.description), String.format("%%%s%%", description)),
            criteriaBuilder.isNotEmpty(root.get(DirectDeal_.rules)));
  }

  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal} based on a specific priorityType
   *
   * @param tier the tier
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> withTier(String tier) {
    Integer priorityType = DealPriorityType.valueOf(tier.toUpperCase()).asInt();
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(DirectDeal_.priorityType), priorityType),
            criteriaBuilder.isNotEmpty(root.get(DirectDeal_.rules)));
  }

  public static Specification<DirectDeal> withTierDealCategoryType(List<String> category) {
    List<Integer> categoryType = getDealCategoryIntValues(category);
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            root.get(DirectDeal_.dealCategory).in(categoryType),
            criteriaBuilder.isNotEmpty(root.get(DirectDeal_.rules)));
  }

  private static List<Integer> getDealCategoryIntValues(List<String> category) {
    List<String> categoryUcase =
        category.stream().map(String::toUpperCase).collect(Collectors.toList());
    return categoryUcase.stream()
        .map(c -> DealCategory.valueOf(c.replace("-", "_")).asInt())
        .collect(Collectors.toList());
  }

  private static Specification<DirectDeal> withDealIdAndDealCategory(
      List<String> dealType, List<String> dealId) {
    List<Integer> type = getDealCategoryIntValues(dealType);
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(root.get(DirectDeal_.dealId), "%" + dealId.get(0) + "%"),
            root.get(DirectDeal_.dealCategory).in(type));
  }

  private static Specification<DirectDeal> withDealDescAndDealCategory(
      List<String> dealType, List<String> dealDesc) {
    List<Integer> type = getDealCategoryIntValues(dealType);
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(root.get(DirectDeal_.DESCRIPTION), "%" + dealDesc.get(0) + "%"),
            root.get(DirectDeal_.dealCategory).in(type));
  }
  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal} based on having rules.
   *
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> withRules() {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.isNotEmpty(root.get(DirectDeal_.rules));
  }

  /**
   * Compose {@link Specification} of DirectDeal {@link DirectDeal} without rules.
   *
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> withoutRules() {
    return Specification.not(withRules());
  }
}
