@unstable
Feature: audit for positions

  Background: log in as seller admin
    Given the user "admin1c" has logged in
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site1"

  Scenario: update position and check audit by date range
    Given position with name "banner_placement" is selected
    When the user updates selected position from the json file "jsons/genevacrud/position/payload/update/UpdateBannerToNative_payload.json"
    Then request passed successfully
    And the test specifies the date range from "start" to "stop"
    And the user selects the position to audit
    Then request passed successfully
    And returned "position audit by date range" data matches the following json file "jsons/genevacrud/audit/position/expected_results/DateRangeAudit_ER.json"

  Scenario: update position and check audit by revision
    Given position with name "banner_placement" is selected
    When the user updates selected position from the json file "jsons/genevacrud/position/payload/update/UpdateNativeToVideo_payload.json"
    Then request passed successfully
    Then the user select a specific revision for position "252"
    Then request passed successfully
    And returned "position audit by revision" data matches the following json file "jsons/genevacrud/audit/position/expected_results/RevisionAudit_ER.json"
