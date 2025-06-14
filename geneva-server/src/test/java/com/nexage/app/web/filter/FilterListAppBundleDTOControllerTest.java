package com.nexage.app.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.filter.FilterListAppBundle_;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import com.nexage.app.services.filter.FilterListAppBundleDTOService;
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
class FilterListAppBundleDTOControllerTest {

  @Mock private FilterListAppBundleDTOService filterListAppBundleDTOService;
  @InjectMocks private FilterListAppBundleDTOController filterListAppBundleDTOController;
  private final Random RANDOM = ThreadLocalRandom.current();
  private final long BUYER_ID = RANDOM.nextLong();
  private final int FILTER_LIST_ID = RANDOM.nextInt();

  @Test
  void shouldGetResponseStatusOkWhenGetAppBundles() {
    Pageable pageable = PageRequest.of(1, 2);
    assertEquals(
        HttpStatus.OK,
        filterListAppBundleDTOController
            .getFilterListAppBundles(BUYER_ID, FILTER_LIST_ID, pageable, null, null)
            .getStatusCode());
  }

  @Test
  void shouldGetPagedAppBundlesWhenGetFilterList() {
    Pageable pageable = PageRequest.of(1, 2);
    Page<FilterListAppBundleDTO> page = new PageImpl(Arrays.asList(new FilterListAppBundleDTO()));
    when(filterListAppBundleDTOService.getFilterListAppBundles(
            BUYER_ID, FILTER_LIST_ID, pageable, null, null))
        .thenReturn(page);
    ResponseEntity responseEntity =
        filterListAppBundleDTOController.getFilterListAppBundles(
            BUYER_ID, FILTER_LIST_ID, pageable, null, null);
    assertNotNull(responseEntity.getBody());
  }

  @Test
  void shouldReturnListOfDeletedFilterListAppBundleIds() {
    Set<Integer> filterListAppBundleIds = IntStream.of(1, 2, 3).boxed().collect(Collectors.toSet());
    when(filterListAppBundleDTOService.deleteFilterListAppBundles(
            BUYER_ID, FILTER_LIST_ID, filterListAppBundleIds))
        .thenReturn(new ArrayList(filterListAppBundleIds));
    assertEquals(
        filterListAppBundleIds,
        new HashSet(
            filterListAppBundleDTOController
                .deleteFilterListAppBundlePIDs(BUYER_ID, FILTER_LIST_ID, filterListAppBundleIds)
                .getBody()));
  }

  @Test
  void shouldReturnResponseEntityWithOkStatusWhenDeleteFilterListAppBundleIds() {
    Set<Integer> filterListAppBundleIds = IntStream.of(1, 2, 3).boxed().collect(Collectors.toSet());
    when(filterListAppBundleDTOService.deleteFilterListAppBundles(
            BUYER_ID, FILTER_LIST_ID, filterListAppBundleIds))
        .thenReturn(new ArrayList(filterListAppBundleIds));
    assertEquals(
        HttpStatus.OK,
        filterListAppBundleDTOController
            .deleteFilterListAppBundlePIDs(BUYER_ID, FILTER_LIST_ID, filterListAppBundleIds)
            .getStatusCode());
  }

  @Test
  void shouldReturnSearchParamInFilterListAppBundleIds() {
    String appBundleToCheck = "test";
    String appBundleData = "com.android.test";
    Pageable pageable = PageRequest.of(0, 10);
    FilterListAppBundleDTO filterListAppBundleDTO = new FilterListAppBundleDTO();
    filterListAppBundleDTO.setApp(appBundleData);
    Page<FilterListAppBundleDTO> page =
        new PageImpl<>(Arrays.asList(filterListAppBundleDTO), pageable, 1);
    ResponseEntity responseEntity =
        filterListAppBundleDTOController.getFilterListAppBundles(
            BUYER_ID,
            FILTER_LIST_ID,
            pageable,
            new HashSet(Arrays.asList(FilterListAppBundle_.APP)),
            appBundleToCheck);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(appBundleData, page.getContent().get(0).getApp());
  }
}
