package com.nexage.app.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.services.filter.FilterListService;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FilterListDTOControllerTest {

  @Mock private FilterListService filterListService;

  @InjectMocks private FilterListDTOController filterListDTOController;

  private final Random RANDOM = ThreadLocalRandom.current();
  private final long BUYER_ID = RANDOM.nextLong();
  private final int FILTER_LIST_ID = RANDOM.nextInt();

  @Test
  void shouldCreateFilterListWithBuyerId() {
    FilterListDTO filterListDTO = createFilterList();
    when(filterListService.createFilterList(
            argThat(matchArg(f -> f.getBuyerId().equals(BUYER_ID)))))
        .thenAnswer(arg -> arg.getArguments()[0]);

    assertEquals(
        Long.valueOf(BUYER_ID),
        filterListDTOController.createFilterList(BUYER_ID, filterListDTO).getBody().getBuyerId());
  }

  @Test
  void shouldReturnResponseEntityWithOkStatusWhenCreatingFilterList() {
    FilterListDTO filterListDTO = createFilterList();
    when(filterListService.createFilterList(
            argThat(matchArg(f -> f.getBuyerId().equals(BUYER_ID)))))
        .thenAnswer(arg -> arg.getArguments()[0]);

    assertEquals(
        HttpStatus.OK,
        filterListDTOController.createFilterList(BUYER_ID, filterListDTO).getStatusCode());
  }

  @Test
  void shouldGetSingleFilterList() {
    when(filterListService.getFilterList(BUYER_ID, FILTER_LIST_ID)).thenReturn(createFilterList());
    assertNotNull(filterListDTOController.getFilterList(BUYER_ID, FILTER_LIST_ID).getBody());
  }

  @Test
  void shouldGetResponseStatusOkWhenGetFilterList() {
    when(filterListService.getFilterList(BUYER_ID, FILTER_LIST_ID)).thenReturn(createFilterList());
    assertEquals(
        HttpStatus.OK,
        filterListDTOController.getFilterList(BUYER_ID, FILTER_LIST_ID).getStatusCode());
  }

  @Test
  void shouldGetMultipleFilterLists() {
    Pageable pageable = PageRequest.of(1, 2);
    when(filterListService.getFilterLists(
            BUYER_ID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()))
        .thenReturn(new PageImpl(Arrays.asList(createFilterList(), createFilterList())));
    Page<FilterListDTO> retVal =
        filterListDTOController
            .getFilterLists(
                BUYER_ID,
                pageable,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty())
            .getBody();
    assertEquals(2, retVal.getTotalElements());
  }

  @Test
  void shouldGetResponseStatusOkWhenGetMultipleFilterList() {
    Pageable pageable = PageRequest.of(1, 2);
    when(filterListService.getFilterLists(
            BUYER_ID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()))
        .thenReturn(new PageImpl(Arrays.asList(createFilterList(), createFilterList())));
    ResponseEntity retVal =
        filterListDTOController.getFilterLists(
            BUYER_ID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    assertEquals(HttpStatus.OK, retVal.getStatusCode());
  }

  @Test
  void shouldGetMultipleFilterListsByFilterListType() {
    Pageable pageable = PageRequest.of(1, 2);
    when(filterListService.getFilterLists(
            BUYER_ID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(FilterListTypeDTO.DOMAIN),
            Optional.empty()))
        .thenReturn(new PageImpl(Arrays.asList(createFilterList(), createFilterList())));
    Page<FilterListDTO> retVal =
        filterListDTOController
            .getFilterLists(
                BUYER_ID,
                pageable,
                Optional.empty(),
                Optional.empty(),
                Optional.of(FilterListTypeDTO.DOMAIN),
                Optional.empty())
            .getBody();
    assertEquals(2, retVal.getTotalElements());
  }

  @Test
  void shouldDeleteFilterList() {
    filterListDTOController.deleteFilterList(BUYER_ID, FILTER_LIST_ID);
    verify(filterListService).deleteFilterList(BUYER_ID, FILTER_LIST_ID);
  }

  @Test
  void shouldReturnOKWhenDeleteFilterList() {
    assertEquals(
        HttpStatus.OK,
        filterListDTOController.deleteFilterList(BUYER_ID, FILTER_LIST_ID).getStatusCode());
  }

  @Test
  void shouldReturnDeletedFilterList() {
    FilterListDTO filterList = createFilterList();
    when(filterListService.deleteFilterList(BUYER_ID, FILTER_LIST_ID)).thenReturn(filterList);
    assertEquals(
        filterList, filterListDTOController.deleteFilterList(BUYER_ID, FILTER_LIST_ID).getBody());
  }

  @Test
  void shouldGetResponseStatusOkWhenAddDomains() {
    MultipartFile filterListCsv = new MockMultipartFile("Test", new byte[] {});
    ResponseEntity<FilterListDTO> responseEntity =
        filterListDTOController.addFilterListCsv(BUYER_ID, FILTER_LIST_ID, filterListCsv);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void shouldGetFilterListWhenAddFilterListCsvProvided() {
    MultipartFile filterListCsv = new MockMultipartFile("Test", new byte[] {});
    when(filterListService.addFilterListCsv(BUYER_ID, FILTER_LIST_ID, filterListCsv))
        .thenReturn(FilterListDTO.builder().build());
    ResponseEntity<FilterListDTO> responseEntity =
        filterListDTOController.addFilterListCsv(BUYER_ID, FILTER_LIST_ID, filterListCsv);

    assertNotNull(responseEntity.getBody());
  }

  private FilterListDTO createFilterList() {
    return FilterListDTO.builder().name("TEst").type(FilterListTypeDTO.DOMAIN).build();
  }

  private ArgumentMatcher<FilterListDTO> matchArg(Predicate<FilterListDTO> argMatches) {
    return o -> argMatches.test((FilterListDTO) o);
  }
}
