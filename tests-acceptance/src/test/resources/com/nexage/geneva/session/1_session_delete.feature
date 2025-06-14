Feature: Delete redis sessions in cache

  Scenario Outline: delete redis session unauthorized
    Given the user "<user>" has logged in with role "<role>"
    When the user deletes the session
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

    Examples:
      | user              | role          |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |
      | SellerAdmin2      | AdminSeller   |
      | SellerManager2    | ManagerSeller |
      | SellerUser2       | UserSeller    |
      | buyerAdmin2       | AdminBuyer    |
      | buyermanager2     | ManagerBuyer  |
      | buyeruser2        | UserBuyer     |

