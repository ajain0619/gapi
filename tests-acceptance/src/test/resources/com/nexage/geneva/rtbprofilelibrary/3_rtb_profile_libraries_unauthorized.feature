Feature: Try to call services without logging in

  @restoreCrudCoreDatabaseBefore
  Scenario: log out
    Given the user logs out

  Scenario: the user is not authorized to search all RTB Profile Libraries
    When the user tries to search all RTB Profile Libraries with follow redirect is "false"
    Then redirect to authenticate page

  Scenario: the user is not authorized to search RTB Profile Library
    When the user tries to search any RTB Profile Library with follow redirect is "false"
    Then redirect to authenticate page

  Scenario: the user is not authorized to create RTB Profile Library
    When the user tries to create RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Create_payload.json" with follow redirect is "false"
    Then redirect to authenticate page

  Scenario: the user is not authorized to update RTB Profile Library
    When the user tries to update any RTB Profile Library with follow redirect is "false"
    Then redirect to authenticate page

  Scenario: the user is not authorized to delete RTB Profile Library
    When the user tries to delete any RTB Profile Library with follow redirect is "false"
    Then redirect to authenticate page

  @restartWiremockAfter
  Scenario: restart wiremock server
    Then nothing else to be done
