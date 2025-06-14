package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.app.services.BuyerService;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BuyerControllerIT extends BaseControllerItTest {

  @Mock private BuyerService buyerService;
  @InjectMocks private BuyerController buyerController;

  @BeforeEach
  void setUp() throws Exception {
    this.mockMvc = MockMvcBuilders.standaloneSetup(buyerController).build();
  }

  @Test
  void shouldContainAllowConnectIdParams() throws Exception {
    // given
    BidderConfig bidderConfig1 = TestObjectsFactory.createBidderConfig();
    bidderConfig1.setAllowConnectId(true);
    BidderConfig bidderConfig2 = TestObjectsFactory.createBidderConfig();
    bidderConfig2.setAllowConnectId(false);
    Long buyerPid = 1L;

    // when
    when(buyerService.getAllBidderConfigsByCompanyPid(buyerPid))
        .thenReturn(List.of(bidderConfig1, bidderConfig2));

    // then
    mockMvc
        .perform(get("/buyers/{buyerPID}/bidderconfigs", buyerPid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].allowConnectId", is(List.of(true, false))));
  }

  @Test
  void testGetBidderConfigsToContainAllowLiverampParams() throws Exception {
    BidderConfig bidderConfig1 = TestObjectsFactory.createBidderConfig();
    bidderConfig1.setAllowLiveramp(true);
    BidderConfig bidderConfig2 = TestObjectsFactory.createBidderConfig();
    bidderConfig2.setAllowLiveramp(false);
    Long buyerPid = 1L;

    when(buyerService.getAllBidderConfigsByCompanyPid(buyerPid))
        .thenReturn(List.of(bidderConfig1, bidderConfig2));

    mockMvc
        .perform(get("/buyers/{buyerPID}/bidderconfigs", buyerPid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].allowLiveramp", is(List.of(true, false))));
  }

  @Test
  void testGetBidderConfigsToContainAllowIdGraphMatchParams() throws Exception {
    BidderConfig bidderConfig1 = TestObjectsFactory.createBidderConfig();
    bidderConfig1.setAllowIdGraphMatch(true);
    BidderConfig bidderConfig2 = TestObjectsFactory.createBidderConfig();
    bidderConfig2.setAllowIdGraphMatch(false);
    Long buyerPid = 1L;

    when(buyerService.getAllBidderConfigsByCompanyPid(buyerPid))
        .thenReturn(List.of(bidderConfig1, bidderConfig2));

    mockMvc
        .perform(get("/buyers/{buyerPID}/bidderconfigs", buyerPid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].allowIdGraphMatch", is(List.of(true, false))));
  }
}
