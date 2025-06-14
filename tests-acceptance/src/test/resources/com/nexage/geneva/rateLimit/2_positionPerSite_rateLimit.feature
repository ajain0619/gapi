Feature: create multiple positions, update position global config, update seller_attribute positions

  @restoreCrudCoreDatabaseBefore
  Scenario: update global config position limit to 10
    Given the user updates limit of property "seller.positions.per.site.limit" global config to "10"

  Scenario Outline: create new positions
    # there are already 6 existing for placement for this site - all sites should be created
    # placement per site limit is 10
    #http://geneva.sbx:8080/geneva/pss/10201/site/10000174/position/100247
    #POST http://geneva.sbx:8080/geneva/pss/10201/site/10000174/position?
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user selects the site "CRUDPosition_Site1"
    And the PSS user creates multiple positions from the json file "jsons/genevacrud/rateLimit/payload/position/Position_payload.json" and set name in "<names>"
    Then request passed successfully with code "201"
    And position "<names>" can be searched in the database

    Examples:
      | names                     |
      | Position_new_banner       |
      | Position1_inactive_banner |
      | Position2_interstitial    |

  Scenario: create new position as SellerManager for the same site exceeding position limit
    # there are already 10 sites existing for placement for this site - this site should not be created
    # placement per site limit is 10
    Given the user "crudPositionManager" has logged in with role "ManagerSeller"
    When the user selects the site "CRUDPosition_Site1"
    And the PSS user creates multiple positions from the json file "jsons/genevacrud/rateLimit/payload/position/Position_payload.json" and set name in "Position5Manager_new_banner2"
    Then "position creation" failed with "400" response code and error message "Limit reached for this seller"
    And position "Position5Manager_new_banner2" cannot be searched in the database

  Scenario: create additional position as SellerAdmin for the same site exceeding position limit
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user selects the site "CRUDPosition_Site1"
    And the PSS user creates position from the json file "jsons/genevacrud/rateLimit/payload/position/Position6_payload.json"
    Then "position creation" failed with "400" response code and error message "Limit reached for this seller"
    And position "Position6_instream_video" cannot be searched in the database

  Scenario: update position should be successful even it already reached its position limit for its site
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site1"
    And position with name "banner_placement" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/rateLimit/payload/position/UpdateBannerPlacement_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/rateLimit/payload/position/UpdateBannerPlacement_ER.json"
    And returned pss position can be searched out in database
    And pss position data in database is correct

  Scenario: clone position - this should not be cloned successfully since the site already reached its position limit
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site1"
    Given position with name "banner_placement" is selected
    When the PSS user clones position to target site "10000174" from the json file "jsons/genevacrud/rateLimit/payload/position/bannerPositionClone_payload.json"
    Then "position creation" failed with "400" response code and error message "Limit reached for this seller"


 # Scenario: update seller_attribute placement limit for seller CRUDPositionTest, CRUDPosition_Site1
 #   Given the user updates seller "CRUDPositionTest" position limit to "11"

  Scenario: update Seller company with position limit equals to 11
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/position/UpdateSellerPositionLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/position/UpdateSellerPositionLimit_ER.json"

  Scenario: update Seller company from pss and verify the position limit equals to 11 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/position/UpdateSellerFromPssPositionLimit_payload.json"
    Then request passed successfully
    And the position limit field value is preserved in the db

  Scenario: recreate additional position
    # additional position should now be created successfully
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user selects the site "CRUDPosition_Site1"
    And the PSS user creates position from the json file "jsons/genevacrud/rateLimit/payload/position/Position6_payload.json"
    Then request passed successfully with code "201"
    And position "Position6_instream_video" can be searched in the database

  Scenario: create additional position for a different site
    # additional position should be created successfully since this site has not reached yet its position limit
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user selects the site "CRUDPosition_Site5_VersionTest"
    And the PSS user creates position from the json file "jsons/genevacrud/rateLimit/payload/position/NewSitePosition_payload.json"
    Then request passed successfully with code "201"
    And position "NewSite_Position5_instream_video" can be searched in the database

  #limit_enabled = false
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config position limit to 1
    Given the user updates limit of property "seller.positions.per.site.limit" global config to "1"

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

  Scenario Outline: create new positions for seller CRUDPositionTest and site CRUDPosition_Site1
    # there are already 6 sites existing for placement for this site, create 1 active and 1 inactive - all sites should be created
    # placement per site limit is 1
    #http://geneva.sbx:8080/geneva/pss/10201/site/10000174/position/100247
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user selects the site "CRUDPosition_Site1"
    And the PSS user creates multiple positions from the json file "jsons/genevacrud/rateLimit/payload/position/Position_payload.json" and set name in "<names>"
    Then request passed successfully with code "201"
    And position "<names>" can be searched in the database

    Examples:
      | names                     |
      | Position_new_banner       |
      | Position1_inactive_banner |
      | Position2_interstitial    |

  Scenario: create new positions for seller adserverSellerTest8 and site AS8A
    # there are already 8 sites existing for placement for this site, create 1 active - this position should NOT be created since limit still applies for seller 105
    # placement per site limit is 1 - change to use the error message saying the limit already exceeded
    #http://geneva.sbx:8080/geneva/pss/10201/site/10000174/position/100247
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user selects the site "AS8A"
    And the PSS user creates position from the json file "jsons/genevacrud/rateLimit/payload/position/Position_payload_seller105.json"
    Then "position creation" failed with "400" response code and error message "Limit reached for this seller"
    And position "Position_new_banner_seller105" cannot be searched in the database

  #nexageAdmin
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config site limit to 1
    Given the user updates limit of property "seller.positions.per.site.limit" global config to "1"

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

  Scenario Outline: create new positions for seller CrudPositionTest
    # there are already 6 sites existing for placement for this site - all sites should be created
    # placement per site limit is 10
    #http://geneva.sbx:8080/geneva/pss/10201/site/10000174/position/100247
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site1"
    When the user creates multiple positions from the json file "jsons/genevacrud/rateLimit/payload/position/Position_nexage_payload.json" and set name in "<names>" and set status in "<status>"
    Then request passed successfully
    And position "<names>" can be searched in the database

    Examples:
      | user              | role          | names                            | status   |
      | crudnexageadmin   | AdminNexage   | Position_new_banner              | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position1_inactive_banner        | INACTIVE |
      | crudnexageadmin   | AdminNexage   | Position2_interstitial           | ACTIVE   |
      | crudnexagemanager | ManagerNexage | PositionManager_new_banner       | ACTIVE   |
      | crudnexagemanager | ManagerNexage | Position1Manager_inactive_banner | INACTIVE |
      | crudnexagemanager | ManagerNexage | Position2Manager_interstitial    | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position3User_medium_rectangle   | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position4User_native             | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position5User_new_banner2        | ACTIVE   |

  Scenario Outline: create new positions for seller adserverSellerTest8
    # there are already 6 sites existing for placement for this site - all sites should be created
    # placement per site limit is 10
    #http://geneva.sbx:8080/geneva/pss/10201/site/10000174/position/100247
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    When the user creates multiple positions from the json file "jsons/genevacrud/rateLimit/payload/position/Position105_nexage_payload.json" and set name in "<names>" and set status in "<status>"
    Then request passed successfully
    And position "<names>" can be searched in the database

    Examples:
      | user              | role          | names                               | status   |
      | crudnexageadmin   | AdminNexage   | Position105_new_banner              | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position1105_inactive_banner        | INACTIVE |
      | crudnexageadmin   | AdminNexage   | Position2105_interstitial           | ACTIVE   |
      | crudnexagemanager | ManagerNexage | PositionManager105_new_banner       | ACTIVE   |
      | crudnexagemanager | ManagerNexage | Position1Manager105_inactive_banner | INACTIVE |
      | crudnexagemanager | ManagerNexage | Position2Manager105_interstitial    | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position3User105_medium_rectangle   | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position4User105_native             | ACTIVE   |
      | crudnexageadmin   | AdminNexage   | Position5User105_new_banner2        | ACTIVE   |
