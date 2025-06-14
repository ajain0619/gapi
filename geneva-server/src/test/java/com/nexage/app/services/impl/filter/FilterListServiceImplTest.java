package com.nexage.app.services.impl.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.admin.core.repository.BidderConfigDenyAllowFilterListRepository;
import com.nexage.admin.core.repository.FilterListRepository;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.FilterListMapper;
import com.nexage.app.services.FileSystemService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FilterListServiceImplTest {

  @Mock private FilterListRepository filterListRepository;
  @Mock private BidderConfigDenyAllowFilterListRepository bidderConfigFilterListRepository;
  @Mock private FileSystemService fileSystemService;
  @InjectMocks private FilterListServiceImpl filterListService;

  private static final Random RANDOM = ThreadLocalRandom.current();
  private String filterListName = "filterListName";
  private FilterListType filterListType = FilterListType.DOMAIN;
  private FilterListUploadStatus filterListUploadStatus = FilterListUploadStatus.READY;
  private FilterListTypeDTO filterListTypeDTO = FilterListTypeDTO.DOMAIN;
  private Long companyId = RANDOM.nextLong();
  private Integer filterListId = Math.abs(RANDOM.nextInt());
  private MultipartFile filterListCsv;

  @BeforeEach
  public void setUp() {
    filterListCsv = new MockMultipartFile("testMultipartFile", new byte[] {});
  }

  @Test
  void whenCreateFilterListShouldReturnCreatedFilterList() {
    FilterListDTO filterListDTO = createValidFilterListDTO();

    Integer id = RANDOM.nextInt(100);
    when(filterListRepository.save(any(FilterList.class)))
        .thenAnswer(
            arg -> {
              FilterList temp = (FilterList) arg.getArguments()[0];
              temp.setPid(id);
              return temp;
            });
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(id, retVal.getPid());
    assertEquals(filterListName, retVal.getName());
    assertEquals(filterListTypeDTO, retVal.getType());
  }

  @Test
  void shouldCreateFilterListWithReadyStatus() {
    FilterListDTO filterListDTO = createValidFilterListDTO();

    when(filterListRepository.save(
            argThat(matchArg(f -> FilterListUploadStatus.READY.equals(f.getUploadStatus())))))
        .then(returnArg());
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(FilterListUploadStatusDTO.READY, retVal.getUploadStatus());
  }

  @Test
  void shouldCreateFilterListWithInvalidCount0() {
    FilterListDTO filterListDTO = createValidFilterListDTO();

    when(filterListRepository.save(argThat(matchArg(f -> f.getInvalid().equals(0)))))
        .then(returnArg());
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(0, retVal.getInvalid().intValue());
  }

  @Test
  void shouldCreateFilterListWithDuplicateCount0() {
    FilterListDTO filterListDTO = createValidFilterListDTO();

    when(filterListRepository.save(argThat(matchArg(f -> f.getDuplicate().equals(0)))))
        .then(returnArg());
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(0, retVal.getDuplicate().intValue());
  }

  @Test
  void shouldCreateFilterListWithErrorCount0() {
    FilterListDTO filterListDTO = createValidFilterListDTO();

    when(filterListRepository.save(argThat(matchArg(f -> f.getError().equals(0)))))
        .then(returnArg());
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(0, retVal.getError().intValue());
  }

  @Test
  void shouldCreateFilterListWithTotalCount0() {
    FilterListDTO filterListDTO = createValidFilterListDTO();

    when(filterListRepository.save(argThat(matchArg(f -> f.getTotal().equals(0)))))
        .then(returnArg());
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(0, retVal.getTotal().intValue());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenCreatingFilterListWithDuplicateName() {
    FilterListDTO filterListDTO = createValidFilterListDTO();
    when(filterListRepository.save(any(FilterList.class)))
        .thenThrow(mock(DataIntegrityViolationException.class));
    assertThrows(
        GenevaValidationException.class, () -> filterListService.createFilterList(filterListDTO));
  }

  @Test
  void shouldGetFilterListWithValidIds() {
    FilterList filterList = createValidFilterList();
    Integer filterListId = 72;
    when(filterListRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(filterList));
    FilterListDTO retVal = filterListService.getFilterList(companyId, filterListId);

    assertEquals(FilterListMapper.MAPPER.map(filterList), retVal);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenNoFilterListFound() {
    when(filterListRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> filterListService.getFilterList(companyId, 0));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldGetPagedFilterList() {
    int pageSize = 3;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterList> page =
        new PageImpl(
            List.of(createValidFilterList(), createValidFilterList(), createValidFilterList()));
    when(filterListRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    Page<FilterListDTO> filterListPage =
        filterListService.getFilterLists(
            companyId,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    assertNotNull(filterListPage);
    assertEquals(pageSize, filterListPage.getNumberOfElements());
  }

  @Test
  void shouldGetPagedFilterListWithNameLike() {
    String searchName = "myList";
    int pageSize = 3;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterList> page =
        new PageImpl(
            List.of(createValidFilterList(), createValidFilterList(), createValidFilterList()));
    when(filterListRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    Page<FilterListDTO> filterListPage =
        filterListService.getFilterLists(
            companyId,
            pageable,
            Optional.of(Set.of("name")),
            Optional.of(searchName),
            Optional.empty(),
            Optional.empty());

    assertNotNull(filterListPage);
    assertEquals(pageSize, filterListPage.getNumberOfElements());
  }

  @Test
  void shouldGetPagedFilterListAndNoNameSearchWhenNoNameFieldPresent() {
    String searchName = "myList";
    int pageSize = 3;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterList> page =
        new PageImpl(
            List.of(createValidFilterList(), createValidFilterList(), createValidFilterList()));
    when(filterListRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    Page<FilterListDTO> filterListPage =
        filterListService.getFilterLists(
            companyId,
            pageable,
            Optional.of(Set.of("notName")),
            Optional.of(searchName),
            Optional.empty(),
            Optional.empty());

    assertNotNull(filterListPage);
    assertEquals(pageSize, filterListPage.getNumberOfElements());
  }

  @Test
  void
      shouldGetPagedFilterListWithCompanyIdAndFilterListTypeDomainAndFilterListUploadStatusReady() {
    // given
    int pageSize = 3;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterList> page =
        new PageImpl(
            List.of(createValidFilterList(), createValidFilterList(), createValidFilterList()));
    when(filterListRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    // when
    Page<FilterListDTO> filterListPage =
        filterListService.getFilterLists(
            companyId,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(FilterListTypeDTO.DOMAIN),
            Optional.of(FilterListUploadStatusDTO.READY));

    // then
    assertEquals(pageSize, filterListPage.getNumberOfElements());
  }

  @Test
  void shouldGetPagedFilterListWithNameLikeAndFilterListTypeDomainAndFilterListUploadStatusReady() {
    // given
    String searchName = "myList";
    int pageSize = 3;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<FilterList> page =
        new PageImpl(
            List.of(createValidFilterList(), createValidFilterList(), createValidFilterList()));
    when(filterListRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    // when
    Page<FilterListDTO> filterListPage =
        filterListService.getFilterLists(
            companyId,
            pageable,
            Optional.of(Set.of("name")),
            Optional.of(searchName),
            Optional.of(FilterListTypeDTO.DOMAIN),
            Optional.of(FilterListUploadStatusDTO.READY));

    // then
    assertNotNull(filterListPage);
    assertEquals(pageSize, filterListPage.getNumberOfElements());
  }

  @Test
  void shouldDeleteFilterListWhenItExists() {
    FilterList filterList = createValidFilterList();
    filterList.setPid(filterListId);
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));

    FilterListDTO retVal = filterListService.deleteFilterList(companyId, filterListId);
    assertEquals(filterList.getPid(), retVal.getPid());
    verify(filterListRepository)
        .save(
            argThat(
                matchArg(
                    arg ->
                        arg.getActive() == false
                            && arg.getName().startsWith(filterListName)
                            && !arg.getName().equals(filterListName))));
  }

  @Test
  void shouldThrowExceptionWhenDeletingAndFilterListDoesntExist() {
    when(filterListRepository.findOne(nullable(Specification.class))).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> filterListService.deleteFilterList(companyId, filterListId));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowDeleteExceptionWhenDeleteFilterListFails() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    when(filterListRepository.save(any(FilterList.class)))
        .thenThrow(new RuntimeException("Exception deleting"));
    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class,
            () -> filterListService.deleteFilterList(companyId, filterListId));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_DELETE_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldReturnDeletedFilterList() {
    FilterList filterList = createValidFilterList();
    filterList.setPid(filterListId);
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    assertEquals(
        filterList.getPid(), filterListService.deleteFilterList(companyId, filterListId).getPid());
  }

  @Test
  void shouldUpdateFilterListStatusToPendingWhenAddDomainsSuccess() throws IOException {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    FilterList filterListUpdated = new FilterList();
    filterListUpdated.setUploadStatus(FilterListUploadStatus.PENDING);
    when(filterListRepository.save(
            argThat(matchArg(arg -> FilterListUploadStatus.PENDING.equals(arg.getUploadStatus())))))
        .thenReturn(filterListUpdated);

    assertEquals(
        FilterListUploadStatusDTO.PENDING,
        filterListService
            .addFilterListCsv(companyId, filterListId, filterListCsv)
            .getUploadStatus());
  }

  @Test
  void shouldThrowExceptionWhenAddingDomainsToFilterListThatDoesntExist() {
    when(filterListRepository.findOne(nullable(Specification.class))).thenReturn(Optional.empty());

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> filterListService.addFilterListCsv(companyId, filterListId, filterListCsv));
    assertEquals(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenWritingFileSystemFails() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    doThrow(new RuntimeException("Error writing domain file"))
        .when(fileSystemService)
        .write(anyString(), anyString(), any());
    assertThrows(
        GenevaValidationException.class,
        () -> filterListService.addFilterListCsv(companyId, filterListId, filterListCsv));
  }

  @Test
  void shouldWriteFileNameWithBuyerIdPrefix() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    filterListService.addFilterListCsv(companyId, 1, filterListCsv);
    verify(fileSystemService)
        .write(anyString(), matches(".+?/.+?/" + companyId.toString() + ".+"), any());
  }

  @Test
  void shouldWriteFileNameContainingFilterListId() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    filterListService.addFilterListCsv(1L, filterListId, filterListCsv);
    verify(fileSystemService)
        .write(anyString(), contains(String.format("_%d_", filterListId)), any());
  }

  @Test
  void shouldWriteBuyerIdFollowedByFilterListIdInFileName() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    filterListService.addFilterListCsv(companyId, filterListId, filterListCsv);
    verify(fileSystemService).write(anyString(), contains(companyId + "_" + filterListId), any());
  }

  @Test
  void shouldWriteFileNameWithFilterListPrefix() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    filterListService.addFilterListCsv(companyId, 1, filterListCsv);
    verify(fileSystemService).write(anyString(), startsWith("filterList/csv/"), any());
  }

  @Test
  void shouldWriteFileNameContainingFilterListTypeDomain() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    filterListService.addFilterListCsv(1L, filterListId, filterListCsv);
    verify(fileSystemService).write(anyString(), contains(String.format("_%s", "domains")), any());
  }

  @Test
  void shouldWriteFileNameContainingFilterListTypeApp() {
    FilterList filterList = new FilterList();
    filterList.setName(filterListName);
    filterList.setType(FilterListType.APP);
    filterList.setCompanyId(companyId);
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    filterListService.addFilterListCsv(1L, filterListId, filterListCsv);
    verify(fileSystemService).write(anyString(), contains(String.format("_%s", "apps")), any());
  }

  @Test
  void shouldThrowExceptionWhenErrorWritingToFileSystem() {
    FilterList filterList = createValidFilterList();
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    doThrow(GenevaAppRuntimeException.class)
        .when(fileSystemService)
        .write(anyString(), anyString(), any());
    assertThrows(
        Exception.class,
        () -> filterListService.addFilterListCsv(companyId, filterListId, filterListCsv));
  }

  @Test
  void shouldDeleteFilterListBidderConfigAssignmentsWhenDeletingFilterList() {
    FilterList filterList = createValidFilterList();
    filterList.setPid(filterListId);
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    when(bidderConfigFilterListRepository.findAllByFilterListPid(filterListId))
        .thenReturn(List.of(new BidderConfigDenyAllowFilterList()));
    filterListService.deleteFilterList(companyId, filterListId).getPid();
    verify(bidderConfigFilterListRepository).deleteInBatch(any());
  }

  @Test
  void shouldNotDeleteFilterListBidderConfigAssignmentsWhenNoAssignmentsForDeletedFilterList() {
    FilterList filterList = createValidFilterList();
    filterList.setPid(filterListId);
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    when(bidderConfigFilterListRepository.findAllByFilterListPid(filterListId)).thenReturn(null);
    filterListService.deleteFilterList(companyId, filterListId).getPid();
    verify(bidderConfigFilterListRepository, never()).deleteInBatch(any());
  }

  @Test
  void shouldHaveValidZeroWhenTotalNull() {
    FilterList filterList = createValidFilterList();
    filterList.setDuplicate(1);
    filterList.setError(1);
    filterList.setInvalid(1);
    Integer filterListId = 72;
    when(filterListRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(filterList));
    FilterListDTO filterListDTO = filterListService.getFilterList(companyId, filterListId);

    assertEquals(0, filterListDTO.getValid());
  }

  @Test
  void shouldHaveValidZeroWhenReturnNegativeValid() {
    FilterList filterListWithoutDuplicate = createValidFilterList();
    filterListWithoutDuplicate.setTotal(1);
    filterListWithoutDuplicate.setError(1);
    filterListWithoutDuplicate.setInvalid(1);
    Integer filterListId = 72;

    when(filterListRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(filterListWithoutDuplicate));
    FilterListDTO filterListDTO = filterListService.getFilterList(companyId, filterListId);

    assertEquals(0, filterListDTO.getValid());

    FilterList filterListWithoutError = createValidFilterList();
    filterListWithoutError.setTotal(1);
    filterListWithoutError.setDuplicate(1);
    filterListWithoutError.setInvalid(1);

    when(filterListRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(filterListWithoutError));
    filterListDTO = filterListService.getFilterList(companyId, filterListId);

    assertEquals(0, filterListDTO.getValid());

    FilterList filterListWithoutInvalid = createValidFilterList();
    filterListWithoutInvalid.setTotal(1);
    filterListWithoutInvalid.setDuplicate(1);
    filterListWithoutInvalid.setError(1);

    when(filterListRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(filterListWithoutInvalid));
    filterListDTO = filterListService.getFilterList(companyId, filterListId);

    assertEquals(0, filterListDTO.getValid());
  }

  @Test
  void whenCreateFilterListTypeAppShouldReturnCreatedFilterListOfApp() {
    FilterListDTO filterListDTO =
        FilterListDTO.builder()
            .name(filterListName)
            .type(FilterListTypeDTO.APP)
            .buyerId(companyId)
            .build();

    Integer id = RANDOM.nextInt(100);
    when(filterListRepository.save(any(FilterList.class)))
        .thenAnswer(
            arg -> {
              FilterList temp = (FilterList) arg.getArguments()[0];
              temp.setPid(id);
              return temp;
            });
    FilterListDTO retVal = filterListService.createFilterList(filterListDTO);

    assertEquals(id, retVal.getPid());
    assertEquals(filterListName, retVal.getName());
    assertEquals(FilterListTypeDTO.APP, retVal.getType());
  }

  @Test
  void shouldThrowExceptionWhenErrorWritingAppFilterListToFileSystem() {
    FilterList filterList = new FilterList();
    filterList.setName(filterListName);
    filterList.setType(FilterListType.APP);
    filterList.setCompanyId(companyId);
    when(filterListRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.of(filterList));
    doThrow(GenevaAppRuntimeException.class)
        .when(fileSystemService)
        .write(anyString(), anyString(), any());
    assertThrows(
        Exception.class,
        () -> filterListService.addFilterListCsv(companyId, filterListId, filterListCsv));
  }

  private Answer returnArg() {
    return arg -> arg.getArguments()[0];
  }

  private ArgumentMatcher<FilterList> matchArg(Predicate<FilterList> argMatches) {
    return o -> argMatches.test((FilterList) o);
  }

  private FilterList createValidFilterList() {
    FilterList filterList = new FilterList();
    filterList.setName(filterListName);
    filterList.setType(filterListType);
    filterList.setCompanyId(companyId);
    return filterList;
  }

  private FilterListDTO createValidFilterListDTO() {
    return FilterListDTO.builder()
        .name(filterListName)
        .type(filterListTypeDTO)
        .buyerId(companyId)
        .build();
  }
}
