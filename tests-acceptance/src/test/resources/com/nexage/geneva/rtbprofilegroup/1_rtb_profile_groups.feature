Feature: create, get, update and delete RTB Profile Groups

  Background: log in as Nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: Create an RTB Profile Group
    When the user creates an rtb profile group from the json file "jsons/genevacrud/rtbprofilegroup/payload/Create_rtb_profile_group_payload.json"
    Then request passed successfully with code "200"
    And returned "rtb profile group" data matches the following json file "jsons/genevacrud/rtbprofilegroup/expected_results/Create_rtb_profile_group_ER.json"

  Scenario: Get an RTB Profile Group
    When the user gets the rtb profile group with pid "1"
    Then request passed successfully with code "200"
    And returned "rtb profile group" data matches the following json file "jsons/genevacrud/rtbprofilegroup/expected_results/Get_rtb_profile_group_ER.json"

  Scenario: Update an RTB Profile Group
    When the user updates the rtb profile group with pid "19" from the json file "jsons/genevacrud/rtbprofilegroup/payload/Update_rtb_profile_group_payload.json"
    Then request passed successfully with code "200"
    And returned "rtb profile group" data matches the following json file "jsons/genevacrud/rtbprofilegroup/expected_results/Update_rtb_profile_group_ER.json"

  Scenario: Delete an RTB Profile Group
    When the user deletes the rtb profile group with pid "19"
    Then request passed successfully with code "200"

