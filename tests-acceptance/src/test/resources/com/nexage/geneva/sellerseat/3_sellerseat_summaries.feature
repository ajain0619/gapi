Feature: Seller Seat Summaries: Get

  Background: log in as seller admin
    Given the user "admin1c" has logged in with role "AdminNexage"

  @restoreCrudCoreDatabaseBefore

  Scenario: 0. get all seller summaries for a seller seat
    When Get page "0" containing "4" seller summaries for seller seat "1" from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "seller" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/summary/GetSellerSeatSummariesPaginated0_ER.json"
    And Get page "1" containing "4" seller summaries for seller seat "1" from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "seller" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/summary/GetSellerSeatSummariesPaginated1_ER.json"
