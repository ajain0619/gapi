package com.nexage.app.services;

import com.nexage.app.dto.DeviceOsDTO;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceOsService {
  /**
   * Find all {@link DeviceOsDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link DeviceOsDTO} instances based on parameters.
   */
  Page<DeviceOsDTO> findAllByName(
      String qt,
      @SearchRequestParamConstraint(allowedParams = "name") Set<String> qf,
      Pageable pageable);
}
