package com.nexage.app.services.filter;

import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FilterListService {

  /**
   * Creates a {@link FilterListDTO} object in the database
   *
   * @param filterList {@link FilterListDTO}
   * @return created {@link FilterListDTO}
   */
  FilterListDTO createFilterList(FilterListDTO filterList);

  /**
   * Get single {@link FilterListDTO} object that contains buyerId and pid
   *
   * @param buyerId {@link Long}
   * @param pid {@link Integer}
   * @return {@link FilterListDTO}
   */
  FilterListDTO getFilterList(Long buyerId, Integer pid);

  /**
   * Get Paginated {@link FilterListDTO} with buyerId and filterList name
   *
   * @param buyerId {@link Long}
   * @param pageable {@link Pageable}
   * @param qf {@link Optional<Set<String>>} Optional Set of query field names
   * @param qt {@link Optional<String>} Optional query term
   * @param filterListType {@link FilterListType} filterListType to query for
   * @param filterListUploadStatus {@link FilterListUploadStatus} FilterListUploadStatus to query
   *     for
   * @return {@link Page<FilterListDTO>}
   */
  Page<FilterListDTO> getFilterLists(
      Long buyerId,
      Pageable pageable,
      Optional<Set<String>> qf,
      Optional<String> qt,
      Optional<FilterListTypeDTO> filterListType,
      Optional<FilterListUploadStatusDTO> filterListUploadStatus);

  /**
   * Deletes the {@link FilterListDTO} in database with corresponding buyerId and pid
   *
   * @param buyerId {@link Long}
   * @param pid {@link Integer}
   * @return {@link FilterListDTO} object deleted
   */
  FilterListDTO deleteFilterList(Long buyerId, Integer pid);

  /**
   * Writes domain CSV file to be processed by a separate process beyond this API
   *
   * @param buyerId {@link Long}
   * @param pid {@link Integer}
   * @param filterListCsv {@link MultipartFile}
   * @return {@link FilterListDTO}
   */
  FilterListDTO addFilterListCsv(Long buyerId, Integer pid, MultipartFile filterListCsv);
}
