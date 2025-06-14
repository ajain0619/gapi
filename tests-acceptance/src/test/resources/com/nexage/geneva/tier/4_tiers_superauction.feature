Feature: create tiers of type sy_decision_maker, super_auction and waterfall as Seller Admin

  Background: log in as seller admin and select the site
    Given the user "user" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "MedFTCompany"
    And the user selects the site "SY_HB_EnabledSite"
    And position with name "SmartYieldPosition_SA" is selected

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
    Given a user create sy_decision_maker tier request input "jsons/genevacrud/tier/pss/payload/CreateSYDMTier_payload.json"
    When create payload contains valid tag
    Then "tier create" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"
############################################################################
# To test createTier API for creating Waterfall Tier using a BidEnabled and#
# DecisionMakerEnabled Tag without existing SY_Decision_Maker Tier         #
############################################################################
  Scenario: create tier of type waterfall for smart yield position
    Given a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTierBasic_payload.json"
    When create payload contains valid tag
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateWaterfallTierBasic_ER.json"
############################################################################
# To test updateTier API for updating an existing tier to                  #
# SY_Decision_Maker Tier                                                   #
# Result : updateTier API should not allow update to SY_Decision_Maker Tier#
############################################################################
  Scenario: update waterfall tier to type sy_decision_maker for smart yield position
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeToSYDM_payload.json"
    Then "tier update" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"
############################################################################
# To test createTier API for creating Super_Auction Tier using a           #
# BidEnabled Tag + DM disabled without requiring existing SY_DM Tier       #
############################################################################
#updated
  Scenario: create tier of type super_auction for smart yield position
    Given a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_payload.json"
    When create payload contains valid tag
    Then "tier update" failed with "400" response code and error message "Decision maker tier does not exists"
############################################################################
# To test createTier API for creating Super_Auction Tier using a non       #
# BidEnabled Tag without requiring existing SY_Decision_Maker Tier         #
############################################################################
  Scenario: create tier of type super_auction for smart yield position
    Given a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTierNonBidEnabled_payload.json"
    When create payload contains invalid tag
    Then "tier create" failed with "400" response code and error message "Decision maker tier does not exists"
############################################################################
# To test updateTier API for updating an existing tier to                  #
# Super_Auction Tier                                                       #
# Result : updateTier API should NOT allow update to Super_Auction Tier    #
# given that tag is bidEnabled
############################################################################
#this should NOT be successful - adsource has bid enabled + DM enabled but DM tier does not exist
  Scenario: update waterfall tier to type super_auction for smart yield position
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeToSuperAuction_payload.json"
    Then "tier update" failed with "400" response code and error message "Tier type can not be updated"
############################################################################
# To test createTier API for creating Waterfall Tier using a normal        #
# Tag without requiring existing SY_Decision_Maker Tier                    #
############################################################################
  Scenario: create tier of type waterfall for smart yield position, bid disabled and DM disabled
    Given a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTier_payload.json"
    When create payload contains valid tag
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateWaterfallTier_ER.json"
###################      End of MX-2597 Tests       ########################
