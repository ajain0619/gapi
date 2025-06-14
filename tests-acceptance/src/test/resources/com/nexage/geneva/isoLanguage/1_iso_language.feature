Feature: search language as nexage admin

  Background: log in
    Given the user "admin1c" has logged in

  Scenario: search for all languages
    When the user searches for all languages
    Then request passed successfully
    And returned "languages" data matches the following json file "jsons/genevacrud/isolanguage/expected_results/search/languages_ER.json"

  Scenario: search for all languages with invalid search param language
    When the user searches for language with params "dummy" and search term "invalid"
    And "get language" failed with "Search request has invalid dummy parameter" response message

  Scenario: search for all languages with valid search param language
    When the user searches for language with params "languageName" and search term "ian"
    Then request passed successfully
    And returned "languages" data matches the following json file "jsons/genevacrud/isolanguage/expected_results/search/searchLanguage_ER.json"
