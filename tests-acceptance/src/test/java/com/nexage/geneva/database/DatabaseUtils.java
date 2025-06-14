package com.nexage.geneva.database;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.config.TestConfiguration;
import com.nexage.geneva.model.crud.BidderConfig;
import com.nexage.geneva.model.crud.Company;
import com.nexage.geneva.model.crud.DealTerm;
import com.nexage.geneva.model.crud.FactCowboyExchange;
import com.nexage.geneva.model.crud.FactCowboyExchangeDeal;
import com.nexage.geneva.model.crud.FactCowboyTraffic;
import com.nexage.geneva.model.crud.InventoryAttribute;
import com.nexage.geneva.model.crud.PositionNonPss;
import com.nexage.geneva.model.crud.PositionPss;
import com.nexage.geneva.model.crud.RevenueShare;
import com.nexage.geneva.model.crud.RtbProfile;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.model.crud.Tag;
import com.nexage.geneva.model.crud.TagArchiveVertica;
import com.nexage.geneva.model.crud.TagRule;
import com.nexage.geneva.model.crud.Tier;
import com.nexage.geneva.util.geneva.CompanyType;
import io.cucumber.datatable.DataTable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class DatabaseUtils {

  @Value("${crud.dw.db.driver}")
  private String crudDwDriver;

  @Autowired
  @Qualifier("dbCoreJdbcTemplate")
  private JdbcTemplate jdbcTemplateDbCore;

  @Autowired
  @Qualifier("crudDatawarehouseJdbcTemplate")
  private JdbcTemplate jdbcTemplateCrudDatawarehouse;

  @Autowired TestConfiguration testConfiguration;

  private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  private static final String VERTICA_DRIVER = "com.vertica.jdbc.Driver";

  public Map<String, Object> getTagPerformance(int tagId) throws SQLException {
    String sql =
        String.format(
            "SELECT d.id, d.name\n"
                + "    , coalesce(sum(ads_requested_adnet), 0) as requests\n"
                + "    , coalesce(sum(ads_served),0) as served\n"
                + "    , coalesce(sum(ads_delivered),0) as delivered \n"
                + "    , coalesce(sum(ads_clicked),0) as clicked\n"
                + "    , coalesce(sum(revenue),0) as revenue \n"
                + "    , coalesce(sum(revenue_net),0) as netRevenue \n"
                + "FROM dim_tag d \n"
                + "LEFT OUTER JOIN fact_revenue_adnet f \n"
                + "    ON  f.tag_id = d.id \n"
                + "        AND start >= now() - interval '7' day\n"
                + "        AND start < now()\n"
                + "WHERE d.id IN (%d) \n"
                + "GROUP BY d.id, d.name;",
            tagId);

    Map<String, Object> dbresult = jdbcTemplateCrudDatawarehouse.queryForMap(sql);

    Map<String, Object> result = new HashMap<>();

    result.put("name", dbresult.get("name"));

    if (crudDwDriver.equals(MYSQL_DRIVER)) {
      // Retrieve data by casting to Bigdecimal
      result.put("requests", ((BigDecimal) dbresult.get("requests")).intValue());
      result.put("served", ((BigDecimal) dbresult.get("served")).intValue());
      result.put("delivered", ((BigDecimal) dbresult.get("delivered")).intValue());
      result.put("clicked", ((BigDecimal) dbresult.get("clicked")).intValue());
      result.put("netRevenue", ((BigDecimal) dbresult.get("netRevenue")).doubleValue());

    } else if (crudDwDriver.equals(VERTICA_DRIVER)) {

      result.put("requests", dbresult.get("requests"));
      result.put("served", dbresult.get("served"));
      result.put("delivered", dbresult.get("delivered"));
      result.put("clicked", dbresult.get("clicked"));
      result.put("netRevenue", dbresult.get("netRevenue"));
    }
    return result;
  }

  public PositionPss getPositionPssByPid(String pid) {
    String sqlQuery =
        "SELECT "
            + "pid, name, site_pid, is_default, is_interstitial, version, mraid_support,"
            + " video_support, screen_location, mraid_adv_tracking, ad_size, static_ad_unit, "
            + "rich_media_ad_unit, rm_mraid_version, video_mraid_2, video_proprietary, video_vast, "
            + "video_response_protocol, video_playback_method, video_start_delay, fullscreen_timing, "
            + "position_alias_name, memo, updated_on, native_version, video_linearity, video_maxdur, "
            + "height, width, video_skippable, video_skipthreshold, video_skipoffset, status, placement_type "
            + "as placementCategory FROM POSITION WHERE pid = ?";
    Object[] args = new Object[] {pid};
    RowMapper<PositionPss> rowMapper = new BeanPropertyRowMapper<>(PositionPss.class);
    PositionPss position = jdbcTemplateDbCore.queryForObject(sqlQuery, args, rowMapper);
    return position;
  }

  public PositionPss getPositionPssByName(String name, String site_pid) {
    String sqlQuery =
        "SELECT "
            + "pid, name, site_pid, is_default, is_interstitial, version, mraid_support, video_support, screen_location,"
            + " mraid_adv_tracking, ad_size, static_ad_unit, rich_media_ad_unit, rm_mraid_version, video_mraid_2, "
            + "video_proprietary, video_vast, video_response_protocol, video_playback_method, video_start_delay, "
            + "fullscreen_timing, position_alias_name, memo, updated_on, native_version, video_linearity, video_maxdur,"
            + " height, width, video_skippable, video_skipthreshold, video_skipoffset, status, placement_type as placementCategory"
            + " FROM POSITION WHERE site_pid = ? AND name = ?";
    Object[] args = new Object[] {site_pid, name};
    RowMapper<PositionPss> rowMapper = new BeanPropertyRowMapper<>(PositionPss.class);
    PositionPss position = jdbcTemplateDbCore.queryForObject(sqlQuery, args, rowMapper);
    return position;
  }

  public PositionNonPss getPositionNonPssByPid(String pid) {
    String sqlQuery =
        "SELECT "
            + "pid, name, site_pid, is_default, is_interstitial, version, mraid_support, "
            + "video_support, screen_location, mraid_adv_tracking, ad_size, static_ad_unit, "
            + "rich_media_ad_unit, rm_mraid_version, video_mraid_2, video_proprietary, video_vast, "
            + "video_response_protocol, video_playback_method, video_start_delay, fullscreen_timing, "
            + "position_alias_name, memo, updated_on, native_version, video_linearity, video_maxdur, height, "
            + "width, video_skippable, video_skipthreshold, video_skipoffset, status, placement_type "
            + "as placementCategory FROM POSITION WHERE pid = ?";
    Object[] args = new Object[] {pid};
    RowMapper<PositionNonPss> rowMapper = new BeanPropertyRowMapper<>(PositionNonPss.class);
    PositionNonPss position = jdbcTemplateDbCore.queryForObject(sqlQuery, args, rowMapper);
    return position;
  }

  public String getPositionPidByNameSitePid(String posName, String sitepid) {
    String sqlQuery = "SELECT pid FROM position WHERE name = ? AND site_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {posName, sitepid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public boolean getAdvancedMraidTracking(String pid) {
    String sqlQuery = "SELECT mraid_adv_tracking FROM POSITION WHERE pid = ?";
    boolean queryresult =
        jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {pid}, Boolean.class);
    return queryresult;
  }

  public Long getPubAliasId(String seller_pid) {
    String sqlQuery = "SELECT pub_alias FROM seller_attributes WHERE seller_pid = ?";
    Long queryresult =
        jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {seller_pid}, Long.class);
    return queryresult;
  }

  public int disableTransparencyForPublisher(String seller_pid) {
    String sqlQuery =
        "UPDATE seller_attributes SET transparency_management_enablement = 0 WHERE seller_pid = ?";
    return jdbcTemplateDbCore.update(sqlQuery, seller_pid);
  }

  public int setDefaultRTBProfileEnablementFlag(String companyPid, int enablement) {
    String sqlQuery = "UPDATE company SET default_rtb_profiles_enabled = ? WHERE pid = ?";
    return jdbcTemplateDbCore.update(sqlQuery, enablement, companyPid);
  }

  public Long getSiteAliasId(String pid) {
    String sqlQuery = "SELECT site_alias FROM site WHERE pid = ?";
    Long queryresult = jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {pid}, Long.class);
    return queryresult;
  }

  public Long getSiteAliasIdForTag(String tag_pid) {
    String sqlQuery = "SELECT site_alias FROM exchange_site_tag WHERE tag_pid = ?";
    Long queryresult =
        jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {tag_pid}, Long.class);
    return queryresult;
  }

  public Long getPubAliasIdForSite(String site_pid) {
    String sqlQuery = "SELECT pub_alias FROM site WHERE pid = ?";
    Long queryresult =
        jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {site_pid}, Long.class);
    return queryresult;
  }

  public Long getPubAliasIdForTag(String tag_pid) {
    String sqlQuery = "SELECT pub_alias FROM exchange_site_tag WHERE tag_pid = ?";
    Long queryresult =
        jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {tag_pid}, Long.class);
    return queryresult;
  }

  public String getExchangePidByTagPid(String tag_pid) {
    String sqlQuery = "SELECT pid FROM exchange_site_tag WHERE tag_pid = ?";
    String result =
        jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {tag_pid}, String.class);
    return result;
  }

  public Company getCompanyByPid(CompanyType type, String pid) {
    String sql;
    Company companyType = null;

    try {
      sql = "SELECT * FROM company WHERE pid = ?";
      Company company =
          jdbcTemplateDbCore.queryForObject(
              sql, new Object[] {pid}, new BeanPropertyRowMapper<>(Company.class));
      companyType = company;
    } catch (EmptyResultDataAccessException e) {
      log.error(e.getMessage());
    }

    return companyType;
  }

  public Company getCompanyByName(String companyName) {
    String sql = "SELECT * FROM company WHERE name = ?";
    return jdbcTemplateDbCore.queryForObject(
        sql, new Object[] {companyName}, new BeanPropertyRowMapper<>(Company.class));
  }

  public int setOneCentralUsernameForUser(String username) {
    String sqlQuery = "UPDATE app_user set onecentral_username = ? WHERE user_name = ?";
    return updateCrudCore(sqlQuery);
  }

  public String getCompanyForOneCentralUserName(String oneCentralUsername) {
    List<String> result = null;
    String sql = "SELECT company_id FROM app_user WHERE onecentral_username = ?";
    result = jdbcTemplateDbCore.queryForList(sql, new Object[] {oneCentralUsername}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getUserTypeForOneCentralUserName(String oneCentralUsername) {
    String sql =
        "SELECT type FROM company WHERE pid = (SELECT company_id FROM app_user WHERE onecentral_username = ?); ";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sql, new Object[] {oneCentralUsername}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getUserPidForOneCentralUser(String oneCentralUsername) {
    String sql = "SELECT pid FROM app_user WHERE onecentral_username = ?;";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sql, new Object[] {oneCentralUsername}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public int getHeaderBiddingEnabledSiteCount(String company_pid) {
    String sql = "SELECT count(*) FROM site WHERE hb_enabled=1 AND company_pid = " + company_pid;
    int count =
        jdbcTemplateDbCore.query(sql, new SingleColumnRowMapper<Integer>(Integer.class)).get(0);
    return count;
  }

  public String getHBThrottleValues(String pid, String value) {
    String sqlQuery;
    List<String> queryresult;
    String result = null;
    switch (value) {
      case "enabled":
        {
          sqlQuery = "SELECT hb_throttle FROM seller_attributes WHERE seller_pid = ?";
          queryresult = jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
          result = queryresult.isEmpty() ? null : (queryresult.get(0));
          break;
        }
      case "percentage":
        {
          sqlQuery = "SELECT hb_throttle_perc FROM seller_attributes WHERE seller_pid = ?";
          queryresult = jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
          result = queryresult.isEmpty() ? null : (queryresult.get(0));
          break;
        }
    }
    return result;
  }

  public String gethbPricePreference(String pid) throws Throwable {
    String sqlQuery;
    List<String> queryresult;
    sqlQuery = "SELECT hb_price_preference FROM seller_attributes WHERE seller_pid = ?";
    queryresult = jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return queryresult.isEmpty() ? null : (queryresult.get(0));
  }

  public String getDealSupplierByColumnNameAndDescAndSiteNameAlias(
      String columnName, String description, String siteNameAlias) {
    String sql =
        "SELECT "
            + columnName
            + " FROM site s "
            + "INNER JOIN exchange_site_tag e ON s.pid=e.site_pid "
            + "INNER JOIN tag t ON e.tag_id=t.primary_id "
            + "INNER JOIN company c ON s.company_pid=c.pid "
            + "LEFT OUTER JOIN position p ON t.position_pid=p.pid "
            + "LEFT OUTER JOIN tag_rule tr ON t.pid=tr.tag_pid "
            + "WHERE (isnull(tr.rule_type) OR tr.rule_type=\"COUNTRY\") "
            + "and e.description = ? AND e.site_name_alias = ?";

    Object[] args = new Object[] {description, siteNameAlias};
    List<String> result = jdbcTemplateDbCore.queryForList(sql, args, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public Site getSiteSummaryByPid(String pid) throws Throwable {
    String sql = "SELECT * FROM site WHERE pid = ?";
    Site site =
        jdbcTemplateDbCore.queryForObject(
            sql, new Object[] {pid}, new BeanPropertyRowMapper<>(Site.class));
    return site;
  }

  public Site getSiteByName(String name) throws Throwable {
    String sql = "SELECT * FROM site WHERE name = ?";
    Site site =
        jdbcTemplateDbCore.queryForObject(
            sql, new Object[] {name}, new BeanPropertyRowMapper<>(Site.class));
    return site;
  }

  public String getTagPidByName(String name) throws Throwable {
    String sqlQuery = "SELECT pid FROM tag WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTagOwnerByName(String name) throws Throwable {
    String sqlQuery = "SELECT owner FROM tag WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTagMonetizationByName(String name) throws Throwable {
    String sqlQuery = "SELECT monetization FROM tag WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTagIncludeConsumerIdByName(String name) throws Throwable {
    String sqlQuery =
        "SELECT include_consumer_id FROM exchange_site_tag WHERE tag_id = (SELECT primary_id FROM tag WHERE name = ?)";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTagIncludeConsumerProfileByName(String name) throws Throwable {
    String sqlQuery =
        "SELECT include_consumer_profile FROM exchange_site_tag WHERE tag_id = (SELECT primary_id FROM tag WHERE name = ?)";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTagIncludeDomainRefByName(String name) throws Throwable {
    String sqlQuery =
        "SELECT include_domain_references FROM exchange_site_tag WHERE tag_id = (SELECT primary_id FROM tag WHERE name = ?)";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRtbOwnerCompanyPidByTagName(String tagName) throws Throwable {
    String sqlQuery =
        "SELECT default_rtb_profile_owner_company_pid FROM exchange_site_tag WHERE tag_id = (SELECT primary_id FROM tag WHERE name = ?)";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tagName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public Tag getTagValuesPidByName(String name) throws Throwable {
    String sqlQuery = "SELECT * FROM tag WHERE name = ?";
    Tag tag =
        jdbcTemplateDbCore.queryForObject(
            sqlQuery, new Object[] {name}, new BeanPropertyRowMapper<>(Tag.class));
    return tag;
  }

  public String getCompanyPidByName(String name) throws Throwable {
    String sqlQuery = "SELECT pid FROM company WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getBuyerGroupPid(String buyerGroupName, String companyPid) throws Throwable {
    String sqlQuery = "SELECT pid FROM buyer_group WHERE name = ? AND company_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(
            sqlQuery, new Object[] {buyerGroupName, companyPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public int insertBuyerGroup(
      String name,
      String companyPid,
      String sfdcLineId,
      String sfdcIoId,
      String currency,
      String billingCountry,
      String billable,
      String version) {
    String sqlQuery =
        "INSERT INTO buyer_group (name, company_pid, sfdc_line_id,"
            + " sfdc_io_id, currency, billing_country, billable, version)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    return jdbcTemplateDbCore.update(
        sqlQuery,
        new Object[] {
          name,
          companyPid,
          sfdcLineId,
          sfdcIoId,
          currency,
          billingCountry,
          Boolean.valueOf(billable),
          version
        });
  }

  public String getBuyerSeatPid(String seatId, String companyPid) throws Throwable {
    String sqlQuery = "SELECT pid FROM buyer_seat WHERE seat = ? AND company_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {seatId, companyPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getBuyerSeatEnabledStatus(String seatId, String companyPid) throws Throwable {
    String sqlQuery = "SELECT enabled FROM buyer_seat WHERE seat = ? AND company_pid = ?";
    List<Boolean> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {seatId, companyPid}, Boolean.class);
    return result.isEmpty() ? null : result.get(0).toString();
  }

  public int insertBuyerSeat(
      String companyPid,
      String name,
      String seat,
      String buyerGroupPid,
      String enabled,
      String defaultDateValue,
      String version) {
    String sqlQuery =
        "INSERT INTO buyer_seat (company_pid, name, seat, buyer_group_pid,"
            + " enabled, version, creation_date, last_updated_date)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    return jdbcTemplateDbCore.update(
        sqlQuery,
        new Object[] {
          companyPid,
          name,
          seat,
          buyerGroupPid,
          Boolean.valueOf(enabled),
          version,
          defaultDateValue,
          defaultDateValue
        });
  }

  public String getBuyerGroupPidForSeat(String seatId, String companyPid) throws Throwable {
    String sqlQuery = "SELECT buyer_group_pid FROM buyer_seat WHERE seat = ? AND company_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {seatId, companyPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRegionIdByName(String name) throws Throwable {
    String sqlQuery = "SELECT region_id FROM company WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSitePidByName(String name) throws Throwable {
    String sqlQuery = "SELECT pid FROM site WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSiteNameByDcn(String dcn) throws Throwable {
    String sqlQuery = "SELECT name FROM site WHERE dcn = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {dcn}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getStatusByCompanyName(String companyName) throws Throwable {
    String sqlQuery = "SELECT status FROM company WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {companyName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getStatusBySiteName(String siteName) throws Throwable {
    String sqlQuery = "SELECT status FROM site WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {siteName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getDcnByName(String name) throws Throwable {
    String sqlQuery = "SELECT dcn FROM site WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String countSiteByDcn(String dcn) throws Throwable {
    String sqlQuery = "SELECT count(*) FROM site WHERE dcn = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {dcn}, String.class);
    return result.get(0);
  }

  public String countSiteByName(String name) throws Throwable {
    String sqlQuery = "SELECT count(*) FROM site WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.get(0);
  }

  public String countTagByName(String name) throws Throwable {
    String sqlQuery = "SELECT count(*) FROM tag WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.get(0);
  }

  public String countRtbProfileByCompanyPid(String companyPid) throws Throwable {
    String sqlQuery =
        "SELECT count(*) FROM exchange_site_tag WHERE default_rtb_profile_owner_company_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {companyPid}, String.class);
    return result.get(0);
  }

  public String countRtbProfileByRtbName(String rtbName) throws Throwable {
    String sqlQuery = "SELECT count(*) FROM exchange_site_tag WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {rtbName}, String.class);
    return result.get(0);
  }

  public String getRtbProfilePidByName(String rtbProfileName) throws Throwable {
    String sqlQuery = "SELECT pid FROM exchange_site_tag WHERE name =?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {rtbProfileName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRtbProfilePidByTag(String tagPid) throws Throwable {
    String sqlQuery = "SELECT pid FROM exchange_site_tag WHERE tag_pid =?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tagPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRtbProfileStatusByName(String rtbProfileName, String companyPid)
      throws Throwable {
    String sqlQuery =
        "SELECT status FROM exchange_site_tag WHERE name = ? AND default_rtb_profile_owner_company_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(
            sqlQuery, new Object[] {rtbProfileName, companyPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRtbProfileStatusByTag(String tagPid) throws Throwable {
    String sqlQuery = "SELECT status FROM exchange_site_tag WHERE tag_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tagPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRTBProfileLibraryPidByName(String name) throws Throwable {
    String sqlQuery = "SELECT pid FROM rtb_profile_library WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getUsers(String name) throws Throwable {
    String sqlQuery = "SELECT pid FROM app_user WHERE user_name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getPositionPidByName(String positionName) {
    String sqlQuery = "SELECT pid FROM position WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {positionName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getStatusByPositionName(String positionName) {
    String sqlQuery = "SELECT status FROM position WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {positionName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getStatusByTagName(String tagName) {
    String sqlQuery = "SELECT status FROM tag WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tagName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTierByPid(String tierPid) {
    String sqlQuery = "SELECT count(*) FROM tier WHERE pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tierPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTierCountByPositionPid(String positionName) {
    String sqlQuery =
        "SELECT count(*) FROM tier WHERE position_pid = (SELECT pid FROM position WHERE name = ?)";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {positionName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTierLevelByTierPid(String tierPid) {
    String sqlQuery = "SELECT level FROM tier WHERE pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tierPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getTagCountByTierPid(String tierPid) {
    String sqlQuery = "SELECT count(*) FROM tier_tag WHERE tier_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {tierPid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSecretKeyHash(String companyId) {
    String sqlQuery = "SELECT digest_ha1_key FROM reporting_api WHERE company_id = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {companyId}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public int updateCountGlobalConfig(String propertyName, int value) {
    String sqlQuery =
        "UPDATE global_config set value = '"
            + value
            + "'"
            + " WHERE property = '"
            + propertyName
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public String getSellerName(String name) {
    String sqlQuery = "SELECT pid FROM company WHERE name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public int updateSellerSiteCount(String updateSellerSite, int value) {
    String sqlQuery =
        "UPDATE seller_attributes set site_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerSite
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerPositionCount(String updateSellerPosition, int value) {
    String sqlQuery =
        "UPDATE seller_attributes set positions_per_site_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerPosition
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerAdSourceCount(String updateSellerAdSource, int value) {
    String sqlQuery =
        "UPDATE seller_attributes set tags_per_position_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerAdSource
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerCampaignCount(String updateSellerCampaign, int value) {
    String sqlQuery =
        "UPDATE seller_attributes set campaigns_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerCampaign
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerCreativePerCampaignCount(String updateSellerCreativePerCampaign, int value)
      throws Throwable {
    String sqlQuery =
        "UPDATE seller_attributes set creatives_per_campaign_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerCreativePerCampaign
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerBidderGroupsCount(String updateSellerBidderGroups, int value)
      throws Throwable {
    String sqlQuery =
        "UPDATE seller_attributes set bidder_libraries_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerBidderGroups
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerBlockGroupsCount(String updateSellerBlockGroups, int value)
      throws Throwable {
    String sqlQuery =
        "UPDATE seller_attributes set block_libraries_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerBlockGroups
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerUsersCount(String updateSellerUsers, int value) {
    String sqlQuery =
        "UPDATE seller_attributes set user_limit = '"
            + value
            + "'"
            + " WHERE seller_pid = '"
            + updateSellerUsers
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerLimitEnabledFlag(String updateSellerUsers, int value) {
    String sqlQuery =
        "UPDATE seller_attributes set limit_enabled = "
            + value
            + " WHERE seller_pid = '"
            + updateSellerUsers
            + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updatePositionStatus() {
    String sqlQuery =
        "UPDATE position set status = 0  WHERE name = " + " 'Position1_inactive_banner' ";
    return updateCrudCore(sqlQuery);
  }

  public int updateSellerAttributesRtb(String rtbPid, String companyPid) {
    String sqlQuery =
        "UPDATE seller_attributes set rtb_profile = "
            + rtbPid
            + " WHERE seller_pid = "
            + companyPid;
    return updateCrudCore(sqlQuery);
  }

  public int updatePositionStatusDeleted(String positionName) {
    String sqlQuery = "UPDATE position set status = -1 WHERE name = '" + positionName + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateSiteRtb(String rtbPid, String sitePid) {
    String sqlQuery = "UPDATE site set rtb_profile = " + rtbPid + " WHERE pid = " + sitePid;
    return updateCrudCore(sqlQuery);
  }

  public int updateSiteStatusDeleted(String siteName) {
    String sqlQuery = "UPDATE site set status = -1 WHERE name = '" + siteName + "'";
    return updateCrudCore(sqlQuery);
  }

  public int updateExchangeRtbProfile(String companyPid, String rtbPid) {
    String sqlQuery =
        "UPDATE exchange_site_tag set default_rtb_profile_owner_company_pid = "
            + companyPid
            + " WHERE pid = "
            + rtbPid;
    return updateCrudCore(sqlQuery);
  }

  public String getSellerSiteLimitValue(String pid) {
    String sqlQuery = "SELECT site_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerPositionLimitValue(String pid) {
    String sqlQuery = "SELECT positions_per_site_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerTagLimitValue(String pid) {
    String sqlQuery = "SELECT tags_per_position_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerCampaignLimitValue(String pid) {
    String sqlQuery = "SELECT campaigns_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerUserLimitValue(String pid) {
    String sqlQuery = "SELECT user_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerBidderGroupLimitValue(String pid) {
    String sqlQuery = "SELECT bidder_libraries_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerBlockGroupLimitValue(String pid) throws Throwable {
    String sqlQuery = "SELECT block_libraries_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerCreativeCampaignLimitValue(String pid) {
    String sqlQuery =
        "SELECT creatives_per_campaign_limit FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getSellerLimitFlagValue(String pid) {
    String sqlQuery = "SELECT limit_enabled FROM seller_attributes WHERE seller_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {pid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String checkUserName(String userName) {
    String sqlQuery = "SELECT pid FROM app_user WHERE user_name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {userName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public void setReportingApiAccess(boolean value) {
    String sql =
        String.format(
            "UPDATE global_config SET value = '%s' WHERE property = 'allow_reporting_api'", value);
    try {
      int updatedRows = jdbcTemplateDbCore.update(sql);
      assertTrue(updatedRows > 0, "No records were updated");
    } catch (DataAccessException exception) {
      log.error(exception.getMessage());
    }
  }

  public List<Map<String, Object>> lookupCoreRecordsByFieldNameAndValue(
      String table, String fieldName, String fieldValueSqlFormatted) {
    String sql = "SELECT * FROM " + table + " WHERE " + fieldName + "=" + fieldValueSqlFormatted;
    List<Map<String, Object>> queryResult = jdbcTemplateDbCore.query(sql, new ColumnMapRowMapper());
    return queryResult;
  }

  public List<Map<String, Object>> lookupCoreRecordsByFieldNameAndValue(
      String table,
      String fieldName,
      String fieldValueSqlFormatted,
      String fieldName2,
      String fieldValueSqlFormatted2)
      throws SQLException {
    String sql =
        "SELECT * FROM "
            + table
            + " WHERE "
            + fieldName
            + "="
            + fieldValueSqlFormatted
            + " AND "
            + fieldName2
            + "="
            + fieldValueSqlFormatted2;
    List<Map<String, Object>> queryResult = jdbcTemplateDbCore.query(sql, new ColumnMapRowMapper());
    return queryResult;
  }

  public int countCoreRecordsByFieldNameAndValue(
      String table, String fieldName, String fieldValueSqlFormatted) {
    String sql =
        "SELECT COUNT(*) FROM " + table + " WHERE " + fieldName + "=" + fieldValueSqlFormatted;
    int count =
        jdbcTemplateDbCore.query(sql, new SingleColumnRowMapper<Integer>(Integer.class)).get(0);
    return count;
  }

  public void resetRecordVersionByPid(String table, String pid) {
    String sql = "UPDATE " + table + " SET VERSION=0 WHERE pid=" + pid;
    jdbcTemplateDbCore.execute(sql);
  }

  public void deleteCoreRecordsByFieldNameAndValue(
      String table, String fieldName, String fieldValueSqlFormatted) {
    String sql = "DELETE FROM " + table + " WHERE +" + fieldName + "=" + fieldValueSqlFormatted;
    jdbcTemplateDbCore.execute(sql);
  }

  public void resetAutoIncrement(String table, String value) {
    var sql = "ALTER TABLE " + table + " AUTO_INCREMENT = " + value;
    jdbcTemplateDbCore.execute(sql);
  }

  public void updateFactRevenueAdnetStartEndDates() {
    String sql =
        String.format(
            "UPDATE fact_revenue_adnet SET start = current_date() - interval '2' day,"
                + "stop = current_date() - interval '1' day WHERE tag_id in (7302,7303,7304,7305,7306,"
                + "7307,7308,7309,7310,7377,7378,7379,7380)");
    jdbcTemplateCrudDatawarehouse.execute(sql);
  }

  public String getTagIncludeSiteName(String name) {
    String sqlQuery =
        "SELECT include_site_name FROM exchange_site_tag WHERE tag_id in "
            + "(SELECT primary_id FROM tag WHERE name = ? )";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getCridHeaderField(String name) {
    String sqlQuery = "SELECT crid_header_field FROM ad_source WHERE name = ? ";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {name}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public List<Tag> getTagsByPositionPid(String pid) {
    String sqlQuery =
        "SELECT pid, name, site_pid, status, primary_id, primary_name, secondary_id, secondary_name, "
            + "ecpm_provision, ecpm_auto, owner, buyer_class, url_template, get_template, post_template, additional_get,"
            + " additional_post, noad_regex, clickthrough_disable, ecpm_manual, adspaceid_template, adspacename_template, "
            + "postprocess_template, httpheader_template, adnetreport_username, adnetreport_password, buyer_pid, id, "
            + "version, monetization, return_raw_response, is_interstitial, ad_size, is_video_allowed, import_revenue_flag,"
            + " position_pid, updated_on, height, width, video_skippable, video_skipthreshold, video_skipoffset, video_linearity,"
            + " video_maxdur, video_start_delay, video_playback_method, video_support, screen_location, adnetreport_apikey, "
            + "adnetreport_apitoken, autogenerated FROM TAG WHERE position_pid = "
            + pid;
    RowMapper<Tag> rowMapper = new BeanPropertyRowMapper<>(Tag.class);
    List<Tag> tagList = jdbcTemplateDbCore.query(sqlQuery, rowMapper);
    return tagList;
  }

  public RtbProfile getRtbProfileByTagsPid(String pid) {
    String sqlQuery =
        "SELECT tag_id, VERSION, pid, creation_date, last_update, site_alias, site_name_alias, site_type,"
            + " pub_alias, pub_name_alias, include_site_name, include_consumer_id, include_consumer_profile, "
            + "include_domain_references, default_reserve, auction_type,  blocked_ad_types, blocked_ad_categories, "
            + "blocked_advertisers, description, filter_bidders, filter_bidders_whitelist, filter_bidders_allowlist, screening_level, "
            + "blocked_external_data_providers, include_geo_data, site_pid, blocked_attributes, low_reserve, "
            + "pub_net_reserve, pub_net_low_reserve, use_default_block, use_default_bidders, alter_reserve"
            + " FROM exchange_site_tag WHERE tag_id in (SELECT primary_id FROM tag WHERE pid = ? )";
    RowMapper<RtbProfile> rowMapper = new BeanPropertyRowMapper<>(RtbProfile.class);
    try {
      RtbProfile rtb =
          jdbcTemplateDbCore.queryForObject(
              sqlQuery, new Object[] {pid}, new BeanPropertyRowMapper<>(RtbProfile.class));
      return rtb;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public DealTerm getLatestDealTermsFromTagPid(String TagPid) {
    String sqlQuery =
        "SELECT site_pid, effective_date, flat_bands, nexage_rev_share, revenue_mode, rtb_fee, tag_pid, pid, version"
            + " FROM deal_term WHERE tag_pid = ? ORDER BY effective_date desc LIMIT 1";
    try {
      DealTerm dealterm =
          jdbcTemplateDbCore.queryForObject(
              sqlQuery, new Object[] {TagPid}, new BeanPropertyRowMapper<>(DealTerm.class));
      return dealterm;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public String getNexageRevShareFromSitePid(String SitePid) {
    String sqlQuery =
        "SELECT nexage_rev_share" + " FROM deal_term WHERE site_pid = ? ORDER BY pid desc LIMIT 1";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {SitePid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRtbFeeBySitePid(String sitePid) {
    String sqlQuery =
        "SELECT rtb_fee" + " FROM deal_term WHERE site_pid = ? ORDER BY pid desc LIMIT 1";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {sitePid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String countDealTermsBySitePid(String sitePid) {
    String sqlQuery = "SELECT count(*) FROM deal_term WHERE site_pid = ?";
    List<String> countTerm =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {sitePid}, String.class);
    return countTerm.isEmpty() ? null : countTerm.get(0);
  }

  public List<RevenueShare> getRevenueShares() {
    String sqlQuery =
        "SELECT st.pid as site_tag_pid, coalesce(tdt.nexage_rev_share, sdt.nexage_rev_share, 0.0) + coalesce(tdt.rtb_fee, sdt.rtb_fee, 0.0) as total_rev_share, coalesce(tdt.nexage_rev_share, sdt.nexage_rev_share, 0.0) as nexage_rev_share,coalesce(tdt.rtb_fee, sdt.rtb_fee, 0.0) as nexage_rtb_fee , t.pid as tag_pid, t.status as tag_status FROM exchange_site_tag st inner join tag t on st.tag_id = t.primary_id AND t.status is not null AND t.status >= 1 left join ( SELECT pid, site_pid, tag_pid, nexage_rev_share, rtb_fee FROM ( SELECT max(pid) as max_pid FROM deal_term WHERE tag_pid is not null group by tag_pid ) as ldt left join deal_term dt on dt.pid = ldt.max_pid ) as tdt on t.pid = tdt.tag_pid left join ( SELECT pid, site_pid, tag_pid, nexage_rev_share, rtb_fee FROM ( SELECT max(pid) as max_pid FROM deal_term WHERE tag_pid is null group by site_pid ) as ldt left join deal_term dt on dt.pid = ldt.max_pid ) as sdt on t.site_pid = sdt.site_pid";
    try {
      RowMapper<RevenueShare> rowMapper = new BeanPropertyRowMapper<>(RevenueShare.class);
      List<RevenueShare> RevenueShareList = jdbcTemplateDbCore.query(sqlQuery, rowMapper);
      return RevenueShareList;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public void tagArchiveVertica() {
    log.info("Start fill dim_tag for Archive Postions");
    insertTagArchiveVertica(getTagArchiveVerticas());
    log.info("Finish fill dim_tag for Archive Postions");
  }

  private List<TagArchiveVertica> getTagArchiveVerticas() {
    String sqlQuery =
        "SELECT "
            + "     t.pid as tagPid, t.name as tagName, t.status as tagStatus, NULL, t.owner as tagOwner, t.site_pid as sitePid, t.buyer_pid as buyerPid, st.site_name_alias as siteNameAlias, \n"
            + "     t.id as tagId, CAST(t.monetization=1 AS SIGNED INTEGER) as monetization, st.pid as exchSiteTagPid, p.name as positionName, NULL, NULL, NULL, NULL, NULL, NULL, NULL \n"
            + "    FROM core.tag t \n"
            + "    LEFT JOIN core.position p \n"
            + "        ON p.pid = t.position_pid \n"
            + "        LEFT OUTER JOIN core.exchange_site_tag st \n"
            + "            ON st.tag_id = t.primary_id";
    try {
      return jdbcTemplateDbCore.query(
          sqlQuery, new BeanPropertyRowMapper<>(TagArchiveVertica.class));
    } catch (EmptyResultDataAccessException e) {
      return Collections.emptyList();
    }
  }

  private void insertTagArchiveVertica(List<TagArchiveVertica> tagArchiveVerticas) {
    for (TagArchiveVertica t : tagArchiveVerticas) {
      try {
        String sqlDeleteTag = String.format("DELETE FROM dim_tag WHERE id=%d", t.getTagPid());
        jdbcTemplateCrudDatawarehouse.update(sqlDeleteTag);
        String sqlInsertTag =
            "INSERT INTO dim_tag VALUES (?,?,?,NULL,?,?,?,?,?,?,?,?,NULL,NULL,NULL,NULL,NULL,NULL,NULL)";
        jdbcTemplateCrudDatawarehouse.update(
            sqlInsertTag,
            t.getTagPid(),
            t.getTagName(),
            t.getTagStatus(),
            t.getTagOwner(),
            t.getSitePid(),
            t.getBuyerPid(),
            t.getSiteNameAlias(),
            t.getTagId(),
            t.getMonetization(),
            t.getExchSiteTagPid(),
            t.getPositionName());
      } catch (Exception e) {
        log.error("Error!!! " + e.getMessage(), e);
      }
    }
  }

  public TagRule getTagRuleFromTagPid(String TagPid) {
    String sqlQuery =
        "SELECT pid, tag_pid, target, target_type, rule_type, param_name, version FROM tag_rule WHERE tag_pid = ?";
    try {
      TagRule tagRule =
          jdbcTemplateDbCore.queryForObject(
              sqlQuery, new Object[] {TagPid}, new BeanPropertyRowMapper<>(TagRule.class));
      return tagRule;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public Tier getTierTagFromTag(Tag tag) {
    String sqlQuery =
        "SELECT pid, level, position_pid, order_strategy, version, autogenerated"
            + " FROM tier WHERE position_pid= ? AND pid in "
            + "(SELECT tier_pid FROM tier_tag WHERE tag_pid = ? )";
    try {
      Tier tier =
          jdbcTemplateDbCore.queryForObject(
              sqlQuery,
              new Object[] {tag.getPosition_pid(), tag.getPid()},
              new BeanPropertyRowMapper<>(Tier.class));
      return tier;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public int[] insertDwDataToTable(String tableName, List<String[]> rowValues) {
    if (!rowValues.isEmpty()) {
      String sqlQuery;
      for (String[] row : rowValues) {
        sqlQuery = "INSERT INTO " + tableName + " VALUES ";
        sqlQuery = sqlQuery + "(";
        for (int i = 0; i < row.length; i++) {
          sqlQuery = sqlQuery + row[i] + ",";
        }
        sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1) + ");\n";
        jdbcTemplateCrudDatawarehouse.update(sqlQuery);
      }
      return new int[0];
    } else return new int[0];
  }

  public int insertCoreDataToTable(String tableName, List<String[]> rowValues) throws Throwable {
    if (rowValues.isEmpty()) throw new Exception("Empty data set");

    StringBuilder sb = new StringBuilder(5000);
    sb.append("INSERT INTO ").append(tableName).append(" VALUES ");
    String rows =
        rowValues.stream()
            .map(row -> "(" + String.join(",", row) + ")")
            .collect(Collectors.joining(","));
    sb.append(rows);

    return jdbcTemplateDbCore.update(sb.toString());
  }

  public void clearDWTable(String tableName) {
    String sqlQuery = "TRUNCATE TABLE " + tableName;
    jdbcTemplateCrudDatawarehouse.execute(sqlQuery);
  }

  public void clearTable(String tableName) {
    String sqlQuery = "TRUNCATE TABLE " + tableName;
    jdbcTemplateDbCore.execute(sqlQuery);
  }

  public int getDWTableRowCount(String tableName) {
    String sqlQuery = "SELECT COUNT(*) FROM " + tableName;
    return jdbcTemplateCrudDatawarehouse.queryForObject(sqlQuery, Integer.class).intValue();
  }

  public DataTable getDwDataTable(String tableName, List<String> columnNames) {
    SqlRowSet sqlRowSet =
        jdbcTemplateCrudDatawarehouse.queryForRowSet(
            new StringBuilder().append("SELECT * ").append("FROM ").append(tableName).toString());
    List<List<String>> rows = new LinkedList<>();

    while (sqlRowSet.next()) {
      rows.add(
          columnNames.stream()
              .map(
                  sqlRowSet
                      ::getString) // Convert directly to string to bypass cucumber conversions.
              .collect(Collectors.toList()));
    }

    return DataTable.create(rows);
  }

  public BidderConfig getBidderConfigCookieSyncParamaters(String bidderId) {
    String sqlQuery =
        "SELECT id, pid, cookie_sync_enabled, bidrequest_userid_preference "
            + "from bidder_config WHERE id = ?";
    BidderConfig bidderCookieConfig =
        jdbcTemplateDbCore.queryForObject(
            sqlQuery, new Object[] {bidderId}, new BeanPropertyRowMapper<>(BidderConfig.class));

    return bidderCookieConfig;
  }

  public BidderConfig getBidderConfigCookieSyncAuditParamaters(String bidderId) {
    String sqlQuery =
        "SELECT id, pid, cookie_sync_enabled, bidrequest_userid_preference "
            + "from bidder_config_aud WHERE id = ? ORDER BY by last_update DESC LIMIT 1";
    BidderConfig bidderCookieConfig =
        jdbcTemplateDbCore.queryForObject(
            sqlQuery, new Object[] {bidderId}, new BeanPropertyRowMapper<>(BidderConfig.class));

    return bidderCookieConfig;
  }

  public List<String> getExchageTagPidsAssociatedWithLibraryPid(String libPid) {
    String sqlQuery =
        String.format(
            "SELECT et.tag_pid FROM rtb_profile_library_association as ls,"
                + "exchange_site_tag as et WHERE et.pid=ls.rtb_profile_pid AND ls.library_pid=%s;",
            libPid);
    return jdbcTemplateDbCore.queryForList(sqlQuery, String.class);
  }

  public List<String> getExchageTagPidsAssociatedWithLibraryPidInAud(String libPid) {
    String sqlQuery =
        String.format(
            "SELECT et.tag_pid FROM rtb_profile_library_association_aud as ls,"
                + "exchange_site_tag as et WHERE et.pid=ls.rtb_profile_pid AND ls.library_pid=%s;",
            libPid);
    return jdbcTemplateDbCore.queryForList(sqlQuery, String.class);
  }

  private int updateCrudCore(String sqlQuery) {
    return updateDatabase(sqlQuery, jdbcTemplateDbCore);
  }

  private int updateDatabase(String sqlQuery, JdbcTemplate template) {
    int result = 0;
    try {
      result = template.update(sqlQuery);
    } catch (DataAccessException e) {
      log.error(e.getMessage());
    }
    return result;
  }

  public List<Integer> getRtbProfileAlterReservesForCompany(String companyPid) {
    String sqlQuery =
        "SELECT alter_reserve FROM exchange_site_tag est , site "
            + "WHERE est.site_pid = site.pid && site.company_pid = ?";

    return jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {companyPid}, Integer.class);
  }

  // Selling rules block
  public String getRuleStatusByName(String ruleName) throws Throwable {
    String sqlQuery = "SELECT status FROM rule WHERE name =?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {ruleName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getRulePidByName(String ruleName) throws Throwable {
    String sqlQuery = "SELECT pid FROM rule WHERE name =?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {ruleName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getActionDataByRulePid(String rulePid) {
    String sqlQuery = "SELECT action_data FROM rule_intended_action WHERE rule_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {rulePid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getActionTypeByRulePid(String rulePid) {
    String sqlQuery = "SELECT action_type FROM rule_intended_action WHERE rule_pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {rulePid}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public Integer countRulesByName(String ruleName) {
    String sqlQuery = "SELECT count(*) FROM rule WHERE name =?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {ruleName}, Integer.class);
  }

  public Integer countCompaniesWithContactByUsername(String userName) {
    String sqlQuery =
        "SELECT count(*) FROM company c JOIN app_user a ON c.contact_id = a.pid WHERE a.user_name =?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {userName}, Integer.class);
  }

  public Integer countIntendedActionsByRule(String rulePid) {
    String sqlQuery = "SELECT count(*) FROM rule_intended_action WHERE rule_pid =?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {rulePid}, Integer.class);
  }

  public Integer countTargetsByRule(String rulePid) {
    String sqlQuery = "SELECT count(*) FROM rule_target WHERE rule_pid =?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {rulePid}, Integer.class);
  }

  public Integer countFormulasByRule(String rulePid) {
    String sqlQuery = "SELECT count(*) FROM rule_formula WHERE rule_pid = ?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {rulePid}, Integer.class);
  }

  public Integer countInventoryAttributeByName(String attributeName) {
    String sqlQuery = "SELECT count(*) FROM attributes WHERE status = 1 AND  name =?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {attributeName}, Integer.class);
  }

  public Integer countInventoryAttributeByCompanyPid(String attributePid) {
    String sqlQuery = "SELECT count(*) FROM attributes WHERE company_pid =?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {attributePid}, Integer.class);
  }

  public Integer countInventoryAttributeValuesByName(List<String> attributeNames) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate =
        new NamedParameterJdbcTemplate(jdbcTemplateDbCore);
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("names", attributeNames);
    List<String> names =
        namedParameterJdbcTemplate.query(
            "SELECT name FROM attribute_values WHERE name in (:names)",
            parameters,
            (resultSet, i) -> resultSet.getString("name"));
    return names.size();
  }

  public InventoryAttribute getInventoryAttributeByName(String attributeName) throws Throwable {
    String sqlQuery = "SELECT pid FROM attributes WHERE name =? ORDER BY last_update DESC LIMIT 1";
    try {
      InventoryAttribute inventoryAttribute =
          jdbcTemplateDbCore.queryForObject(
              sqlQuery,
              new Object[] {attributeName},
              new BeanPropertyRowMapper<>(InventoryAttribute.class));
      return inventoryAttribute;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public Long getBpTagByName(String name) {
    String sqlQuery = "SELECT pid FROM brand_protection_tag WHERE name=?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {name}, Long.class);
  }

  public int countBpTagByName(String name) {
    String sqlQuery = "SELECT count(pid) FROM brand_protection_tag WHERE name=?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {name}, Integer.class);
  }

  public Long getBpTagValuesByName(String name) {
    String sqlQuery = "SELECT pid FROM brand_protection_tag_values WHERE name=?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {name}, Long.class);
  }

  public int countBpTagValuesByName(String name) {
    String sqlQuery = "SELECT count(pid) FROM brand_protection_tag_values WHERE name=?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {name}, Integer.class);
  }

  public Long getBpCategoryByName(String name) {
    String sqlQuery = "SELECT pid FROM brand_protection_category WHERE name=?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {name}, Long.class);
  }

  public int countBpCategoryByName(String name) {
    String sqlQuery = "SELECT count(pid) FROM brand_protection_category WHERE name=?";
    return jdbcTemplateDbCore.queryForObject(sqlQuery, new Object[] {name}, Integer.class);
  }

  public Long getCrsTagMappingsByBpAndCrs(String bprotTagId, String crsTagId) {
    String sqlQuery = "SELECT pid FROM crs_tag_mapping WHERE bprot_tag_pid=? AND crs_tag_id=?";
    return jdbcTemplateDbCore.queryForObject(
        sqlQuery, new Object[] {bprotTagId, crsTagId}, Long.class);
  }

  public int countCrsTagMappingsByBpAndCrs(String bprotTagId, String crsTagId) {
    String sqlQuery =
        "SELECT count(pid) FROM crs_tag_mapping WHERE bprot_tag_pid=? AND crs_tag_id=?";
    return jdbcTemplateDbCore.queryForObject(
        sqlQuery, new Object[] {bprotTagId, crsTagId}, Integer.class);
  }

  public boolean containsBrandProtectionTagAuditRevType(String revType, String pid) {
    return containsRevTypeForPidFrom(revType, pid, "brand_protection_tag_aud");
  }

  public boolean containsBrandProtectionTagValuesAuditRevType(String revType, String pid) {
    return containsRevTypeForPidFrom(revType, pid, "brand_protection_tag_values_aud");
  }

  public boolean containsBrandProtectionCategoryAuditRevType(String revType, String pid) {
    return containsRevTypeForPidFrom(revType, pid, "brand_protection_category_aud");
  }

  public boolean containsCrsTagMappingAuditRevType(String revType, String pid) {
    return containsRevTypeForPidFrom(revType, pid, "crs_tag_mapping_aud");
  }

  private boolean containsRevTypeForPidFrom(String revType, String pid, String table) {
    String sqlQuery = "SELECT REVTYPE FROM " + table + " WHERE revtype = ? AND pid = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {revType, pid}, String.class);
    return !result.isEmpty();
  }

  public int setCompanyCurrency(String companyPid, String currency) {
    String sqlQuery = "UPDATE company SET currency = ? WHERE pid = ?";
    return jdbcTemplateDbCore.update(sqlQuery, currency, companyPid);
  }

  public Long getKeyValuePairPid(String sellerId, String name) {
    String sqlQuery = "SELECT pid FROM key_value_pairs WHERE seller_id = ? AND key_name = ?";
    Object[] args = new Object[] {sellerId, name};
    return jdbcTemplateDbCore.queryForObject(sqlQuery, args, Long.class);
  }

  public Long getSellersSeatKeyValuePairPid(String sellerSeatId, String name) {
    String sqlQuery = "SELECT pid FROM key_value_pairs WHERE seller_seat_id = ? AND key_name = ?";
    Object[] args = new Object[] {sellerSeatId, name};
    return jdbcTemplateDbCore.queryForObject(sqlQuery, args, Long.class);
  }

  public int insertRules(
      String companyPid, List<Long> rulePids, List<String> ruleNames, List<Integer> ruleTypes) {

    String rulesInsert =
        "INSERT INTO rule (pid, company_pid, version, status, name, description, "
            + "last_update, rule_type, seller_seat_pid) "
            + "VALUES (?, ?, 1, 1, ?, ?, now(), ?, NULL)";

    List<Object[]> inputData = new ArrayList<>();
    for (int i = 0; i < rulePids.size(); i++) {
      Long rulePid = rulePids.get(i);
      String ruleName = ruleNames.get(i);
      Integer ruleType = ruleTypes.get(i);
      inputData.add(new Object[] {rulePid, companyPid, ruleName, "desc " + rulePid, ruleType});
    }

    int[] updates = jdbcTemplateDbCore.batchUpdate(rulesInsert, inputData);
    return updates.length;
  }

  public int insertSites(String companyPid, List<Long> sitePids) {
    String sitesSql =
        "INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used, "
            + "creation_date, days_free, dcn, description, domain,"
            + " filter_bots, input_date_format, integration, last_update, live, name, override_ip,  platform,"
            + " report_batch_size, report_frequency, revenue_launch_date, rules_update_frequency,"
            + " send_ids, status, total_timeout, traffic_throttle, type, url, version, zip_overlay,"
            + " company_pid, buyer_timeout, id ,  enable_groups, coppa_restricted, ad_truth,"
            + " mask_ip) "
            + "VALUES (?, 0, 1, 1, now(), 0, ?, ?,"
            + " 'site.com', 1, 'yyyyMMdd', 'API', now(), 1, ?, 0, 'OTHER',"
            + " 180000, 180000, now(), 1800000, 1, 1, 5000, 0, 'MOBILE_WEB',"
            + " 'ssfl', 0, 0, ?, 1000, ?, 0, 0, 0, 0)";

    List<Object[]> inputData = new ArrayList<>();
    for (Long sitePid : sitePids) {
      String dcn = "vmbc " + sitePid;
      String desc = "Site description " + sitePid;
      String name = "Site name " + sitePid;
      String id = "Site id " + sitePid;
      inputData.add(new Object[] {sitePid, dcn, desc, name, companyPid, id});
    }

    int[] updates = jdbcTemplateDbCore.batchUpdate(sitesSql, inputData);
    return updates.length;
  }

  public int insertPositions(List<Long> positionPids, List<Long> sitePids) {
    String positionsSql =
        "INSERT INTO position (pid, name, site_pid, is_default, is_interstitial,"
            + " version, mraid_support, video_support, screen_location, mraid_adv_tracking, ad_size, "
            + "static_ad_unit, rich_media_ad_unit, rm_mraid_version, video_mraid_2, video_proprietary,"
            + " video_vast, video_response_protocol, video_playback_method, video_start_delay,"
            + " fullscreen_timing, position_alias_name, memo, updated_on, native_version, video_linearity,"
            + " video_maxdur, height, width, video_skippable, video_skipthreshold, video_skipoffset,"
            + " status, placement_type, traffic_type, rtb_profile, ad_size_type, native_config) "
            + "VALUES (?, ?, ?, 0, 0, 1, 1, 0, -1, 1, NULL, NULL,"
            + " NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,"
            + " NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL)";

    List<Object[]> inputData = new ArrayList<>();
    for (int i = 0; i < positionPids.size(); i++) {
      Long positionPid = positionPids.get(i);
      String name = "Pos name " + positionPid;
      Long sitePid = sitePids.get(i);
      inputData.add(new Object[] {positionPid, name, sitePid});
    }

    int[] updates = jdbcTemplateDbCore.batchUpdate(positionsSql, inputData);
    return updates.length;
  }

  public int insertDealRuleRelations(List<Object[]> relations) {
    String sql = "INSERT INTO deal_rule (deal_pid, rule_group_pid,version) VALUES (?, ?, 1)";

    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, relations);
    return updates.length;
  }

  public int insertSiteRuleRelations(List<Object[]> relations) {
    String sql = "INSERT INTO site_rule (site_pid, rule_pid) VALUES (?, ?)";

    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, relations);
    return updates.length;
  }

  public int insertPositionRuleRelations(List<Object[]> relations) {
    String sql = "INSERT INTO position_rule (position_pid, rule_pid) VALUES (?, ?)";

    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, relations);
    return updates.length;
  }

  public int insertCompanyRuleRelations(List<Object[]> relations) {
    String sql = "INSERT INTO company_rule (rule_pid, company_pid) VALUES (?, ?)";

    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, relations);
    return updates.length;
  }

  public int insertRevenueGroup() {
    String sql =
        "INSERT revenue_group (pid, id, revenue_group_name, status, version)"
            + "VALUES (1, 19 , \"Verizon2\",1, 1)";
    return jdbcTemplateDbCore.update(sql);
  }

  public int inserDealSyncStatus(
      List<Long> companyPids, List<Long> dealPids, List<Integer> syncs, List<String> syncTimes) {
    String sql =
        "INSERT deal_sync_status (company_pid, deal_pid, sync, sync_time,data,version)"
            + "VALUES (?, ?, ? ,?,'Test','1')";

    List<Object[]> inputData = new ArrayList<>();

    for (int i = 0; i < dealPids.size(); i++) {
      Long companyPid = companyPids.get(i);
      Long dealPid = dealPids.get(i);
      int sync = syncs.get(i);
      String syncTime = syncTimes.get(i);
      inputData.add(new Object[] {companyPid, dealPid, sync, syncTime});
    }

    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, inputData);
    return updates.length;
  }

  public void insertDealSyncCompanies() {
    String query =
        "SELECT count(*) FROM company WHERE pid = 5110 " + "OR pid = 5773 " + "OR pid = 55816";
    int count =
        jdbcTemplateDbCore.query(query, new SingleColumnRowMapper<Integer>(Integer.class)).get(0);
    if (count <= 0) {
      String insert =
          "INSERT INTO company (pid, id, name, type, VERSION) "
              + "VALUES  (5110, 5110, 'DV360', 'BUYER', 0), "
              + "(5773, 5773, 'Xandr', 'BUYER', 0), "
              + "(55816, 55816, 'Xandr MS Rebroadcast', 'BUYER', 0)";
      jdbcTemplateDbCore.execute(insert);
    }
  }

  public int insertDealSyncOptionsDv360(
      Long companyPid, List<Long> dealPids, List<String> formats, List<String> syncTimes) {
    String delete = "DELETE FROM deal_options_dv360 where deal_pid = ? AND company_pid = ?";
    String sql =
        "INSERT into deal_options_dv360 (deal_pid, company_pid, format, updated_on, active, version)"
            + " VALUES (?, ?, ?, ?, 1, '1')";

    List<Object[]> deleteData = new ArrayList<>();
    List<Object[]> inputData = new ArrayList<>();

    for (int i = 0; i < dealPids.size(); i++) {
      Long dealPid = dealPids.get(i);
      String format = formats.get(i).length() > 0 ? formats.get(i) : null;
      String syncTime = syncTimes.get(i);
      deleteData.add(new Object[] {dealPid, companyPid});
      inputData.add(new Object[] {dealPid, companyPid, format, syncTime});
    }
    insertDealSyncCompanies();
    jdbcTemplateDbCore.batchUpdate(delete, deleteData);
    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, inputData);
    return updates.length;
  }

  public int insertDealSyncOptionsXandr(
      Long companyPid, List<Long> dealPids, List<Long> bidderIds, List<String> syncTimes) {
    String delete = "DELETE FROM deal_options_xandr where deal_pid = ? AND company_pid = ?";
    String sql =
        "INSERT into deal_options_xandr (deal_pid, company_pid, bidder_id, updated_on, active, version)"
            + " VALUES (?, ?, ?, ?, 1,'1')";

    List<Object[]> deleteData = new ArrayList<>();
    List<Object[]> inputData = new ArrayList<>();

    for (int i = 0; i < dealPids.size(); i++) {
      Long dealPid = dealPids.get(i);
      Long bidderId = bidderIds.get(i);
      String syncTime = syncTimes.get(i);
      deleteData.add(new Object[] {dealPid, companyPid});
      inputData.add(new Object[] {dealPid, companyPid, bidderId, syncTime});
    }
    insertDealSyncCompanies();
    jdbcTemplateDbCore.batchUpdate(delete, deleteData);
    int[] updates = jdbcTemplateDbCore.batchUpdate(sql, inputData);
    return updates.length;
  }

  public String getDapPlayListId(String posName) {
    System.out.println("getDapPlayListId 2222" + posName);
    String sqlQuery =
        "SELECT v.playlist_id FROM position p JOIN placement_video v ON v.pid = p.pid WHERE p.name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {posName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public String getDapPlayerId(String posName) {
    System.out.println("getDapPlayerId 111" + posName);
    String sqlQuery =
        "SELECT v.player_id FROM position p JOIN placement_video v ON v.pid = p.pid WHERE p.name = ?";
    List<String> result =
        jdbcTemplateDbCore.queryForList(sqlQuery, new Object[] {posName}, String.class);
    return result.isEmpty() ? null : result.get(0);
  }

  public void insertDealDataForDv360DealSync(Long companyPid, Long dealPid) {
    log.debug("insertDealDataForDv360DealSync: {}, {}", companyPid, dealPid);

    String dealSql1 =
        "INSERT INTO deal (pid, version, id, floor, auction_type, status, description, start, stop, "
            + "created_by, creation_date, updated_on, visibility, priority_type, "
            + "placement_formula, auto_update, currency, all_sellers, all_bidders) "
            + "VALUES ('"
            + dealPid
            + "', '0', '1234567890987654321', '1.00000000', '4', '1', 'Test Deal', "
            + "'2021-03-05 00:00:00', NULL, '2', '2021-03-05 15:57:46', '2021-03-05 15:57:46', "
            + "0, '100', NULL, NULL, 'USD', 0, 0)";

    String ruleSql1 =
        "INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, "
            + "rule_type, seller_seat_pid) "
            + "VALUES ('654321', "
            + companyPid
            + ", '0', '1', '33334444555566667', NULL, '2021-03-05 15:57:46', "
            + "'2', NULL)";

    String dealRuleSql1 =
        "INSERT INTO deal_rule (deal_pid, rule_group_pid, version) "
            + "VALUES ('"
            + dealPid
            + "', '654321', '0')";

    String ruleTargetjson1 =
        "[{\"buyerCompany\":" + companyPid + ",\"buyerGroups\":[1],\"seats\":[\"12345\", \"1\"]}]";

    String ruleTargetSql1 =
        "INSERT INTO rule_target (version, status, match_type, target_type, data, rule_pid) "
            + "VALUES ('0', '1', '1', '22', '"
            + ruleTargetjson1
            + "', '654321')";

    String ruleMultiAdSizeTargetSql1 =
        "INSERT INTO rule_target (version, status, match_type, target_type, data, rule_pid) "
            + "VALUES ('0', '1', '1', '29', '100x100', '654321')";

    insertDealSyncCompanies();

    // order product
    jdbcTemplateDbCore.execute(dealSql1);
    jdbcTemplateDbCore.execute(ruleSql1);
    jdbcTemplateDbCore.execute(dealRuleSql1);
    jdbcTemplateDbCore.execute(ruleTargetSql1);
    jdbcTemplateDbCore.execute(ruleMultiAdSizeTargetSql1);
  }

  public int insertDeviceOs(Map<Long, String> data) {

    String deviceOsInsert = "INSERT INTO device_os (`pid`, `name`) VALUES (?, ?)";

    List<Object[]> inputData = new ArrayList<>();
    data.forEach(
        (pid, os) -> {
          inputData.add(new Object[] {pid, os});
        });

    int[] updates = jdbcTemplateDbCore.batchUpdate(deviceOsInsert, inputData);
    return updates.length;
  }

  public void insertExperimentRuleTestData() {
    String ruleSql =
        "INSERT INTO rule (pid, company_pid, version, status, name, description, last_update, rule_type, seller_seat_pid) "
            + "VALUES (500, NULL, '0', 1, 'ExperimentRule', NULL, now(), 4, NULL)";

    String ruleTargetSql =
        "INSERT INTO rule_target (pid, version, status, match_type, target_type, data, rule_pid) "
            + "VALUES (500, 0, 1, 1, 1, '{\"geosegments\":[{\"segmentId\":20989635,\"woeid\":23424744,"
            + "\"name\":\"Andorra (AD)\"}]}', 500)";

    String ruleIntendedActionSql =
        "INSERT INTO rule_intended_action (pid, rule_pid, action_type, action_data, version, last_update) "
            + "VALUES (500, 500, 1, '1', 0, now())";

    String experimentSql =
        "INSERT INTO experiment (pid, name, status, version, created_on, updated_on, start_date, "
            + "end_date, traffic_split_type, metric_tagging_enabled, description, rule_pid) "
            + "VALUES (1000, 'ExperimentRule', 1, 0, '2020-09-29 15:44:04', '2020-09-29 15:44:04', '2020-09-30 12:00:00', "
            + "'2020-11-30 12:00:00', 0, 1, 'test description', 500)";

    String experimentBucketSql =
        "INSERT INTO experiment_buckets (pid, id, experiment_pid, model_name, name, status, "
            + "version, created_on, updated_on, percentage) "
            + "VALUES (1000, 'bucket-1000', 1000, 'test model name', 'bucket-1000', 1, 0, '2020-09-29 17:44:04', '2020-09-30 12:00:00', 25)";

    jdbcTemplateDbCore.execute(ruleSql);
    jdbcTemplateDbCore.execute(ruleTargetSql);
    jdbcTemplateDbCore.execute(ruleIntendedActionSql);
    jdbcTemplateDbCore.execute(experimentSql);
    jdbcTemplateDbCore.execute(experimentBucketSql);
  }

  public int insertDealPublisher(Map<Long, Long> data) {

    String deviceOsInsert =
        "INSERT INTO deal_publisher (`deal_pid`, `pub_pid`, `version`) VALUES (?, ?, ?)";

    List<Object[]> inputData = new ArrayList<>();
    data.forEach(
        (deal_pid, pub_pid) -> {
          inputData.add(new Object[] {deal_pid, pub_pid, 1});
        });

    int[] updates = jdbcTemplateDbCore.batchUpdate(deviceOsInsert, inputData);
    return updates.length;
  }

  public void addFactCowboyTrafficRecords(List<FactCowboyTraffic> factCowboyTrafficList) {
    String sql =
        "INSERT INTO fact_cowboy_traffic (start, auction_run_hash_id, auction_run_id, seller_id, site_id, placement_id, deal_id, seat_id, app_bundle_id, bidder_id, hb_partner_pid, request_url, request_payload, response_payload) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    jdbcTemplateCrudDatawarehouse.batchUpdate(
        sql,
        factCowboyTrafficList.stream()
            .map(
                factCowboyTraffic ->
                    new Object[] {
                      factCowboyTraffic.getStart(),
                      factCowboyTraffic.getAuctionRunHashId(),
                      factCowboyTraffic.getAuctionRunId(),
                      factCowboyTraffic.getSellerId(),
                      factCowboyTraffic.getSiteId(),
                      factCowboyTraffic.getPlacementId(),
                      factCowboyTraffic.getDealId(),
                      factCowboyTraffic.getSeatId(),
                      factCowboyTraffic.getAppBundleId(),
                      factCowboyTraffic.getBidderId(),
                      factCowboyTraffic.getHbPartnerPid(),
                      factCowboyTraffic.getRequestUrl(),
                      factCowboyTraffic.getRequestPayload(),
                      factCowboyTraffic.getResponsePayload(),
                    })
            .collect(Collectors.toList()));
  }

  public void addFactCowboyExchangeRecords(List<FactCowboyExchange> factCowboyExchangeList) {
    String sql =
        "INSERT INTO fact_cowboy_exchange (start, auction_run_hash_id, auction_run_id, bidder_id, bidder_url, response_code, request_payload, response_payload, prebid_filter_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    jdbcTemplateCrudDatawarehouse.batchUpdate(
        sql,
        factCowboyExchangeList.stream()
            .map(
                factCowboyExchange ->
                    new Object[] {
                      factCowboyExchange.getStart(),
                      factCowboyExchange.getAuctionRunHashId(),
                      factCowboyExchange.getAuctionRunId(),
                      factCowboyExchange.getBidderId(),
                      factCowboyExchange.getBidderUrl(),
                      factCowboyExchange.getResponseCode(),
                      factCowboyExchange.getRequestPayload(),
                      factCowboyExchange.getResponsePayload(),
                      factCowboyExchange.getPrebidFilterReason()
                    })
            .collect(Collectors.toList()));
  }

  public void addFactCowboyExchangeDealRecords(
      List<FactCowboyExchangeDeal> factCowboyExchangeDealList) {
    String sql =
        "INSERT INTO fact_cowboy_exchange_deals (start, auction_run_hash_id, auction_run_id, bidder_id, deal_id) VALUES (?, ?, ?, ?, ?);";
    jdbcTemplateCrudDatawarehouse.batchUpdate(
        sql,
        factCowboyExchangeDealList.stream()
            .map(
                factCowboyExchangeDeal ->
                    new Object[] {
                      factCowboyExchangeDeal.getStart(),
                      factCowboyExchangeDeal.getAuctionRunHashId(),
                      factCowboyExchangeDeal.getAuctionRunId(),
                      factCowboyExchangeDeal.getBidderId(),
                      factCowboyExchangeDeal.getDealId()
                    })
            .collect(Collectors.toList()));
  }

  public void addPreBidFilterReasonRecords(Integer id, String name) {
    String sql =
        String.format(
            "INSERT INTO dim_pre_bid_filter_reason (id, name) VALUES ( "
                + id
                + ", '"
                + name
                + "');");
    jdbcTemplateCrudDatawarehouse.execute(sql);
  }

  public boolean sspScreenIdsExist(List<String> sspScreenIds) {
    String sellerScreensIdQuery =
        sspScreenIds.stream()
            .map(s -> "'" + s + "'")
            .collect(
                Collectors.joining(
                    ",", "SELECT count(*) from dooh_screen where ssp_screen_id IN (", ");"));

    return sspScreenIds.size()
        == jdbcTemplateDbCore.queryForObject(sellerScreensIdQuery, Integer.class);
  }

  public void initializeVenueTypes() {
    if (jdbcTemplateDbCore.queryForObject(
            "select count(*) from dooh_screen_venue_type", Integer.class)
        == 0) {
      jdbcTemplateDbCore.update(
          "INSERT INTO dooh_screen_venue_type(id, name, version, created_on) VALUES (10000000, 'baltimore.yahoo.office', 0, NOW())");
    }
  }
}
