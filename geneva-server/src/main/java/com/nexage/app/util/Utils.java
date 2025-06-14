package com.nexage.app.util;

import com.google.common.base.Splitter;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

  private static final String CHARSET = "UTF-8";

  /**
   * Encode an array of bytes.
   *
   * <p>NOTE: This method has been extracted from nexage-utils module.
   *
   * @param data bytes array
   * @return encoded valuex
   * @deprecated Use {@link #safeEncode(byte[])} instead
   */
  @Deprecated
  public static String encode(final byte[] data) {
    try {
      String tmp = new String(Base64.encodeBase64(data), CHARSET);
      // make it url safe
      tmp = tmp.replace('+', '-');
      tmp = tmp.replace('/', '_');
      tmp = tmp.replace('=', '.');
      return tmp;
    } catch (UnsupportedEncodingException uee) {
      log.error("Unexpected error encoding string", uee);
    }
    return null;
  }

  /**
   * Encode an array of bytes.
   *
   * @param data bytes array
   * @return encoded value
   */
  public static String safeEncode(final byte[] data) {
    return Base64.encodeBase64URLSafeString(data);
  }

  public static String encode(final String data) {
    try {
      return encode(data.getBytes(CHARSET));
    } catch (UnsupportedEncodingException uee) {
      log.error(String.format("Unexpected error encoding string \"%s\"", data), uee);
    }
    return null;
  }

  /**
   * Generic API to convert an array of String to linear representation.
   *
   * <p>NOTE: This method has been extracted from nexage-utils module.
   *
   * @param values values to be converted
   * @param separator final value separator between values
   */
  public static String convertArrayToString(String[] values, String separator) {
    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; values != null && i < values.length; i++) {
      stringBuilder.append(values[i]);
      stringBuilder.append(separator);
    }
    return stringBuilder.toString();
  }

  public static List<String> getListFromCommaSeparatedString(String list) {
    var splitter = Splitter.on(",").omitEmptyStrings().trimResults();
    List<String> outputList = new ArrayList<>();
    if (StringUtils.isNotBlank(list)) {
      Iterable<String> values = splitter.split(list);
      for (String s : values) {
        outputList.add(s);
      }
    }
    return outputList;
  }

  public static void validateCreativeSuccessRate(
      @NonNull BigDecimal creativeSuccessRateThreshold, boolean ignoreOptOut) {
    if (creativeSuccessRateThreshold.floatValue() <= 0
        || creativeSuccessRateThreshold.floatValue() > 100) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID);
    }
    if (ignoreOptOut) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_THRESHOLD_NOT_ALLOWED);
    }
  }
}
