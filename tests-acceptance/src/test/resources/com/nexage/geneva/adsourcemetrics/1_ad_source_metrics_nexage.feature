Feature: Get Ad Source Metrics as a Nexage Admin

  Background: Log in as a Nexage Admin and select the seller company
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "debpublisher"

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: Get Ad Source Metrics for a particular Ad source
    Given the user gets the ad source with name "A8X"
    And the user specifies the date range from "2016-01-15T00:00:00-05:00" to "2016-02-02T00:00:00-05:00"
    And the ad source summary interval is "<interval>"
    Then the ad source metrics for ad source are retrieved
    Then request passed successfully
    And returned "ad source metrics" data matches the following json file "jsons/genevacrud/adsourcemetrics/expected_results/<file_ER>.json"

    Examples:
      | interval | file_ER                              |
      |          | AdSourceMetricsForAdSourceDefault_ER |
      | Daily    | AdSourceMetricsForAdSourceDaily_ER   |
      | Weekly   | AdSourceMetricsForAdSourceWeekly_ER  |
      | Monthly  | AdSourceMetricsForAdSourceMonthly_ER |

  Scenario Outline: Get Ad Source Metrics for a particular Ad source and site
    Given the user selects the site "Deb's Test Site"
    And the user gets the ad source with name "A8X"
    And the user specifies the date range from "2016-01-15T00:00:00-05:00" to "2016-02-02T00:00:00-05:00"
    And the ad source summary interval is "<interval>"
    Then the ad source metrics for ad source and site are retrieved
    Then request passed successfully
    And returned "ad source metrics" data matches the following json file "jsons/genevacrud/adsourcemetrics/expected_results/<file_ER>.json"

    Examples:
      | interval | file_ER                                     |
      |          | AdSourceMetricsForAdSourceAndSiteDefault_ER |
      | Daily    | AdSourceMetricsForAdSourceAndSiteDaily_ER   |
      | Weekly   | AdSourceMetricsForAdSourceAndSiteWeekly_ER  |
      | Monthly  | AdSourceMetricsForAdSourceAndSiteMonthly_ER |

  Scenario Outline: Get Ad Source Metrics for a particular Ad source, site and position
    Given the user specifies the date range from "2016-01-15T00:00:00-05:00" to "2016-02-02T00:00:00-05:00"
    And the user selects the site "Deb's Test Site"
    And position with name "DebTestPosition" is selected
    And the user gets the ad source with name "A8X"
    And the ad source summary interval is "<interval>"
    Then the ad source metrics for ad source, site and position are retrieved
    Then request passed successfully
    And returned "ad source metrics" data matches the following json file "jsons/genevacrud/adsourcemetrics/expected_results/<file_ER>.json"

    Examples:
      | interval | file_ER                                          |
      |          | AdSourceMetricsForAdSourceSitePositionDefault_ER |
      | Daily    | AdSourceMetricsForAdSourceSitePositionDaily_ER   |
      | Weekly   | AdSourceMetricsForAdSourceSitePositionWeekly_ER  |
      | Monthly  | AdSourceMetricsForAdSourceSitePositionMonthly_ER |

  Scenario Outline: Get Ad Source Metrics for a particular Ad source, site, position and tag
    Given the user specifies the date range from "2016-01-15T00:00:00-05:00" to "2016-02-02T00:00:00-05:00"
    And the user selects the site "Deb's Test Site"
    And position with name "DebTestPosition" is selected
    And tag with name "Nexage Exchange - Deb's Test Tag" is selected
    And the user gets the ad source with name "A8X"
    And the ad source summary interval is "<interval>"
    And the ad source metrics for ad source, site, position and tag are retrieved
    Then request passed successfully
    And returned "ad source metrics" data matches the following json file "jsons/genevacrud/adsourcemetrics/expected_results/<file_ER>.json"

    Examples:
      | interval | file_ER                                             |
      |          | AdSourceMetricsForAdSourceSitePositionTagDefault_ER |
      | Daily    | AdSourceMetricsForAdSourceSitePositionTagDaily_ER   |
      | Weekly   | AdSourceMetricsForAdSourceSitePositionTagWeekly_ER  |
      | Monthly  | AdSourceMetricsForAdSourceSitePositionTagMonthly_ER |
