package com.nexage.admin.core.util;

import java.util.Date;
import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeZoneUtils {

  private static final TimeZone TIME_ZONE = TimeZone.getDefault();

  /**
   * Get a date with the time zone offset from UTC added.
   *
   * @param date date
   * @return date with utc time zone offset added
   */
  public static Date getDateWithUtcTimeZoneOffsetAdded(Date date) {
    return (null == date ? null : new Date(date.getTime() + getUtcOffsetInMils(date)));
  }

  /**
   * Get a date with the time zone offset from UTC removed.
   *
   * @param date date
   * @return date with utc time zone offset removed
   */
  public static Date getDateWithUtcTimeZoneOffsetRemoved(Date date) {
    return (null == date ? null : new Date(date.getTime() - getUtcOffsetInMils(date)));
  }

  /**
   * Get the time zone offset from UTC.
   *
   * @param date - date
   * @return utc offset in milliseconds
   */
  public static long getUtcOffsetInMils(Date date) {
    return TIME_ZONE.getOffset(date.getTime());
  }
}
