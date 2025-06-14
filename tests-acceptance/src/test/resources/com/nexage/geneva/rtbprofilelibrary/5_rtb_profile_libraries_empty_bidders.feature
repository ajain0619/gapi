Feature: PSS RTB Profile Libraries with eligible bidders traffic disabled

  Background: log in as seller admin
    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"

  @restoreCrudCoreDatabaseBefore

  Scenario: create RTB Profile Library with empty bidder group
    When the PSS user creates RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/create/Create_empty_bidders_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/create/Create_empty_bidders_ER.json"

  Scenario: Publisher retrieves RTB Profile Libraries
    When the PSS user searches all RTB Profile Libraries
    Then request passed successfully
    And returned "RTB Profile Libraries" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/GetAll_nonempty_bidders_ER.json"
