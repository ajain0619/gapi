Feature: create, update, search deals as nexage admin

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: search for all deals
    When the user searches for all deals
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/AllDeals_ER.json"

  Scenario: search for deal by existing deal id
    Given the user searches for all deals
    When the user searches for deal by deal id "144222524111424355"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealById_ER.json"

  Scenario: Create rule without a publisher
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateInactiveRuleWithoutPublisher_payload.json"
    Then request passed successfully
    And returned "rule" data matches the following json file "jsons/genevacrud/sellingrule/expected_results/CreateInactiveRuleWithoutPublisher_ER.json"

  Scenario: create deal with difference status from rule
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/ActiveDealWithInactiveRule_payload.json"
    Then "deal create" failed with "400" response code

  Scenario: create deal with both position and placement formula assignments
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/AllFields_Payload_Positions_And_PlacementFormula.json"
    Then "deal create" failed with "400" response code and error message "Placement Formula and explicit Assignments (for positions/publishers/sites) cannot be used at the same time when creating/updating a deal."

  Scenario: create deal with both site and placement formula assignments
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/AllFields_Payload_Sites_And_PlacementFormula.json"
    Then "deal create" failed with "400" response code and error message "Placement Formula and explicit Assignments (for positions/publishers/sites) cannot be used at the same time when creating/updating a deal."

  Scenario: create deal with both publisher and placement formula assignments
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/AllFields_Payload_Publishers_And_PlacementFormula.json"
    Then "deal create" failed with "400" response code and error message "Placement Formula and explicit Assignments (for positions/publishers/sites) cannot be used at the same time when creating/updating a deal."

  Scenario: create deal with placement formula but no auto update
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/AllFields_Payload_With_PlacementFormula_No_Auto_Update.json"
    Then "deal create" failed with "400" response code and error message "Auto Update flag cannot be null (has to be true or false) when using placement formula"

  Scenario: create deal with placement formula with invalid Rule data
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/Deal_create_payload_with_Invalid_placement_formula_inventory_attributes.json"
    Then "deal create" failed with "400" response code and error message "Rule data is invalid"

  Scenario: create deal with placement formula with Pid and file name in rule data do not match
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/Deal_create_payload_with_not_matching_placement_formula_inventory_attributes.json"
    Then "deal create" failed with "400" response code and error message "Pid and file name in rule data do not match"

  Scenario: Create rule with multiple targets
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisher_payload.json"
    Then request passed successfully
    And returned "rule" data matches the following json file "jsons/genevacrud/sellingrule/expected_results/CreateRuleWithoutPublisherForExternalDealId_ER.json"


  Scenario: create deal with external deal id with rule targets other than buyerseat'
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/Deal_create_payload_with_external_deal_id_enabled.json"
    Then "deal create" failed with "400" response code and error message "Target can not be other than Buyer seat when external deal ID is enabled"

  Scenario Outline: create deal from <filename>
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/<file_ER>.json"

    Examples:
      | file_payload                                                   | file_ER                                                   |
      | AllFields_payload_with_positions                               | AllFields_with_positions_ER                               |
      | AllFields_payload_with_placement_formula                       | AllFields_with_placement_formula_ER                       |
      | AllFields_payload_with_placement_formula_no_matching_positions | AllFields_with_placement_formula_no_matching_positions_ER |
      | RequiredFields_payload                                         | RequiredFields_ER                                         |
      | AllFields_payload_with_placement_formula_inventory_attributes  | AllFields_with_placement_formula_inventory_attributes_ER  |

  Scenario: get call on a deal that has a placement formula by deal id
    Given the user searches for all deals
    When the user searches for deal by deal id "14466299473087828"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealByIdWithPlacementFormula_ER.json"

  Scenario: search for a page of deals with rules attached
    When the user searches for a page of deals with rules attached
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/PagedDealsWithRules_ER.json"

  Scenario: search for a page of deals with no rules attached
    When the user searches for paged deals with no rules attached
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/PagedDealWithNoRules_ER.json"

  Scenario: search for a page of all deals regardless of rules
    When the user searches for paged deals regardless of rules
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/AllDealsPaged_ER.json"

  Scenario: search for a page of deals with rules attached searching dealId
    When the user searches for a page of deals with rules attached seaching for dealId "14466299473087827"
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/PagedDealsWithDealId_ER.json"

  Scenario: search for a page of deals with rules attached searching description
    When the user searches for a page of deals with rules attached searching for description "allFieldsTest2"
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/PagedDealsWithDescription_ER.json"

  Scenario: update deal with a different status than rule
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updateStatusToNotMatchRule_payload.json"
    Then "deal update" failed with "400" response code

  Scenario Outline: update deal with different set of fields
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload                                    | file_ER                                    |
      | updateRequiredFields_payload                    | updateRequiredFields_ER                    |
      | updateRemovePosition_payload                    | updateRemovePosition_ER                    |
      | updateRemovePublisher_payload                   | updateRemovePublisher_ER                   |
      | updateRemoveAllPositions_payload                | updateRemoveAllPositions_ER                |
      | updateAddPlacementFormula_payload               | updateAddPlacementFormula_ER               |
      | updatePlacementFormula_payload                  | updatePlacementFormula_ER                  |
      | updatePlacementFormulaNoPositionsMatch_payload  | updatePlacementFormulaNoPositionsMatch_ER  |
      | updatePlacementFormulaAutoUpdate_payload        | updatePlacementFormulaAutoUpdate_ER        |
      | updateRemovePlacementFormulaAssociation_payload | updateRemovePlacementFormulaAssociation_ER |
      | updatePlacementFormulaWithInventory_payload     | updatePlacementFormulaWithInventory_ER     |

  Scenario: update deal with a placement formula and update it again with a site assignment to ensure no more placement assignments
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updatePlacementFormula_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/updatePlacementFormulaBeforeSiteAssignment_ER.json"
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updateDealRemovePlacementFormulaWithSite_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/updateDealRemovePlacementFormulaWithSite_ER.json"

  Scenario: update deal placement formula without auto update attribute
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updatePlacementFormulaAutoUpdateMissing_payload.json"
    Then "deal update" failed with "400" response code and error message "Auto Update flag cannot be null (has to be true or false) when using placement formula"

  Scenario: update deal remove placement formula but include auto update attribute
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updatePlacementFormulaMissingAutoUpdateIncluded_payload.json"
    Then "deal update" failed with "400" response code and error message "Auto Update flag can only be used when using placement formula"

  Scenario: update deal with both placement formula and positions
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/UpdateWithPositionsAndPlacementFormula_payload.json"
    Then "deal update" failed with "400" response code and error message "Placement Formula and explicit Assignments (for positions/publishers/sites) cannot be used at the same time when creating/updating a deal."

  Scenario: update deal with invalid placement formula
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the defaultRTBProfilesFlag for company "CRUDPositionTest" is set to "true"
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updateInvalidPlacementFormula_payload.json"
    Then "deal update" failed with "400" response code

  Scenario: update deal to remove an association
    Given the user searches for all deals
    And the exchange_site_tag record with pid "31002" initially has version 0
    And there are initially no exchange_site_tag_aud records with pid "31002"
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updateRemoveProfileAssociation_payload.json"
    Then request passed successfully
    And there are now "0" exchange_site_tag_aud records with pid "31002"

  Scenario: update deal to add an association
    Given the user searches for all deals
    And the exchange_site_tag record with pid "10000008" initially has version 0
    And there are initially no exchange_site_tag_aud records with pid "10000008"
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/updateAddProfileAssociation_payload.json"
    Then request passed successfully
    And there are now "0" exchange_site_tag_aud records with pid "10000008"

  Scenario: change deal status from disabled to enabled
    Given the user searches for all deals
    When the user changes deal status from disabled to enabled for deal with Deal id "1442225289467393435"
    Then request passed with "204" response code

  Scenario: change deal status from enabled to disabled
    Given the user searches for all deals
    When the user changes deal status from enabled to disabled for deal with Deal id "1442225289467393435"
    Then request passed with "204" response code

  Scenario: search for deal by non existing deal pid will fail
    When the user searches for non existing deal
    Then "deal search" failed with "404" response code

  Scenario Outline: create invalid deal will fail
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/<filename>.json"
    Then "deal creation" failed with "400" response code

    Examples:
      | filename                |
      | InvalidDeal_payload     |
      | DuplicateDealId_payload |

  Scenario: update deal to invalid state will fail
    Given the user searches for all deals
    When the user update a deal with Deal id "1442225178554269962" from the json file "jsons/genevacrud/deal/payload/update/UpdateToInvalid_payload.json"
    Then "deal update" failed with "400" response code

  Scenario Outline: archived tags should not be returned in the selected suppliers grid for a deal
    Given the user searches for all deals
    When the user searches for deal by deal id "<dealId>"
    Then request passed successfully
    And the returned deal data should not contain the archived tag "<rtbProfileId>"

    Examples:
      | dealId              | rtbProfileId |
      | 1457031635254364732 | 10000046     |
      | 1457031635254364732 | 10000047     |
      | 1457031857582262189 | 10000046     |
      | 1457031857582262189 | 10000047     |
      | 1457031762001880712 | 10000047     |
      | 1457031911582269943 | 10000047     |

  Scenario Outline: inactive tags should be returned in the selected suppliers grid for a deal
    Given the user searches for all deals
    When the user searches for deal by deal id "<dealId>"
    Then request passed successfully
    And the returned deal data should contain the inactive tag "<rtbProfileId>"

    Examples:
      | dealId              | rtbProfileId |
      | 1457031635254364732 | 10000044     |
      | 1457031857582262189 | 10000044     |
      | 1457031857582262189 | 10000045     |
      | 1457031762001880712 | 10000045     |
      | 1457031911582269943 | 10000045     |

  Scenario: Deal creation will fail for publishers with different currencies and deal currency other than USD
    Given the user selects the "Seller" company "CRUDPositionTest"
    And the currency for company is set to "EUR"
    Given the user selects the "Seller" company "Rinky"
    And the currency for company is set to "USD"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/MX8603_Payload_With_Valid_Eur_Currency_payload.json"
    Then "deal creation" failed with "400" response code and error message "Deal currency should be set to USD"

  Scenario: Deal creation will fail when deal currency is incorrect
    Given the user selects the "Seller" company "CRUDPositionTest"
    Given the user selects the "Seller" company "Rinky"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/MX8603_Payload_With_Invalid_Currency_payload.json"
    Then "deal creation" failed with "400" response code and error message "Currency is not supported"

  Scenario: Deal creation will fail when placement formula is set and deal currency other than USD
    Given the user selects the "Seller" company "CRUDPositionTest"
    Given the user selects the "Seller" company "Rinky"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/MX8603_Payload_With_Placement_Formula_And_Valid_Currency_payload.json"
    Then "deal creation" failed with "400" response code and error message "Deal currency should be set to USD"

  Scenario Outline: Successfully create deal for publishers
    Given the user selects the "Seller" company "<company_name>"
    And the currency for company is set to "<company_currency>"
    Given the user selects the "Seller" company "<company_name_2>"
    And the currency for company is set to "<company_currency_2>"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/<file_ER>.json"

    Examples:
      | company_name     | company_name_2 | company_currency | company_currency_2 | file_payload                                   | file_ER                                 |
      | CRUDPositionTest | Rinky          | EUR              | EUR                | MX8603_Payload_With_Valid_Eur_Currency_payload | MX8603_RequiredFields_With_Eur_Currency |
      | CRUDPositionTest | Rinky          | USD              | USD                | MX8603_Payload_With_Valid_Usd_Currency_payload | MX8603_RequiredFields_With_Usd_Currency |

  Scenario: Deal creation will fail when deal priority type is incorrect
    Given the user selects the "Seller" company "CRUDPositionTest"
    Given the user selects the "Seller" company "Rinky"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/MX7680_Payload_With_Invalid_Deal_Priority.json"
    Then "deal creation" failed with "400" response code and error message "Bad Request"

  Scenario: Deal creation will fail when deal deal category is incorrect on global dashboard
    Given the user selects the "Seller" company "CRUDPositionTest"
    Given the user selects the "Seller" company "Rinky"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/SSP21267_Payoad_With_Seller_Deal_Category.json"
    Then "deal creation" failed with "400" response code and error message "Invalid deal Category."

  Scenario Outline: Successfully create deal with priority tier
    Given the user selects the "Seller" company "CRUDPositionTest"
    Given the user selects the "Seller" company "Rinky"
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/<file_payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/<file_ER>.json"

    Examples:
      | file_payload                         | file_ER                                          |
      | MX7680_Payload_With_No_Deal_Priority | MX7680_RequiredFields_With_Default_Deal_Priority |
      | MX7680_Payload_With_Deal_Priority    | MX7680_RequiredFields_With_Deal_Priority         |

  Scenario: update deal to invalid priority will fail
    Given the user searches for all deals
    When the user update a deal with Deal id "1446629641838983368" from the json file "jsons/genevacrud/deal/payload/update/MX7680_Payload_With_Invalid_Deal_Priority.json"
    Then "deal update" failed with "400" response code and error message "Bad Request"

  Scenario: update deal with priority will succeed
    Given the user searches for all deals
    When the user update a deal with Deal id "1446629641838983368" from the json file "jsons/genevacrud/deal/payload/update/MX7680_Payload_With_Open_Deal_Priority.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/MX7680_RequiredFields_With_Deal_Priority.json"

  Scenario: update deal to not allowed currency will fail
    Given the user selects the "Seller" company "Seller PR-8826"
    And the currency for company is set to "EUR"
    Given the user searches for all deals
    When the user update a deal with Deal id "1446629641836763368" from the json file "jsons/genevacrud/deal/payload/update/MX9427_Payload_With_Valid_Gbp_Currency_payload.json"
    Then "deal update" failed with "400" response code and error message "Deal currency should be set to USD or common for all publishers"

  Scenario: update deal with allowed currency will succeed
    Given the user selects the "Seller" company "Seller PR-8826"
    And the currency for company is set to "GBP"
    Given the user searches for all deals
    When the user update a deal with Deal id "1446629641836763368" from the json file "jsons/genevacrud/deal/payload/update/MX9427_Payload_With_Valid_Gbp_Currency_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/MX9427_RequiredFields_With_Gbp_Currency.json"

  Scenario: Get One Deal By PID
    When the user fetches one Deal for deal PID "1"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealByPID_1_ER.json"
