package com.nexage.app.util.placement;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlacementValidMemoGenerator {

  public static final String DEFAULT_MEMO_NAME = "pl";
  public static final String UI_UNDEFINED_MEMO_NAME = "undefined";

  /**
   * PlacementValidMemoGenerator
   *
   * <p>note: "pl" is the default placement memo prefix of one is not provided. "undefined" is
   * passed in from the geneva ui when no placement memo is provided
   *
   * @param siteId site id.
   * @param memo placement memo.
   * @return {@link String} New placement memo with the format "{memo}-s{siteId}-t{time}" if a memo
   *     and siteId are provided.
   * @return {@link String} New placement memo with the format "{memo}" if a negative siteId is
   *     provided.
   * @return {@link String} New placement memo with the format "pl-s{siteId}-t{time}" if a null,
   *     empty, or "undefined" memo is provided.
   * @return {@link String} New placement memo with the format "pl" if a null, empty, or "undefined"
   *     memo is provided and a negative siteId is provided.
   */
  public static String generate(Long siteId, String memo) {
    /* prefix for placement memo */
    String plpfx =
        (isNull(memo) || memo.length() <= 0 || UI_UNDEFINED_MEMO_NAME.equals(memo))
            ? DEFAULT_MEMO_NAME
            : memo.replaceAll("(-s\\d+-t\\d+)+$", "");
    if (isNull(siteId) || siteId <= 0) {
      return plpfx;
    }
    long timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    return String.format("%s-s%d-t%d", plpfx, siteId, timestamp);
  }
}
