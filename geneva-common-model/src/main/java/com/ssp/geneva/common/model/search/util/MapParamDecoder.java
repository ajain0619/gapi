package com.ssp.geneva.common.model.search.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapParamDecoder {

  private static final String SYMBOLS_TO_REMOVE_REGEX = "[\\[\\](){}]";
  private static final Pattern KEY_VALUE_SEPARATOR_PATTERN = Pattern.compile("\\s*=\\s*");
  private static final String VALUE_SEPARATOR_REGEX = "\\s*[|]\\s*";
  private static final char SEPARATOR = ',';

  /**
   * Given a string that is of the form {key1=value1|value2, key2=value1|value2|value3, ...}, turn
   * it into a map.
   *
   * @param str The string that will be turned into a map
   * @return An ImmutableMap containing the keys with ImmutableList of values
   *     {key1=[value1,value2],key2=[value1,value2,value3],...}
   */
  public static MultiValueMap<String, String> decodeQueryParam(String str) {
    MultiValueMap<String, String> result = new LinkedMultiValueMap<>();

    if (str == null) {
      return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    Map<String, String> map;
    try {
      map =
          Splitter.on(SEPARATOR)
              .trimResults()
              .omitEmptyStrings()
              .withKeyValueSeparator(Splitter.on(KEY_VALUE_SEPARATOR_PATTERN))
              .split(str.replaceAll(SYMBOLS_TO_REMOVE_REGEX, StringUtils.EMPTY));
    } catch (IllegalArgumentException e) {
      return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    map.forEach(
        (key, value) ->
            Arrays.asList(value.split(VALUE_SEPARATOR_REGEX)).forEach(v -> result.add(key, v)));
    return CollectionUtils.unmodifiableMultiValueMap(result);
  }

  /**
   * Given a string that is of the form {key1=value1,key2=value2,...}, turn it into a map.
   *
   * @param str The string that will be turned into a map
   * @return An ImmutableMap containing the key-value pairs found in the string
   */
  public static Map<String, String> decodeString(String str) {
    return ImmutableMap.copyOf(
        Splitter.on(SEPARATOR)
            .trimResults()
            .omitEmptyStrings()
            .withKeyValueSeparator(Splitter.on(KEY_VALUE_SEPARATOR_PATTERN))
            .split(str.replaceAll(SYMBOLS_TO_REMOVE_REGEX, StringUtils.EMPTY)));
  }

  /**
   * Given a MultiValueMap that is of the form {key1=list.of(value1,value2), key2=value2, ...}, turn
   * it into a Map.
   *
   * @param multi The MultiValue map that will be turned into a map
   * @return A Map containing the key-value pairs found in the MultiValueMap
   *     {key1=[value1,value2],key2=[value2],...}
   */
  public static Map<String, String> decodeMap(MultiValueMap<String, String> multi) {
    if (CollectionUtils.isEmpty(multi)) {
      return Map.of();
    }
    Map<String, String> map = new HashMap<>(multi.entrySet().size());
    for (Map.Entry<String, List<String>> entry : multi.entrySet()) {
      var sb = new StringBuilder();
      for (String s : entry.getValue()) {
        if (sb.length() > 0) {
          sb.append(',');
        }
        sb.append(s);
      }
      map.put(entry.getKey(), sb.toString());
    }
    return map;
  }
}
