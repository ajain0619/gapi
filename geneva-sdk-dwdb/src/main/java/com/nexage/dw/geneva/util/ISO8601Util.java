package com.nexage.dw.geneva.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class ISO8601Util {

  private static final DateTimeFormatter dateAndTimeFormat =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
  private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static Date parse(String date) throws ParseException {
    try {
      return Date.from(Instant.from(dateAndTimeFormat.parse(date)));
    } catch (DateTimeParseException ex) {
      LocalDate localDate = LocalDate.parse(date, dateFormat);
      return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
  }

  public static String format(Date date) {
    return dateAndTimeFormat.format(
        ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
  }
}
