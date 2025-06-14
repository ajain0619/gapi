package com.nexage.app.services.filter;

import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterListAppBundleDTOService {

  /**
   * Get Paginated {@link FilterListAppBundle} for specified buyerId and filterListId
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @param pageable {@link Pageable}
   * @param qf {@link Set<String>} Optional query fields to search on
   * @param qt {@link String} Optional query term for query fields
   * @return {@link Page<FilterListAppBundle>}
   */
  Page<FilterListAppBundleDTO> getFilterListAppBundles(
      Long buyerId, Integer filterListId, Pageable pageable, Set<String> qf, String qt);

  /**
   * Delete filterListAppBundleIds associated with filterListId
   *
   * @param buyerId {@link Long}
   * @param filterListId {@link Integer}
   * @param filterListAppBundleIds {@link Set<Integer>}
   */
  List<FilterListAppBundleDTO> deleteFilterListAppBundles(
      Long buyerId, Integer filterListId, Set<Integer> filterListAppBundleIds);
}
