package com.nexage.app.util.validator.site;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import org.springframework.util.MultiValueMap;

/**
 * Custom implementation of {@link MultiValueQueryParams} which allows to use custom constraint
 * validation.
 */
@SiteQueryFieldParameterConstraint
public class SiteQueryParams extends MultiValueQueryParams {

  public SiteQueryParams(MultiValueMap<String, String> fields, SearchQueryOperator operator) {
    super(fields, operator);
  }
}
