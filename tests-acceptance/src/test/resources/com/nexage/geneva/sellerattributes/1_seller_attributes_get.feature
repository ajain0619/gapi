Feature: Seller Attributes - Get Seller Attributes

  Scenario Outline: User, Manager, Admin SELLERS should have access to sellerattributes
    Given the user "<user>" has logged in
    When the user gets seller attributes for seller id "<sellerid>"
    Then request passed successfully
    Examples:
      | user              | sellerid |
      | crudselleruser    | 10063    |
      | crudsellermanager | 10063    |
      | crudselleradmin   | 10063    |

  Scenario Outline: User, Manager, Admin NEXAGE should have access to sellerattributes
    Given the user "<user>" has logged in with role "<role>"
    When the user gets seller attributes for seller id "<sellerid>"
    Then request passed successfully
    Examples:
      | user              | sellerid | role          |
      | crudnexageuser    | 10063    | AdminNexage   |
      | crudnexagemanager | 10063    | ManagerNexage |
      | crudnexageadmin   | 10063    | UserNexage    |
