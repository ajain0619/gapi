Feature: search genre as nexage admin

  Background: log in
    Given the user "admin1c" has logged in

  Scenario: search for all genres
    When the user searches for all genres
    Then request passed successfully
    And returned "genre" data matches the following json file "jsons/genevacrud/contentgenre/expected_results/search/genres_ER.json"

  Scenario: search for all genres with invalid search param genre
    When the user searches for genre with params "dummy" and search term "something"
    And "get genre" failed with "Search request has invalid dummy parameter" response message

  Scenario: search for all genres with valid search param genre
    When the user searches for genre with params "genre" and search term "Action"
    Then request passed successfully
    And returned "genre" data matches the following json file "jsons/genevacrud/contentgenre/expected_results/search/searchGenre_ER.json"
