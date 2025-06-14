package com.ssp.geneva.server.report.report.velocity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ssp.geneva.server.report.report.Report;
import com.ssp.geneva.server.report.report.ReportRequest;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.exceptions.ReportException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Log4j2
public abstract class AbstractVelocityReport<R extends ReportRequest, T extends ReportResponse>
    extends Report<R, T> {

  @Autowired
  @Qualifier("dwNamedJdbcTemplate")
  protected NamedParameterJdbcTemplate dwNamedJdbcTemplate;

  @Autowired
  @Qualifier("coreNamedJdbcTemplate")
  protected NamedParameterJdbcTemplate coreNamedJdbcTemplate;

  @Value("${report.maxRows:10000}")
  protected int maxRows;

  private static StringResourceRepository repo;
  private static ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

    try {
      Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
      Velocity.setProperty("string.resource.loader.cache", Boolean.TRUE.toString());
      Velocity.setProperty(
          "string.resource.loader.class",
          "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
      Velocity.setProperty(
          "string.resource.loader.repository.class",
          "com.ssp.geneva.server.report.report.velocity.CachedStringResourceRepository");
      Velocity.init();
    } catch (Exception e) {
      log.error("ERROR : Exception in static initializer", e);
    }
    repo = StringResourceLoader.getRepository();
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Object> getParameters(R request) {
    return mapper.convertValue(request, Map.class);
  }

  protected final List<T> getReportData(R request, String sql, RowMapper<T> rowMapper)
      throws ReportException, DataAccessException {
    if (dwNamedJdbcTemplate == null) throw new ReportException("JdbcTemplate not initialized");
    if (request == null) throw new ReportException("Missing ReportContext");
    if (sql == null) throw new ReportException("Missing report SQL");

    Map<String, Object> parameters = new HashMap<>();
    parameters.putAll(getParameters(request));
    parameters.put("reportUser", request.getReportUser());
    parameters.put("reportMetadata", request.getReportMetadata());

    VelocityContext velocityContext = new VelocityContext();
    velocityContext.put("StringUtils", StringUtils.class);
    velocityContext.put("request", request);

    Map<String, Object> paramMap = new HashMap<>();
    for (Entry<String, Object> param : parameters.entrySet()) {
      paramMap.put(param.getKey(), param.getValue());
      velocityContext.put(param.getKey(), param.getValue());
    }

    // add caching of the velocity template
    String trimmedSql = sql.trim();
    if (repo.getStringResource(trimmedSql) == null) {
      repo.putStringResource(trimmedSql, trimmedSql);
      log.debug("adding to cache: {}", trimmedSql);
    }

    StringWriter sqlOut = new StringWriter();
    Velocity.getTemplate(trimmedSql).merge(velocityContext, sqlOut);

    log.debug("report request : {}", request.toString());
    log.debug("executing query: {}", sqlOut.toString());

    long startTime = System.currentTimeMillis();
    log.debug("limiting max rows to: {}", maxRows);
    ((JdbcTemplate) dwNamedJdbcTemplate.getJdbcOperations()).setMaxRows(maxRows);
    List<T> results = dwNamedJdbcTemplate.query(sqlOut.toString(), paramMap, rowMapper);
    log.debug("result size: {}", results.size());
    log.debug("query time : {} ms", System.currentTimeMillis() - startTime);
    return results;
  }
}
