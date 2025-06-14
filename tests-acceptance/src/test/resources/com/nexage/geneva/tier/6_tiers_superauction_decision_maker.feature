Feature: create tiers of type sy_decision_maker, super_auction and waterfall as Seller Admin

  Background: log in as seller admin and select the site
    Given the user "user" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "MedFTCompany"
    And the user selects the site "SY_HB_EnabledSite"

  ## get - should be successful
  Scenario: get all DM tiers
    Given position with name "SA_posWith_DMSATier" is selected
    When the user gets all decision maker publisher tiers
    Then request passed successfully
    And returned "tiers" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/GetAllDMTiers_ER.json"

# create SUPER_AUCTION tier when SY_Decision_Maker tier doesn't exist - bidEnabled + DM disabled
  Scenario: create tier of type super_auction for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_payload.json"
    When sy_decision_maker tier does not exists
    Then "tier create" failed with "400" response code and error message "Decision maker tier does not exists"

# add DM using adsource bid enabled + DM disabled
##Create SY_Decision_Maker tier bid enabled tag only
  Scenario: create tier of type sy_decision_maker for smart yield position - bid enabled + DM disabled
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_BidEnabledOnly_payload.json"
    Then "tier create" failed with "400" response code and error message "Adsource for the tag is not decision maker enabled"

# add DM using adsource bid disabled + DM disabled
##Create SY_Decision_Maker tier bid disabled and DM disabled
  Scenario: create tier of type sy_decision_maker for smart yield position - bid disabled + DM disabled
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_BidDMDisabled_payload.json"
    Then "tier create" failed with "400" response code and error message "Adsource for the tag is not decision maker enabled"

# create DM tier with 2 tags
##Create SY_Decision_Maker tier using decision maker enabled tag + bid enabled	and DM enabled only
# send 2 buyers in the payload
  Scenario: create tier of type sy_decision_maker for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_withTwoTags_payload.json"
    Then "tier create" failed with "400" response code

##Happy Path Decision Maker
##Create SY_Decision_Maker tier using decision maker enabled tag only + bid disabled (Happy Path) - no tiers yet for the position
  Scenario: create tier of type sy_decision_maker for smart yield position with DM enabled + bid disabled
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_DMEnabledOnly_payload.json"
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/NewCreateSYDMTier_DMEnabledOnly_ER.json"

  @restoreCrudCoreDatabaseBefore

##Happy Path Decision Maker
##Create SY_Decision_Maker tier using decision maker enabled tag (Happy Path) - no tiers yet for the position
  Scenario: create tier of type sy_decision_maker for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_payload.json"
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/NewCreateSYDMTier_ER.json"

##Create another SY_Decision_Maker tier when a decision maker tier already exists for a position
  Scenario: create second tier of type sy_decision_maker for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/SecondCreateSYDMTier_payload.json"
    Then "tier create" failed with "400" response code and error message "Decision maker tier already exists for the placement"

#create a SUPER_AUCTION tier when SY_Decision_Maker tier already exists for a position
##HAppy Path Super Auction Tier
  Scenario: create tier of type super_auction for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_payload.json"
    When sy_decision_maker tier already exists
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateSuperAuctionTier_ER.json"

## Happy Path Waterfall 1 - create WATERFALL tier when SY_Decision_Maker tier and Super Auction tier exists
  Scenario: create tier of type waterfall for smart yield position when SY_Decision_Maker tier and Super Auction tier exists
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTier_payload.json"
    When sy_decision_maker tier already exists
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateWaterfallTier_ER.json"

  @restoreCrudCoreDatabaseBefore

  #update waterfall to SA – bid and DM disabled so 400 response
  Scenario: update waterfall to SA – bid and DM disabled so 400 response
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And third publisher tier is retrieved
    When the user updates the publisher waterfall tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeWaterfallToSA_payload.json"
    Then "tier update" failed with "400" response code and error message "Tier type can not be updated"

 #update waterfall to DM – bid and DM disabled so 400 response
  Scenario: update waterfall to DM – bid and DM disabled so 400 response
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And third publisher tier is retrieved
    When the user updates the publisher waterfall tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeWaterfallToDM_payload.json"
    Then "tier update" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"

 #update SA to DM
  Scenario: update SA to DM
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And second publisher tier is retrieved
    When the user updates the publisher super auction tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeSAToDM_payload.json"
    Then "tier update" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"

 #update SA to Waterfall
  Scenario: update SA to Waterfall
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And second publisher tier is retrieved
    When the user updates the publisher super auction tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeSAToWaterfall_payload.json"
    Then "tier update" failed with "400" response code and error message "Tier type can not be updated"

 #update Waterfall to SA
  Scenario: update Waterfall to SA - with DM, SA and waterfall exists + bid enabled
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And fourth publisher tier is retrieved
    When the user updates the publisher second waterfall tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTierTypeWaterfallToSA_bidEnabled_payload.json"
    Then "tier update" failed with "400" response code and error message "Tier type can not be updated"

  ## Happy Path Waterfall 2 - create WATERFALL tier when SY_Decision_Maker tier doesn't exist(Restore db before this scenario)
  Scenario: create tier of type waterfall for smart yield position
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTier_payload.json"
    When sy_decision_maker tier does not exists
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateWaterfallTier_level0_ER.json"

##Create SY_Decision_Maker tier  - waterfall exists for the position
  Scenario: create tier of type sy_decision_maker for smart yield position - waterfall exists
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_payload.json"
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/NewCreateSYDMTier_ER.json"

#create a SUPER_AUCTION tier when SY_Decision_Maker and Waterfall tier already exists for a position
##HAppy Path Super Auction Tier - 2 tags
  Scenario: create tier of type super_auction for smart yield position with 2 tags
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_two_tags_payload.json"
    When sy_decision_maker tier already exists
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateSuperAuctionTier_two_tags_ER.json"

############################################################################
# To test updateTier API for updating an existing SY DM tier to            #
# Waterfall Tier                                                           #
# Result : updateTier API should not allow update to Waterfall Tier        #
############################################################################
  Scenario: update tier to type waterfall from decision maker for smart yield position - adsource used for DM has bid enabled and DM enabled
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateDMTierTypeToWaterfall_payload.json"
    Then "tier update" failed with "400" response code and error message "Tier type can not be updated"
############################################################################
# To test updateTier API for updating an existing SY DM tier to            #
# SuperAuction Tier                                                        #
# Result : updateTier API should not allow update to Super Auction Tier    #
############################################################################
  Scenario: update tier to type superauction from decision maker for smart yield position - adsource used for DM has bid enabled and DM enabled
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateDMTierTypeToSA_payload.json"
    Then "tier update" failed with "400" response code and error message "Tier type can not be updated"

############################################################################
# To test updateTier API for updating an existing SY DM tier to            #
# SuperAuction Tier                                                        #
# Result : updateTier API should not allow update to Super Auction Tier    #
############################################################################
  Scenario: update DM tier to level 1 tier for smart yield position - adsource used for DM has bid enabled and DM enabled
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateDMTierTypeToLevel1_payload.json"
    Then "tier update" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"

##Delete an existing SY_Decision_Maker tier - with SA existing
  Scenario: delete tier of type sy_decision_maker for smart yield position - SA tier exists
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user deletes the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/DeleteDMTierType_payload.json"
    Then "tier delete" failed with "400" response code and error message "Super auction tier can not exists without decision maker tier"
    And tier pid "10352" can be searched in the database

##Delete an existing SA tier
  Scenario: delete tier of type superauction for smart yield position
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And second publisher tier is retrieved
    When the user deletes the publisher super auction tier from the json file "jsons/genevacrud/tier/pss/payload/DeleteSATierType_payload.json"
    Then request passed with "204" response code
    And tier pid "10353" cannot be searched in the database

##Delete an existing SY_Decision_Maker tier - SA not existing
  Scenario: delete tier of type sy_decision_maker for smart yield position - SA tier deleted
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user deletes the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/DeleteDMTierType_payload.json"
    Then request passed with "204" response code
    And tier pid "10352" cannot be searched in the database

##Delete an existing Waterfall tier
  Scenario: delete tier of type sy_decision_maker for smart yield position - SA tier deleted
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user deletes the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/DeleteWaterfallTierType_payload.json"
    Then request passed with "204" response code
    And tier pid "10354" cannot be searched in the database

  @restoreCrudCoreDatabaseBefore

 ## create WATERFALL tier when SY_Decision_Maker tier does not exists
  Scenario: create WATERFALL tier when SY_Decision_Maker tier does not exists
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates waterfall tier using json file "jsons/genevacrud/tier/pss/payload/CreateWaterfallTier_payload.json"
    When sy_decision_maker tier does not exists
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateWaterfallTier_level0_ER.json"

 ##Create SY_Decision_Maker tier - waterfall tier already exists for the position
  Scenario: create tier of type sy_decision_maker for smart yield position with DM enabled + bid disabled - waterfall tier already exists for the position
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_DMEnabledOnly_payload.json"
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/NewCreateSYDMTier_DMEnabledOnly_ER.json"

#additional cases
  Scenario: updateTier to have tag with DM disabled
    Given position with name "SA_posWith_DMSATier" is selected
    And the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user gets all decision maker publisher tiers
    And the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateDMTierType_withTagDMDisabled_payload.json"
    Then "tier update" failed with "400" response code and error message "Decision Maker tier type not supported for tier create/update API"

  Scenario: updateDMtier with buyer using DM disabled
    Given position with name "SA_posWith_DMSATier" is selected
    When the user gets all decision maker publisher tiers
    And the user updates the decision maker publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTagWithDMDisabled_payload.json"
    Then "tier update" failed with "400" response code and error message "Adsource for the tag is not decision maker enabled"

  Scenario: updateDMtier with buyer using DM enabled
    Given position with name "SA_posWith_DMSATier" is selected
    When the user gets all decision maker publisher tiers
    When the user updates the decision maker publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateTagWithDMEnabled_payload.json"
    Then request passed successfully
    And returned "updated tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/UpdateTagWithDMEnabled_ER.json"

  @restoreCrudCoreDatabaseBefore

 #edge case
  Scenario: create tier of type sy_decision_maker for smart yield position with DM enabled + bid disabled - waterfall tier already exists for the position
    Given position with name "SmartYieldPosition_SA" is selected
    And create new sy_decision_maker tier request from the json file "jsons/genevacrud/tier/pss/payload/NewCreateSYDMTier_DMEnabledOnly_payload.json"
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/NewCreateSYDMTier_DMEnabledOnly_ER.json"

 # assigning 3 tags to SA tier with the same buyer ids
  Scenario: create tier of type super_auction for smart yield position with 2 tags
    Given position with name "SmartYieldPosition_SA" is selected
    And a user creates super_auction tier using json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_sameBuyerIds_payload.json"
    When sy_decision_maker tier already exists
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateSuperAuctionTier_sameBuyerIds_ER.json"
