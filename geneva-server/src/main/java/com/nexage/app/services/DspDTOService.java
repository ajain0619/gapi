package com.nexage.app.services;

import com.nexage.app.dto.dsp.DspDTO;
import com.nexage.app.dto.dsp.DspSummaryDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DspDTOService {

  /**
   * Find all {@link DspDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @param isRtbEnabled Flag to return RTB enabled companies.
   * @return {@link Page} of {@link DspDTO} instances based on parameters.
   */
  Page<DspDTO> findAll(Set<String> qf, String qt, Pageable pageable, boolean isRtbEnabled);

  /**
   * Find all {@link DspSummaryDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link DspSummaryDTO} instances based on parameters.
   */
  Page<DspSummaryDTO> findAllSummary(
      Set<String> qf, String qt, Pageable pageable, boolean isRtbEnabled);
}
