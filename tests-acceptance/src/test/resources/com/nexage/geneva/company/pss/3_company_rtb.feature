Feature: create, update, read a company with RTB profiles

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: create an external or seller company
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/rtb/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/rtb/expected_results/<file_ER>.json"
    When the user sets the company type "<company_type>"
    Then company can be searched out in database
    And there are "1" rtb profiles for the company "<company_name>"
    And there is/are only "1" tags for tag name "<tag_name>"
    And tag "<tag_name>" has correct owner value "<tag_owner>"

    Examples:
      | company_type | company_name | file_payload                        | file_ER                        | tag_name | tag_owner |
      | Seller       | OnlyTag      | CreateCompanyRTBOnlyTagInfo_payload | CreateCompanyRTBOnlyTagInfo_ER | Test TWO | 1         |
      | Seller       | haruko       | CreateCompanyNewRTB_payload         | CreateCompanyNewRTB_ER         | Ineludi  | 1         |

  Scenario Outline: get a company as nexage mgr or nexage user
    Given the user "<userlogin>" has logged in with role "<role>"
    And set company "OnlyTag"
    When company data is retrieved using pss
    Then request passed successfully
    And returned "company data" data matches the following json file "jsons/genevacrud/company/pss/rtb/expected_results/<file_ER>.json"

    Examples:
      | userlogin         | role          | file_ER                    |
      | crudnexagemanager | ManagerNexage | GetCompanyRTB_ER           |
      | crudnexageuser    | UserNexage    | GetCompanyRTBNexageUser_ER |

  Scenario: get a company as an external admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And set company "OnlyTag"
    When company data is retrieved using pss
    Then "get company" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario Outline: update a company as an external admin or nexage user
    Given the user "<userlogin>" has logged in with role "<role>"
    And set company "OnlyTag"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/rtb/payload/UpdateCompanyRTBCorrectData_payload.json"
    Then "update company" failed with "401" response code and error message "You're not authorized to perform this operation"

    Examples:
      | userlogin      | role        |
      | pssSellerAdmin | AdminSeller |
      | crudnexageuser | UserNexage  |

  Scenario Outline: update a company with RTB pid belonging to the other company, non-existing RTB pid or updating tag pid
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "haruko"
    Given the defaultRTBProfilesFlag for company "haruko" is set to "true"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/rtb/payload/<file_payload>.json"
    Then "update company" failed with "<expected_http_status>" response code and error message "<error_message>"

    Examples:
      | file_payload                           | expected_http_status | error_message                                                  |
      | UpdateCompanyAssignedRTB_payload       | 404                  | Default RTB Profile record not found                           |
      | UpdateCompanyNonExistingRTB_payload    | 404                  | Default RTB Profile record not found                           |
      | UpdateCompanyRTBExistingTagPid_payload | 400                  | Alter or change of Tag for existing RTB Profile is not allowed |

  Scenario Outline: update a company RTB profile
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "OnlyTag"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/rtb/payload/<file_request_response>_payload.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/rtb/expected_results/<file_request_response>_ER.json"
    And there are "1" rtb profiles for the company "OnlyTag"

    Examples:
      | file_request_response       |
      | UpdateCompanyRTBCorrectData |
      | UpdateCompanyRTBAllData     |

  Scenario Outline: create a company with empty RTB, with already assigned RTB or Buyer company with RTB
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/rtb/payload/<file_payload>.json"
    Then "create company" failed with "400" response code and error message "<error_message>"

    Examples:
      | file_payload                           | error_message                                 |
      | CreateCompanyEmptyRTB_payload          | Default RTB Profile tag not available         |
      | CreateCompanyAssignedRTB_payload       | New publisher cannot use existing RTB Profile |
      | CreateCompanyRTBAssignedTagPid_payload | Tag has invalid entry for default RTB Profile |

  Scenario Outline: create a company with RTB profile as an external admin or nexage user
    Given the user "<userlogin>" has logged in with role "<role>"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/rtb/payload/CreateCompanyNewRTB_payload.json"
    Then "create company" failed with "401" response code and error message "You're not authorized to perform this operation"

    Examples:
      | userlogin      | role        |
      | pssSellerAdmin | AdminSeller |
      | crudnexageuser | UserNexage  |
