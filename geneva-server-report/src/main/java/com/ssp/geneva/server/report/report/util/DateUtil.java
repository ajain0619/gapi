package com.ssp.geneva.server.report.report.util;

import com.ssp.geneva.common.model.report.ReportType;
import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;
import com.ssp.geneva.server.report.report.util.ReportDefEnums.DateRange;
import com.ssp.geneva.server.report.report.util.ReportDefEnums.Interval;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class that handles various date ranges supported in reports. The methods return
 * start/stop timestamps based on the given daterange or other inputs (like days, weeks, etc). The
 * start/stop timestamp definition and handling are totally based on start/stop in datawarehouse
 * facts.
 */
@Log4j2
public final class DateUtil {

  public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
  public static final DateTimeFormatter DATETIME_FORMAT =
      DateTimeFormatter.ofPattern(DATETIME_FORMAT_STRING);
  public static final DateTimeFormatter DATETIME_FORMAT_PARSE =
      DateTimeFormatter.ofPattern(DATETIME_FORMAT_STRING + "[.S]");

  public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
  public static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);

  public static final String MONTH_FORMAT_STRING = "yyyy-MM";
  public static final DateTimeFormatter MONTH_FORMAT =
      DateTimeFormatter.ofPattern(MONTH_FORMAT_STRING);

  public static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");

  public static final DateTimeFormatter DATETIME_TIME_ZONE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

  public static final String START = "start";

  public static final String STOP = "stop";

  private DateUtil() {
    // private constructor to enforce non-instantiability
  }

  /**
   * Utility function to round up the time part of the Date object. Return a date with time set to
   * 00:00:00, date part is unchanged.
   *
   * @param date
   * @return Date
   */
  public static Date roundTimeToSeconds(Date date) {
    if (date != null) {
      Calendar cal = getCalendar();
      cal.setTime(date);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      date = cal.getTime();
    }
    return date;
  }

  /**
   * Return a date with time set to 00:00:00, date part is unchanged.
   *
   * @param date
   * @return Date
   */
  public static Date getStartDate(Date date) {
    return roundTimeToSeconds(date);
  }

  /**
   * Return the stop datetime for the given date. It is usually the next day with time rounded to
   *
   * @param date
   * @return Date
   */
  public static Date getStopDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_YEAR, 1);
    return roundTimeToSeconds(cal.getTime());
  }

  /**
   * Utility function to round up all the time part but hour of the Date object.
   *
   * @param date
   * @return Date
   */
  public static Date roundTimeToHour(Date date) {
    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      date = cal.getTime();
    }
    return date;
  }

  public static Date getStartDateRelativeToDays(int days) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_YEAR, days);
    Date d = roundTimeToSeconds(cal.getTime());
    return d;
  }

  public static Date getEndDateRelativeToDays(int days) {
    return getStartDateRelativeToDays(days + 1);
  }

  /** Get last week same datetime for given date */
  public static String getLastWeekDateRelativeToGivenDate(Date stopDate) {
    Calendar c = getCalendar();
    c.setTime(stopDate);
    c.add(Calendar.DAY_OF_YEAR, -7);
    return format(c.getTime());
  }

  public static String[] getPreviousRelativeDateRange(Date start, Date stop) {
    LocalDate startlocal =
        LocalDateTime.ofInstant(
                Instant.ofEpochMilli(start.getTime()), ZoneId.of("America/New_York"))
            .toLocalDate();
    LocalDate endlocal =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(stop.getTime()), ZoneId.of("America/New_York"))
            .toLocalDate();
    int days = Period.between(startlocal, endlocal).getDays();
    return new String[] {
      startlocal
          .minus(days, ChronoUnit.DAYS)
          .atStartOfDay()
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
      format(start)
    };
  }

  /** Get current (start and stop) intervals for intra-day key metrics */
  public static Map<String, Date> getIntraDayInterval() {
    Map<String, Date> dates = new HashMap<String, Date>();
    Calendar c = getCalendar();

    // Results till last 10 minutes
    c.add(Calendar.MINUTE, -10);
    dates.put(STOP, c.getTime());

    // Last week same time
    c.add(Calendar.WEEK_OF_YEAR, -1);
    dates.put(START, c.getTime());
    return dates;
  }

  /** Get current (start and stop) intervals for inter-day key metrics */
  public static Map<String, Date> getInterDayInterval() {
    Map<String, Date> dates = new HashMap<String, Date>();

    // Results till yesterday
    Date stop = getStartDate(getNow());
    dates.put("stop", stop);

    // Last week same time
    Calendar c = getCalendar();
    c.setTime(stop);
    c.add(Calendar.WEEK_OF_YEAR, -1);
    dates.put(START, c.getTime());
    return dates;
  }

  public static Date getStartDateRelativeToWeeks(int weekNr) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.WEEK_OF_YEAR, weekNr);
    cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
    return roundTimeToSeconds(cal.getTime());
  }

  // The end date will always be midnight of first day relative to weekNr + 1.
  // This is based on how start/stop timestamps handled in our datawarehouse facts.
  public static Date getEndDateRelativeToWeeks(int weekNr) {
    return getStartDateRelativeToWeeks(weekNr + 1);
  }

  public static Date getThisMonthStartDate() {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH, 1);
    return roundTimeToSeconds(c.getTime());
  }

  public static Date getLastMonthStartDate() {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.MONTH, -1);
    c.set(Calendar.DATE, 1);
    return roundTimeToSeconds(c.getTime());
  }

  // The end date for last month will always be midnight of day next to last day of the month.
  // i.e. end date for May will be 2011-06-01 00:00:00.
  // This is based on the start/stop timestamps in our datawarehouse facts.
  public static Date getLastMonthStopDate() {
    return getThisMonthStartDate();
  }
  /**
   * Common API to get current time. Facilitates changing TZ for all persistent dates.
   *
   * @return Date
   */
  public static Date getNow() {
    return getCalendar().getTime();
  }

  /**
   * Get the current time as Calendar object
   *
   * @return Calendar
   */
  public static Calendar getCalendar() {
    return Calendar.getInstance();
  }

  @SuppressWarnings("unused")
  private void logDate(String msg, Date d) {
    if (log.isDebugEnabled()) log.debug(msg + " : " + format(d));
  }

  /**
   * Get all the dates between given start and stop timestamp. The dates are inclusive of start and
   * stop based on their timestamp. The stop date with timestamp 00:00:00 will not be included in
   * the returned list.
   *
   * @param start
   * @param stop
   * @return Map of strings to string
   * @throws ParseException
   */
  public static Map<String, String> getAllDatesBetween(String start, String stop)
      throws ParseException {
    Map<String, String> dates = new HashMap<String, String>();
    Calendar sCal = Calendar.getInstance();
    Calendar eCal = Calendar.getInstance();
    sCal.setTime(parse(start));
    eCal.setTime(parse(stop));
    while (eCal.after(sCal)) {
      dates.put(format((sCal.getTime()), DATE_FORMAT_STRING), format((sCal.getTime())));
      sCal.add(Calendar.DAY_OF_YEAR, 1);
    }
    return dates;
  }

  /**
   * Get all the weeks between given start and stop timestamp. The weeks are inclusive of start and
   * stop based on their timestamp. The week always starts on sunday. Map with week number and
   * respective date. In Mysql, week number start with 0 and in java it starts with 1. So week
   * number returned here is tweaked to match with Mysql.
   *
   * @param start
   * @param stop
   * @return map of string to string
   * @throws ParseException
   */
  public static Map<String, String> getAllWeeksBetween(String start, String stop)
      throws ParseException {
    Map<String, String> dates = new HashMap<String, String>();
    Calendar sCal = Calendar.getInstance();
    Calendar eCal = Calendar.getInstance();
    sCal.setTime(parse(start));
    eCal.setTime(parse(stop));
    int initialWeekOfYear = sCal.get(Calendar.WEEK_OF_YEAR);
    int nextWeekOfYear = 300; // set ridiculously high
    int year = sCal.get(Calendar.YEAR);
    while (eCal.after(sCal)) {
      if (nextWeekOfYear < initialWeekOfYear) {
        // fix bug where, if week increments from last week of year to first week of next year, year
        // was not incremented
        year = year + 1;
      }
      // Minus 1 to match with Mysql
      dates.put(format(sCal.getTime(), DATE_FORMAT_STRING), format(sCal.getTime()));
      // Number of days left in this week
      int days = 7 - sCal.get(Calendar.DAY_OF_WEEK);
      // Advance to first day in next week
      initialWeekOfYear = sCal.get(Calendar.WEEK_OF_YEAR);
      sCal.add(Calendar.DAY_OF_YEAR, days + 1);
      nextWeekOfYear = sCal.get(Calendar.WEEK_OF_YEAR);
    }
    return dates;
  }

  /**
   * Get all the months between given start and stop timestamp. The months are inclusive of start
   * and stop based on their timestamp. Map with month number and respective date. In Mysql, month
   * number start with 1 and in java it starts with 0. So month number returned here is tweaked to
   * match with Mysql.
   *
   * @param start
   * @param stop
   * @return map of strings to string
   * @throws ParseException
   */
  public static Map<String, String> getAllMonthsBetween(String start, String stop)
      throws ParseException {
    Map<String, String> dates = new HashMap<String, String>();
    Calendar sCal = Calendar.getInstance();
    Calendar eCal = Calendar.getInstance();
    sCal.setTime(parse(start));
    sCal.set(Calendar.DAY_OF_MONTH, 1);
    eCal.setTime(parse(stop));
    while (eCal.after(sCal)) {
      dates.put(format((sCal.getTime()), DATE_FORMAT_STRING), format((sCal.getTime())));
      sCal.add(Calendar.MONTH, 1);
      sCal.set(Calendar.DAY_OF_MONTH, 1);
    }
    return dates;
  }

  public static Map<String, String> getAll24HoursForDate(String date) throws ParseException {
    Map<String, String> dates = new HashMap<String, String>();
    for (int i = 0; i < 24; i++) {
      String formatted = i < 10 ? String.format("%02d", i) : String.valueOf(i);
      dates.put(String.valueOf(i), date.replaceFirst("00", formatted));
    }
    return dates;
  }

  public static String format(@Nonnull Date date) {
    return DATETIME_FORMAT.format(
        ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
  }

  public static String format(@Nonnull Date date, String format) {
    switch (format) {
      case DATE_FORMAT_STRING:
        return DATE_FORMAT.format(
            ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
      case DATETIME_FORMAT_STRING:
        return DATETIME_FORMAT.format(
            ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
      case MONTH_FORMAT_STRING:
        return MONTH_FORMAT.format(
            ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
      default:
        return null;
    }
  }

  public static Date parse(String date) throws ParseException {
    LocalDateTime ldt = LocalDateTime.parse(date, DATETIME_FORMAT_PARSE);
    return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date parseFromMonthFormat(String date) throws ParseException {
    // This method call at the moment assumes that for a given month, we are starting from the
    // beginning. This
    // is consistent with the previous implementation
    return Date.from(
        YearMonth.parse(date).atDay(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date parseFromDateFormat(String date) throws ParseException {
    return Date.from(
        LocalDate.parse(date).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static String fomatDateToGivenTZ(Date date, final TimeZone tz) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_STRING);
    sdf.setTimeZone(tz);
    return sdf.format(date);
  }

  // return a date time formatted string for the input date string
  // input string could be date time or just date formatted
  public static String getDateTimeString(String dateString) {
    String returnString = dateString;
    if (dateString.contains(":")) {
      returnString = dateString;
    } else {
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STRING);
      Date date;
      try {
        date = sdf.parse(dateString);
        returnString =
            DATETIME_FORMAT.format(
                ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
      } catch (ParseException e) {
        log.warn("error formatting date string: " + dateString);
      }
    }

    return returnString;
  }

  public static Map<String, String> getDatesBetweenForInterval(
      String start, String stop, Interval interval) {
    Map<String, String> dates = null;
    try {
      switch (interval) {
        case HOURLY:
          dates = getAll24HoursForDate(start);
          break;
        case DAILY:
          dates = getAllDatesBetween(start, stop);
          break;
        case WEEKLY:
          dates = getAllWeeksBetween(start, stop);
          break;
        case MONTHLY:
          dates = getAllMonthsBetween(start, stop);
          break;
        default:
          dates = new HashMap<>();
          break;
      }
    } catch (ParseException e) {
      log.warn(
          "ParseException (filling empty intervals compromised) while trying to parse dates [ "
              + start
              + " and "
              + stop
              + "] for interval: "
              + interval);
    }
    return dates;
  }

  /**
   * Daily - Current day starting from 12.00am till now Weekly - Last 7 days including today (till
   * now) Last week - Last Sunday to Saturday Monthly - Last 30 days including today (till now) Last
   * month - Calendar month before the current month
   */
  public static Map<String, String> getDateRanges(String dateRange, ReportType type) {
    Map<String, String> dateRanges = new HashMap<>();
    String start = null;
    String stop = null;

    if (DateRange.TODAY.getRange().equals(dateRange)) {
      // Midnight till now
      start = format(roundTimeToSeconds(getNow()));
      stop = format(getNow());
    } else if (DateRange.THIS_WEEK.getRange().equals(dateRange)) {
      Calendar c = getCalendar();
      int thisWeek = c.get(Calendar.WEEK_OF_YEAR);
      start = format(getStartDateRelativeToWeeks(thisWeek));
      // Since the revenue data is available only till y'day, we want to restrict
      // the query till yday (i.e., midnight of today) for all the attributes involved in revenue
      // report
      // i.e. including traffic data though it is available till now.
      if (type.name().contains("REVENUE")) {
        stop = format(roundTimeToSeconds(getNow()));
      } else {
        stop = format(getNow());
      }
    } else if (DateRange.LAST_WEEK.getRange().equals(dateRange)) {
      Calendar c = getCalendar();
      int lastWeek = c.get(Calendar.WEEK_OF_YEAR) - 1;
      start = format(getStartDateRelativeToWeeks(lastWeek));
      stop = format(getEndDateRelativeToWeeks(lastWeek));
    } else if (DateRange.THIS_MONTH.getRange().equals(dateRange)) {
      start = format(getThisMonthStartDate());
      // Since the revenue data is available only till y'day, we want to restrict
      // the query till yday for all the attributes involved in revenue report
      // i.e. including traffic data though it is available till now.
      if (type.name().contains("REVENUE")) {
        stop = format(roundTimeToSeconds(getNow()));
      } else {
        stop = format(getNow());
      }
    } else if (DateRange.LAST_MONTH.getRange().equals(dateRange)) {
      start = format(getLastMonthStartDate());
      stop = format(getLastMonthStopDate());
    }
    dateRanges.put(START, start);
    dateRanges.put(STOP, stop);
    return dateRanges;
  }

  public static void main(String[] args) throws ParseException {
    //		 System.out.println("<------- Date ranges for revenue reports --------->");
    //		 System.out.println("This Week: " + getDateRanges(DateRange.THIS_WEEK.getRange(),
    // ReportType.REVENUE_BY_ADSOURCE));
    //		 System.out.println("This Month: " + getDateRanges(DateRange.THIS_MONTH.getRange(),
    // ReportType.REVENUE_BY_ADSOURCE));
    //		 System.out.println("Last Week: " + getDateRanges(DateRange.LAST_WEEK.getRange(),
    // ReportType.REVENUE_BY_ADSOURCE));
    //		 System.out.println("Last Month: " + getDateRanges(DateRange.LAST_MONTH.getRange(),
    // ReportType.REVENUE_BY_ADSOURCE));
    //
    //		 Map<String, String> dates = getDateRanges(DateRange.LAST_WEEK.getRange(),
    // ReportType.REVENUE_BY_ADSOURCE);
    //		 System.out.println("All dates between dates [" + dates + "] :" +
    // getAllDatesBetween(dates.get(START), dates.get(STOP)));
    //		 System.out.println("");
    //
    //		 System.out.println("<------- Date ranges for other reports --------->");
    //		 System.out.println("Today: " + getDateRanges(DateRange.TODAY.getRange(),
    // ReportType.TRAFFIC_BY_ADSOURCE));
    //		 System.out.println("This Week: " + getDateRanges(DateRange.THIS_WEEK.getRange(),
    // ReportType.TRAFFIC_BY_ADSOURCE));
    //		 System.out.println("This Month: " + getDateRanges(DateRange.THIS_MONTH.getRange(),
    // ReportType.TRAFFIC_BY_ADSOURCE));
    //		 System.out.println("Last Week: " + getDateRanges(DateRange.LAST_WEEK.getRange(),
    // ReportType.TRAFFIC_BY_ADSOURCE));
    //		 System.out.println("Last Month: " + getDateRanges(DateRange.LAST_MONTH.getRange(),
    // ReportType.TRAFFIC_BY_ADSOURCE));
    //
    //		 dates = getDateRanges(DateRange.THIS_WEEK.getRange(), ReportType.TRAFFIC_BY_ADSOURCE);
    //		 System.out.println("All dates between dates [" + dates + "] :" +
    // getAllDatesBetween(dates.get(START), dates.get(STOP)));
    //
    //		 dates = getDateRanges(DateRange.THIS_MONTH.getRange(), ReportType.TRAFFIC_BY_ADSOURCE);
    //		 System.out.println("All weeks between dates [" + dates + "] :" +
    // getAllWeeksBetween(dates.get(START), dates.get(STOP)));
    //
    //		 dates = getDateRanges(DateRange.THIS_WEEK.getRange(), ReportType.TRAFFIC_BY_ADSOURCE);
    //		 System.out.println("All months between dates [2011-01-11 17:45:00, 2011-12-15 17:45:00] :"
    // + getAllMonthsBetween("2011-01-11 17:45:00", "2011-12-15 17:45:00"));
    //
    //		 System.out.println("Intra day: " + getIntraDayInterval());
    //		 System.out.println("Inter day: " + getInterDayInterval());
    //		 System.out.println(getAll24HoursForDate("2010-11-13 00:00:00"));
    //		 System.out.println(getAllDatesBetween("2010-11-13 00:00:00", "2012-02-13 00:00:00"));
    //		 System.out.println(getAllWeeksBetween("2010-11-13 00:00:00", "2012-02-13 00:00:00"));
    //		 System.out.println(getAllMonthsBetween("2010-11-13 00:00:00", "2012-02-13 00:00:00"));

    //		 Calendar c = Calendar.getInstance();
    //		 c.set(Calendar.YEAR, 2011);
    //		 c.set(Calendar.MONTH, Calendar.JANUARY);
    //		 c.set(Calendar.DAY_OF_YEAR, 1);
    //		 System.out.println(c.get(Calendar.WEEK_OF_YEAR));
    //
    //		 System.out.println("2011-04-01 00:00:00".equals("2011-04-01 00:00:00"));

    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
    System.out.println(format(c.getTime()));
    System.out.println(fomatDateToGivenTZ(c.getTime(), TimeZone.getTimeZone("America/New_York")));
    System.out.println(
        fomatDateToGivenTZ(c.getTime(), TimeZone.getTimeZone("America/Los_Angeles")));
  }

  public static Map<String, String> getZeroDatesMap(
      ReportRequest request, ReportDimension dimension) {
    if (dimension != null) {
      ReportDefEnums.ApiInterval apiInterval =
          ReportDefEnums.ApiInterval.getInterval(dimension.toString());
      if (apiInterval != null) {
        return DateUtil.getDatesBetweenForInterval(
            request.getStart(), request.getStop(), apiInterval.translateInterval());
      }
    }
    return new HashMap<>();
  }

  /**
   * Check if specified date is in the past. This method uses default JVM time zone for comparison.
   * If {@code date} is null, this method will return false.
   *
   * @param date date to check
   * @return {@code true} if date is in the past, {@code false} otherwise
   */
  public static boolean isInPast(Date date) {
    return (date != null && date.toInstant().isBefore(Instant.now()));
  }
}
