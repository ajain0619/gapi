Feature: PSS RTB Profile Libraries with Default Eligible Bidder as PSS Seller Admin

  Background: log in as seller admin and get his company
    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"

  @restoreCrudCoreDatabaseBefore

  Scenario: search all RTB Profile Libraries - when default group is true, this should not be seen in the response
    When the PSS user searches all RTB Profile Libraries
    Then request passed successfully
    And returned "RTB Profile Libraries" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/GetAll_ER_Default.json"

  Scenario: search RTB Profile Library
    When the PSS user searches all RTB Profile Libraries for a publisher WITH ref seller_admin parameter
    Then "RTB Profile Libraries search" failed with "401" response code

  Scenario: search RTB Profile Library
    When the user searches all RTB Profile Libraries
    Then "RTB Profile Libraries search" failed with "401" response code

  Scenario: update RTB Profile Library with different parameters
    When the PSS user searches RTB Profile Library "BDGRP1"
    And the PSS user updates the RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/payload/update/UpdateEligible_payload_pss.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/expected_results/update/UpdateEligible_ER_pss.json"

  Scenario: clone RTB Profile Library
    When the PSS user clones RTB Profile Library from the json file "jsons/genevacrud/rtbprofilelibrary/pss/payload/create/Clone_payloadDefault.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rtbprofilelibrary/pss/expected_results/create/CloneResult_Default.json"

  Scenario: delete RTB Profile Library
    When the PSS user searches RTB Profile Library "TEST DEFAULT"
    And the PSS user deletes the RTB Profile Library
    Then request passed without errors
    And the PSS user cannot search deleted library
