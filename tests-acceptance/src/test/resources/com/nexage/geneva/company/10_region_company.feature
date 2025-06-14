Feature: create, update, delete, search company with new region field via seller API

  #Create a new company with region fulfilled
  Scenario: Create a company with region as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/companyRegions/CreateCompanyWithRegion_payload.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/companyRegions/CreateCompanyWithRegion_ER.json"
    And company can be searched out in database
    And company data in database is correct
    And region id for company "regionCompany" equals to "1"

  #Update a created company with region and existing one also with region
  Scenario Outline: Update a company with region as nexage manager
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "<company_name>"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/companyRegions/<file_payload>.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/company/expected_results/companyRegions/<file_ER>.json"
    And region id for company "<company_name>" equals to "<region_id>"

    Examples:
      | company_name        | file_payload                     | file_ER                     | region_id |
      | regionCompany       | UpdateCompanyWithRegion1_payload | UpdateCompanyWithRegion1_ER | 1         |
      | adserverSellerTest8 | UpdateCompanyWithRegion2_payload | UpdateCompanyWithRegion2_ER | 5         |

  #Get a company with region as nexage admin or nexage user
  Scenario Outline: Get a company with region as nexage admin or nexage user
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "<company_name>"
    When the company data is retrieved
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/companyRegions/<file_ER>.json"
    And region id for company "<company_name>" equals to "<region_id>"

    Examples:
      | user           | role         | company_name        | file_ER                               | region_id |
      | admin1c        | AdminNexage  | regionCompany       | GetRegionCompanyNexageAdmin_ER        | 1         |
      | crudnexageuser | ManagerNexage| adserverSellerTest8 | GetRegionCompanyNexageUserPssAdmin_ER | 5         |

  #Get a company with region as pss user
  Scenario: Get a company with region as pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the company data is retrieved
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/companyRegions/GetRegionCompanyNexageUserPssAdmin_ER.json"

  #Delete a company with region
  Scenario: Delete a company with region
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "regionCompany"
    When the user deletes the selected company
    Then request passed successfully
    And deleted company can not be searched out
    And company can not be searched out in database

  #Create a company with incorrect region id (id does not belong to the list of available ids)

  Scenario: Create a company with incorrect region id
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/companyRegions/CreateCompanyIncorrectRegion_payload.json"
    Then the request failed with http status "400" errorcode "1252" and message "Unknown region."

  #Create a company with invalid region id (incorrect format id)
  Scenario: Create a company with invalid region id
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/companyRegions/CreateCompanyInvalidRegion_payload.json"
    Then the request failed with http status "400" errorcode "2004" and message "Bad Request. Check your request parameters (json format, type..)"

  #Update a company with incorrect region id (id does not belong to the list of available ids)
  Scenario: Update a company with incorrect region id
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/companyRegions/UpdateCompanyIncorrectRegion_payload.json"
    Then the request failed with http status "400" errorcode "1252" and message "Unknown region."

  #Update a company with region as PSS admin
  @restoreCrudCoreDatabaseBefore

  Scenario: Update a company with region as pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the user updates a company from the json file "jsons/genevacrud/company/payload/companyRegions/UpdateCompanyWithRegion2_payload.json"
    Then "update company" failed with "401" response code and error message "You're not authorized to perform this operation."

  #Gets all region values as nexage admin
  Scenario: Get all possible regions as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user gets all available regions
    Then request passed successfully
    And returned "Regions" data matches the following json file "jsons/genevacrud/region/expected_results/GetAllRegions_ER.json"

  #Gets all region values as pss admin
  Scenario: Get all possible regions as pss admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user gets all available regions
    Then "get regions" failed with "401" response code and error message "You're not authorized to perform this operation."
