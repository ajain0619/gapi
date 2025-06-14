Feature: Assigned inventory for deal

  Scenario Outline: Update deal specific inventory
    When the user "admin1c" has logged in with role "AdminNexage"
    And the user selects deal PID <dealPid>
    And the user updates specific assigned inventory from JSON file <payloadFile>
    Then request passed successfully with code "201"

    Examples:
      | dealPid | payloadFile                                                                   |
      | 1       | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" |
      | 2       | "jsons/genevacrud/deal/payload/update/update_specific_inventory_complex.json" |

  Scenario Outline: Update deal specific inventory unauthorized
    When the user "<user>" has logged in with role "<role>"
    And the user selects deal PID <dealPid>
    And the user updates specific assigned inventory from JSON file <payloadFile>
    Then "update deal" failed with "401" response code

    Examples:
      | user              | dealPid | payloadFile                                                                   | role          |
      | tagarchiveuser    | 2       | "jsons/genevacrud/deal/payload/update/update_specific_inventory_complex.json" | UserSeller    |
      | tagarchiveadmin   | 1       | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | AdminSeller   |
      | tagarchivemanager | 1       | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | ManagerSeller |

  Scenario: Update deal formula inventory
    When the user "admin1c" has logged in with role "AdminNexage"
    And the user selects deal PID 1
    And the user updates formula assigned inventory from JSON file "jsons/genevacrud/deal/payload/update/update_formula_inventory_payload.json"
    Then request passed successfully with code "201"

  Scenario: Get specific inventory for deal
    When the user "admin1c" has logged in with role "AdminNexage"
    And  the user fetches specific inventory for deal PID 2
    Then request passed successfully with code "200"
    And returned "assigned-inventory" data matches the following json file "jsons/genevacrud/deal/expected_results/search/specific_inventory_ER.json"

  Scenario: Get specific inventory no placements
    When the user "admin1c" has logged in with role "AdminNexage"
    And the user selects deal PID 1
    And the user updates specific assigned inventory from JSON file "jsons/genevacrud/deal/payload/update/assigned_inventory_no_placements.json"
    Then request passed successfully with code "201"
    And the user fetches specific inventory for deal PID 1
    Then request passed successfully with code "200"
    And returned "assigned-inventory" data matches the following json file "jsons/genevacrud/deal/expected_results/search/assigned_inventory_no_placements_ER.json"

  Scenario: Get formula inventory for deal
    When the user "admin1c" has logged in with role "AdminNexage"
    And the user fetches formula inventory for deal PID 1
    Then request passed successfully with code "200"
    And returned "assigned-inventory" data matches the following json file "jsons/genevacrud/deal/expected_results/search/formula_inventory_ER.json"
    And the returned data doesn't contain the following fields "placementFormula"

  Scenario Outline: Update deal specific inventory associated with seller
    When the user "<user>" has logged in with role "<role>"
    And the user selects deal PID <dealPid>
    And the user updates specific assigned inventory associated with seller "<sellerPid>" from JSON file <payloadFile>
    Then request passed successfully with code "201"

    Examples:
      | user              | dealPid | sellerPid | payloadFile                                                                   | role          |
      | admin1c           | 1       | 10213     | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | AdminNexage   |
      | admin1c           | 2       | 10213     | "jsons/genevacrud/deal/payload/update/update_specific_inventory_complex.json" | AdminNexage   |
      | tagarchiveadmin   | 1       | 10213     | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | AdminSeller   |
      | tagarchivemanager | 1       | 10213     | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | ManagerSeller |

  Scenario Outline: Update deal specific inventory associated with seller unauthorized
    When the user "<user>" has logged in with role "<role>"
    And the user selects deal PID <dealPid>
    And the user updates specific assigned inventory associated with seller "<sellerPid>" from JSON file <payloadFile>
    Then "update deal" failed with "401" response code

    Examples:
      | user              | dealPid | sellerPid | payloadFile                                                                   | role          |
      | tagarchiveuser    | 1       | 10213     | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | UserSeller    |
      | tagarchiveadmin   | 1       | 1         | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | AdminSeller   |
      | tagarchivemanager | 1       | 1         | "jsons/genevacrud/deal/payload/update/update_specific_inventory_payload.json" | ManagerSeller |

  Scenario: Update seller deal formula inventory
    Given the user "NexageUser1" has logged in with role "UserNexage"
    And the user selects deal PID 1
    When the user updates assigned inventory formula associated with seller "1" from JSON file "jsons/genevacrud/deal/payload/update/seller/update_seller_formula_inventory_payload.json"
    Then request passed successfully with code "200"
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/seller/update_seller_formula_inventory_ER.json"

  Scenario: Update seller deal formula inventory unauthorized
    Given the user "buyeruser1" has logged in with role "UserBuyer"
    And the user selects deal PID 1
    When the user updates assigned inventory formula associated with seller "274" from JSON file "jsons/genevacrud/deal/payload/update/seller/update_seller_formula_inventory_payload.json"
    Then "update seller deal" failed with "401" response code

  Scenario: Update seller deal formula inventory invalid seller placement formula
    Given the user "NexageUser1" has logged in with role "UserNexage"
    And the user selects deal PID 1
    When the user updates assigned inventory formula associated with seller "2" from JSON file "jsons/genevacrud/deal/payload/update/seller/update_seller_formula_inventory_payload.json"
    Then "update seller deal" failed with "404" response code

  Scenario: Download the inventory file associated with the deal
    Given the user "NexageUser1" has logged in with role "UserNexage"
    And the user selects deal PID 9
    When the user downloads the deal inventory file for deal pid "9" and file pid "1"
    Then request passed successfully with code "200"
    And the downloaded deal inventory file name should be "domains.xls" and the content should match the content of "excels/genevacrud/deal/expected_results/domains.xls"

  Scenario: Upload the valid bulk inventory file
    Given the user "NexageUser1" has logged in with role "AdminNexage"
    When the user uploads the bulk inventory file "csvs/bulkInventoryFiles/valid-entries.csv"
    Then request passed successfully with code "200"
    And returned "bulk-inventories" data matches the following json file "jsons/genevacrud/deal/expected_results/search/bulk_inventory_valid_file_ER.json"

  Scenario: Upload the invalid bulk inventory file
    Given the user "NexageUser1" has logged in with role "AdminNexage"
    When the user uploads the bulk inventory file "csvs/bulkInventoryFiles/invalid-entries.csv"
    Then "bulk-inventories" failed with "400" response code and error message ""File contains 3 invalid entries. Please review the following invalid entries: \"248,62,7762\",\"248,4532\",\"2148\""

  Scenario: Upload the bulk inventory file with missing or incorrect header
    Given the user "NexageUser1" has logged in with role "AdminNexage"
    When the user uploads the bulk inventory file "csvs/bulkInventoryFiles/valid-entries-missing-header.csv"
    Then "bulk-inventories" failed with "400" response code and error message "Invalid csv or xls file"

  Scenario: Upload the inventory file
    Given the user "NexageUser1" has logged in with role "AdminNexage"
    When the user uploads the inventory file "csvs/inventoryFiles/domains.csv", file name "domains.csv", file type "DOMAIN", deal id "1"
    Then request passed successfully with code "201"

  Scenario: Upload invalid inventory file
    Given the user "NexageUser1" has logged in with role "AdminNexage"
    When the user uploads the inventory file "csvs/inventoryFiles/invalid-domains.csv", file name "invalid-domains.csv", file type "DOMAIN", deal id "1"
    Then "inventory file upload" failed with "400" response code
