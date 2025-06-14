Feature: search device types with targets as nexage admin

  Background: log in
    Given the user "admin1c" has logged in

  Scenario: search for all device Types
    When the user searches for all device types
    Then request passed successfully
    And returned "deviceType" data matches the following json file "jsons/genevacrud/devicetype/expected_results/search/AllDeviceTypes_ER.json"

  Scenario: search for all device Types with invalid search param pid
    When the user searches for device types with params "pid" and search term "phone"
    And "get device type" failed with "Search request has invalid pid parameter" response message

  Scenario: search for all device Types by name value "phone"
    When the user searches for device types with params "name" and search term "phone"
    Then request passed successfully
    And returned "deviceType" data matches the following json file "jsons/genevacrud/devicetype/expected_results/search/searchDeviceTypeParam_ER.json"
