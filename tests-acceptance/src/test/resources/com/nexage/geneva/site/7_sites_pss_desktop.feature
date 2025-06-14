Feature: Seller Desktop Site: get, create, update desktop site as seller admin

  Background: log in as seller admin
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "traffic_type"

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: create new desktop site with all fields or only required fields
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<file_ER>.json"
    And default site data is set
    And site "<site_name>" can be searched in the database

    Examples:
      | file_payload                       | file_ER                       | site_name     |
      | CreateDesktopSiteAllFields_payload | CreateSiteDesktopAllFields_ER | desktop_site1 |
      | CreateDesktopSiteRequired_payload  | CreateSiteDesktopRequired_ER  | desktop_site2 |
      | CreateMobileToUpdate_payload       | CreateMobileToUpdate_ER       | mobile_site1  |

  Scenario: get the seller site
    Given the Seller sites for PSS user are retrieved
    When the PSS user selects the site "desktop_site1"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetDesktopSite_ER.json"

  Scenario: update desktop site with all fields
    Given the Seller sites for PSS user are retrieved
    And the PSS user selects the site "desktop_site1"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateDesktopSiteAllFields_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateDesktopSiteAllFields_payload.json"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateDesktopSiteAllFields_ER.json"
