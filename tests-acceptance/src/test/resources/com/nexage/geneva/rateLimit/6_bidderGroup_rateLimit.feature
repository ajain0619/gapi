Feature: create multiple bidderLibraries, update global bidderLibraries limit, update seller_attribute bidderLibraries limit

  @restoreCrudCoreDatabaseBefore
  Scenario: update global config bidder library limit to 4
    When the user updates limit of property "seller.bidder.libraries.limit" global config to "4"

  Scenario: check bidder library limit api
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user checks bidder library limit and remaining item is "3"

  Scenario: create default eligible RTB Profile Library
    # 2 existing bidder groups for publisher 105
    # no column to determine status, so just create 1 group - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_DefaultEligible_payload.json" with name in "BidderDefaultEligibleGroup" and privilege level in "PUBLISHER"
    Then request passed successfully
    And biddergroup "BidderDefaultEligibleGroup" can be searched in the database

  Scenario: create default eligible RTB Profile Library
    # 2 existing bidder groups for publisher 105
    # no column to determine status, so just create 1 group - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_ExchangeDefault_payload.json" with name in "BidderExchangeDefaultGroup" and privilege level in "PUBLISHER"
    Then request passed successfully
    And biddergroup "BidderExchangeDefaultGroup" can be searched in the database

  Scenario Outline: create multiple RTB Profile Libraries
    # 2 existing bidder groups for publisher 105
    # no column to determine status, so just create 1 group - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_payload.json" with name in "<names>" and privilege level in "<privilege>"
    Then request passed successfully
    And biddergroup "<names>" can be searched in the database

    Examples:
      | names                 | privilege   |
      | biddergroupGlobal     | GLOBAL      |
      | biddergroupNexageOnly | NEXAGE_ONLY |
      | bidderGroupPublisher1 | PUBLISHER   |
      | bidderGroupPublisher2 | PUBLISHER   |

  Scenario: clone RTB Profile Library
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user clones RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/Clone_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/biddergroup/Clone_ER.json"

  Scenario: check bidder library limit api
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user checks bidder library limit and remaining item is "0"

  Scenario: create additional RTB Profile Library
    # this should not be created
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_exceed_payload.json" with name in "testBidderGroupLimitExceed" and privilege level in "PUBLISHER"
    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
    And biddergroup "testBidderGroupLimitExceed" cannot be searched in the database

  Scenario: clone RTB Profile Library - should NOT be created
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user clones RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/Clone_again_payload.json"
    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
    And biddergroup "testBidderCloneLimitExceed" cannot be searched in the database

  Scenario Outline: create multiple RTB Profile Libraries
    # 4 existing bidder groups for publisher 105
    # all should NOT be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_payload.json" with name in "<names>" and privilege level in "<privilege>"
    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
    And biddergroup "<names>" cannot be searched in the database

    Examples:
      | names                      | privilege   |
      | biddergroupGlobalAGAIN     | GLOBAL      |
      | biddergroupNexageOnlyAGAIN | NEXAGE_ONLY |

  Scenario: create default eligible RTB Profile Library
    # 2 existing bidder groups for publisher 105
    # no column to determine status, so just create 1 group - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_DefaultEligible_payload.json" with name in "BidderDefaultEligibleGroupAGAIN" and privilege level in "PUBLISHER"
    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
    And biddergroup "BidderDefaultEligibleGroupAGAIN" cannot be searched in the database

  Scenario: create default eligible RTB Profile Library
    # 2 existing bidder groups for publisher 105
    # all should NOT be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_ExchangeDefault_payload.json" with name in "BidderExchangeDefaultGroup" and privilege level in "BidderExchangeDefaultGroupAGAIN"
    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
    And biddergroup "BidderExchangeDefaultGroupAGAIN" cannot be searched in the database
  # Scenario: update RTB Profile Library
  # the new groups should not be created since the seller already reached its biddergroup limit
  #   Given the user "pssSellerAdmin" has logged in
  #   When the PSS user searches RTB Profile Library "BGN1"
  #   And the PSS user updates the RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/AddGroup_payload.json"
  #   Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
  #And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/biddergroup/AddGroup_ER.json"
  Scenario: create new RTB Profile Libraries for seller defaultBidder - this bidder group should be created successfully even adserverSellerTest8 has already reached its limit
    # 2 existing bidder groups for publisher 10218 - defaultBidder
    # no column to determine status, so just create 1 group - all should be created (limit is 5)
    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup10218_payload.json" with name in "testBidderGroupLimit10218" and privilege level in "PUBLISHER"
    Then request passed successfully
    And biddergroup "testBidderGroupLimit10218" can be searched in the database

  #Scenario: update seller_attribute bidder group limit
  #  Given the user updates seller "adserverSellerTest8" bidderGroups limit to "5"
  Scenario: update Seller company with bidder library limit equals to 5
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/UpdateSellerBidderGroupLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/biddergroup/UpdateSellerBidderGroupLimit_ER.json"

  Scenario: update Seller company from pss and verify the bidder library limit equals to 5 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/UpdateSellerFromPssBidderGroupLimit_payload.json"
    Then request passed successfully
    And the biddergroup limit field value for seller adserverSellerTest8 is preserved in the db

  Scenario: recreate additional RTB Profile Library
    # this should now be created successfully - this should be passed after fixing update above
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_exceed_payload.json" with name in "testBidderGroupLimitExceed" and privilege level in "PUBLISHER"
    Then request passed successfully
    And biddergroup "testBidderGroupLimitExceed" can be searched in the database

  #limit_enabled = false
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config bidder library limit to 1
    When the user updates limit of property "seller.bidder.libraries.limit" global config to "1"

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

  Scenario: create multiple RTB Profile Libraries
    # 1 existing bidder group for publisher 105
    # no column to determine status, so just create 2 groups - all should be created (limit is 1)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup_payload.json" with name in "testBidderGroupLimit1" and privilege level in "PUBLISHER"
    Then request passed successfully
    And biddergroup "testBidderGroupLimit1" can be searched in the database

  Scenario: create multiple RTB Profile Libraries - limit is still applicable for this seller
    # 2 existing bidder group for publisher 10218
    # no column to determine status, so just create 1 group - all should NOT be created (limit is 1)
    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"
    When the PSS user creates bidder RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/BidderGroup10218_payload.json" with name in "testBidderGroupLimit10218" and privilege level in "PUBLISHER"
    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
    And biddergroup "testBidderGroupLimit10218" cannot be searched in the database

  #nexageAdmin
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config bidder library limit to 5
    When the user updates limit of property "seller.bidder.libraries.limit" global config to "1"

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

  Scenario Outline: create multiple RTB Profile Libraries
    # 1 existing bidder group for publisher 105
    # no column to determine status, so just create 2 groups - all should be created (limit is 1)
    Given the user "<user>" has logged in with role "<role>"
    When the user creates RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/<filename>_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/biddergroup/<filename>_ER.json"

    Examples:
      | user              | role          | filename                                 |
      | crudnexageadmin   | AdminNexage   | BidderGroupNexageAdmin                   |
      | crudnexageadmin   | AdminNexage   | BidderGroupNexageAdmin_DefaultEligible   |
      | crudnexagemanager | ManagerNexage | BidderGroupNexageManager                 |
      | crudnexagemanager | ManagerNexage | BidderGroupNexageManager_ExchangeDefault |
      | crudnexageuser    | UserNexage    | BidderGroupNexageUser                    |

  Scenario Outline: create multiple RTB Profile Libraries
    # 2 existing bidder groups for publisher 10218 - defaultBidder
    # no column to determine status, so just create 2 groups - all should be created (limit is 1)
    Given the user "<user>" has logged in with role "<role>"
    When the user creates RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/biddergroup/<filename>_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/biddergroup/<filename>_ER.json"

    Examples:
      | user              | role          | filename                                      |
      | crudnexageadmin   | AdminNexage   | BidderGroupNexageAdmin10218                   |
      | crudnexageadmin   | AdminNexage   | BidderGroupNexageAdmin10218_DefaultEligible   |
      | crudnexagemanager | ManagerNexage | BidderGroupNexageManager10218                 |
      | crudnexagemanager | ManagerNexage | BidderGroupNexageManager10218_ExchangeDefault |
      | crudnexageuser    | UserNexage    | BidderGroupNexageUser10218                    |
