package com.nexage.admin.core.specification;

import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.admin.core.model.filter.FilterList_;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterListSpecification {

  /**
   * Compose {@link Specification} of {@link FilterList} with companyId
   *
   * @param companyId {@link Long}
   * @return {@link Specification<FilterList>}
   */
  public static Specification<FilterList> withCompanyId(
      Long companyId,
      Optional<FilterListType> filterListType,
      Optional<FilterListUploadStatus> filterListUploadStatus) {

    Specification<FilterList> companySpec = hasCompanyId(companyId).and(isActive());
    Specification<FilterList> filterListSpec =
        (filterListType.map(type -> companySpec.and(hasFilterListType(type))).orElse(companySpec));
    return filterListUploadStatus
        .map(uploadStatus -> filterListSpec.and(hasFilterListUploadStatus(uploadStatus)))
        .orElse(filterListSpec);
  }

  /**
   * Compose {@link Specification} of {@link FilterList} with companyId and filterList that has like
   * name
   *
   * @param companyId {@link Long}
   * @param name {@link String}
   * @return {@link Specification<FilterList>}
   */
  public static Specification<FilterList> withCompanyIdAndNameLike(
      Long companyId,
      String name,
      Optional<FilterListType> filterListType,
      Optional<FilterListUploadStatus> filterListUploadStatus) {
    Specification<FilterList> spec =
        withCompanyId(companyId, filterListType, filterListUploadStatus);
    return spec != null
        ? spec.and(
            new CustomSearchSpecification.Builder<FilterList>()
                .with(FilterList_.NAME, name)
                .build())
        : null;
  }

  /**
   * Componse {@link Specification<FilterList>} with companyId and filterListId
   *
   * @param companyId {@link Long}
   * @param filterListId {@link Long}
   * @return {@link Specification<FilterList>}
   */
  public static Specification<FilterList> withCompanyIdAndPid(
      Long companyId, Integer filterListId) {
    Specification<FilterList> specHasCompanyId = Specification.where(hasCompanyId(companyId));
    Specification<FilterList> spec =
        specHasCompanyId != null
            ? specHasCompanyId.and(Specification.where(hasFilterListPid(filterListId)))
            : null;
    return spec != null ? spec.and(isActive()) : null;
  }

  private static Specification<FilterList> hasCompanyId(Long companyId) {
    return (root, query, cb) -> cb.equal(root.get(FilterList_.COMPANY_ID), companyId);
  }

  private static Specification<FilterList> isActive() {
    return (root, query, cb) -> cb.equal(root.get(FilterList_.ACTIVE), true);
  }

  private static Specification<FilterList> hasFilterListPid(Integer filterListId) {
    return (root, query, cb) -> cb.equal(root.get(FilterList_.PID), filterListId);
  }

  private static Specification<FilterList> hasFilterListType(FilterListType filterListType) {
    return (root, query, cb) -> cb.equal(root.get(FilterList_.TYPE), filterListType);
  }

  private static Specification<FilterList> hasFilterListUploadStatus(
      FilterListUploadStatus filterListUploadStatus) {
    return (root, query, cb) ->
        cb.equal(root.get(FilterList_.UPLOAD_STATUS), filterListUploadStatus);
  }
}
