Feature: create, update, delete, search RTB Profile Libraries as Nexage Admin user

  Background: user logs in
    Given the user "admin1c" has logged in with role "AdminNexage"

  @restoreCrudCoreDatabaseBefore

  Scenario: search all RTB Profile Libraries
    When the user searches all RTB Profile Libraries
    Then request passed successfully
    And returned "RTB Profile Libraries" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/GetAll_ER.json"

  Scenario: search RTB Profile Library
    When the user searches RTB Profile Library "BGR1"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/Get_ER.json"

  Scenario Outline: update RTB Profile Library with different parameters
    When the user searches RTB Profile Library "BGR1"
    And the user updates the RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload          | file_ER          |
      | UpdateLibrary_payload | UpdateLibrary_ER |
      | AddGroup_payload      | AddGroup_ER      |
      | RemoveGroup_payload   | RemoveGroup_ER   |

  Scenario: create RTB Profile Library
    When the user creates RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Create_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/create/Create_ER.json"

  Scenario: create RTB Profile Library from other Libraries
    When the user clones RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Clone_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/create/CloneResult_ER.json"

  Scenario: delete RTB Profile Library
    When the user searches RTB Profile Library "TEST"
    And the user deletes the RTB Profile Library with follow redirect is "true"
    Then request passed without errors
    And the user cannot search deleted library

  Scenario: create invalid RTB Profile Library will fail
    When the user tries to create RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Invalid_payload.json" with follow redirect is "true"
    Then "RTB Profile Library creation" failed with "400" response code

  Scenario: update a RTB Profile Library using invalid data will fail
    When the user searches RTB Profile Library "BGR1"
    And the user tries to update the RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/Invalid_payload.json" with follow redirect is "true"
    Then "RTB Profile Library update" failed with "400" response code

  Scenario: search a RTB Profile Library that doesn`t exist will fail
    When the user tries to update any RTB Profile Library with follow redirect is "true"
    Then "RTB Profile Library search" failed with "404" response code

  Scenario: delete a RTB Profile Library that doesn`t exist will fail
    When the user tries to update any RTB Profile Library with follow redirect is "true"
    Then "RTB Profile Library deletion" failed with "404" response code

  Scenario: update a RTB Profile Library that doesn`t exist will fail
    When the user tries to update any RTB Profile Library with follow redirect is "true"
    Then "RTB Profile Library update" failed with "404" response code
