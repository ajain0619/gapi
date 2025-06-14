package com.nexage.admin.core.specification;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.ToString;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@ToString
public class CustomSearchSpecification<T> implements Specification<T> {

  private SearchCriteria criteria;

  private CustomSearchSpecification(final SearchCriteria criteria) {
    super();
    this.criteria = criteria;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    if (Long.class.equals(criteria.getType())) {
      return builder.equal(root.get(criteria.getKey()), criteria.getValue());
    }

    return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
  }

  public static class Builder<T> {
    private Set<Field> fields;
    private final List<SearchCriteria> params;

    public Builder() {
      params = new ArrayList<>();
    }

    public Builder(Class<T> clazz) {
      this.fields = getAllFields(new HashSet<>(), clazz);
      this.params = new ArrayList<>();
    }

    private Set<Field> getAllFields(Set<Field> fields, Class<?> type) {
      if (type.getSuperclass() != null) {
        getAllFields(fields, type.getSuperclass());
      }

      fields.addAll(Arrays.asList(type.getDeclaredFields()));

      return fields;
    }

    public Builder<T> with(String key, Object value) {
      Class<?> fieldType = null;
      if (!CollectionUtils.isEmpty(fields)) {
        var field =
            fields.stream().filter(f -> f.getName().equalsIgnoreCase(key)).findFirst().orElse(null);
        if (field != null) {
          fieldType = field.getType();
          validateValueType(value, fieldType);
        }
      }
      params.add(new SearchCriteria(key, value, fieldType));
      return this;
    }

    public Specification<T> build() {
      if (params.isEmpty()) {
        return null;
      }

      List<Specification<T>> specs = new ArrayList<>(params.size());
      for (SearchCriteria param : params) {
        specs.add(new CustomSearchSpecification<>(param));
      }

      Specification<T> result = specs.get(0);
      for (int i = 1; i < specs.size(); i++) {
        result = Specification.where(result).or(specs.get(i));
      }
      return result;
    }

    private void validateValueType(Object value, Class<?> type) {
      if (Long.class.equals(type)) {
        boolean isTermNumeric = value.toString().matches("^\\d+$");
        if (!isTermNumeric) {
          throw new GenevaValidationException(
              CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE);
        }
      }
    }
  }
}
