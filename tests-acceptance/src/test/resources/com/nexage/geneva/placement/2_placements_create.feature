Feature: Create placement endpoint tests

  Scenario: the user is not authorized to create placement
    When unauthorized user tries to create placement from the json file "jsons/genevacrud/placements/payload/CreatePlacementBanner_payload.json"
    Then "placement creation" failed with "401" response code

  Scenario Outline: create placement with different placement categories with video
    Given the user "<one_central_username>" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "<site_name>"
    And the user creates placement from the json file "jsons/genevacrud/placements/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                      | file_ER                                       | one_central_username | site_name                             |
      | CreatePlacementInStreamVideo_payload              | CreatePlacementInStreamVideo_ER               | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreateVideoPlacementOnlyInPlacementVideo_payload  | CreateVideoPlacementOnlyInPlacementVideo_ER   | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementInterstitial_payload               | CreatePlacementInterstitial_ER                | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementWithVideo_payload                  | CreatePlacementWithVideo_ER                   | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementWithLongformVideo_payload          | CreatePlacementWithLongformVideo_ER           | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementWithInArticle_payload              | CreatePlacementWithInArticle_ER               | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementWithInFeed_payload                 | CreatePlacementWithInFeed_ER                  | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementWithVideoStartDelay_payload        | CreatePlacementWithVideoStartDelay_ER         | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |

  Scenario Outline: create placement with different placement categories without video
    Given the user "<one_central_username>" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "<site_name>"
    And the user creates placement from the json file "jsons/genevacrud/placements/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/<file_ER>.json"
    And the returned data doesn't contain the following fields "placementVideo"

    Examples:
      | file_payload                           | file_ER                           | one_central_username | site_name                             |
      | CreatePlacementBanner_payload          | CreatePlacementBanner_ER          | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementMediumRectangle_payload | CreatePlacementMediumRectangle_ER | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |
      | CreatePlacementWithoutName_payload     | CreatePlacementWithoutName_ER     | NexageAdmin1         | CRUDPosition_Site5_VersionTest        |

  Scenario Outline: create placement fails with correct error message
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the user creates placement from the json file "jsons/genevacrud/placements/payload/<file_payload>.json"
    Then "placement create" failed with "400" response code and error message "<fail_msg>"
    Examples:
      | file_payload                                            | fail_msg                                        |
      | CreatePlacementBanner_payload_with_invalid_name         | "The value does not match the pattern"          |
      | CreatePlacementInArticleScreenLocationInvalid_payload   | "Placement Screen Location is invalid"          |
      | CreatePlacementInFeedScreenLocationInvalid_payload      | "Placement Screen Location is invalid"          |
      | CreatePlacementInArticleMraidSupportInvalid_payload     | "Placement MRAID Support is invalid"            |
      | CreatePlacementInFeedMraidSupportInvalid_payload        | "Placement MRAID Support is invalid"            |
      | CreatePlacementImpressionTypeHandlingInvalid_payload    | "Placement Impression Type Handling is invalid" |

  Scenario: External API user logged in via B2B can create placement
    Given the user "role-api-user-1c" logs in via B2B with role "ApiSeller"
    And the user selects site pid "10000199" for the company with pid "807"
    And the user creates a placement banner with name "role_api_placement_creation" for site pid "10000199"
    Then request passed successfully with code "201"

  Scenario: External MANAGER user logged in via B2B can create placement
    Given the user "nexage1cmgr" logs in via B2B with role "ManagerNexage"
    And the user selects site pid "10000199" for the company with pid "807"
    And the user creates a placement banner with name "role_manager_placement_creation" for site pid "10000199"
    Then request passed successfully with code "201"

  Scenario: create Dap O2 placement with default values
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the user creates placement from the json file "jsons/genevacrud/placements/payload/CreateDapPlacementWithInputDapParams_payload.json"
    Then request passed successfully with code "201"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/CreateDapPlacementWithInputDapParams_ER.json"
    And compare dap o2 default player param values "o2_dap_placement_1"

  @restartWiremockAfter
  Scenario: restart wiremock server
    Then nothing else to be done
