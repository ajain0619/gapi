package com.nexage.admin.core.specification;

import static com.nexage.admin.core.specification.FilterListAppBundleSpecification.withFilterListAppBundleIds;
import static com.nexage.admin.core.specification.FilterListAppBundleSpecification.withFilterListId;
import static com.nexage.admin.core.specification.FilterListAppBundleSpecification.withFilterListIdAndLike;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.admin.core.model.filter.FilterListAppBundle_;
import com.nexage.admin.core.repository.FilterListAppBundleRepository;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/filter-list-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class FilterListAppBundleSpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private FilterListAppBundleRepository filterListAppBundleRepository;

  @Test
  void shouldFindSpecWithFilterListId() {
    // given
    Specification<FilterListAppBundle> spec = withFilterListId(1);

    // when
    long countSpec = filterListAppBundleRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecWithAppBundleField() {
    // given
    Specification<FilterListAppBundle> spec =
        withFilterListIdAndLike(1, new HashSet(Arrays.asList(FilterListAppBundle_.APP)), "app");

    // when
    long countSpec = filterListAppBundleRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecWithStatusField() {
    // given
    Specification<FilterListAppBundle> spec =
        withFilterListIdAndLike(
            1, new HashSet(Arrays.asList(FilterListAppBundle_.STATUS)), "status");

    // when
    long countSpec = filterListAppBundleRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecWithAppBundleAndStatus() {
    // given
    Specification<FilterListAppBundle> spec =
        withFilterListIdAndLike(
            1,
            new HashSet(Arrays.asList(FilterListAppBundle_.APP, FilterListAppBundle_.STATUS)),
            null);

    // when
    long countSpec = filterListAppBundleRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecWithFilterListIdLike() {
    // given
    Specification<FilterListAppBundle> spec =
        withFilterListIdAndLike(
            12,
            new HashSet(
                Arrays.asList(
                    FilterListAppBundle_.APP,
                    FilterListAppBundle_.STATUS,
                    FilterListAppBundle_.FILTER_LIST_ID)),
            "VALID");

    // when
    assertNotNull(spec);
  }

  @Test
  void shouldFindSpecWithFilterListAppBundleIds() {
    // given
    Specification<FilterListAppBundle> spec =
        withFilterListAppBundleIds(1, new HashSet(Arrays.asList(1, 2)));

    // when
    long countSpec = filterListAppBundleRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }
}
