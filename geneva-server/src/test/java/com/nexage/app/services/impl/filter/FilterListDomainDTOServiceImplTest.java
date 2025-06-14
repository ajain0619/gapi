package com.nexage.app.services.impl.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.admin.core.model.filter.FilterListDomain_;
import com.nexage.admin.core.repository.FilterListDomainRepository;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListDomainDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.FilterListDomainMapper;
import com.nexage.app.services.filter.FilterListService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FilterListDomainDTOServiceImplTest {

  @Mock private FilterListService filterListService;
  @Mock private FilterListDomainRepository domainRepository;

  @InjectMocks private FilterListDomainDTOServiceImpl filterListDomainService;

  private Long buyerId;
  private Integer filterListId;
  private MultipartFile filterListCsv;
  private Set<Integer> filterListDomainIds;
  private FilterListDTO filterListDTO;
  private Integer filterListDomainId;
  private FilterListDomain filterListDomain;
  private FilterListDomainDTO filterListDomainDTO;

  @BeforeEach
  public void setUp() {
    buyerId = Math.abs(ThreadLocalRandom.current().nextLong());
    filterListId = Math.abs(ThreadLocalRandom.current().nextInt());
    filterListDomainIds =
        new HashSet(
            Arrays.asList(
                ThreadLocalRandom.current().nextInt(), ThreadLocalRandom.current().nextInt()));
    filterListCsv = new MockMultipartFile("testMultiPartFile", new byte[] {});
    filterListDTO = createValidFilterListDTO();
    filterListDomainId = ThreadLocalRandom.current().nextInt();
    filterListDomain = createValidFilterListDomain();
    filterListDomainDTO = FilterListDomainMapper.INSTANCE.map(filterListDomain);
  }

  @Test
  void shouldGetPagedDomainFilterList() {
    int pageSize = 3;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterListDomain> filterListDomains =
        new PageImpl(
            Arrays.asList(
                createValidFilterListDomain(),
                createValidFilterListDomain(),
                createValidFilterListDomain()));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(domainRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(filterListDomains);
    Page<FilterListDomainDTO> page =
        filterListDomainService.getFilterListDomains(buyerId, filterListId, pageable, null, null);

    assertNotNull(page);
    assertNotNull(page.getContent());
    assertEquals(pageSize, page.getNumberOfElements());
  }

  @Test
  void shouldGetDomainFilterListWithQueryFieldAndTerm() {
    Pageable pageable = PageRequest.of(1, 1);
    Set<String> qf = new HashSet(Arrays.asList(FilterListDomain_.DOMAIN));
    String qt = "nintendo.com";
    Page<FilterListDomain> page = new PageImpl(Arrays.asList(filterListDomain));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(domainRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<FilterListDomainDTO> retVal =
        filterListDomainService.getFilterListDomains(buyerId, filterListId, pageable, qf, qt);
    assertEquals(filterListDomainDTO, retVal.getContent().get(0));
  }

  @Test
  void shouldGetDomainFilterListWithoutQueryTermsWhenQueryTermNull() {
    Pageable pageable = PageRequest.of(1, 1);
    Set<String> qf = new HashSet(Arrays.asList(FilterListDomain_.DOMAIN));
    Page<FilterListDomain> page = new PageImpl(Arrays.asList(filterListDomain));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(domainRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<FilterListDomainDTO> retVal =
        filterListDomainService.getFilterListDomains(buyerId, filterListId, pageable, qf, null);
    assertEquals(filterListDomainDTO, retVal.getContent().get(0));
  }

  @Test
  void shouldGetDomainFilterListWithoutQueryTermsWhenQueryFieldNull() {
    Pageable pageable = PageRequest.of(1, 1);
    String qt = "nintendo.com";
    Page<FilterListDomain> page = new PageImpl(Arrays.asList(createValidFilterListDomain()));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(domainRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<FilterListDomainDTO> retVal =
        filterListDomainService.getFilterListDomains(buyerId, filterListId, pageable, null, qt);
    assertEquals(filterListDomainDTO, retVal.getContent().get(0));
  }

  @Test
  void shouldReturnDeletedFilterListDomainIds() {
    List<FilterListDomain> filterListDomains =
        filterListDomainIds.stream()
            .map(this::createValidFilterListDomain)
            .collect(Collectors.toList());
    when(domainRepository.findAll(any(Specification.class))).thenReturn(filterListDomains);
    assertEquals(
        filterListDomains.stream()
            .map(FilterListDomainMapper.INSTANCE::map)
            .collect(Collectors.toList()),
        filterListDomainService.deleteFilterListDomains(
            buyerId, filterListId, filterListDomainIds));
  }

  @Test
  void shouldReturnEmptyListWhenDeletingFilterListDomainsThatDontExist() {
    List<FilterListDomain> filterListDomains = Collections.emptyList();
    when(domainRepository.findAll(any(Specification.class))).thenReturn(filterListDomains);
    assertTrue(
        filterListDomainService
            .deleteFilterListDomains(buyerId, filterListId, filterListDomainIds)
            .isEmpty());
    verify(domainRepository, never()).deleteInBatch(anyCollection());
  }

  @Test
  void shouldThrowExceptionWhenDeleteFilterListDomainsFails() {
    List<FilterListDomain> filterListDomains =
        filterListDomainIds.stream()
            .map(this::createValidFilterListDomain)
            .collect(Collectors.toList());
    when(domainRepository.findAll(any(Specification.class))).thenReturn(filterListDomains);
    doThrow(new RuntimeException("Delete failed"))
        .when(domainRepository)
        .deleteInBatch(filterListDomains);

    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class,
            () ->
                filterListDomainService.deleteFilterListDomains(
                    buyerId, filterListId, filterListDomainIds));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_DOMAIN_DELETE_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenAddingDomainsAndFilterListDoesntExist() {
    when(filterListService.getFilterList(buyerId, filterListId))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND));
    Pageable pageable = PageRequest.of(1, 1);
    Set<String> qf = Collections.emptySet();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                filterListDomainService.getFilterListDomains(
                    buyerId, filterListId, pageable, qf, ""));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenDeletingDomainsFromFilterListThatDoesntExist() {
    Set<Integer> filterListDomainIds = new HashSet(Arrays.asList(1, 2, 3));
    when(filterListService.getFilterList(buyerId, filterListId))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND));
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                filterListDomainService.deleteFilterListDomains(
                    buyerId, filterListId, filterListDomainIds));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  private FilterListDomain createValidFilterListDomain(Integer id) {
    FilterListDomain filterListDomain = new FilterListDomain();
    filterListDomain.setPid(id);
    return filterListDomain;
  }

  private FilterListDomain createValidFilterListDomain() {
    return FilterListDomain.builder().pid(filterListDomainId).filterListId(filterListId).build();
  }

  private FilterListDTO createValidFilterListDTO() {
    FilterListDTO filterListDTO = new FilterListDTO();
    filterListDTO.setPid(filterListId);
    return filterListDTO;
  }
}
