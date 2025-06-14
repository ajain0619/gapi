Feature: Try to call position services without logging in

  Scenario: log out
    Given the user logs out

  Scenario: the user is not authorized to get all positions
    When unauthorized user tries to get all positions
    Then redirect to authenticate page

  Scenario: the user is not authorized to get position
    When unauthorized user tries to get position
    Then redirect to authenticate page

  Scenario: the user is not authorized to create position
    When unauthorized user tries to create position from the json file "jsons/genevacrud/position/pss/payload/create/CreateBannerPlacement_payload.json"
    Then redirect to authenticate page

  Scenario: the user is not authorized to update position
    When unauthorized user tries to update position from the json file "jsons/genevacrud/position/pss/payload/update/UpdateSelectedPlacement_payload.json"
    Then redirect to authenticate page

  Scenario: the user is not authorized to delete position
    When unauthorized user tries to delete position with follow redirect is "false"
    Then redirect to authenticate page

  @restartWiremockAfter
  Scenario:  restart wiremock server
    Then nothing else to be done
