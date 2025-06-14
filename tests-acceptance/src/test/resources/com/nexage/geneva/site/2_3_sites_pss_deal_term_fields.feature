Feature: PSS - Sites: create, update site with new fields from current deal term object. Revenue settings

  Background: log in as nexage yield manager and select company
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "adserverSellerTest8"

  @restoreCrudCoreDatabaseBefore

  Scenario: get the site deal terms and update them to default (seller level)
    And the user gets all the site deal terms
    Then returned "deal terms" data matches the following json file "jsons/genevacrud/site/expected_results/GetSiteDealTerms.json"
    When the user updates the sites with default deal term from the json file "jsons/genevacrud/site/payload/UpdateSiteDealTerms.json"
    Then request passed successfully
    And returned "deal terms" data matches the following json file "jsons/genevacrud/site/expected_results/UpdatedSiteDealTerms.json"
    And the nexage rev share in the database for the site "AS8A" equals to "0.09900000"

  Scenario Outline: update deal terms using invalid data will fail
    Given the user selects the "Seller" company "<company>"
    And the user gets all the site deal terms
    When the user updates the sites with default deal term from the json file "jsons/genevacrud/site/payload/<filename>.json"
    Then "deals term update" failed with "<code>" response code and error message "<message>"

    Examples:
      | company             | filename                                    | code | message                                            |
      | 111 DJG Seller      | UpdateSiteDealTerms_NoDefaultSellerRevShare | 404  | Seller does not have default rev share and RTB fee |
      | adserverSellerTest8 | UpdateSiteDealTerms_NonExistingSitePids     | 404  | Site doesn't exist in database                     |

  Scenario: create a site with all new fields for currentDealTerm as internal admin
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteRevenue_payload.json"
    And request passed successfully with code "201"
    And returned "create site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreatePssSiteRevenue_ER.json"
    And the deal terms for specified site "revenueSite" equals to "1"
    And the nexage rev share in the database for the site "revenueSite" equals to "0.53000000"
    And the rtb fee in the database for the site "revenueSite" equals to "0.32000000"

  Scenario: get a site with all new fields as an internal admin
    When the PSS user selects the site "revenueSite"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetPssSiteRevenue_ER.json"

  Scenario: update a site with new revenue fields as an internal admin
    When the PSS user selects the site "revenueSite"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteRevenue_payload.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteRevenue_payload.json"
    Then request passed successfully
    And returned "update site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdatePssSiteRevenue_ER.json"
    And the nexage rev share in the database for the site "revenueSite" equals to "0.13000000"
    And the rtb fee in the database for the site "revenueSite" equals to "0.12000000"
    And the deal terms for specified site "revenueSite" equals to "2"

  Scenario Outline: create a site with incorrect revenue values as an internal admin
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then "create site" failed with "400" response code and error message "<error_message>"
    And site with the site name "<site_name>" is not generated
    And the deal terms for specified site "<site_name>" equals to "0"

    Examples:
      | file_payload                              | error_message                                                    | site_name         |
      | CreatePssSiteIncorrectRevShare_payload    | Current deal term revenue share is less than 0 or greater than 1 | incRevShare       |
      | CreatePssSiteEmptyRevShare_payload        | Current deal term revenue share is required                      | emptyRevShare     |

  Scenario Outline: update a site without nexageRevenueShare or without rtbFee as an internal admin
    When the PSS user selects the site "revenueSite"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then "update site" failed with "400" response code and error message "<error_message>"

    Examples:
      | file_payload                         | error_message                               |
      | UpdatePssSiteWithoutRevShare_payload | Current deal term revenue share is required |

  Scenario: get a site with revenue settings as an external admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user selects the site "revenueSite"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetPssSiteRevenueExternalUser_ER.json"

  Scenario: update a site with revenue settings as an external admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user selects the site "revenueSite"
    Then the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdatePssSiteRevenueExternal_payload.json"
    Then "update site" failed with "403" response code and error message "Current deal term is not allowed"

  Scenario: create a site with revenue settings as an external admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteRevenue_payload.json"
    Then "" failed with "401" response code and error message "You're not authorized to perform this operation"
    And the deal terms for specified site "revenueSite" equals to "2"
