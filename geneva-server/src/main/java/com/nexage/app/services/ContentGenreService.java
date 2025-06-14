package com.nexage.app.services;

import com.nexage.app.dto.ContentGenreDTO;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentGenreService {
  /**
   * Find all {@link ContentGenreDTO} under request criteria, returning a paginated response.
   *
   * @param qt The term to be found.
   * @param qf Unique {@link Set} of fields.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link ContentGenreDTO} instances based on parameters.
   */
  Page<ContentGenreDTO> findAll(
      String qt,
      @SearchRequestParamConstraint(allowedParams = "genre") Set<String> qf,
      Pageable pageable);
}
