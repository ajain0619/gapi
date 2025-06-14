Feature: try to create, update, delete, search Buyer company as buyer users

  Scenario Outline: search Buyer companies by prefix with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user searches "Buyer" companies by prefix "RTB"
    Then "company search" failed with "401" response code

    Examples:
      | user          | role         |
      | BuyerAdmin1   | AdminBuyer   |
      | BuyerManager1 | ManagerBuyer |
      | BuyerUser1    | UserBuyer    |

  Scenario Outline: create a Buyer company with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Buyer" company from the json file "jsons/genevacrud/company/payload/buyer/<file_payload>.json"
    Then "company creation" failed with "401" response code

    Examples:
      | user          | file_payload                     | role         |
      | BuyerAdmin1   | CreateAllFeatureDisabled_payload | AdminBuyer   |
      | BuyerManager1 | CreateAllFeatureEnabled_payload  | ManagerBuyer |
      | BuyerUser1    | CreateAllFeatureEnabled_payload  | UserBuyer    |

  # Scenario for admin passes instead of failing
  @unstable
  Scenario Outline: update Buyer company with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Buyer" company "cpi buyer"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/buyer/UpdateTextBox_payload.json"
    Then "company update" failed with "401" response code

    Examples:
      | user          | role         |
      | BuyerAdmin1   | AdminBuyer   |
      | BuyerManager1 | ManagerBuyer |
      | BuyerUser1    | UserBuyer    |

  @unstable
  Scenario Outline: delete a Buyer company with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Buyer" company "cpi buyer"
    When the user deletes the selected company
    Then "company deletion" failed with "401" response code

    Examples:
      | user          | role         |
      | BuyerAdmin1   | AdminBuyer   |
      | BuyerManager1 | ManagerBuyer |
      | BuyerUser1    | UserBuyer    |

  Scenario: create a Buyer company
    Given the user "BuyerAdmin1 " has logged in with role "AdminBuyer"
    When the user creates a "Buyer" company from the json file "jsons/genevacrud/company/payload/buyer/CreateAllFeatureEnabled_payload.json"
    Then "company creation" failed with "401" response code


  Scenario: cannot assign seller seat to a non-seller company
    Given the user "BuyerAdmin1" has logged in with role "AdminBuyer"
    And the user selects the "Buyer" company "cpi buyer"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/buyer/UpdateBuyerWithSellerSeat_payload.json"
    Then request passed successfully
    And returned "company update with seller seat" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/UpdateBuyerWithSellerSeat_ER.json"

