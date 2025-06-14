Feature: Only see active adnets when creating tag or ad source

  @restoreCrudCoreDatabaseBefore

  Scenario: get the list of all available ad nets for a pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user specifies the date range from "2016-01-04T00:00:00-04:00" to "2015-12-30T23:59:59-04:00"
    And ad source summaries are retrieved
    And "MEDIATION" ad source type is selected
    And the user gets all ad source defaults
    And the PSS user tries to get list of available adnets
    And selfServeEnablement is correct
    Then request passed successfully
    And returned "ad nets" data matches the following json file "jsons/genevacrud/adnet/expected_results/PSSAdnetsEnabled_ER.json"

  # The below tests are for MX-361 tests the adnets returned for multiple seller accounts as well as nexage users
  # nexage users should see all adsources
  # Seller users should see all pss and additional adnetworks for respective seller company
  Scenario Outline: get the list of all available ad nets for seller1 admin
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "<company>"
    And the user specifies the date range from "2016-01-04T00:00:00-04:00" to "2015-12-30T23:59:59-04:00"
    And ad source summaries are retrieved
    And "MEDIATION" ad source type is selected
    And the user gets all ad source defaults
    And the PSS user tries to get list of available adnets
    And selfServeEnablement is correct
    Then request passed successfully
    And returned "ad nets" data matches the following json file "jsons/genevacrud/adnet/expected_results/<file_ER>.json"

    Examples:
      | user                   | role        | company                        | file_ER               |
      | admin1c                | AdminNexage | CRUDPositionTest               | AdNetsNexageUsers_ER  |
      | crudPositionAdmin      | AdminSeller | CRUDPositionTest               | AdNetsSeller1Admin_ER |
      | positionArchivingAdmin | AdminSeller | PositionArchiving_TestCompany1 | AdNetsSeller2Admin_ER |
