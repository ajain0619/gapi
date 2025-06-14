package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.AppBundleData_;
import com.nexage.admin.core.model.filter.FiilterListStatus;
import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.admin.core.model.filter.FilterListAppBundle_;
import com.nexage.admin.core.specification.CustomSearchSpecification.Builder;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterListAppBundleSpecification {

  /**
   * Compose {@link Specification} of {@link FilterListAppBundle} with filterListId
   *
   * @param filterListId {@link Long}
   * @return {@link Specification<FilterListAppBundle>}
   */
  public static Specification<FilterListAppBundle> withFilterListId(Integer filterListId) {
    return hasFilterListId(filterListId);
  }

  /**
   * Compose {@link Specification} of {@link FilterListAppBundle} with filterListId and query search
   * parameters
   *
   * @param filterListId {@link Integer}
   * @param qf {@link Set<String>} Query Fields
   * @param qt {@link String} Query Term
   * @return {@link Specification<FilterListAppBundle>}
   */
  public static Specification<FilterListAppBundle> withFilterListIdAndLike(
      Integer filterListId, Set<String> qf, String qt) {
    return withFilterListId(filterListId).and(hasFieldsLike(qf, qt));
  }

  /**
   * Compose {@link Specification} of {@link FilterListAppBundle} with pids in {@link Set}
   *
   * @param filterListId {@link Integer}
   * @param filterListAppBundleIds {@link Set<Integer>}
   * @return {@link Specification<FilterListAppBundle>}
   */
  public static Specification<FilterListAppBundle> withFilterListAppBundleIds(
      Integer filterListId, Set<Integer> filterListAppBundleIds) {
    return withFilterListId(filterListId)
        .and((root, query, cb) -> root.get(FilterListAppBundle_.PID).in(filterListAppBundleIds));
  }

  private static Specification<FilterListAppBundle> hasFieldsLike(Set<String> qf, String qt) {
    Specification<FilterListAppBundle> filterListAppBundleSpec;
    Builder<FilterListAppBundle> searchSpecification = new Builder<>();
    qf.stream()
        .filter(field -> !field.equals(FilterListAppBundle_.STATUS))
        .filter(field -> !field.equals(FilterListAppBundle_.APP))
        .forEach(field -> searchSpecification.with(field, qt));
    filterListAppBundleSpec = searchSpecification.build();
    if (filterListAppBundleSpec != null && qf.contains(FilterListAppBundle_.STATUS)) {
      filterListAppBundleSpec =
          filterListAppBundleSpec.or(
              (root, query, cb) ->
                  cb.equal(root.get(FilterListAppBundle_.STATUS), FiilterListStatus.valueOf(qt)));
    }

    if (filterListAppBundleSpec != null && qf.contains(FilterListAppBundle_.APP)) {
      filterListAppBundleSpec =
          filterListAppBundleSpec.or(
              (root, query, cb) ->
                  cb.like(
                      root.get(FilterListAppBundle_.APP).get(AppBundleData_.APP_BUNDLE_ID),
                      "%" + qt + "%"));
    }
    return filterListAppBundleSpec;
  }

  private static Specification<FilterListAppBundle> hasFilterListId(Integer filterListId) {
    return (root, query, cb) ->
        cb.equal(root.get(FilterListAppBundle_.FILTER_LIST_ID), filterListId);
  }
}
