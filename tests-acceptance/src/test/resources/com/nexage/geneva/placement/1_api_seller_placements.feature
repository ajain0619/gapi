Feature: Api Seller Placements: Get

  Background: log in as seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    And the user selects the site "AS8A"

  @restoreCrudCoreDatabaseBefore
  Scenario: get all placements under a site paginated
    When The site pid is passed into grab "1" placements
    Then request passed successfully
    And returned "placements" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacementsPaginated0_ER.json"
    And grab the second page of "1" placements "1" page
    Then request passed successfully
    And returned "placements" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacementsPaginated1_ER.json"

  Scenario Outline: Confirm placememtType and active filters are applied
    Given a request is made to list the placements for this company and placement type "<placement_type>" and status "<status>"
    Then request passed successfully
    And returned "placements" data has the key "totalElements" and value "<total_elements>"

    Examples:
      | placement_type   | status   | total_elements |
      | BANNER           | ACTIVE   | 8              |
      | INTERSTITIAL     | ACTIVE   | 0              |
      | MEDIUM_RECTANGLE | ACTIVE   | 0              |
      | NATIVE           | ACTIVE   | 0              |
      | INSTREAM_VIDEO   | ACTIVE   | 0              |
      | REWARDED_VIDEO   | ACTIVE   | 0              |
      | BANNER           | INACTIVE | 0              |

  Scenario: Get Placements with minimal data
    When The seller pid is pass into grab "105" and the site pid "950" grab all minimal placements
    Then request passed successfully
    And returned "placements" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacementsMinimalPaged1_ER.json"

  Scenario: Get Placements with minimal data
    When The seller pid is pass into grab "105" and the site pid "950" grab all minimal placements with QT "bod"
    Then request passed successfully
    And returned "placements" data matches the following json file "jsons/genevacrud/placements/expected_results/GetPlacementsMinimalPagedQT1_ER.json"
