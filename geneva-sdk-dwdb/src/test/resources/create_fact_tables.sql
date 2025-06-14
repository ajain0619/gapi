use test_dw;

DROP TABLE IF EXISTS fact_traffic_adnet;
CREATE TABLE fact_traffic_adnet
(
    start         datetime    DEFAULT NULL,
    stop          datetime    DEFAULT NULL,
    site_id       bigint(20)  DEFAULT NULL,
    zone          varchar(32) DEFAULT NULL,
    adnet_id      bigint(20)  DEFAULT NULL,
    tag_id        bigint(20)  DEFAULT NULL,
    tag_owner     int(11)     DEFAULT NULL,
    ads_requested bigint(20)  DEFAULT NULL,
    ads_served    bigint(20)  DEFAULT NULL,
    ads_displayed bigint(20)  DEFAULT NULL,
    ads_clicked   bigint(20)  DEFAULT NULL,
    ads_blocked   bigint(20)  DEFAULT NULL,
    calls_timeout bigint(20)  DEFAULT NULL,
    `group`       varchar(32) DEFAULT NULL,
    KEY idx_adnet_id (adnet_id),
    KEY idx_site_id (site_id),
    KEY idx_start (start),
    KEY idx_stop (stop),
    KEY idx_tag_id (tag_id),
    KEY idx_zone (zone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_traffic_site;
CREATE TABLE fact_traffic_site
(
    start         datetime    DEFAULT NULL,
    stop          datetime    DEFAULT NULL,
    site_id       bigint(20)  DEFAULT NULL,
    zone          varchar(32) DEFAULT NULL,
    ads_requested bigint(20)  DEFAULT NULL,
    ads_served    bigint(20)  DEFAULT NULL,
    ads_displayed bigint(20)  DEFAULT NULL,
    ads_clicked   bigint(20)  DEFAULT NULL,
    ads_blocked   bigint(20)  DEFAULT NULL,
    ads_bots      bigint(20)  DEFAULT NULL,
    ads_throttled bigint(20)  DEFAULT NULL,
    `group`       varchar(32) DEFAULT NULL,
    KEY idx_site_id (site_id),
    KEY idx_start (start),
    KEY idx_stop (stop),
    KEY idx_zone (zone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_traffic_targeted_adnet;
CREATE TABLE fact_traffic_targeted_adnet
(
    start         datetime     DEFAULT NULL,
    stop          datetime     DEFAULT NULL,
    site_id       bigint(20)   DEFAULT NULL,
    zone          varchar(32)  DEFAULT NULL,
    adnet_id      bigint(20)   DEFAULT NULL,
    tag_id        bigint(20)   DEFAULT NULL,
    tag_owner     int(11)      DEFAULT NULL,
    country       varchar(3)   DEFAULT NULL,
    carrier       varchar(200) DEFAULT NULL,
    device_make   varchar(64)  DEFAULT NULL,
    device_model  varchar(64)  DEFAULT NULL,
    device_os     varchar(64)  DEFAULT NULL,
    device_osv    varchar(64)  DEFAULT NULL,
    ads_requested bigint(20)   DEFAULT NULL,
    ads_served    bigint(20)   DEFAULT NULL,
    ads_displayed bigint(20)   DEFAULT NULL,
    ads_clicked   bigint(20)   DEFAULT NULL,
    ads_blocked   bigint(20)   DEFAULT NULL,
    calls_timeout bigint(20)   DEFAULT NULL,
    KEY idx_adnet_id (adnet_id),
    KEY idx_carrier (carrier),
    KEY idx_country (country),
    KEY idx_device_make (device_make),
    KEY idx_device_model (device_model),
    KEY idx_device_os (device_os),
    KEY idx_site_id (site_id),
    KEY idx_start (start),
    KEY idx_stop (stop),
    KEY idx_tag_id (tag_id),
    KEY idx_zone (zone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_traffic_targeted_site;
CREATE TABLE fact_traffic_targeted_site
(
    start         datetime                                                  DEFAULT NULL,
    stop          datetime                                                  DEFAULT NULL,
    site_id       bigint(20)                                                DEFAULT NULL,
    zone          varchar(32)                                               DEFAULT NULL,
    country       varchar(3) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
    carrier       varchar(200)                                              DEFAULT NULL,
    device_make   varchar(64)                                               DEFAULT NULL,
    device_model  varchar(64)                                               DEFAULT NULL,
    device_os     varchar(64)                                               DEFAULT NULL,
    device_osv    varchar(64)                                               DEFAULT NULL,
    ads_requested bigint(20)                                                DEFAULT NULL,
    ads_served    bigint(20)                                                DEFAULT NULL,
    ads_displayed bigint(20)                                                DEFAULT NULL,
    ads_clicked   bigint(20)                                                DEFAULT NULL,
    ads_blocked   bigint(20)                                                DEFAULT NULL,
    ads_bots      bigint(20)                                                DEFAULT NULL,
    ads_throttled bigint(20)                                                DEFAULT NULL,
    KEY idx_carrier (carrier),
    KEY idx_country (country),
    KEY idx_device_make (device_make),
    KEY idx_device_model (device_model),
    KEY idx_device_os (device_os),
    KEY idx_site_id (site_id),
    KEY idx_start (start),
    KEY idx_stop (stop),
    KEY idx_zone (zone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_exchange_auctions;
CREATE TABLE fact_exchange_auctions
(
    start              datetime       DEFAULT NULL,
    stop               datetime       DEFAULT NULL,
    exchange_id        bigint(20)     DEFAULT NULL,
    site_id            bigint(20)     DEFAULT NULL,
    tag_id             bigint(20)     DEFAULT NULL,
    auction_type       smallint(6)    DEFAULT NULL,
    ads_requested      bigint(20)     DEFAULT NULL,
    auctions           bigint(20)     DEFAULT NULL,
    ads_served         bigint(20)     DEFAULT NULL,
    throttled          bigint(20)     DEFAULT NULL,
    no_bidders         bigint(20)     DEFAULT NULL,
    no_bids            bigint(20)     DEFAULT NULL,
    no_sufficient_bids bigint(20)     DEFAULT NULL,
    bidders_sum        bigint(20)     DEFAULT NULL,
    bidders_min        bigint(20)     DEFAULT NULL,
    bidders_max        bigint(20)     DEFAULT NULL,
    bidders_avg        double         DEFAULT NULL,
    price_sum          decimal(16, 8) DEFAULT NULL,
    price_min          decimal(16, 8) DEFAULT NULL,
    price_max          decimal(16, 8) DEFAULT NULL,
    price_avg          decimal(16, 8) DEFAULT NULL,
    bids_max           bigint(20)     DEFAULT '0',
    KEY idxs_start_stop (start, stop),
    KEY idx_exchange_id (exchange_id),
    KEY idx_site_id (site_id),
    KEY idx_tag_id (tag_id),
    KEY idx_auction_id (auction_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_exchange_bids;
CREATE TABLE fact_exchange_bids
(
    start             datetime       DEFAULT NULL,
    stop              datetime       DEFAULT NULL,
    exchange_id       bigint(20)     DEFAULT NULL,
    site_id           bigint(20)     DEFAULT NULL,
    tag_id            bigint(20)     DEFAULT NULL,
    bidder_id         bigint(20)     DEFAULT NULL,
    bids_requested    bigint(20)     DEFAULT NULL,
    bids_received     bigint(20)     DEFAULT NULL,
    bids_won          bigint(20)     DEFAULT NULL,
    bids_forfeit      bigint(20)     DEFAULT NULL,
    bids_insufficient bigint(20)     DEFAULT NULL,
    bids_timeout      bigint(20)     DEFAULT NULL,
    price_sum         decimal(16, 8) DEFAULT NULL,
    price_min         decimal(16, 8) DEFAULT NULL,
    price_max         decimal(16, 8) DEFAULT NULL,
    price_avg         decimal(16, 8) DEFAULT NULL,
    bids_unscreenable bigint(20)     DEFAULT NULL,
    KEY idxs_start_stop (start, stop),
    KEY idx_exchange_id (exchange_id),
    KEY idx_site_id (site_id),
    KEY idx_tag_id (tag_id),
    KEY idx_bidder_id (bidder_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_exchange_wins;
CREATE TABLE fact_exchange_wins
(
    start         datetime       DEFAULT NULL,
    stop          datetime       DEFAULT NULL,
    exchange_id   bigint(20)     DEFAULT NULL,
    site_id       bigint(20)     DEFAULT NULL,
    zone          varchar(32)    DEFAULT NULL,
    tag_id        bigint(20)     DEFAULT NULL,
    bidder_id     bigint(20)     DEFAULT NULL,
    ads_served    bigint(20)     DEFAULT NULL,
    price_sum     decimal(16, 8) DEFAULT NULL,
    price_min     decimal(16, 8) DEFAULT NULL,
    price_max     decimal(16, 8) DEFAULT NULL,
    price_avg     decimal(16, 8) DEFAULT NULL,
    ads_delivered bigint(20)     DEFAULT NULL,
    revenue       decimal(16, 8) DEFAULT NULL,
    seat_id       varchar(40)    DEFAULT NULL,
    KEY idxs_start_stop (start, stop),
    KEY idx_exchange_id (exchange_id),
    KEY idx_site_id (site_id),
    KEY idx_zone (zone),
    KEY idx_tag_id (tag_id),
    KEY idx_bidder_id (bidder_id),
    KEY idx_seat_id (seat_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fact_screened_ad;
CREATE TABLE fact_screened_ad
(
    start         datetime   DEFAULT NULL,
    stop          datetime   DEFAULT NULL,
    site_id       bigint(20) DEFAULT NULL,
    adnet_id      bigint(20) DEFAULT NULL,
    ad_id         bigint(20) DEFAULT NULL,
    seen_count    bigint(20) DEFAULT NULL,
    is_blocked    smallint   DEFAULT 0,
    blocked_count bigint(20) DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `fact_exchange_bidders`
(
    `start`             datetime       DEFAULT NULL,
    `stop`              datetime       DEFAULT NULL,
    `exchange_id`       bigint(20)     DEFAULT NULL,
    `site_id`           bigint(20)     DEFAULT NULL,
    `tag_id`            bigint(20)     DEFAULT NULL,
    `bidder_id`         bigint(20)     DEFAULT NULL,
    `bid_requests`      bigint(20)     DEFAULT NULL,
    `bid_responses`     bigint(20)     DEFAULT NULL,
    `bids_won`          bigint(20)     DEFAULT NULL,
    `bids`              bigint(20)     DEFAULT NULL,
    `bids_forfeit`      bigint(20)     DEFAULT NULL,
    `bids_insufficient` bigint(20)     DEFAULT NULL,
    `bids_timeout`      bigint(20)     DEFAULT NULL,
    `price_sum`         decimal(16, 8) DEFAULT NULL,
    `price_min`         decimal(16, 8) DEFAULT NULL,
    `price_max`         decimal(16, 8) DEFAULT NULL,
    `price_avg`         decimal(16, 8) DEFAULT NULL,
    `bids_unscreenable` bigint(20)     DEFAULT NULL,
    KEY `idx_start` (`start`),
    KEY `idx_stop` (`stop`),
    KEY `idx_site` (`site_id`),
    KEY `idx_bidder` (`bidder_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `fact_exchange_seatbids`
(
    `start`             datetime       DEFAULT NULL,
    `stop`              datetime       DEFAULT NULL,
    `exchange_id`       bigint(20)     DEFAULT NULL,
    `site_id`           bigint(20)     DEFAULT NULL,
    `tag_id`            bigint(20)     DEFAULT NULL,
    `bidder_id`         bigint(20)     DEFAULT NULL,
    `seat_id`           varchar(40)    DEFAULT '???',
    `bids`              bigint(20)     DEFAULT NULL,
    `bids_won`          bigint(20)     DEFAULT NULL,
    `bids_forfeit`      bigint(20)     DEFAULT NULL,
    `bids_insufficient` bigint(20)     DEFAULT NULL,
    `wins_timeout`      bigint(20)     DEFAULT NULL,
    `price_sum`         decimal(16, 8) DEFAULT NULL,
    `price_min`         decimal(16, 8) DEFAULT NULL,
    `price_max`         decimal(16, 8) DEFAULT NULL,
    `price_avg`         decimal(16, 8) DEFAULT NULL,
    `bids_unscreenable` bigint(20)     DEFAULT NULL,
    KEY `idx_start` (`start`),
    KEY `idx_stop` (`stop`),
    KEY `idx_seat` (`seat_id`),
    KEY `idx_site` (`site_id`),
    KEY `idx_start_seat` (`start`, `seat_id`),
    KEY `idx_bidder` (`bidder_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



