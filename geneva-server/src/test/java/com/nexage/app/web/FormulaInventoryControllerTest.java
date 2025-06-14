package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.services.FormulaInventoryService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FormulaInventoryControllerTest {
  @InjectMocks private FormulaInventoryController formulaInventoryController;

  @Mock private FormulaInventoryService formulaInventoryService;

  @Test
  void shouldGetPlacementsByFormulaForDeals() {
    Integer total = 15;
    Integer count = 10;
    List<FormulaInventoryDTO> formulaInventoryDTOS =
        TestObjectsFactory.gimme(count, FormulaInventoryDTO.class);
    Pageable pageable = PageRequest.of(0, count);
    Page page = new PageImpl(formulaInventoryDTOS, pageable, total);
    PlacementFormulaDTO placementFormulaDTO = mock(PlacementFormulaDTO.class);
    when(formulaInventoryService.getPlacementsByFormulaForDeals(placementFormulaDTO, pageable))
        .thenReturn(page);

    var response =
        formulaInventoryController
            .fetchPagePlacementsByFormulaForDeals(placementFormulaDTO, false, pageable)
            .getBody();
    assertNotNull(response);
    assertEquals(
        response.getContent().get(0).getCompanyId(), formulaInventoryDTOS.get(0).getCompanyId());
    assertEquals(response.getTotalElements(), total.longValue());
    assertEquals(response.getNumberOfElements(), count.longValue());
    assertTrue(response.getPageable().isPaged());
  }

  @Test
  void shouldGetPlacementsByFormulaForDealsUnPaged() {
    Integer total = 15;
    List<FormulaInventoryDTO> formulaInventoryDTOS =
        TestObjectsFactory.gimme(total, FormulaInventoryDTO.class);
    Pageable pageable = PageRequest.of(0, total);
    Page page = new PageImpl(formulaInventoryDTOS);
    PlacementFormulaDTO placementFormulaDTO = mock(PlacementFormulaDTO.class);
    when(formulaInventoryService.getPlacementsByFormulaForDeals(
            placementFormulaDTO, Pageable.unpaged()))
        .thenReturn(page);

    var response =
        formulaInventoryController
            .fetchPagePlacementsByFormulaForDeals(placementFormulaDTO, true, pageable)
            .getBody();
    assertNotNull(response);
    assertEquals(
        response.getContent().get(0).getCompanyId(), formulaInventoryDTOS.get(0).getCompanyId());
    assertEquals(response.getTotalElements(), total.longValue());
    assertEquals(response.getNumberOfElements(), total.longValue());
    assertEquals(response.getPageable(), Pageable.unpaged());
  }

  @Test
  void shouldGetPlacementsByFormulaForPublisher() {
    Integer total = 15;
    Integer count = 10;
    Long publisherPid = 1L;
    List<FormulaInventoryDTO> formulaInventoryDTOS =
        TestObjectsFactory.gimme(count, FormulaInventoryDTO.class);
    Pageable pageable = PageRequest.of(0, count);
    Page page = new PageImpl(formulaInventoryDTOS, pageable, total);
    PlacementFormulaDTO placementFormulaDTO = mock(PlacementFormulaDTO.class);
    when(formulaInventoryService.getPlacementsByFormulaForPublisher(
            publisherPid, placementFormulaDTO, pageable))
        .thenReturn(page);

    var response =
        formulaInventoryController
            .fetchPlacementsByFormulaForPublisher(
                publisherPid, placementFormulaDTO, false, pageable)
            .getBody();
    assertNotNull(response);
    assertEquals(
        response.getContent().get(0).getCompanyId(), formulaInventoryDTOS.get(0).getCompanyId());
    assertEquals(response.getTotalElements(), total.longValue());
    assertEquals(response.getNumberOfElements(), count.longValue());
    assertTrue(response.getPageable().isPaged());
  }

  @Test
  void shouldGetPlacementsByFormulaForPublisherUnPaged() {
    Integer total = 15;
    Long publisherPid = 1L;
    List<FormulaInventoryDTO> formulaInventoryDTOS =
        TestObjectsFactory.gimme(total, FormulaInventoryDTO.class);
    Pageable pageable = PageRequest.of(0, total);
    Page page = new PageImpl(formulaInventoryDTOS);
    PlacementFormulaDTO placementFormulaDTO = mock(PlacementFormulaDTO.class);
    when(formulaInventoryService.getPlacementsByFormulaForPublisher(
            publisherPid, placementFormulaDTO, Pageable.unpaged()))
        .thenReturn(page);

    var response =
        formulaInventoryController
            .fetchPlacementsByFormulaForPublisher(publisherPid, placementFormulaDTO, true, pageable)
            .getBody();
    assertNotNull(response);
    assertEquals(
        response.getContent().get(0).getCompanyId(), formulaInventoryDTOS.get(0).getCompanyId());
    assertEquals(response.getTotalElements(), total.longValue());
    assertEquals(response.getNumberOfElements(), total.longValue());
    assertEquals(response.getPageable(), Pageable.unpaged());
  }
}
