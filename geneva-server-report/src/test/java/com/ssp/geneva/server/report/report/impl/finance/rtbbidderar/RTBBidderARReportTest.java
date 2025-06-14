package com.ssp.geneva.server.report.report.impl.finance.rtbbidderar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.repository.BidderConfigRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RTBBidderARReportTest {
  @Mock private BidderConfigRepository bidderConfigRepository;
  @InjectMocks private RTBBidderARReport rtbBidderARReport;

  @Test
  void shouldPostProcessResponse() {
    // given
    List<BidderConfig> bidderConfigs =
        List.of(
            createBidderConfig(1L, BigDecimal.valueOf(10)),
            createBidderConfig(2L, BigDecimal.valueOf(20)));
    given(bidderConfigRepository.findAllById(any())).willReturn(bidderConfigs);

    // when
    List<RTBBidderARResponse> result =
        rtbBidderARReport.postProcess(List.of(createResponse("1"), createResponse("2")));

    // then
    result.stream()
        .map(RTBBidderARResponse::getMinTrafficCharge)
        .forEach(Assertions::assertNotNull);
  }

  private BidderConfig createBidderConfig(Long id, BigDecimal cpm) {
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setPid(id);
    bidderConfig.setBidRequestCpm(cpm);
    return bidderConfig;
  }

  private RTBBidderARResponse createResponse(String id) {
    RTBBidderARResponse response = new RTBBidderARResponse();
    response.setBidderId(id);
    response.setBidRequests("10");
    response.setNetAcquisitionCost("10");
    response.setGrossWins("10");
    return response;
  }
}
