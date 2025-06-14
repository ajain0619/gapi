Feature: get, create, update, delete ad nets as Nexage Admin

  Background: login as Nexage admin and select buyer
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "pr_buyer_test"

  Scenario: get ad nets for buyer
    When the user gets ad nets for selected buyer
    Then returned "ad nets" data matches the following json file "jsons/genevacrud/adnet/expected_results/AdNetBuyerSummaries_ER.json"

  Scenario: get ad net by name
    When the user gets ad nets for selected buyer
    And the user selects ad net "adnet3"
    Then returned "ad nets" data matches the following json file "jsons/genevacrud/adnet/expected_results/AdNetSummary_ER.json"

  Scenario Outline: create ad net
    When the user creates ad net from the json file "jsons/genevacrud/adnet/payload/<file_payload>.json"
    Then request passed successfully
    And returned adnet data matches the following json file "jsons/genevacrud/adnet/expected_results/<file_ER>.json"
    And the value of "cridHeaderField" in the database matches with the server response

    Examples:
      | file_payload                                         | file_ER                                         |
      | CreateAdsourceAllFields_Payload                      | CreateAdsourceAllFields_ER                      |
      | CreateAdsourceRequiredFields_Payload                 | CreateAdsourceRequiredFields_ER                 |
      | CreateAdsourceCridHeaderValid_Payload                | CreateAdsourceCridHeaderValid_ER                |
      | CreateAdsourceCridHeaderValidSpecialChars_Payload    | CreateAdsourceCridHeaderValidSpecialChars_ER    |
      | CreateAdsourceCridHeaderValidNull_Payload            | CreateAdsourceCridHeaderValidNull_ER            |
      | CreateAdsourceCridHeaderValidEmpty_Payload           | CreateAdsourceCridHeaderValidEmpty_ER           |
      | CreateAdsourceBidEnabledDecisionMakerEnabled_Payload | CreateAdsourceBidEnabledDecisionMakerEnabled_ER |

  Scenario Outline: create ad net with invalid cridHeaderField
    When the user creates ad net from the json file "jsons/genevacrud/adnet/payload/<file_payload>.json"
    Then "Create bidder" failed with "<responseCode>" response code and error message "<errorMssage>"

    Examples:
      | file_payload                                        | responseCode | errorMssage                                                                              |
      | CreateAdsourceCridHeaderInvalidSpace_Payload        | 400          | Creative ID header name should not have control characters, spaces, and separators in it |
      | CreateAdsourceCridHeaderInvalidMoreThan255_Payload  | 400          | Creative ID header name should have no more than 255 characters                          |
      | CreateAdsourceCridHeaderInvalidSpecialChars_Payload | 400          | Creative ID header name should not have control characters, spaces, and separators in it |

  Scenario Outline: update ad net with different set of fields
    When the user gets ad nets for selected buyer
    And the user selects ad net "adnet3"
    And the user updates selected ad net from the json file "jsons/genevacrud/adnet/payload/<file_payload>.json"
    Then request passed successfully
    And returned "ad net" data matches the following json file "jsons/genevacrud/adnet/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                 | file_ER                                 |
      | UpdateRequiredFields_Payload                 | UpdateRequiredFields_ER                 |
      | UpdateAllFields_Payload                      | UpdateAllFields_ER                      |
      | UpdateBidEnabledDecisionMakerEnabled_Payload | UpdateBidEnabledDecisionMakerEnabled_ER |

  Scenario: delete ad net
    When the user gets ad nets for selected buyer
    And the user deletes ad net "adnet2"
    Then request passed successfully
    And ad net status changed to deleted

  Scenario: unable to create ad net with invalid data
    When the user gets ad nets for selected buyer
    And the user creates ad net from the json file "jsons/genevacrud/adnet/payload/AdSourceIncorrectJson_Payload.json"
    Then "ad net creation" failed with "400" response code

  Scenario: unable to update ad net with invalid data in JSON
    When the user gets ad nets for selected buyer
    And the user selects ad net "adnet3"
    And the user updates selected ad net from the json file "jsons/genevacrud/adnet/payload/AdSourceIncorrectJson_Payload.json"
    Then "ad net update" failed with "400" response code

  Scenario: unable to update not existing ad net
    When the user updates not existing ad net
    Then "ad net update" failed with "400" response code

  Scenario: unable to delete not existing ad net
    When the user deletes not existing ad net
    Then "ad net update" failed with "400" response code
