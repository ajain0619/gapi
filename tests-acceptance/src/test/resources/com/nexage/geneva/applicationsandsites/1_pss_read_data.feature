Feature: PSS: Get Publisher Data

  Scenario Outline: Grab publisher data
    Given the user "<user>" has logged in with role "<role>"
    When "<company_pid>" publisher is retrieved
    Then request passed successfully
    And returned "publisher" data matches the following json file "<json_file>"

    Examples:
      | user              | role          | company_pid | json_file                                                                           |
      | admin1c           | AdminNexage   | 1           | jsons/genevacrud/inventorycreation/siteapps/expected_results/1_pss_data_ER.json     |
      | crudnexagemanager | ManagerNexage | 1           | jsons/genevacrud/inventorycreation/siteapps/expected_results/1_pss_data_ER.json     |
      | crudnexageuser    | UserNexage    | 1           | jsons/genevacrud/inventorycreation/siteapps/expected_results/1_pss_data_ER.json     |
      | selleradmin1      | AdminSeller   | 10063       | jsons/genevacrud/inventorycreation/siteapps/expected_results/10063_pss_data_ER.json |
      | sellermanager1    | ManagerSeller | 10063       | jsons/genevacrud/inventorycreation/siteapps/expected_results/10063_pss_data_ER.json |
      | selleruser1       | UserSeller    | 10063       | jsons/genevacrud/inventorycreation/siteapps/expected_results/10063_pss_data_ER.json |

  Scenario Outline: Grab publisher data unauthorized
    Given the user "<user>" has logged in with role "<role>"
    When "1" publisher is retrieved
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

    Examples:
      | user           | role          |
      | SellerAdmin2   | AdminSeller   |
      | SellerManager2 | ManagerSeller |
      | SellerUser2    | UserSeller    |
      | buyerAdmin2    | AdminBuyer    |
      | buyermanager2  | ManagerBuyer  |
      | buyeruser2     | UserBuyer     |

  Scenario: Grab publisher data negative
    Given the user "admin1c" has logged in with role "AdminNexage"
    When "null" publisher is retrieved
    Then response failed with "400" response code, error message "Bad Request" and without field errors.
