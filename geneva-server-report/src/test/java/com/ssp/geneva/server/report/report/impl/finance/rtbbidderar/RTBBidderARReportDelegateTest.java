package com.ssp.geneva.server.report.report.impl.finance.rtbbidderar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.ExchangeConfig;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.ExchangeConfigRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RTBBidderARReportDelegateTest {
  public static final String AUCTION_BID_REQUEST_CPM = "auction.bid.request.cpm";
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private ExchangeConfigRepository exchangeConfigRepository;
  @InjectMocks private RTBBidderARReport rtbBidderARReport;
  @Mock private RTBBidderARReportDelegate rtbBidderARReportDelegate;

  @Test
  void shouldReturnIntegerValueWhenPropertyExistsAndIsString() {
    // given
    List<BidderConfig> bidderConfigs =
        List.of(
            createBidderConfig(1L, BigDecimal.valueOf(10)),
            createBidderConfig(2L, BigDecimal.valueOf(20)));
    given(bidderConfigRepository.findAllById(any())).willReturn(bidderConfigs);
    var property =
        RTBBidderARReport.RTBBidderARReportProperty.AUCTION_BID_REQUEST_CPM_PROPERTY
            .getPropertyName();
    var config = mock(ExchangeConfig.class);
    when(config.getProperty()).thenReturn(AUCTION_BID_REQUEST_CPM);
    when(exchangeConfigRepository.findByProperty(property)).thenReturn(config);

    // when
    List<RTBBidderARResponse> result =
        rtbBidderARReport.postProcess(List.of(createResponse("3"), createResponse("2")));

    String returnedValue =
        exchangeConfigRepository.findByProperty(AUCTION_BID_REQUEST_CPM).getProperty();

    // then
    assertEquals(AUCTION_BID_REQUEST_CPM, returnedValue);
  }

  private RTBBidderARResponse createResponse(String id) {
    RTBBidderARResponse response = new RTBBidderARResponse();
    response.setBidderId(id);
    response.setBidRequests("10");
    response.setNetAcquisitionCost("10");
    response.setGrossWins("10");
    return response;
  }

  private BidderConfig createBidderConfig(Long id, BigDecimal cpm) {
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setPid(id);
    bidderConfig.setBidRequestCpm(cpm);
    return bidderConfig;
  }
}
