package com.ssp.geneva.server.report.performance.pss.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@ExtendWith(MockitoExtension.class)
class BiddersPerformancePubSelfServeDaoImplTest {

  @Mock
  @Qualifier("dwNamedJdbcTemplate")
  private NamedParameterJdbcTemplate dwNamedTemplate;

  @InjectMocks private BiddersPerformancePubSelfServeDaoImpl biddersPerformancePubSelfServeDao;

  @Test
  void shouldReturnEmptyBiddersPerformanceForPubSelfServeListWhenAnyCostValuesNotFound() {
    // When no cost value is passed
    SqlRowSet sqlRowSet = getMockedSqlRowSet();
    List<BiddersPerformanceForPubSelfServe> biddersPerformanceForPubSelfServeList =
        biddersPerformancePubSelfServeDao.getBiddersPerformancePss("1", "1", Set.of(1L), "admin");
    assertNotNull(biddersPerformanceForPubSelfServeList);
    assertEquals(0, biddersPerformanceForPubSelfServeList.size());

    // When only gross acquisition cost is passed
    sqlRowSet = getMockedSqlRowSet();
    Mockito.when(sqlRowSet.getBigDecimal("grossAcquisitionCost")).thenReturn(BigDecimal.ONE);
    biddersPerformanceForPubSelfServeList =
        biddersPerformancePubSelfServeDao.getBiddersPerformancePss("1", "1", Set.of(1L), "admin");
    assertNotNull(biddersPerformanceForPubSelfServeList);
    assertEquals(0, biddersPerformanceForPubSelfServeList.size());

    // When only net acquisition cost is passed
    sqlRowSet = getMockedSqlRowSet();
    Mockito.when(sqlRowSet.getBigDecimal("grossAcquisitionCost")).thenReturn(null);
    Mockito.when(sqlRowSet.getBigDecimal("netAcquisitionCost")).thenReturn(BigDecimal.ONE);
    biddersPerformanceForPubSelfServeList =
        biddersPerformancePubSelfServeDao.getBiddersPerformancePss("1", "1", Set.of(1L), "admin");
    assertNotNull(biddersPerformanceForPubSelfServeList);
    assertEquals(0, biddersPerformanceForPubSelfServeList.size());
  }

  @Test
  void
      shouldReturnBiddersPerformanceForPubSelfServeListWhenGetBiddersPerformancePssWithCostValues() {
    SqlRowSet sqlRowSet = getMockedSqlRowSet();
    Mockito.when(sqlRowSet.getBigDecimal("grossAcquisitionCost")).thenReturn(BigDecimal.ONE);
    Mockito.when(sqlRowSet.getBigDecimal("netAcquisitionCost")).thenReturn(BigDecimal.ONE);

    List<BiddersPerformanceForPubSelfServe> biddersPerformanceForPubSelfServeList =
        biddersPerformancePubSelfServeDao.getBiddersPerformancePss("1", "1", Set.of(1L), "admin");

    assertNotNull(biddersPerformanceForPubSelfServeList);
    assertEquals(1, biddersPerformanceForPubSelfServeList.size());
  }

  private SqlRowSet getMockedSqlRowSet() {
    SqlRowSet sqlRowSet = Mockito.mock(SqlRowSet.class);
    Mockito.when(dwNamedTemplate.queryForRowSet(anyString(), any(SqlParameterSource.class)))
        .thenReturn(sqlRowSet);
    Mockito.when(sqlRowSet.next()).thenReturn(true).thenReturn(false);
    return sqlRowSet;
  }
}
