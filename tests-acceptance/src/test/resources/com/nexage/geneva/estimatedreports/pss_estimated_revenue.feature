Feature: PSS Estimated revenue - Nexage Exchange, Mellenial Media, Mediation Sources, Direct Sold Campaigns

  Background: user login
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: Estimated revenue for the seller when no data
    Given the datawarehouse table "fact_revenue_adnet" is cleared
    And the datawarehouse table "fact_traffic_adserver" is cleared
    And the user selects the "Seller" company "adserverSellerTest7"
    And the user specifies the date range from "2015-04-01" to "2015-04-30"
    When the user selects estimated report for seller
    Then request passed successfully
    And returned "estimated revenue" data matches the following json file "jsons/genevacrud/estimatedrevenue/expected_results/estimatedRevenueNoData_ER.json"
    When the user selects estimated report with adnet drilldown
    Then request passed successfully
    And returned "estimated revenue" data matches the following json file "jsons/genevacrud/estimatedrevenue/expected_results/estimatedRevenueDrillDownAdnetNoData_ER.json"
    When the user selects estimated report with advertiser drilldown
    Then request passed successfully
    And returned "estimated revenue" data matches the following json file "jsons/genevacrud/estimatedrevenue/expected_results/estimatedRevenueDrillDownAdvertiserNoData_ER.json"

  Scenario: Load databases
    Given the datawarehouse table "fact_revenue_adnet" is cleared
    And the datawarehouse table "fact_traffic_adserver" is cleared
    And the datawarehouse table "fact_revenue_adnet" is populated from the data in the file "src/test/resources/csvs/EstimatedPayoutsReportJob/factRevenueAdnetInitData.csv"
    And the datawarehouse table "fact_traffic_adserver" is populated from the data in the file "src/test/resources/csvs/EstimatedPayoutsReportJob/factTrafficAdserverInitData.csv"

  Scenario: Estimated revenue for the seller
    Given the user selects the "Seller" company "adserverSellerTest7"
    And the user specifies the date range from "2015-04-01" to "2015-04-30"
    When the user selects estimated report for seller
    Then request passed successfully
    And returned "estimated revenue" data matches the following json file "jsons/genevacrud/estimatedrevenue/expected_results/estimatedRevenue1_ER.json"

  Scenario: Estimated revenue for the seller with adnet drill down
    Given the user selects the "Seller" company "adserverSellerTest7"
    And the user specifies the date range from "2015-04-01" to "2015-04-30"
    When the user selects estimated report with adnet drilldown
    Then request passed successfully
    And returned "estimated revenue" data matches the following json file "jsons/genevacrud/estimatedrevenue/expected_results/estimatedRevenueDrillDownAdnet_ER.json"

  Scenario: Estimated revenue for the seller with advertiser drill down - direct campaigns
    Given the user selects the "Seller" company "adserverSellerTest7"
    And the user specifies the date range from "2015-04-01" to "2015-04-30"
    When the user selects estimated report with advertiser drilldown
    Then request passed successfully
    And returned "estimated revenue" data matches the following json file "jsons/genevacrud/estimatedrevenue/expected_results/estimatedRevenueDrillDownAdvertiser_ER.json"

# crudsellermanager with company id 100063 which is selfserve_allowed=0
  Scenario: Estimated revenue report not available for pss disabled seller
    Given the user "crudsellermanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2015-04-01" to "2015-04-30"
    When the user selects estimated report for seller
    Then "estimated revenue" failed with "401" response code
    When the user selects estimated report with adnet drilldown
    Then "estimated revenue adnet drilldown" failed with "401" response code
    When the user selects estimated report with advertiser drilldown
    Then "estimated revenue advertiser drilldown" failed with "401" response code
