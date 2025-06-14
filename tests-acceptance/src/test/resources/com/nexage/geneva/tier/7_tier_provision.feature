@unstable @provision
Feature: create, read, update, delete tiers for provision api

  Background: log in as seller admin and select the site
    Given setup wiremock user for provision API
    Given the user fetches an authentication token
    And set company "Provision"
    And set site "mobilepro"
    And set position pid for name "mediumprovision"

  Scenario Outline: create an AOL tag with all fields
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/<file_payload>.json"
    Then request passed successfully

    Examples:
      | file_payload           |
      | CrTagForTiers1_payload |
      | CrTagForTiers2_payload |
      | CrTagForTiers3_payload |
      | CrTagForTiers4_payload |

  Scenario: create an AOL tag with all fields
    And set position pid for name "instreamprovision"
    When the user creates provision tag from the json file "jsons/genevacrud/tag/provision_api/payload/CrTagForTiers5_payload.json"
    Then request passed successfully

  #Positive scenarios for CRUD operations for tiers (resort as well)
  Scenario Outline: create tier with correct data, empty tier level
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then request passed successfully
    And tier count for position "mediumprovision" equals to "<count>"
    And tier level for tier pid "<tier_pid>" equals to "<tier_level>"

    Examples:
      | file_payload                     | count | tier_pid | tier_level |
      | CrTierCorrectData_payload        | 1     | 10356    | 0          |
      | CrTierEmptyLevelTierType_payload | 2     | 10357    | 1          |

  Scenario: update tier with correct data
    Given set specific tier pid as "10356"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/UpTierCorrectData_payload.json"
    Then request passed successfully
    When the user gets all tiers using provision api
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/UpTierCorrectData_ER.json"
    And tag pid count for the "10356" equals to "2"

  Scenario: resort tiers
    And the user sort tiers using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/SortTierCorrect_payload.json"
    Then request passed successfully
    When the user gets all tiers using provision api
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/SortTierCorrect_ER.json"

  Scenario: get tier tag
    And set specific tier pid as "10356"
    And the user gets the tier using provision api
    Then request passed successfully
    And returned "get tier" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/GetTierWithTag_ER.json"

  Scenario: get tier without tags
    And set specific tier pid as "10357"
    And set tag pid for name "VA012"
    And the user unassign tag from the tier using provision api
    Then request passed successfully
    And tier pid "10357" cannot be searched in the database

  Scenario: delete tier with assigned tags
    When set specific tier pid as "10356"
    And the user deletes the tier using provision api
    Then request passed successfully
    And tier pid "10356" cannot be searched in the database
    When the user gets all tiers using provision api
    Then request passed successfully
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/GetAllTiersAfterDelete_ER.json"
    And tag pid for the "10356" tier pid cannot be searched in the database

  #Negative scenarios for CRUD operations for tiers
  Scenario Outline: create tier with correct data, empty tier level
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then request passed successfully

    Examples:
      | file_payload                     |
      | CrTierCorrectData_payload        |
      | CrTierEmptyLevelTierType_payload |

  Scenario: create tier with already assigned tag
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/CrTierAssignedTag_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""tags":"Element '5566600' already assigned""
    And tier count for position "mediumprovision" equals to "2"

  Scenario: create tier with empty data
    And set position pid for name "instreamprovision"
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/CrTierEmptyData_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""orderStrategy":"Value should not be empty""
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""tags":"Value should not be empty""
    And tier count for position "instreamprovision" equals to "0"

  Scenario Outline: create tier with decision maker tierType, empty tags or empty order strategy
    And set position pid for name "instreamprovision"
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"
    And tier count for position "instreamprovision" equals to "0"

    Examples:
      | file_payload               | field_errors                                                  |
      | CrTierDecMaker_payload     | "tierType":"The value 'SY_DECISION_MAKER' does not supported" |
      | CrTierWithoutTag_payload   | "tags":"Value should not be empty"                            |
      | CrTierWithoutOrder_payload | "orderStrategy":"Value should not be empty                    |

  Scenario Outline: create tier with incorrect tag pid or with SUPER_AUCTION tierType
    And set position pid for name "instreamprovision"
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "<create_message>" failed with "400" response code and error message "<error_message>"
    And tier count for position "instreamprovision" equals to "0"

    Examples:
      | file_payload               | error_message                         | create_message                          |
      | CrTierIncorrectTag_payload | Invalid Tag specified                 | create tier with incorrect tag          |
      | CrTierSA_payload           | Tier type not valid for this position | create tier with super auction tierType |

  Scenario Outline: create tier with invalid tierType, level, tag or order
    And set position pid for name "instreamprovision"
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "<create_message>" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tier count for position "instreamprovision" equals to "0"

    Examples:
      | file_payload                  | create_message                    |
      | CrTierInvalidTierType_payload | create tier with invalid tierType |
      | CrTierInvalidLevel_payload    | create tier with invalid level    |
      | CrTierInvalidTag_payload      | create tier with invalid tag      |
      | CrTierInvalidOrder_payload    | create tier with invalid order    |

  Scenario: create tier with the same level
    When the user creates tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/CrTierSameLevel_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""level":"The value is duplicated""
    And tier count for position "mediumprovision" equals to "2"

  Scenario: update tier with already assigned tag
    And set specific tier pid as "10359"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/UpTierAssignedTag_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors ""tags":"Element '5566599' already assigned""
    And tag pid count for the "10358" equals to "1"
    And tag pid count for the "10359" equals to "1"

  Scenario Outline: update tier with empty data, decision maker tierType, the same level or empty order strategy
    And set specific tier pid as "10359"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"

    Examples:
      | file_payload               | field_errors                                                  |
      | UpTierDecMaker_payload     | "tierType":"The value 'SY_DECISION_MAKER' does not supported" |
      | UpTierSameLevel_payload    | "level":"The value is duplicated"                             |
      | UpTierWithoutOrder_payload | "orderStrategy":"Value should not be empty"                   |
      | UpTierWithoutTag_payload   | "tags":"Value should not be empty"                            |

  Scenario Outline: update tier with incorrect tag pid, without tierType, SUPER_AUCTION tierType, without id or any data, incorrect id
    And set specific tier pid as "10358"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "<update_message>" failed with "400" response code and error message "<error_message>"

    Examples:
      | file_payload                  | error_message                                     | update_message                          |
      | UpTierWithoutTierType_payload | Tier type can not be updated                      | update tier without tier type           |
      | UpTierSA_payload              | Tier type can not be updated                      | update tier with super auction tierType |
      | UpTierIncorrectTag_payload    | Invalid Tag specified                             | update tier with incorrect tag          |
      | UpTierWithoutId_payload       | Invalid Input.Please check your input parameters. | update tier without id                  |
      | UpTierEmptyData_payload       | Invalid Input.Please check your input parameters. | update tier without any data            |
      | UpTierIncorrectId_payload     | Invalid Input.Please check your input parameters. | update tier incorrect id                |

  Scenario Outline: update tier with incorrect version or without version
    And set specific tier pid as "10358"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "<update_message>" failed with "500" response code

    Examples:
      | file_payload                   | update_message                     |
      | UpTierIncorrectVersion_payload | update tier with incorrect version |
      | UpTierWithoutVersion_payload   | update tier without version        |

  Scenario Outline: update tier with invalid tierType, level, tag, order, version or id
    And set specific tier pid as "10358"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "<update_message>" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    And tier count for position "instreamprovision" equals to "0"

    Examples:
      | file_payload                  | update_message                    |
      | UpTierInvalidTierType_payload | update tier with invalid tierType |
      | UpTierInvalidTag_payload      | update tier with invalid tag      |
      | UpTierInvalidVersion_payload  | update tier with invalid version  |
      | UpTierInvalidLevel_payload    | update tier with invalid level    |
      | UpTierInvalidOrder_payload    | update tier with invalid order    |
      | UpTierInvalidId_payload       | update tier invalid id            |

  Scenario Outline: update tier with non-existing tier id in the url or belongs to other placement.
    And set specific tier pid as "<pid>"
    When the user updates the tier using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "<update_message>" failed with "400" response code and error message "Invalid Input.Please check your input parameters."
    And tier count for position "instreamprovision" equals to "0"

    Examples:
      | file_payload                        | update_message                         | pid  |
      | UpTierNonExisting_payload           | update non-existing tier               | 5180 |
      | UpTierBelongsOtherPlacement_payload | update tier belongs to other placement | 5175 |

  Scenario Outline: get non-existing tier or which belongs to other placement
    When set specific tier pid as "<tier_pid>"
    And the user gets the tier using provision api
    Then "get tier" failed with "400" response code and error message "Invalid Input.Please check your input parameters."

    Examples:
      | tier_pid |
      | 5180     |
      | 5175     |

  Scenario Outline: delete non-existing tier or belongs to other placement
    When set specific tier pid as "<tier_pid>"
    And the user deletes the tier using provision api
    Then "delete tier" failed with "400" response code and error message "Invalid Input.Please check your input parameters."

    Examples:
      | tier_pid |
      | 5180     |
      | 5175     |

  #Negative cases for assign/unassign/resort tags
  Scenario: assign tag to non-existing tier
    When set specific tier pid as "5180"
    And set tag pid for name "VA013"
    And the user assign tag to the tier using provision api
    Then "assign tag to the tier" failed with "404" response code and error message "Tier not found in position"

  Scenario: assign non-existing tag to the tier
    When set specific tier pid as "10358"
    And set non-existing tag pid "90"
    And the user assign tag to the tier using provision api
    Then "assign non-existing tag to the tier" failed with "404" response code and error message "Tag not found in position"

  Scenario: assign tag to the tier, belongs to other placement
    When set specific tier pid as "5201"
    And set tag pid for name "VA013"
    And the user assign tag to the tier using provision api
    Then "assign tag to the tier belongs to other placement" failed with "404" response code and error message "Tier not found in position"
    And tag pid count for the "5201" equals to "1"

  Scenario: assign tag to the same tier or tier, belongs to other placement
    When set specific tier pid as "10358"
    And set tag pid for name "VA011"
    And the user assign tag to the tier using provision api
    Then "assign tag" failed with "400" response code and error message "Tag is already assigned a position"
    And tag pid count for the "10358" equals to "1"

  Scenario Outline: assign tag belongs to the tier in other placement or deleted tag
    When set specific tier pid as "<tier_pid>"
    And set tag pid for name "<tag_name>"
    And the user assign tag to the tier using provision api
    Then "assign tag" failed with "404" response code and error message "Tag not found in position"
    And tag pid count for the "<tier_pid>" equals to "<tag_pid_count>"

    Examples:
      | tier_pid | tag_name                        | tag_pid_count |
      | 10358    | DealTagTest-PSS-Banner-Archived | 1             |
      | 10358    | DIRECT-defaultplacement         | 1             |

  Scenario: unassign unassigned tag
    When set specific tier pid as "10358"
    And set tag pid for name "VA013"
    And the user unassign tag from the tier using provision api
    Then "unassign unassigned tag" failed with "404" response code and error message "Invalid Tag specified"

  Scenario: unassign tag from non-existing tier
    When set specific tier pid as "5180"
    And set tag pid for name "VA012"
    And the user unassign tag from the tier using provision api
    Then "unassign tag from non-existing tier" failed with "404" response code and error message "Tier not found in position"

  Scenario: unassign non-existing tag from the tier
    When set specific tier pid as "10358"
    And set non-existing tag pid "90"
    And the user unassign tag from the tier using provision api
    Then "unassign non-existing tag" failed with "404" response code and error message "Invalid Tag specified"

  Scenario: unassign tag from tier belongs to other placement
    When set specific tier pid as "5201"
    And set tag pid for name "DoubleClick Dart for Mobile - doubleclick"
    And the user assign tag to the tier using provision api
    Then "unassign tag from the tier belongs to other placement" failed with "404" response code and error message "Tag not found in position"

  Scenario: resort tier belongs to other placement
    And the user sort tiers using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/SortTierIncorrect_payload.json"
    Then "sort tiers" failed with "404" response code and error message "Tier not found in position"

  Scenario Outline: resort tier with duplicate id or incorrect tier id count
    And the user sort tiers using provision api from the json file "jsons/genevacrud/tier/provision_api/payload/<file_payload>.json"
    Then "sort tiers" failed with "400" response code and error message "Ids count are wrong"

    Examples:
      | file_payload               |
      | SortTierDuplicate_payload  |
      | SortTierSmallCount_payload |

  #Positive cases for assign/unassign/resorting tags
  Scenario Outline: assign several tags to the tier from the unassigned section or between tiers
    When set specific tier pid as "<tier_pid>"
    And set tag pid for name "<tag_name>"
    And the user assign tag to the tier using provision api
    Then request passed successfully
    And tag pid count for the "10358" equals to "<tag_pid_count>"
    When the user gets all tiers using provision api
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/<file_ER>.json"

    Examples:
      | tier_pid | tag_name | tag_pid_count | file_ER                    |
      | 10358    | VA013    | 2             | GetAllTiersFirstAssign_ER  |
      | 10358    | VA014    | 3             | GetAllTiersSecondAssign_ER |

  Scenario Outline: assign several tags to the tier from the other tiers
    When set specific tier pid as "10359"
    And set tag pid for name "<tag_name>"
    And the user assign tag to the tier using provision api
    Then request passed successfully
    And tag pid count for the "10358" equals to "<tag_pid_count_from>"
    And tag pid count for the "10359" equals to "<tag_pid_count_to>"
    When the user gets all tiers using provision api
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/<file_ER>.json"

    Examples:
      | tag_name | tag_pid_count_from | tag_pid_count_to | file_ER                    |
      | VA011    | 2                  | 2                | GetAllTiersThirdAssign_ER  |
      | VA013    | 1                  | 3                | GetAllTiersFourthAssign_ER |

  Scenario: unassign one tag from the tier
    When set specific tier pid as "10359"
    And set tag pid for name "VA013"
    And the user unassign tag from the tier using provision api
    Then request passed successfully
    And tag pid count for the "10359" equals to "2"
    When the user gets all tiers using provision api
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/GetAllTiersFirstUnassign_ER.json"

  Scenario: unassign all tags from the tier
    When set specific tier pid as "10358"
    And set tag pid for name "VA014"
    And the user unassign tag from the tier using provision api
    Then request passed successfully
    And tier pid "10358" cannot be searched in the database
    When the user gets all tiers using provision api
    And returned "get tiers" data matches the following json file "jsons/genevacrud/tier/provision_api/expected_results/GetAllTiersSecondUnassign_ER.json"
