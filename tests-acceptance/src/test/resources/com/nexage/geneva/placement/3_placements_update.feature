Feature: Update placement endpoint tests

  @restoreCrudCoreDatabaseBefore

  Scenario: Create placement to update
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the user creates placement from the json file "jsons/genevacrud/placements/payload/CreatePlacementBanner_payload.json"
    Then request passed successfully with code "201"

  Scenario: Nexage User can update placement by changing screenLocation
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the user updates placement from the json file "jsons/genevacrud/placements/payload/UpdatePlacementBanner_payload.json"
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/UpdatePlacementBanner_ER.json"

  Scenario: External API user logged in via B2B can update placement by changing placementCategory to MediumRectangle
    Given the user "role-api-user-1c" logs in via B2B with role "ApiSeller"
    And the user selects site pid "10000199" for the company with pid "807"
    And the user creates a placement banner with name "role_api_placement_update" for site pid "10000199"
    Then request passed successfully with code "201"
    And the user updates placement from the json file "jsons/genevacrud/placements/payload/UpdatePlacementMediumRectangle_payload.json"
    Then request passed successfully with code "200"

  Scenario: the user is not authorized to update placement
    Given the user "role-api-user-1c" logs in via B2B with role "ApiSeller"
    When unauthorized user tries to update placement from the json file "jsons/genevacrud/placements/payload/UpdatePlacementBanner_payload.json"
    Then "placement update" failed with "401" response code

  Scenario Outline: Update placement with video
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "<company_name>"
    And the user selects the site "<site_name>"
    And the user updates placement from the json file "jsons/genevacrud/placements/payload/update/<update_payload>.json"
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/update/<update_ER>.json"

    Examples:
      | update_payload                                                  | update_ER                                                   | site_name                      | company_name            |
      | UpdateBannerPlacementToVideo_payload                            | UpdateBannerPlacementToVideo_ER                             | CRUDPosition_Site1             | CRUDPositionTest        |
      | UpdatePlacementWithPlacementVideoNoCompanion_payload            | UpdatePlacementWithPlacementVideoNoCompanion_ER             | CRUDPosition_Site1             | CRUDPositionTest        |
      | UpdatePlacementWithPlacementVideoOneCompanion_payload           | UpdatePlacementWithPlacementVideoOneCompanion_ER            | TagArchive_TestSite1           | TagArchive_TestCompany  |
      | UpdatePlacementWithPlacementVideoMultipleCompanion_payload      | UpdatePlacementWithPlacementVideoMultipleCompanion_ER       | mobilepro                      | Provision               |
      | UpdatePlacementWithPlacementVideoAddCompanion_payload           | UpdatePlacementWithPlacementVideoAddCompanion_ER            | CRUDPosition_Site1             | CRUDPositionTest        |
      | UpdatePlacementWithPlacementVideoDeleteCompanion_payload        | UpdatePlacementWithPlacementVideoDeleteCompanion_ER         | mobilepro                      | Provision               |
      | UpdatePlacementToInArticle_payload                              | UpdatePlacementToInArticle_ER                               | mobilepro                      | Provision               |
      | UpdatePlacementToInFeed_payload                                 | UpdatePlacementToInFeed_ER                                  | mobilepro                      | Provision               |
      | UpdatePlacementWithLongformPlacementVideo_payload               | UpdatePlacementWithLongformPlacementVideo_ER                | CRUDPosition_Site1             | CRUDPositionTest        |

  Scenario Outline: Update placement without video and removes video
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "<company_name>"
    And the user selects the site "<site_name>"
    And the user updates placement from the json file "jsons/genevacrud/placements/payload/update/<update_payload>.json"
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/update/<update_ER>.json"
    And the returned data doesn't contain the following fields "placementVideo"

    Examples:
      | update_payload                                             | update_ER                                              | site_name                      | company_name            |
      | UpdatePlacementInstreamWithoutPlacementVideo_payload       | UpdatePlacementInstreamWithoutPlacementVideo_ER        | mobilepro                      | Provision               |
      | UpdatePlacementVideoToBanner_payload                       | UpdatePlacementVideoToBanner_ER                        | mobilepro                      | Provision               |

  Scenario Outline: update placement fails with correct error message
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the user updates placement from the json file "jsons/genevacrud/placements/payload/update/<file_payload>.json"
    Then "placement update" failed with "400" response code and error message "<fail_msg>"
    Examples:
      | file_payload                                                | fail_msg                                        |
      | UpdatePlacementInArticleScreenLocationInvalid_payload       | "Placement Screen Location is invalid"          |
      | UpdatePlacementInFeedScreenLocationInvalid_payload          | "Placement Screen Location is invalid"          |
      | UpdatePlacementInArticleMraidSupportInvalid_payload         | "Placement MRAID Support is invalid"            |
      | UpdatePlacementInFeedMraidSupportInvalid_payload            | "Placement MRAID Support is invalid"            |
      | UpdatePlacementImpressionTypeHandlingInvalid_payload        | "Placement Impression Type Handling is invalid" |
