Feature: Seller Sites: get, create, update as Seller Admin

  Background: log in as seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved

  Scenario: get all seller sites
    When the Seller sites for PSS user are retrieved
    Then request passed successfully
    And returned "seller sites" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetAllSites_ER.json"

  Scenario Outline: create new site
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<file_ER>.json"
    And default site data is set

    Examples:
      | file_payload                     | file_ER                     |
      | CreateSiteAllFields_payload      | CreateSiteAllFields_ER      |
      | CreateSiteWithoutDesc_payload    | CreateSiteWithoutDesc_ER    |
      | CreateSiteRequiredFields_payload | CreateSiteRequiredFields_ER |
      | CreateSitePlatformFields_payload | CreateSitePlatformFields_ER |

  Scenario: Bad Post Payload on Site Creation
    When the PSS user creates a site from the json file "jsons/genevacrud/site/payload/CreateSiteAllFieldsBadRequest_payload.json"
    Then response failed with "400" response code, error message "Bad Request" and without field errors.

  Scenario Outline: Site Creation with unauthorized users
    Given the user "<user>" has logged in with role "<role>"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateDesktopSiteAllFields_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation" and without field errors.

    Examples:
      | user           | role       |
      | selleruser1    | UserSeller |
      | buyeruser1     | UserBuyer  |
      | crudnexageuser | UserNexage |

  Scenario Outline: update site random fields
    Given the Seller sites for PSS user are retrieved
    And the PSS user selects the site "<site name>"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/<file_payload>.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<file_ER>.json"

    Examples:
      | site name  | file_payload                      | file_ER                      |
      | AS8A       | UpdateSite_payload                | UpdateSite_ER                |
      | AS8Aupdate | UpdateSiteWithNullDesc_payload    | UpdateSiteWithNullDesc_ER    |
      | AS8Aupdate | UpdateSiteWithNonNullDesc_payload | UpdateSiteWithNonNullDesc_ER |

  Scenario: update site platform field
    Given the Seller sites for PSS user are retrieved
    And the PSS user selects the site "AS8Aupdate"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSitePlatform_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSitePlatform_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateSitePlatform_ER.json"

  Scenario: update site adding all position types - verify value for advanced mraid tracking- Verify trafficType for mediation/smart_yield
    Given the user "debmraidtest" has logged in with role "AdminSeller"
    And the user selects the site "debmraidsitemobileweb"
    When the user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteAllPositionTypes_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteAllPositionTypes_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateSiteAllPositionTypes_ER.json"

  Scenario: update site by adding a new banner position, then update again adding all other position types - verify advanced mraid tracking value for the existing banner does not change
    Given the user "debmraidtest" has logged in with role "AdminSeller"
    And the user selects the site "debmraidsiteapplication"
    When the user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteAddBanner_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteAddBanner_payload.json"
    Then request passed successfully
    When the user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteAddPositionTypes_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteAddPositionTypes_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateSiteAddPositionTypes_ER.json"

  Scenario: get the seller site
    Given the Seller sites for PSS user are retrieved
    When the PSS user selects the site "AS8B"
    Then request passed successfully
    And returned "seller site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetSite_ER.json"
