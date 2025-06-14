Feature: get placement endpoint tests

  @restoreCrudCoreDatabaseBefore
  Scenario: get placements with video attributes
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site1"
    And the user updates placement from the json file "jsons/genevacrud/placements/payload/update/UpdatePlacementWithLongformVideoForReading_payload.json"
    And the user reads the placements for the site with the site pid "10000174" for the company with the company pid "10201"
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacements.json"
