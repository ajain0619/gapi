Feature: add, modify, get eligible bidders list as nexage and seller - admin, manager and user

  Scenario Outline: Add/Modify eligible bidders list from publisher as a Nexage Admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/nexage/<file_payload>.json"
    Then request passed successfully
    And returned "Publisher" data matches the following json file "jsons/genevacrud/eligiblebidders/expected_results/nexage/<file_ER>.json"

    Examples:
      | file_payload                  | file_ER                  |
      | AddEligibleBidders_payload    | AddEligibleBidders_ER    |
      | ModifyEligibleBidders_payload | ModifyEligibleBidders_ER |

  Scenario: Get eligible bidders list from publisher as a Seller Admin
    Given the user "debselleradmin" has logged in with role "AdminSeller"
    When the user selects the "Seller" company "debpublisher"
    And publisher data is retrieved
    Then request passed successfully
    And returned "publisher with eligible bidders" data matches the following json file "jsons/genevacrud/eligiblebidders/expected_results/selleradmin/GetEligibleBidders_ER.json"

  Scenario: Remove eligible bidders list from publisher as a Nexage Admin
    Given the user "dkadoura" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/nexage/RemoveEligibleBidders_payload.json"
    Then request passed successfully
    And returned "Publisher" data matches the following json file "jsons/genevacrud/eligiblebidders/expected_results/nexage/RemoveEligibleBidders_ER.json"

  Scenario Outline: Add/Modify/Remove eligible bidders list from publisher as a Nexage Manager
    Given the user "debnexmgr" has logged in with role "ManagerNexage"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/nexage/<file_payload>.json"
    Then request passed successfully
    And returned "Publisher" data matches the following json file "jsons/genevacrud/eligiblebidders/expected_results/nexage/<file_ER>.json"

    Examples:
      | file_payload                  | file_ER                  |
      | AddEligibleBidders_payload   | AddEligibleBidders_ER    |
      | ModifyEligibleBidders_payload | ModifyEligibleBidders2_ER |
      | RemoveEligibleBidders_payload | RemoveEligibleBidders_ER |

  Scenario: Add eligible bidders list from publisher as a Seller Admin - eligible bidder list should remain unchanged
    Given the user "debselleradmin" has logged in with role "AdminSeller"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/selleradmin/AddEligibleBidders_payload.json"
    Then request passed successfully
    And returned "Publisher" data matches the following json file "jsons/genevacrud/eligiblebidders/expected_results/selleradmin/AddEligibleBidders_ER.json"

  Scenario: Add an eligible bidder list as a nexage admin in order to execute the test for modify/remove as a seller admin
    Given the user "dkadoura" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/nexage/AddEligibleBidders_payload.json"
    Then request passed successfully

  Scenario Outline: Modify/Remove eligible bidders list from publisher as a Seller Admin - eligible bidder list should remain unchanged
    Given the user "debselleradmin" has logged in with role "AdminSeller"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/selleradmin/<file_payload>.json"
    Then request passed successfully
    And returned "Publisher" data matches the following json file "jsons/genevacrud/eligiblebidders/expected_results/selleradmin/<file_ER>.json"

    Examples:
      | file_payload                  | file_ER                  |
      | ModifyEligibleBidders_payload | ModifyEligibleBidders_ER |
      | RemoveEligibleBidders_payload | RemoveEligibleBidders_ER |

  @unstable
  Scenario: Negative Test - Add bidder to eligible bidders list with unknown pid
    Given the user "dkadoura" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/negative/AddEligibleBidderWithUnknownPid_payload.json"
    Then "Add Eligible Bidder" failed with "404" response code

  Scenario: Negative Test - Add bidder to eligible bidders list with unknown site type
    Given the user "dkadoura" has logged in with role "AdminNexage"
    When the user selects the "Seller" company "debpublisher"
    And the user updates a publisher from the json file "jsons/genevacrud/eligiblebidders/payload/negative/AddEligibleBidderWithUnknownSiteType_payload.json"
    When "Add Eligible Bidder" failed with "400" response code
    Then "Add Eligible Bidder" failed with "Bad Request" response message
