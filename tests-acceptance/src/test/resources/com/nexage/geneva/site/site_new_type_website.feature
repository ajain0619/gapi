Feature: Seller Website Site: get, create, update website site as seller admin

  Background: log in as seller admin
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "traffic_type"

  Scenario: create new website site with all fields
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateWebsiteAllFields_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteWebsiteAllFields_ER.json"
    And default site data is set
    And site "website_site1" can be searched in the database

  Scenario: get the seller site
    Given the Seller sites for PSS user are retrieved
    When the PSS user selects the site "website_site1"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetWebsite_ER.json"

  Scenario: update website site with all fields
    Given the Seller sites for PSS user are retrieved
    And the PSS user selects the site "website_site1"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateWebsiteAllFields_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateWebsiteAllFields_payload.json"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateWebsiteAllFields_ER.json"
