Feature: create, update, delete, search Seller company as seller users

  Scenario Outline: create a Seller company with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then "seller creation" failed with "401" response code

    Examples:
      | user           | role          | file_payload                                           |
      | adminathens1   | AdminSeller   | CreateAllFeatureDisabled_payload                       |
      | SellerManager1 | ManagerSeller | CreateAllFeatureEnabled_payload                        |
      | cpi_seller     | UserSeller    | CreateAllFeatureEnabledReportingTypeRestricted_payload |

  Scenario Outline: delete a Seller company with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    When the user deletes the selected company
    Then "seller search" failed with "401" response code

    Examples:
      | user           | role          |
      | adminathens1   | AdminSeller   |
      | SellerManager1 | ManagerSeller |
      | cpi_seller     | UserSeller    |

  # MX-349 start tests
  Scenario: Retrieve list of Seller company storing ETag value
    Given the user "admin1c" has logged in with role "AdminNexage"
    And "Seller" companies are retrieved
    And ETag value will be saved

  #  same records and ETag with 304 response
  Scenario: Retrieve again list of Seller company without any changes
    Given the user "admin1c" has logged in with role "AdminNexage"
    And "Seller" companies are retrieved
    And ETag values are the same
    Then json content should NOT be present


  # create new seller to retrieve different ETag
  Scenario: create a Seller company
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/MX349_newSeller_payload.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/seller/MX349_newSeller_ER.json"
    And company can be searched out in database
    And company data in database is correct
    And company has currency set to "USD"


   # MX-8596 - wrong currency
  Scenario: create a Seller company with wrong currency
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/MX8596_newSeller_payload_wrong_currency.json"
    Then "seller creation" failed with "400" response code

  # MX-8596
  Scenario: create a Seller company with currency other than USD
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/MX8596_newSeller_payload.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/seller/MX8596_newSeller_ER.json"
    And company can be searched out in database
    And company data in database is correct
    And company has currency set to "EUR"


  # 200 response - NOT same records
  Scenario: Retrieve again list of Seller company WITH changes
    Given the user "admin1c" has logged in with role "AdminNexage"
    And "Seller" companies are retrieved
    And ETag values are NOT the same


  # MX-13166 end tests
  Scenario: create a Seller company with correct revenue_group_pid foreign key
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the revenueGroup data has been added to db
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/newSellerWithRevenueGroup_payload.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/seller/newSellerWithRevenueGroup_ER.json"
    And company can be searched out in database
    And company data in database is correct

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario: create a Seller company with non-existing revenue_group_pid foreign key will fail
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the revenueGroup data has been added to db
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/newSellerWithWrongRevenueGroup_payload.json"
    Then "seller creation" failed with "500" response code


  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario: update Seller company with correct revenue_group_pid foreign key
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    Given the revenueGroup data has been added to db
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateSellerWithRevenueGroup_payload.json"
    Then request passed successfully
    And returned "company update with seller seat" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdateSellerWithRevenueGroup_ER.json"

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario: update Seller company with non-existing revenue_group_pid foreign key will fail
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the revenueGroup data has been added to db
    And "Seller" companies are retrieved
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/newSellerWithWrongRevenueGroup_payload.json"
    Then "seller update" failed with "400" response code


  # scenarios for admin and manager pass instead of failing
  @unstable
  Scenario Outline: update Seller company with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateBy<filename>_payload.json"
    Then "seller update" failed with "401" response code

    Examples:
      | user           | role          | filename |
      | adminathens1   | AdminSeller   | Admin    |
      | SellerManager1 | ManagerSeller | Manager  |
      | cpi_seller     | UserSeller    | User     |

  Scenario Outline: update Seller company with unsupported currency
    Given the user "<user>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/MX8596_newSeller_payload_update_currency.json"
    Then "seller update" failed with "400" response code

    Examples:
      | user    | role        | file_payload                             |
      | admin1c | AdminNexage | MX8596_newSeller_payload_update_currency |


  Scenario Outline: delete a Seller company which has a site with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    When the user deletes the selected company
    Then "company deletion" failed with "401" response code

    Examples:
      | user           | role          |
      | adminathens1   | AdminSeller   |
      | SellerManager1 | ManagerSeller |
      | cpi_seller     | UserSeller    |

  Scenario: update Seller company setting a seller seat
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateSellerWithSellerSeat_payload.json"
    Then request passed successfully
    And returned "company update with seller seat" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdateSellerWithSellerSeat_ER.json"


  Scenario: create a Seller company with a seller seat assigned
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/CreateSellerWithSellerSeat_payload.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/seller/CreateSellerWithSellerSeat_ER.json"
    And company can be searched out in database
    And company data in database is correct
    And company has currency set to "USD"

    Examples:
      | user    | role        | file_payload                       | file_ER                       |
      | admin1c | AdminNexage | CreateSellerWithSellerSeat_payload | CreateSellerWithSellerSeat_ER |
