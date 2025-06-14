Feature: create dooh screens

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Cooler Screens"

  Scenario: Initialize venue type
    When venue is initialized

  Scenario: Throws exception when creating invalid screens
    When the user creates screens for seller "10235" from JSON file sending as form data "jsons/genevacrud/dooh/screens/payload/CreateInvalidScreens_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"sellerScreenId": "must not be null"}"

  Scenario: Creates dooh screens with file
    When the user creates screens for seller "10235" from JSON file sending as form data "jsons/genevacrud/dooh/screens/payload/CreateValidScreens_payload.json"
    Then request passed successfully with code "201"
    And database contains the following ssp screen ids "10235-1234567abcd-efgh,10235-1234568abcd-efgh,10235-1234569abcd-efgh"

  Scenario: Reads dooh screens
    When the user reads screens for seller "10235"
    Then request passed successfully with code "200"
    And returned "doohScreens" data matches the following json file "jsons/genevacrud/dooh/screens/expected_results/GetScreens_ER.json"

  Scenario: Throws exception when creating screens with invalid restrictions
    When the user creates screens for seller "10235" from JSON file sending as form data "jsons/genevacrud/dooh/screens/payload/CreateInvalidScreensRestrictions_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"restrictions": "size must be between 1 and 444"}"

  Scenario: Throws exception when creating screens with invalid network
    When the user creates screens for seller "10235" from JSON file sending as form data "jsons/genevacrud/dooh/screens/payload/CreateInvalidScreensNetwork_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"network": "size must be between 1 and 64"}"

  Scenario: Throws exception when creating screens with invalid floorPrice
    When the user creates screens for seller "10235" from JSON file sending as form data "jsons/genevacrud/dooh/screens/payload/CreateInvalidScreensFloor_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"floorPrice": "must be greater than 0.0"}"
