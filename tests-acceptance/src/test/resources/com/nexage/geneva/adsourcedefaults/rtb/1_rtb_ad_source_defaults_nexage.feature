Feature: get, update rtb ad source defaults as Nexage Admin

  Background: log in as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And "RTB" ad source type is selected

  Scenario: get all rtb ad source defaults
    When the user gets all ad source defaults
    Then request passed successfully
    And returned "all RTB ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/rtb/expected_results/GetAllAdSourceDefaults_ER.json"

  Scenario Outline: update rtb ad source defaults with different parameters
    When the user updates ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/rtb/payload/<file_payload>.json"
    Then request passed successfully
    And returned "updated RTB ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/rtb/expected_results/<file_ER>.json"

    Examples:
      | file_payload                       | file_ER                       |
      | UpdateBlockAndBidderGroups_payload | UpdateBlockAndBidderGroups_ER |
      | RemoveBlockAndBidderGroups_payload | RemoveBlockAndBidderGroups_ER |
      | UpdateBiddersWhiteList_payload     | UpdateBiddersWhiteList_ER     |
