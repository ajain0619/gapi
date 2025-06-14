package com.nexage.admin.core.model.placementformula.formula;

import javax.persistence.criteria.Path;

public interface AttributeInfo {
  Object getValueAsObject(String value);

  <T> Path<T> getPath(RootWrapper root, String value);
}
