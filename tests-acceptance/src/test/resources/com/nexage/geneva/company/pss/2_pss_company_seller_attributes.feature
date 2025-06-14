#Contains some specific cases with seller attributes for a company (publisher)
Feature: create, update, delete, search company using PSS API with seller attributes combinations

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

      #Creates a company with seller attribues values are not in the correct format
  Scenario Outline: create a company with incorrect values
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then "create company" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"

    Examples:
      | file_payload                                   |
      | CreateCompanySeAtIncorrectThEnabled_payload    |
      | CreateCompanySeAtIncorrectPfoEnabled_payload   |
      | CreateCompanySeAtIncorrectLimitEnabled_payload |
      | CreateCompanySeAtIncorrectSAEnabled_payload    |

  #Creates a company with seller attributes values that are not allowed (too big)
  Scenario Outline: create a company with incorrect parameters length values
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "<field_errors>"

    Examples:
      | file_payload                                         | field_errors                                                                   |
      | CreateCompanySeAtIncorrectRevShare_payload           | {"attributes.revenueShare":"must be less than or equal to 99999999.99999999"}  |
      | CreateCompanySeAtIncorrectThPerc_payload             | {"attributes.hbThrottlePercentage":"must be less than or equal to 100"}        |
      | CreateCompanySeAtIncorrectSiteLimit_payload          | {"attributes.siteLimit":"must be less than or equal to 32767"}                 |
      | CreateCompanySeAtIncorrectPosLimit_payload           | {"attributes.positionsPerSiteLimit":"must be less than or equal to 32767"}     |
      | CreateCompanySeAtIncorrectTagLimit_payload           | {"attributes.tagsPerPositionLimit":"must be less than or equal to 32767"}      |
      | CreateCompanySeAtIncorrectCamLimit_payload           | {"attributes.campaignsLimit":"must be less than or equal to 32767"}            |
      | CreateCompanySeAtIncorrectCreativesLimit_payload     | {"attributes.creativesPerCampaignLimit":"must be less than or equal to 32767"} |
      | CreateCompanySeAtIncorrectBidderLimit_payload        | {"attributes.bidderLibrariesLimit":"must be less than or equal to 99999"}      |
      | CreateCompanySeAtIncorrectBlockLimit_payload         | {"attributes.blockLibrariesLimit":"must be less than or equal to 99999"}       |
      | CreateCompanySeAtIncorrectUserLimit_payload          | {"attributes.userLimit":"must be less than or equal to 32767"}                 |
      | CreateCompanySeAtIncorrectPricePref_payload          | {"attributes.hbPricePreference":"must be less than or equal to 65535"}         |
      | CreateCompanySeAtIncorrectJointDealRevShare_payload  | {"attributes.jointDealRevShare":"must be less than or equal to 1.00"}          |
      | CreateCompanySeAtIncorrectSSPDealRevShare_payload    | {"attributes.sspDealRevShare":"must be less than or equal to 1.00"}            |
      | CreateCompanySeAtIncorrectSellerDealRevShare_payload | {"attributes.sellerDealRevShare":"must be less than or equal to 1.00"}         |

  Scenario: create a buyer company with seller attributes
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/CreateBuyerSeAt_payload.json"
    Then "create company" failed with "400" response code and error message "The seller attributes is not allowed"

  Scenario Outline: create a seller or external company without seller attributes
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then "create company" failed with "400" response code and error message "The seller attributes is missing in the request"

    Examples:
      | file_payload                    |
      | CreateSellerWithoutSA_payload   |
      | CreateExternalWithoutSA_payload |

  Scenario Outline: create a seller company with different seller attributes being populated
    Given the user "crudnexagemanagersmartex" has logged in with role "ManagerSmartexNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"
    When the user sets the company type "<company_type>"
    Then company can be searched out in database
    And company data in database is correct

    Examples:
      | company_type | file_payload                                             | file_ER                                             |
      | Seller       | CreatePssSellerCompanyWithDynamicFloorEnabled_payload    | CreatePssSellerCompanyWithDynamicFloorEnabled_ER    |
      | Seller       | CreatePssSellerCompanyWithDealFeeRevShareEnabled_payload | CreatePssSellerCompanyWithDealFeeRevShareEnabled_ER |
      | Seller       | CreatePssSellerCompanyWithCustomDealFloorEnabled_payload | CreatePssSellerCompanyWithCustomDealFloorEnabled_ER |

  Scenario Outline: update a seller company with seller attribute dynamic floor enabled
    Given the user "crudnexagemanagersmartex" has logged in with role "ManagerSmartexNexage"
    And set company "<company_name>"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | company_name            | file_payload                               | file_ER                               |
      | PssExternalDynamicFloor | UpdatePssSellerCompanyDynamicFloor_payload | UpdatePssSellerCompanyDynamicFloor_ER |

  Scenario Outline: update a seller company with seller attribute custom deal floor enabled
    Given the user "crudnexagemanagersmartex" has logged in with role "ManagerSmartexNexage"
    And set company "<company_name>"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/<file_ER>.json"

    Examples:
      | company_name            | file_payload                                             | file_ER                                             |
      | PssCustomDealFloor      | UpdatePssSellerCompanyWithCustomDealFloorEnabled_payload | UpdatePssSellerCompanyWithCustomDealFloorEnabled_ER |

  Scenario: create a company with seller attribute dynamic floor enabled and incorrect role
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    When the user creates a company using pss from the json file "jsons/genevacrud/company/pss/payload/CreatePssSellerCompanyWithDynamicFloorEnabled2_payload.json"
    Then "create company" failed with "401" response code and error message "You're not authorized to perform this operation."
