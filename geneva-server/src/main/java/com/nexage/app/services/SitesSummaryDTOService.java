package com.nexage.app.services;

import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.dto.seller.SitesSummaryDTO;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface SitesSummaryDTOService {
  /**
   * Find all {@link SitesSummaryDTO} under request criteria, returning a paginated response.
   *
   * @param sellerId sellerId
   * @param start start
   * @param stop stop
   * @param siteName siteName
   * @param sitePids sitePids
   * @param pageable Pagination based on {@link Pageable}
   * @return Summary of {@link SiteDTO} instances based on parameters.
   */
  SitesSummaryDTO getSitesSummaryDTO(
      Long sellerId,
      Date start,
      Date stop,
      Optional<String> siteName,
      Optional<List<Long>> sitePids,
      Pageable pageable)
      throws ParseException;
}
