package com.nexage.admin.core.model;

import com.nexage.admin.core.batch.BatchColumn;
import com.nexage.admin.core.batch.callback.CallbackStatementSetter;
import java.util.Arrays;
import java.util.List;

public class DoohScreenBatch {

  private DoohScreenBatch() {}

  public static final List<BatchColumn<DoohScreen>> COLUMNS =
      Arrays.asList(
          new BatchColumn<>(
              "ssp_screen_id",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(1, doohScreen.getSspScreenId()))),
          new BatchColumn<>(
              "seller_pid",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setLong(2, doohScreen.getSellerPid()))),
          new BatchColumn<>(
              "seller_screen_id",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(3, doohScreen.getSellerScreenId()))),
          new BatchColumn<>(
              "seller_screen_name",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(4, doohScreen.getSellerScreenName()))),
          new BatchColumn<>(
              "network",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(5, doohScreen.getNetwork()))),
          new BatchColumn<>(
              "venue_type_id",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setInt(6, doohScreen.getVenueTypeId()))),
          new BatchColumn<>(
              "location_type",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(7, doohScreen.getLocationType()))),
          new BatchColumn<>(
              "latitude",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setDouble(8, doohScreen.getLatitude()))),
          new BatchColumn<>(
              "longitude",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setDouble(9, doohScreen.getLongitude()))),
          new BatchColumn<>(
              "country",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(10, doohScreen.getCountry()))),
          new BatchColumn<>(
              "state",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(11, doohScreen.getState()))),
          new BatchColumn<>(
              "dma",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(12, doohScreen.getDma()))),
          new BatchColumn<>(
              "city",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(13, doohScreen.getCity()))),
          new BatchColumn<>(
              "zip",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(14, doohScreen.getZip()))),
          new BatchColumn<>(
              "address",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(15, doohScreen.getAddress()))),
          new BatchColumn<>(
              "bearing",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(16, doohScreen.getBearing()))),
          new BatchColumn<>(
              "link",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(17, doohScreen.getLink()))),
          new BatchColumn<>(
              "ad_types",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(18, doohScreen.getAdTypes()))),
          new BatchColumn<>(
              "min_ad_duration",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setInt(19, doohScreen.getMinAdDuration()))),
          new BatchColumn<>(
              "resolution",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(20, doohScreen.getResolution()))),
          new BatchColumn<>(
              "accepted_ad_sizes",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(21, doohScreen.getAcceptedAdSizes()))),
          new BatchColumn<>(
              "aspect_ratio",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(22, doohScreen.getAspectRatio()))),
          new BatchColumn<>(
              "avg_dwell_time",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setObject(23, doohScreen.getAvgDwellTime()))),
          new BatchColumn<>(
              "avg_impression_multiplier",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setDouble(24, doohScreen.getAvgImpressionMultiplier()))),
          new BatchColumn<>(
              "avg_weekly_impressions",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setDouble(25, doohScreen.getAvgWeeklyImpressions()))),
          new BatchColumn<>(
              "avg_daily_impressions",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setObject(26, doohScreen.getAvgDailyImpressions()))),
          new BatchColumn<>(
              "avg_monthly_impressions",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setObject(27, doohScreen.getAvgMonthlyImpressions()))),
          new BatchColumn<>(
              "avg_cpm",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setObject(28, doohScreen.getAvgCpm()))),
          new BatchColumn<>(
              "restrictions",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setString(29, doohScreen.getRestrictions()))),
          new BatchColumn<>(
              "max_ad_duration",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setInt(30, doohScreen.getMaxAdDuration()))),
          new BatchColumn<>(
              "floor",
              CallbackStatementSetter.checkSqlEx(
                  (ps, doohScreen) -> ps.setBigDecimal(31, doohScreen.getFloorPrice()))));
}
