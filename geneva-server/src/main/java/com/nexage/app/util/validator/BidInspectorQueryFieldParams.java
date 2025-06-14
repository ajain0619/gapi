package com.nexage.app.util.validator;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import org.springframework.util.MultiValueMap;

/**
 * Custom implementation of {@link MultiValueQueryParams} which allows to use custom constraint
 * validation.
 */
@BidInspectorQueryFieldParameterConstraint
public class BidInspectorQueryFieldParams extends MultiValueQueryParams {
  public BidInspectorQueryFieldParams(
      MultiValueMap<String, String> fields, SearchQueryOperator operator) {
    super(fields, operator);
  }
}
