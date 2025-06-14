package com.ssp.geneva.server.report.report.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MathUtil {

  public static Double roundDouble(Double value, int precision) {
    return value != null
        ? (double) Math.round(value * Math.pow(10.0, precision)) / Math.pow(10.0, precision)
        : null;
  }
}
