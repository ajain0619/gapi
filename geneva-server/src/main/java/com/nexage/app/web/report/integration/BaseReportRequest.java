package com.nexage.app.web.report.integration;

import com.nexage.app.security.UserContext;
import com.ssp.geneva.server.report.report.ReportRequest;
import com.ssp.geneva.server.report.report.exceptions.ReportDateParseException;
import com.ssp.geneva.server.report.report.util.DateUtil;
import com.ssp.geneva.server.report.report.util.ISO8601Util;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import javax.validation.constraints.NotNull;

public abstract class BaseReportRequest implements ReportRequest {

  public abstract class ReportMetadata
      implements com.ssp.geneva.server.report.report.ReportMetadata {

    @Override
    public String getEnvironment() {
      return "geneva";
    }

    public abstract String getReportName();

    @Override
    public String getReportType() {
      return "rpt";
    }
  }

  public static class ReportUser implements com.ssp.geneva.server.report.report.ReportUser {

    private final UserContext userContext;

    public ReportUser(UserContext userContext) {
      this.userContext = userContext;
    }

    @Override
    public Long getCompany() {
      /*
      at this point only seller seat user will have >1 company. As this
      reporting is going away, this is just a minimal effort to make a
      seller seat user see revenue report and render it in sellers
      dashboard in UI. If necessary more Reports should query for multiple companies.
      */
      return userContext.getCompanyPids().iterator().next();
    }

    @Override
    public Set<Long> getCompanies() {
      return userContext.getCompanyPids();
    }

    @Override
    public String getUserName() {
      return userContext.getUserId();
    }

    @Override
    public Long getUserPid() {
      return userContext.getPid();
    }

    @Override
    public boolean isNexageUser() {
      return userContext.isNexageUser();
    }
  }

  protected ReportUser reportUser;

  @NotNull protected String start;
  @NotNull protected String stop;

  @Override
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    try {
      Date d = ISO8601Util.parse(start);
      this.start = DateUtil.format(d);
    } catch (ParseException e) {
      throw new ReportDateParseException("Unable to parse Date to ISO 8601 format");
    }
  }

  @Override
  public String getStop() {
    return stop;
  }

  public final void setStop(String stop) {
    try {
      Date d = ISO8601Util.parse(stop);

      Calendar cal = new GregorianCalendar();
      cal.setTime(d);
      if (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) == 59) {

        cal.add(Calendar.DAY_OF_MONTH, 1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        d = cal.getTime();
      }

      this.stop = DateUtil.format(d);
    } catch (ParseException e) {
      throw new ReportDateParseException("Unable to parse Date to ISO 8601 format");
    }
  }

  @Override
  public ReportUser getReportUser() {
    return reportUser;
  }

  public void setReportUser(ReportUser reportUser) {
    this.reportUser = reportUser;
  }
}
