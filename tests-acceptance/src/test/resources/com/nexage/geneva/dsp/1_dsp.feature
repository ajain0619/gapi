Feature: DSP - Search for a page of DSPs as Nexage Admin

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: search for all DSPs
    When the user requests all DSPs
    Then request passed successfully
    And returned "DSPs" data matches the following json file "jsons/genevacrud/dsp/expected_results/search/AllDSPs.json"

  Scenario: search for a page of DSPs
    When the user requests a page of DSPs with page "0", size "5"
    Then request passed successfully
    And returned "DSPs" data matches the following json file "jsons/genevacrud/dsp/expected_results/search/PageOfDSPs.json"

  Scenario: search for all DSP summaries
    When the user requests all DSP summaries
    Then request passed successfully
    And returned "DSPs" data matches the following json file "jsons/genevacrud/dsp/expected_results/search/AllDSPSummaries.json"

  Scenario: search for all DSP summaries
    When the user requests a page of DSP summaries with page "0", size "5"
    Then request passed successfully
    And returned "DSPs" data matches the following json file "jsons/genevacrud/dsp/expected_results/search/PageOfDSPSummaries.json"

  Scenario: search for a DSP by name
    When the user requests a DSP with name "AdFonic"
    Then request passed successfully
    And returned "DSPs" data matches the following json file "jsons/genevacrud/dsp/expected_results/search/DSPByName.json"
