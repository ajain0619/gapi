package com.nexage.admin.core.specification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class SearchCriteria {
  private String key;
  private Object value;
  private Class<?> type;

  SearchCriteria(String key, Object value) {
    this.key = key;
    this.value = value;
  }
}
