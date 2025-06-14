Feature: Seller

  Scenario: get seller
    Given the user "crudselleruser" has logged in with role "UserSeller"
    When the user gets seller with pid "10063"
    Then request passed successfully
    And returned "seller" data matches the following json file "jsons/genevacrud/seller/expected_results/get_seller_ER.json"

  Scenario Outline: get seller - authorized
    Given the user "<user>" has logged in with role "<role>"
    When the user gets seller with pid "<seller_pid>"
    Then request passed successfully

    Examples:
      | user                     | seller_pid | role                 |
      | crudnexageadmin          | 10063      | AdminNexage          |
      | crudnexagemanagersmartex | 10063      | ManagerSmartexNexage |
      | crudnexagemanageryield   | 10063      | ManagerYieldNexage   |
      | crudnexagemanager        | 10063      | ManagerNexage        |
      | crudnexageuser           | 10063      | UserNexage           |
      | crudselleradmin          | 10063      | AdminSeller          |
      | crudsellermanager        | 10063      | ManagerSeller        |
      | crudselleruser           | 10063      | UserSeller           |
      | crudbuyeradmin           | 274        | AdminBuyer           |
      | crudbuyermanager         | 274        | ManagerBuyer         |
      | crudbuyeruser            | 274        | UserBuyer            |

  Scenario: get seller - non-nexage user not associated with seller
    Given the user "crudselleradmin" has logged in with role "AdminSeller"
    When the user gets seller with pid "105"
    Then "request" failed with "401" response code

  Scenario: get all sellers by seller pid
    Given the user "crudselleradmin" has logged in with role "AdminSeller"
    When Get page "0" containing "4" sellers by seller pid "10201"
    Then request passed successfully
    And returned "sellers" data matches the following json file "jsons/genevacrud/seller/expected_results/GetSellersBySellerPidPaginated.json"

  Scenario: get bad request when seller pid is not numeric.
    Given the user "crudselleradmin" has logged in with role "AdminSeller"
    When Get page "0" containing "4" sellers by seller pid "S1"
    Then response failed with "400" response code, error message "Bad Request" and without field errors.
