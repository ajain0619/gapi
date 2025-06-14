Feature: get entitlements


  Scenario Outline: Get Entitlements for current user with Query Param with Session
    Given the user "<user>" has logged in with role "<role>"
    When the user grabs all entitlements matching qt current with qf status with follow redirect is "true"
    Then the request returned status "OK"
    And returned "access_token" data matches the following json file "<json_file>"

    Examples:
      | user                   | role                | json_file                                                                                              |
      | crudnexageadmin        | AdminNexage         | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_admin_nexage_QT_QF_ER.json         |
      | crudnexagemanageryield | ManagerYieldNexage  | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_manager_yield_nexage_QT_QF_ER.json |
      | crudnexagemanager      | ManagerNexage       | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_manager_nexage_QT_QF_ER.json       |
      | crudnexageuser         | UserNexage          | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_user_nexage_QT_QF_ER.json          |
      | crudselleradmin        | AdminSeller         | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_admin_seller_QT_QF_ER.json         |
      | crudsellermanager      | ManagerSeller       | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_manager_seller_QT_QF_ER.json       |
      | crudselleruser         | UserSeller          | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_user_seller_QT_QF_ER.json          |
      | crudbuyeradmin         | AdminBuyer          | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_admin_buyer_QT_QF_ER.json          |
      | crudbuyermanager       | ManagerBuyer        | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_manager_buyer_QT_QF_ER.json        |
      | crudbuyeruser          | UserBuyer           | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_user_buyer_QT_QF_ER.json           |
      | svcsellerseat034       | AdminSeatHolder     | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_admin_seatholder_QT_QF_ER.json     |
      | svcsellerseat957       | ManagerSeatHolder   | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_manager_seatholder_QT_QF_ER.json   |
      | svcsellerseat944       | UserSeatHolder      | jsons/genevacrud/entitlements/expected_results/get_all_entitlements_user_seatholder_QT_QF_ER.json      |


  Scenario: Get Entitlements for current user with Query Param with no Session
    Given the user logs out
    Then the user grabs all entitlements matching qt current with qf status with follow redirect is "false"
    And  redirect to authenticate page
