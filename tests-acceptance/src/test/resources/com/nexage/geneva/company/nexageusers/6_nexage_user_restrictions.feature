Feature: Seller Sites: create, update should not be successful for Nexage Users and Sellers

  Background: user logs in
    Given the user "crudnexageuser" has logged in with role "UserNexage"

  Scenario: update site random fields as Nexage User
    Given the user selects the "Seller" company "cpi_seller"
    And the user selects the site "cpi_site"
    When the user updates site with data from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateSite313_payload.json"
    Then "site update" failed with "401" response code
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/company/payload/nexageuser/UpdateSite313_ER.json"

  Scenario: delete site as Nexage User
    Given the user selects the "Seller" company "Athens1"
    When the user deletes site "athens1site2"
    Then "site deletion" failed with "401" response code
    And site "athens1site2" can be searched in the database

  Scenario: create position with different parameters as Nexage User
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    When the user creates position first call from the json file "jsons/genevacrud/company/payload/nexageuser/CreateBanner_payload.json"
    Then "position creation" failed with "401" response code
    And position "nexageUserNewPlacement" cannot be searched in the database

  Scenario: update position with different parameters as Nexage User
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And position with name "custom1a" is selected
    When the user updates selected position first call from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateBannerToNative_payload.json"
    Then "position update" failed with "401" response code
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/company/payload/nexageuser/UpdateSite950_ER.json"

  Scenario: delete position as Nexage User
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site1"
    Given position with name "CRUDPosition_UpdateCases" is selected
    When the user deletes selected position
    Then "position deletion" failed with "401" response code
    And position "CRUDPosition_UpdateCases" can be searched in the database

  Scenario: create a new tag as Nexage User
    Given the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/create/NewNonExchangeTagRequiredFields_payload.json"
    Then "tag creation" failed with "401" response code
    And tag "iVdopia - NewNonExchangeTagRequiredFields" cannot be searched in the database

  Scenario Outline: delete tag as Nexage User
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site2"
    When the user deletes the "<tag type>" tag "<tag name>"
    Then "tag deletion" failed with "401" response code
    And tag "<tag name>" can be searched in the database

    Examples:
      | tag type     | tag name                                    |
      | non-exchange | Adstars - bannerposition2_nonExchange       |
      | exchange     | Nexage Exchange - banner_position1_excahnge |

  Scenario: create a Seller company as Nexage User
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/CreateAllFeatureEnabled_payload.json"
    Then "seller creation" failed with "401" response code
    And company "createName" cannot be searched out in database

  Scenario: update Seller company as Nexage User
    Given the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateTextBox_payload.json"
    Then "company update" failed with "401" response code
    And company "adserverSellerTest8update" cannot be searched out in database

  Scenario: delete a Seller company as Nexage User
    Given the user selects the "Seller" company "adserverSellerTest8"
    When the user deletes the selected company
    Then "company deletion" failed with "401" response code
    And company "adserverSellerTest8" can be searched out in database
