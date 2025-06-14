Feature: create multiple campaigns tags, update global campaigns limit, update seller_attribute campaigns limit

  Scenario: update global config campaign limit to 11
    Given the user updates limit of property "seller.campaigns.limit" global config to "11"

  #Scenario: update seller_attribute campaign limit for seller adserverTestSeller8
  #Given the user updates seller "adserverSellerTest8" campaign limit to "12"
  Scenario: update Seller company with site limit equals to 12
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/rateLimit/payload/campaign/UpdateSellerCampaignLimit_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/rateLimit/payload/campaign/UpdateSellerCampaignLimit_ER.json"

  Scenario: update Seller company from pss and verify the site limit equals to 12 didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user updates a publisher from the json file "jsons/genevacrud/rateLimit/payload/campaign/UpdateSellerFromPssCampaignLimit_payload.json"
    Then request passed successfully
    And the campaign limit field value for seller adserverSellerTest8 is preserved in the db

  #limit_enabled = false
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config campaign limit to 1
    Given the user updates limit of property "seller.campaigns.limit" global config to "1"

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

  # add scenario that we can still add campaigns for other publishers
  # add advertiser for TagArchiveAdmin login, to be able to create a campaign
  #nexageAdmin
  @restoreCrudCoreDatabaseBefore
  Scenario: update global config campaign limit to 1
    Given the user updates limit of property "seller.campaigns.limit" global config to "1"

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
