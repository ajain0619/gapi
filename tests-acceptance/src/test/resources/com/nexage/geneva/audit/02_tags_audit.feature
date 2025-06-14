@unstable
Feature: audit for tags

  Background: log in as seller admin
    Given the user "admin1c" has logged in

  Scenario: update tag(non-exchange) and check audit by date range
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user updates the "non-exchange" tag "Nexage-Adserver - test8d" from the json file "jsons/genevacrud/tag/payload/update/UpdateNonExchangeTagAllFields_payload.json"
    And request passed successfully
    And the test specifies the date range from "start" to "stop"
    And the user selects the tag to audit
    Then request passed successfully
    And returned "tag audit by date range" data matches the following json file "jsons/genevacrud/audit/tag/expected_results/DateRangeAudit_ER.json"

  Scenario: update tag(exchange) and check audit by revision
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user updates the "exchange" tag "Nexage Exchange - Nexage-Adserver - test8z" from the json file "jsons/genevacrud/tag/payload/update/UpdateExchangeTagAllFields_payload.json"
    And request passed successfully
    When the user selects a specific revision for tag "252"
    Then request passed successfully
    And returned "tag audit by revision" data matches the following json file "jsons/genevacrud/audit/tag/expected_results/RevisionAudit_ER.json"
