package com.nexage.app.services.impl;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.sparta.jpa.model.DealView;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DealDTOMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class DealDTOServiceImpTest {

  @Mock DirectDealRepository directDealRepository;
  @Mock Pageable pageable;
  @InjectMocks DealDTOServiceImpl dealService;

  Page<DirectDeal> pagedEntity;
  Page<DealDTO> pagedDealDto;

  @BeforeEach
  void setup() {
    pagedEntity = new PageImpl(TestObjectsFactory.gimme(10, DirectDeal.class));
    pagedDealDto = pagedEntity.map(deal -> DealDTOMapper.MAPPER.map(deal));
  }

  @Test
  void testFindAll() {
    when(directDealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    Page<DealDTO> returnedPage = dealService.findAll("", null, pageable);

    assertEquals(
        pagedDealDto.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
    assertEquals(pagedDealDto.getTotalElements(), returnedPage.getTotalElements());
  }

  @Test
  void findAllByMultipleParams() {
    var qf = new LinkedHashSet<String>();
    qf.add("{all=true");
    qf.add("dealId=12345}");
    when(directDealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    var out = dealService.findAll(null, qf, pageable);
    assertEquals(pagedDealDto.getContent().get(0).getPid(), out.getContent().get(0).getPid());
    assertEquals(pagedDealDto.getTotalElements(), out.getTotalElements());
  }

  @ParameterizedTest
  @ValueSource(strings = {"{dealId=12345,dealCategory=SSP}", "{dealCategory=SSP}"})
  void shouldFindAllForDifferentSearchParamCombinations(String searchParams) {
    var qf = new LinkedHashSet<String>();
    qf.add(searchParams);
    when(directDealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    var out = dealService.findAll(null, qf, pageable);
    assertEquals(pagedDealDto.getContent().get(0).getPid(), out.getContent().get(0).getPid());
    assertEquals(pagedDealDto.getTotalElements(), out.getTotalElements());
  }

  @Test
  void findByInvalidDealCategoryShouldThrowException() {
    var qf = new LinkedHashSet<String>();
    qf.add("{dealCategory=Multi}");
    assertThrows(GenevaValidationException.class, () -> dealService.findAll("", qf, pageable));
  }

  @Test
  void testFindAllWithDescriptionCriteria() {
    when(directDealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    Page<DealDTO> returnedPage = dealService.findAll("", singleton("description"), pageable);

    assertEquals(
        pagedDealDto.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
    assertEquals(pagedDealDto.getTotalElements(), returnedPage.getTotalElements());
  }

  @Test
  void testFindAllWithTierCriteria() {
    when(directDealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    Page<DealDTO> returnedPage = dealService.findAll("OPEN", singleton("priorityType"), pageable);

    assertEquals(
        pagedDealDto.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
    assertEquals(pagedDealDto.getTotalElements(), returnedPage.getTotalElements());
  }

  @Test
  void testFindAllWithTierCriteria_shouldThrowOnInvalidTier() {
    var qf = singleton("potato");
    assertThrows(GenevaValidationException.class, () -> dealService.findAll("", qf, pageable));
  }

  @Test
  void findOneTest() {
    DealView mock = createDirectDealView();
    DealDTO dealDetailDTO = DealDTO.builder().pid(1L).dealId("Test").build();
    when(directDealRepository.findByPid(any())).thenReturn(Optional.of(mock));
    Optional<DealView> result = directDealRepository.findByPid(1L);
    assertEquals(result.get().getPid(), mock.getPid());
    assertEquals(result.get().getDealId(), mock.getDealId());
  }

  @Test
  void findOneTestEmpty() {
    when(directDealRepository.findByPid(any())).thenReturn(Optional.empty());
    var exception = assertThrows(GenevaValidationException.class, () -> dealService.findOne(1L));
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldGetValidDealsWithDealIdAndZeroDeals() {
    MultiValueMap<String, String> mVMap = new LinkedMultiValueMap<>();
    mVMap.add("sellers", "1,2,3");
    mVMap.add("dspBuyerSeats", "123");
    mVMap.add("dealId", "234");
    MultiValueQueryParams inputParams = new MultiValueQueryParams(mVMap, SearchQueryOperator.AND);

    Page<DirectDeal> dealPage = new PageImpl<>(List.of(new DirectDeal(), new DirectDeal()));

    Map<String, List<String>> paramMap = new HashMap<>();
    paramMap.put("dealId", mVMap.getOrDefault("dealId", null));

    when(directDealRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    var deals = dealService.getDeals(inputParams, PageRequest.of(0, 100));
    assertEquals(0, deals.getTotalElements());
  }

  private DealView createDirectDealView() {
    var view = mock(DealView.class);
    when(view.getPid()).thenReturn(1L);
    when(view.getDealId()).thenReturn("test");
    return view;
  }
}
