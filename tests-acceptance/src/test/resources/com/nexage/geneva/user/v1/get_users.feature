Feature: Get users for a company pid

  @restoreCrudCoreDatabaseBefore

  Scenario: Get all users for a given company pid
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user searches a company pid "companyPid" "0" "800"
    Then request passed successfully
    And returned "all users for an company pid" data matches the following json file "jsons/genevacrud/user/expected_results/GetAllUsersForCompanyPid_V1_ER.json"

  Scenario: Get all users for a given seller seat pid
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user searches a seller seat pid "sellerSeatPid" "1"
    Then request passed successfully
    And returned "all users for an seller pid" data matches the following json file "jsons/genevacrud/user/expected_results/GetUsersBySellerSeat_V1_ER.json"

  Scenario Outline: Get paginated list of users
    Given the user "<user>" has logged in with role "<role>"
    And the paginated list of users are retrieved
    Then request passed successfully
    And returned "paginated user list" data matches the following json file "jsons/genevacrud/user/expected_results/<filename>"

    Examples:
      | user        | role        | filename                               |
      | admin1c     | AdminNexage | GetPaginatedUsers_V1_ER.json           |
      | adminbuyer1 | AdminBuyer  | GetPaginatedBuyerAdminUsers_V1_ER.json |

  Scenario: Get users matching the search criteria
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the paginated list of users are retrieved based on the search criteria
    Then request passed successfully
    And returned "paginated user list" data matches the following json file "jsons/genevacrud/user/expected_results/GetPaginatedUsersSearchByUserName_V1_ER.json"

  Scenario: Get a user for a given user pid
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user searches a user pid "200"
    Then request passed successfully
    And returned "user for a user pid" data matches the following json file "jsons/genevacrud/user/expected_results/GetUserByPid_V1_ER.json"

  Scenario Outline: get current logged in user details
    Given the user "<user>" has logged in with role "<role>"
    When current user logged in details are retrieved
    Then request passed successfully
    And returned "logged user details" data matches the following json file "jsons/genevacrud/user/expected_results/<filename>"
    Examples:
      | user        | role        | filename                                |
      | admin1c     | AdminNexage | GetUserV1Details_ER.json                |
      | adminbuyer1 | AdminBuyer  | GetAdminBuyerCurrentUserDetails_ER.json |

  Scenario Outline: get current logged in user details with multiple search params
    Given the user "<user>" has logged in with role "<role>"
    When the current user details are retrieved based on the "<qf>" and "<qt>"
    Then request passed successfully
    And returned "result" data matches the following json file "jsons/genevacrud/user/expected_results/<filename>"
    Examples:
      ## NOTE: This test scenario will fail, if run locally, due to "version" property differences in expected data
      ## defined in JSON files and actual data returned from DB (master pipeline passes with current test data setup!).
      ## The cause of this is described in JIRA task: https://jira.ouryahoo.com/browse/SSP-25701
      | user                   | role               | qf                     | qt    | filename                                  |
      | crudnexageadmin        | AdminNexage        | onlyCurrent            | true  | GetCurrentUser_AdminNexage_ER.json        |
      | crudnexageadmin        | AdminNexage        | name,onlyCurrent,email | true  | GetCurrentUser_AdminNexage_ER.json        |
      | crudnexageadmin        | AdminNexage        | onlyCurrent            | false | GetPaginatedUsers_Nexage_ER.json          |
      | crudnexageadmin        | AdminNexage        |                        |       | GetPaginatedUsers_Nexage_ER.json          |
      | crudnexagemanageryield | ManagerYieldNexage | onlyCurrent            | true  | GetCurrentUser_ManagerYieldNexage_ER.json |
      | crudnexagemanageryield | ManagerYieldNexage |                        |       | GetPaginatedUsers_Nexage_ER.json          |
      | crudnexagemanager      | ManagerNexage      | onlyCurrent            | true  | GetCurrentUser_ManagerNexage_ER.json      |
      | crudnexagemanager      | ManagerNexage      |                        |       | GetPaginatedUsers_Nexage_ER.json          |
      | crudnexageuser         | UserNexage         | onlyCurrent            | true  | GetCurrentUser_UserNexage_ER.json         |
      | crudnexageuser         | UserNexage         |                        |       | GetPaginatedUsers_Nexage_ER.json          |
      | crudselleradmin        | AdminSeller        | onlyCurrent            | true  | GetCurrentUser_AdminSeller_ER.json        |
      | crudselleradmin        | AdminSeller        |                        |       | GetPaginatedUsers_AdminSeller_ER.json     |
      | crudsellermanager      | ManagerSeller      | onlyCurrent            | true  | GetCurrentUser_ManagerSeller_ER.json      |
      | crudsellermanager      | ManagerSeller      |                        |       | GetCurrentUser_ManagerSeller_ER.json      |
      | crudselleruser         | UserSeller         | onlyCurrent            | true  | GetCurrentUser_UserSeller_ER.json         |
      | crudselleruser         | UserSeller         |                        |       | GetCurrentUser_UserSeller_ER.json         |
      | crudbuyeradmin         | AdminBuyer         | onlyCurrent            | true  | GetCurrentUser_AdminBuyer_ER.json         |
      | crudbuyeradmin         | AdminBuyer         |                        |       | GetPaginatedUsers_AdminBuyer_ER.json      |
      | crudbuyermanager       | ManagerBuyer       | onlyCurrent            | true  | GetCurrentUser_ManagerBuyer_ER.json       |
      | crudbuyermanager       | ManagerBuyer       |                        |       | GetCurrentUser_ManagerBuyer_ER.json       |
      | crudbuyeruser          | UserBuyer          | onlyCurrent            | true  | GetCurrentUser_UserBuyer_ER.json          |
      | crudbuyeruser          | UserBuyer          |                        |       | GetCurrentUser_UserBuyer_ER.json          |
      | seatholderadmin        | AdminSeatHolder    | onlyCurrent            | true  | GetCurrentUser_AdminSeatHolder_ER.json    |
      | seatholderadmin        | AdminSeatHolder    |                        |       | GetPaginatedUsers_AdminSeatHolder_ER.json |
      | seatholdermanager      | ManagerSeatHolder  | onlyCurrent            | true  | GetCurrentUser_ManagerSeatHolder_ER.json  |
      | seatholdermanager      | ManagerSeatHolder  |                        |       | GetCurrentUser_ManagerSeatHolder_ER.json  |
      | seatholderuser         | UserSeatHolder     | onlyCurrent            | true  | GetCurrentUser_UserSeatHolder_ER.json     |
      | seatholderuser         | UserSeatHolder     |                        |       | GetCurrentUser_UserSeatHolder_ER.json     |

  Scenario: get user by name
    Given the user "admin1c" has logged in with role "AdminNexage"
    And users are retrieved
    When user "sellermanager1" is selected
    And user data is retrieved
    Then request passed successfully
    And returned "user" data matches the following json file "jsons/genevacrud/user/expected_results/UserByName_V1_ER.json"
