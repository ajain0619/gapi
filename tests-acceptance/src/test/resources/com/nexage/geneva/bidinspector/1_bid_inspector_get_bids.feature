Feature: Bid inspector - Search for a page of bids as Nexage Admin

  Background: Open Bid inspector as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: Update dim_pre_bid_filter_reason table
    Given the following dim_pre_bid_filter_reason records with id "<id>" and name "<name>" are added
    Examples:
      |id | name |
      | 1 | pre bid filter reason 1 |
      | 2 | pre bid filter reason 2 |

  Scenario: Update fact_cowboy_traffic table
    Given the following fact_cowboy_traffic records are added
      | 2019-03-11 15:34:10 | 45670                | abc17 | 67 | 458 | 678 | P678 | abc900 | 789 | 13807 | 7899 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=8a969d89017777d9dbe1dce95685004f | {"bidderId":13807,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"e7d09415c18041b7b5d3ccc5cc9e9d6d","id":"e7d09415c18041b7b5d3ccc5cc9e9d6d"} | {"bidderId":13807,"url":"null","auction":"e7d09415c18041b7b5d3ccc5cc9e9d6d","id":"e7d09415c18041b7b5d3ccc5cc9e9d6d","cur":"USD"} |
      | 2019-03-11 15:34:10 | 45671                | abc18 | 67 | 454 | 679 | P679 | abc922 | 789 | 13808 | 7899 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=9a969d89016666d9dbe1dce95685004f | {"bidderId":13808,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"f7d09415c18041b7b5d3aaa5cc9e9d6d","id":"f7d09415c18041b7b5d3aaa5cc9e9d6d"} | {"bidderId":13808,"url":"null","auction":"f7d09415c18041b7b5d3aaa5cc9e9d6d","id":"f7d09415c18041b7b5d3aaa5cc9e9d6d","cur":"USD"} |
      | 2019-03-12 15:34:10 | -9027528480977354317 | abc19 | 69 | 454 | 680 | P681 | abc933 | 222 | 13809 | 7678 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=2a969d89015555d9dbe1dce95685004f | {"bidderId":13809,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"g7d09415c18041b7b5d3bbb5cc9e9d6d","id":"g7d09415c18041b7b5d3bbb5cc9e9d6d"} | {"bidderId":13809,"url":"null","auction":"g7d09415c18041b7b5d3bbb5cc9e9d6d","id":"g7d09415c18041b7b5d3bbb5cc9e9d6d","cur":"USD"} |

  Scenario: Update fact_cowboy_exchange table
    Given the following fact_cowboy_exchange records are added
      | 2019-03-11 15:34:10 | 45670                | abc17 | 13807 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=8a969d89017777d9dbe1dce95685004f | 200 | {"bidderId":13807,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"e7d09415c18041b7b5d3ccc5cc9e9d6d","id":"e7d09415c18041b7b5d3ccc5cc9e9d6d"} | {"bidderId":13807,"url":"null","auction":"e7d09415c18041b7b5d3ccc5cc9e9d6d","id":"e7d09415c18041b7b5d3ccc5cc9e9d6d","cur":"USD"} | 0 |
      | 2019-03-12 15:31:10 | 45670                | abc17 | 13891 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=9b969d89018888d9dbe1dce95685114l | 200 | {"bidderId":13891,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"f7d09415c18041b7b5d3bbb5cc9e9d7f","id":"l7d09415c18041b7b5d3bbb5cc9e9d6k"} | {"bidderId":13891,"url":"null","auction":"j7d09415c18041b7b5d3nnn5cc9e9d6k","id":"k8d09415c18041b7b5d3mmm5cc9e9d6f","cur":"USD"} | 1 |
      | 2019-03-13 15:32:10 | 45670                | abc17 | 14991 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=2k969d89013333d9dbe1dce95685114m | 400 | {"bidderId":14991,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"m7d09415c18041b7b5d3nnn5cc9e9d6k","id":"k7d09415c18041b7b5d3ttt5cc9e9d6m"} | {"bidderId":14991,"url":"null","auction":"l7d09415c18041b7b5d3bbb5cc9e9d6t","id":"j8d09415c18041b7b5d3nnn5cc9e9d6t","cur":"USD"} | 2 |
      | 2019-03-10 16:34:10 | -9027528480977354317 | abc19 | 13809 | https://web-oao.ssp.yahoo.com/admax/adServe.do?dcn=2k969d89013333d9dbe1dce95685114m | 200 | {"bidderId":13809,"url":"https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration","auction":"n7d09415c18041b7b5d3ooo5cc9e9d6b","id":"b7d09415c18041b7b5d3iii5cc9e9d6n"} | {"bidderId":13809,"url":"null","auction":"o7d09415c18041b7b5d3vvv5cc9e9d6j","id":"p8d09415c18041b7b5d3uuu5cc9e9d6k","cur":"EUR"} | 0 |

  Scenario: Update fact_cowboy_exchange_deals table
    Given the following fact_cowboy_exchange_deals records are added
      | 2019-03-11 15:31:10 | 45670                | abc17 | 13807 | P123 |
      | 2019-03-12 15:30:10 | 45670                | abc17 | 13807 | P456 |
      | 2019-03-13 15:34:10 | -9027528480977354317 | abc19 | 13809 | P222 |
      | 2019-03-13 15:32:10 | -9027528480977354317 | abc19 | 13809 | P444 |

  Scenario: Get all bids without any filter
    When the user searches for bids with page "Empty", size "Empty", qf "Empty", sort "Empty"
    Then request passed successfully
    And returned "search response" data matches the following json file "jsons/genevacrud/bidinspector/expected_results/getAllBids_ER.json"

  Scenario Outline: Get bids with pagination
    When the user searches for bids with page "<page>", size "<size>", qf "Empty", sort "Empty"
    Then request passed successfully
    And returned "search response" data matches the following json file "jsons/genevacrud/bidinspector/expected_results/pagination/<filename>.json"
    Examples:
      | page | size | filename              |
      | 0    | 2    | paginationForBids_ER  |
      | 2    | 1    | pagination2ForBids_ER |

  Scenario Outline: Search bids with filter
    When the user searches for bids with page "Empty", size "Empty", qf "<queryFields>", sort "<sortBy>"
    Then request passed successfully
    And returned "search response" data matches the following json file "jsons/genevacrud/bidinspector/expected_results/filter/<filename>.json"
    Examples:
      | queryFields                                          | sortBy              | filename                              |
      | {bidderId=13807}                                     | sellerId,asc        | filterByBidderId_ER                   |
      | {sellerId=67}                                        | placementId,desc    | filterBySellerId_ER                   |
      | {sellerId=67,placementId=679}                        | Empty               | filterBySellerIdAndPlacementId_ER     |
      | {siteId=454}                                         | dealId,desc         | filterBySiteId_ER                     |
      | {siteId=454,dealId=P681,appBundleId=222}             | Empty               | filterBySiteIdDealIdAndAppBundleId_ER |
      | {siteId=454,dealId=P681,appBundleId=444}             | Empty               | filterByQueryParamsCombinationDoesNotExist_ER |

  Scenario Outline: Search bids with invalid filter
    When the user searches for bids with page "Empty", size "Empty", qf "<queryFields>", sort "Empty"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{}"
    Examples:
      | queryFields                                       |
      | {sellerId=67,placementId=,siteId=123}             |
      | {siteId=454,dealId=P681,invalidKey=222}           |
      | {siteId=454,dealId=P681,placementId=abc}          |

  Scenario Outline: Search Auction Details
    When the user searches for Auction Details auctionRunHashId "<auctionRunHashId>" page "Empty", size "Empty", qf "Empty", sort "Empty"
    Then request passed successfully
    And returned "search response" data matches the following json file "jsons/genevacrud/bidinspector/expected_results/<filename>.json"
    Examples:
    | auctionRunHashId      | filename                            |
    | 45670                 | getAllAuctionDetails45670_ER        |
    | -9027528480977354317  | getAllAuctionDetails-90275_ER       |
