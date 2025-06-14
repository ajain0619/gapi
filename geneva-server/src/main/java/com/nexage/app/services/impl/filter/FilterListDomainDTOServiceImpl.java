package com.nexage.app.services.impl.filter;

import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.admin.core.repository.FilterListDomainRepository;
import com.nexage.admin.core.specification.FilterListDomainSpecification;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListDomainDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.FilterListDomainMapper;
import com.nexage.app.services.filter.FilterListDomainDTOService;
import com.nexage.app.services.filter.FilterListService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Log4j2
@Transactional
public class FilterListDomainDTOServiceImpl implements FilterListDomainDTOService {

  private final FilterListService filterListService;
  private final FilterListDomainRepository filterListDomainRepository;

  @Override
  public Page<FilterListDomainDTO> getFilterListDomains(
      Long buyerId, Integer filterListId, Pageable pageable, Set<String> qf, String qt) {
    FilterListDTO filterListDTO = filterListService.getFilterList(buyerId, filterListId);
    return filterListDomainRepository
        .findAll(getFindAllSpecification(filterListDTO.getPid(), qf, qt), pageable)
        .map(FilterListDomainMapper.INSTANCE::map);
  }

  @Override
  public List<FilterListDomainDTO> deleteFilterListDomains(
      Long buyerId, Integer filterListId, Set<Integer> filterListDomainIds) {
    filterListService.getFilterList(buyerId, filterListId);
    List<FilterListDomain> filterListDomains =
        filterListDomainRepository.findAll(
            FilterListDomainSpecification.withFilterListDomainIds(
                filterListId, filterListDomainIds));
    if (CollectionUtils.isNotEmpty(filterListDomains)) {
      try {
        filterListDomainRepository.deleteInBatch(filterListDomains);
      } catch (Exception exception) {
        log.warn("Exception deleting filterListDomain PIDs", exception);
        throw new GenevaAppRuntimeException(
            ServerErrorCodes.SERVER_FILTER_LIST_DOMAIN_DELETE_ERROR);
      }
    }
    return filterListDomains.stream()
        .map(FilterListDomain::getPid)
        .map(pid -> FilterListDomainDTO.builder().pid(pid).build())
        .collect(Collectors.toList());
  }

  private Specification<FilterListDomain> getFindAllSpecification(
      Integer filterListId, Set<String> qf, String qt) {
    if (useQueryTermAndField().test(qf, qt)) {
      return FilterListDomainSpecification.withFilterListIdAndLike(filterListId, qf, qt);
    } else {
      return FilterListDomainSpecification.withFilterListId(filterListId);
    }
  }

  private BiPredicate<Set<String>, String> useQueryTermAndField() {
    return (qf, qt) -> CollectionUtils.isNotEmpty(qf) && StringUtils.isNotEmpty(qt);
  }
}
