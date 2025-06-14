package com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.ssp.geneva.server.report.report.ReportMetadata;
import com.ssp.geneva.server.report.report.ReportUser;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
@ExtendWith(SpringExtension.class)
class RtbRevenuePerformanceReportIT {

  @Autowired private RtbRevenuePerformanceReport rtbRevenuePerformanceReport;
  @Autowired private DirectDealRepository directDealRepository;

  @Autowired private PhoneCastConfigService phoneCastConfigService;

  @Autowired
  @Qualifier("dwNamedJdbcTemplate")
  protected NamedParameterJdbcTemplate dwNamedJdbcTemplate;

  private final String now = LocalDate.now().toString();
  private final String tomorrow = LocalDate.now().plusDays(1).toString();
  private final String dealId = "test-deal-id";
  private final String dealDescription = "test-deal-description";

  @Test
  void shouldGetReportData() throws Exception {
    // given
    RtbRevenuePerformanceRequest request = new RtbRevenuePerformanceRequestImpl(now, tomorrow);
    List<RtbRevenuePerformanceResponse> responseList = new ArrayList<>();
    RtbRevenuePerformanceResponse response = buildResponse();
    responseList.add(response);

    JdbcTemplate template = Mockito.mock(JdbcTemplate.class);

    when(phoneCastConfigService.getExchangeIdsAsList()).thenReturn(List.of("2160"));
    when(dwNamedJdbcTemplate.getJdbcOperations()).thenReturn(template);
    when(dwNamedJdbcTemplate.query(
            any(String.class), any(Map.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(responseList);
    when(directDealRepository.findByDealId(anyString())).thenReturn(Optional.of(createDeal()));

    // when
    rtbRevenuePerformanceReport.getReportData(request);

    // then
    assertEquals("google", response.getS2sHbPartner());
    assertEquals("Google Inc.", response.getS2sHbPartnerName());
    assertEquals(123L, response.getS2sHbPartnerPid(), 0.0);
    assertEquals(20L, response.getAdDelivered(), 0.0);
    assertEquals(30L, response.getAdServed(), 0.0);
    assertEquals("TestSite", response.getSite());
    assertEquals(
        12D,
        response.getRevenue(),
        0.0,
        "Revenue not correct"); // totalRevenue - net_revenue or mmRevenue
    assertEquals(0D, response.getMmRevenue(), 0.0, "MMRevenue not correct");
    assertEquals(12D, response.getTotalRevenue(), 0.0, "TotalRevenue not correct");
    assertEquals(dealId, response.getDealID());
    assertEquals(dealDescription, response.getDeal());
  }

  private static class RtbRevenuePerformanceRequestImpl implements RtbRevenuePerformanceRequest {

    private final String start;
    private final String stop;

    RtbRevenuePerformanceRequestImpl(String start, String stop) {
      this.start = start;
      this.stop = stop;
    }

    @Override
    public String getStart() {
      return start;
    }

    public String getStop() {
      return stop;
    }

    @Override
    public ReportUser getReportUser() {
      return null;
    }

    @Override
    public ReportMetadata getReportMetadata() {
      return null;
    }

    @Override
    public RtbRevenuePerformanceDimension getDim() {
      return RtbRevenuePerformanceDimension.deal;
    }

    public void setDim(RtbRevenuePerformanceDimension dim) {}

    @Override
    public Long getSite() {
      return null;
    }

    public void setSite(Long site) {}

    @Override
    public Set<Long> getSiteIds() {
      return null;
    }

    public void setSiteIds(Set<Long> siteIds) {}

    @Override
    public String getPosition() {
      return null;
    }

    public void setPosition(String position) {}

    @Override
    public Long getTag() {
      return null;
    }

    public void setTag(Long tag) {}

    @Override
    public String getCountry() {
      return null;
    }

    public void setCountry(String country) {}

    @Override
    public Long getBidder() {
      return null;
    }

    public void setBidder(Long bidder) {}

    @Override
    public Long getBuyer() {
      return null;
    }

    public void setBuyer(Long buyer) {}

    @Override
    public String getAdomain() {
      return null;
    }

    public void setAdomain(String adomain) {}

    @Override
    public String getSeat() {
      return null;
    }

    public void setSeat(String seat) {}

    @Override
    public String getDeal() {
      return null;
    }

    public void setDeal(String deal) {}

    @Override
    public Long getCompany() {
      return null;
    }

    public void setCompany(Long company) {}

    @Override
    public boolean isDealAsNullParam() {
      return false;
    }

    public void setDealAsNullParam(boolean dealAsNullParam) {}

    @Override
    public Long getS2sHbPartner() {
      return null;
    }

    public void setS2sHbPartner(Long s2sHbPartner) {}
  }

  private RtbRevenuePerformanceResponse buildResponse() {
    RtbRevenuePerformanceResponse response = new RtbRevenuePerformanceResponse();
    response.setAdDelivered(20L);
    response.setAdServed(30L);
    response.setSite("TestSite");
    response.setTotalRevenue(12D);
    response.setS2sHbPartner("google");
    response.setS2sHbPartnerName("Google Inc.");
    response.setS2sHbPartnerPid(123L);
    response.setDealID(dealId);

    return response;
  }

  private DirectDeal createDeal() {
    DirectDeal deal = new DirectDeal();
    deal.setDealId(dealId);
    deal.setDescription(dealDescription);
    return deal;
  }
}
