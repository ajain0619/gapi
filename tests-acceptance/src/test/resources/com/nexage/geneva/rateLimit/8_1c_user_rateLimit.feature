@sso
Feature: create multiple sso users tags, update global users limit, update seller_attribute users limit

  @restoreCrudCoreDatabaseBefore
  Scenario: update global config user limit to 2
    When the user updates limit of property "seller.users.limit" global config to "2"

  Scenario Outline: create new users
    # 1 existing user for publisher 105
    # 2 is the user limit - all users should be created
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_payload.json" with name in "<names>" and email in "<email>" and role in "<roles>" and set status in "<status>"
    Then request passed successfully with code "201"
    And users "<names>" can be searched in the database

    Examples:
      | names                   | email             | roles        | status |
      | rateLimitSellerManager2 | 01tewagzsd@qwe.ru | ROLE_MANAGER | false  |
      | rateLimitSelleradmin1   | 01gfxdhxdfh@q.ru  | ROLE_ADMIN   | true   |


  Scenario: additional user to exceed the limit
    # this should not be seen in the database - still not counting the logged in user
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_exceeded_payload.json" with name in "rateLimitSellermanager4" and email in "01jdffgd@rea.ru" and role in "ROLE_USER" and set status in "true"
    Then "user creation" failed with "400" response code and error message "Limit reached for this seller"
    And users "rateLimitSellermanager4" cannot be searched in the database

  Scenario: update Seller company with user limit equals to 4
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/user/UpdateSeller10201UserLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/user/UpdateSeller10201UserLimit_ER.json"

  Scenario: create new user
    # 2 existing users for publisher 10201
    # 3 is the user limit for this seller - all users should be created since limit not yet reached for this seller
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_10201_payload.json" with name in "rateLimitSellerUser3" and email in "01dfgfsd@ew.ri" and role in "ROLE_USER" and set status in "true"
    Then request passed successfully with code "201"
    And users "rateLimitSellerUser3" can be searched in the database

  #Scenario: update seller_attribute user limit for seller adserverSellerTest8
  #  When the user updates seller "adserverSellerTest8" users limit to "3"
  Scenario: update Seller company with site limit equals to 3
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/user/UpdateSellerUserLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/user/UpdateSellerUserLimit_ER.json"

  Scenario: update Seller company from pss and verify the site limit equals to 4 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/user/UpdateSellerFromPssUserLimit_payload.json"
    Then request passed successfully
    And the user limit field value for seller adserverSellerTest8 is preserved in the db

  Scenario: recreate additional user to exceed the limit
    # this should now be seen in the database
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_exceeded_payload.json" with name in "rateLimitSellermanager4" and email in "01jdffgd@rea.ru" and role in "ROLE_USER" and set status in "true"
    Then request passed successfully with code "201"
    And users "rateLimitSellermanager4" can be searched in the database

  #limit_enabled = false
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config user limit to 1
    When the user updates limit of property "seller.users.limit" global config to "1"

  #And the user updates seller "adserverSellerTest8" limit_enabled equals "0"
  Scenario: update Seller company with limit enabled flag to false (0)
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_ER.json"

  Scenario: update Seller company from pss and verify the limit enabled flag false did not changed
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105FromPssLimitEnabledFlag_payload.json"
    Then request passed successfully
    And the limit enabled flag value is preserved in the db

  Scenario Outline: create new users
    # no existing user for publisher 105
    # 1 is the user limit - all users should be created since limit_enabled for this seller is false
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_payload.json" with name in "<names>" and email in "<email>" and role in "<roles>" and set status in "<status>"
    Then request passed successfully with code "201"
    And users "<names>" can be searched in the database

    Examples:
      | names                  | email            | roles      | status |
      | rateLimitUser10        | 01fdsfhd@gfdg.ru | ROLE_ADMIN | true   |
      | rateLimitSelleradmin11 | 01qweqwe@eraw.ru | ROLE_ADMIN | true   |

  Scenario: create new user
    # 2 existing users for publisher 10201
    # 1 is the user limit - all users should be created since limit_enabled for this seller is false
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_10201_payload.json" with name in "rateLimitSellerUser3" and email in "01dfgfsd@ew.ri" and role in "ROLE_USER" and set status in "true"
    Then "user creation" failed with "400" response code and error message "Limit reached for this seller"
    And users "rateLimitSellerUser3" cannot be searched in the database

  #nexageAdmin
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config user limit to 1
    When the user updates limit of property "seller.users.limit" global config to "1"

  #And the user updates seller "adserverSellerTest8" limit_enabled equals "0"
  Scenario: update Seller company with limit enabled flag to false (0)
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_ER.json"

  Scenario: update Seller company from pss and verify the limit enabled flag false did not changed
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105FromPssLimitEnabledFlag_payload.json"
    Then request passed successfully
    And the limit enabled flag value is preserved in the db

  Scenario Outline: create new users
    # 1 existing user for publisher 105
    # 1 is the user limit - all 4 users should be created
    Given the user "<user>" has logged in with role "<role>"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_payload.json" with name in "<names>" and email in "<email>" and role in "<roles>" and set status in "<status>"
    Then request passed successfully with code "201"
    And users "<names>" can be searched in the database

    Examples:
      | user              | role          | names          | email                   | roles        | status |
      | crudnexageadmin   | AdminNexage   | NexageAdmin1   | 01nexageadmin1@ex.com   | ROLE_ADMIN   | true   |
      | crudnexageadmin   | AdminNexage   | NexageAdmin2   | 01sdfhd@q.tt            | ROLE_ADMIN   | true   |
      | crudnexagemanager | ManagerNexage | NexageManager1 | 01nexagemanager1@ex.com | ROLE_MANAGER | true   |
      | crudnexagemanager | ManagerNexage | NexageManager2 | 01dfhs@rqe.fg           | ROLE_MANAGER | true   |

  Scenario Outline: create new users for a different seller
    # 1 existing user for publisher 10201
    # 1 is the user limit - all 4 users should be created
    Given the user "<user>" has logged in with role "<role>"
    When the user creates multiple 1c users from the json file "jsons/genevacrud/rateLimit/payload/user/User_10201_payload.json" with name in "<names>" and email in "<email>" and role in "<roles>" and set status in "<status>"
    Then request passed successfully with code "201"
    And users "<names>" can be searched in the database

    Examples:
      | user              | role          | names               | email                | roles        | status |
      | crudnexageadmin   | AdminNexage   | NexageAdmin110201   | 01nexageuser1@ex.com | ROLE_ADMIN   | true   |
      | crudnexageadmin   | AdminNexage   | NexageAdmin210201   | 01sdf@qw.yy          | ROLE_ADMIN   | true   |
      | crudnexagemanager | ManagerNexage | NexageManager110201 | 01hdfsfsa@qw.ru      | ROLE_MANAGER | true   |
      | crudnexagemanager | ManagerNexage | NexageManager210201 | 01jgdfjdf@qew.ru     | ROLE_MANAGER | true   |
