package com.nexage.app.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.filter.FilterListDomainDTO;
import com.nexage.app.services.filter.FilterListDomainDTOService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FilterListDomainDTOControllerTest {

  @Mock private FilterListDomainDTOService filterListService;
  @InjectMocks private FilterListDomainDTOController filterListDomainDTOController;
  private final Random RANDOM = ThreadLocalRandom.current();
  private final long BUYER_ID = RANDOM.nextLong();
  private final int FILTER_LIST_ID = RANDOM.nextInt();

  @Test
  void shouldGetResponseStatusOkWhenGetDomains() {
    Pageable pageable = PageRequest.of(1, 2);
    assertEquals(
        HttpStatus.OK,
        filterListDomainDTOController
            .getFilterListDomains(BUYER_ID, FILTER_LIST_ID, pageable, null, null)
            .getStatusCode());
  }

  @Test
  void shouldGetPagedDomainsWhenGetDomains() {
    Pageable pageable = PageRequest.of(1, 2);
    Page<FilterListDomainDTO> page = new PageImpl(Arrays.asList(new FilterListDomainDTO()));
    when(filterListService.getFilterListDomains(BUYER_ID, FILTER_LIST_ID, pageable, null, null))
        .thenReturn(page);
    ResponseEntity responseEntity =
        filterListDomainDTOController.getFilterListDomains(
            BUYER_ID, FILTER_LIST_ID, pageable, null, null);
    assertNotNull(responseEntity.getBody());
  }

  @Test
  void shouldReturnListOfDeletedFilterListDomainIds() {
    Set<Integer> filterListDomainIds = IntStream.of(1, 2, 3).boxed().collect(Collectors.toSet());
    when(filterListService.deleteFilterListDomains(BUYER_ID, FILTER_LIST_ID, filterListDomainIds))
        .thenReturn(new ArrayList(filterListDomainIds));
    assertEquals(
        filterListDomainIds,
        new HashSet(
            filterListDomainDTOController
                .deleteFilterListDomainPIDs(BUYER_ID, FILTER_LIST_ID, filterListDomainIds)
                .getBody()));
  }

  @Test
  void shouldReturnResponseEntityWithOkStatusWhenDeleteFilterListDomainIds() {
    Set<Integer> filterListDomainIds = IntStream.of(1, 2, 3).boxed().collect(Collectors.toSet());
    when(filterListService.deleteFilterListDomains(BUYER_ID, FILTER_LIST_ID, filterListDomainIds))
        .thenReturn(new ArrayList(filterListDomainIds));
    assertEquals(
        HttpStatus.OK,
        filterListDomainDTOController
            .deleteFilterListDomainPIDs(BUYER_ID, FILTER_LIST_ID, filterListDomainIds)
            .getStatusCode());
  }
}
