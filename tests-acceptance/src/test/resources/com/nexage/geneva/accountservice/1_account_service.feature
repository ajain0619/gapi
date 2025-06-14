Feature: user account service - login, logout, get logged user details

  Scenario: successful log in
    Given the user "admin1c" has logged in with role "AdminNexage"
    And services are available with follow redirect is "true"
    And the user selects the "Seller" company "debpublisher"
    When the company data is retrieved
    Then request passed successfully

  Scenario: get current logged user details
    Given the user "admin1c" has logged in with role "AdminNexage"
    When current user details are retrieved with follow redirect is "true"
    Then request passed successfully
    And returned "logged user details" data matches the following json file "jsons/genevacrud/accountservice/expected_results/GetUserDetails_ER.json"

  Scenario: successful log out
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user logs out
    And services are not available with follow redirect is "false"

  Scenario: Login via sso as a Nexage Manager
    Given the user "nexage1cmgr" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "debpublisher"
    When the company data is retrieved
    Then request passed successfully

  Scenario: Login via sso as a Nexage user
    Given the user "nexage1cuser" has logged in with role "UserNexage"
    And the user selects the "Seller" company "debpublisher"
    When the company data is retrieved
    Then request passed successfully

  Scenario: fail on login with invalid credentials
    When the user "INVALID" tries to log in

  Scenario: fail on getting current logged user details without login
    When the user logs out
    And current user details are retrieved with follow redirect is "false"
    Then redirect to authenticate page
