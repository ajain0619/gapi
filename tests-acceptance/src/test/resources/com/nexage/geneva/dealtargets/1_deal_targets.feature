Feature: create, update, search deals with targets as nexage admin

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  @restoreCrudCoreDatabaseBefore

  Scenario: create deal with targets
    When the user create a deal from the json file "jsons/genevacrud/dealtargets/payload/create/AllFields_Targets_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/create/AllFields_Targets_ER.json"

  Scenario:  create deal with duplicate targets - all targets should be created
    When the user create a deal from the json file "jsons/genevacrud/dealtargets/payload/create/TargetsWithDuplicates_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/create/TargetsWithDuplicates_ER.json"

  Scenario:  create a deal with empty targets
    When the user create a deal from the json file "jsons/genevacrud/dealtargets/payload/create/DealWithEmptyTargets_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/create/DealWithEmptyTargets_ER.json"

  Scenario:  get all deals
    When the user searches for all deals
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/search/GetAllDealsWithTargets_ER.json"

  Scenario:  Get a deal - uses deal id to lookup the deal pid
    Given the user searches for all deals
    When the user searches for deal by deal id "1446629641836763651"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/search/DealByPidWithTargets_ER.json"

  Scenario Outline: create deal with invalid target values will fail
    When the user create a deal from the json file "jsons/genevacrud/dealtargets/payload/create/<filename>.json"
    Then "deal creation with invalid target values" failed with "400" response code

    Examples:
      | filename                            |
      | TargetWithInvalidRuleType_payload   |
      | TargetWithInvalidTargetType_payload |

  Scenario Outline: create deal with missing required target fields will fail with correct message
    When the user create a deal from the json file "jsons/genevacrud/dealtargets/payload/create/<filename>.json"
    Then "deal creation with invalid target values" failed with "400" response code
    And "deal creation with missing target fields" failed with "target type, rule type and target data is required" response message

    Examples:
      | filename                            |
      | TargetWithMissingRuleType_payload   |
      | TargetWithMissingTargetType_payload |
      | TargetWithMissingData_payload       |

  Scenario Outline: Add new targets for a existing deal
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/dealtargets/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload               | file_ER               |
      | updateDealTargets_payload1 | updateDealTargets_ER1 |

  Scenario Outline: update existing targets for a deal
    Given the user searches for all deals
    When the user update a deal with Deal id "1457031635254364732" from the json file "jsons/genevacrud/dealtargets/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload               | file_ER               |
      | updateDealTargets_payload2 | updateDealTargets_ER2 |

  Scenario: search for above deal with targets
    Given the user searches for all deals
    When the user searches for deal by deal id "1457031635254364732"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/search/DealByIdTargets_ER.json"

  Scenario: update with some non-existent target pid for a deal(Negative case)
    Given the user searches for all deals
    When the user update a deal with Deal id "1457031635254364732" from the json file "jsons/genevacrud/dealtargets/payload/update/updateDealTargets_payload3.json"
    Then "deal update with unknown pid value" failed with "404" response code

  Scenario Outline: remove some targets for a deal
    Given the user searches for all deals
    When the user update a deal with Deal id "1457031635254364732" from the json file "jsons/genevacrud/dealtargets/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload                            | file_ER                            |
      | updateRemoveSomeTargetsForDeal_payload1 | updateRemoveSomeTargetsForDeal_ER1 |

  Scenario Outline: remove all targets for a deal
    Given the user searches for all deals
    When the user update a deal with Deal id "1457031635254364732" from the json file "jsons/genevacrud/dealtargets/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/dealtargets/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload                           | file_ER                           |
      | updateRemoveAllTargetsForDeal_payload2 | updateRemoveAllTargetsForDeal_ER2 |
