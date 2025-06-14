Feature: Revenue Groups

  Scenario: Get revenue groups as an internal user
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets revenue groups
    Then request passed successfully with code "200"
    And returned "revenue groups" data matches the following json file "jsons/genevacrud/revenuegroup/expected_results/GetAllRevenueGroups_ER.json"

  Scenario: Get revenue groups as an internal user using paging
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets revenue groups page "1" of size "2"
    Then request passed successfully with code "200"
    And returned "revenue groups" data matches the following json file "jsons/genevacrud/revenuegroup/expected_results/GetPagedRevenueGroups_ER.json"

  Scenario: Get revenue groups as an external user
    Given the user "SellerUser1" has logged in with role "UserSeller"
    When the user gets revenue groups
    Then "get revenue groups" failed with "401" response code
