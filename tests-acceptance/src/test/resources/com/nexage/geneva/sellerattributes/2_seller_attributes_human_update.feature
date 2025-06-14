Feature: Seller Attributes - Update Seller Attributes Human sampling rates

  Scenario: Update human sampling rates to 10 and 90
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user updates seller attributes prebid and postbid human sampling rates to "10" and "90" respectively
    Then request passed successfully
    And returned "seller attributes" data matches the following json file "jsons/genevacrud/sellerattributes/expected_results/UpdateHumanSampleRatesSellerAttributes_ER.json"

  Scenario: Get updated human sampling rates
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user gets seller attributes
    Then request passed successfully
    And returned "seller attributes" data matches the following json file "jsons/genevacrud/sellerattributes/expected_results/GetSellerAttributes_ER.json"
