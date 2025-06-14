package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.admin.core.specification.FilterListSpecification;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/filter-list-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class FilterListRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired FilterListRepository filterListRepository;

  @Test
  void shouldFindAllFilterLists() {
    // when
    List<FilterList> filterListPage = filterListRepository.findAll();

    // then
    assertEquals(4, filterListPage.size());
  }

  @Test
  void shouldFindFilterListsWithFilterListTypeAndUploadStatus() {
    // given
    Long companyId = 2L;
    FilterListType filterListType = FilterListType.DOMAIN;
    FilterListUploadStatus filterListUploadStatus = FilterListUploadStatus.READY;
    Specification<FilterList> spec =
        FilterListSpecification.withCompanyId(
            companyId, Optional.of(filterListType), Optional.of(filterListUploadStatus));

    // when
    List<FilterList> filterListPage = filterListRepository.findAll(spec);

    // then
    assertEquals(1, filterListPage.size());
  }

  @Test
  void shouldFindFilterListsWithUploadStatus() {
    // given
    Long companyId = 2L;
    FilterListUploadStatus filterListUploadStatus = FilterListUploadStatus.READY;
    Specification<FilterList> spec =
        FilterListSpecification.withCompanyId(
            companyId, Optional.empty(), Optional.of(filterListUploadStatus));

    // when
    List<FilterList> filterListPage = filterListRepository.findAll(spec);

    // then
    assertEquals(2, filterListPage.size());
  }

  @Test
  void shouldFindFilterListsWithCompanyIdAndPid() {
    // given
    Long companyId = 2L;
    Specification<FilterList> spec = FilterListSpecification.withCompanyIdAndPid(companyId, 1);

    // when
    List<FilterList> filterListPage = filterListRepository.findAll(spec);

    // then
    assertEquals(1, filterListPage.size());
  }

  @Test
  void shouldFindFilterListsWithUploadStatusAndName() {
    // given
    String name = "Filter List 1";
    Long companyId = 2L;
    FilterListUploadStatus filterListUploadStatus = FilterListUploadStatus.READY;
    Specification<FilterList> spec =
        FilterListSpecification.withCompanyIdAndNameLike(
            companyId, name, Optional.empty(), Optional.of(filterListUploadStatus));

    // when
    List<FilterList> filterListPage = filterListRepository.findAll(spec);

    // then
    assertEquals(1, filterListPage.size());
  }

  @Test
  void shouldFindZeroFilterListsWithUploadStatusAndNameNotMatching() {
    // given
    String name = "Filter List 123";
    Long companyId = 2L;
    FilterListUploadStatus filterListUploadStatus = FilterListUploadStatus.READY;
    Specification<FilterList> spec =
        FilterListSpecification.withCompanyIdAndNameLike(
            companyId, name, Optional.empty(), Optional.of(filterListUploadStatus));

    // when
    List<FilterList> filterListPage = filterListRepository.findAll(spec);

    // then
    assertEquals(0, filterListPage.size());
  }
}
