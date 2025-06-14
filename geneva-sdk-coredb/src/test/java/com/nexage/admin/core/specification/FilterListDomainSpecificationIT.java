package com.nexage.admin.core.specification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.admin.core.model.filter.FilterListDomain_;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/filter-list-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class FilterListDomainSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private CriteriaBuilder criteriaBuilderMock;

  private CriteriaQuery criteriaQueryMock;

  private Root<FilterListDomain> filterListDomainRootMock;

  @BeforeEach
  public void setUp() {
    criteriaBuilderMock = mock(CriteriaBuilder.class);
    criteriaQueryMock = mock(CriteriaQuery.class);
    filterListDomainRootMock = mock(Root.class);
  }

  @Test
  void shouldFindFilterListDomainSpecificationQueryFields() {
    Set<String> qf = Set.of("domain", "status");
    Predicate predicate = mock(Predicate.class);
    Path path = mock(Path.class);
    when(filterListDomainRootMock.get(FilterListDomain_.DOMAIN)).thenReturn(path);

    Expression domainId = mock(Expression.class);
    when(criteriaBuilderMock.like(domainId, "%VALID%")).thenReturn(predicate);
    Specification<FilterListDomain> actual =
        FilterListDomainSpecification.withFilterListIdAndLike(12, qf, "VALID");
    actual.toPredicate(filterListDomainRootMock, criteriaQueryMock, criteriaBuilderMock);
    verify(filterListDomainRootMock, times(1)).get(FilterListDomain_.DOMAIN);
  }

  @Test
  void shouldFindFilterListDomainSpecificationFilterListDomainIds() {
    Set<Integer> filterListDomainIds = Set.of(1, 2);
    Path path = mock(Path.class);
    when(filterListDomainRootMock.get(FilterListDomain_.PID)).thenReturn(path);

    Specification<FilterListDomain> actual =
        FilterListDomainSpecification.withFilterListDomainIds(12, filterListDomainIds);
    actual.toPredicate(filterListDomainRootMock, criteriaQueryMock, criteriaBuilderMock);
    verify(filterListDomainRootMock, times(1)).get(FilterListDomain_.PID);
  }
}
