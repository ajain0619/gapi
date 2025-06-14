package com.nexage.app.util.validator.rule.queryfield;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import org.springframework.util.MultiValueMap;

/**
 * Custom implementation of {@link MultiValueQueryParams} which allows to use custom constraint
 * validation.
 */
@SellerRuleAPIQueryFieldParameterConstraint
public class SellerRuleAPIQueryParams extends MultiValueQueryParams {

  public SellerRuleAPIQueryParams(
      MultiValueMap<String, String> fields, SearchQueryOperator operator) {
    super(fields, operator);
  }
}
