Feature: Seller Sites: get, create, update sites as nexage or pss users with new fields migrated from seller api

  Background: log in as seller admin
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "traffic_type"

  Scenario:N-3 create a site with the duplicate name within one company
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDuplicateNameSameCompany_payload.json"
    Then "create site" failed with "400" response code and error message "Site exists with the given name"

  Scenario:N-2 create a site with duplicate name from the other company
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDuplicateNameDifCompany_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteDuplicateNameDifCompany_ER.json"

  Scenario:N-1 update a site with the duplicate name within one company
    When the PSS user selects the site "aleksis_site"
    Then the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDuplicateNameSameCompany_payload.json"
    And request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDuplicateNameSameCompany_payload.json"
    Then "update site" failed with "400" response code and error message "Site exists with the given name"

  Scenario:N update a site with duplicate name from the other company
    When the PSS user selects the site "aleksis_site"
    Then the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDuplicateNameDifCompany_payload.json"
    And request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDuplicateNameDifCompany_payload.json"
    Then request passed successfully
    And returned "update site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateSiteDuplicateNameDifCompany_ER.json"
