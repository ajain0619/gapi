Feature: Seller Summaries: Get

  Background: log in as seller admin
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: 0. get all seller summaries by seller name
    When Get page "0" containing "4" seller summaries by seller name "adserver" and from between "2020-07-01T00:00:00-04:00" and "2020-07-30T00:00:00-04:00"
    Then request passed successfully
    And returned "sellers" data matches the following json file "jsons/genevacrud/seller/expected_results/GetSellerSummariesBySellerNamePaginated0_ER.json"
    And Get page "1" containing "4" seller summaries by seller name "adserver" and from between "2020-07-01T00:00:00-04:00" and "2020-07-30T00:00:00-04:00"
    Then request passed successfully
    And returned "sellers" data matches the following json file "jsons/genevacrud/seller/expected_results/GetSellerSummariesBySellerNamePaginated1_ER.json"
