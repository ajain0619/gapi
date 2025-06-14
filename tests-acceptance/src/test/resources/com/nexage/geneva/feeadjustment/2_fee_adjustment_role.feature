Feature: Fee adjustment role based access tests.

  Scenario Outline: Getting all fee adjustments with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user gets all fee adjustments
    Then "fee adjustment get all" failed with "401" response code

    Examples:
      | user        | role       |
      | SellerUser1 | UserSeller |
      | BuyerUser1  | UserBuyer  |

  Scenario Outline: Getting a fee adjustment with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user gets fee adjustment with pid "1"
    Then "fee adjustment get" failed with "401" response code

    Examples:
      | user        | role       |
      | NexageUser1 | UserNexage |
      | SellerUser1 | UserSeller |
      | BuyerUser1  | UserBuyer  |

  Scenario Outline: Creating a fee adjustment with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user creates fee adjustment from the json file "jsons/genevacrud/feeadjustment/payload/<file_payload>.json"
    Then "fee adjustment creation" failed with "401" response code

    Examples:
      | user        | role       | file_payload                    |
      | NexageUser1 | UserNexage | create_fee_adjustment_1_payload |
      | SellerUser1 | UserSeller | create_fee_adjustment_1_payload |
      | BuyerUser1  | UserBuyer  | create_fee_adjustment_1_payload |

  Scenario Outline: Updating a fee adjustment with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user updates fee adjustment with pid "1" from the json file "jsons/genevacrud/feeadjustment/payload/<file_payload>.json"
    Then "fee adjustment update" failed with "401" response code

    Examples:
      | user        | role       | file_payload                    |
      | NexageUser1 | UserNexage | update_fee_adjustment_1_payload |
      | SellerUser1 | UserSeller | update_fee_adjustment_1_payload |
      | BuyerUser1  | UserBuyer  | update_fee_adjustment_1_payload |

  Scenario Outline: Deleting a fee adjustment with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user deletes fee adjustment with pid "1"
    Then "fee adjustment delete" failed with "401" response code

    Examples:
      | user        | role       |
      | NexageUser1 | UserNexage |
      | SellerUser1 | UserSeller |
      | BuyerUser1  | UserBuyer  |
