Feature: buyer group user privilege

  @restoreCrudCoreDatabaseBefore

  Scenario Outline: Internal (Nexage) and External (Seller) users can read data (get all buyer groups)
    Given the user "<user>" has logged in with role "<role>"
    When the user gets all buyer groups for a buyer "10024"
    Then request passed successfully

    Examples:
      | user              | role          |
      | admin1c           | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |
      | tagarchiveadmin   | AdminSeller   |
      | tagarchivemanager | ManagerSeller |
      | tagarchiveuser    | UserSeller    |


  Scenario: Internal (Nexage) user cannot create buyer group
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user creates buyer group from the json file "jsons/genevacrud/buyergroup/payload/1_CreateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.


  Scenario: Internal (Nexage) user cannot update buyer group
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user attempts to send a buyer group update request with the json file "jsons/genevacrud/buyergroup/payload/2_UpdateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.


  Scenario: External (MedFTBuyer) user cannot read data (get all buyer groups)
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user gets all buyer groups
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.


  Scenario: External (MedFTBuyer) user cannot create buyer group
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user creates buyer group from the json file "jsons/genevacrud/buyergroup/payload/1_CreateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.


  Scenario: External (MedFTBuyer) user cannot update buyer group
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user attempts to send a buyer group update request with the json file "jsons/genevacrud/buyergroup/payload/2_UpdateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

  Scenario: Internal (Nexage) user cannot create buyer group V1 api
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user creates buyer group using V1 api from the json file "jsons/genevacrud/buyergroup/payload/1_CreateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

  Scenario: External (MedFTBuyer) user cannot create buyer group using V1 api
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user creates buyer group using V1 api from the json file "jsons/genevacrud/buyergroup/payload/1_CreateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

  Scenario: Internal (Nexage) user cannot create buyer group V1 api
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user updates buyer group with pid "1" using V1 api from the json file "jsons/genevacrud/buyergroup/payload/1_UpdateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

  Scenario: External (MedFTBuyer) user cannot create buyer group using V1 api
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user updates buyer group with pid "1" using V1 api from the json file "jsons/genevacrud/buyergroup/payload/1_UpdateBuyerGroup_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.
