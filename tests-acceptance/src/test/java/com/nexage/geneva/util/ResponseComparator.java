package com.nexage.geneva.util;

import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

public class ResponseComparator extends CustomComparator {

  private final String[] expectedObjectIgnoredKeys;

  public ResponseComparator(
      JSONCompareMode mode, String[] expectedObjectIgnoredKeys, Customization... customizations) {
    super(mode, customizations);
    this.expectedObjectIgnoredKeys = expectedObjectIgnoredKeys;
  }

  @Override
  protected void checkJsonObjectKeysExpectedInActual(
      String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result)
      throws JSONException {
    // Remove ignored keys from json object
    if (expectedObjectIgnoredKeys != null) {
      Arrays.stream(expectedObjectIgnoredKeys).forEach(attribute -> expected.remove(attribute));
    }
    super.checkJsonObjectKeysExpectedInActual(prefix, expected, actual, result);
  }
}
