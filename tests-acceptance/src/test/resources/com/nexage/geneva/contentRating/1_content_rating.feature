Feature: search rating as nexage admin

  Background: log in
    Given the user "admin1c" has logged in

  Scenario: search for all ratings
    When the user searches for all ratings
    Then request passed successfully
    And returned "rating" data matches the following json file "jsons/genevacrud/contentRating/expected_results/search/ratings_ER.json"

  Scenario: search for all ratings with invalid search param rating
    When the user searches for rating with params "dummy" and search term "something"
    And "get rating" failed with "Search request has invalid dummy parameter" response message

  Scenario: search for all ratings with valid search param rating
    When the user searches for rating with params "rating" and search term "NR"
    Then request passed successfully
    And returned "rating" data matches the following json file "jsons/genevacrud/contentRating/expected_results/search/searchRating_ER.json"
