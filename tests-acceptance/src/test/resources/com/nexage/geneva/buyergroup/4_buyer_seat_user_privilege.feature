Feature: buyer seat user privilege


  Scenario Outline: Internal (Nexage) and External (Seller) user can read data (get all buyer seats)
    Given the user "<user>" has logged in with role "<role>"
    When the user gets all buyer seats for a buyer "10024"
    Then request passed successfully

    Examples:
      | user              | role          |
      | admin1c           | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |
      | tagarchiveadmin   | AdminNexage   |
      | tagarchivemanager | ManagerNexage |
      | tagarchiveuser    | UserNexage    |

  Scenario: Internal (Nexage) user cannot update buyer seat
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user attempts to send a buyer seat update request with the json file "jsons/genevacrud/buyerseat/payload/2_UpdateSeatOne_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

  Scenario: External (MedFTBuyer) user cannot read data (get all buyer seats)
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user gets all buyer seats
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

  Scenario: External (MedFTBuyer) user cannot update buyer seat
    Given the user "buyeruser2" has logged in with role "UserBuyer"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user attempts to send a buyer seat update request with the json file "jsons/genevacrud/buyerseat/payload/2_UpdateSeatOne_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.
