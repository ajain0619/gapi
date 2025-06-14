package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.Company;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CustomSearchSpecificationTest {

  @Mock(extraInterfaces = Serializable.class)
  Root<Company> root;

  @Mock(extraInterfaces = Serializable.class)
  CriteriaQuery<?> query;

  @Mock(extraInterfaces = Serializable.class)
  CriteriaBuilder criteriaBuilder;

  @Test
  void shouldNotProduceSpecificationWithoutKeys() {
    CustomSearchSpecification.Builder<Object> builder = new CustomSearchSpecification.Builder<>();
    builder.build();
    assertNotNull(builder);
    assertNull(builder.build());
  }

  @Test
  void shouldProduceSpecificationWithKeys() {
    CustomSearchSpecification.Builder<Object> builder = new CustomSearchSpecification.Builder<>();
    final String key = UUID.randomUUID().toString();
    final String value = UUID.randomUUID().toString();
    builder.with(key, value).build();
    assertNotNull(builder);
    Specification<Object> specification = builder.build();
    assertNotNull(specification);
    assertTrue(specification.toString().contains(key));
    assertTrue(specification.toString().contains(value));
  }

  @Test
  void shouldProduceConjunctionSpecificationWithKeys() {
    CustomSearchSpecification.Builder<Company> builder = new CustomSearchSpecification.Builder<>();
    String key = UUID.randomUUID().toString();
    String value = UUID.randomUUID().toString();
    builder.with(key, value);
    assertNotNull(builder);
    Specification<Company> specification = builder.build();
    assertNotNull(specification);

    Predicate predicate = mock(Predicate.class);
    Path path = mock(Path.class);
    when(root.get(key)).thenReturn(path);
    when(criteriaBuilder.like(eq(path), anyString())).thenReturn(predicate);
    Predicate result = builder.build().toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
    assertEquals(predicate, result);
  }

  @Test
  void shouldProduceConjuctionSpecificationWithLongKeys() {
    CustomSearchSpecification.Builder<Company> builder =
        new CustomSearchSpecification.Builder<>(Company.class);
    String key = "pid";
    Long value = 123L;
    builder.with(key, value);
    assertNotNull(builder);
    Specification<Company> specification = builder.build();
    assertNotNull(specification);

    Predicate predicate = mock(Predicate.class);
    Path path = mock(Path.class);
    when(root.get(key)).thenReturn(path);
    when(criteriaBuilder.equal(eq(path), anyLong())).thenReturn(predicate);
    Predicate result = builder.build().toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
    assertEquals(predicate, result);
  }

  @Test
  void shouldReturnErrorOnUnsupportedQueryFields() {
    CustomSearchSpecification.Builder<Company> builder =
        new CustomSearchSpecification.Builder<>(Company.class);
    String key = "pid";
    String value = "S1";
    GenevaValidationException specificationException =
        assertThrows(GenevaValidationException.class, () -> builder.with(key, value));

    assertEquals(
        CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE,
        specificationException.getErrorCode());
  }
}
