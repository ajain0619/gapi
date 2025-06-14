@searchtest
Feature: Test proper parsing of query field (qf) and query operator (qo) for search endpoints

  Scenario Outline: User executes search query
    Given the user "admin1c" has logged in
    When performs search with multi value search query "<search query>" and operator "<search operator>"
    Then request passed successfully
    And returned "search params" data matches the following json string "<parsed json>"

    Examples:
      | search query                          | search operator | parsed json                                                                     |
      | {key1=val1}                           |                 | {"fields":{"key1":["val1"]},"operator":"OR"}                                    |
      | {key1=val1,key2=val3}                 | or              | {"fields":{"key1":["val1"],"key2":["val3"]},"operator":"OR"}                    |
      | {key1=val 1,key2=val 3}               |                 | {"fields":{"key1":["val 1"],"key2":["val 3"]},"operator":"OR"}                  |
      | {key1=val 1\|val 2,key2=val 3}        | AnD             | {"fields":{"key1":["val 1","val 2"],"key2":["val 3"]},"operator":"AND"}         |
      | {key1=val 1\|val 2,key2=val 3\|val 4} | troll           | {"fields":{"key1":["val 1","val 2"],"key2":["val 3","val 4"]},"operator":"AND"} |
