Feature: create multiple sites, update global site limit, update seller_attribute site limit

  Scenario: update global config site limit to 10
    Given the user updates limit of property "seller.sites.limit" global config to "10"

  Scenario: check site limit api
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the PSS user selects the site "CRUDPosition_Site1"
    When the PSS user checks site limit and remaining item is "4"

  Scenario Outline: create new site
    # there are already 6 sites existing for this seller - all sites should be created as 1 site is inactive
    # 21 is site limit (15 active, 1 inactive)
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the PSS user creates multiple sites from the json file "jsons/genevacrud/rateLimit/payload/site/Site_pss_payload.json" and set name in "<names>" and set status "<status>"
    Then request passed successfully with code "201"
    And site "<names>" can be searched in the database

    Examples:
      | names          | status   |
      | siteRateLimit1 | ACTIVE   |
      | siteRateLimit2 | INACTIVE |
      | siteRateLimit3 | ACTIVE   |
      | siteRateLimit4 | ACTIVE   |

  Scenario: create additional site exceeding site limit
    # this should not be created as it exceeds site limit
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the PSS user creates a site from the json file "jsons/genevacrud/rateLimit/payload/site/Site6_payload.json"
    Then "site creation" failed with "400" response code and error message "Limit reached for this seller"
    And site "siteRateLimit6" cannot be searched in the database

  Scenario: create additional site exceeding site limit for crudPositionManager
    # this should not be created as it exceeds site limit
    Given the user "crudPositionManager" has logged in with role "ManagerSeller"
    When the PSS user creates a site from the json file "jsons/genevacrud/rateLimit/payload/site/Site6_payload.json"
    Then "site creation" failed with "400" response code and error message "Limit reached for this seller"
    And site "siteRateLimit6" cannot be searched in the database

  Scenario: update site can still be done
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    And the Seller sites for PSS user are retrieved
    And the PSS user selects the site "CRUDPosition_Site1"
    And the PSS user gets site update info from the json file "jsons/genevacrud/rateLimit/payload/site/UpdateSite_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/rateLimit/payload/site/UpdateSite_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/rateLimit/payload/site/UpdateSite_ER.json"

  # Scenario: update seller_attribute site limit for seller CRUDPositionTest
  #   Given the user updates seller "CRUDPositionTest" site limit to "11"

  Scenario: update Seller company with site limit equals to 11
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/site/UpdateSellerSiteLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/site/UpdateSellerSiteLimit_ER.json"

  Scenario: update Seller company from pss and verify the site limit equals to 11 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/site/UpdateSellerFromPssSiteLimit_payload.json"
    Then request passed successfully
    And the site limit field value is preserved in the db

  Scenario: recreate additional site
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    #  additional site should now be created successfully
    When the PSS user creates a site from the json file "jsons/genevacrud/rateLimit/payload/site/Site6_payload.json"
    Then request passed successfully with code "201"
    And site "siteRateLimit6" can be searched in the database

  #limit_enabled = false
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config site limit to 1
    Given the user updates limit of property "seller.sites.limit" global config to "1"

  # And the user updates seller "CRUDPositionTest" limit_enabled equals "0"

  Scenario: update Seller company with limit enabled flag to false (0)
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/UpdateSellerLimitEnabledFlag_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/UpdateSellerLimitEnabledFlag_ER.json"

  Scenario: update Seller company from pss and verify the limit enabled flag false did not changed
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/UpdateSellerFromPssLimitEnabledFlag_payload.json"
    Then request passed successfully
    And the limit enabled flag value is preserved in the db

  Scenario Outline: create new site for seller CRUDPositionTest
    # there are already 6 sites existing for this seller - all sites should be created
    # 1 is site limit (2 active, 1 inactive)
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the PSS user creates multiple sites from the json file "jsons/genevacrud/rateLimit/payload/site/Site_pss_payload.json" and set name in "<names>" and set status "<status>"
    Then request passed successfully with code "201"
    And site "<names>" can be searched in the database

    Examples:
      | names          | status   |
      | siteRateLimit1 | ACTIVE   |
      | siteRateLimit2 | INACTIVE |
      | siteRateLimit3 | ACTIVE   |

  Scenario: create new site for seller adserverSellerTest8
    # there are already 2 sites existing for this seller - 1 active additional site should NOT be created since site limit still applies for this seller
    # 1 is site limit
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates a site from the json file "jsons/genevacrud/rateLimit/payload/site/Site_seller105_payload.json"
    Then "site creation" failed with "400" response code and error message "Limit reached for this seller"
    And site "siteRateLimit1_seller105" cannot be searched in the database

  # nexageAdmin
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config site limit to 1
    Given the user updates limit of property "seller.sites.limit" global config to "1"

  #And the user updates seller "CRUDPositionTest" limit_enabled equals "0"

  Scenario: update Seller company with limit enabled flag to false (0)
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/UpdateSellerLimitEnabledFlag_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/UpdateSellerLimitEnabledFlag_ER.json"

  Scenario: update Seller company from pss and verify the limit enabled flag false did not changed
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/UpdateSellerFromPssLimitEnabledFlag_payload.json"
    Then request passed successfully
    And the limit enabled flag value is preserved in the db
