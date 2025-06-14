@unstable @provision
Feature: Positions: get, create, update, delete through Provision API

  Background: : A token is fetched to authenticate to the provision API
    Given setup wiremock user for provision API
    Given the user fetches an authentication token
    And set company "Provision"
    When set site "mobilepro"

  Scenario: Create a position without all fields
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionWithoutAll_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""placementCategory":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""interstitial":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""name":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""mraidSupport":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""memo":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""screenLocation":"Value should not be empty""

  Scenario: Create a Banner position with all fields
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBanner_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "a"

  Scenario: Create a Banner position with FULLSCREEN location
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBannerFullscreen_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"screenLocation":"The value 'FULLSCREEN_VISIBLE' does not supported for 'BANNER'"}"
    And position pid is not retrieved for name "wilhelmtale"

  Scenario: Create a Banner position with VIDEO support
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBannerVideo_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"videoSupport":"The value 'VIDEO' does not supported for 'BANNER'"}"
    And position pid is not retrieved for name "habanero"

  Scenario: Create an Interstitital position with all fields
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInterstitial_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "sophia"

  Scenario: Create an Interstitital position with HEADER location
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInterstitialHeader_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"screenLocation":"The value 'HEADER_VISIBLE' does not supported for 'INTERSTITIAL'"}"
    And position pid is not retrieved for name "toesztonamdaragoe"

  Scenario: Create an Interstitital position site integration JS
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInterstitialJS_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "andereLiga"

  Scenario: Create an Interstitital position site integration JS with video
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInterstitialJSVideo_payload.json"
    Then "create position" failed with "400" response code and error message "Site integration type does not support this type of the placement category with video support"
    And position pid is not retrieved for name "interceptor"

  Scenario: Create an Interstitital position with Banner video support
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInterstitialBanner_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "fridaynight"

  Scenario: Create a Medium Rectangle position with all fields
    And set site "FOLK_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMediumRect_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "vilnanasha"

  Scenario: Create a Medium Rectangle position with video
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMediumRectVideo_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "idea"

  Scenario: Create a Medium Rectangle position with FULLSCREEN
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMediumRectFullscreen_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"screenLocation":"The value 'FULLSCREEN_VISIBLE' does not supported for 'MEDIUM_RECTANGLE'"}"
    And position pid is not retrieved for name "allmyloving"

  Scenario: Create a Medium Rectangle position with incorrect width
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMediumRectIncWidth_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"width":"The value '400' does not supported for 'MEDIUM_RECTANGLE'"}"
    And position pid is not retrieved for name "validator"

  Scenario: Create a Medium Rectangle position with incorrect height
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMediumRectIncHeight_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"height":"The value '100' does not supported for 'MEDIUM_RECTANGLE'"}"
    And position pid is not retrieved for name "mediator"

  Scenario: Create a Medium Rectangle position with video and site SDK
    And set site "FOLK_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMediumRectVideoSdk_payload.json"
    Then "create position" failed with "400" response code and error message "Site integration type does not support this type of the placement category with video support"
    And position pid is not retrieved for name "hombre"

  Scenario: Create a Native position with all fields
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionNative_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "bailar_hasta_la_muerte"

  Scenario: Create a Native position with ABOVE location
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionNativeAbove_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"screenLocation":"The value 'ABOVE_VISIBLE' does not supported for 'NATIVE'"}"
    And position pid is not retrieved for name "sunshineReggae"

  Scenario: Create a Native position with Yes mraid support
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionNativeYesMraid_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"mraidSupport":"The value 'YES' does not supported for 'NATIVE'"}"
    And position pid is not retrieved for name "della"

  Scenario: Create a Native position with Banner support
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionNativeBanner_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"videoSupport":"The value 'BANNER' does not supported for 'NATIVE'"}"
    And position pid is not retrieved for name "soycaballero"

  Scenario: Create a Native position for Mobile web site
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionNativeMobile_payload.json"
    Then "create position" failed with "400" response code and error message "Site type does not support this type of the placement category"
    And position pid is not retrieved for name "genevaclient"

  Scenario: Create an Instream position with all fields
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInstream_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "beautifulgirl"

  Scenario: Create an Instream position with site integration JS
    And set site "JAZZ_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInstreamJS_payload.json"
    Then "create position" failed with "400" response code and error message "Site integration type does not support this type of the placement category"
    And position pid is not retrieved for name "enjoythesilence"

  Scenario: Create an Instream position with Header location
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInstreamIncLoc_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"screenLocation":"The value 'HEADER_VISIBLE' does not supported for 'INSTREAM_VIDEO'"}"
    And position pid is not retrieved for name "borntobewild"

  Scenario: Create an Instream position with BANNER support
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInstreamBannerSupport_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"videoSupport":"The value 'BANNER' does not supported for 'INSTREAM_VIDEO'"}"
    And position pid is not retrieved for name "lastdaymylife"

  Scenario: Create a position without placementCategory
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionWithoutPlacement_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"placementCategory":"Value should not be empty"}"
    And position pid is not retrieved for name "totus_floreo"

  Scenario: Create a position with incorrect placementCategory
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionIncorrectPlacement_payload.json"
    Then "create position" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And position pid is not retrieved for name "italiano"

  Scenario Outline: Create a position without memo field, incorrect length memo or incorrect format memo
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And position pid is not retrieved for name "<position_name>"

    Examples:
      | position_name                    | file_payload                      | field_errors                                                                                                |
      | indian                           | CrPositionWithoutMemo_payload     | "fieldErrors":{"memo":"Value should not be empty"}                                                          |
      | 12345678901234567890123456789012 | CrPositionExtraLengthMemo_payload | "fieldErrors":{"name":"Entered value is too long","memo":"Entered value is too long"}                       |
      | romy-u$#.                        | CrPositionIncorrectMemo_payload   | "fieldErrors":{"name":"The value does not match the pattern","memo":"The value does not match the pattern"} |

  Scenario: Create a position with empty memo
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionEmptyMemo_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"name":"Entered value is too short","memo":"Entered value is too short"}"

  Scenario: Create a position with whitespace memo
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionWhitespaceMemo_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"name":"The value '_' does not match the expected from ' '"}"
    And position pid is not retrieved for name " "

  Scenario: Create a position with existing memo
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBanner_payload.json"
    Then "create position" failed with "400" response code and error message ""Position name already exists in this site""

  Scenario: Create a position without name
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionWithoutName_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"name":"Value should not be empty"}"

  Scenario: Create a position without screenLocation
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionWithoutLoc_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"screenLocation":"Value should not be empty"}"
    And position pid is not retrieved for name "itsnotwhatyouthink"

  Scenario: Create a position with incorrect format screenLocation
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionIncorrectLoc_payload.json"
    Then "create position" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And position pid is not retrieved for name "tarantella"

  Scenario Outline: Create a position without width, zero width or with value that greater than allowed
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And position pid is not retrieved for name "<position_name>"

    Examples:
      | position_name | file_payload                   | field_errors                                        |
      | draft         | CrPositionWithoutWidth_payload | "fieldErrors":{"width":"Value should not be empty"} |
      | cellosuites   | CrPositionZeroWidth_payload    | "fieldErrors":{"width":"Incorrect min value"}       |
      | mistica       | CrPositionExtraWidth_payload   | "fieldErrors":{"width":"Incorrect max value"}       |

  Scenario: Create a position with incorrect format width
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionIncorrectWidth_payload.json"
    Then "create position" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And position pid is not retrieved for name "sweetjulia"

  Scenario: Create a position with float width
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionFloatWidth_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "arabiahanine"

  Scenario Outline: Create a position without height, zero heigth or height value greater than allowed
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And position pid is not retrieved for name "<position_name>"

    Examples:
      | position_name | file_payload                    | field_errors                                         |
      | lookmate      | CrPositionWithoutHeight_payload | "fieldErrors":{"height":"Value should not be empty"} |
      | lulelule      | CrPositionZeroHeight_payload    | "fieldErrors":{"height":"Incorrect min value"}       |
      | extrahe       | CrPositionExtraHeight_payload   | "fieldErrors":{"height":"Incorrect max value"}       |

  Scenario: Create a position with incorrect format height
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionIncorrectHeight_payload.json"
    Then "create position" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And position pid is not retrieved for name "brandenburgconcert"

  Scenario: Create a position with float height
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionFloatHeight_payload.json"
    Then request passed successfully
    And position pid is retrieved for name "donjuan"

  Scenario Outline: Create a position without mraidSupport or without videoSupport
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And position pid is not retrieved for name "<position_name>"

    Examples:
      | position_name   | file_payload                          | field_errors                                               |
      | charisma        | CrPositionWithoutMraid_payload        | "fieldErrors":{"mraidSupport":"Value should not be empty"} |
      | behavelikeclark | CrPositionWithoutVideoSupport_payload | "fieldErrors":{"videoSupport":"Value should not be empty"} |

  Scenario Outline: Create a position with incorrect mraidSupport or incorrect videoSupport
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/<file_payload>.json"
    Then "create position" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And position pid is not retrieved for name "<position_name>"

    Examples:
      | position_name   | file_payload                            |
      | mediumrect_memo | CrPositionIncorrectMraid_payload        |
      | ciaccone        | CrPositionIncorrectVideoSupport_payload |


  Scenario: Create a position without interstitial
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionWithoutInterstitial_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"interstitial":"Value should not be empty"}"
    And position pid is not retrieved for name "haendelsarabande"

  Scenario: Create a position with incorrect interstitial
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionIncorrectInterstitial_payload.json"
    Then "create position" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And position pid is not retrieved for name "disappointment"

  Scenario: Create a position with true interstitial for native
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionTrueInterstitialNative_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"interstitial":"The value 'true' does not supported for 'NATIVE'"}"
    And position pid is not retrieved for name "dependsonyou"

  Scenario: Test external for creating a Banner position
    Given the user "role-api-user-1c" logs in via B2B
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBanner_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario Outline: Get Banner, Interstitial, Medium Rectangle, Native, Instream position
    And set site "<site_name>"
    And set position pid for name "<position_name>"
    Then the position data for provision API is retrieved
    And request passed successfully
    And returned "provision position" data matches the following json file "jsons/genevacrud/position/provisionApi/ER/<file_ER>.json"

    Examples:
      | site_name | position_name          | file_ER                             |
      | mobilepro | a                      | GetBannerPositionProvision_ER       |
      | mobilepro | sophia                 | GetInterstitialPositionProvision_ER |
      | FOLK_SITE | vilnanasha             | GetMedRectPositionProvision_ER      |
      | JAZZ_SITE | bailar_hasta_la_muerte | GetNativePositionProvision_ER       |
      | mobilepro | beautifulgirl          | GetInstreamPositionProvision_ER     |

  Scenario: Get non existing position
    And try to get non existing provision
    Then "get position" failed with "500" response code

  Scenario: Update position from Interstitial to Banner
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInterstitialUpdate_payload.json"
    Then request passed successfully
    And set position pid for name "monday"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionInterstitialBanner_payload.json"
    Then request passed successfully
    Then returned "provision position" data matches the following json file "jsons/genevacrud/position/provisionApi/ER/UpdatePositionInterstitialBanner_ER.json"
    And position pid is not retrieved for name "monday"
    And position pid is retrieved for name "whenifoundyou"

  Scenario: Update position from Banner to Instream
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBannerUpdate_payload.json"
    Then request passed successfully
    And set position pid for name "tuesday"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionBannerInstream_payload.json"
    Then request passed successfully
    Then returned "provision position" data matches the following json file "jsons/genevacrud/position/provisionApi/ER/UpdatePositionBannerInstream_ER.json"
    And position pid is not retrieved for name "tuesday"
    And position pid is retrieved for name "gotmeonmyknees"

  Scenario: Update position from Medium Rectangle to Banner
    And set site "FOLK_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionMedUpdate_payload.json"
    Then request passed successfully
    And set position pid for name "wednesday"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionMedBanner_payload.json"
    Then request passed successfully
    Then returned "provision position" data matches the following json file "jsons/genevacrud/position/provisionApi/ER/UpdatePositionMedBanner_ER.json"
    And position pid is not retrieved for name "wednesday"
    And position pid is retrieved for name "positionsamour"

  Scenario: Update position from Native to Intersitial
    And set site "FOLK_SITE"
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionNativeUpdate_payload.json"
    Then request passed successfully
    And set position pid for name "thursday"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionNativeInterstitial_payload.json"
    Then request passed successfully
    Then returned "provision position" data matches the following json file "jsons/genevacrud/position/provisionApi/ER/UpdatePositionNativeInterstitial_ER.json"
    And position pid is not retrieved for name "thursday"
    And position pid is retrieved for name "downinneworleans"

  Scenario: Update position from Instream to Native
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionInstreamUpdate_payload.json"
    Then request passed successfully
    And set position pid for name "saturday"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionInstreamNative_payload.json"
    Then "update position" failed with "400" response code and error message "Site type does not support this type of the placement category"
    And position pid is not retrieved for name "brahms"
    And position pid is retrieved for name "saturday"

  Scenario: Update position with memo length greater than allowed
    When the user creates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/CrPositionBigLength_payload.json"
    Then request passed successfully
    And set position pid for name "sunday"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionBigLength_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"memo":"Entered value is too long"}"
    And position pid is not retrieved for name "biglengthmemo"
    And position pid is retrieved for name "sunday"

  Scenario: Update position with the same version
    And set position pid for name "gotmeonmyknees"
    When the user updates position using provision API from the json file "jsons/genevacrud/position/provisionApi/payload/UpdatePositionBannerInstream_payload.json"
    Then "update position with the same version" failed with "500" response code
    And position pid is retrieved for name "gotmeonmyknees"

  Scenario Outline: Delete Banner, Interstitial, Medium Rectangle, Native or Instream position
    And set site "<site_name>"
    And set position pid for name "<position_name>"
    And set company pid for name "Provision"
    And the position for provision is deleted
    Then request passed successfully
    And position pid is retrieved for name "<position_name>"
    And status of the position with the position name "<position_name>" is "-1"

    Examples:
      | site_name | position_name          |
      | mobilepro | a                      |
      | mobilepro | sophia                 |
      | FOLK_SITE | vilnanasha             |
      | JAZZ_SITE | bailar_hasta_la_muerte |
      | mobilepro | beautifulgirl          |

