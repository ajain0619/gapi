@rule
Feature: create, update, delete, search selling rules under v1/sellers/{sellerPid}/rules endpoint

  Background:
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "Seller1"

  Scenario Outline: 0a. a user is attached to the company
    Given the user "<onecentral_username>" is detached from his companies
    Given the user "<onecentral_username>" is attached to the company "Seller1"

    Examples:
      | onecentral_username |
      | tagarchiveadmin     |
      | tagarchivemanager   |
      | tagarchiveuser      |

  # rule types: BRAND_PROTECTION(1), DEAL(2)
  Scenario: 0c. create rules in DB used in further query-field-based rule fetching scenarios
    Given insert device os data of ids "1,2,3" and names "supplyData7,testData7,ios"
    Given rules created for company named "Seller1" with rule pids "21,22,23,24,25", rule names "rn21,rn22,rn23,rn24,rn25", rule types "1,1,1,1,2"
    Given rules created for company named "seller8" with rule pids "31,32,33", rule names "rn31,rn32,rn33", rule types "1,1,1"
    Given sites created for company named "Seller1" with site pids "11,12,13"
    Given positions created with pids "31,32,33,34" for sites "11,12,12,13" correspondingly

  Scenario: 0d. create rules' DB relations used in further query-field-based rule fetching scenarios
    Given site_rule relations "(11, 21), (11, 22), (11, 23), (12, 24), (11, 25)" defined as (site_pid, rule_pid) pairs
    Given position_rule relations "(31, 21), (34, 21), (31, 22), (32, 23), (33, 24), (34, 24), (31, 25)" defined as (position_pid, rule_pid) pairs
    Given rule pids "21,23,24,25" assigned to a seller company named "Seller1"
    Given rule pids "31,32,33" assigned to a seller company named "seller8"

#  The user role used in this scenario does not matter. There is another scenario to check searching
#  for rules using query field for various user roles.
#  This scenario is just to check various query field combinations so it is common for any user role
  Scenario Outline: 1. get rules using query field for nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And set company "Seller1"
    And the user is searching for rules with query field "<queryField>" and field operator "<operator>"
    When rules were retrieved using query field criteria
    Then request passed successfully
    And returned "found rules" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/queryfield/<file_name_ER>_ER.json"

    Examples:
      | queryField                                         | file_name_ER                                 | operator |
      | {pid=21\|22\|23\|24}                               | QueryFieldSearchByPids                       | AND      |
      | {name=rn21}                                        | QueryFieldSearchByName                       | AND      |
      | {type=BRAND_PROTECTION}                            | QueryFieldSearchByType                       | AND      |
      | {deployedForSites=12\|13}                          | QueryFieldSearchBySitePids                   | AND      |
      | {deployedForPlacements=31\|33}                     | QueryFieldSearchByPlacementPids              | AND      |
      | {deployedForSeller=true}                           | QueryFieldSearchDeployedForSeller            | AND      |
      | {deployedForSites=11,deployedForPlacements=31\|34} | QueryFieldSearchDeployedForSiteAndPlacements | AND      |
      | {name=rn24,deployedForSeller=}                     | QueryFieldSearchByNameAndDeployedForSeller   | AND      |
      | {}                                                 | QueryFieldEmptyOrMissing                     | AND      |
      | {}                                                 | QueryFieldEmptyOrMissing                     | OR       |
      | {name=rn,type=BRAND_PROTECTION}                    | QueryFieldSearchByNameOrType                 | OR       |
      | {name=rn,type=BRAND_PROTECTION}                    | QueryFieldSearchByNameAndType                | AND      |

  Scenario: 2. get rules without query field criteria for nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And set company "Seller1"
    When rules were retrieved without query field parameter in query string
    Then request passed successfully
    And returned "found rules" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/queryfield/QueryFieldEmptyOrMissing_ER.json"

  Scenario Outline: 3. get rules using query field for users with various roles
    Given the user "<username>" has logged in with role "<role>"
    And set company "Seller1"
    And the user is searching for rules with query field "{pid=23}" and field operator "AND"
    When rules were retrieved using query field criteria
    Then request passed successfully

    Examples:
      | username          | role          |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |

  Scenario Outline: 4. get rules using invalid query field
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And set company "Seller1"
    And the user is searching for rules with query field "<queryField>" and field operator "AND"
    When rules were retrieved using query field criteria
    Then "found rule" failed with "400" response code

    Examples:
      | queryField                                   |
      | {type=BID_PROTECTION}                        |
      | {pid=ab13}                                   |
      | {deployedForSeller=no}                       |
      | {deployedForSites=23d\|x19}                  |
      | {deployedForPlacements=23d\|x19}             |
      | {deployedForSites=11,deployedForSeller=1\|3} |

  Scenario Outline: 5. no rules found using query field for users with various roles
    Given the user "<username>" has logged in with role "<role>"
    And set company "Seller1"
    And the user is searching for rules with query field "<query_field>" and field operator "<operator>"
    When rules were retrieved using query field criteria
    Then request passed successfully
    And returned "found rules" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/queryfield/QueryFieldNoResults_ER.json"

    Examples:
      | username          | role          | query_field                | operator |
      | crudnexagemanager | ManagerNexage | {pid=1000}                 | AND      |
      | crudnexagemanager | ManagerNexage | {pid=1000}                 | OR       |
      | crudnexageuser    | UserNexage    | {pid=1000,name=rn11}       | OR       |
      | crudnexageuser    | UserNexage    | {pid=1000,deployForSeller= | OR       |

  Scenario Outline: 6. create selling rules
    When the user creates selling rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/<file_param>_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/<file_param>_ER.json"
    And rule count for "<rule_name>" is 1
    And rule with name "<rule_name>" has correct action type value "<action_type>" in the db and action data value "<action_data>"

    Examples:
      | file_param      | rule_name            | action_type | action_data |
      | CreateRuleBPall | BPruleWithAllTargets | 1           | 1           |

  Scenario: 7. get non-existing rule pid
    Given the user sets non-existing rule pid
    When one rule is retrieved for UI user
    Then "get rule" failed with "404" response code

  Scenario: 8. get rule as API user
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user finds rule pid for name "BPruleWithAllTargets"
    When one rule is retrieved for UI user
    Then "get rule" failed with "401" response code

  Scenario Outline: 9. get one rule for users with various roles
    Given the user "<username>" has logged in with role "<role>"
    And set company "Seller1"
    And the user finds rule pid for name "BPruleWithAllTargets"
    When one rule is retrieved for UI user
    Then request passed successfully

    Examples:
      | username          | role          |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |
      | tagarchiveadmin   | AdminSeller   |
      | tagarchivemanager | ManagerSeller |
      | tagarchiveuser    | UserSeller    |

  Scenario: 10. delete rule as API user
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user finds rule pid for name "BPruleWithAllTargets"
    When one rule is retrieved for UI user
    Then "delete rule" failed with "401" response code

  Scenario Outline: 11. delete rule as non Nexage admin or manager
    Given the user "<username>" logs in via B2B with role "<role>"
    And the user finds rule pid for name "BPruleWithAllTargets"
    When rule is deleted
    Then "delete rule" failed with "403" response code

    Examples:
      | username          | role          |
      | crudnexageuser    | UserNexage    |
      | tagarchiveadmin   | AdminSeller   |
      | tagarchivemanager | ManagerSeller |
      | tagarchiveuser    | UserSeller    |

  Scenario Outline: 12. delete rule
    Given the user "<username>" has logged in with role "<role>"
    And set company "Seller1"
    And the user finds rule pid for name "BPruleWithAllTargets"
    When rule is deleted
    Then request passed successfully
    And the rule status "-1" is correct for rule "BPruleWithAllTargets"
    When one rule is retrieved for UI user
    Then "get rule" failed with "404" response code

    Examples:
      | username          | role          |
      | crudnexagemanager | ManagerNexage |

  Scenario:13 create and update a rule no change version incremented
    When the user creates selling rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/CreateRuleBM_test_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/CreateRuleBM_test_ER.json"
    And rule count for "BMTest" is 1
    When the user finds rule pid for name "BMTest"
    When the user updates the rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/UpdateRuleBM_test_no_change_payload.json"
    Then request passed successfully
    And returned "updated rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/UpdateRuleBM_test_no_change_ER.json"
    And rule intended actions count for "BMTest" is 1

  Scenario Outline:14 update a rule wrong input
    When the user finds rule pid for name "BMTest"
    When the user updates the rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/CreateRuleBM_test_<file_param>_payload.json"
    Then "Update rule" failed with "<expected_response_code>" response code
    And "Update rule" failed with "<expected_response_message>" response message

    Examples:
      | file_param                                        | expected_response_code | expected_response_message                                           |
      | illegal_intended_action                           | 400                    | Editing intended action that belongs to another rule is not allowed |
      | illegal_rule_target                               | 400                    | Editing rule target that belongs to another rule is not allowed     |
      | illegal_assignment                                | 400                    | Assigning a rule to the alien target                                |
      | illegal_version                                   | 409                    | Stale data issue, refresh the data and try again                    |

  Scenario Outline:15 update a rule
    When the user finds rule pid for name "BMTest"
    When the user updates the rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/UpdateRuleBM_test_<file_param>_payload.json"
    And request passed successfully
    And returned "updated rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/UpdateRuleBM_test_<file_param>_ER.json"

    Examples:
      | file_param                 |
      | 01_simple                  |
      | 02_update_intended_action  |
      | 03_replace_intended_action |

  Scenario Outline: 16 create selling rule without formula then add the placement formula
    When the user creates selling rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/<file_param>_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/<file_param>_ER.json"
    And rule formulas count for rule "<rule_name>" is 0
    Then the user finds rule pid for name "BMruleWithoutPFormula"
    Then the user updates the rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/<rule_with_formula>_payload.json"
    And request passed successfully
    And returned "updated rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/<rule_with_formula>_ER.json"
    And rule formulas count for rule "<rule_name>" is 1

    Examples:
      | file_param                | rule_name             | rule_with_formula        |
      | CreateRuleWithoutPFormula | BMruleWithoutPFormula | UpdateRuleBMWithPFormula |

  Scenario: 17 create and update a selling rule with target type DEAL_CATEGORY
    When the user creates selling rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/CreateRuleBMWithTarget_DealCategory_payload.json"
    Then request passed successfully with code "201"
    And returned "create rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/CreateRuleBMWithTarget_DealCategory_ER.json"
    Then the user finds rule pid for name "RuleBMWithTarget_DealCategory"
    Then the user updates the rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/UpdateRuleBMWithTarget_DealCategory_payload.json"
    Then request passed successfully with code "200"
    And returned "updated rule" data matches the following json file "jsons/genevacrud/sellingrule/v1.sellers.rules/expected_results/UpdateRuleBMWithTarget_DealCategory_ER.json"

  Scenario Outline: 18 create invalid rule
    When the user creates selling rule using v1 api from the json file "jsons/genevacrud/sellingrule/v1.sellers.rules/payload/CreateRuleBMWith<file_param>_payload.json"
    Then "Create rule" failed with "<expected_response_code>" response code
    And "Create rule" failed with "<expected_response_message>" response message

    Examples:
      | file_param                                        | expected_response_code | expected_response_message                                           |
      | InvalidTarget_DealCategory                        | 400                    | Rule Target DEAL_CATEGORY has invalid value                         |
