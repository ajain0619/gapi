Feature: publisher map

  Scenario: 0. create deals for further search tests
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisher_payload.json"
    And the user create a deal from the json file "jsons/genevacrud/deal/payload/create/DealWithInventory_payload.json"
    Then request passed successfully
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisher_payload.json"
    And the user create a deal from the json file "jsons/genevacrud/deal/payload/create/DealWithInventory2_payload.json"
    Then request passed successfully

  Scenario: get publisher map associated with deal for admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user gets publisher associated with deal by dealPid "12" and publisherId "10213"
    Then request passed successfully

  Scenario Outline: get publisher map associated with deal for user
    Given the user "<user>" has logged in with role "<role>"
    When the user gets publisher associated with deal by dealPid "12" and publisherId "10213"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.


    Examples:
      | user        | role       |
      | BuyerUser1  | UserBuyer  |
      | BuyerUser2  | UserBuyer  |
      | SellerUser1 | UserSeller |
