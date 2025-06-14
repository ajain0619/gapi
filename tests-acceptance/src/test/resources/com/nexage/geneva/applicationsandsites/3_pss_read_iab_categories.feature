Feature: Test the endpoint to read IAB Categories

  Background: log in as nexage admin
    Given the user "admin1c" has logged in

  Scenario: A request to read the iab_categories.json file matches expected results.
    Given a request is made to the endpoint to read the static resource with the filename "iab_categories.json"
    Then returned ".../static/iab_categories.json" data matches the following json file "jsons/genevacrud/static/iab_categories.json"

  Scenario: A request to read a non-existent file returns a 404 response code
    Given a request is made to the endpoint to read the static resource with the filename "non_existent_filename.json"
    Then ".../static/non_existend_filename.json" failed with "404" response code
