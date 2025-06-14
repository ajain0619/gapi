@sso
Feature: Create users

  Scenario Outline: create 1c user <file_payload> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a userdto from the json file "jsons/genevacrud/user/payload/create/v1/<file_payload>_payload.json"
    Then request passed successfully with code "201"
    And returned "created user" data matches the following json file "jsons/genevacrud/user/expected_results/create/v1/<file_ER>_ER.json"

    Examples:
      | user              | role          | file_payload             | file_ER                  |
      | crudnexageadmin   | AdminNexage   | NexageAdmin1_V1          | NexageAdmin1_V1          |
      | crudnexageadmin   | AdminNexage   | NexageManager1_V1        | NexageManager1_V1        |
      | crudnexageadmin   | AdminNexage   | SmartExchangeManager1_V1 | SmartExchangeManager1_V1 |
      | crudnexageadmin   | AdminNexage   | NexageUser1_V1           | NexageUser1_V1           |
      | crudnexageadmin   | AdminNexage   | SellerAdmin1_V1          | SellerAdmin1_V1          |
      | crudnexageadmin   | AdminNexage   | SellerManager1_V1        | SellerManager1_V1        |
      | crudnexageadmin   | AdminNexage   | SellerUser1_V1           | SellerUser1_V1           |
      | crudnexageadmin   | AdminNexage   | BuyerAdmin1_V1           | BuyerAdmin1_V1           |
      | crudnexageadmin   | AdminNexage   | BuyerManager1_V1         | BuyerManager1_V1         |
      | crudnexageadmin   | AdminNexage   | BuyerUser1_V1            | BuyerUser1_V1            |
      | crudnexagemanager | ManagerNexage | SellerAdmin2_V1          | SellerAdmin2_V1          |
      | crudnexagemanager | ManagerNexage | SellerManager2_V1        | SellerManager2_V1        |
      | crudnexagemanager | ManagerNexage | SellerUser2_V1           | SellerUser2_V1           |
      | crudnexagemanager | ManagerNexage | BuyerAdmin2_V1           | BuyerAdmin2_V1           |
      | crudnexagemanager | ManagerNexage | BuyerManager2_V1         | BuyerManager2_V1         |
      | crudnexagemanager | ManagerNexage | BuyerUser2_V1            | BuyerUser2_V1            |
      | crudselleradmin   | AdminSeller   | SellerAdmin3_V1          | SellerAdmin3_V1          |
      | crudselleradmin   | AdminSeller   | SellerManager3_V1        | SellerManager3_V1        |
      | crudselleradmin   | AdminSeller   | SellerUser3_V1           | SellerUser3_V1           |
      | crudbuyeradmin    | AdminBuyer    | BuyerAdmin3_V1           | BuyerAdmin3_V1           |
      | crudbuyeradmin    | AdminBuyer    | BuyerManager3_V1         | BuyerManager3_V1         |
      | crudbuyeradmin    | AdminBuyer    | BuyerUser3_V1            | BuyerUser3_V1            |

  Scenario Outline: create external user <file_payload> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a userdto from the json file "jsons/genevacrud/user/payload/create/v1/<file_payload>_payload.json"
    Then request passed successfully with code "201"
    And returned "created user" data matches the following json file "jsons/genevacrud/user/expected_results/create/v1/<file_ER>_ER.json"

    Examples:
      | user            | role        | file_payload      | file_ER           |
      | crudselleradmin | AdminSeller | SellerAdmin5_V1   | SellerAdmin5_V1   |
      | crudselleradmin | AdminSeller | SellerManager5_V1 | SellerManager5_V1 |
      | crudselleradmin | AdminSeller | SellerUser5_V1    | SellerUser5_V1    |
      | crudbuyeradmin  | AdminBuyer  | BuyerAdmin5_V1    | BuyerAdmin5_V1    |
      | crudbuyeradmin  | AdminBuyer  | BuyerManager5_V1  | BuyerManager5_V1  |
      | crudbuyeradmin  | AdminBuyer  | BuyerUser5_V1     | BuyerUser5_V1     |

  Scenario Outline: create seller seat user <filename> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a userdto from the json file "jsons/genevacrud/user/payload/create/v1/<file_payload>_payload.json"
    Then request passed successfully with code "201"
    And returned "created seller seat user" data matches the following json file "jsons/genevacrud/user/expected_results/create/v1/<file_ER>_ER.json"

    Examples:
      | user              | role          | file_payload          | file_ER               |
      | crudnexageadmin   | AdminNexage   | Seller1RoleAdmin_V1   | Seller1RoleAdmin_V1   |
      | crudnexageadmin   | AdminNexage   | Seller1RoleManager_V1 | Seller1RoleManager_V1 |
      | crudnexagemanager | ManagerNexage | Seller2RoleAdmin_V1   | Seller2RoleAdmin_V1   |
      | crudnexagemanager | ManagerNexage | Seller2RoleManager_V1 | Seller2RoleManager_V1 |

  Scenario Outline: <user> is not authorized to create 1c user <file_payload>
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a userdto from the json file "jsons/genevacrud/user/payload/create/v1/<file_payload>_payload.json"
    Then "user creation" failed with "401" response code

    Examples:
      | user              | role          | file_payload      |
      | crudnexagemanager | ManagerNexage | NexageAdmin2_V1   |
      | crudnexagemanager | ManagerNexage | NexageManager2_V1 |
      | crudnexagemanager | ManagerNexage | NexageUser2_V1    |
      | crudnexageuser    | UserNexage    | NexageAdmin2_V1   |
      | crudnexageuser    | UserNexage    | NexageManager2_V1 |
      | crudnexageuser    | UserNexage    | NexageUser2_V1    |
      | crudselleradmin   | AdminSeller   | SellerAdmin2_V1   |
      | crudbuyeradmin    | AdminBuyer    | BuyerAdmin2_V1    |
      | crudsellermanager | ManagerSeller | SellerAdmin2_V1   |
      | crudsellermanager | ManagerSeller | SellerManager2_V1 |
      | crudsellermanager | ManagerSeller | SellerUser2_V1    |
      | crudbuyermanager  | ManagerBuyer  | BuyerAdmin2_V1    |
      | crudbuyermanager  | ManagerBuyer  | BuyerManager2_V1  |
      | crudbuyermanager  | ManagerBuyer  | BuyerUser2_V1     |

  Scenario Outline: user cannot create duplicate 1c user
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    When the user creates a duplicate 1c userdto from the json file "jsons/genevacrud/user/payload/create/v1/<file_payload>_payload.json"
    Then "user creation" failed with "400" response code

    Examples:
      | file_payload             |
      | UserDuplicateUsername_V1 |
      | UserDuplicateEmail_V1    |

  Scenario Outline: cannot create seller seat user <filename> by <user> for seller seat without sellers
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a userdto from the json file "jsons/genevacrud/user/payload/create/v1/<file_payload>_payload.json"
    Then "seller seat user creation" failed with "400" response code and error message "Can't create a user for seller seat that doesn't have sellers assigned to it"

    Examples:
      | user            | file_payload        | role        |
      | crudnexageadmin | Seller3RoleAdmin_V1 | AdminNexage |

  Scenario Outline: update external user <user to update> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then request passed successfully
    And returned "updated user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/<file_ER>_ER.json"

    Examples:
      | user            | role        | user to update | file_payload      | file_ER           |
      | crudselleradmin | AdminSeller | SellerAdmin5   | SellerAdmin5_V1   | SellerAdmin5_V1   |
      | crudselleradmin | AdminSeller | SellerManager5 | SellerManager5_V1 | SellerManager5_V1 |
      | crudselleradmin | AdminSeller | SellerUser5    | SellerUser5_V1    | SellerUser5_V1    |
      | crudbuyeradmin  | AdminBuyer  | BuyerAdmin5    | BuyerAdmin5_V1    | BuyerAdmin5_V1    |
      | crudbuyeradmin  | AdminBuyer  | BuyerManager5  | BuyerManager5_V1  | BuyerManager5_V1  |
      | crudbuyeradmin  | AdminBuyer  | BuyerUser5     | BuyerUser5_V1     | BuyerUser5_V1     |

  Scenario Outline: update external user affiliation <user to update> by <user>
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then request passed successfully
    And returned "updated user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/<file_ER>_ER.json"

    Examples:
      | user            | role        | user to update | file_payload      | file_ER           |
      | crudnexageadmin | AdminNexage | SellerAdmin5   | SellerAdmin5_2_V1 | SellerAdmin5_2_V1 |
      | crudnexageadmin | AdminNexage | SellerAdmin5   | SellerAdmin5_3_V1 | SellerAdmin5_3_V1 |
      | crudnexageadmin | AdminNexage | SellerAdmin5   | SellerAdmin5_4_V1 | SellerAdmin5_4_V1 |

  Scenario Outline: delete <user to delete> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user deletes user "<user to delete>"
    Then request passed with "204" response code

    Examples:
      | user            | role        | user to delete |
      | crudselleradmin | AdminSeller | SellerAdmin5   |
      | crudselleradmin | AdminSeller | SellerManager5 |
      | crudselleradmin | AdminSeller | SellerUser5    |
      | crudbuyeradmin  | AdminBuyer  | BuyerAdmin5    |
      | crudbuyeradmin  | AdminBuyer  | BuyerManager5  |
      | crudbuyeradmin  | AdminBuyer  | BuyerUser5     |
