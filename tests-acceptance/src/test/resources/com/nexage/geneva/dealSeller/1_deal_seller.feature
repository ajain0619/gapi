Feature: find deals related to a particular seller

  Background: Login as non nexage user with seller company assigned
    Given the user "tagarchiveuser" has logged in with role "UserSeller"

  Scenario: 0. create deals for further search tests
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisher_payload.json"
    And the user create a deal from the json file "jsons/genevacrud/deal/payload/create/DealWithInventory_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/DealWithInventory_ER.json"
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisher_payload.json"
    And the user create a deal from the json file "jsons/genevacrud/deal/payload/create/DealWithInventory2_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/DealWithInventory2_ER.json"

  Scenario Outline: 1. search for all deals associated with seller for Internal User
    Given the user "<user>" has logged in with role "<role>"
    When the user searches for all deals associated with seller by sellerPid "10213"
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealsForSeller_ER.json"

    Examples:
      | user        | role          |
      | gnnexagemgr | ManagerNexage |
      | NexageUser1 | UserNexage    |
      | nexageadmin | AdminNexage   |

  Scenario Outline: 1. search for all deals associated with seller for Seller User
    Given the user "<user>" has logged in with role "<role>"
    When the user searches for all deals associated with seller by sellerPid "10213"
    Then request passed successfully
    And returned "deals" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealsForSellerForSellerUser_ER.json"

    Examples:
      | user              | role          |
      | tagarchiveuser    | UserSeller    |
      | tagarchivemanager | ManagerSeller |
      | tagarchiveadmin   | AdminSeller   |

  Scenario: 2. search for all deals associated with other seller
    When the user searches for all deals associated with seller by sellerPid "1"
    Then "search deal" failed with "401" response code

  Scenario: 3. search for deals associated with seller by dealId
    When the user searches for deals associated with seller by sellerPid "10213" and qf "dealId", qt "InventoryDeal2"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealsForSellerUsingParams_ER.json"

  Scenario: 4. search for deals associated with seller by description
    When the user searches for deals associated with seller by sellerPid "10213" and qf "description", qt "Inventory Test Deal2"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealsForSellerUsingParams_ER.json"

  Scenario: 5. search for deals associated with seller by dealId and description
    When the user searches for deals associated with seller by sellerPid "10213" and qf "dealId, description", qt "Inventory Test Deal2"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/DealsForSellerUsingParams_ER.json"

  Scenario: 6. search for deals associated with seller by invalid qf
    When the user searches for deals associated with seller by sellerPid "10213" and qf "invalid qf", qt "Inventory Test Deal2"
    Then "search deal" failed with "400" response code

  Scenario: 7. search for deals associated with seller by invalid qt
    When the user searches for deals associated with seller by sellerPid "10213" and qf "dealId", qt ""
    Then "search deal" failed with "400" response code

  Scenario Outline: 8. find deal associated with seller by pid
    Given the user "<user>" has logged in with role "<role>"
    When the user gets deal associated with seller by sellerPid "10213" and dealPid "13"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/DealWithInventory2_ER.json"

    Examples:
      | user              | role          |
      | tagarchiveuser    | UserSeller    |
      | tagarchivemanager | ManagerSeller |
      | tagarchiveadmin   | AdminSeller   |
      | gnnexagemgr       | ManagerNexage |
      | NexageUser1       | UserNexage    |
      | nexageadmin       | AdminNexage   |

  Scenario Outline: 8. find deal associated with seller by pid for seller user
    Given the user "<user>" has logged in with role "<role>"
    When the user gets deal associated with seller by sellerPid "10213" and dealPid "12"
    Then "find deal" failed with "404" response code

    Examples:
      | user              | role          |
      | tagarchiveuser    | UserSeller    |
      | tagarchivemanager | ManagerSeller |
      | tagarchiveadmin   | AdminSeller   |

  Scenario Outline: 8. find deal associated with seller by pid for seller user
    Given the user "<user>" has logged in with role "<role>"
    When the user gets deal associated with seller by sellerPid "10213" and dealPid "12"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/DealWithInventory_ER.json"
    Examples:
      | user        | role          |
      | gnnexagemgr | ManagerNexage |
      | NexageUser1 | UserNexage    |
      | nexageadmin | AdminNexage   |

  Scenario: 9. find deal associated with seller by other seller dealPid
    When the user gets deal associated with seller by sellerPid "10213" and dealPid "1"
    Then "find deal" failed with "404" response code

  Scenario: 10. find deal associated with other seller
    When the user gets deal associated with seller by sellerPid "1" and dealPid "1"
    Then "find deal" failed with "401" response code

  Scenario: 11. create deal associated with seller
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    When the user create a deal associated with seller "10213" from the json file "jsons/genevacrud/deal/payload/create/seller/createDealAssociatedWithSeller_Payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/seller/createDealAssociatedWithSeller_ER.json"

  Scenario Outline: 12. create deal associated with seller Unauthorized
    Given the user "<user>" has logged in with role "<role>"
    When the user create a deal associated with seller "<seller>" from the json file "jsons/genevacrud/deal/payload/create/seller/createDealAssociatedWithSeller_Payload.json"
    Then "create deal" failed with "401" response code
    Examples:
      | user              | seller | role          |
      | tagarchiveuser    | 10213  | UserSeller    |
      | tagarchivemanager | 1      | ManagerSeller |
      | tagarchiveadmin   | 1      | AdminSeller   |

  Scenario Outline: 13. create deal associated with seller validation
    Given the user "<user>" has logged in with role "<role>"
    When the user create a deal associated with seller "<seller>" from the json file "jsons/genevacrud/deal/payload/create/seller/<payload>_Payload.json"
    Then "create deal" failed with "400" response code and error message "<message>"
    Examples:
      | user              | role          | seller | message                           | payload                                          |
      | tagarchiveadmin   | AdminSeller   | 10213  | Invalid deal category for seller. | createDealAssociatedWithSellerAndDealCategorySSP |
      | tagarchivemanager | ManagerSeller | 10213  | Multiple sellers on request.      | createDealAssociatedWithMultiSeller              |

  Scenario Outline: 14. update deal associated with seller
    Given the user "<user>" has logged in with role "<role>"
    Given the user searches for all deals associated with seller by sellerPid "10213"
    When the user update a deal associated with seller "10213" with Deal id "1622170524642691520" from the json file "jsons/genevacrud/deal/payload/update/seller/<payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/seller/<ER>.json"
    Examples:
      | user              | role          | payload                                | ER                                |
      | tagarchivemanager | ManagerSeller | updateDealAssociatedWithSeller_Payload | updateDealAssociatedWithSeller_ER |
      | nexageadmin       | AdminNexage   | updateDealCategory_Payload             | updateDealCategory_ER             |

  Scenario Outline: 15. update deal associated with seller Unauthorized
    Given the user "<user>" has logged in with role "<role>"
    Given the user searches for all deals associated with seller by sellerPid "10213"
    When the user update a deal associated with seller "<seller>" with Deal id "1622170524642691520" from the json file "jsons/genevacrud/deal/payload/update/seller/updateDealAssociatedWithSeller_Payload.json"
    Then "update deal" failed with "401" response code
    Examples:
      | user              | seller | role          |
      | tagarchiveuser    | 10213  | UserSeller    |
      | tagarchivemanager | 1      | ManagerSeller |
      | tagarchiveadmin   | 1      | AdminSeller   |

  Scenario Outline: 16. update deal associated with seller validation
    Given the user "<user>" has logged in with role "<role>"
    Given the user searches for all deals associated with seller by sellerPid "10213"
    When the user update a deal associated with seller "<seller>" with Deal id "1622170524642691520" from the json file "jsons/genevacrud/deal/payload/update/seller/<payload>_Payload.json"
    Then "update deal" failed with "400" response code and error message "<message>"
    Examples:
      | user              | role          | seller | message                           | payload                                           |
      | tagarchivemanager | ManagerSeller | 10213  | Multiple sellers on request.      | updateDealAssociatedWithMultiSeller               |
      | tagarchivemanager | ManagerSeller | 10213  | Deal category cannot be null.     | updateDealAssociatedWithSellerAndDealCategoryNull |

  Scenario Outline: 17. create deal associated with seller and placement formula
    Given the user "<user>" has logged in with role "<role>"
    When the user create a deal associated with seller "10213" from the json file "jsons/genevacrud/deal/payload/create/seller/createDealAssociatedWithSellerAndPlacementFormula_Payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/seller/createDealAssociatedWithSellerAndPlacementFormula_ER.json"
    Examples:
      | user              | role          |
      | tagarchivemanager | ManagerSeller |

  Scenario Outline: 18. create deal associated with an invalid seller
    Given the user "<user>" has logged in with role "<role>"
    When the user create a deal associated with seller "10213" from the json file "jsons/genevacrud/deal/payload/create/seller/createDealAssociatedWithInvalidSellerAndPlacementFormula_Payload.json"
    Then "create deal" failed with "400" response code and error message "Invalid Seller Name for Placement Formula."

    Examples:
      | user              | role          |
      | tagarchivemanager | ManagerSeller |

  Scenario Outline: 19. update deal associated with seller
    Given the user "<user>" has logged in with role "<role>"
    Given the user searches for all deals associated with seller by sellerPid "10213"
    When the user update a deal associated with seller "10213" with Deal id "1622170524642691520" from the json file "jsons/genevacrud/deal/payload/update/seller/<payload>.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/seller/<ER>.json"
    Examples:
      | user              | payload                                                   | ER                                                   | role          |
      | tagarchivemanager | updateDealAssociatedWithSellerAndPlacementFormula_Payload | updateDealAssociatedWithSellerAndPlacementFormula_ER | ManagerSeller |

  Scenario Outline: 20. update deal associated with invalid seller
    Given the user "<user>" has logged in with role "<role>"
    Given the user searches for all deals associated with seller by sellerPid "10213"
    When the user update a deal associated with seller "10213" with Deal id "InventoryDeal2" from the json file "jsons/genevacrud/deal/payload/update/seller/<payload>.json"
    Then "update deal" failed with "400" response code and error message "Invalid Seller Name for Placement Formula."

    Examples:
      | user              | role          | payload                                                          |
      | tagarchivemanager | ManagerSeller | updateDealAssociatedWithInvalidSellerAndPlacementFormula_Payload |
