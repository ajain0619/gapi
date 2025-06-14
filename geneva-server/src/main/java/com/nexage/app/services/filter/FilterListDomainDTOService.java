package com.nexage.app.services.filter;

import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.app.dto.filter.FilterListDomainDTO;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterListDomainDTOService {

  /**
   * Get Paginated {@link FilterListDomain} for specified buyerId and filterListId
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @param pageable {@link Pageable}
   * @param qf {@link Set<String>} Optional query fields to search on
   * @param qt {@link String} Optional query term for query fields
   * @return {@link Page<FilterListDomain>}
   */
  Page<FilterListDomainDTO> getFilterListDomains(
      Long buyerId, Integer filterListId, Pageable pageable, Set<String> qf, String qt);

  /**
   * Delete filterListDomainIds associated with filterListId
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @param filterListDomainIds {@link Set<Integer>}
   */
  List<FilterListDomainDTO> deleteFilterListDomains(
      Long buyerId, Integer filterListId, Set<Integer> filterListDomainIds);
}
