Feature: create tiers of type sy_decision_maker, super_auction and waterfall as Seller Admin

  Background: log in as seller admin and select the site
    Given the user "medft04@yahoo.com" has logged in with role "UserSeller"
    And the user selects the site "SY_HB_EnabledSite"

  #########################################################################################
# For a given SmartYieldPosition, these are tests for Super Auction feature to test     #
# create/update of following tierType:                                                  #
# WATERFALL                                                                             #
# SUPER_AUCTION                                                                         #
# SY_DECISION_MAKER                                                                     #
#########################################################################################
############################################################################
# To test createTier API for creating SY_Decision_Maker Tier using a       #
# DecisionMakerEnabled Tag                                                 #
# Result : createTier API should not create SY_Decision_Maker Tier         #
############################################################################
  Scenario: create tier of type sy_decision_maker for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user create sy_decision_maker tier request input "jsons/genevacrud/tier/pss/payload/CreateSYDMTier_payload.json"
    When create payload contains valid tag
    Then "tier create" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"

############################################################################
# To test createTier API for creating Waterfall Tier using a BidEnabled and#
# DecisionMakerEnabled Tag without existing SY_Decision_Maker Tier         #
############################################################################
  Scenario: create tier of type waterfall for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTierBasic_payload.json"
    When create payload contains valid tag
    Then "tier create" failed with "401" response code and error message "You're not authorized to perform this operation"

############################################################################
# To test createTier API for creating Super_Auction Tier using a           #
# BidEnabled Tag without requiring existing SY_Decision_Maker Tier         #
############################################################################
# this should have 401 response - check with Rasanga
  Scenario: create tier of type super_auction for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_payload.json"
    When create payload contains valid tag
    Then "tier create" failed with "401" response code and error message "You're not authorized to perform this operation"

############################################################################
# To test createTier API for creating Super_Auction Tier using a non       #
# BidEnabled Tag without requiring existing SY_Decision_Maker Tier         #
############################################################################
# this should have 401 response - check with Rasanga
  Scenario: create tier of type super_auction for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTierNonBidEnabled_payload.json"
    When create payload contains invalid tag
    Then "tier create" failed with "401" response code and error message "You're not authorized to perform this operation"

############################################################################
# To test createTier API for creating Waterfall Tier using a normal        #
# Tag without requiring existing SY_Decision_Maker Tier                    #
############################################################################
  Scenario: create tier of type waterfall for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTier_payload.json"
    When create payload contains valid tag
    Then "tier create" failed with "401" response code and error message "You're not authorized to perform this operation"

############################################################################
# To test updateTier API for updating an existing tier to                  #
# Super_Auction Tier                                                       #
# Result : updateTier API should not allow update to Super_Auction Tier
# given that tag is bidEnabled
############################################################################
  Scenario: update waterfall tier to type super_auction for smart yield position
    Given position with name "sy_enabled_position" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeToSuperAuction_UserRole_payload.json"
    Then "update create" failed with "400" response code and error message "Tier type can not be updated"

############################################################################
# To test updateTier API for updating an existing waterfall tier to        #
# SY_Decision_Maker Tier                                                   #
# Result : updateTier API should not allow update to SY_Decision_Maker Tier#
############################################################################
  Scenario: update waterfall tier to type sy_decision_maker for smart yield position
    Given position with name "sy_enabled_position" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeToSYDM_UserRole_payload.json"
    Then "tier update" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"
###################      End of MX-2597 Tests       ########################

##Create SY_Decision_Maker tier using decision maker enabled tag - no tiers yet for the position
  Scenario: create tier of type sy_decision_maker for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_payload.json"
    Then "tier create" failed with "401" response code and error message "You're not authorized to perform this operation"

## get - should be successful
  Scenario: get all tiers
    Given position with name "SA_posWith_DMSATier" is selected
    When the user gets all publisher tiers
    Then request passed successfully
    And returned "tiers" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/GetAllSATiers_ER.json"

  Scenario: get tier
    Given position with name "SA_posWith_DMSATier" is selected
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user gets the publisher tier
    Then request passed successfully
    And returned "tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/GetTierSA_ER.json"

  Scenario: get all DM tiers
    Given position with name "SA_posWith_DMSATier" is selected
    When the user gets all decision maker publisher tiers
    Then request passed successfully
    And returned "tiers" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/GetAllDMTiers_ER.json"
