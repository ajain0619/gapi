Feature: get, update rtb ad source defaults as Seller Admin

  Background: log in as seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "RTB" ad source type is selected

  @restoreCrudCoreDatabaseBefore

  Scenario: get all rtb ad source defaults
    When the user gets all ad source defaults
    Then request passed successfully
    And returned "all RTB ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/rtb/pss/expected_results/GetAllAdSourceDefaults_ER.json"

  Scenario Outline: update rtb ad source defaults
    When the user updates ad source defaults from the json file "jsons/genevacrud/adsourcedefaults/rtb/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "updated RTB ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/rtb/pss/expected_results/<file_ER>.json"

    Examples:
      | file_payload                       | file_ER                       |
      | UpdateBlockAndBidderGroups_payload | UpdateBlockAndBidderGroups_ER |
      | RemoveBlockAndBidderGroups_payload | RemoveBlockAndBidderGroups_ER |
      | UpdateBiddersWhiteList_payload     | UpdateBiddersWhiteList_ER     |
