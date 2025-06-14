use test_dw;

DROP TABLE IF EXISTS dim_adnet;
CREATE TABLE dim_adnet
(
    id         bigint       NOT NULL,
    name       varchar(255) NOT NULL,
    status     smallint     NOT NULL,
    deleted    datetime    DEFAULT NULL,
    guid       varchar(32) DEFAULT NULL,
    company_id bigint      DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS dim_gateway;
CREATE TABLE dim_gateway
(
    id      bigint       NOT NULL,
    name    varchar(255) NOT NULL,
    status  smallint     NOT NULL,
    deleted datetime DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS dim_company;
CREATE TABLE dim_company
(
    id            bigint       NOT NULL,
    name          varchar(255) NOT NULL,
    status        smallint     NOT NULL,
    deleted       datetime     DEFAULT NULL,
    type          varchar(255) NOT NULL,
    external_name varchar(255) DEFAULT NULL,
    bidder_id     bigint       DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS dim_site;
CREATE TABLE dim_site
(
    id         bigint       NOT NULL,
    name       varchar(255) NOT NULL,
    status     smallint     NOT NULL,
    deleted    datetime DEFAULT NULL,
    dcn        varchar(32)  NOT NULL,
    company_id bigint   DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS dim_tag;
CREATE TABLE dim_tag
(
    id             bigint       NOT NULL,
    name           varchar(255) NOT NULL,
    status         smallint     NOT NULL,
    deleted        datetime     DEFAULT NULL,
    tag_owner      int          NOT NULL,
    site_id        int          NOT NULL,
    adnet_id       int          NOT NULL,
    external_name  varchar(255) DEFAULT NULL,
    guid           varchar(32)  DEFAULT NULL,
    monetization   smallint     DEFAULT '1',
    rtb_profile_id bigint       DEFAULT NULL,
    position_name  varchar(45)  DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS dim_auction;
CREATE TABLE dim_auction
(
    id      bigint       NOT NULL,
    name    varchar(255) NOT NULL,
    status  smallint     NOT NULL,
    deleted datetime DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS dim_screened_ad;
CREATE TABLE dim_screened_ad
(
    screened_ad_id     varchar(32) NOT NULL,
    pid                bigint(20)  NOT NULL,
    site_id            bigint(20)  NOT NULL,
    adnet_id           bigint(20)  NOT NULL,
    buyer_id           bigint(20)    DEFAULT NULL,
    first_seen         datetime      DEFAULT NULL,
    creative_file_name varchar(100)  DEFAULT NULL,
    markup_file_name   varchar(100)  DEFAULT NULL,
    ad_text            varchar(4096) DEFAULT NULL,
    ad_creative_url    varchar(1024) DEFAULT NULL,
    ad_click_url       varchar(1024) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS dim_advertiser;
CREATE TABLE dim_advertiser
(
    id         bigint(20)   NOT NULL,
    name       varchar(255) NOT NULL,
    status     smallint(6)  NOT NULL,
    deleted    datetime DEFAULT NULL,
    company_id bigint(20)   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS dim_campaign;
CREATE TABLE dim_campaign
(
    id            bigint(20)   NOT NULL,
    name          varchar(255) NOT NULL,
    status        smallint(6)  NOT NULL,
    deleted       datetime    DEFAULT NULL,
    ext_id        varchar(32) DEFAULT NULL,
    type          smallint(6)  NOT NULL,
    advertiser_id bigint(20)   NOT NULL,
    company_id    bigint(20)   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS dim_creative;
CREATE TABLE dim_creative
(
    id            bigint(20)   NOT NULL,
    name          varchar(255) NOT NULL,
    status        smallint(6)  NOT NULL,
    deleted       datetime DEFAULT NULL,
    advertiser_id bigint(20)   NOT NULL,
    company_id    bigint(20)   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


