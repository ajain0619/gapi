Feature: PSS RTB Profile Libraries with eligible bidders traffic disabled

  @restoreCrudCoreDatabaseBefore

  Scenario: update first bidder config with different parameters
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "CreateCompany20150917105726538"
    When the user updates first bidder config from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/UpdateBidderConfig1_payload.json"
    Then request passed successfully
    And the user selects the "Buyer" company "asdf"
    When the user updates first bidder config from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/UpdateBidderConfig2_payload.json"
    Then request passed successfully

  Scenario: Publisher retrieves RTB Profile Libraries
    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"
    When the PSS user searches all RTB Profile Libraries
    Then request passed successfully
    And returned "RTB Profile Libraries" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/GetAll_active_bidders_ER.json"
