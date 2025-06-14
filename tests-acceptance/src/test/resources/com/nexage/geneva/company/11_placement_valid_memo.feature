Feature: Read new valid placement memo via valid-memo API

  Background:
    Given the user "admin1c" has logged in

  Scenario Outline: Get a valid placement memo
    Given the user selects the company "10214" and site "10000186"
    When the user copies the placement with memo "<placement_memo>"
    Then the new placement memo starts with "^<placement_memo>-s10000186-t\d+$"
    And the new placement memo is unique

    Examples:
    | placement_memo |
    | posarchive4    |
    | foo            |
    | qwerty         |
    | hamburger12345 |

  Scenario: Get a valid placement memo no placement name
    Given the user selects the company "10214" and site "10000186"
    When the user copies the placement with memo ""
    Then the new placement memo starts with "^pl-s10000186-t\d+$"
    And the new placement memo is unique

  Scenario: Get a valid placement memo invalid company id
    Given the user selects the company "-1" and site "10000186"
    When the user copies the placement with memo "<placement_memo>"
    Then the new placement memo starts with "^<placement_memo>-s10000186-t\d+$"
    And the new placement memo is unique

  Scenario: Get a empty placement memo invalid site id
    Given the user selects the company "10214" and site "-1"
    When the user copies the placement with memo "<placement_memo>"
    Then the new placement memo is empty
    And the new placement memo is not unique



