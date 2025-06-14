Feature: Seller Sites: create, update should not be successful for Nexage Users and Sellers

  Background: user logs in
    Given the user "crudPositionUser" has logged in with role "UserSeller"

  Scenario: update site random fields as Seller User
    Given the user selects the site "CRUDPosition_Site1"
    When the user updates site with data from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateSite10201_payload.json"
    Then "site update" failed with "401" response code
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/company/payload/nexageuser/UpdateSite10201_ER.json"

  Scenario: delete site as Seller User
    Given the user deletes site "CRUDPosition_Site1"
    Then "site deletion" failed with "401" response code
    And site "CRUDPosition_Site1" can be searched in the database

  Scenario: create position with different parameters as Seller User
    Given the user selects the site "CRUDPosition_Site1"
    When the user creates position first call from the json file "jsons/genevacrud/company/payload/nexageuser/CreateBanner10201_payload.json"
    Then "position creation" failed with "401" response code
    And position "sellerUserNewPlacement" cannot be searched in the database

  Scenario: update position with different parameters as Seller User
    Given the user selects the site "CRUDPosition_Site1"
    And position with name "banner_placement" is selected
    When the user updates selected position first call from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateBanner10201_payload.json"
    Then "position update" failed with "401" response code
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/company/payload/nexageuser/UpdateSite10201_ER.json"

  Scenario: delete position as Seller User
    Given the user selects the site "CRUDPosition_Site1"
    Given position with name "CRUDPosition_UpdateCases" is selected
    When the user deletes selected position
    Then "position deletion" failed with "401" response code
    And position "CRUDPosition_UpdateCases" can be searched in the database

  Scenario: create a new tag as Seller User
    Given the user selects the site "CRUDPosition_Site1"
    When the user creates the "non-exchange" tag from the json file "jsons/genevacrud/company/payload/nexageuser/NewNonExchangeTagRequiredFields_payload.json"
    Then "tag creation" failed with "401" response code
    And tag "iVdopia - NewNonExchangeTagRequiredFields" cannot be searched in the database

  Scenario Outline: delete tag as Seller User
    Given the user selects the site "CRUDPosition_Site2"
    When the user deletes the "<tag type>" tag "<tag name>"
    Then "tag deletion" failed with "401" response code
    And tag "<tag name>" can be searched in the database

    Examples:
      | tag type     | tag name                                    |
      | non-exchange | Adstars - bannerposition2_nonExchange       |
      | exchange     | Nexage Exchange - banner_position1_excahnge |

  Scenario: create a Seller company as Seller User
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateAllFeatureEnabledSeller_payload.json"
    Then "seller creation" failed with "401" response code
    And company "createNameSellerUser" cannot be searched out in database

  Scenario: update Seller company as Seller User
    Given the seller user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateTextBox10201_payload.json"
    Then "company update" failed with "401" response code
    And company "CRUDPositionTestUpdate" cannot be searched out in database

  Scenario: delete a Seller company as Seller User
    Given the seller user deletes the selected company
    Then "company deletion" failed with "401" response code
    And company "CRUDPositionTest" can be searched out in database
