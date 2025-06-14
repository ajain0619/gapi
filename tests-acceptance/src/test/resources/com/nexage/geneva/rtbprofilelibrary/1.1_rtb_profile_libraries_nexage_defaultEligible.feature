Feature: update, delete, search RTB Profile Libraries with Default Eligible Bidder as Nexage Admin user

  Background: user logs in
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "defaultBidder"

  Scenario: search all RTB Profile Libraries
    When the user searches all RTB Profile Libraries
    Then request passed successfully
    And returned "RTB Profile Libraries" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/GetAll_ER.json"

  Scenario: search RTB Profile Library for a specific Publisher without ref seller_admin parameter
    When the PSS user searches all RTB Profile Libraries for a publisher without ref seller_admin parameter
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/Get_withoutParameter.json"

  Scenario: search RTB Profile Library for a specific Publisher WITH ref seller_admin parameter
    When the PSS user searches all RTB Profile Libraries for a publisher WITH ref seller_admin parameter
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/Get_withParameter.json"

  Scenario: search RTB Profile Library
    When the user searches RTB Profile Library "Default Eligible Bidder Group"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/Get_DFLT.json"

  Scenario: update RTB Profile Library with different value for isDefaultEligible
    When the user searches RTB Profile Library "Default Eligible Bidder Group"
    And the user updates the RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/UpdateEligible_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/update/UpdateEligible_ER.json"

  Scenario: search RTB Profile Library for a specific Publisher without ref seller_admin parameter - default should be seen since it was updated to false
    When the PSS user searches all RTB Profile Libraries for a publisher without ref seller_admin parameter
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/Get_withoutParameterFalse.json"

  Scenario: create RTB Profile Library
    When the user clones RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Clone_payloadDefault.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/payload/create/CloneResult_payloadDefault.json"

  Scenario: delete RTB Profile Library
    When the user searches RTB Profile Library "TEST DEFAULT"
    And the user deletes the RTB Profile Library with follow redirect is "true"
    Then request passed without errors
    And the user cannot search deleted library
