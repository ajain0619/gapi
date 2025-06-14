@sso
Feature: create, update, delete users

  Scenario Outline: <user> is not authorized to delete <user to delete>
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user deletes 1c user "<user to delete>"
    Then "user deletion" failed with "401" response code

    Examples:
      | user              | role          | user to delete |
      | crudnexagemanager | ManagerNexage | NexageAdmin1   |
      | crudnexagemanager | ManagerNexage | NexageManager1 |
      | crudnexagemanager | ManagerNexage | NexageUser1    |
      | crudnexageuser    | UserNexage    | NexageAdmin1   |
      | crudnexageuser    | UserNexage    | NexageManager1 |
      | crudnexageuser    | UserNexage    | NexageUser1    |

  Scenario Outline: delete <user to delete> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user deletes 1c user "<user to delete>"
    Then request passed with "204" response code

    Examples:
      | user              | role          | user to delete |
      | crudnexageadmin   | AdminNexage   | NexageAdmin1   |
      | crudnexageadmin   | AdminNexage   | NexageUser1    |
      | crudnexageadmin   | AdminNexage   | SellerAdmin2   |
      | crudnexageadmin   | AdminNexage   | SellerManager2 |
      | crudnexageadmin   | AdminNexage   | SellerUser2    |
      | crudnexageadmin   | AdminNexage   | BuyerAdmin2    |
      | crudnexageadmin   | AdminNexage   | BuyerManager2  |
      | crudnexageadmin   | AdminNexage   | BuyerUser2     |
      | crudnexagemanager | ManagerNexage | SellerAdmin1   |
      | crudnexagemanager | ManagerNexage | BuyerManager1  |
      | crudselleradmin   | AdminSeller   | SellerManager1 |
      | crudselleradmin   | AdminSeller   | SellerUser1    |
      | crudbuyeradmin    | AdminBuyer    | BuyerAdmin1    |
      | crudbuyeradmin    | AdminBuyer    | BuyerUser1     |

  Scenario: user deletes company contact user and company contact reference is removed
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And users are retrieved
    And count of companies with user "NexageManager1" as contact 1
    When the user deletes 1c user "NexageManager1"
    Then request passed with "204" response code
    And count of companies with user "NexageManager1" as contact 0

  Scenario: user cannot delete himself
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    When the user "crudnexageadmin" deletes himself
    Then "user deletion" failed with "401" response code
