@unstable @provision
Feature: Sites: get, create, update, delete through Provision API

  Background: : A token is fetched to authenticate to the provision API
    Given setup wiremock user for provision API
    Given the user fetches an authentication token
    And set company "Provision"

  Scenario: Create a site without fields for an admin
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithoutFields_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""iabCategories":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""coppaRestricted":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""domain":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""name":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""type":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""platform":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""url":"Value should not be empty""

  Scenario Outline: Create a site for IOS with combinations of platforms and all fields
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then request passed successfully
    And dcn is retrieved for site name "<site_name>"
    And status of the site with the site name "<site_name>" is "1"

    Examples:
      | site_name        | file_payload                            |
      | IPAD_IPHONE_site | CreateSitePrIpadIphoneAllFields_payload |
      | IPHONE_site      | CreateSitePrIphoneAllFields_payload     |
      | i                | CreateSitePrIpadAllFields_payload       |

  Scenario Outline: Create a site for Android with combinations of platforms and all fields
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then request passed successfully
    And dcn is retrieved for site name "<site_name>"
    And status of the site with the site name "<site_name>" is "1"

    Examples:
      | site_name                                                                                                                                                                                                                                                       | file_payload                             |
      | 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345 | CreateSitePrTabletPhoneAllFields_payload |
      | PHONE_site                                                                                                                                                                                                                                                      | CreateSitePrPhoneAllFields_payload       |
      | TABLET_site                                                                                                                                                                                                                                                     | CreateSitePrTabletAllFields_payload      |

  Scenario: Create a site for Mobile with all fields
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrMobileAllFields_payload.json"
    Then request passed successfully
    And dcn is retrieved for site name "Mobile_site"
    And status of the site with the site name "Mobile_site" is "1"

  Scenario: Create a site for Desktop with all fields
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrDesktopAllFields_payload.json"
    Then request passed successfully
    And dcn is retrieved for site name "Desktop_site"
    And status of the site with the site name "Desktop_site" is "1"

  Scenario Outline: Create a site with empty name field or without name
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"

    Examples:
      | file_payload                      | field_errors                                        |
      | CreateSitePrWithEmptyName_payload | "fieldErrors":{"name":"Entered value is too short"} |
      | CreateSitePrWithoutName_payload   | "fieldErrors":{"name":"Value should not be empty"}  |

  Scenario: Create a site with whitespace name field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithWhitespaceName_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"name":"The value does not match the pattern"}"
    And site with the site name "  " is not generated

  Scenario: Create a site with extra large name field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithExtraLargeName_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"name":"Entered value is too long"}"
    And site with the site name "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456" is not generated

  Scenario Outline: Create a site without description field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then request passed successfully
    And dcn is retrieved for site name "<site_name>"
    And status of the site with the site name "<site_name>" is "1"

    Examples:
      | site_name                | file_payload                               |
      | Without_description_ios  | CreateSitePrWithoutDescriptionIos_payload  |
      | Without_description_andr | CreateSitePrWithoutDescriptionAndr_payload |
      | Without_description_mob  | CreateSitePrWithoutDescriptionMob_payload  |
      | Without_description_desc | CreateSitePrWithoutDescriptionDesc_payload |

  Scenario: Create a site with extra large description field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrExtraLargeDescription_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"description":"Incorrect value length"}"
    And site with the site name "a1" is not generated

  Scenario Outline: Create a mobile site with incorrect url values
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                                | field_errors                                                 |
      | a50       | CreateSitePrMobileWithoutUrl_payload        | "fieldErrors":{"url":"Value should not be empty"}            |
      | a51       | CreateSitePrMobileWithEmptyUrl_payload      | "fieldErrors":{"url":"Entered value is too short"}           |
      | a52       | CreateSitePrMobileWithWhitespaceUrl_payload | "fieldErrors":{"url":"The value does not match the pattern"} |
      | a53       | CreateSitePrMobileWithExtraLargeUrl_payload | "fieldErrors":{"url":"Entered value is too long"}            |

  Scenario: Create a mobile site with max length url field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrMobileMaxLengthUrl_payload.json"
    Then request passed successfully
    And dcn is retrieved for site name "mobile_max_url_length"
    And status of the site with the site name "mobile_max_url_length" is "1"

  Scenario Outline: Create a site with incorrect domain values
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                             | field_errors                                                    |
      | a2        | CreateSitePrWithoutDomain_payload        | "fieldErrors":{"domain":"Value should not be empty"}            |
      | a3        | CreateSitePrWithEmptyDomain_payload      | "fieldErrors":{"domain":"Entered value is too short"}           |
      | a54       | CreateSitePrWithWhitespaceDomain_payload | "fieldErrors":{"domain":"The value does not match the pattern"} |
      | a4        | CreateSitePrWithExtraLargeDomain_payload | "fieldErrors":{"domain":"Entered value is too long"}            |

  Scenario Outline: Create an IOS or Android sites with incorrect appBundle values
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                                   | field_errors                                                       |
      | a5        | CreateSitePrIOSWithoutAppId_payload            | "fieldErrors":{"appBundle":"Value should not be empty"}            |
      | a55       | CreateSitePrIOSWithWhitespaceAppId_payload     | "fieldErrors":{"appBundle":"The value does not match the pattern"} |
      | a6        | CreateSitePrIOSWithExtraLargeAppId_payload     | "fieldErrors":{"appBundle":"Incorrect value length"}               |
      | a56       | CreateSitePrIOSWithDifferentAppId_payload      | "fieldErrors":{"appBundle":"The value does not match the pattern"} |
      | a7        | CreateSitePrAndroidWithoutAppId_payload        | "fieldErrors":{"appBundle":"Value should not be empty"}            |
      | a57       | CreateSitePrAndroidWithWhitespaceAppId_payload | "fieldErrors":{"appBundle":"The value does not match the pattern"} |
      | a8        | CreateSitePrAndroidWithExtraLargeAppId_payload | "fieldErrors":{"appBundle":"Incorrect value length"}               |
      | a58       | CreateSitePrAndroidWithDifferentAppId_payload  | "fieldErrors":{"appBundle":"The value does not match the pattern"} |
      | a75       | CreateSitePrMobileWithNonEmptyAppId_payload    | "fieldErrors":{"appBundle":"Value should be empty"}                |

  Scenario Outline: Create an IOS or android site with incorrect url values
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                                      | field_errors                                                                          |
      | a9        | CreateSitePrIOSWithoutUrl_payload                 | "fieldErrors":{"appBundle":"Value should be empty","url":"Value should not be empty"} |
      | a10       | CreateSitePrIOSWithIncorrectFormatUrl_payload     | "fieldErrors":{"url":"The value does not match the pattern"}                          |
      | a11       | CreateSitePrIOSWithExtraLargeUrl_payload          | "fieldErrors":{"url":"Entered value is too long"}                                     |
      | a60       | CreateSitePrIOSWithoutIdUrl_payload               | "fieldErrors":{"url":"The value does not match the pattern"}                          |
      | a59       | CreateSitePrIOSWithAndroidFormatUrl_payload       | "fieldErrors":{"url":"The value does not match the pattern"}                          |
      | a12       | CreateSitePrAndroidWithoutUrl_payload             | "fieldErrors":{"appBundle":"Value should be empty","url":"Value should not be empty"} |
      | a13       | CreateSitePrAndroidWithIncorrectFormatUrl_payload | "fieldErrors":{"url":"The value does not match the pattern"}                          |
      | a14       | CreateSitePrAndroidWithExtraLargeUrl_payload      | "fieldErrors":{"url":"Entered value is too long"}                                     |
      | a60       | CreateSitePrAndroidWithoutIdUrl_payload           | "fieldErrors":{"url":"The value does not match the pattern"}                          |
      | a61       | CreateSitePrAndroidWithIOSUrl_payload             | "fieldErrors":{"url":"The value does not match the pattern"}                          |

  Scenario: Create a site without coppaRestricted field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithoutCoppaRestricted_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"coppaRestricted":"Value should not be empty"}"
    And site with the site name "a15" is not generated

  Scenario: Create a site with incorrect value coppaRestricted field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithIncorrectCoppaRestricted_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and without field errors.
    And site with the site name "a16" is not generated

  Scenario Outline: Create an IOS or Android site without platform field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"appBundle":"Value should be empty","platform":"Value should not be empty"}"
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                               |
      | a62       | CreateSitePrIOSWithoutPlatform_payload     |
      | a64       | CreateSitePrAndroidWithoutPlatform_payload |

  Scenario Outline: Create a desktop site with Android/IOS platform field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""url":"The value does not match the pattern""
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                               | field_errors                                                          |
      | b89       | CreateSitePrDesktopIOSPlatform_payload     | "platform":"The value 'IPAD' does not supported for 'DESKTOP'"        |
      | b90       | CreateSitePrDesktopAndroidPlatform_payload | "platform":"The value 'ANDROID_TAB' does not supported for 'DESKTOP'" |

  Scenario: Create a site with incorrect platform field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrIncorrectPlatform_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and without field errors.
    And site with the site name "a63" is not generated

  Scenario: Create a site with incorrect format iabCategories field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithIncorrectFormatIabCategories_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and without field errors.
    And site with the site name "a18" is not generated

  Scenario: Create a site without iabCategories field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithoutIabCategories_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"iabCategories":"Value should not be empty"}"
    And site with the site name "a17" is not generated

  Scenario Outline: Create a site with incorrect values in iabCategories field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name | file_payload                                              | field_errors                                                                                       |
      | a67       | CreateSitePrWithIncorrectValueIabCategories_payload       | "fieldErrors":{"iabCategories":"Category '1' was not found"}                                       |
      | a68       | CreateSitePrWithEmptyValueIabCategories_payload           | "fieldErrors":{"iabCategories":"The number of elements in the array is less than allowed"}         |
      | a69       | CreateSitePrWithParentChildrenValuesIabCategories_payload | "fieldErrors":{"iabCategories":"Can't use category 'IAB1-1' because parent category selected too"} |
      | a19       | CreateSitePrWithExtraIabCategories_payload                | "fieldErrors":{"iabCategories":"The number of elements in the array is greater than allowed"}      |

  Scenario: Create a site without type field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithoutType_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""fieldErrors":{"type":"Value should not be empty"}"
    And site with the site name "a20" is not generated

  Scenario: Create a site with incorrect format type field
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithIncorrectFormatType_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and without field errors.
    And site with the site name "a21" is not generated

  Scenario: Create a duplicate site with correct values
    When the user creates a site using provision API from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrIpadIphoneAllFields_payload.json"
    Then response failed with "400" response code, error message "Site exists with the given name" and without field errors.

  Scenario Outline: Get an IOS, android, mobile or desktop site
    When set site "<site_name>"
    And the site data for provision API is retrieved
    Then request passed successfully
    Then returned "provision site" data matches the following json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"

    Examples:
      | site_name        | file_payload                            |
      | IPAD_IPHONE_site | CreateSitePrIpadIphoneAllFields_payload |
      | TABLET_site      | CreateSitePrTabletAllFields_payload     |
      | Mobile_site      | CreateSitePrMobileAllFields_payload     |
      | Desktop_site     | CreateSitePrDesktopAllFields_payload    |

  Scenario: Update an IOS site to an android site
    When set site "IPAD_IPHONE_site"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/UpdateSitePrAndroidAllFields_payload.json"
    Then request passed successfully
    Then returned "provision site" data matches the following json file "jsons/genevacrud/site/provisionApi/ER/UpdateSitePrAndroidAllFields_ER.json"
    And dcn is retrieved for site name "Updated_to_android"
    And status of the site with the site name "Updated_to_android" is "1"
    And site with the site name "IPAD_IPHONE_site" is not generated

  Scenario: Update an Android site to an IOS site
    When set site "TABLET_site"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/UpdateSitePrIOSAllFields_payload.json"
    Then request passed successfully
    Then returned "provision site" data matches the following json file "jsons/genevacrud/site/provisionApi/ER/UpdateSitePrIOSAllFields_ER.json"
    And dcn is retrieved for site name "Updated_android_ios"
    And status of the site with the site name "Updated_android_ios" is "1"
    And site with the site name "TABLET_site" is not generated

  Scenario Outline: Update an IOS site to a Mobile site or Desktop site
    When set site "Without_description_ios"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Site type cannot be changed" and without field errors.
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name           | file_payload                            |
      | Updated_ios_mobile  | UpdateSitePrIosMobileAllFields_payload  |
      | Updated_ios_desktop | UpdateSitePrIosDesktopAllFields_payload |

  Scenario Outline: Update an Android site to a Mobile site or Desktop site
    When set site "Without_description_andr"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Site type cannot be changed" and without field errors.
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name               | file_payload                                |
      | Updated_android_mobile  | UpdateSitePrAndroidMobileAllFields_payload  |
      | Updated_android_desktop | UpdateSitePrAndroidDesktopAllFields_payload |

  Scenario Outline: Update a Mobile site to an IOS, Android or Desktop site
    When set site "Mobile_site"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Site type cannot be changed" and without field errors.
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name              | file_payload                               |
      | Updated_mobile_ios     | UpdateSitePrMobileIOSAllFields_payload     |
      | Updated_mobile_android | UpdateSitePrMobileAndroidAllFields_payload |
      | Updated_mobile_desktop | UpdateSitePrMobileDesktopAllFields_payload |

  Scenario Outline: Update a Desktop site to an IOS, Android or Mobile site
    When set site "Desktop_site"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Site type cannot be changed" and without field errors.
    And site with the site name "<site_name>" is not generated

    Examples:
      | site_name               | file_payload                                |
      | Updated_desktop_ios     | UpdateSitePrDesktopIOSAllFields_payload     |
      | Updated_desktop_android | UpdateSitePrDesktopAndroidAllFields_payload |
      | Updated_desktop_mobile  | UpdateSitePrDesktopMobileAllFields_payload  |

  Scenario: Update a site with the existing name
    When set site "Mobile_site"
    And the user updates site provision with data from the json file "jsons/genevacrud/site/provisionApi/payload/CreateSitePrWithoutDescriptionMob_payload.json"
    Then "Update a site with the existing name" failed with "500" response code
    And there is only "1" site with specified name "Without_description_mob"

