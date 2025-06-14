Feature: create multiple creativesPercampaigns tags, update global creativesPercampaigns limit, update seller_attribute creativesPercampaigns limit

  @restoreCrudCoreDatabaseBefore
  Scenario: update global config creative limit to 3
    When the user updates limit of property "seller.creatives.per.campaign.limit" global config to "3"

  Scenario: update global config creative limit to 2
    When the user updates limit of property "seller.creatives.per.campaign.limit" global config to "2"

  Scenario: update global config creative limit to 10
    When the user updates limit of property "seller.creatives.per.campaign.limit" global config to "10"
  # Scenario: update seller_attribute creative limit
   # When the user updates seller "adserverSellerTest8" creative per campaign limit to "3"
  Scenario: update Seller company with site limit equals to 3
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/creative/UpdateSellerCampaignCreativeLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/creative/UpdateSellerCampaignCreativeLimit_ER.json"

  Scenario: update Seller company from pss and verify the site limit equals to 3 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/creative/UpdateSellerFromPssCampaignCreativeLimit_payload.json"
    Then request passed successfully
    And the creative campaign limit field value for seller adserverSellerTest8 is preserved in the db

  #limit_enabled = false
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config creative limit to 1
    When the user updates limit of property "seller.creatives.per.campaign.limit" global config to "1"
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

  # global limit is 1
  #nexageAdmin
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config site limit to 1
    Given the user updates limit of property "seller.creatives.per.campaign.limit" global config to "1"
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
