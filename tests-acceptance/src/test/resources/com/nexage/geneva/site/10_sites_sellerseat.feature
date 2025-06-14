Feature: Get Sites for a List of Sellers

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: Get All Sites for Sellers
    Given the user "<user>" has logged in with role "<role>"
    When get sites of sellers "<sellers>" by "companyPid" with size "10" and page "0"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/<expected_file>.json"

      Examples:
      | user              | role            | sellers     |         expected_file                        |
      | pssSellerAdmin    | AdminSeller     | 105         | GetSitesForSellersAsExternalAdmin_ER         |
      | admin1c           | AdminNexage     | 810,812     | GetSitesForSellersAsSellerSeatUser_ER        |
      | sellerSeatUser2   | AdminSellerSeat | 810,812     | GetSitesForSellersAsSellerSeatUser_ER        |
      | sellerSeatUser3   | UserSellerSeat  | 810,811,812 | GetSitesForSellersAsSellerSeatGlobalUser_ER  |

  Scenario Outline: Get All Sites for unAuthorized user
    Given the user "<user>" has logged in with role "<role>"
    When get sites of sellers "<sellers>" by "companyPid" with size "10" and page "0"
    Then "get sites call" failed with "401" response code and error message "Unauthorized"

    Examples:
      | user              | role            | sellers     |
      | sellerSeatUser2   | AdminSellerSeat | 800, 811    |
      | sellerSeatUser3   | UserSellerSeat  | 800         |

  Scenario Outline: Get All Sites for Seller Seat
    Given the user "<user>" has logged in with role "<role>"
    When get sites for seller seat ID "<seller_seat_id>"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/<expected_file>.json"

    Examples:
      | user              | role            | seller_seat_id | expected_file           |
      | admin1c           | AdminNexage     | 2              | GetSitesBySellerSeat_ER |
      | sellerSeatUser2   | AdminSellerSeat | 2              | GetSitesBySellerSeat_ER |
      | sellerSeatUser3   | UserSellerSeat  | 2              | GetSitesBySellerSeat_ER |
