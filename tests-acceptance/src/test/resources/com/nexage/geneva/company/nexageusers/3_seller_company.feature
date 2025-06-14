Feature: create, update, delete, search Seller company as nexage users

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: create a Seller company
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"
    And company can be searched out in database
    And company data in database is correct

    Examples:
      | user              | role          | file_payload                                           | file_ER                                           |
      | crudnexageadmin   | AdminNexage   | CreateAllFeatureDisabled_payload                       | CreateAllFeatureDisabled_ER                       |
      #  for direct publisher access:
      | crudnexagemanager | ManagerNexage | CreateAllFeatureEnabled_payload                        | CreateAllFeatureEnabled_ER                        |
      #  for indirect third party access:
      | crudnexagemanager | ManagerNexage | CreateAllFeatureEnabledReportingTypeRestricted_payload | CreateAllFeatureEnabledReportingTypeRestricted_ER |

  Scenario: delete a Seller company
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Rinky"
    When the user deletes the selected company
    Then request passed successfully
    And deleted company can not be searched out
    And company can not be searched out in database

  Scenario Outline: search an existing Seller company
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "111 DJG Seller"
    When the company data is retrieved
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/seller/SearchPid_ER.json"

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |

  Scenario Outline: search non existing Seller company will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user selects non existing "Seller" company
    Then "company search" failed with "404" response code

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |

  Scenario Outline: create a Seller company with missing field will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/CreateCompanyWithMissing<field name>_payload.json"
    Then "created seller" failed with "400" response code

    Examples:
      | user              | role          | field name   |
      | crudnexageadmin   | AdminNexage   | Name         |
      | crudnexagemanager | ManagerNexage | Website      |
      | crudnexageadmin   | AdminNexage   | RevenueShare |
      | crudnexagemanager | ManagerNexage | RtbFee       |

  Scenario Outline: update Seller company with different parameters
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "<company name>"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"

    Examples:
      | user                  | role               | company name              | file_payload                                       | file_ER                                       |
      | crudnexagemanageryield| ManagerYieldNexage | adserverSellerTest8       | UpdateTextBox_payload                              | UpdateTextBox_ER                              |
      | crudnexagemanager     | ManagerNexage      | sriniseller7              | UpdateReportingTypeRestricted2Unrestricted_payload | UpdateReportingTypeRestricted2Unrestricted_ER |
      | crudnexageadmin       | AdminNexage        | test_rtb                  | UpdateCheckboxUnChecked2Checked_payload            | UpdateCheckboxUnChecked2Checked_ER            |
      | crudnexagemanager     | ManagerNexage      | test_rtb                  | UpdateCheckboxChecked2Unchecked_payload            | UpdateCheckboxChecked2Unchecked_ER            |
      | crudnexageadmin       | AdminNexage        | adserverSellerTest8update | UpdateSellerAttributes_payload                     | UpdateSellerAttributes_ER                     |
      | crudnexageadmin       | AdminNexage        | adserverSellerTest8update | UpdatePublisherDataProtectionRole_payload          | UpdatePublisherDataProtectionRole_ER          |

  # MX-1403
  Scenario: update Seller company from pss and verify the hbthrottle fields didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8update"
    And the user updates a publisher from the json file "jsons/genevacrud/company/payload/seller/UpdateSellerFromPssToCheckHeaderBiddingThrottleFields.json"
    Then request passed successfully
    And the hbthrottle fields are preserved in the db

  # MX-2276
  Scenario: update Seller company from pss and verify the hbPricePreference field didnt change
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8update"
    And the user updates a publisher from the json file "jsons/genevacrud/company/payload/seller/UpdateSellerFromPssToCheckHbPricePreferenceField.json"
    Then request passed successfully
    And the hbPricePreference field is preserved in the db


  Scenario Outline: delete a Seller company which has a site will fail
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "111 DJG Seller"
    When the user deletes the selected company
    Then "company deletion" failed with "400" response code

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |

  # scenario below fails due to environment issue
  # Currently if a user attempts to remove the site or seller alias name it should make the field in the db null,
  # but instead it is an empty string
  # https://jira.nexage.com/browse/KS-5332
  @unstable
  Scenario Outline: deletes a Seller company alias name and check if it`s null
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "alias test"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/DeleteAlias_payload.json"
    Then request passed successfully
    And the company alias name was removed

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |

  # MX-1403
  Scenario Outline: create a Seller company with invalid header_bidding_throttle_percent field will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/CreateCompanyWithInvalidHBThrottlePercent<number>_payload.json"
    Then "created seller" failed with "400" response code and error message "Header bidding throttling percentage is less than 0 or greater than 100"

    Examples:
      | user              | role          | number |
      | crudnexageadmin   | AdminNexage   | 1      |
      | crudnexagemanager | ManagerNexage | 2      |

  # MX-1403
  @restoreCrudCoreDatabaseBefore
  Scenario Outline: update Seller company with different parameters
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateInvalidHeaderBiddingThrottlePercentage<number>_payload.json"
    Then "updated seller" failed with "400" response code and error message "Header bidding throttling percentage is less than 0 or greater than 100"

    Examples:
      | user              | role          | number |
      | crudnexageadmin   | AdminNexage   | 1      |
      | crudnexagemanager | ManagerNexage | 2      |
