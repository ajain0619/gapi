package com.nexage.admin.core.specification;

import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.BrandProtectionCategory_;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTag_;
import java.util.Set;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandProtectionTagSpecification {

  /**
   * Compose {@link Specification} of {@link BrandProtectionTag} that specifies the input category
   * id
   *
   * @param categoryId the category ID
   * @return {@link Specification} of {@link BrandProtectionTag}
   */
  public static Specification<BrandProtectionTag> withCategoryId(long categoryId) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.join(BrandProtectionTag_.category).get(BrandProtectionCategory_.pid), categoryId);
  }

  /**
   * Compose {@link Specification} of {@link BrandProtectionTag} based on the input query field and
   * query term
   *
   * @param qf the query fields
   * @param qt the query term to match
   * @return {@link Specification} of {@link BrandProtectionTag}
   */
  public static Specification<BrandProtectionTag> withQueryFieldsAndSearchTerm(
      Set<String> qf, String qt) {
    CustomSearchSpecification.Builder<BrandProtectionTag> builder =
        new CustomSearchSpecification.Builder<>();

    if (nonNull(qt) && CollectionUtils.isNotEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    return builder.build();
  }

  /**
   * Compose {@link Specification} of {@link BrandProtectionTag} that specifies parent categories
   *
   * @return {@link Specification} of {@link BrandProtectionTag}
   */
  public static Specification<BrandProtectionTag> withNullParentTagPid() {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.isNull(
            root.join(BrandProtectionTag_.parentTag, JoinType.LEFT).get(BrandProtectionTag_.pid));
  }

  /**
   * Compose {@link Specification} of {@link BrandProtectionTag} that specifies input parentTagPid
   *
   * @param parentTagPid pid that is passed to get children categories
   * @return {@link Specification} of {@link BrandProtectionTag}
   */
  public static Specification<BrandProtectionTag> withParentTagPid(Long parentTagPid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.join(BrandProtectionTag_.parentTag).get(BrandProtectionTag_.pid), parentTagPid);
  }

  /**
   * Compose {@link Specification} of {@link BrandProtectionTag} that specifies input name
   *
   * @param name substring of category tag
   * @return {@link Specification} of {@link BrandProtectionTag}
   */
  public static Specification<BrandProtectionTag> parentsWithMatchingCategory(String name) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Join<BrandProtectionTag, BrandProtectionTag> selfJoin =
          root.join(BrandProtectionTag_.parentTag, JoinType.INNER);

      Predicate p1 =
          criteriaBuilder.equal(
              selfJoin.join(BrandProtectionTag_.category).get(BrandProtectionCategory_.pid), 6L);
      Predicate p2 =
          criteriaBuilder.or(
              criteriaBuilder.like(selfJoin.get(BrandProtectionTag_.name), "%" + name + "%"),
              criteriaBuilder.like(root.get(BrandProtectionTag_.name), "%" + name + "%"));
      criteriaQuery.groupBy(root.get(BrandProtectionTag_.parentTag));
      return criteriaBuilder.and(p1, p2);
    };
  }
}
