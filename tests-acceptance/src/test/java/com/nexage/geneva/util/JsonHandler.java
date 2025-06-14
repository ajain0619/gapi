package com.nexage.geneva.util;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class JsonHandler {

  /**
   * Custom check if json is object
   *
   * @param value json
   * @return true if json is object
   */
  public static boolean isJsonObject(String value) {
    return value.startsWith("{") && value.endsWith("}");
  }

  /**
   * Custom check if json is array
   *
   * @param value json
   * @return true if json is array
   */
  public static boolean isJsonArray(String value) {
    return value.startsWith("[") && value.endsWith("]");
  }

  /**
   * create json object from file
   *
   * @param filename json file
   * @return created json object
   * @throws Throwable
   */
  public static JSONObject getJsonObjectFromFile(String filename) throws Throwable {
    return new JSONObject(TestUtils.getResourceAsString(filename));
  }

  /**
   * create json array from file
   *
   * @param filename json file
   * @return created json array
   * @throws Throwable
   */
  public static JSONArray getJsonArrayFromFile(String filename) throws Throwable {
    return new JSONArray(TestUtils.getResourceAsString(filename));
  }

  /**
   * create json from file and add timestamp to specific field
   *
   * @param filename json file
   * @param fieldName field to adding timestamp
   * @return json object where timestamp is added to field
   * @throws Throwable
   */
  public static JSONObject getJsonObjectFromFileWithTimestamp(String filename, String fieldName)
      throws Throwable {
    JSONObject jsonObject = getJsonObjectFromFile(filename);
    return addTimestampToField(jsonObject, fieldName);
  }

  /**
   * Add timestamp to specific field in specific json object
   *
   * @param jsonObject json object with specific field
   * @param fieldName field to adding timestamp
   * @return json object where timestamp is added to field
   * @throws Throwable
   */
  private static JSONObject addTimestampToField(JSONObject jsonObject, String fieldName)
      throws Throwable {
    return jsonObject.put(fieldName, jsonObject.get(fieldName) + TestUtils.timeStamp());
  }
}
