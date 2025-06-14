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

import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.admin.core.model.filter.FilterListAppBundle_;
import com.nexage.admin.core.repository.FilterListAppBundleRepository;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.FilterListAppBundleMapper;
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
class FilterListAppBundleDTOServiceImplTest {

  @Mock private FilterListService filterListService;
  @Mock private FilterListAppBundleRepository appBundleRepository;

  @InjectMocks private FilterListAppBundleDTOServiceImpl filterListAppBundleService;

  private Long buyerId;
  private Integer filterListId;
  private MultipartFile filterListCsv;
  private Set<Integer> filterListAppBundleIds;
  private FilterListDTO filterListDTO;
  private Integer filterListAppBundleId;
  private FilterListAppBundle filterListAppBundle;
  private FilterListAppBundleDTO filterListAppBundleDTO;

  @BeforeEach
  void setUp() {
    buyerId = Math.abs(ThreadLocalRandom.current().nextLong());
    filterListId = Math.abs(ThreadLocalRandom.current().nextInt());
    filterListAppBundleIds =
        new HashSet(
            Arrays.asList(
                ThreadLocalRandom.current().nextInt(), ThreadLocalRandom.current().nextInt()));
    filterListCsv = new MockMultipartFile("testMultiPartFile", new byte[] {});
    filterListDTO = createValidFilterListDTO();
    filterListAppBundleId = ThreadLocalRandom.current().nextInt();
    filterListAppBundle = createValidFilterListAppBundle();
    filterListAppBundleDTO = FilterListAppBundleMapper.MAPPER.map(filterListAppBundle);
  }

  @Test
  void shouldGetPagedAppBundleFilterList() {
    int pageSize = 5;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterListAppBundle> filterListAppBundles =
        new PageImpl(
            Arrays.asList(
                createValidFilterListAppBundle(),
                createValidFilterListAppBundle(),
                createValidFilterListAppBundle(),
                createValidFilterListAppBundle(),
                createValidFilterListAppBundle()));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(appBundleRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(filterListAppBundles);
    Page<FilterListAppBundleDTO> page =
        filterListAppBundleService.getFilterListAppBundles(
            buyerId, filterListId, pageable, null, null);

    assertNotNull(page);
    assertNotNull(page.getContent());
    assertEquals(pageSize, page.getNumberOfElements());
  }

  @Test
  void shouldGetAppBundleFilterListWithQueryFieldAndTerm() {
    Pageable pageable = PageRequest.of(1, 1);
    Set<String> qf = new HashSet(Arrays.asList(FilterListAppBundle_.APP));
    String qt = "test";
    Page<FilterListAppBundle> page = new PageImpl(Arrays.asList(filterListAppBundle));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(appBundleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<FilterListAppBundleDTO> retVal =
        filterListAppBundleService.getFilterListAppBundles(buyerId, filterListId, pageable, qf, qt);
    assertEquals(filterListAppBundleDTO, retVal.getContent().get(0));
  }

  @Test
  void shouldGetAppBundleFilterListWithoutQueryTermsWhenQueryTermNull() {
    Pageable pageable = PageRequest.of(1, 1);
    Set<String> qf = new HashSet(Arrays.asList(FilterListAppBundle_.APP));
    Page<FilterListAppBundle> page = new PageImpl(Arrays.asList(filterListAppBundle));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(appBundleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<FilterListAppBundleDTO> retVal =
        filterListAppBundleService.getFilterListAppBundles(
            buyerId, filterListId, pageable, qf, null);
    assertEquals(filterListAppBundleDTO, retVal.getContent().get(0));
  }

  @Test
  void shouldGetAppBundleFilterListWithoutQueryTermsWhenQueryFieldNull() {
    Pageable pageable = PageRequest.of(1, 1);
    String qt = "test";
    Page<FilterListAppBundle> page = new PageImpl(Arrays.asList(createValidFilterListAppBundle()));
    when(filterListService.getFilterList(buyerId, filterListId)).thenReturn(filterListDTO);
    when(appBundleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<FilterListAppBundleDTO> retVal =
        filterListAppBundleService.getFilterListAppBundles(
            buyerId, filterListId, pageable, null, qt);
    assertEquals(filterListAppBundleDTO, retVal.getContent().get(0));
  }

  @Test
  void shouldReturnDeletedFilterListAppBundleIds() {
    List<FilterListAppBundle> filterListAppBundles =
        filterListAppBundleIds.stream()
            .map(this::createValidFilterListAppBundle)
            .collect(Collectors.toList());
    when(appBundleRepository.findAll(any(Specification.class))).thenReturn(filterListAppBundles);
    assertEquals(
        filterListAppBundles.stream()
            .map(FilterListAppBundleMapper.MAPPER::map)
            .collect(Collectors.toList()),
        filterListAppBundleService.deleteFilterListAppBundles(
            buyerId, filterListId, filterListAppBundleIds));
  }

  @Test
  void shouldReturnEmptyListWhenDeletingFilterListAppBundlesThatDontExist() {
    List<FilterListAppBundle> filterListAppBundles = Collections.emptyList();
    when(appBundleRepository.findAll(any(Specification.class))).thenReturn(filterListAppBundles);
    assertTrue(
        filterListAppBundleService
            .deleteFilterListAppBundles(buyerId, filterListId, filterListAppBundleIds)
            .isEmpty());
    verify(appBundleRepository, never()).deleteInBatch(anyCollection());
  }

  @Test
  void shouldThrowExceptionWhenDeleteFilterListAppBundlesFails() {
    List<FilterListAppBundle> filterListAppBundles =
        filterListAppBundleIds.stream()
            .map(this::createValidFilterListAppBundle)
            .collect(Collectors.toList());
    when(appBundleRepository.findAll(any(Specification.class))).thenReturn(filterListAppBundles);
    doThrow(new RuntimeException("Delete failed"))
        .when(appBundleRepository)
        .deleteInBatch(filterListAppBundles);

    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class,
            () ->
                filterListAppBundleService.deleteFilterListAppBundles(
                    buyerId, filterListId, filterListAppBundleIds));
    assertEquals(
        ServerErrorCodes.SERVER_FILTER_LIST_APP_BUNDLE_DELETE_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenAddingAppBundlesAndFilterListDoesntExist() {
    Pageable pageable = PageRequest.of(1, 1);
    when(filterListService.getFilterList(buyerId, filterListId))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND));

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                filterListAppBundleService.getFilterListAppBundles(
                    buyerId, filterListId, pageable, null, ""));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenDeletingAppBundlesFromFilterListThatDoesntExist() {
    Set<Integer> filterListAppBundleIds = new HashSet(Arrays.asList(1, 2, 3));
    when(filterListService.getFilterList(buyerId, filterListId))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND));
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                filterListAppBundleService.deleteFilterListAppBundles(
                    buyerId, filterListId, filterListAppBundleIds));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  private FilterListAppBundle createValidFilterListAppBundle(Integer id) {
    FilterListAppBundle filterListAppBundle = new FilterListAppBundle();
    filterListAppBundle.setPid(id);
    return filterListAppBundle;
  }

  private FilterListAppBundle createValidFilterListAppBundle() {
    return FilterListAppBundle.builder()
        .pid(filterListAppBundleId)
        .filterListId(filterListId)
        .build();
  }

  private FilterListDTO createValidFilterListDTO() {
    FilterListDTO filterListDTO = new FilterListDTO();
    filterListDTO.setPid(filterListId);
    return filterListDTO;
  }
}
