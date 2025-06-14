Feature: create, update, delete, search company using PSS API

  Scenario Outline: create a seller company
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"
    When the user sets the company type "<company_type>"
    Then company can be searched out in database
    And company data in database is correct

    Examples:
      | company_type | file_payload                     | file_ER                     |
      | Seller       | CreatePssSellerCompany2_payload  | CreatePssSellerCompany2_ER  |
      | Seller       | CreatePssSellerCompany_payload   | CreatePssSellerCompany_ER   |
      | Seller       | CreatePssSellerCompany3_payload  | CreatePssSellerCompany3_ER  |

  Scenario: create a buyer company with RTB profile
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/CreatePssBuyerCompanyRtb_payload.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/CreatePssBuyerCompanyRtb_ER.json"
    When the user sets the company type "Buyer"
    Then company can be searched out in database
    And company data in database is correct
    And set company "PssBuyerDisappointment"
    When company data is retrieved using pss
    And the user gets all bidder configs for newly created company
    Then returned "create bidder config" data matches the following json file "jsons/genevacrud/company/pss/expected_results/GetBidderConfigPssCompany_ER.json"

  Scenario Outline: get a buyer or seller company
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "<company_name>"
    When company data is retrieved using pss
    Then request passed successfully
    And returned "company data" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | company_name              | file_ER                      |
      | PssExternalDisappointment | GetPssSellerCompany2_ER      |
      | PssSellerDisappointment   | GetPssSellerCompany_ER       |
      | PssSellerDealRevShare     | GetPssSeller_DealRevShare_ER |
      | PssBuyerDisappointment    | GetPssBuyerCompany_ER        |

  Scenario Outline: update a seller company without seller attributes
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "<company_name>"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>_payload.json"
    Then "update company" failed with "400" response code and error message "The seller attributes is missing in the request."

    Examples:
      | file_payload            | company_name              |
      | UpdateSellerWithoutSA   | PssSellerDisappointment   |
      | UpdateSeller2WithoutSA  | PssExternalDisappointment |

  Scenario Outline: update a company with only necessary seller attributes
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "<company_name>"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | company_name              | file_payload                     | file_ER                     |
      | PssExternalDisappointment | UpdateSeller2RequiredSA_payload  | UpdateSeller2RequiredSA_ER  |
      | PssSellerDisappointment   | UpdateSellerRequiredSA_payload   | UpdateSellerRequiredSA_ER   |

  Scenario Outline: update a buyer or seller company with seller attributes
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And set company "<company_name>"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | company_name              | file_payload                                  | file_ER                                     |
      | PssExternalDisappointment | UpdatePssSellerCompany2_payload               | UpdatePssSellerCompany2_ER                  |
      | PssBuyerDisappointment    | UpdatePssBuyerCompany_payload                 | UpdatePssBuyerCompany_ER                    |
      | PssSellerDisappointment   | UpdatePssSellerCompany_payload                | UpdatePssSellerCompany_ER                   |
      | PssSellerDealRevShare     | UpdatePssSellerCompanyDealFeeRevShare_payload | UpdatePssSellerCompanyDealFeeRevShare_ER    |


  Scenario Outline: delete a buyer or seller company
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "<company_name>"
    When the user deletes a company using pss
    Then request passed successfully
    And the company status "-1" is correct for company "<company_name>"

    Examples:
      | company_name              |
      | PssExternalDisappointment |
      | PssSellerDisappointment   |
      | PssBuyerDisappointment    |
      | PssSellerDealRevShare     |

  Scenario: get a company as an external user
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And set company "adserverSellerTest8"
    When company data is retrieved using pss
    Then request passed successfully

  Scenario: update a company as an external user
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And set company "adserverSellerTest8"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/UpdatePssCompanyExternalUser_payload.json"
    Then "update company" failed with "401" response code and error message "You're not authorized to perform this operation."

  Scenario: delete a company as an external user
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And set company "adserverSellerTest8"
    When the user deletes a company using pss
    Then "delete company" failed with "401" response code and error message "You're not authorized to perform this operation."


  Scenario Outline: create seller or buyer company with wrong currency
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then "create company" failed with "400" response code and error message "Currency code is not supported."

    Examples:
      | file_payload                         |
      | CreatePssSellerCompany_payload_XXX   |
      | CreatePssBuyerCompanyRtb_payload_XXX |

  Scenario Outline: create seller or buyer company with currency
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"
    When the user sets the company type "<company_type>"
    Then company can be searched out in database
    And company data in database is correct
    And company has currency set to "JPY"

    Examples:
      | company_type | file_payload                         | file_ER                         |
      | Seller       | CreatePssSellerCompany_payload_JPY   | CreatePssSellerCompany_ER_JPY   |
      | Buyer        | CreatePssBuyerCompanyRtb_payload_JPY | CreatePssBuyerCompanyRtb_ER_JPY |

  Scenario Outline: create seller company with seller seat assignment
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "company with seller seat" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                  |  file_ER                                |
      | CreatePssSellerCompanyWithSellerSeat_payload  | CreatePssSellerCompanyWithSellerSeat_ER |

  Scenario Outline: get a seller company with seller seat assignment
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "<company_name>"
    When company data is retrieved using pss
    Then request passed successfully
    And returned "company data" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | company_name              | file_ER                  |
      | PssSellerWithSellerSeat   | GetPssSellerCompanyWithSellerSeat_ER   |

  Scenario: update a company as an admin user with currency change
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "PssSellerDisappointmentJPY"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/UpdatePssSellerCompany_payload_JPY.json"
    Then "update company" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"

  @restoreCrudCoreDatabaseBefore

  Scenario: create a company as an external user
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/CreatePssSellerCompany2_payload.json"
    Then "create company" failed with "401" response code and error message "You're not authorized to perform this operation."

  Scenario: get a non-existing company
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects non existing company using pss
    Then "get company" failed with "404" response code and error message "Company doesn't exist in database."

  Scenario: update a non-existing company
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user updates a non-existing company using pss from the json file "jsons/genevacrud/company/pss/payload/UpdatePssNonExistingCompany_payload.json"
    Then "update company" failed with "404" response code and error message "Company doesn't exist in database."

  Scenario: delete a non-existing company
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user deletes a non-existing company using pss
    Then "delete company" failed with "404" response code and error message "Company doesn't exist in database."

  Scenario: delete a company which have sites
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "adserverSellerTest8"
    When the user deletes a company using pss
    Then request passed successfully
    And the company status "-1" is correct for company "adserverSellerTest8"

  Scenario Outline: create a company with incorrect parameters data
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then "create company" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"

    Examples:
      | file_payload                                   |
      | CreatePssCompanyIncorrectReportingApi_payload  |
      | CreatePssCompanyIncorrectAdServing_payload     |
      | CreatePssCompanyIncorrectRestrictDrill_payload |
      | CreatePssCompanyIncorrectReportingType_payload |
      | CreatePssCompanyIncorrectCpiTracking_payload   |
      | CreatePssCompanyIncorrectRtbEn_payload         |
      | CreatePssCompanyIncorrectMediationEn_payload   |
      | CreatePssCompanyIncorrectRtbRevenue_payload    |
      | CreatePssCompanyIncorrectStatus_payload        |
      | CreatePssCompanyIncorrectType_payload          |
      | CreatePssCompanyIncorrectTest_payload          |
      | CreatePssCompanyIncorrectSelfServe_payload     |
      | CreatePssCompanyIncorrectPayoutEn_payload      |

  Scenario Outline: create a company with incorrect parameters length values
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"

    Examples:
      | file_payload                                 | field_errors                                                               |
      | CreatePssCompanyIncorrectNoticeUrl_payload   | {"cpiConversionNoticeUrl":"size must be between 0 and 200"}                |
      | CreatePssCompanyIncorrectSalesforce_payload  | {"salesforceId":"size must be between 0 and 100"}                          |
      | CreatePssCompanyIncorrectGlobalAlias_payload | {"globalAliasName":"size must be between 0 and 100"}                       |
      | CreatePssCompanyIncorrectWebsite_payload     | {"website":"size must be between 0 and 100"}                               |
      | CreatePssCompanyIncorrectDescription_payload | {"description":"size must be between 0 and 255"}                           |
      | CreatePssCompanyIncorrectDirectFee_payload   | {"directAdServingFee":"must be less than or equal to 99999999.99999999"}   |
      | CreatePssCompanyIncorrectHouseFee_payload    | {"houseAdServingFee":"must be less than or equal to 99999999.99999999"}    |
      | CreatePssCompanyIncorrectRemnant_payload     | {"nonRemnantHouseAdCap":"must be less than or equal to 99999999.99999999"} |
      | CreatePssCompanyIncorrectOverageFee_payload  | {"houseAdOverageFee":"must be less than or equal to 99999999.99999999"}    |
      | CreatePssCompanyIncorrectBidderFee_payload   | {"bidderAdServingFee":"must be less than or equal to 9999999.99"}          |

  Scenario Outline: create a company with incorrect region or user id
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then "create company" failed with "400" response code and error message "<error_message>"

    Examples:
      | file_payload                               | error_message                  |
      | CreatePssCompanyIncorrectRegion_payload    | Unknown region                 |
      | CreatePssCompanyIncorrectContactId_payload | User doesn't exist in database |


  Scenario Outline: update company pfo (price floor optimization) enable and check tags alter reserve values
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "Seller PR-8826"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And the rtbProfile alter reserves for company equals "<alter_reserve>"

    Examples:
      | file_payload                     | alter_reserve |
      | UpdateCompanyPfoEnabled_payload  | 1             |
      | UpdateCompanyPfoDisabled_payload | 0             |


  Scenario Outline: create a company with defaultRtbProfilesEnabled flag
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                           | file_ER                                           |
      | CreatePssCompanyDefaultRtbProfilesEnabledNull_payload  | CreatePssCompanyDefaultRtbProfilesEnabledNull_ER  |
      | CreatePssCompanyDefaultRtbProfilesEnabledTrue_payload  | CreatePssCompanyDefaultRtbProfilesEnabledTrue_ER  |
      | CreatePssCompanyDefaultRtbProfilesEnabledFalse_payload | CreatePssCompanyDefaultRtbProfilesEnabledFalse_ER |


  Scenario Outline: update a company with defaultRtbProfilesEnabled flag was set
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "<company_name>"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then "Update company" failed with "400" response code and error message "<error_message>"

    Examples:
      | company_name                           | file_payload                                           | error_message                                           |
      | PssSellerDefaultRtbProfilesEnabledTrue | UpdatePssCompanyDefaultRtbProfilesEnabledFalse_payload | Default RTB Profile enabled flag can not be updated.    |
