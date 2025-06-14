Feature: create multiple blockLibraries, update global blockLibraries limit, update seller_attribute blockLibraries limit

  @restoreCrudCoreDatabaseBefore
  Scenario: update global config block library limit to 4
    When the user updates limit of property "seller.block.libraries.limit" global config to "4"

  Scenario: check block library limit api
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user checks block library limit and remaining item is "3"

  Scenario: create default eligible RTB Profile Library
    # 2 existing block groups for publisher 105
    # no column to determine status, so just create 1 group - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_DefaultEligible_payload.json" with name in "BlockDefaultEligibleGroup" and privilege level in "PUBLISHER"
    Then request passed successfully
    And blockgroup "BlockDefaultEligibleGroup" can be searched in the database

  Scenario: create exchange default RTB Profile Library
    # 2 existing block groups for publisher 105
    # no column to determine status, so just create 1 group - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_ExchangeDefault_payload.json" with name in "BlockExchangeDefaultGroup" and privilege level in "PUBLISHER"
    Then request passed successfully
    And blockgroup "BlockExchangeDefaultGroup" can be searched in the database

  Scenario Outline: create multiple RTB Profile Libraries
    # 2 existing block groups for publisher 105
    # no column to determine status, so just create 2 groups - all should be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_payload.json" with name in "<names>" and privilege level in "<privilege>"
    Then request passed successfully
    And blockgroup "<names>" can be searched in the database

    Examples:
      | names                | privilege   |
      | blockgroupGlobal     | GLOBAL      |
      | blockgroupNexageOnly | NEXAGE_ONLY |
      | blockGroupPublisher1 | PUBLISHER   |
      | blockGroupPublisher2 | PUBLISHER   |

  Scenario: clone RTB Profile Library - 1 cloned group
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user clones RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/Clone_payload.json"
    Then request passed successfully
    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/blockgroup/CloneResult.json"

  Scenario: check block library limit api
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user checks block library limit and remaining item is "0"

  Scenario: create additional RTB Profile Library
    # this should not be created
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_exceeded_payload.json" with name in "rateLimitBlockGroupExceeded" and privilege level in "PUBLISHER"
    Then "block group creation" failed with "400" response code and error message "Limit reached for this seller"
    And blockgroup "rateLimitBlockGroupExceeded" cannot be searched in the database

  Scenario: clone RTB Profile Library - should NOT be created
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user clones RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/Clone_again_payload.json"
    Then "block group creation" failed with "400" response code and error message "Limit reached for this seller"
    And blockgroup "testBlockCloneLimitExceed" cannot be searched in the database

  Scenario Outline: create multiple RTB Profile Libraries
    # 4 existing block groups for publisher 105
    # all should NOT be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_payload.json" with name in "<names>" and privilege level in "<privilege>"
    Then "block group creation" failed with "400" response code and error message "Limit reached for this seller"
    And blockgroup "<names>" cannot be searched in the database

    Examples:
      | names                     | privilege   |
      | blockgroupGlobalAGAIN     | GLOBAL      |
      | blockgroupNexageOnlyAGAIN | NEXAGE_ONLY |

  Scenario: create default eligible RTB Profile Library
    # 4 existing block groups for publisher 105
    # all should NOT be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_DefaultEligible_payload.json" with name in "BlockDefaultEligibleGroupAGAIN" and privilege level in "PUBLISHER"
    Then "block group creation" failed with "400" response code and error message "Limit reached for this seller"
    And blockgroup "BlockDefaultEligibleGroupAGAIN" cannot be searched in the database

  Scenario: create exchange default RTB Profile Library
    # 4 existing block groups for publisher 105
    # all should NOT be created (limit is 4)
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_ExchangeDefault_payload.json" with name in "BlockExchangeDefaultGroupAGAIN" and privilege level in "PUBLISHER"
    Then "block group creation" failed with "400" response code and error message "Limit reached for this seller"
    And blockgroup "BlockExchangeDefaultGroupAGAIN" cannot be searched in the database

  # Scenario: update RTB Profile Library - this should still update successfully even the seller reached its limit for blockgroups
  #   Given the user "pssSellerAdmin" has logged in
  #   When the PSS user searches RTB Profile Library "BGR1"
  #   And the PSS user updates the RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/RemoveGroup_payload.json"
  #   Then request passed successfully
  #   And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/blockgroup/RemoveGroup_ER.json"
  Scenario: create new RTB Profile Libraries for seller defaultBidder - this bidder group should be created successfully even adserverSellerTest8 has already reached its limit
    # 1 existing block group for publisher 10218 - defaultBidder
    # no column to determine status, so just create 1 group - all should be created (limit is 8)
    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup10218_payload.json" with name in "blockgroup110218" and privilege level in "PUBLISHER"
    Then request passed successfully
    And blockgroup "blockgroup110218" can be searched in the database

  #Scenario: update seller_attribute block group limit
  #  When the user updates seller "adserverSellerTest8" blockGroups limit to "5"
  Scenario: update Seller company with block library limit equals to 5
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/UpdateSellerBlockGroupLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/blockgroup/UpdateSellerBlockGroupLimit_ER.json"

  Scenario: update Seller company from pss and verify the site limit equals to 5 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/UpdateSellerFromPssBlockGroupLimit_payload.json"
    Then request passed successfully
    And the blockgroup limit field value for seller adserverSellerTest8 is preserved in the db

  Scenario: recreate additional RTB Profile Library
    # this should not be created - update with the correct error message
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_exceeded_payload.json" with name in "rateLimitBlockGroupExceeded" and privilege level in "PUBLISHER"
    Then request passed successfully
    And blockgroup "rateLimitBlockGroupExceeded" can be searched in the database
#
#  #limit_enabled = false
#  @restoreCrudCoreDatabaseBefore
#  Scenario: update global config block library limit to 1
#    When the user updates limit of property "seller.block.libraries.limit" global config to "1"
#
#  #And the user updates seller "adserverSellerTest8" limit_enabled equals "0"
#  Scenario: update Seller company with limit enabled flag to false (0)
#    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
#    And the user selects the "Seller" company "adserverSellerTest8"
#    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_payload.json"
#    Then request passed successfully
#    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_ER.json"
#
#  Scenario: update Seller company from pss and verify the limit enabled flag false did not changed
#    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
#    And the user selects the "Seller" company "adserverSellerTest8"
#    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105FromPssLimitEnabledFlag_payload.json"
#    Then request passed successfully
#    And the limit enabled flag value is preserved in the db
#
#  Scenario: create multiple RTB Profile Libraries
#    # 1 existing block for publisher 105
#    # no column to determine status, so just create 2 groups - all should be created (limit is 1)
#    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
#    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup_payload.json" with name in "BlockGroupRateLimit" and privilege level in "PUBLISHER"
#    Then request passed successfully
#    And blockgroup "BlockGroupRateLimit" can be searched in the database
#
#  #add scenario that there is still blockgroup limit for another seller defaultBidder
#  Scenario: create new RTB Profile Libraries for seller defaultBidder - this bidder group should be created successfully even adserverSellerTest8 has already reached its limit
#    # 1 existing block group for publisher 10218 - defaultBidder
#    # no column to determine status, so just create 1 group - should NOT be created
#    Given the user "defaultBidder@aol.com" has logged in with role "AdminSeller"
#    When the PSS user creates block RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/BlockGroup10218_payload.json" with name in "blockgroup110218" and privilege level in "PUBLISHER"
#    Then "biddergroup creation" failed with "400" response code and error message "Limit reached for this seller"
#    And blockgroup "blockgroup110218" cannot be searched in the database
#
#  #nexageAdmin
#  @restoreCrudCoreDatabaseBefore
#  Scenario: update global config block library limit to 1
#    When the user updates limit of property "seller.block.libraries.limit" global config to "1"
#
#  #And the user updates seller "adserverSellerTest8" limit_enabled equals "0"
#  Scenario: update Seller company with limit enabled flag to false (0)
#    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
#    And the user selects the "Seller" company "adserverSellerTest8"
#    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_payload.json"
#    Then request passed successfully
#    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105LimitEnabledFlag_ER.json"
#
#  Scenario: update Seller company from pss and verify the limit enabled flag false did not changed
#    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
#    And the user selects the "Seller" company "adserverSellerTest8"
#    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/UpdateSeller105FromPssLimitEnabledFlag_payload.json"
#    Then request passed successfully
#    And the limit enabled flag value is preserved in the db
#
#  Scenario Outline: create multiple RTB Profile Libraries
#    # 1 existing block for publisher 105
#    # no column to determine status, so just create 2 groups each nexage user - all should be created (limit is 1)
#    Given the user "<user>" has logged in with role "<role>"
#    When the user creates RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/<filename>_payload.json"
#    Then request passed successfully
#    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/blockgroup/<filename>_ER.json"
#
#    Examples:
#      | user              | role          | filename                                |
#      | crudnexageadmin   | AdminNexage   | BlockGroupNexageAdmin                   |
#      | crudnexageadmin   | AdminNexage   | BlockGroupNexageAdmin_DefaultEligible   |
#      | crudnexagemanager | ManagerNexage | BlockGroupNexageManager                 |
#      | crudnexagemanager | ManagerNexage | BlockGroupNexageManager_ExchangeDefault |
#      | crudnexageuser    | UserNexage    | BlockGroupNexageUser                    |
#
#  Scenario Outline: create multiple RTB Profile Libraries
#    # 1 existing block for publisher 10218
#    # no column to determine status, so just create 6 groups - all should be created (limit is 1)
#    Given the user "<user>" has logged in with role "<role>"
#    When the user creates RTB Profile Library from the json file "jsons/genevacrud/rateLimit/payload/blockgroup/<filename>_payload.json"
#    Then request passed successfully
#    And returned "RTB Profile Library" data matches the following json file "jsons/genevacrud/rateLimit/payload/blockgroup/<filename>_ER.json"
#
#    Examples:
#      | user              | role          | filename                                     |
#      | crudnexageadmin   | AdminNexage   | BlockGroupNexageAdmin10218                   |
#      | crudnexageadmin   | AdminNexage   | BlockGroupNexageAdmin10218_DefaultEligible   |
#      | crudnexagemanager | ManagerNexage | BlockGroupNexageManager10218                 |
#      | crudnexagemanager | ManagerNexage | BlockGroupNexageManager10218_ExchangeDefault |
#      | crudnexageuser    | UserNexage    | BlockGroupNexageUser10218                    |
