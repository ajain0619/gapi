Feature: Seller Sites: update a site using PSS API and make sure, that only correct combinations between integration and placements are allowed

  Background: log in as nexage yield manager and select company
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "traffic_type"

  @restoreCrudCoreDatabaseBefore
  Scenario Outline: create mobile or desktop site with javascript integration, update to API, create instream position, update to JS
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<createsite_payload>.json"
    And request passed successfully with code "201"
    When the PSS user selects the site "<site_name>"
    Then the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<updatesite_payload1>.json"
    And request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/<updatesite_payload1>.json"
    And request passed successfully
    And returned "updated site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<updatesite_ER>.json"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/CreateInstreamForApiSite_payload.json"
    And request passed successfully with code "201"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<updatesite_payload2>.json"
    Then request passed successfully

    Examples:
      | createsite_payload               | updatesite_payload1                  | updatesite_ER                   | updatesite_payload2                  | site_name  |
      | CreateMobileSiteJS_payload       | UpdateMobileSiteJSToApi_payload      | UpdateMobileSiteJSToApi_ER      | UpdateMobileSiteApiToJS_payload      | js_mobile  |
      | CreateDesktopSiteJS_payload      | UpdateDesktopSiteJSToApi_payload     | UpdateDesktopSiteJSToApi_ER     | UpdateDesktopSiteApiToJS_payload     | js_desktop |
      | CreateApplicationSiteJS_payload  | UpdateApplicationSiteJSToApi_payload | UpdateApplicationSiteJSToApi_ER | UpdateApplicationSiteApiToJS_payload | js_appl    |

  Scenario: create application site with javascript integration, update to SDK, then from SDK to API
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateApplicationSiteJSToSDK_payload.json"
    And request passed successfully with code "201"
    When the PSS user selects the site "js_appl2"
    Then the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateApplicationSiteJSToSDK_payload.json"
    And request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateApplicationSiteJSToSDK_payload.json"
    Then request passed successfully
    And returned "updated site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateApplicationSiteJSToSDK_ER.json"
    Then the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateApplicationSiteSDKToApi_payload.json"
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateApplicationSiteSDKToApi_payload.json"
    Then request passed successfully
    And returned "updated site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateApplicationSiteSDKToApi_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario Outline: create mobile site with interstitial,medium or banner placement with video and try to update site integration to JS
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/<createsite_payload>.json"
    And request passed successfully with code "201"
    When the PSS user selects the site "<site_name>"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/<position_payload>.json"
    And request passed successfully with code "201"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<updatesite_payload>.json"
    Then request passed successfully

    Examples:
      | createsite_payload               | position_payload                    | updatesite_payload                                | site_name   |
      | CreateMobileSiteAPI_payload      | CreateInterstitialWithVideo_payload | UpdateMobileSiteApiToJSOtherVersion_payload       | api_mobile  |
      | CreateMobileSiteAPI_payload      | CreateMediumWithVideo_payload       | UpdateMobileSiteApiToJSOtherVersion_payload       | api_mobile  |
      | CreateMobileSiteAPI_payload      | CreateBannerWithVideo_payload       | UpdateMobileSiteApiToJSOtherVersion_payload       | api_mobile  |
      | CreateDesktopSiteAPI_payload     | CreateInterstitialWithVideo_payload | UpdateDesktopSiteApiToJSOtherVersion_payload      | api_desktop |
      | CreateDesktopSiteAPI_payload     | CreateMediumWithVideo_payload       | UpdateDesktopSiteApiToJSOtherVersion_payload      | api_desktop |
      | CreateDesktopSiteAPI_payload     | CreateBannerWithVideo_payload       | UpdateDesktopSiteApiToJSOtherVersion_payload      | api_desktop |
      | CreateApplicationSiteAPI_payload | CreateInterstitialWithVideo_payload | UpdateApplicationSiteApiToJSOtherVersion_payload  | api_appl    |
      | CreateApplicationSiteAPI_payload | CreateInterstitialWithVideo_payload | UpdateApplicationSiteApiToSDKOtherVersion_payload | api_appl    |
      | CreateApplicationSiteSDK_payload | CreateInterstitialWithVideo_payload | UpdateApplicationSiteSdkToJS_payload              | sdk_appl    |

  @restoreCrudCoreDatabaseBefore
  Scenario Outline: create application site with medium or banner placement with video and try to update site integration to JS or SDK
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateApplicationSiteAPI_payload.json"
    And request passed successfully with code "201"
    When the PSS user selects the site "api_appl"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/<position_payload>.json"
    And request passed successfully with code "201"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/<update_payload>.json"
    Then request passed successfully

    Examples:
      | position_payload              | update_payload                                    |
      | CreateBannerWithVideo_payload | UpdateApplicationSiteApiToJSOtherVersion_payload  |
      | CreateMediumWithVideo_payload | UpdateApplicationSiteApiToJSOtherVersion_payload  |
      | CreateBannerWithVideo_payload | UpdateApplicationSiteApiToSDKOtherVersion_payload |
      | CreateMediumWithVideo_payload | UpdateApplicationSiteApiToSDKOtherVersion_payload |

  Scenario: create site with correct parameters for pss admin and update site integration
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreatePssSiteAllNewFieldsPA_payload.json"
    Then request passed successfully with code "201"
    When the PSS user selects the site "mgbPssite"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/CreateInterstitialWithVideo_payload.json"
    Then request passed successfully with code "201"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteApiToJS_payload.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteApiToJS_payload.json"
    Then request passed successfully
    And returned "updated site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/UpdateSiteApiToJS_ER.json"
