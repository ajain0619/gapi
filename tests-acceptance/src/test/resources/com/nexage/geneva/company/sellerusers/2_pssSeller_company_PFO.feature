Feature: search, update PFO switch for existing Seller company as pss users

  @restoreCrudCoreDatabaseBefore

  Scenario: search an existing Seller company - Price Floor Optimization defaulted to OFF
    # 4.1 http://geneva.sbx:8080/geneva/companies/105
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the company data is retrieved
    Then request passed successfully
    And returned "company search" data matches the following json file "jsons/genevacrud/company/expected_results/seller/pssSearchPid105_ER.json"

  Scenario Outline: create new tags with different PFO values (OFF, OFF to ON, Empty) for company with OFF PFO
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "<type>" tag from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"

    Examples:
      | type     | file_payload                               | file_ER                     |
      | exchange | PssExchangeTagPFOCompanyOFF_OFF_payload    | PssExchangeTagPFO_OFF_ER    |
      | exchange | PssExchangeTagPFOCompanyOFF_OFF_ON_payload | PssExchangeTagPFO_OFF_ON_ER |
      | exchange | PssExchangeTagPFOCompanyOFF_Empty_payload  | PssExchangeTagPFO_Empty_ER  |

  Scenario: search an existing site of a Seller company - check site, position and tags Price Floor Optimization should be defaulted to OFF
    # 4.2 http://geneva.sbx:8080/geneva/sellers/sites/950
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/company/expected_results/seller/GetSite950_pss_ER.json"

  Scenario Outline: clone tags with different combinations of PFO when company PFO is OFF
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "<type>" tag from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"

    Examples:
      | type     | tagName                      | file_payload                      | file_ER                      |
      | exchange | pssExchangeTagComOffTagOff   | PssCopyTagCompOffTagOff_payload   | PssCopyTagCompOffTagOff_ER   |
      | exchange | pssExchangeTagComOffTagOffOn | PssCopyTagCompOffTagOn_payload    | PssCopyTagCompOffTagOn_ER    |
      | exchange | pssExchangeTagComOffTagOff   | PssCopyTagCompOffTagOffOn_payload | PssCopyTagCompOffTagOffOn_ER |
      | exchange | pssExchangeTagComOffTagOffOn | PssCopyTagCompOffTagOnOff_payload | PssCopyTagCompOffTagOnOff_ER |

  Scenario: update Seller company with Price Floor Optimization enabled (switch to ON) as pssSellerAdmin
    #4.5 http://geneva.sbx:8080/geneva/companies/105
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/pssUpdatePFO_ON_payload.json"
    Then "Company Update" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario: update Seller company with Price Floor Optimization enabled (switch to ON) as crudAdmin to check tag creation
  #4.5 http://geneva.sbx:8080/geneva/companies/105
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "adserverSellerTest8"
    When the user updates a company from the json file "jsons/genevacrud/company/payload/seller/pssUpdatePFO_ON_payload.json"
    Then request passed successfully

  Scenario Outline: create new tags with different PFO values (OFF, OFF to ON, Empty) for company with ON PFO
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "<type>" tag from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"

    Examples:
      | type     | file_payload                              | file_ER                       |
      | exchange | PssExchangeTagPFOCompanyON_ON_payload     | PssExchangeTagPFO_ON_ER       |
      | exchange | PssExchangeTagPFOCompanyON_ON_OFF_payload | PssExchangeTagPFO_ON_OFF_ER   |
      | exchange | PssExchangeTagPFOCompanyON_Empty_payload  | PssExchangeTagPFO_ON_Empty_ER |

  Scenario Outline: clone tags with different combinations of PFO when company PFO is ON
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "<type>" tag from the json file "jsons/genevacrud/company/payload/seller/<file_payload>.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"

    Examples:
      | type     | tagName                     | file_payload                     | file_ER                     |
      | exchange | pssExchangeTagComOnTagOn    | PssCopyTagCompOnTagOn_payload    | PssCopyTagCompOnTagOn_ER    |
      | exchange | pssExchangeTagComOnTagOnOff | PssCopyTagCompOnTagOnOff_payload | PssCopyTagCompOnTagOnOff_ER |
      | exchange | pssExchangeTagComOnTagOn    | PssCopyTagCompOnTagOff_payload   | PssCopyTagCompOnTagOff_ER   |
      | exchange | pssExchangeTagComOnTagOnOff | PssCopyTagCompOnTagOffOn_payload | PssCopyTagCompOnTagOffOn_ER |

  Scenario: update Seller company PFO switch whose pss user(even has admin role, but is not affiliated with the company) does not have access to a company fails
    #4.6 Update company PFO using user not affiliated with the Seller
    # http://geneva.sbx:8080/geneva/companies/105
    Given the user "adminathens1" has logged in with role "AdminSeller"
    When Seller selects the company with id "105" and type "Seller"
    When the user updates a company without permissions for the user from the json file "jsons/genevacrud/company/payload/seller/pssNotAffiliatedUpdatePFO_ON_payload.json"
    Then "Company Update" failed with "401" response code and error message "You're not authorized to perform this operation"
