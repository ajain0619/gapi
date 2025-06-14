Feature: Create pss endpoint tests

  Scenario Outline: create position with different placement categories with video
    Given the user "<one_central_username>" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "<site_name>"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/create/<file_ER>.json"

    Examples:
      | file_payload                                             | file_ER                                             | one_central_username | site_name                      |
      | CreatePositionInStreamVideo_payload                      | CreatePositionInStreamVideo_ER                      | NexageAdmin1         | CRUDPosition_Site5_VersionTest |
      | CreatePositionInterstitial_payload                       | CreatePositionInterstitial_ER                       | NexageAdmin1         | CRUDPosition_Site5_VersionTest |
      | CreatePositionWithVideo_payload                          | CreatePositionWithVideo_ER                          | NexageAdmin1         | CRUDPosition_Site5_VersionTest |
      | CreatePositionWithLongformVideo_payload                  | CreatePositionWithLongformVideo_ER                  | NexageAdmin1         | CRUDPosition_Site5_VersionTest |
      | CreatePositionInArticle_payload                          | CreatePositionInArticle_ER                          | NexageAdmin1         | CRUDPosition_Site5_VersionTest |
      | CreatePositionInFeed_payload                             | CreatePositionInFeed_ER                             | NexageAdmin1         | CRUDPosition_Site5_VersionTest |
      | CreatePositionInFeedWithNullMraidAdvanceTracking_payload | CreatePositionInFeedWithNullMraidAdvanceTracking_ER | NexageAdmin1         | CRUDPosition_Site5_VersionTest |

  Scenario: Test external user for creating position with different placement categories with video
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000192" for the company with pid "10201"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/CreatePositionInStreamVideo_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario Outline: create placement with different placement categories without video
    Given the user "<one_central_username>" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "<site_name>"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "placement" data matches the following json file "jsons/genevacrud/position/pss/expected_results/create/<file_ER>.json"
    And the returned data doesn't contain the following fields "placementVideo"

    Examples:
      | file_payload                          | file_ER                          | one_central_username | site_name                             |
      | CreatePositionBanner_payload          | CreatePositionBanner_ER          | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePositionMediumRectangle_payload | CreatePositionMediumRectangle_ER | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePositionNative_payload          | CreatePositionNative_ER          | NexageAdmin1         | DealTagTestSite1-App-ImpressionGroups |
      | CreatePositionWithoutName_payload     | CreatePositionWithoutName_ER     | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |

  Scenario Outline: create placement fails with correct error message
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/invalid/<file_payload>.json"
    Then "position create" failed with "400" response code and error message "<fail_msg>"

    Examples:
      | file_payload                                                     | fail_msg                                        |
      | CreatePositionInArticleScreenLocationInvalid_payload             | "Placement Screen Location is invalid"          |
      | CreatePositionInFeedScreenLocationInvalid_payload                | "Placement Screen Location is invalid"          |
      | CreatePositionInArticleMraidSupportInvalid_payload               | "Placement MRAID Support is invalid"            |
      | CreatePositionInFeedMraidSupportInvalid_payload                  | "Placement MRAID Support is invalid"            |
      | CreatePositionImpressionTypeHandlingInvalid_payload              | "Placement Impression Type Handling is invalid" |

  @restartWiremockAfter
  Scenario: restart wiremock server
    Then nothing else to be done
