@unstable
Feature: audit fir sites

  Background: log in as seller admin
    Given the user "admin1c" has logged in

  Scenario: update site and check audit by date range
    Given the user selects the "Seller" company "cpi_seller"
    And the user selects the site "cpi_site"
    And the user updates site with data from the json file "jsons/genevacrud/site/payload/UpdateSite_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/payload/UpdateSite_2_payload.json"
    And request passed successfully
    And the test specifies the date range from "start" to "stop"
    When the user selects the site to audit
    Then request passed successfully
    And returned "site audit by date range" data matches the following json file "jsons/genevacrud/audit/site/expected_results/DateRangeAudit_ER.json"

  Scenario: update site creating tag and check audit by revision
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagAllFields_payload.json"
    And request passed successfully
    When the user select a specific revision for site "252"
    Then request passed successfully
    And returned "site audit by revision" data matches the following json file "jsons/genevacrud/audit/site/expected_results/RevisionAudit_ER.json"
