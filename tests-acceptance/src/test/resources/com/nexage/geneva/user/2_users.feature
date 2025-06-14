Feature: search users, reset and change passwords for users, restrict and allow users access to site

  Scenario: search users by company name
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Athens1"
    When the user searches for users by selected company
    Then request passed successfully
    And returned "users" data matches the following json file "jsons/genevacrud/user/expected_results/AllUsersForCompanyPid_ER.json"

  Scenario: search allowed sites for user "athens1user1"
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And users are retrieved
    And user "athens1user1" is selected
    When the user retrieves allowed sites for selected user
    Then request passed successfully
    And returned "users" data matches the following json file "jsons/genevacrud/user/expected_results/AllowedSiteForAthens1user1_ER.json"

  Scenario: restrict user access to site
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And users are retrieved
    And user "athens1user1" is selected
    And the user retrieves allowed sites for selected user
    And the user "restricts" access to site "athens1site2" for selected user
    And request passed successfully
    When the user retrieves allowed sites for selected user
    Then request passed successfully
    And returned "user sites" data matches the following json file "jsons/genevacrud/user/expected_results/AllowedSiteForUserAfterRestrict_ER.json"

  Scenario: allow user access to site
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And users are retrieved
    And user "athens1manager1" is selected
    And the user retrieves allowed sites for selected user
    When the user "allows" access to site "athens1site2" for selected user
    Then request passed successfully
    And the user retrieves allowed sites for selected user
    And request passed successfully
    And returned "user sites" data matches the following json file "jsons/genevacrud/user/expected_results/AllowedSiteForUserAfterAllow_ER.json"
