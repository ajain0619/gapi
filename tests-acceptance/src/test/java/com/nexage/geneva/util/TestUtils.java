package com.nexage.geneva.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class TestUtils {
  public static final ObjectMapper mapper = new ObjectMapper();
  private static final String US_EASTERN = "America/New_York";

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    dateFormat.setTimeZone(TimeZone.getTimeZone(US_EASTERN));
    mapper.setDateFormat(dateFormat);
  }

  /**
   * Generate a string using the current date&time, format as yyyyMMddHHmmssSSS String can be used
   * in any field which requires unique input
   *
   * @return string representation of date in yyyyMMddHHmmssSSS format
   */
  public static String timeStamp() {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    LocalDateTime localDateTime = LocalDateTime.now();
    ZoneId zone = ZoneId.of(US_EASTERN);
    ZoneOffset zoneOffSet = zone.getRules().getOffset(localDateTime);
    Date date = Date.from(localDateTime.toInstant(zoneOffSet));
    return dateFormat.format(date);
  }

  /**
   * Split incoming string using equals as separator into map.
   *
   * @param query incoming string
   * @return {@code Map} with keys and value created from string
   */
  public static Map<String, String> splitQuery(String query) {
    Map<String, String> queryPairs = new LinkedHashMap<>();
    String[] pairs = query.split("&&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      if (idx != -1) {
        queryPairs.put(pair.substring(0, idx).trim(), pair.substring(idx + 1).trim());
      }
    }
    return queryPairs;
  }

  /**
   * Get resource from file as string
   *
   * @param fileName name of the resource file
   * @return {@code String} resource
   */
  public static String getResourceAsString(String fileName) throws Throwable {
    return IOUtils.toString(getResourceAsInputStream(fileName), "UTF-8");
  }

  /**
   * Get resource from file as input stream
   *
   * @param fileName name of the resource file
   * @return {@code String} resource
   */
  public static InputStream getResourceAsInputStream(String fileName) throws Throwable {
    ClassLoader classLoader = TestUtils.class.getClassLoader();
    URL url = classLoader.getResource(fileName);
    assertNotNull(url, String.format("File [%s] is not found", fileName));
    return url.openStream();
  }

  /**
   * Check whether source matches pattern
   *
   * @param source Source string
   * @param pattern Pattern
   * @return true if matches, otherwise false
   */
  public static boolean regExGetMatch(String source, String pattern) {
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(source);
    return m.matches();
  }

  /**
   * Get list of matches of pattern to source string
   *
   * @param source Source string
   * @param pattern Pattern
   * @return list of matches
   */
  public static List<String> regExGetAllMatches(String source, String pattern) {
    List<String> allMatches = new ArrayList<>();
    Matcher m = Pattern.compile(pattern).matcher(source);
    while (m.find()) {
      allMatches.add(m.group());
    }
    return allMatches;
  }

  /**
   * Terminate tests forcibly
   *
   * @param code exit code
   */
  public static void terminateTests(int code) {
    System.exit(code);
  }

  /**
   * Convert model object to json
   *
   * @param entity model object to convert
   * @return {@code String} json object
   */
  public static String convertToJson(Object entity) throws Throwable {
    return mapper.writeValueAsString(entity);
  }
}
