package com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.ssp.geneva.server.report.report.ReportMetadata;
import com.ssp.geneva.server.report.report.ReportUser;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
class FinanceMetricsBySiteReportIT {

  @Autowired FinanceMetricsBySiteReport financeMetricsBySiteReport;

  @Autowired private PhoneCastConfigService phoneCastConfigService;

  @Autowired
  @Qualifier("dwNamedJdbcTemplate")
  protected NamedParameterJdbcTemplate dwNamedJdbcTemplate;

  String now = LocalDate.now().toString();
  String tomorrow = LocalDate.now().plusDays(1).toString();

  @Test
  void shouldGetReportData() throws Exception {
    FinanceMetricsBySiteRequest request = new FinanceMetricsBySiteRequestImpl(now, tomorrow);
    List<FinanceMetricsBySiteResponse> responseList = new ArrayList<>();
    FinanceMetricsBySiteResponse response = buildResponse();
    responseList.add(response);

    JdbcTemplate template = Mockito.mock(JdbcTemplate.class);

    when(phoneCastConfigService.getExchangeIdsAsList()).thenReturn(List.of("2160"));
    when(dwNamedJdbcTemplate.getJdbcOperations()).thenReturn(template);
    when(dwNamedJdbcTemplate.query(
            any(String.class), any(Map.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(responseList);

    financeMetricsBySiteReport.getReportData(request);

    assertEquals(100.00, response.getFillRate(), 0.00);
    assertEquals(1, response.getAdDelivered());
    assertEquals("google", response.getHbPartnerName());
    assertEquals(0.00, response.getCtr(), 0.00);
  }

  @Test
  void shouldGetParameters() {
    FinanceMetricsBySiteRequest request = new FinanceMetricsBySiteRequestImpl(now, tomorrow);
    when(phoneCastConfigService.getExchangeIdsAsList()).thenReturn(List.of("2160", "1331", "1410"));

    Map<String, Object> params = financeMetricsBySiteReport.getParameters(request);

    assertEquals(List.of("2160", "1331", "1410"), params.get("exchangeIds"));
    assertEquals(now, params.get("start"));
    assertEquals(tomorrow, params.get("stop"));
  }

  private class FinanceMetricsBySiteRequestImpl implements FinanceMetricsBySiteRequest {

    private String start;
    private String stop;

    FinanceMetricsBySiteRequestImpl(String start, String stop) {
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
  }

  private FinanceMetricsBySiteResponse buildResponse() {

    FinanceMetricsBySiteResponse response = new FinanceMetricsBySiteResponse();
    response.setAdDelivered(1);
    response.setAdServed(1);
    response.setAdRequests(1);
    response.setHbPartnerName("google");
    response.setNetRevenue(0.07);
    response.setSite("TestSite");
    response.setPublisher("TestPublisher");

    return response;
  }
}
