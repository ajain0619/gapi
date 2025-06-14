Feature: Seller Sites Summaries: Get

  Background: log in as seller admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Rovio"

  Scenario: 0. get all sites summaries under a seller
    When Get page "0" containing "4" sites summaries from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSummariesPaginated0_ER.json"
    And Get page "1" containing "4" sites summaries from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSummariesPaginated1_ER.json"

  Scenario: 1. get all sites summaries under a seller by site name
    When Get page "0" containing "3" sites summaries by site name "Rovio-Angry" and from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSummariesBySiteNamePaginated0_ER.json"
    And Get page "1" containing "3" sites summaries by site name "Rovio-Angry" and from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSummariesBySiteNamePaginated1_ER.json"

  Scenario: 2. get all sites summaries under a seller by site pids
    When Get page "0" containing "3" sites summaries by site pids "1353,1301,1275,1340,1288" and from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSummariesBySitePidsPaginated0_ER.json"
    And Get page "1" containing "3" sites summaries by site pids "1353,1301,1275,1340,1288" and from between "2020-07-21T00:00:00-04:00" and "2020-07-27T00:00:00-04:00"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSummariesBySitePidsPaginated1_ER.json"
