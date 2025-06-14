Feature: buyer seat

  Background: log in as nexage admin and select the buyer company
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "MedFTBuyer"

  Scenario: insert test data
    Given these buyer groups that belong to the selected company
      | name        | sfdcLineId     | sfdcIoId     | currency | billingCountry | billable | version |
      | japanGroup  | testsfdcLineId | testsfdcIoId | YEN      | JPN            | true     | 0       |
      | germanGroup | testLineId     | testIoId     | EUR      | DEU            | true     | 0       |
    And these buyer seats that belong to the selected company and buyer group "japanGroup"
      | name          | seat        | enabled | version |
      | SeatOneName   | SeatOneId   | false   | 0       |
      | SeatTwoName   | SeatTwoId   | false   | 0       |
      | SeatThreeName | SeatThreeId | false   | 0       |

  Scenario Outline: create buyer seat
    When the user creates buyer seat from the json file "jsons/genevacrud/buyerseat/payload/<file_payload>.json"
    Then request passed successfully
    And returned "create buyer seat" data matches the following json file "jsons/genevacrud/buyerseat/expected_results/<file_ER>.json"
    And date fields are not empty for "jsons/genevacrud/buyerseat/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                 | file_ER                                  |
      | 1_CreateBuyerSeat_payload                    | 1_CreateBuyerSeat_ER                     |
      | 1_CreateBuyerSeat_PidVersionExcluded_payload | 1_CreateBuyerSeat_PidVersionGenerated_ER |

  Scenario: get all buyer seats
    When the user gets all buyer seats
    Then request passed successfully
    And returned "buyer seat" data matches the following json file "jsons/genevacrud/buyerseat/expected_results/1_GetAllBuyerSeats_ER.json"

  Scenario: get all buyer seats by name
    When the user gets all buyer seats matching qf "name" with qt "sEatOnE"
    Then request passed successfully
    And returned "buyer seat" data matches the following json file "jsons/genevacrud/buyerseat/expected_results/5_GetAllBuyerSeatsByName_ER.json"

  Scenario: update buyer seat, changing enabled from false to true which makes it the only enabled seat in the group
    When the user updates buyer seat with ID "SeatOneId" using the json file "jsons/genevacrud/buyerseat/payload/2_UpdateSeatOne_payload.json"
    Then request passed successfully
    And returned buyer seat update data matches the following json file "jsons/genevacrud/buyerseat/expected_results/2_UpdateSeatOne_ER.json"
    And update date is recorded for "jsons/genevacrud/buyerseat/expected_results/2_UpdateSeatOne_ER.json"


  Scenario: move a non-enabled seat to another group
    When the user moves buyer seat with ID "SeatThreeId" to group "germanGroup" using the json file "jsons/genevacrud/buyerseat/payload/3_MoveGroupSeatThree_payload.json"
    Then request passed successfully
    And returned buyer seat update data matches the following json file "jsons/genevacrud/buyerseat/expected_results/3_MoveGroupSeatThree_ER.json"
    And update date is recorded for "jsons/genevacrud/buyerseat/expected_results/3_MoveGroupSeatThree_ER.json"


  Scenario: get all buyer seats after the updates
    When the user gets all buyer seats
    Then request passed successfully
    And returned "buyer seat" data matches the following json file "jsons/genevacrud/buyerseat/expected_results/4_GetAllBuyerSeatsUpdated_ER.json"


  Scenario: move the only enabled seat of a group to another group
    When the user moves buyer seat with ID "SeatOneId" to group "germanGroup" using the json file "jsons/genevacrud/buyerseat/payload/MoveGroupSeatOne_payload.json"
    Then request passed successfully
    And returned buyer seat update data matches the following json file "jsons/genevacrud/buyerseat/expected_results/MoveGroupSeatOne_ER.json"


  Scenario: disable the only enabled seat in the group
    When the user updates buyer seat with ID "SeatOneId" using the json file "jsons/genevacrud/buyerseat/payload/DisableSeatOne_payload.json"
    Then request passed successfully
    And returned buyer seat update data matches the following json file "jsons/genevacrud/buyerseat/expected_results/DisableSeatOne_ER.json"


  Scenario: change seat ID
    When the user updates buyer seat with ID "SeatOneId" using the json file "jsons/genevacrud/buyerseat/payload/ChangeSeatIdSeatOne_payload.json"
    Then request passed successfully
    And returned buyer seat update data matches the following json file "jsons/genevacrud/buyerseat/expected_results/ChangeSeatIdSeatOne_ER.json"
    And update date is recorded for "jsons/genevacrud/buyerseat/expected_results/ChangeSeatIdSeatOne_ER.json"


  Scenario: send stale data for update (version is still 0 while current version on server is 1 after the update above)
    When the user updates buyer seat with ID "SeatThreeId" using the json file "jsons/genevacrud/buyerseat/payload/StaleVersionUpdateSeat_payload.json"
    Then response failed with "500" response code, error message "Data record has been updated since last loaded. Please reload it and try again" and without field errors.
