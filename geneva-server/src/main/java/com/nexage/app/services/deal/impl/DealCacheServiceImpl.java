package com.nexage.app.services.deal.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nexage.app.services.deal.DealCacheService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

/**
 * This class was originally stored within geneva-sdk-dwdb wrongly. It has been moved closer to its
 * consumption because it uses only coredb. This code shouldn't use the template but specific
 * repositories. Please do not follow this approach at all cost.
 */
@Log4j2
@Service
public class DealCacheServiceImpl implements DealCacheService {

  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private static final String query = "SELECT description, visibility FROM deal WHERE id = :guid";
  private static final String getAllDeals = "SELECT id, description, visibility FROM deal";
  private LoadingCache<String, Deal> dealCache;
  private ScheduledFuture<?> handler;
  private final NamedParameterJdbcTemplate coreNamedJdbcTemplate;
  private final JdbcTemplate coreTemplate;

  @Autowired
  public DealCacheServiceImpl(
      NamedParameterJdbcTemplate coreNamedJdbcTemplate, JdbcTemplate coreServicesJdbcTemplate) {
    this.coreNamedJdbcTemplate = coreNamedJdbcTemplate;
    this.coreTemplate = coreServicesJdbcTemplate;
  }

  @PostConstruct
  void init() {
    dealCache = CacheBuilder.newBuilder().maximumSize(1000).build(new Loader());
    loadAllDeals();

    final Runnable reaper =
        () -> {
          log.info("Refreshing deal description cache");
          loadAllDeals();
        };
    LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
    Long fromNowToMidnightInterval =
        now.until(
            LocalDate.now(ZoneId.of("America/New_York")).plusDays(1).atStartOfDay(),
            ChronoUnit.MINUTES);
    handler =
        scheduler.scheduleAtFixedRate(
            reaper,
            fromNowToMidnightInterval,
            1440,
            TimeUnit.MINUTES); // schedule to run at every midnight
    log.info(
        "Cache refresh will happen at: {}",
        now.plus(fromNowToMidnightInterval, ChronoUnit.MINUTES));
  }

  @PreDestroy
  void destroy() {
    if (handler != null) {
      handler.cancel(true);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription(String id) {
    String description = "";
    id = id.trim();
    try {
      if (StringUtils.isNotEmpty(id) && dealCache.get(id) != null) {
        description = dealCache.get(id).getDescription();
      }
    } catch (ExecutionException e) {
      log.error("Error in getting deal description for id{}: {}", id, e);
    } catch (Exception e) {
      log.error("Could be due to invalid deal id {} : {}", id, e);
    }
    log.debug(
        "getDealDescription received deal id: {} and its description is : {}", id, description);
    return description;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getVisibility(String id) {
    boolean flag = false;
    id = id.trim();
    try {
      if (StringUtils.isNotEmpty(id) && dealCache.get(id) != null) {
        flag = dealCache.get(id).getVisibility();
      }
    } catch (ExecutionException e) {
      log.error("Error in getting deal visibility for id{}: {}", id, e);
    } catch (Exception e) {
      log.error("Could be due to invalid deal id {} : {}", id, e);
    }
    log.debug("getVisibility received deal id: {} and its visiblity is : {}", id, flag);
    return flag;
  }

  /** {@inheritDoc} */
  @Override
  public void removeDeal(String id) {
    log.info("deal id :{} got invalidated", id);
    dealCache.invalidate(id);
  }

  /** {@inheritDoc} */
  @Override
  public boolean refreshCache() {
    return loadAllDeals();
  }

  private boolean loadAllDeals() {
    boolean status = false;
    SqlRowSet rs = coreTemplate.queryForRowSet(getAllDeals);
    while (rs != null && rs.next()) {
      Deal d = new Deal(rs.getString(2), rs.getBoolean(3));
      dealCache.put(rs.getString(1), d);
      log.debug(" loading deal: {} ", d.toString());
      status = true;
    }
    log.info("dealCache size: {}", dealCache.size());
    return status;
  }

  private Deal getDeal(String id) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("guid", id);
    SqlRowSet rs = coreNamedJdbcTemplate.queryForRowSet(query, paramMap);
    if (rs != null && rs.next()) {
      Deal d = new Deal(rs.getString(1), rs.getBoolean(2));
      log.debug("deal id: {} and its data {}", id, d.toString());
      return d;
    }
    log.warn("deal id: {} and its description is empty", id);
    return null;
  }

  final class Loader extends CacheLoader<String, Deal> {

    @Override
    public Deal load(String key) throws Exception {
      return getDeal(key);
    }
  }

  static final class Deal {
    private final String description;
    private final boolean visibility;

    Deal(String desc, boolean flag) {
      this.description = desc;
      this.visibility = flag;
    }

    public String getDescription() {
      return description;
    }

    public boolean getVisibility() {
      return visibility;
    }

    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
          .append("desc:", description)
          .append("visibility:", visibility)
          .toString();
    }
  }
}
