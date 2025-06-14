package com.nexage.app.web.deal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.deals.DealPlacementDTO;
import com.nexage.app.dto.deals.DealSellerDTO;
import com.nexage.app.dto.deals.DealSiteDTO;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.services.deal.DealSpecificAssignedInventoryService;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class DealBulkInventoriesProcessControllerIT extends BaseControllerItTest {

  private MockMvc mockMvc;

  @InjectMocks private DealBulkInventoriesProcessController controller;
  @Mock private DealSpecificAssignedInventoryService service;

  @BeforeEach
  public void setUp() {

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldSuccesOnValidInputFile() throws Throwable {
    MockMultipartFile inventoriesFile =
        new MockMultipartFile(
            "inventoriesFile", "Seller Id,App/Site Id,Placement Id\n1,11,111".getBytes());

    SpecificAssignedInventoryDTO mockSpecificInventoryDto = new SpecificAssignedInventoryDTO();
    var placement = new DealPlacementDTO();
    placement.setPlacementPid(111L);
    placement.setPlacementName("placement");
    placement.setPlacementMemo("memo");

    var site = new DealSiteDTO();
    site.setSitePid(11L);
    site.setSiteName("site name");
    site.setPlacements(List.of(placement));

    var seller = new DealSellerDTO();
    seller.setSellerPid(1L);
    seller.setSellerName("seller name");
    seller.setSites(List.of(site));

    List<DealSellerDTO> sellers = new ArrayList<>();
    sellers.add(seller);
    mockSpecificInventoryDto.setContent(sellers);

    when(service.processBulkInventories(inventoriesFile)).thenReturn(mockSpecificInventoryDto);

    mockMvc
        .perform(multipart("/v1/deals/bulk-inventories").file(inventoriesFile))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturn400StatusOnBadRequest() throws Throwable {
    MockMultipartFile inventoriesFile = new MockMultipartFile("abc", "1,2".getBytes());

    mockMvc
        .perform(multipart("/v1/deals/bulk-inventories").file(inventoriesFile))
        .andExpect(status().isBadRequest());
  }
}
