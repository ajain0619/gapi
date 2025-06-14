Feature: PSS RTB Profile Libraries as Seller Admin

  Background: log in as seller admin and get his company
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"

  @restoreCrudCoreDatabaseBefore

  Scenario: search all RTB Profile Libraries
    When the PSS user searches all RTB Profile Libraries
    Then request passed successfully
    And returned "RTB Profile Libraries" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/pss/expected_results/GetAll_ER.json"

  Scenario: search RTB Profile Library
    When the PSS user searches RTB Profile Library "BGN1"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/pss/expected_results/Get_ER.json"

  Scenario: create RTB Profile Library
    When the PSS user creates RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/pss/payload/create/Create_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/pss/expected_results/create/Create_ER.json"

  Scenario: clone RTB Profile Library
    When the PSS user clones RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/pss/payload/create/Clone_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/pss/expected_results/create/CloneResult.json"

  Scenario Outline: update RTB Profile Library with different parameters
    When the PSS user searches RTB Profile Library "BGR1"
    And the PSS user updates the RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload          | file_ER          |
      | UpdateLibrary_payload | UpdateLibrary_ER |
      | AddGroup_payload      | AddGroup_ER      |
      | RemoveGroup_payload   | RemoveGroup_ER   |

  Scenario: delete RTB Profile Library
    When the PSS user searches RTB Profile Library "BGN1"
    And the PSS user deletes the RTB Profile Library
    Then request passed without errors
    And the PSS user cannot search deleted library

  Scenario: create invalid RTB Profile Library will fail
    When the PSS user tries to create a RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Invalid_payload.json"
    And "the RTB Profile Library creation" failed with "400" response code

  Scenario: update a RTB Profile Library using invalid data will fail
    When the PSS user searches RTB Profile Library "BGR1"
    And the PSS user tries to update RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/Invalid_payload.json"
    Then "RTB Profile Libraries update" failed with "400" response code

  Scenario: search a RTB Profile Library that doesn`t exist will fail
    When the PSS user tries to search any RTB Profile Library
    Then "RTB Profile Libraries search" failed with "404" response code

  Scenario: update a RTB Profile Library that doesn`t exist will fail
    When the PSS user tries to update any RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/UpdateLibrary_payload.json"
    Then "RTB Profile Libraries update" failed with "404" response code

  Scenario: delete a RTB Profile Library that doesn`t exist will fail
    When the PSS user tries to delete any RTB Profile Library
    Then "RTB Profile Libraries deletion" failed with "404" response code

  Scenario: delete other publisher's RTB Profile Library will fail
    When the PSS user tries to delete RTB Profile Library of any publisher
    Then "RTB Profile Libraries deletion" failed with "401" response code

  Scenario: create RTB Profile Library for other publisher will fail
    When the PSS user tries to create RTB Profile Library of any publisher from the json file "jsons/genevacrud/rtbprofilelibrary/pss/payload/create/Create_payload.json"
    Then "RTB Profile Libraries creation" failed with "401" response code

  Scenario: search other publisher's RTB Profile Library will fail
    When the PSS user tries to search RTB Profile Library of any publisher
    Then "RTB Profile Libraries search" failed with "401" response code

  Scenario: update other publisher's RTB Profile Library will fail
    When the PSS user tries to update RTB Profile Library of any publisher from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/UpdateLibrary_payload.json"
    Then "RTB Profile Libraries update" failed with "401" response code

  @unstable
  Scenario: check format of bidder display names
    When the user gets list of bidder id to name mappings
    Then request passed successfully
    And returned "id to name mapping" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/pss/expected_results/IdNameMappings_ER.json"
