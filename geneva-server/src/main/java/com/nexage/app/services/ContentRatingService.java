package com.nexage.app.services;

import com.nexage.app.dto.ContentRatingDTO;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRatingService {
  /**
   * Find all {@link ContentRatingDTO} under request criteria, returning a paginated response.
   *
   * @param qt The term to be found.
   * @param qf Unique {@link Set} of fields.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link ContentRatingDTO} instances based on parameters.
   */
  Page<ContentRatingDTO> findAll(
      String qt,
      @SearchRequestParamConstraint(allowedParams = "rating") Set<String> qf,
      Pageable pageable);
}
