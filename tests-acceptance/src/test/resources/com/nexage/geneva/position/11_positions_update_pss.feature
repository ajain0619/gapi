Feature: Update pss endpoint tests

  Scenario Outline: update position with video
    Given the user "<one_central_username>" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "<company_name>"
    And the user selects the site "<site_name>"
    And position with name "<position_name>" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/pss/payload/update/<file_payload>.json" with detail "false"
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload                                              | file_ER                                              | one_central_username | site_name                      | company_name     | position_name          |
      | UpdatePositionInstreamWithoutPlacementVideo_payload       | UpdatePositionInstreamWithoutPlacementVideo_ER       | NexageAdmin1         | mobilepro                      | Provision        | instreamprovision      |
      | UpdatePositionWithPlacementVideoMultipleCompanion_payload | UpdatePositionWithPlacementVideoMultipleCompanion_ER | NexageAdmin1         | mobilepro                      | Provision        | interstitialprovision  |
      | UpdatePositionWithPlacementVideoOneCompanion_payload      | UpdatePositionWithPlacementVideoOneCompanion_ER      | NexageAdmin1         | CRUDPosition_Site5_VersionTest | CRUDPositionTest | position3              |
      | UpdateBannerPlacementToVideo_payload                      | UpdateBannerPlacementToVideo_ER                      | NexageAdmin1         | CRUDPosition_Site1             | CRUDPositionTest | banner_placement       |
      | UpdatePositionWithPlacementVideoDeleteCompanion_payload   | UpdatePositionWithPlacementVideoDeleteCompanion_ER   | NexageAdmin1         | mobilepro                      | Provision        | interstitialprovision  |

  Scenario: Test external user for updating position with video
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000205" for the company with pid "10201"
    And position by pid "100326" is retrieved from database
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/pss/payload/update/UpdatePositionInstreamWithoutPlacementVideo_payload.json" with detail "false"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario Outline: update placement fails with correct error message
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site4_UpdateCases"
    And position with name "CRUDPosition_UpdateCases" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/pss/payload/invalid/<file_payload>.json" with detail "false"
    Then "position update" failed with "400" response code and error message "<fail_msg>"

    Examples:
      | file_payload                                                     | fail_msg                                        |
      | UpdatePositionInArticleScreenLocationInvalid_payload             | "Placement Screen Location is invalid"          |
      | UpdatePositionInFeedScreenLocationInvalid_payload                | "Placement Screen Location is invalid"          |
      | UpdatePositionInArticleMraidSupportInvalid_payload               | "Placement MRAID Support is invalid"            |
      | UpdatePositionInFeedMraidSupportInvalid_payload                  | "Placement MRAID Support is invalid"            |
      | UpdatePositionImpressionTypeHandlingInvalid_payload              | "Placement Impression Type Handling is invalid" |

  @restartWiremockAfter
  Scenario: restart wiremock server
    Then nothing else to be done
