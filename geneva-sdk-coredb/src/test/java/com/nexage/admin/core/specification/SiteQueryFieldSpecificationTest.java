package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Site_;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SiteQueryFieldSpecificationTest {

  @Mock private CriteriaBuilder criteriaBuilder;

  @Mock private CriteriaQuery query;

  @Mock private Root<Site> root;

  @BeforeEach
  public void setUp() {
    openMocks(this);
  }

  @Test
  void shouldReturnSiteQueryFieldSpecificationOnlyStatus() {
    // given
    SiteQueryFieldSpecification specification = getSiteQueryFieldSpecificationBuilder().build();

    Predicate statusPredicate = mock(Predicate.class);
    Path statusPath = mock(Path.class);
    when(root.get(Site_.status)).thenReturn(statusPath);
    when(statusPath.in(anySet())).thenReturn(statusPredicate);

    // when
    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    // then
    assertEquals(statusPredicate, result);
  }

  @Test
  void shouldReturnSiteQueryFieldSpecificationWithName() {
    // given
    SiteQueryFieldSpecification.SiteQueryFieldSpecificationBuilder builder =
        getSiteQueryFieldSpecificationBuilder();
    builder.name(Optional.of("S1"));
    SiteQueryFieldSpecification specification = builder.build();

    Predicate namePredicate = mock(Predicate.class);
    Path statusPath = mock(Path.class);
    Path namePath = mock(Path.class);
    when(root.get(Site_.status)).thenReturn(statusPath);
    when(root.get(Site_.name)).thenReturn(namePath);
    when(criteriaBuilder.like(eq(namePath), anyString())).thenReturn(namePredicate);

    // when
    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    // then
    assertEquals(namePredicate, result);
  }

  @Test
  void shouldReturnSiteQueryFieldSpecificationWithCompanyName() {
    // given
    SiteQueryFieldSpecification.SiteQueryFieldSpecificationBuilder builder =
        getSiteQueryFieldSpecificationBuilder();
    builder.companyName(Optional.of("S1"));
    SiteQueryFieldSpecification specification = builder.build();

    Predicate companyNamePredicate = mock(Predicate.class);
    Path statusPath = mock(Path.class);
    Path companyNamePath = mock(Path.class);
    when(root.get(Site_.status)).thenReturn(statusPath);
    when(root.get(Site_.company).get(Company_.name)).thenReturn(companyNamePath);
    when(criteriaBuilder.like(eq(companyNamePath), anyString())).thenReturn(companyNamePredicate);

    // when
    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    // then
    assertEquals(companyNamePredicate, result);
  }

  @Test
  void shouldReturnSiteQueryFieldSpecificationWithUserCompanyName() {
    // given
    SiteQueryFieldSpecification.SiteQueryFieldSpecificationBuilder builder =
        getSiteQueryFieldSpecificationBuilder();
    builder.companyName(Optional.of("S1"));
    builder.userCompanyPids(Optional.of(Sets.newHashSet(4L, 5L, 6L)));
    SiteQueryFieldSpecification specification = builder.build();

    Predicate companyNamePredicate = mock(Predicate.class);
    Path statusPath = mock(Path.class);
    Path companyNamePath = mock(Path.class);
    when(root.get(Site_.status)).thenReturn(statusPath);
    when(root.get(Site_.company).get(Company_.name)).thenReturn(companyNamePath);
    when(criteriaBuilder.like(eq(companyNamePath), anyString())).thenReturn(companyNamePredicate);

    // when
    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    // then
    assertEquals(companyNamePredicate, result);
  }

  private SiteQueryFieldSpecification.SiteQueryFieldSpecificationBuilder
      getSiteQueryFieldSpecificationBuilder() {
    SiteQueryFieldSpecification.SiteQueryFieldSpecificationBuilder builder =
        SiteQueryFieldSpecification.builder();
    builder.statuses(Optional.of(Set.of(Status.INACTIVE.asInt(), Status.ACTIVE.asInt())));
    return builder;
  }
}
