package com.nexage.app.search;

import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
@Profile("e2e-test")
@RequestMapping(value = "/search-tests", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchTestController {

  @GetMapping
  public ResponseEntity<MultiValueQueryParams> getParsedSearchWithoutOperator(
      @MultiValueSearchParams(operator = SearchQueryOperator.OR) MultiValueQueryParams search) {
    return ResponseEntity.ok(search);
  }

  @GetMapping(params = "qo")
  public ResponseEntity<MultiValueQueryParams> getParsedSearchWithOperator(
      @MultiValueSearchParams MultiValueQueryParams search) {
    return ResponseEntity.ok(search);
  }
}
