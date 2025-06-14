Feature: Available Suppliers: get

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  @restoreCrudCoreDatabaseBefore

  Scenario: get all suppliers
    Given the user searches for all suppliers
    And returned "All Suppliers" data matches the following json file "jsons/genevacrud/supplier/expected_results/search/AllDealSuppliers.json"

  Scenario Outline: search for supplier by existing supplier id and validate the attributes
    Given the user searches for all suppliers
    When the user searches for supplier by supplier id "<supplierId>"
    Then request passed successfully
    And returned "Deal Suppliers" data matches the following json file "jsons/genevacrud/supplier/expected_results/search/<filename>.json"

    Examples:
      | supplierId | filename                               |
      | 10000014   | DealSupplierById                       |
      | 10000024   | SupplierNonPss                         |
      | 10000025   | SupplierNonPssInactive                 |
      | 10000026   | SupplierInterstitialBothTagOverrides   |
      | 10000027   | SupplierBannerTagOverrides             |
      | 10000028   | SupplierInstreamVideo                  |
      | 10000029   | SupplierInterstitialVideoAndStatic     |
      | 10000030   | SupplierInterstitialStaticTagOverrides |
      | 10000031   | SupplierInterstitialVideoTagOverrides  |
      | 10000033   | SupplierMediumRectangle                |
      | 10000034   | SupplierNativeSiteTypeOverride         |
      | 10000035   | SupplierNativeWhiteListTargeting       |
      | 10000036   | SupplierBlacklistedTargetCountries     |
      | 10000039   | SupplierVideoAdsizeOverrides           |
      | 10000040   | SupplierSiteTypeOverride               |

  Scenario Outline: archived tags should not be available in the supplier list
    Given the user searches for all suppliers
    When the user searches for an "archived" supplier by id "<supplierId>"
    Then the archived supplier "<supplierId>" should not be available

    Examples:
      | supplierId |
      | 10000037   |
      | 10000038   |

  Scenario Outline: inactive tags should be available in the supplier list
    Given the user searches for all suppliers
    When the user searches for an "inactive" supplier by id "<supplierId>"
    Then the inactive supplier "<supplierId>" should be available

    Examples:
      | supplierId |
      | 10000025   |
      | 10000032   |
