Feature: Seller - Sites - Placements - Tags:

  Scenario Outline: Get the tags
    Given the user "<user_name>" has logged in with role "<role>"
    When the user hits the tags endpoint
    Then the request returned status "OK"
    And returned "tags" data matches the following json file "jsons/genevacrud/tag/expected_results/get/Tags_ER.json"
    Examples:
      | user_name      | role          |
      | admin1c        | AdminNexage   |
      | selleradmin1   | AdminSeller   |
      | sellermanager1 | ManagerSeller |
      | selleruser1    | UserSeller    |
      | buyeruser1     | UserBuyer     |

  Scenario: Get Tags Access denied
    Given the user "role-api-user-1c" logs in via B2B
    When the user hits the tags endpoint
    Then response failed with "403" response code, error message "Forbidden" and without field errors.
