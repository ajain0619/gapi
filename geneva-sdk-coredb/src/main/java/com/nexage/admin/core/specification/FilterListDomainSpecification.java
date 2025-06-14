package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.filter.Domain_;
import com.nexage.admin.core.model.filter.FiilterListStatus;
import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.admin.core.model.filter.FilterListDomain_;
import com.nexage.admin.core.specification.CustomSearchSpecification.Builder;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterListDomainSpecification {

  /**
   * Compose {@link Specification} of {@link FilterListDomain} with filterListId
   *
   * @param filterListId {@link Long}
   * @return {@link Specification<FilterListDomain>}
   */
  public static Specification<FilterListDomain> withFilterListId(Integer filterListId) {
    return hasFilterListId(filterListId);
  }

  /**
   * Compose {@link Specification} of {@link FilterListDomain} with filterListId and query search
   * parameters
   *
   * @param filterListId {@link Integer}
   * @param qf {@link Set<String>} Query Fields
   * @param qt {@link String} Query Term
   * @return {@link Specification<FilterListDomain>}
   */
  public static Specification<FilterListDomain> withFilterListIdAndLike(
      Integer filterListId, Set<String> qf, String qt) {
    return withFilterListId(filterListId).and(hasFieldsLike(qf, qt));
  }

  /**
   * Compose {@link Specification} of {@link FilterListDomain} with pids in {@link Set}
   *
   * @param filterListId {@link Integer}
   * @param filterListDomainIds {@link Set<Integer>}
   * @return {@link Specification<FilterListDomain>}
   */
  public static Specification<FilterListDomain> withFilterListDomainIds(
      Integer filterListId, Set<Integer> filterListDomainIds) {
    return withFilterListId(filterListId)
        .and((root, query, cb) -> root.get(FilterListDomain_.PID).in(filterListDomainIds));
  }

  private static Specification<FilterListDomain> hasFieldsLike(Set<String> qf, String qt) {
    Specification filterListDomainSpec;
    Builder searchSpecification = new Builder<FilterListDomain>();
    qf.stream()
        .filter(field -> !field.equals(FilterListDomain_.STATUS))
        .filter(field -> !field.equals(FilterListDomain_.DOMAIN))
        .forEach(field -> searchSpecification.with(field, qt));
    filterListDomainSpec = searchSpecification.build();
    if (qf.contains(FilterListDomain_.STATUS)) {
      Specification<FilterListDomain> where = Specification.where(filterListDomainSpec);
      filterListDomainSpec =
          where != null
              ? where.or(
                  (root, query, cb) ->
                      cb.equal(root.get(FilterListDomain_.STATUS), FiilterListStatus.valueOf(qt)))
              : (root, query, cb) ->
                  cb.equal(root.get(FilterListDomain_.STATUS), FiilterListStatus.valueOf(qt));
    }

    if (qf.contains(FilterListDomain_.DOMAIN)) {
      Specification<FilterListDomain> where = Specification.where(filterListDomainSpec);
      filterListDomainSpec =
          (where != null)
              ? where.or(
                  (root, query, cb) ->
                      cb.like(
                          root.get(FilterListDomain_.DOMAIN).get(Domain_.DOMAIN), "%" + qt + "%"))
              : (root, query, cb) ->
                  cb.like(root.get(FilterListDomain_.DOMAIN).get(Domain_.DOMAIN), "%" + qt + "%");
    }
    return filterListDomainSpec;
  }

  private static Specification<FilterListDomain> hasFilterListId(Integer filterListId) {
    return (root, query, cb) -> cb.equal(root.get(FilterListDomain_.FILTER_LIST_ID), filterListId);
  }
}
