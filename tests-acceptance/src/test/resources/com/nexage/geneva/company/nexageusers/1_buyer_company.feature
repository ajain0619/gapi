Feature: create, update, delete, search Buyer company as nexage users

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: search an existing Buyer company
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Buyer" company "AdFonic"
    When the company data is retrieved
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/SearchPid_ER.json"

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |

  Scenario Outline: search an existing Buyer company by name containing a string
    Given the user searches all type "<type>" companies with query field "<queryField>" and query term "<queryTerm>"
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/<expected_results>.json"

    Examples:
      | type  | queryField | queryTerm   | expected_results                                           |
      | BUYER | name      | AdFonic     | SearchBuyerTypeWithNameFieldContainingStringAdFonic_ER     |
      | BUYER | name      | DataXu      | SearchBuyerTypeWithNameFieldContainingStringDataXu_ER      |
      | BUYER | name      | test_PR4011 | SearchBuyerTypeWithNameFieldContainingStringTest_PR4011_ER |

  Scenario: search non existing Buyer company will fail
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user selects non existing "Buyer" company
    Then "company search" failed with "404" response code

  Scenario Outline: update Buyer company with different parameters
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Buyer" company "<company name>"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/buyer/<file_payload>.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/<file_ER>.json"

    Examples:
      | user              | role          | company name | file_payload                            | file_ER                            |
      | crudnexageadmin   | AdminNexage   | cpi buyer    | UpdateTextBox_payload                   | UpdateTextBox_ER                   |
      | crudnexagemanager | ManagerNexage | asdf         | UpdateCheckboxUnChecked2Checked_payload | UpdateCheckboxUnChecked2Checked_ER |
      | crudnexagemanager | ManagerNexage | asdf         | UpdateCheckboxChecked2Unchecked_payload | UpdateCheckboxChecked2Unchecked_ER |

  Scenario Outline: create a Buyer company with different parameters
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Buyer" company from the json file "jsons/genevacrud/company/payload/buyer/<file_payload>.json"
    Then request passed successfully
    And returned "created buyer" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/<file_ER>.json"
    And company can be searched out in database
    And company data in database is correct
    And company has currency set to "<currency>"

    Examples:
      | user              | role          | file_payload                        | file_ER                        | currency |
      | crudnexageadmin   | AdminNexage   | CreateAllFeatureDisabled_payload    | CreateAllFeatureDisabled_ER    | USD      |
      | crudnexagemanager | ManagerNexage | CreateAllFeatureEnabled_payload     | CreateAllFeatureEnabled_ER     | USD      |
      | crudnexagemanager | ManagerNexage | CreateAllFeatureEnabled_NOK_payload | CreateAllFeatureEnabled_NOK_ER | NOK      |

  Scenario Outline: create a Buyer company with missing field will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user creates a "Buyer" company from the json file "jsons/genevacrud/company/payload/buyer/CreateCompanyWithMissing<field name>_payload.json"
    Then "company creation" failed with "400" response code

    Examples:
      | user              | role          | field name |
      | crudnexageadmin   | AdminNexage   | Name       |
      | crudnexagemanager | ManagerNexage | Website    |

  # scenarios below fails due to environment issue.
  # error 500 occurs when trying to delete buyer that contains ad cards
  # https://jira.nexage.com/browse/KS-2985
  @unstable
  Scenario: delete a Buyer company
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "Buyer1"
    When the user deletes the selected company
    Then request passed successfully
    And deleted company can not be searched out
    And company can not be searched out in database

  Scenario Outline: delete a Buyer company which has Adnets will fail
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Buyer" company "MedFTBuyer"
    When the user deletes the selected company
    Then "company deletion" failed with "500" response code

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |

  Scenario Outline: search Buyer companies by prefix
    Given the user "<user>" has logged in with role "<role>"
    When the user searches "Buyer" companies by prefix "RTB"
    Then request passed successfully
    And returned "buyer companies by prefix" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/SearchBuyersPrefix_ER.json"

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | ManagerNexage |

  Scenario: create a Buyer company with rtb profile should create correct bidderconfig values.
    Given the user "crudnexagemanager" has logged in with role "AdminNexage"
    When the user creates a "Buyer" company from the json file "jsons/genevacrud/company/payload/buyer/CreateCompanyRTBEnabled_payload.json"
    Then request passed successfully
    And returned "created buyer" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/CreateCompanyRTBEnabled_ER.json"
    And company can be searched out in database
    And company data in database is correct
    When the user selects the "Buyer" company "testBidderConfig"
    And the user gets all bidder configs for newly created company
    Then returned "create bidder config" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/CreateBidderConfig_ER.json"
