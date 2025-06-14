Feature: Api Seller Placements Summaries: Get

  Background: log in as seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    And the user selects the site "AS8A"

  Scenario: get all placements summaries under a site paginated
    When Get page "0" containing "4" placements summaries from between "2020-04-30T00:00:00-04:00" and "2020-05-06T05:37:51-04:00"
    Then request passed successfully
    And returned "placements" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacementsSummariesPaginated0_ER.json"
    And Get page "1" containing "4" placements summaries from between "2020-04-30T00:00:00-04:00" and "2020-05-06T05:37:51-04:00"
    Then request passed successfully
    And returned "placements" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacementsSummariesPaginated1_ER.json"
