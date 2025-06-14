Feature: Seller Application Site CTV_OTT Platform: get, create, update application site with ctv_ott platform as seller admin

  Background: log in as seller admin
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "traffic_type"

  Scenario: create new application site with ctv_ott platform and all fields
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateCtvOttPlatformSiteAllFields_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateCtvOttPlatformSiteAllFields_ER.json"
    And default site data is set
    And site "ctv_ott_site" can be searched in the database

  Scenario: get the seller site
    Given the Seller sites for PSS user are retrieved
    When the PSS user selects the site "ctv_ott_site"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetCtvOttPlatformSite_ER.json"

  Scenario: update website site with all fields
    Given the Seller sites for PSS user are retrieved
    And the PSS user selects the site "ctv_ott_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateCtvOttPlatformSiteAllFields_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateCtvOttPlatformSiteAllFields_payload.json"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateCtvOttPlatformSiteAllFields_ER.json"
