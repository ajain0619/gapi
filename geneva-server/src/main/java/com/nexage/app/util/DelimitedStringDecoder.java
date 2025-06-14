package com.nexage.app.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DelimitedStringDecoder {

  /**
   * Given a string that is of the form {value1|value2,...}, turn it into a List of Longs.
   *
   * @param str The string that will be turned into a list of Longs
   * @return An List<Long> containing the values found in the string
   */
  public static List<Long> decodeString(String str) {
    List<Long> decodedString = new ArrayList<>();
    if (StringUtils.isNotBlank(str)) {
      decodedString =
          Arrays.stream(str.split("\\|")).map(Long::parseLong).collect(Collectors.toList());
    }
    return decodedString;
  }
}
