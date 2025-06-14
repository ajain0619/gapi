package com.nexage.geneva.response;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.nexage.geneva.request.Request;
import com.nexage.geneva.util.ErrorHandler;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.ResponseComparator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.skyscreamer.jsonassert.ArrayValueMatcher;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

/**
 * Helps match returned response data with expected result(put response object or json data) and
 * verify response code
 */
@Log4j2
public class ResponseHandler {

  public static final String RESPONSE_FIELDS_ERROR = "fieldErrors";

  /**
   * Verify JSONResource contains expected http response status code
   *
   * @param request Request entity
   * @param jsonResource json resource
   * @param expectedCode http response expected status code
   * @throws Throwable
   */
  public static void verifySuccessfulResponseCode(
      Request request, JSONResource jsonResource, int expectedCode) throws Throwable {
    ErrorHandler.assertTrue(
        request,
        jsonResource.status(expectedCode),
        "expected http response code is "
            + expectedCode
            + ". "
            + "Actual is "
            + jsonResource.http().getResponseCode()
            + ".");
  }

  /**
   * Verify whether exception message contains status code
   *
   * @param request Request entity
   * @param message message describing request purpose
   * @param exceptionMessage Exception message
   * @param expectedCode http response expected status code
   */
  public static void verifyFailedResponseCode(
      Request request, String message, String exceptionMessage, String expectedCode) {
    if (!exceptionMessage.contains("[" + expectedCode + "]")) {
      ErrorHandler.assertNotEmpty(
          request,
          message
              + " expected http response code is "
              + expectedCode
              + "."
              + "\n"
              + exceptionMessage
              + ".");
    }
  }

  public static void verifyRedirectResponse(Request request, JSONResource jsonResource)
      throws Throwable {
    ErrorHandler.assertTrue(
        request,
        jsonResource.location().toString().matches(".*/(login|b2b)"),
        "Invalid location value " + jsonResource.location().toString());
  }

  /**
   * Verify whether exception message matches expected message
   *
   * @param request Request entity
   * @param message message describing request purpose
   * @param exceptionMessage Exception message
   * @param expectedErrorMessage http response expected message
   */
  public static void verifyFailedResponseMessage(
      Request request, String message, String exceptionMessage, String expectedErrorMessage) {
    if (!exceptionMessage.contains(expectedErrorMessage)) {
      ErrorHandler.assertNotEmpty(
          request,
          message
              + " expected http response message is "
              + expectedErrorMessage
              + "."
              + "\n"
              + exceptionMessage
              + ".");
    }
  }

  /**
   * Verify whether field errors are contained in the exception message
   *
   * @param request Request entity
   * @param exceptionMessage Exception message
   * @param fieldErrors Field errors
   * @throws Throwable
   */
  public static void verifyFailedFieldErrors(
      Request request, String exceptionMessage, String fieldErrors) throws Throwable {
    String[] split = exceptionMessage.split("\n");
    if (split.length == 2) {
      JSONObject exMessage = new JSONObject(split[1]);
      JSONObject fieldErrorsObj = new JSONObject((fieldErrors));
      JSONAssert.assertEquals(
          fieldErrorsObj.toString(),
          exMessage.getJSONObject(RESPONSE_FIELDS_ERROR).toString(),
          JSONCompareMode.LENIENT);
    } else {
      if (!exceptionMessage.contains(fieldErrors)) {
        System.out.println(exceptionMessage);
        System.out.println(fieldErrors);
        ErrorHandler.assertNotEmpty(
            request, "Field errors is " + fieldErrors + "." + "\n" + exceptionMessage + ".");
      }
      if (fieldErrors.equals("")) {
        ErrorHandler.assertTrue(
            request,
            !exceptionMessage.contains("field"),
            "Field errors is not empty" + "\n" + exceptionMessage + ".");
      }
    }
  }

  /**
   * Verify whether field errors are contained in the exception message (one of two provided
   * options)
   *
   * @param request Request entity
   * @param exceptionMessage Exception message
   * @param firstFieldError - first option for field error
   * @param secondFieldError - second option for field error
   */
  public static void verifyBothFieldErrors(
      Request request, String exceptionMessage, String firstFieldError, String secondFieldError) {
    if (!(exceptionMessage.contains(firstFieldError)
        || exceptionMessage.contains(secondFieldError))) {
      System.out.println(exceptionMessage);
      ErrorHandler.assertNotEmpty(
          request, "Field errors is not equal to provided ones." + "\n" + exceptionMessage + ".");
    }
  }

  /**
   * Verify whether field errors are contained in the exception message
   *
   * @param request Request entity
   * @param exceptionMessage Exception message
   */
  public static void verifyEmptyFieldErrors(Request request, String exceptionMessage) {
    if (exceptionMessage.contains("fieldErrors")) {
      ErrorHandler.assertNotEmpty(
          request, "Field errors is not empty." + "\n" + exceptionMessage + ".");
    }
  }

  /**
   * Match request response with expected result
   *
   * @param request request
   * @param serverResponse response
   * @param message message describing request purpose
   * @param expectedJson expected json
   * @throws Throwable
   */
  public static void matchResponseWithExpectedResult(
      Request request, JSONResource serverResponse, String message, String expectedJson)
      throws Throwable {
    String actualJson;
    if (JsonHandler.isJsonArray(expectedJson.trim())) {
      actualJson = serverResponse.array().toString();
    } else if (JsonHandler.isJsonObject(expectedJson.trim())) {
      actualJson = serverResponse.object().toString();
    } else {
      throw new RuntimeException("Unknown type of content in json file");
    }
    JSONComparator comparator = getComparator(request);
    JSONAssert.assertEquals(message, expectedJson, actualJson, comparator);
  }

  /**
   * Match request response from expected result array
   *
   * @param request request
   * @param serverResponse response
   * @param message message describing request purpose
   * @param expectedJson expected json array
   * @throws Throwable
   */
  public static void matchResponseWithExpectedResultset(
      Request request, JSONResource serverResponse, String message, String expectedJson)
      throws Throwable {
    matchResponseWithExpectedResult(request, serverResponse, message, expectedJson);
  }

  /**
   * Match request response with expected result (ignore additional non-expected fields)
   *
   * @param request request
   * @param expectedJson expected json
   * @param actualObject actual object
   * @param message message describing request purpose
   */
  public static void matchResponseWithExpectedResult(
      Request request, String expectedJson, JSONObject actualObject, String message) {
    JSONComparator comparator = getComparator(request);
    JSONAssert.assertEquals(message, expectedJson, actualObject.toString(), comparator);
  }

  public static void matchResponseWithExpectedResult(
      Request request, String expectedString, InputStream actualObject, String message) {
    try {
      String returnedString = IOUtils.toString(actualObject, UTF_8).trim();

      if (!returnedString.equals(expectedString.trim())) {
        throw new Exception(
            String.format(
                "Errors occurred while comparing actual: %s and expected: %s data:\n%s",
                returnedString, expectedString, message));
      }
    } catch (Exception e) {
      ErrorHandler.assertNotEmpty(request, e.getMessage());
    }
  }

  public static void checkResponseForValue(
      String expectedKey, String expectedValue, JSONResource response, String message)
      throws Exception {
    String actualValue = response.object().getString(expectedKey);
    if (!actualValue.equals(expectedValue))
      throw new Exception(
          String.format(
              "Errors occurred while comparing actual: %s and expected: %s data:\n%s",
              actualValue, expectedValue, message));
  }

  /**
   * Match request response with expected result
   *
   * @param request request
   * @param expectedJson expected json
   * @param actualArray actual array
   * @param message message describing request purpose
   */
  public static void matchResponseWithExpectedResult(
      Request request, String expectedJson, JSONArray actualArray, String message) {
    JSONComparator comparator = getComparator(request);
    JSONAssert.assertEquals(message, expectedJson, actualArray.toString(), comparator);
  }

  private static JSONComparator getComparator(Request request) {
    List<Customization> customizations = new ArrayList<>();
    if (request.getActualObjectIgnoredKeys() != null) {
      Arrays.stream(request.getActualObjectIgnoredKeys())
          .forEach(
              key -> {
                // Ignoring keys that has nested arrays
                if (key.contains("[*]")) {
                  String[] splittedKey = key.split("\\[\\*\\].");
                  if (splittedKey.length > 2) {
                    // For arrays with two levels of nesting, using ArrayValueMatcher to ignore the
                    // comparison
                    String mainKey = splittedKey[0] + "[*]." + splittedKey[1];
                    String nestedKey = "**." + splittedKey[2];
                    customizations.add(
                        new Customization(
                            mainKey,
                            new ArrayValueMatcher<>(
                                new CustomComparator(
                                    JSONCompareMode.LENIENT,
                                    new Customization(nestedKey, (o1, o2) -> true)))));
                  } else {
                    // Ignoring keys that has one level of nesting
                    customizations.add(new Customization(key, (o1, o2) -> true));
                  }
                } else {
                  // Ignoring keys that has direct values
                  customizations.add(new Customization(key, (o1, o2) -> true));
                }
              });
    }
    return new ResponseComparator(
        JSONCompareMode.LENIENT,
        request.getExpectedObjectIgnoredKeys(),
        customizations.toArray(Customization[]::new));
  }
}
