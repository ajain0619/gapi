Feature: search identity providers as nexage admin

  Background: log in
    Given the user "admin1c" has logged in

  Scenario: search for all identity providers
    When the user searches for all identity providers
    Then request passed successfully
    And returned "Identity Provider" data matches the following json file "jsons/genevacrud/identityprovider/expected_results/get_all_identity_providers_ER.json"
