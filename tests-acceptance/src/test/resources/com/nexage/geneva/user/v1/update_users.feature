Feature: Create users

  Scenario Outline: update <user to update> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then request passed successfully
    And returned "updated user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/<file_ER>_ER.json"

    Examples:
      | user              | role          | user to update | file_payload             | file_ER                  |
      | crudnexageadmin   | AdminNexage   | NexageAdmin1   | NexageAdmin1_V1          | NexageAdmin1_V1          |
      | crudnexageadmin   | AdminNexage   | NexageManager1 | NexageManager1_V1        | NexageManager1_V1        |
      | crudnexageadmin   | AdminNexage   | NexageManager1 | SmartExchangeManager1_V1 | SmartExchangeManager1_V1 |
      | crudnexageadmin   | AdminNexage   | NexageUser1    | NexageUser1_V1           | NexageUser1_V1           |
      | crudnexageadmin   | AdminNexage   | SellerAdmin1   | SellerAdmin1_V1          | SellerAdmin1_V1          |
      | crudnexageadmin   | AdminNexage   | SellerManager1 | SellerManager1_V1        | SellerManager1_V1        |
      | crudnexageadmin   | AdminNexage   | SellerUser1    | SellerUser1_V1           | SellerUser1_V1           |
      | crudnexageadmin   | AdminNexage   | BuyerAdmin1    | BuyerAdmin1_V1           | BuyerAdmin1_V1           |
      | crudnexageadmin   | AdminNexage   | BuyerManager1  | BuyerManager1_V1         | BuyerManager1_V1         |
      | crudnexageadmin   | AdminNexage   | BuyerUser1     | BuyerUser1_V1            | BuyerUser1_V1            |
      | crudnexagemanager | ManagerNexage | SellerManager2 | SellerManager2_V1        | SellerManager2_V1        |
      | crudnexagemanager | ManagerNexage | SellerUser2    | SellerUser2_V1           | SellerUser2_V1           |
      | crudnexagemanager | ManagerNexage | BuyerAdmin2    | BuyerAdmin2_V1           | BuyerAdmin2_V1           |
      | crudnexagemanager | ManagerNexage | BuyerManager2  | BuyerManager2_V1         | BuyerManager2_V1         |
      | crudnexagemanager | ManagerNexage | BuyerUser2     | BuyerUser2_V1            | BuyerUser2_V1            |
      | crudselleradmin   | AdminSeller   | SellerAdmin1   | SellerAdmin1V1_V1        | SellerAdmin1V1_V1        |
      | crudselleradmin   | AdminSeller   | SellerManager3 | SellerManager3_V1        | SellerManager3_V1        |
      | crudselleradmin   | AdminSeller   | SellerUser3    | SellerUser3_V1           | SellerUser3_V1           |
      | crudbuyeradmin    | AdminBuyer    | BuyerAdmin1    | BuyerAdminV1_V1          | BuyerAdminV1_V1          |
      | crudbuyeradmin    | AdminBuyer    | BuyerManager3  | BuyerManager3_V1         | BuyerManager3_V1         |
      | crudbuyeradmin    | AdminBuyer    | BuyerUser3     | BuyerUser3_V1            | BuyerUser3_V1            |


  Scenario Outline: update seller seat user <user to update> by <user> with different roles
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then request passed successfully
    And returned "created seller seat user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/<file_ER>_ER.json"

    Examples:
      | user            | role        | user to update | file_payload    | file_ER         |
      | crudnexageadmin | AdminNexage | SellerAdmin1   | SellerAdmin1_V1 | SellerAdmin1_V1 |

  Scenario Outline: <user> is not authorized to update <user to update>
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<filename>_payload.json"
    Then "user deletion" failed with "401" response code

    Examples:
      | user              | role          | user to update | filename          |
      | crudnexagemanager | ManagerNexage | NexageAdmin1   | NexageAdmin1_V1   |
      | crudnexagemanager | ManagerNexage | NexageManager1 | NexageManager1_V1 |
      | crudnexagemanager | ManagerNexage | NexageUser1    | NexageUser1_V1    |
      | crudnexageuser    | UserNexage    | NexageAdmin1   | NexageAdmin1_V1   |
      | crudnexageuser    | UserNexage    | NexageManager1 | NexageManager1_V1 |
      | crudnexageuser    | UserNexage    | NexageUser1    | NexageUser1_V1    |

  Scenario Outline: update <user to update> set dealAdmin permission by <user>
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then request passed successfully
    And returned "updated user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/<file_ER>_ER.json"

    Examples:
      | user            | role        | user to update | file_payload        | file_ER             |
      | crudnexageadmin | AdminNexage | NexageAdmin1   | NexageAdminDA1_V1   | NexageAdminDA1_V1   |
      | crudnexageadmin | AdminNexage | NexageManager1 | NexageManagerDA1_V1 | NexageManagerDA1_V1 |
      | crudnexageadmin | AdminNexage | NexageUser1    | NexageUserDA1_V1    | NexageUserDA1_V1    |
      | crudnexageadmin | AdminNexage | SellerAdmin1   | SellerAdminDA1_V1   | SellerAdminDA1_V1   |
      | crudnexageadmin | AdminNexage | SellerManager1 | SellerManagerDA1_V1 | SellerManagerDA1_V1 |
      | crudnexageadmin | AdminNexage | SellerUser1    | SellerUserDA1_V1    | SellerUserDA1_V1    |
      | crudnexageadmin | AdminNexage | BuyerAdmin1    | BuyerAdminDA1_V1    | BuyerAdminDA1_V1    |
      | crudnexageadmin | AdminNexage | BuyerManager1  | BuyerManagerDA1_V1  | BuyerManagerDA1_V1  |
      | crudnexageadmin | AdminNexage | BuyerUser1     | BuyerUserDA1_V1     | BuyerUserDA1_V1     |

  Scenario Outline: update <user to update> remove dealAdmin permission by <user>
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then request passed successfully
    And returned "updated user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/<file_ER>_ER.json"

    Examples:
      | user            | role        | user to update | file_payload           | file_ER                |
      | crudnexageadmin | AdminNexage | NexageAdmin1   | NexageAdminNotDA1_V1   | NexageAdminNotDA1_V1   |
      | crudnexageadmin | AdminNexage | NexageManager1 | NexageManagerNotDA1_V1 | NexageManagerNotDA1_V1 |
      | crudnexageadmin | AdminNexage | NexageUser1    | NexageUserNotDA1_V1    | NexageUserNotDA1_V1    |
      | crudnexageadmin | AdminNexage | SellerAdmin1   | SellerAdminNotDA1_V1   | SellerAdminNotDA1_V1   |
      | crudnexageadmin | AdminNexage | SellerManager1 | SellerManagerNotDA1_V1 | SellerManagerNotDA1_V1 |
      | crudnexageadmin | AdminNexage | SellerUser1    | SellerUserNotDA1_V1    | SellerUserNotDA1_V1    |
      | crudnexageadmin | AdminNexage | BuyerAdmin1    | BuyerAdminNotDA1_V1    | BuyerAdminNotDA1_V1    |
      | crudnexageadmin | AdminNexage | BuyerManager1  | BuyerManagerNotDA1_V1  | BuyerManagerNotDA1_V1  |
      | crudnexageadmin | AdminNexage | BuyerUser1     | BuyerUserNotDA1_V1     | BuyerUserNotDA1_V1     |

  Scenario Outline: update <user to update> set dealAdmin permission by <user> with invalid roles
    Given the user "<user>" has logged in with role "<role>"
    And users are retrieved
    When the user updates a userdto "<user to update>" from the json file "jsons/genevacrud/user/payload/update/v1/<file_payload>_payload.json"
    Then "user update" failed with "401" response code

    Examples:
      | user              | role | user to update | file_payload        |
      | crudnexagemanager | ManagerNexage | NexageAdmin1   | NexageAdminDA1_V1   |
      | crudnexagemanager | ManagerNexage | NexageManager1 | NexageManagerDA1_V1 |
      | crudnexagemanager | ManagerNexage | NexageUser1    | NexageUserDA1_V1    |
      | crudnexagemanager | ManagerNexage | SellerAdmin1   | SellerAdminDA1_V1   |
      | crudnexagemanager | ManagerNexage | SellerManager1 | SellerManagerDA1_V1 |
      | crudnexagemanager | ManagerNexage | SellerUser1    | SellerUserDA1_V1    |
      | crudnexagemanager | ManagerNexage | BuyerAdmin1    | BuyerAdminDA1_V1    |
      | crudnexagemanager | ManagerNexage | BuyerManager1  | BuyerManagerDA1_V1  |
      | crudnexagemanager | ManagerNexage | BuyerUser1     | BuyerUserDA1_V1     |
      | crudselleradmin   | AdminSeller   | SellerAdmin1   | SellerAdminDA1_V1   |
      | crudselleradmin   | AdminSeller   | SellerManager1 | SellerManagerDA1_V1 |
      | crudselleradmin   | AdminSeller   | SellerUser1    | SellerUserDA1_V1    |
      | crudbuyeradmin    | AdminBuyer    | BuyerAdmin1    | BuyerAdminDA1_V1    |
      | crudbuyeradmin    | AdminBuyer    | BuyerManager1  | BuyerManagerDA1_V1  |
      | crudbuyeradmin    | AdminBuyer    | BuyerUser1     | BuyerUserDA1_V1     |

  Scenario: update user Primary Contact checkbox
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And users are retrieved
    When the user updates a userdto "NexageAdmin1" from the json file "jsons/genevacrud/user/payload/update/v1/NexageAdminPrimaryContact_V1_payload.json"
    Then request passed successfully
    And returned "updated user" data matches the following json file "jsons/genevacrud/user/expected_results/update/v1/NexageAdminPrimaryContact_V1_ER.json"
