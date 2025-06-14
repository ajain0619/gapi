Feature: Get exchange rate endpoint tests

  Background: log in as Nexage Admin
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"

  Scenario Outline: Get exchange rates with qf, qt and latest
    When the user gets exchange rate with qf "<qf>" and qt "<qt>" and latest "<latest>"
    Then request passed successfully
    And returned "exchange rates" data matches the following json file "jsons/genevacrud/exchangerate/expected_results/<filename>.json"

    Examples:
      | qf       | qt   | latest | filename                                  |
      | currency | EUR  | false  | get_exchange_rates_with_currency_ER       |
      | currency | EUR  | true   | get_latest_exchange_rate_with_currency_ER |
      | null     | null | false  | get_exchange_rates_ER                     |
      | null     | null | true   | get_latest_exchange_rates_ER              |
      | null     | null | null   | get_latest_exchange_rates_ER              |

  Scenario Outline: Get exchange rates with latest and invalid qf or qt
    When the user gets exchange rate with qf "<qf>" and qt "<qt>" and latest "<latest>"
    Then response failed with "400" response code, error message "Bad Request" and without field errors.

    Examples:
      | qf       | qt   | latest |
      | forexId  | 1    | false  |
      | currency | null | false  |
      | currency |      | false  |
