Feature: Seller Attributes - Update Seller Attributes Custom deal floor enabled

  Scenario: Update custom deal floor enabled
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates seller attribute custom deal floor enabled to true
    Then request passed successfully
    And returned "seller attributes" data matches the following json file "jsons/genevacrud/sellerattributes/expected_results/UpdateCustomDealFloorEnabledSellerAttributes_ER.json"

  Scenario: Get updated custom deal floor enabled
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user gets seller attributes
    Then request passed successfully
    And returned "seller attributes" data matches the following json file "jsons/genevacrud/sellerattributes/expected_results/GetSellerAttributecCustomDealFloorEnabled_ER.json"
