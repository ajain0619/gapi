package com.nexage.app.services.impl.filter;

import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.admin.core.repository.FilterListAppBundleRepository;
import com.nexage.admin.core.specification.FilterListAppBundleSpecification;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.FilterListAppBundleMapper;
import com.nexage.app.services.filter.FilterListAppBundleDTOService;
import com.nexage.app.services.filter.FilterListService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@Transactional
public class FilterListAppBundleDTOServiceImpl implements FilterListAppBundleDTOService {

  private final FilterListService filterListService;
  private final FilterListAppBundleRepository filterListAppBundleRepository;

  @Autowired
  public FilterListAppBundleDTOServiceImpl(
      FilterListService filterListService,
      FilterListAppBundleRepository filterListAppBundleRepository) {
    this.filterListService = filterListService;
    this.filterListAppBundleRepository = filterListAppBundleRepository;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public Page<FilterListAppBundleDTO> getFilterListAppBundles(
      Long buyerId, Integer filterListId, Pageable pageable, Set<String> qf, String qt) {
    FilterListDTO filterListDTO = filterListService.getFilterList(buyerId, filterListId);
    return filterListAppBundleRepository
        .findAll(getFindAllSpecification(filterListDTO.getPid(), qf, qt), pageable)
        .map(FilterListAppBundleMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#buyerId)")
  public List<FilterListAppBundleDTO> deleteFilterListAppBundles(
      Long buyerId, Integer filterListId, Set<Integer> filterListAppBundleIds) {
    filterListService.getFilterList(buyerId, filterListId);
    List<FilterListAppBundle> filterListAppBundles =
        filterListAppBundleRepository.findAll(
            FilterListAppBundleSpecification.withFilterListAppBundleIds(
                filterListId, filterListAppBundleIds));
    if (CollectionUtils.isNotEmpty(filterListAppBundles)) {
      try {
        filterListAppBundleRepository.deleteInBatch(filterListAppBundles);
      } catch (Exception exception) {
        log.warn("Exception deleting filterListAppBundle PIDs", exception);
        throw new GenevaAppRuntimeException(
            ServerErrorCodes.SERVER_FILTER_LIST_APP_BUNDLE_DELETE_ERROR);
      }
    }
    return filterListAppBundles.stream()
        .map(FilterListAppBundle::getPid)
        .map(pid -> FilterListAppBundleDTO.builder().pid(pid).build())
        .collect(Collectors.toList());
  }

  private Specification<FilterListAppBundle> getFindAllSpecification(
      Integer filterListId, Set<String> qf, String qt) {
    if (useQueryTermAndField().test(qf, qt)) {
      return FilterListAppBundleSpecification.withFilterListIdAndLike(filterListId, qf, qt);
    } else {
      return FilterListAppBundleSpecification.withFilterListId(filterListId);
    }
  }

  private BiPredicate<Set<String>, String> useQueryTermAndField() {
    return (qf, qt) -> CollectionUtils.isNotEmpty(qf) && StringUtils.isNotEmpty(qt);
  }
}
