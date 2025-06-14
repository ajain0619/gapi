Feature: get, create, update, delete mediation ad source defaults as Nexage Admin

  Background: log in as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user specifies the date range from "2015-12-30T23:59:59-04:00" to "2016-01-04T00:00:00-04:00"
    And ad source summaries are retrieved
    And "MEDIATION" ad source type is selected

  @restoreCrudCoreDatabaseBefore

  Scenario: get all mediation ad source defaults
    When the user gets all ad source defaults
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/GetAllAdSourceDefaults_ER.json"

  Scenario: get mediation ad source defaults
    Given the user selects ad source with pid "7008"
    When the user gets mediation ad source defaults
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/GetAdSourceDefaults_ER.json"

  Scenario: get mediation ad source defaults
    Given the user selects ad source with pid "7048"
    When the user gets mediation ad source defaults
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/GetAdSourceDefaultsMX135_ER.json"

  Scenario: update all fields in mediation ad source defaults
    Given the user selects ad source with pid "7008"
    When the user updates ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/mediation/payload/UpdateAdSourceDefaultsAllFields_payload.json"
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/UpdateAdSourceDefaultsAllFields_ER.json"

  Scenario: update all fields in mediation ad source defaults
    Given the user selects ad source with pid "7048"
    When the user updates ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/mediation/payload/UpdateAdSourceDefaultsAllFieldsMX135_payload.json"
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/UpdateAdSourceDefaultsAllFieldsMX135_ER.json"

  Scenario: delete mediation ad source defaults
    Given the user selects ad source with pid "7008"
    When the user deletes mediation ad source defaults
    Then request passed with "204" response code
    And the user gets mediation ad source defaults
    And "mediation ad source defaults search" failed with "404" response code

  Scenario: create mediation ad source defaults
    Given the user selects ad source with pid "7021"
    When the user creates mediation ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/mediation/payload/CreateAdSourceDefaults_payload.json"
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/CreateAdSourceDefaults_ER.json"

  Scenario: get mediation adnets/buyers
    When the PSS user tries to get list of available adnets
    Then request passed successfully
    And returned "Ad source" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/expected_results/GetAdSourceBuyerNexage_ER.json"

  Scenario: get defaults for non existing mediation ad source will fail
    Given the user selects ad source with pid "0000"
    When the user gets mediation ad source defaults
    Then "mediation ad source defaults search" failed with "404" response code
