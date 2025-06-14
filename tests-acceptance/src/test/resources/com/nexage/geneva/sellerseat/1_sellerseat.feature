Feature: create, update, search seller seats as nexage admin

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: search for seller seats
    Given the user searches for all seller seats
    Then request passed successfully
    And returned "seller seats" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/search/SellerSeats_ER.json"

  Scenario Outline: search seller seats for name containing a string
    Given the user searches all seller seats with query field name and query term "<queryTerm>" and assignable "<assignable>"
    Then request passed successfully
    And returned "seller seat" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/search/<expected_results>.json"

    Examples:
      | queryTerm     | assignable| expected_results                 |
      | 1             | true      |SellerSeatWithNameContaining1_ER  |
      | 2             | true      |SellerSeatWithNameContaining2_ER  |
      | 3             | false     |SellerSeatWithNameContaining3_ER  |

  Scenario Outline: create seller seat
    When the user create a seller seat from the json file "jsons/genevacrud/sellerseat/payload/create/<payload>.json"
    Then request passed successfully
    And returned "created seller seat" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/create/<expected_results>.json"
    Examples:
      | payload                             | expected_results               | desc                                |
      | EnabledSellerSeat_payload           | EnabledSellerSeat_ER           | create seller seat with two sellers |
      | EnabledSellerSeat_noSellers_payload | EnabledSellerSeat_noSellers_ER | create seller seat with no sellers  |

  Scenario: create seller seat with unknown sellers
    When the user create a seller seat from the json file "jsons/genevacrud/sellerseat/payload/create/EnabledSellerSeatWithUnknownSellers_payload.json"
    Then "create seller seat" failed with "404" response code

  Scenario Outline: search for seller seat without sellers
    Given the user "<user>" has logged in
    When the user searches for seller seat by pid "2"
    Then request passed successfully
    And returned "seller seat" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/search/SellerSeatWithPid2_ER.json"
    Examples:
      | user             |
      | admin1c          |
      | sellerSeatUser2  |
      | svcsellerseat034 |
      | svcsellerseat957 |
      | svcsellerseat878 |
      | svcsellerseat470 |
      | svcsellerseat944 |
      | svcsellerseat444 |

  Scenario Outline: search for seller seat with sellers
    Given the user "<user>" has logged in
    When the user searches for seller seat by pid "6"
    Then request passed successfully
    And returned "seller seat" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/create/EnabledSellerSeat_ER.json"
    Examples:
      | user    |
      | admin1c |

  Scenario: search for seller seat by non existing seller seat pid will fail
    When the user searches for non existing seller seat
    Then "seller seat search" failed with "404" response code

  Scenario: update non existing seller seat
    When the user updates a seller seat with pid "0" providing the json file "jsons/genevacrud/sellerseat/payload/update/SellerSeatWithPid0.json"
    Then "seller seat update" failed with "404" response code

  Scenario Outline: update existing seller seat having: "<desc>"
    When the user updates a seller seat with pid "<pid>" providing the json file "jsons/genevacrud/sellerseat/payload/update/<payload>.json"
    Then request passed successfully
    And returned "seller seat" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/update/<expected_results>.json"

    Examples:
      | pid | payload            | expected_results   | desc                                                             |
      | 5   | SellerSeatWithPid5 | SellerSeatWithPid5 | NoSellersAssociatedSoFar                                         |
      | 2   | SellerSeatWithPid2 | SellerSeatWithPid2 | some sellers associated already (add one and remove two sellers) |

  Scenario Outline: search for updated seller seat with different sellers
    Given the user "<user>" has logged in
    When the user searches for seller seat by pid "2"
    Then request passed successfully
    And returned "seller seat" data matches the following json file "jsons/genevacrud/sellerseat/expected_results/update/SellerSeatWithPid2.json"
    Examples:
      | user            |
      | admin1c         |
      | sellerSeatUser2 |
