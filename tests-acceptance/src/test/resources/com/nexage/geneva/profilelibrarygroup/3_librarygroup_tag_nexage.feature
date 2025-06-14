Feature: Rtbprofile library group operations 2

  Background: log in as Nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: get library group hierarchy for Bidder_default
    When the user gets a group pid "12" in seller "10217"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTags_bidder1_Nexage.json"

  Scenario: get library group hierarchy for Bidder_nondefault
    When the user gets a group pid "13" in seller "10217"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTags_bidder2_Nexage.json"

  Scenario: get library group hierarchy for Block_default Categories
    When the user gets a group pid "7" in seller "10217"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTags_block1a_Nexage.json"

  Scenario: get library group hierarchy for Block_default Domains
    When the user gets a group pid "8" in seller "10217"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTags_block1b_Nexage.json"

  Scenario: get library group hierarchy for Block_nondefault Categories
    When the user gets a group pid "9" in seller "10217"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTags_block2a_Nexage.json"

  Scenario: get library group hierarchy for Block_nondefault Domains
    When the user gets a group pid "10" in seller "10217"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTags_block2b_Nexage.json"
