package com.nexage.app.services;

import com.nexage.app.dto.DeviceTypeDTO;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceTypeService {
  /**
   * Find all {@link DeviceTypeDTO} under request criteria, returning a paginated response.
   *
   * @param qt The term to be found.
   * @param qf Unique {@link Set} of fields.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link DeviceTypeDTO} instances based on parameters.
   */
  Page<DeviceTypeDTO> findAll(
      String qt,
      @SearchRequestParamConstraint(allowedParams = "name") Set<String> qf,
      Pageable pageable);
}
