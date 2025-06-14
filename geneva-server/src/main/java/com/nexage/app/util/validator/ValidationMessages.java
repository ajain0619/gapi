package com.nexage.app.util.validator;

/** Contains validation messages for validators */
public final class ValidationMessages {

  private ValidationMessages() {
    throw new IllegalStateException("Utility class");
  }

  public static final String WRONG_STRING_LENGTH = "Incorrect value length";
  public static final String WRONG_STRING_LENGTH_TO_LARGE = "Entered value is too long";
  public static final String WRONG_STRING_PATTERN = "The value does not match the pattern";
  public static final String WRONG_NUMBER_MAX = "Incorrect max value";
  public static final String WRONG_NUMBER_MIN = "Incorrect min value";
  public static final String WRONG_IS_EMPTY = "Value should not be empty";
  public static final String WRONG_REQUIRED_FIELD = "Required field";
  public static final String WRONG_ELEMENT_IS_EMPTY = "Elements in the array cannot be empty";
  public static final String WRONG_VALUE = "Value is incorrect";
  public static final String WRONG_IS_NOT_EMPTY = "Value should be empty";
  public static final String WRONG_ARRAY_LENGTH_TO_LARGE =
      "The number of elements in the array is greater than allowed";
  public static final String WRONG_ARRAY_LENGTH_TO_SMALL =
      "The number of elements in the array is less than allowed";
  public static final String WRONG_IAB_CATEGORY_PARENT_EXIST =
      "Can't use category '%s' because parent category selected too";
  public static final String WRONG_IAB_CATEGORY_NOT_EXIST = "Category '%s' was not found";

  public static final String COMPANY_NOT_FOUND = "Company doesn't exist in database";

  public static final String PLACEMENT_SCREEN_LOCATION_CONSTRAINT =
      "Placement Screen Location is invalid";

  public static final String NOT_AUTHORIZED = "Update operation not authorized";
  public static final String QUERY_FIELD_VALIDATION_FAILED =
      "Query field parameter validation failed";
  public static final String QUERY_FIELD_FAILED_DUE_TO = "Query field %s is invalid. Reason: %s";
  public static final String PLACEMENT_VIDEO_PLAYER_HEIGHT_WIDTH_NOT_NULL =
      "Player height and width cannot be null if player required is true";
  public static final String PLACEMENT_VIDEO_SKIP_OFFSET_NOT_NULL =
      "Skipoffset cannot be null if skippable is true";
  public static final String IDENTITYIQ_USER_VALIDATION_INVALID_USER_DETAILS =
      "Invalid User Details";
  public static final String PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION =
      "Invalid video settings for longform selection";
  public static final String PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION =
      "Invalid or missing streamType in video settings";
  public static final String PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION =
      "Invalid or missing playerBrand in video settings";
  public static final String PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION =
      "Invalid or missing ssai in video settings";
  public static final String PLACEMENT_DAP_CONSTRAINT_VIOLATION =
      "Placement Category must be Banner or Medium Rectangle or Interstitial and site type should be Desktop or Mobile Web and Placement should support both Video and Banner for enabling  DAP settings";
  public static final String PLAYER_REQUIRED_DAP_CONSTRAINT =
      "If player required is false for DAP then it should not have player type or player id or playlist id";
  public static final String DAP_PLAYER_TYPE_CONSTRAINT =
      "DAP Player Type can not be null if player required is true";
  public static final String PLAYER_ID_CONSTRAINT =
      "Invalid player Id or Player ID is not supported for Yahoo player type DAP";
  public static final String PLAYLIST_ID_CONSTRAINT = "Invalid playlist Id";
  public static final String VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP =
      "Video placement type is mandatory for DAP placement";
  public static final String INVALID_TRAFFIC_SOURCE_TYPE =
      "Invalid traffic source type value. Allowed values are %s";
  public static final String INVALID_START_DELAY_VALUE = "Placement Video start delay is invalid";

  public static final String INVALID_SELLER_BUYER_SEAT_VALUE =
      "Invalid sellers and dspBuyerSeats property values";
  public static final String INVALID_RECIPIENT = "Please provide at least one recipient";

  public static final String PLACEMENT_FORMULA_MORE_THEN_ONE_ATTRIBUTE_OCCURRENCE_IN_GROUP =
      "Please provide only one attribute in a group";

  public static final String PLACEMENT_FORMULA_BAD_DATA = "Please provide valid rule data";
}
