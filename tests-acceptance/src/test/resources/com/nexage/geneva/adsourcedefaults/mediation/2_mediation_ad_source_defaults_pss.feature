Feature: get, create, update, delete mediation ad source defaults as pss

  Background: log in as seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2016-01-13T00:00:00-04:00" to "2016-01-19T23:59:59-04:00"
    And "MEDIATION" ad source type is selected

  @restoreCrudCoreDatabaseBefore

  Scenario: get mediation ad source defaults
    Given the user selects ad source with pid "7048"
    When the user gets mediation ad source defaults
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/pss/expected_results/GetAdSourceDefaultsMX135_ER.json"

  Scenario: update all fields in mediation ad source defaults
    Given the user selects ad source with pid "7008"
    When the user updates ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/mediation/pss/payload/UpdateAdSourceDefaultsAllFields_payload.json"
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/pss/expected_results/UpdateAdSourceDefaultsAllFields_ER.json"

  Scenario: update all fields in mediation ad source defaults
    Given the user selects ad source with pid "7048"
    When the user updates ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/mediation/pss/payload/UpdateAdSourceDefaultsAllFieldsMX135_payload.json"
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/pss/expected_results/UpdateAdSourceDefaultsAllFieldsMX135_ER.json"

  Scenario: delete mediation ad source defaults
    Given the user selects ad source with pid "7008"
    When the user deletes mediation ad source defaults
    Then request passed with "204" response code
    And the user gets mediation ad source defaults
    And "mediation ad source defaults search" failed with "404" response code

  Scenario: create mediation ad source defaults
    Given the user selects ad source with pid "7021"
    When the user creates mediation ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/mediation/pss/payload/CreateAdSourceDefaults_payload.json"
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/pss/expected_results/CreateAdSourceDefaults_ER.json"

  Scenario: get mediation adnets/buyers
    When the PSS user tries to get list of available adnets
    Then request passed successfully
    And returned "Ad source" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/pss/expected_results/GetAdSourceBuyerPss_ER.json"

  Scenario: get defaults for non existing mediation ad source will fail
    Given the user selects ad source with pid "0000"
    When the user gets mediation ad source defaults
    Then "mediation ad source defaults search" failed with "404" response code

  Scenario: get defaults of another publisher will fail
    When the user gets mediation ad source defaults of another publisher
    Then "mediation ad source defaults search" failed with "401" response code
