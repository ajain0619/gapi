Feature: Seller - Sites - Tags: get, create, update as Nexage Admin

  Background: log in as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario Outline: create a new tag
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    When the user creates the "<tag type>" tag from the json file "jsons/genevacrud/tag/payload/create/<file_payload>.json"
    Then request passed successfully
    And "created" tag data matches the following json file "jsons/genevacrud/tag/expected_results/create/<file_ER>.json"

    Examples:
      | tag type     | file_payload                            | file_ER                            |
      | non-exchange | NewNonExchangeTagRequiredFields_payload | NewNonExchangeTagRequiredFields_ER |
      | non-exchange | NewNonExchangeTagAllFields_payload      | NewNonExchangeTagAllFields_ER      |

  Scenario: create a new tag with SmartYield RuleType - this tag has adsource use_wrapped_sdk = 0
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagAllFields_SmartYieldSDK0_payload.json"
    Then "tag creation" failed with "400" response code

  Scenario: create a new client tag with SmartYield RuleType - this tag has adsource use_wrapped_sdk = 1
    Given the user selects the "Seller" company "MedFTCompany"
    And the user selects the site "S12C"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagAllFields_SmartYield_payload.json"
    Then request passed successfully
    And "created" tag data matches the following json file "jsons/genevacrud/tag/expected_results/create/NewNonExchangeTagAllFields_SmartYield_ER.json"

  Scenario: create a new client tag WITHOUT SmartYield RuleType - this tag has adsource use_wrapped_sdk = 1
    Given the user selects the "Seller" company "MedFTCompany"
    And the user selects the site "S12C"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagAllFields_NOSYSDK1_payload.json"
    Then request passed successfully
    And "created" tag data matches the following json file "jsons/genevacrud/tag/expected_results/create/NewNonExchangeTagAllFields_NOSYSDK1_ER.json"

  Scenario: create a new client tag with invalid SmartYield ruleType
    Given the user selects the "Seller" company "MedFTCompany"
    And the user selects the site "S12C"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagAllFields_SmartYieldInvalid_payload.json"
    Then "tag creation" failed with "400" response code

  Scenario: create invalid new tag will fail
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagInvalid_payload.json"
    Then "tag creation" failed with "400" response code
