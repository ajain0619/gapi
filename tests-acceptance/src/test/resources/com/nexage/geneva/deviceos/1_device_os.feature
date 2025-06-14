Feature: search device types with targets as nexage admin

  Background: log in
    Given the user "admin1c" has logged in

  Scenario: search for all device os
    When insert device os data of ids "1,2,3" and names "linux,android,ios"
    And the user searches for all device os
    Then request passed successfully
    And returned "deviceOs" data matches the following json file "jsons/genevacrud/deviceos/expected_results/search/AllDeviceOs_ER.json"

  Scenario: search for all device os with invalid search param pid
    When the user searches for device os with query field "pid" and search term "linux"
    And "get device os" failed with "Search request has invalid pid parameter" response message

  Scenario: search for all device os by name value "android"
    When the user searches for device os with query field "name" and search term "android"
    Then request passed successfully
    And returned "deviceOs" data matches the following json file "jsons/genevacrud/deviceos/expected_results/search/searchDeviceOsParam_ER.json"
