package com.nexage.app.services;

import com.nexage.app.dto.deals.DealDTO;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DealDTOService {
  /**
   * Find all {@link DealDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link DealDTO} instances based on parameters.
   */
  Page<DealDTO> findAll(String qt, Set<String> qf, Pageable pageable);

  /**
   * Find one {@link DealDTO}.
   *
   * @param dealPid the deal pid
   * @return the {@link DealDTO}.
   */
  DealDTO findOne(Long dealPid);

  Optional<Map<String, List<String>>> createMultiValueMap(Set<String> qf);

  void validateDealCategoryIfPresent(Map<String, List<String>> qf);

  Page<DealDTO> getDeals(MultiValueQueryParams multiValueQueryParams, Pageable pageable);
}
