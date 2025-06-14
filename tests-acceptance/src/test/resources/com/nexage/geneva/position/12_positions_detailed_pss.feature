Feature: Detailed position tests

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

# No Video tag in position and placementVideo
  Scenario: get detailed position of different placement categories without video
    Given the user "sri1001firstsri1312" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "Provision"
    And the user selects the site "mobilepro"
    And position by pid "100328" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then the returned data doesn't contain the following fields "placementVideo"

# VideoTag in both position and videoPlacement or only in VideoPlacement without companion data 1
# VideoTag in both position and videoPlacement or only in VideoPlacement with companion data 3
  Scenario Outline: get detailed position of different placement categories with video and without companion data
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "<site_name>"
    And position by pid "<position_pid>" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/detailed/<file_ER>.json"

    Examples:
      | site_name                      | position_pid | file_ER                                |
      | CRUDPosition_Site1             | 100248       | DetailPositionWithOnlyVideo_1_ER       |
      | CRUDPosition_Site5_VersionTest | 100312       | DetailPositionWithVideoAndCompanion_ER |

  Scenario: Test external user for getting detailed position of different placement categories with video and without companion data
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000174" for the company with pid "10201"
    And position by pid "100248" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then response failed with "403" response code, error message "Forbidden" and without field errors.

#VideoTag in only position and not in videoPlacement
  Scenario: get detailed position of different placement categories with video in only position table
    Given the user "sri1001firstsri1312" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "Provision"
    And the user selects the site "mobilepro"
    And position by pid "100326" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then request passed successfully with code "200"
    And the returned data doesn't contain the following fields "placementVideo"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/detailed/DetailPositionWithVideoInPosition_ER.json"

  Scenario: get detailed position for longform
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site1"
    And position with name "smartYield_updateCases" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/pss/payload/update/UpdatePositionWithLongformVideoForReading_payload.json" with detail "false"
    And position by pid "100316" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/detailed/DetailedPositionWithLongformVideo_ER.json"

  @restartWiremockAfter
  Scenario: restart wiremock server
    Then nothing else to be done
