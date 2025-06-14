Feature: Get position pss endpoint tests

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline:1 Get position with different placement categories with video
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "<site_name>"
    And position with name "<position_name>" is selected
    And the PSS user gets data for selected position
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/get/<file_ER>.json"

    Examples:
      | file_ER                             | site_name                      | position_name          |
      | GetPositionWithVideo_ER             | CRUDPosition_Site1             | interstitial_placement |
      | GetPositionWithVideoAndCompanion_ER | CRUDPosition_Site5_VersionTest | position3              |

  Scenario:2 Get positions for seller and site
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site1"
    And the PSS user gets all positions
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/get/GetPositions.json"

  Scenario: Test external user for getting position
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    When the user selects site pid "10000174" for the company with pid "10201"
    And position by pid "100248" is retrieved from database
    And the PSS user gets data for selected position
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  @restartWiremockAfter
  Scenario: restart wiremock server
