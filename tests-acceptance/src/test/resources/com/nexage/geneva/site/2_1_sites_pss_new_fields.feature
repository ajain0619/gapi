Feature: Seller Sites: get, create, update sites as nexage or pss users with new fields migrated from seller api

  #Create a site using PSS API with new fields from Seller API as nexage admin (with all fields or only required) and nexage mananger
  Scenario Outline: create site with correct parameters for nexage admin or nexage manager
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "traffic_type"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<file_ER>.json"

    Examples:
      | file_payload                             | file_ER                             | user              | role          |
      | CreatePssSiteAllNewFieldsNA_payload      | CreatePssSiteAllNewFieldsNA_ER      | admin1c           | AdminNexage   |
      | CreatePssSiteRequiredNewFieldsNA_payload | CreatePssSiteRequiredNewFieldsNA_ER | admin1c           | AdminNexage   |
      | CreatePssSiteAllNewFieldsNM_payload      | CreatePssSiteAllNewFieldsNM_ER      | crudnexagemanager | ManagerNexage |

  #Create a site using PSS API with new fields from Seller API as nexage user
  Scenario: create site with correct parameters for nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteAllNewFieldsNU_payload.json"
    Then "create site" failed with "401" response code and error message "Unauthorized"

  #Get created sites using PSS API with new fields from Seller API as nexage admin, manager or user
  Scenario Outline: get site as nexage admin, manager or user
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "traffic_type"
    When the PSS user selects the site "mgb6site"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetPssSiteWithNewFields_ER.json"

    Examples:
      | user              | role          |
      | admin1c           | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |

  #Update a site using PSS API with new fields from Seller API as nexage admin or nexage manager (only required fields)
  Scenario Outline: update site with all or only required new parameters for nexage admin, manager
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "traffic_type"
    When the PSS user selects the site "<site_name>"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<file_ER>.json"

    Examples:
      | user                   | role               | site_name | file_payload                             | file_ER                             |
      | crudnexagemanageryield | ManagerYieldNexage | mgb6site  | UpdatePssSiteAllNewFieldsNA_payload      | UpdatePssSiteAllNewFieldsNA_ER      |
      | crudnexagemanageryield | ManagerYieldNexage | mgb8site  | UpdatePssSiteRequiredNewFieldsNM_payload | UpdatePssSiteRequiredNewFieldsNM_ER |

  #Update a site using PSS API without new fields from Seller API as nexage manager
  Scenario: update site without new parameters for nexage manager
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "traffic_type"
    When the PSS user selects the site "mgb7site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteWithoutNewFieldsNM_payload.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteWithoutNewFieldsNM_payload.json"
    Then "update site" failed with "500" response code

  #Update a site using PSS API with new fields from Seller API as nexage user
  Scenario: update site with all new parameters for nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Seller" company "traffic_type"
    When the PSS user selects the site "mgb7site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteAllNewFieldsNU_payload.json"
    Then "update site" failed with "401" response code and error message "Unauthorized"

  #Create a site using PSS API with new fields from Seller API as PSS admin
  Scenario: create site with correct parameters for pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteAllNewFieldsPA_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreatePssSiteAllNewFieldsPA_ER.json"

  #Update a site using PSS API with new fields from Seller API as PSS admin
  Scenario: update site with all new parameters for pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user selects the site "mgbPssite"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteAllNewFieldsPA_payload.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteAllNewFieldsPA_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetPssSiteAllNewFieldsPA_ER.json"

  #Get a site using PSS API with new fields from Seller API as PSS admin
  Scenario: get site as pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user selects the site "mgbPssite"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetPssSiteAllNewFieldsPA_ER.json"

  @restoreCrudCoreDatabaseBefore

  #Create a site using PSS API with new fields from Seller API as nexage admin and update this site as PSS admin
  Scenario: create site as nexage admin and update site with new parameters as pss admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteAllNewFieldsNA_payload.json"
    When the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the PSS user selects the site "mgb6site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteByPACreatedNA_payload.json"
    Then request passed successfully
    Then the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteByPACreatedNA_payload.json"
    And request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetPssSiteAfterUpdatePA_ER.json"

  #Create a site using PSS API with new fields from Seller API as PSS admin and update this site as nexage yield manager
  Scenario: create site as pss admin and update site with new parameters as nexage yield manager
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    And the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteAllNewFieldsPA_payload.json"
    When the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the PSS user selects the site "mgbPssite"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteByNACreatedPA_payload.json"
    Then request passed successfully
    Then the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteByNACreatedPA_payload.json"
    And request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdatePssSiteByNACreatedPA_ER.json"

  #Create a site using PSS API with new fields from Seller API with some invalid values in the fields
  Scenario Outline: create site with invalid parameter length
    Given the user "admin1c" has logged in with role "AdminNexage"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then "create site" failed with "400" response code and error message "<expected_message>"
    And site with the site name "<site_name>" is not generated

    Examples:
      | file_payload                             | site_name    | expected_message  |
      | CreatePssSiteIncEthnicityMap_payload     | ethinic_site | ethnicityMap      |
      | CreatePssSiteIncGenderMap_payload        | gender_site  | genderMap         |
      | CreatePssSiteIncDateFormat_payload       | date_site    | inputDateFormat   |
      | CreatePssSiteIncMaritalStatusMap_payload | marital_site | maritalStatusMap  |
