@sellerseatrule
Feature: create, update, delete, search seller seat rule

  @restoreCrudCoreDatabaseBefore

  Scenario: 1. Successfully create seller seat rule
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates seller seat rule for seller seat "1" from the json file "jsons/genevacrud/sellerseatrule/payload/create/CreateSSRuleBP_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/create/CreateSSRuleBP_ER.json"
    And rule count for "SellerSeatBPrule" is 1
    And rule with name "SellerSeatBPrule" has correct action type value "1" in the db and action data value "1"

  Scenario: 2. Successfully create a rule with the same name for a different seller seat
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates seller seat rule for seller seat "2" from the json file "jsons/genevacrud/sellerseatrule/payload/create/CreateSSRuleBPSellerSeat2_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/create/CreateSSRuleBPSellerSeat2_ER.json"
    And rule count for "SellerSeatBPrule" is 2

  Scenario: 3. Try to create a rule with the same name for the same seller seat
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates seller seat rule for seller seat "1" from the json file "jsons/genevacrud/sellerseatrule/payload/create/CreateSSRuleBP_payload.json"
    Then "create rule" failed with "400" response code and error message "Duplicate rule name"
    And rule count for "SellerSeatBPrule" is 2

  Scenario: 4. Try to create a rule with empty required fields
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates seller seat rule for seller seat "1" from the json file "jsons/genevacrud/sellerseatrule/payload/create/CreateBrokenSSRule_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"intendedActions[].actionData":"may not be empty","name":"must not be null","type":"must not be null","sellerSeatPid":"must not be null","intendedActions[].actionType":"must not be null","status":"must not be null"}"

  Scenario Outline: 5. Create rules for find & search tests
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates seller seat rule for seller seat "2" from the json file "jsons/genevacrud/sellerseatrule/payload/create/<file_param>.json"
    Then request passed successfully with code "201"

    Examples:
      | file_param                     |
      | CreateSSRuleForSearch1_payload |
      | CreateSSRuleForSearch2_payload |

  Scenario Outline: 6. Find all seller seat rules for authorized user
    Given the user "<user>" has logged in with role "<role>"
    When the user searches for all seller seat rules in seller seat "<pid>"
    Then request passed successfully
    And returned "get seller seat rules" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/search/<file_param>_ER.json"

    Examples:
      | user             | role           | pid | file_param              |
      | admin1c          | AdminNexage    | 1   | RulesWithSellerSeatPid1 |
      | admin1c          | AdminNexage    | 2   | RulesWithSellerSeatPid2 |
      | crudnexageuser   | UserNexage     | 1   | RulesWithSellerSeatPid1 |
      | crudnexageuser   | UserNexage     | 2   | RulesWithSellerSeatPid2 |
      | svcsellerseat944 | UserSellerSeat | 2   | RulesWithSellerSeatPid2 |

  Scenario: 7. Find all seller seat rules for unauthorized user
    Given the user "svcsellerseat944" has logged in with role "UserSellerSeat"
    When the user searches for all seller seat rules in seller seat "1"
    Then "get seller seat rules" failed with "401" response code

  Scenario Outline: 8. Find all seller seat rules with search
    Given the user "svcsellerseat944" has logged in with role "UserSellerSeat"
    When the user searches for all seller seat rules in seller seat "2" with query field "<query_field>" and term "<query_term>"
    Then request passed successfully
    And returned "get seller seat rules" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/search/<file_param>_ER.json"

    Examples:
      | query_field | query_term | file_param                               |
      | name        | Lancelot   | RulesWithSellerSeatPid2Filtered_Lancelot |
      | name        | Sir        | RulesWithSellerSeatPid2Filtered_Both     |
      | name,pid    | 5678       | RulesWithSellerSeatPid2Filtered_Robin    |
      | name,pid    | 4          | RulesWithSellerSeatPid2Filtered_Both     |

  Scenario: 9. Find all seller seat rules with search on invalid field
    Given the user "svcsellerseat944" has logged in with role "UserSellerSeat"
    When the user searches for all seller seat rules in seller seat "2" with query field "invalid" and term "whatever"
    Then "get seller seat rules" failed with "400" response code

  Scenario: 10. Update non-existing seller seat rule
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the user sets non-existing seller seat rule pid
    When the user updates the rule for seller seat "1" from the json file "jsons/genevacrud/sellerseatrule/payload/update/UpdateNonExistingRule_payload.json"
    Then "update rule" failed with "404" response code and error message "Selling rule not found"

  Scenario Outline: 11. Update a seller seat rule
    When the user finds seller seat rule pid for name "<rule_name>"
    When the user updates the rule for seller seat "2" from the json file "jsons/genevacrud/sellerseatrule/payload/update/<file_payload>_payload.json"
    Then request passed successfully
    And returned "updated rule" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/update/<file_ER>_ER.json"
    And rule intended actions count for "<rule_name>" is 1
    And rule target count for "<rule_name>" is <target_count>

    Examples:
      | file_payload    | file_ER         | rule_name         | target_count |
      | UpdateSSRuleBP  | UpdateSSRuleBP  | Sir Lancelot 1234 | 1            |
      | UpdateSSRuleBP2 | UpdateSSRuleBP2 | Sir Lancelot 1234 | 2            |

  Scenario Outline: 12. Find rule by pid by authorized user
    Given the user "<user>" has logged in with role "<role>"
    When the user fetches seller seat rule with "4" in seller seat "2"
    Then request passed successfully
    And returned "seller seat rule" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/search/RuleWithPid4_ER.json"

    Examples:
      | user             |  role           |
      | admin1c          |  AdminNexage    |
      | crudnexageuser   |  UserNexage     |
      | svcsellerseat944 |  UserSellerSeat |

  Scenario: 13. Find rule by pid by unauthorized user
    Given the user "svcsellerseat944" has logged in with role "UserSellerSeat"
    When the user fetches seller seat rule with "1" in seller seat "1"
    Then "get seller seat rules" failed with "401" response code

  Scenario Outline: 14. Find rule by pid with wrong params
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user fetches seller seat rule with "<rulePid>" in seller seat "<seatPid>"
    Then "get seller seat rules" failed with "404" response code

    Examples:
      | seatPid | rulePid |
      | 2       | 123     |
      | 123     | 4       |

  Scenario: 15. Successfully create a rule with target type BUYER_SEATS using old notation retrived one is converted to new notation
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates seller seat rule for seller seat "2" from the json file "jsons/genevacrud/sellerseatrule/payload/create/CreateSSRuleSellerSeat2_BuyerSeatsOld_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellerseatrule/expected_results/create/CreateSSRuleSellerSeat2_BuyerSeatsOld_ER.json"
    And rule count for "SellerSeatBPrule" is 2
