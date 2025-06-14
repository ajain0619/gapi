package com.nexage.app.services.impl.queries;

import com.nexage.admin.dw.util.ReportDefEnums;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class VerticaQueries implements DWQueries {

  @Override
  public StringBuilder buildSQLforMetrics(ReportDefEnums.Interval interval) {

    StringBuilder query = new StringBuilder("select ");

    switch (interval) {
      case DAILY:
        query
            .append("DATE(start) as ts, ")
            .append(queryBodyForMetrics())
            .append("GROUP BY  DATE(start)");
        break;
      case WEEKLY:
        query
            .append("GREATEST(TIMESTAMP_TRUNC(start, 'DAY'), MIN(start)) as ts, ")
            .append(queryBodyForMetrics())
            .append("GROUP BY  TIMESTAMP_TRUNC(start, 'DAY')");
        break;
      case MONTHLY:
        query
            .append(
                "TO_TIMESTAMP(YEAR(start) || '-' || MONTH(start) || '-' || '1', 'YYYY-MM-DD') as ts, ")
            .append(queryBodyForMetrics())
            .append("GROUP BY YEAR(start), MONTH(start)");
        break; // Hourly is not supported, and in the previous incarnation it produced a totally
        // meaningless result
      case HOURLY:
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HOURLY_DRILLDOWN_NOT_ENABLED);
      default:
        break;
    }

    log.debug("metrics query: {}", query.toString());
    return query;
  }

  private StringBuilder queryBodyForMetrics() {
    return new StringBuilder()
        .append("sum(ads_requested_site) as requests, ")
        .append("sum(ads_served) as served, ")
        .append("sum(ads_delivered) as delivered,")
        .append("sum(ads_clicked) as clicks, ")
        .append("sum(revenue) - sum(revenue_net) as revenue,")
        .append("CASE WHEN sum(ads_requested_site) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN coalesce((sum(ads_served) / sum(ads_requested_site)) * 100, 0) > 100 THEN 100 ")
        .append(
            "ELSE coalesce((sum(ads_served) / sum(ads_requested_site)) * 100, 0) END END as fillrate,")
        .append("CASE WHEN sum(ads_delivered) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN coalesce((sum(ads_clicked) / sum(ads_delivered)) * 100, 0) > 100 THEN 100 ")
        .append("ELSE coalesce((sum(ads_clicked) / sum(ads_delivered)) * 100, 0) END END as ctr,")
        .append("CASE WHEN sum(ads_delivered) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN sum(ads_delivered) > 0 THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_delivered)) * 1000 ELSE 0 END END as ecpm,")
        .append("CASE WHEN sum(ads_requested_site) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN sum(ads_requested_site) > 0 THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_requested_site)) * 1000 ELSE 0 END END as rpm")
        .append(" from fact_revenue_adnet_vw_daily")
        .append(" where publisher_id = ?")
        .append(" and tag_monetization in (1,-1)")
        .append(" and start >= ?")
        .append(" and start < ?");
  }

  @Override
  public StringBuilder buildSQLforAdSourceMetrics(ReportDefEnums.Interval interval) {
    StringBuilder query = new StringBuilder("select ");

    switch (interval) {
      case DAILY:
        query
            .append("DATE(start) as ts, ")
            .append(queryBodyForAdSourceMetrics())
            .append("GROUP BY  DATE(start)");
        break;
      case WEEKLY:
        query
            .append("GREATEST(TIMESTAMP_TRUNC(start, 'DAY'), MIN(start)) as ts, ")
            .append(queryBodyForAdSourceMetrics())
            .append("GROUP BY  TIMESTAMP_TRUNC(start, 'DAY')");
        break;
      case MONTHLY:
        query
            .append(
                "TO_TIMESTAMP(YEAR(start) || '-' || MONTH(start) || '-' || '1', 'YYYY-MM-DD') as ts, ")
            .append(queryBodyForAdSourceMetrics())
            .append("GROUP BY YEAR(start), MONTH(start)");
        break; // Hourly is not supported, and in the previous incarnation it produced a totally
        // meaningless result
      case HOURLY:
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HOURLY_DRILLDOWN_NOT_ENABLED);
      default:
        break;
    }

    log.debug("metrics query: {}", query.toString());
    return query;
  }

  private StringBuilder queryBodyForAdSourceMetrics() {
    StringBuilder query = new StringBuilder();
    query.append("sum(ads_requested_adnet) as requests,");
    query
        .append("sum(ads_served) as served, ")
        .append("sum(ads_delivered) as delivered,")
        .append("sum(ads_clicked) as clicks, ")
        .append("sum(revenue) - sum(revenue_net) as revenue,")
        .append("CASE WHEN sum(ads_requested_adnet) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN coalesce(ROUND((sum(ads_served) / sum(ads_requested_adnet)) * 100, 4.0), 0) > 100 THEN 100 ")
        .append(
            "ELSE coalesce(ROUND((sum(ads_served) / sum(ads_requested_adnet)) * 100, 4.0), 0) END END as fillrate,")
        .append("CASE WHEN sum(ads_delivered) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN coalesce(ROUND((sum(ads_clicked) / sum(ads_delivered)) * 100, 4.0), 0) > 100 THEN 100 ")
        .append(
            "ELSE coalesce(ROUND((sum(ads_clicked) / sum(ads_delivered)) * 100, 4.0), 0) END END as ctr,")
        .append("CASE WHEN sum(ads_delivered) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN sum(ads_delivered) > 0 THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_delivered)) * 1000 ELSE 0 END END as ecpm,")
        .append("CASE WHEN sum(ads_requested_adnet) = 0 THEN 0 ELSE ")
        .append(
            "CASE WHEN sum(ads_requested_adnet) > 0 THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_requested_adnet)) * 1000 ELSE 0 END END as rpm")
        .append(" from fact_revenue_adnet_vw_daily")
        .append(" where publisher_id = ?")
        .append(" and adnet_id = ?")
        .append(" and (site_id=? or ? = 0)")
        .append(" and (zone=? or ?::varchar is null)")
        .append(" and (tag_id=? or ? = 0)")
        .append(" and tag_monetization in (1,-1)")
        .append(" and start >= ?")
        .append(" and start < ?");

    return query;
  }
}
