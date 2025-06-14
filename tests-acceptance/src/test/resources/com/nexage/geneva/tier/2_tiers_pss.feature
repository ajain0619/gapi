Feature: create, update, delete tiers as Seller Admin

  Background: log in as seller admin and select the site
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user selects the site "AS8B"
    And position with name "position1" is selected

  @restoreCrudCoreDatabaseBefore

  Scenario: get all tiers
    When the user gets all publisher tiers
    Then request passed successfully
    And returned "tiers" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/GetAllTiers_ER.json"

  Scenario: get tier
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user gets the publisher tier
    Then request passed successfully
    And returned "tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/GetTier_ER.json"

  Scenario: create tier
    When the user creates publisher tier from the json file "jsons/genevacrud/tier/pss/payload/CreateTier_payload.json"
    Then request passed successfully
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateTier_ER.json"

  Scenario Outline: update tier with different parameters
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "updated tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/<file_ER>.json"

    Examples:
      | file_payload               | file_ER               |
      | AddTagToTier_payload       | AddTagToTier_ER       |
      | AddSecondTagInTier_payload | AddSecondTagInTier_ER |

  Scenario: update tier with different parameters
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/RemoveTagFromTier_payload.json"
    Then request passed successfully
    And returned tier is removed

  Scenario: create invalid tier will fail
    When the user creates publisher tier from the json file "jsons/genevacrud/tier/pss/payload/CreateInvalidTier_payload.json"
    Then "tier creation" failed with "400" response code

  Scenario: update tier using invalid tier will fail
    Given the user gets all publisher tiers
    And first publisher tier is retrieved
    When the user updates the publisher tier from the json file "jsons/genevacrud/tier/pss/payload/UpdateInvalid_payload.json"
    Then "tier update" failed with "400" response code

  # scenario below fails due to environment issue
  # currently it returns 400 error instead of 404
  @unstable
  Scenario: get non existing tier will fail
    When the user gets non existing publisher tier
    Then "tier search" failed with "404" response code

  Scenario: create tier of type sy_decision_maker for non smart yield position
    When the user creates sy_decision_maker tier from the json file "jsons/genevacrud/tier/pss/payload/CreateSYDMTier_payload.json"
    Then "tier create" failed with "400" response code

  Scenario: create tier of type super_auction for non smart yield position
    When the user creates super_auction tier from the json file "jsons/genevacrud/tier/pss/payload/CreateSuperAuctionTier_payload.json"
    Then "tier create" failed with "400" response code
