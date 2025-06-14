Feature: Try to call bidder config services for the user that has no privileges

  Background: log in as seller admin
    Given the user "crudselleradmin" has logged in

  @restoreCrudCoreDatabaseBefore

  Scenario: the user is not authorized to get all bidder configs
    When the user tries to get all bidder configs
    Then "bidder configs search" failed with "401" response code

  Scenario: the user is not authorized to get bidder configs
    When the user tries to get any bidder configs
    Then "bidder config search" failed with "401" response code

  Scenario: the user is not authorized to create bidder config
    When the user tries to create bidder config from the json file "jsons/genevacrud/bidderconfig/payload/create/CreateBidderConfig_payload.json"
    Then "bidder config creation" failed with "401" response code

  Scenario: the user is not authorized to update bidder config
    When the user tries to update bidder config from the json file "jsons/genevacrud/bidderconfig/payload/update/BidderConfiguration_payload.json"
    Then "bidder config update" failed with "401" response code

  Scenario: the user is not authorized to get list of publishers
    When the user tries to get list of publishers
    Then "list of publishers search" failed with "401" response code

  Scenario: the user is not authorized to get list of publisher sites
    When the user tries to get list of publisher sites
    Then "list of publisher sites search" failed with "401" response code
