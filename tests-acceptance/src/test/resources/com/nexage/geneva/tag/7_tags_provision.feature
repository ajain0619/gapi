@unstable @provision
Feature: Tags: get, create, update, delete through Provision API

  Background: A token is fetched to authenticate to the provision API
    Given setup wiremock user for provision API
    Given the user fetches an authentication token
    And set company "Provision"
    When set site "mobilepro"
    And set position pid for name "provision_ad"

  @restoreCrudCoreDatabaseBefore

  Scenario: create a tag without fields
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagsWithoutFields.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And response failed with ""fieldErrors":{"buyer.pid":"Value should not be empty, Value is incorrect","name":"Value should not be empty"}" field error or with this one ""fieldErrors":{"buyer.pid":"Value is incorrect, Value should not be empty","name":"Value should not be empty"}"

  Scenario: create an AOL tag with all fields
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolAllFields.json"
    Then request passed successfully
    And tag "AV011" is retrieved from database
    And AOL Mobile tag "AV011" has correct additional values

  Scenario: create an AOL tag with extra fields - primaryId, primaryName, secondaryId, secondaryName
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrAolExtraFields.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""secondaryId":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""secondaryName":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""primaryName":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""primaryId":"Value should be empty""
    And tag "AV0" is not retrieved from database

  Scenario: create an AOL tag with incorrect buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIncorrectBuyerPid.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"buyer.pid":"Value is incorrect"}"
    And tag "AV1" is not retrieved from database

  Scenario Outline: create an AOL tag with incorrect name field
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"

    Examples:
      | file_payload                 | response_message                                              |
      | CrTagAolWithoutName          | "fieldErrors":{"name":"Value should not be empty"}            |
      | CrTagAolIncorrectPatternName | "fieldErrors":{"name":"The value does not match the pattern"} |
      | CrTagAolBigLengthName        | "fieldErrors":{"name":"Incorrect value length"}               |

  Scenario: create an AOL tag with empty name
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolEmptyName.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And response failed with ""fieldErrors":{"name":"The value does not match the pattern, Incorrect value length"}" field error or with this one ""fieldErrors":{"name":"Incorrect value length, The value does not match the pattern"}"

  Scenario: create an AOL tag with duplicate name
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolAllFields.json"
    Then "create AOL tag" failed with "400" response code and error message "The tag name already exists in this position"
    And there is/are only "1" tags for tag name "AV011"

  Scenario: create a tag with the same name but different tag type
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdNameFromAol.json"
    Then request passed successfully
    And there is/are only "2" tags for tag name "AV011"

  Scenario Outline: create an AOL tag with combinations for rtbProfile - pubNetReserve, alterReserve
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                          | response_message                                                                                                                                                                                 | tag_name |
      | CrTagAolWithoutRtb                    | "fieldErrors":{"buyer.pid":"Value is incorrect"}                                                                                                                                                 | AV2      |
      | CrTagAolEmptyRtb                      | "fieldErrors":{"rtbProfile.pubNetReserve":"Value should not be empty","rtbProfile.useDefaultBidders":"Value should not be empty","rtbProfile.bidderFilterWhitelist":"Value should not be empty"} | AV3      |
      | CrTagAolWithoutPubnetreserve          | "fieldErrors":{"rtbProfile.pubNetReserve":"Value should not be empty"}                                                                                                                           | AV4      |
      | CrTagAolZeroPubnetreserve             | "fieldErrors":{"rtbProfile.pubNetReserve":"Incorrect min value"}                                                                                                                                 | AV5      |
      | CrTagAolIncorrectDPlacesPubnetreserve | "fieldErrors":{"rtbProfile.pubNetReserve":"Wrong number format"}                                                                                                                                 | AV7      |
      | CrTagAolBigPubnetreserve              | "fieldErrors":{"rtbProfile.pubNetReserve":"Incorrect max value"}                                                                                                                                 | AV6      |

  Scenario Outline: create an AOL tag with incorrect pubnetreserve and alterreserve
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                         | tag_name |
      | CrTagAolIncorrectFormatPubnetreserve | AV8      |
      | CrTagAolIncorrectAlterreserve        | AV9      |

  Scenario: create an AOL tag with incorrect revenue flag
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagIncorrectRevenueflag.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "AV10" is not retrieved from database

  Scenario: create an AOL tag with incorrect ecpmProvision and ecpmManual
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIncorrectEcpmprovision.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"ecpmProvision":"Value is incorrect"}"
    And tag "AV11" is not retrieved from database

  Scenario: create an AOL tag with incorrect ecpmManual
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIncorrectEcpmmanual.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "AV12" is not retrieved from database

  Scenario: create an AOL tag without revenue flag
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagWithoutRevenue.json"
    Then request passed successfully
    And tag "AV2772" is retrieved from database
    And AOL Mobile tag "AV2772" has correct additional values

  Scenario: create a tag without ecpmProvision and ecpmManual
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagWithoutProvisionManual.json"
    Then request passed successfully
    And tag "AV998" is retrieved from database
    And AOL Mobile tag "AV998" has correct additional values

  Scenario Outline: create a tag with incorrect combinations of importRevenueFlag, ecpmProvision, ecpmManual
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                               | response_message                                                                                            | tag_name |
      | CrTagBigEcpmManual                         | "fieldErrors":{"ecpmManual":"Incorrect max value"}                                                          | AV23     |
      | CrTagIncorrectDplacesManual                | "fieldErrors":{"ecpmManual":"Wrong number format"}                                                          | AV24     |
      | CrTagAolProvisionAutoManualNonZero         | "fieldErrors":{"ecpmManual":"Must be 0"}                                                                    | AV25     |
      | CrTagAolProvisionManualManualNonZero       | "fieldErrors":{"ecpmProvision":"Value is incorrect","ecpmManual":"Must be 0"}                               | AV26     |
      | CrTagAdRevenueFalseProvisionAutoManualZero | "fieldErrors":{"ecpmProvision":"Value is incorrect (also check importRevenueFlag)"}                         | AV2882   |
      | CrTagAolWithReportUsernamePassword         | "fieldErrors":{"adNetReportPassword":"Value should be empty","adNetReportUserName":"Value should be empty"} | AV32     |

  Scenario: create an AOL tag with incorrect autoExpand of tagController
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagIncorrectTagController.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "AV33" is not retrieved from database

  Scenario Outline: create an AOL tag with incorrect format tagController
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"tagController.autoExpand":"Value should not be empty"}"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                 | tag_name |
      | CrTagAolWithoutTagController | AV34     |
      | CrTagAolWithoutAutoExpand    | AV35     |

  Scenario Outline: create an AOL tag for different positions type
    And set position pid for name "<position_name>"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully
    And tag "<tag_name>" is retrieved from database
    And AOL Mobile tag "<tag_name>" has correct additional values

    Examples:
      | position_name         | file_payload             | tag_name |
      | provision_ad          | CrTagAolWithBanner       | AV1501   |
      | interstitialprovision | CrTagAolWithInterstitial | AV1511   |
      | mediumprovision       | CrTagAolWithMedium       | AV1521   |
      | instreamprovision     | CrTagAolWithInstream     | AV1531   |

  Scenario: create an AOL tag for interstitial position with JS integration and video settings
    When set site "JAZZ_SITE"
    And set position pid for name "intjsprovision"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIntJsVideoSettings.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""videoPlaybackMethod":"Value should be empty""
    And tag "AV157" is not retrieved from database

  Scenario Outline: create an AOL tag for different native or banner position with video settings
    And set position pid for name "<position_name>"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""videoPlaybackMethod":"Value should be empty""
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | position_name   | file_payload                | tag_name |
      | provision_ad    | CrTagAolBannerVideoSettings | AV154    |
      | nativeprovision | CrTagAolNativeVideoSettings | AV161    |

  Scenario Outline: create an AOL tag for different combinations of position paramaters
    And set position pid for name "<position_name>"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | position_name     | file_payload                  | tag_name | response_message                                                                                                                                         |
      | provision_ad      | CrTagAolBannerVideoSupport    | AV155    | "fieldErrors":{"videoSupport":"The value 'VIDEO' does not supported for 'BANNER'"}                                                                       |
      | mediumprovision   | CrTagAolMediumIncWidthHeight  | AV156    | "fieldErrors":{"width":"The value '400' does not supported for 'MEDIUM_RECTANGLE'","height":"The value '350' does not supported for 'MEDIUM_RECTANGLE'"} |
      | instreamprovision | CrTagAolInsBannerVideo        | AV158    | "fieldErrors":{"videoSupport":"The value 'BANNER' does not supported for 'INSTREAM_VIDEO'"}                                                              |
      | nativeprovision   | CrTagAolNativeWidthHeight     | AV160    | "fieldErrors":{"width":"Value should be empty","height":"Value should be empty"}                                                                         |
      | nativeprovision   | CrTagAolNativeBannerVideo     | AV162    | "fieldErrors":{"videoSupport":"The value 'BANNER' does not supported for 'NATIVE'"}                                                                      |
      | instreamprovision | CrTagAolInsWithoutWidth       | AV163    | "fieldErrors":{"width":"Value should not be empty"}                                                                                                      |
      | instreamprovision | CrTagAolInsWithoutHeight      | AV164    | "fieldErrors":{"height":"Value should not be empty"}                                                                                                     |
      | instreamprovision | CrTagAolInsWithoutPlayback    | AV167    | "fieldErrors":{"videoPlaybackMethod":"Value should not be empty"}                                                                                        |

  Scenario Outline: create an AOL tag for validation of paramaters from position
    And set position pid for name "instreamprovision"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload               | tag_name | response_message                                                   |
      | CrTagAolBigWidth           | AV182    | "fieldErrors":{"width":"Incorrect max value"}                      |
      | CrTagAolBigHeight          | AV185    | "fieldErrors":{"height":"Incorrect max value"}                     |
      | CrTagAolEmptyVideoPlayback | AV190    | "fieldErrors":{"videoPlaybackMethod":"Entered value is too short"} |
      | CrTagAolBigVideoPlayback   | AV191    | "fieldErrors":{"videoPlaybackMethod":"Entered value is too long"}  |

  Scenario Outline: create an AOL tag for validation of paramaters from position
    And set position pid for name "instreamprovision"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                | tag_name |
      | CrTagAolIncorVideoSupport   | AV180    |
      | CrTagAolIncorWidth          | AV183    |
      | CrTagAolIncorHeight         | AV186    |
      | CrTagAolIncorVideoMaxdur    | AV189    |
      | CrTagAolIncorVideoSkippable | AV193    |
      | CrTagAolIncorSkipOffset     | AV197    |

  Scenario: create an AOL tag for validation of paramaters from position
    And set position pid for name "instreamprovision"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIncorVideoPlayback.json"
    Then "create tag" failed with "400" response code and error message "One or more of the video playback methods is invalid"
    And tag "AV_192" is not retrieved from database

  Scenario: create an AOL tag without rules field
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolWithoutRules.json"
    Then request passed successfully
    And tag "AV37" is retrieved from database
    And AOL Mobile tag "AV37" has correct additional values

  Scenario: create an AOL tag with Allow and Block for the same rule type
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolAllowBlock.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"rules":"There is should be only one rule with the name 'Country'."}"
    And tag "AV36" is not retrieved from database

  Scenario Outline: create an AOL tag without fields for rules, incorrect data
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                         | response_message                                                             | tag_name |
      | CrTagAolWithoutTargetTypeCountry     | "fieldErrors":{"rules":"Field TargetType can not be empty"}                  | AV51     |
      | CrTagAolWithoutRuleTypeCountry       | "fieldErrors":{"rules":"Field RuleType can not be empty"}                    | AV52     |
      | CrTagAolWithoutDataCountry           | "fieldErrors":{"rules":"Field Data can not be empty"}                        | AV53     |
      | CrTagAolIncorrectDataCountry         | "fieldErrors":{"rules":"country code is incorrect."}                         | AV54     |
      | CrTagAolWithoutTargetTypeDevice      | "fieldErrors":{"rules":"Field TargetType can not be empty"}                  | AV55     |
      | CrTagAolWithoutRuleTypeDevice        | "fieldErrors":{"rules":"Field RuleType can not be empty"}                    | AV56     |
      | CrTagAolWithoutDataDevice            | "fieldErrors":{"rules":"Field Data can not be empty"}                        | AV57     |
      | CrTagAolIncorrectDataDevice          | "fieldErrors":{"rules":"device is incorrect. [Belcatel/*]"}                  | AV58     |
      | CrTagAolWithoutTargetTypeOs          | "fieldErrors":{"rules":"Field TargetType can not be empty"}                  | AV59     |
      | CrTagAolWithoutRuleTypeOs            | "fieldErrors":{"rules":"Field RuleType can not be empty"}                    | AV60     |
      | CrTagAolWithoutDataOs                | "fieldErrors":{"rules":"Field Data can not be empty"}                        | AV61     |
      | CrTagAolIncorrectDataOs              | "fieldErrors":{"rules":"os name is incorrect. [iOSs/*/*]"}                   | AV62     |
      | CrTagAolWithoutTargetTypeISPCarrier  | "fieldErrors":{"rules":"Field TargetType can not be empty"}                  | AV63     |
      | CrTagAolWithoutRuleTypeISPCarrier    | "fieldErrors":{"rules":"Field RuleType can not be empty"}                    | AV64     |
      | CrTagAolWithoutDataISPCarrier        | "fieldErrors":{"rules":"Field Data can not be empty"}                        | AV65     |
      | CrTagAolIncorrectDataISPCarrier      | "fieldErrors":{"rules":"carrier name is incorrect. [MTSs]"}                  | AV66     |
      | CrTagAolWithoutTargetTypeCarrierWifi | "fieldErrors":{"rules":"Field TargetType can not be empty"}                  | AV67     |
      | CrTagAolWithTargetTypeCarrierWifiNeg | "fieldErrors":{"rules":"CarrierWifi target type is incorrect. [NegKeyword]"} | AV68     |
      | CrTagAolWithoutRuleTypeCarrierWifi   | "fieldErrors":{"rules":"Field RuleType can not be empty"}                    | AV69     |
      | CrTagAolWithoutDataCarrierWifi       | "fieldErrors":{"rules":"Field Data can not be empty"}                        | AV70     |
      | CrTagAolIncorrectDataCarrierWifi     | "fieldErrors":{"rules":"CarrierWifi data is incorrect. [Keyword]"}           | AV71     |

  Scenario Outline: create an AOL tag with parent and child data at the same time for devices, countries, os versions
    When set site "FOLK_SITE"
    And set position pid for name "para_ioso"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                   | response_message                                                                                             | tag_name |
      | CrTagAolParentChildDeviceModel | "fieldErrors":{"rules":"Impossible to use rule 'Apple/iPod Touch', because parent version is selected too."} | AV1514   |
      | CrTagAolParentChildCountry     | "fieldErrors":{"rules":"country code is incorrect."}                                                         | AV1410   |
      | CrTagAolParentChildOsVersion   | "fieldErrors":{"rules":"Impossible to use rule 'iOS/4.0/*', because parent version is selected too."}        | AV1794   |

  Scenario: create an AOL tag with rules for ISPCarrier and CarrierWifi
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolWithISPCarrierCarrierWifi.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"rules":"Rules 'ISPCarrier' and 'CarrierWifi' are not allowed together."}"
    And tag "AV72" is not retrieved from database

  Scenario: create an AOL tag with irrelevant devices and OS for an android site
    When set site "JAZZ_SITE"
    And set position pid for name "nativeprovision"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIosDevicesOsAndroidSite.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "device is incorrect. [Apple/iPhone]"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "os name is incorrect. [iOS/*/*]"
    And tag "AV251" is not retrieved from database

  Scenario: create an AOL tag with irrelevant devices and OS for an IOS site
    When set site "FOLK_SITE"
    And set position pid for name "para_ioso"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolAndroidDevicesOsIOSSite.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "os name is incorrect. [Android/*/1.5]"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "device is incorrect. [Amazon/*]"
    And tag "AV252" is not retrieved from database

  Scenario Outline: create an AOL tag with incorrect targetType, ruleType for different rules
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                           | tag_name |
      | CrTagAolIncorrectTargetTypeCountry     | AV40     |
      | CrTagAolIncorrectRuleTypeCountry       | AV41     |
      | CrTagAolIncorrectTargetTypeDevice      | AV42     |
      | CrTagAolIncorrectRuleTypeDevice        | AV43     |
      | CrTagAolIncorrectTargetTypeOs          | AV44     |
      | CrTagAolIncorrectRuleTypeOs            | AV45     |
      | CrTagAolIncorrectTargetTypeISPCarrier  | AV46     |
      | CrTagAolIncorrectRuleTypeISPCarrier    | AV47     |
      | CrTagAolIncorrectTargetTypeCarrierWifi | AV48     |
      | CrTagAolIncorrectRuleTypeCarrierWifi   | AV49     |

  Scenario: create an AOL tag with empty bidderFilters, libraries, blockedAdCategories, blockedAdvertisers
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolEmptyBiddersSettings.json"
    Then request passed successfully
    And tag "AV300" is retrieved from database
    And AOL Mobile tag "AV300" has correct additional values

  @restoreCrudCoreDatabaseBefore
  Scenario Outline: create an AOL tag with not empty bidderFilters and libraries
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully
    And tag "<tag_name>" is retrieved from database
    And AOL Mobile tag "<tag_name>" has correct additional values

    Examples:
      | file_payload                        | tag_name |
      | CrTagAolNotEmptyBidFiltersLibraries | AV301    |
      | CrTagAolLibrariesRBidderNotEmpty    | AV305    |

  Scenario Outline: create an AOL tag with incorrect combinations of bidder parameters
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                          | response_message                                                                      | tag_name |
      | CrTagAolIncorrectBidderFilters        | "fieldErrors":{"bidderFilters":"Unknown value '10'"}                                  | AV302    |
      | CrTagAolIncorrectLibrariesBid         | "fieldErrors":{"libraries":"Supported only 'BLOCKLIST' value for 'listType'"}         | AV303    |
      | CrTagAolWhitelistFalseRBidderNotEmpty | "fieldErrors":{"rtbProfileBidders":"Value should be empty"}                           | AV304    |
      | CrTagAolWhitelistTrueDefBidderTrue    | "fieldErrors":{"useDefaultBidders":"Can not be true if bidderFilterWhitelist = true"} | AV306    |
      | CrTagAolIncorrectRBidders             | "fieldErrors":{"rtbProfileBidders":"Unknown value '10'"}                              | AV307    |

  Scenario: create an AOL tag with incorrect default bidders
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIncorrectDefBidders.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tag "AV308" is not retrieved from database

  Scenario Outline: create an AOL tag with incorrect blockedAdCategories and parent/child categories
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                  | response_message                                                                                                                 | tag_name |
      | CrTagAolIncorrectAdCategories | "fieldErrors":{"rtbProfile.blockedAdCategories":"ad categories ID is incorrect. [IAB1s]"}                                        | AV74     |
      | CrTagAolParentChildCategories | "fieldErrors":{"rtbProfile.blockedAdCategories":"Impossible to use category 'IAB1-1', because parent category is selected too."} | AV75     |

  Scenario: create an AOL tag with correct blockedAdCategories and blockedAdvertisers
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolCorrectCategoriesAdvertisers.json"
    Then request passed successfully
    And tag "AV76" is retrieved from database
    And AOL Mobile tag "AV76" has correct additional values

  Scenario: create an AOL tag with incorrect blockedAdvertisers
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAolIncorrectAdvertisers.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"rtbProfile.blockedAdvertisers":"blockedAdvertiser is incorrect. [ ]"}"
    And tag "AV77" is not retrieved from database

  Scenario: create an Ad Network with reporting settings when they are disabled
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdReportingDisabled.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""adNetReportPassword":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""ecpmProvision":"Value is incorrect""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""ecpmManual":"Must be 0""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""adNetReportUserName":"Value should be empty"}"
    And tag "AV210" is not retrieved from database

  Scenario: create an Ad Network without reporting settings when they are disabled
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithoutReportingDisabled.json"
    Then request passed successfully
    And tag "AV211" is retrieved from database
    And Ad Network tag "AV211" has correct additional values

  Scenario Outline: create an Ad Network with revenue true and without username or password
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                      | response_message                                              | tag_name |
      | CrTagAdRevenueTrueWithoutUsername | "fieldErrors":{"adNetReportPassword":"Value should be empty"} | AV78     |
      | CrTagAdRevenueTrueWithoutPassword | "fieldErrors":{"adNetReportUserName":"Value should be empty"} | AV79     |

  Scenario Outline: create a tag with correct combinations of importRevenueFlag, ecpmProvision, ecpmManual
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully
    And tag "<tag_name>" is retrieved from database
    And Ad Network tag "<tag_name>" has correct additional values

    Examples:
      | file_payload                                   | tag_name |
      | CrTagAdRevenueTrueProvisionAutoManualZero      | AV291    |
      | CrTagAdRevenueTrueProvisionManualManualNonZero | AV30     |
      | CrTagAdRevenueTrueProvisionManualManualZero    | AV31     |

  Scenario: create an Ad Network tag with duplicate name for the same buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdDuplicateSameBuyer.json"
    Then "create tag" failed with "400" response code and error message "The tag name already exists in this position"
    And there is/are only "1" tags for tag name "AV291"

  Scenario: create an Ad Network tag with duplicate name for the different buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdForDuplicate.json"
    Then request passed successfully
    And there is/are only "2" tags for tag name "AV291"

  @restoreCrudCoreDatabaseBefore

  Scenario: create an Ad Network tag with incorrect buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdIncorrectBuyerPid.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""secondaryId":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""secondaryName":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""buyer.pid":"Value is incorrect""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""adNetReportPassword":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""primaryName":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""adNetReportUserName":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""primaryId":"Value should be empty"}"
    And tag "AV79" is not retrieved from database

  Scenario Outline: create an Ad Network tag with the same primary Id/name, secondary Id/name for the different buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully
    And tag "<tag_name>" is retrieved from database
    And Ad Network tag "<tag_name>" has correct additional values

    Examples:
      | file_payload                    | tag_name |
      | CrTagAdPidPnameSidSnameSample   | AV2211   |
      | CrTagAdPidPnameSidSample        | AV2221   |
      | CrTagAdPidPnameSnameSample      | AV2231   |
      | CrTagAdPidSidSnameSample        | AV2241   |
      | CrTagAdPidPnameSample           | AV2251   |
      | CrTagAdPidSnameSample           | AV2261   |
      | CrTagAdPidSidSample             | AV2271   |
      | CrTagAdPidSample                | AV2281   |
      | CrTagAdPidPnameSidSnameDifBuyer | AV2211   |
      | CrTagAdPidPnameSidDifBuyer      | AV2221   |
      | CrTagAdPidPnameSnameDifBuyer    | AV2231   |
      | CrTagAdPidSidSnameDifBuyer      | AV2241   |
      | CrTagAdPidPnameDifBuyer         | AV2251   |
      | CrTagAdPidSnameDifBuyer         | AV2261   |
      | CrTagAdPidSidDifBuyer           | AV2271   |
      | CrTagAdPidDifBuyer              | AV2281   |

  Scenario Outline: create an Ad Network tag with the primary Id/name, secondary Id/name which already created for the same buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"primaryId":"Integration parameters should be unique"}"
    And there is/are only "2" tags for tag name "<tag_name>"

    Examples:
      | file_payload                  | tag_name |
      | CrTagAdPidPnameSidSnameSample | AV2211   |
      | CrTagAdPidPnameSidSample      | AV2221   |
      | CrTagAdPidPnameSnameSample    | AV2231   |
      | CrTagAdPidSidSnameSample      | AV2241   |
      | CrTagAdPidPnameSample         | AV2251   |
      | CrTagAdPidSnameSample         | AV2261   |
      | CrTagAdPidSidSample           | AV2271   |
      | CrTagAdPidSample              | AV2281   |

  Scenario: create an Ad Network tag with disabled creative
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdDisabledCreative.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""width":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""videoSupport":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""videoPlaybackMethod":"Value should be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""height":"Value should be empty""
    And tag "AV897" is not retrieved from database

  Scenario: create an Ad Network tag without buyer
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithoutBuyerPid.json"
    And response failed with ""fieldErrors":{"buyer.pid":"Value should not be empty, Value is incorrect"}" field error or with this one ""fieldErrors":{"buyer.pid":"Value is incorrect, Value should not be empty"}"
    And tag "AV747" is not retrieved from database

  Scenario Outline: create an Ad Network tag with combinations of primaryId, primaryName, secondaryId, secondaryName all required
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                  | response_message                                                       | tag_name |
      | CrTagAdWithoutPrimaryId       | "fieldErrors":{"primaryId":"Value should not be empty"}                | AV80     |
      | CrTagAdWithoutPrimaryName     | "fieldErrors":{"primaryName":"Value should not be empty"}              | AV81     |
      | CrTagAdWithoutSecondaryId     | "fieldErrors":{"secondaryId":"Value should not be empty"}              | AV82     |
      | CrTagAdWithoutSecondaryName   | "fieldErrors":{"secondaryName":"Value should not be empty"}            | AV83     |
      | CrTagAdEmptyPrimaryId         | "fieldErrors":{"primaryId":"Value should not be empty"}                | AV84     |
      | CrTagAdIncorrectPrimaryId     | "fieldErrors":{"primaryId":"The value does not match the pattern"}     | AV85     |
      | CrTagAdBigPrimaryId           | "fieldErrors":{"primaryId":"Incorrect value length"}                   | AV86     |
      | CrTagAdEmptyPrimaryName       | "fieldErrors":{"primaryName":"Value should not be empty"}              | AV87     |
      | CrTagAdIncorrectPrimaryName   | "fieldErrors":{"primaryName":"The value does not match the pattern"}   | AV88     |
      | CrTagAdBigPrimaryName         | "fieldErrors":{"primaryName":"Incorrect value length"}                 | AV89     |
      | CrTagAdEmptySecondaryid       | "fieldErrors":{"secondaryId":"Value should not be empty"}              | AV90     |
      | CrTagAdIncorrectSecondaryId   | "fieldErrors":{"secondaryId":"The value does not match the pattern"}   | AV91     |
      | CrTagAdBigSecondaryId         | "fieldErrors":{"secondaryId":"Incorrect value length"}                 | AV92     |
      | CrTagAdEmptySecondaryName     | "fieldErrors":{"secondaryName":"Value should not be empty"}            | AV93     |
      | CrTagAdIncorrectSecondaryName | "fieldErrors":{"secondaryName":"The value does not match the pattern"} | AV94     |
      | CrTagAdBigSecondaryName       | "fieldErrors":{"secondaryName":"Incorrect value length"}               | AV95     |

  Scenario Outline: create an Ad Network tag with primaryId, primaryName, secondaryId when these fields are not allowed
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                     | response_message                                      | tag_name |
      | CrTagAdWithPrimaryNameNotAllowed | "fieldErrors":{"primaryName":"Value should be empty"} | AV97     |
      | CrTagAdWithSecondaryIdNotAllowed | "fieldErrors":{"secondaryId":"Value should be empty"} | AV98     |

  Scenario: create an Ad Network tag with secondary name not allowed
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithSecondaryNameNotAllowed.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"secondaryName":"Value should be empty"}"
    And tag "AV99" is not retrieved from database

  Scenario: create an Ad Network tag without integration fields - not required
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithIntFieldsNotRequired.json"
    Then request passed successfully
    And tag "AV100" is retrieved from database
    And Ad Network tag "AV100" has correct additional values

  Scenario Outline: create an Ad Network tag with combinations of adNetReportApiKey, adNetReportApiToken fields are required
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                 | response_message                                                             | tag_name |
      | CrTagAdWithoutApiKey         | "fieldErrors":{"adNetReportApiKey":"Value should not be empty"}              | AV101    |
      | CrTagAdWithoutApiToken       | "fieldErrors":{"adNetReportApiToken":"Value should not be empty"}            | AV102    |
      | CrTagAdWithBigApiKey         | "fieldErrors":{"adNetReportApiKey":"Incorrect value length"}                 | AV104    |
      | CrTagAdWithIncorrectApiKey   | "fieldErrors":{"adNetReportApiKey":"The value does not match the pattern"}   | AV105    |
      | CrTagAdWithBigApiToken       | "fieldErrors":{"adNetReportApiToken":"Incorrect value length"}               | AV107    |
      | CrTagAdWithIncorrectApiToken | "fieldErrors":{"adNetReportApiToken":"The value does not match the pattern"} | AV108    |

  Scenario: create an Ad Network tag with empty api key
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithEmptyApiKey.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And response failed with ""fieldErrors":{"adNetReportApiKey":"The value does not match the pattern, Value should not be empty"}" field error or with this one ""fieldErrors":{"adNetReportApiKey":"Value should not be empty, The value does not match the pattern"}"
    And tag "AV103" is not retrieved from database

  Scenario: create an Ad Network tag with empty api key
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithEmptyApiToken.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And response failed with ""fieldErrors":{"adNetReportApiToken":"The value does not match the pattern, Value should not be empty"}" field error or with this one ""fieldErrors":{"adNetReportApiToken":"Value should not be empty, The value does not match the pattern"}"
    And tag "AV106" is not retrieved from database

  Scenario Outline: create an Ad Network tag with combinations of adNetReportUsername, adNetReportPassword fields are not allowed
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                  | response_message                                              | tag_name |
      | CrTagAdWithApiKeyNotAllowed   | "fieldErrors":{"adNetReportApiKey":"Value should be empty"}   | AV111    |
      | CrTagAdWithApiTokenNotAllowed | "fieldErrors":{"adNetReportApiToken":"Value should be empty"} | AV112    |

  Scenario Outline: create an Ad Network tag with combinations of adNetReportUsername, adNetReportPassword fields are required
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<response_message>"
    And tag "<tag_name>" is not retrieved from database

    Examples:
      | file_payload                 | response_message                                                             | tag_name |
      | CrTagAdWithoutUsername       | "fieldErrors":{"adNetReportUserName":"Value should not be empty"}            | AV113    |
      | CrTagAdWithoutPassword       | "fieldErrors":{"adNetReportPassword":"Value should not be empty"}            | AV114    |
      | CrTagAdWithBigUsername       | "fieldErrors":{"adNetReportUserName":"Incorrect value length"}               | AV116    |
      | CrTagAdWithIncorrectUsername | "fieldErrors":{"adNetReportUserName":"The value does not match the pattern"} | AV117    |
      | CrTagAdWithBigPassword       | "fieldErrors":{"adNetReportPassword":"Incorrect value length"}               | AV119    |
      | CrTagAdWithIncorrectPassword | "fieldErrors":{"adNetReportPassword":"The value does not match the pattern"} | AV120    |

  Scenario: create an Ad Network tag with empty username
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithEmptyUsername.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And response failed with ""fieldErrors":{"adNetReportUserName":"The value does not match the pattern, Value should not be empty"}" field error or with this one ""fieldErrors":{"adNetReportUserName":"Value should not be empty, The value does not match the pattern"}"
    And tag "AV115" is not retrieved from database

  Scenario: create an Ad Network tag with empty password
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdWithEmptyPassword.json"
    Then "create tag" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And response failed with ""fieldErrors":{"adNetReportPassword":"The value does not match the pattern, Value should not be empty"}" field error or with this one ""fieldErrors":{"adNetReportPassword":"Value should not be empty, The value does not match the pattern"}"
    And tag "AV118" is not retrieved from database

  @restoreCrudCoreDatabaseBefore

  Scenario: create an Ad Network tag for disabled ad source
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdDisabledSource.json"
    Then "create tag" failed with "400" response code
    And tag "AV240" is not retrieved from database

  Scenario Outline: create an AOL tag with all fields
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully
    And tag "<tag_pid>" is retrieved from database
    And AOL Mobile tag "<tag_pid>" has correct additional values

    Examples:
      | file_payload      | tag_pid   |
      | CrTagAolAllFields | AV011     |
      | CrTagAolForUpdate | updateAol |

  Scenario Outline: create an Ad tag for checking update unique parameters
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully

    Examples:
      | file_payload                  |
      | CrTagAdPidPnameSecond         |
      | CrTagAdPidPnameSidSecond      |
      | CrTagAdPidPnameSidSnameSecond |
      | CrTagAdPidPnameSnameSecond    |
      | CrTagAdPidSecond              |
      | CrTagAdPidSidSecond           |
      | CrTagAdPidSidSnameSecond      |
      | CrTagAdPidSnameSecond         |
      | CrTagAdPidPnameSample         |
      | CrTagAdPidPnameSidSample      |
      | CrTagAdPidPnameSidSnameSample |
      | CrTagAdPidPnameSnameSample    |
      | CrTagAdPidSample              |
      | CrTagAdPidSidSample           |
      | CrTagAdPidSidSnameSample      |
      | CrTagAdPidSnameSample         |

  Scenario: create an Ad Network tag to update it later
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagAdRevenueTrueProvisionAutoManualZero.json"
    Then request passed successfully
    And tag "AV291" is retrieved from database

  Scenario Outline: get an AOL and Ad Network tags
    And set tag pid for name "<tag_pid>"
    And the tag data for provision API is retrieved
    Then request passed successfully
    And returned "provision tag" data matches the following json file "jsons/genevacrud/tag/provision_api/ER/<file_payload>.json"

    Examples:
      | file_payload                        | tag_pid   |
      | GetTagAdWithIntFieldsNotRequired_ER | AV291     |
      | GetTagAolWithoutRules_ER            | updateAol |

  Scenario: get a tag that does not belong to a position or site
    And set tag pid for name "A24 - T24A"
    And the tag data for provision API is retrieved
    Then "create tag" failed with "404" response code and error message "Tag doesn't exist in database"

  Scenario: update an AOL tag to an Adnetwork tag
    And set tag pid for name "updateAol"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAolToAd.json"
    Then "update tag" failed with "400" response code and error message "Exchange Tag and Profile don't match"
    And tag "b1" is not retrieved from database

  Scenario: update an Adnetwork tag to an AOL tag
    And set tag pid for name "AV291"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAdToAol.json"
    Then "update tag" failed with "401" response code and error message "Buyer is read-only"
    And tag "b2" is not retrieved from database

  Scenario: update an AOL tag with duplicate name
    And set tag pid for name "updateAol"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAolAllFields.json"
    Then "update tag" failed with "400" response code and error message "The tag name already exists in this position"
    And there is/are only "1" tags for tag name "AV011"

  Scenario: update an Ad Network tag with duplicate name for the same buyer
    And set tag pid for name "AV291"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAdDuplicateSameBuyer.json"
    Then request passed successfully
    And there is/are only "1" tags for tag name "AV291"

  Scenario: update an Ad Network tag with duplicate name for the different buyer
    And set tag pid for name "AV291"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAdForDuplicate.json"
    Then "update tag" failed with "401" response code and error message "Buyer is read-only"
    And there is/are only "1" tags for tag name "AV291"

  Scenario Outline: update an Ad Network tag with the same primary Id/name, secondary Id/name
    And set tag pid for name "<cr_tag_name>"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"primaryId":"Integration parameters should be unique"}"
    And tag "<up_tag_name>" is not retrieved from database

    Examples:
      | file_payload                  | up_tag_name | cr_tag_name |
      | UpTagAdPidPnameSidSnameSample | b3          | AV2211      |
      | UpTagAdPidPnameSidSample      | b4          | AV2221      |
      | UpTagAdPidPnameSnameSample    | b5          | AV2231      |
      | UpTagAdPidSidSnameSample      | b6          | AV2241      |
      | UpTagAdPidPnameSample         | b7          | AV2251      |
      | UpTagAdPidSnameSample         | b8          | AV2261      |
      | UpTagAdPidSidSample           | b9          | AV2271      |
      | UpTagAdPidSample              | b10         | AV2281      |

  Scenario: update an AOL tag with new name
    And set tag pid for name "AV011"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAolNewName.json"
    Then request passed successfully
    And tag "b11" is retrieved from database
    And AOL Mobile tag "b11" has correct additional values

  Scenario: update an Ad Network tag with new name
    And set tag pid for name "AV291"
    When the user updates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/UpTagAdNewName.json"
    Then request passed successfully
    And tag "b12" is retrieved from database
    And Ad Network tag "b12" has correct additional values

  @unstable
  Scenario Outline: delete an AOL and Ad Network tags
    And set tag pid for name "<tag_pid>"
    And tag for provision API is deleted
    Then request passed successfully
    And status of the tag with the tag name "tag_pid" is "-1"

    Examples:
      | tag_pid |
      | AV011   |
      | AV100   |
