package com.nexage.app.services.impl;

import com.nexage.admin.core.pubselfserve.CompanyPubSelfServeView;
import com.nexage.admin.core.repository.CompanyPubSelfServeViewRepository;
import com.nexage.admin.dw.util.DateUtil;
import com.nexage.admin.dw.util.ReportDefEnums.Interval;
import com.nexage.app.dto.publisher.PublisherMetricsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PubSelfServeMetricsService;
import com.nexage.app.services.impl.queries.DWQueries;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
@Service("publisherMetricsService")
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
public class PubSelfServeMetricsServiceImpl implements PubSelfServeMetricsService {

  private static final int DEFAULT_SCALING = 8;

  private final JdbcTemplate jdbcTemplate;
  private final CompanyPubSelfServeViewRepository companyPubSelfServeViewRepository;
  private final DWQueries queries;
  private final UserContext userContext;

  public PubSelfServeMetricsServiceImpl(
      @Qualifier("dwJdbcTemplate") JdbcTemplate jdbcTemplate,
      CompanyPubSelfServeViewRepository companyPubSelfServeViewRepository,
      DWQueries queries,
      UserContext userContext) {
    this.jdbcTemplate = jdbcTemplate;
    this.companyPubSelfServeViewRepository = companyPubSelfServeViewRepository;
    this.queries = queries;
    this.userContext = userContext;
  }

  @Override
  public PublisherMetricsDTO getMetrics(
      Long pubPid, String isoStartDate, String isoEndDate, String intervalString) {
    validatePublisher(pubPid);
    Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());

    Interval interval = Interval.DAILY; // set default
    if (StringUtils.isNotEmpty(intervalString)) {
      interval = Interval.getInterval(intervalString);
    }

    String startDate = parseIsoDate(isoStartDate);
    String endDate = parseIsoDate(isoEndDate);

    StringBuilder query = queries.buildSQLforMetrics(interval);
    PublisherMetricsDTO metrics = new PublisherMetricsDTO(interval);

    try (PreparedStatement pstmt = con.prepareStatement(query.toString())) {
      pstmt.setLong(1, pubPid);
      pstmt.setString(2, startDate);
      pstmt.setString(3, endDate);
      ResultSet rowSet = pstmt.executeQuery();
      metrics = buildResults(interval, startDate, endDate, rowSet);
    } catch (Exception e) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_PUBLISHER_METRICS_FAILED);
    }

    return metrics;
  }

  @Override
  public PublisherMetricsDTO getAdSourceMetrics(
      Long pubPid,
      Long adsourcePid,
      Long sitePid,
      String position,
      Long tagPid,
      String isoStartDate,
      String isoEndDate,
      String intervalString) {
    Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
    validatePublisher(pubPid);

    if (null == adsourcePid) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    Interval interval = Interval.DAILY; // set default
    if (StringUtils.isNotEmpty(intervalString)) {
      interval = Interval.getInterval(intervalString);
    }

    String startDate = parseIsoDate(isoStartDate);
    String endDate = parseIsoDate(isoEndDate);

    StringBuilder query = queries.buildSQLforAdSourceMetrics(interval);
    PublisherMetricsDTO metrics = new PublisherMetricsDTO(interval);
    sitePid = sitePid != null ? sitePid : 0L;
    tagPid = tagPid != null ? tagPid : 0L;

    try (PreparedStatement pstmt = con.prepareStatement(query.toString())) {
      pstmt.setLong(1, pubPid);
      pstmt.setLong(2, adsourcePid);
      pstmt.setLong(3, sitePid);
      pstmt.setLong(4, sitePid);
      pstmt.setString(5, position);
      pstmt.setString(6, position);
      pstmt.setLong(7, tagPid);
      pstmt.setLong(8, tagPid);
      pstmt.setString(9, startDate);
      pstmt.setString(10, endDate);

      ResultSet rowSet = pstmt.executeQuery();
      metrics = buildResults(interval, startDate, endDate, rowSet);
    } catch (Exception e) {
      throw new GenevaAppRuntimeException(
          ServerErrorCodes.SERVER_PUBLISHER_ADSOURCE_METRICS_FAILED);
    }
    return metrics;
  }

  private void validatePublisher(Long pubPid) {
    if (null == pubPid) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    checkPrivileges(pubPid);
  }

  private PublisherMetricsDTO buildResults(
      Interval interval, String startDate, String endDate, ResultSet rowset) {
    PublisherMetricsDTO metrics = new PublisherMetricsDTO(interval);
    try {
      while (rowset.next()) {
        Date date = rowset.getDate("ts");
        String dateString = getKeyForDate(date, interval);
        BigDecimal requests = rowset.getBigDecimal("requests");
        BigDecimal served = rowset.getBigDecimal("served");
        BigDecimal delivered = rowset.getBigDecimal("delivered");
        BigDecimal clicks = rowset.getBigDecimal("clicks");
        BigDecimal revenue = rowset.getBigDecimal("revenue");
        BigDecimal fillrate = rowset.getBigDecimal("fillrate");
        BigDecimal ctr = rowset.getBigDecimal("ctr").setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal ecpm =
            rowset.getBigDecimal("ecpm").setScale(DEFAULT_SCALING, BigDecimal.ROUND_CEILING);
        BigDecimal rpm =
            rowset.getBigDecimal("rpm").setScale(DEFAULT_SCALING, BigDecimal.ROUND_CEILING);

        metrics.addData(
            dateString, requests, served, delivered, clicks, revenue, fillrate, ctr, rpm, ecpm);
      }
    } catch (Exception e) {
      log.error("exception accessing core data: {}", e.getMessage());
    }

    Map<String, String> all = DateUtil.getDatesBetweenForInterval(startDate, endDate, interval);
    for (Entry<String, String> checkIfMissed : all.entrySet()) {
      if (!metrics.hasDate(checkIfMissed.getKey())) {
        try {
          metrics.addNullData(checkIfMissed.getKey());
        } catch (Exception e) {
          log.warn("could not parse data string: {}", checkIfMissed.getKey());
        }
      }
    }
    return metrics;
  }

  protected String getKeyForDate(Date date, Interval interval) {
    Calendar cal = Calendar.getInstance();
    String key = null;
    cal.setTime(date);
    switch (interval) {
      case DAILY:
        key = DateUtil.format(cal.getTime(), DateUtil.DATE_FORMAT_STRING);
        break;
      case WEEKLY:
        key =
            new StringBuilder(String.valueOf(cal.get(Calendar.YEAR)))
                .append("-")
                .append(String.valueOf(cal.get(Calendar.WEEK_OF_YEAR) - 1))
                .toString();
        break;
      case MONTHLY:
        key = DateUtil.format(cal.getTime(), DateUtil.MONTH_FORMAT_STRING);
        break;
      default:
        break;
    }
    return key;
  }

  private String parseIsoDate(String isoDateString) {
    Date date = null;
    try {
      date = ISO8601Util.parse(isoDateString);
    } catch (ParseException e) {
      log.error("Error Parsing start/end dates : {}", e.getMessage());
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    return DateUtil.format(date, DateUtil.DATETIME_FORMAT_STRING);
  }

  private void checkPrivileges(long pubId) {
    CompanyPubSelfServeView pssCompany =
        companyPubSelfServeViewRepository
            .findById(pubId)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));

    if (!userContext.isPublisherSelfServeEnabled(pubId)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    } else if (!pssCompany.isSelfServeAllowed() && !userContext.isNexageUser()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PSS_NOT_ENABLED);
    }
  }
}
