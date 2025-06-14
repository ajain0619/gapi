Feature: \search, update PFO switch for existing Seller company as nexage users

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: search an existing Seller company - Price Floor Optimization defaulted to OFF
    # http://geneva.sbx:8080/geneva/companies/105
    # 1. Seller 105 - Existing Publisher defaults to OFF
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the company data is retrieved
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/seller/SearchPid105_ER.json"

    Examples:
      | user              | role          |
      | crudnexageadmin   | AdminNexage   |
      | crudnexagemanager | ManagerNexage |
      | crudnexageuser    | UserNexage    |

  Scenario Outline: search an existing seller seatholder or nexage company by name containing a string
    Given the user searches all type "<type>" companies with query field "<queryField>" and query term "<queryTerm>"
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<expected_results>.json"

    Examples:
      | type       | queryField | queryTerm      | expected_results                                                 |
      | SELLER     | name       | Provision      | SearchSellerTypeWithNameFieldContainingStringProvision_ER        |
      | SELLER     | name       | Cooler Screens | SearchSellerTypeWithNameFieldContainingStringCoolerScreens_ER    |
      | SELLER     | name       | Burst Media    | SearchSellerTypeWithNameFieldContainingStringBurstMedia_ER       |
      | SEATHOLDER | name       | 1              | SearchSeatholderTypeWithNameFieldContainingStringSeatholder_1_ER |
      | NEXAGE     | name       | Nexage Inc     | SearchNexageTypeWithNameFieldContainingStringNexage_Inc_ER       |

  Scenario: update Seller company with Price Floor Optimization enabled (switch to ON)
    # http://geneva.sbx:8080/geneva/companies/105
    # 2. Seller 105 - Existing Publisher switch/update to ON
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdatePFO_ON_payload.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdatePFO_ON_ER.json"

  Scenario: search an existing site 963 of a Seller company - check site, position and tags Price Floor Optimization should be all ON
    # http://geneva.sbx:8080/geneva/sellers/sites/963
    # 2.2.3 Check all tag for site 963 - all should be ONLY_IF_HIGHER
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8B"
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/company/expected_results/seller/GetSite963ON_ER.json"

  Scenario: create a publisher tag with different set of fields - this should inherit company switch which is ON
    #POST http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag
    # 2.2.4
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/company/payload/seller/NewExchangeTag_105_Publisher.json"
    Then request passed successfully with code "201"
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/company/expected_results/seller/NewExchangeTag_105_Publisher_ER.json"

  Scenario: update Seller company with Price Floor Optimization disabled (switch to OFF)
    # http://geneva.sbx:8080/geneva/companies/105
    # 2.5 Seller 105 - Existing Publisher switch/update to OFF
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdatePFO_payload_OFF.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdatePFO_OFF_ER.json"

  Scenario: update Seller company with Price Floor Optimization disabled (switch to OFF) using crudnexageuser
    # http://geneva.sbx:8080/geneva/companies/105
    # 2.8 Update company PFO using crudnexageuser
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    When Seller selects the company with id "105" and type "Seller"
    And the user updates a company without permissions for the user from the json file "jsons/genevacrud/company/payload/seller/UpdatePFO_NexageUser_payload_OFF.json"
    Then "Company Update" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario: create a Seller company (defaults to ON)
    # http://geneva.sbx:8080/geneva/companies/10220
    # 3. Create new Seller (switch defaults to ON)
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/seller/CreateSellerPFO_enabled_payload.json"
    Then request passed successfully
    And returned "Seller" data matches the following json file "jsons/genevacrud/company/expected_results/seller/CreateSellerPFO_enabled_ER.json"
    And company can be searched out in database
    And company data in database is correct
