DROP TABLE IF EXISTS country;
CREATE TABLE country
(
    id        varchar(32)  NOT NULL,
    VERSION   int          NOT NULL,
    dialCode  varchar(255) DEFAULT NULL,
    name      varchar(255) NOT NULL,
    shortName varchar(4)   DEFAULT NULL,
    iso2Alpha varchar(2)   DEFAULT NULL,
    domain    varchar(255) DEFAULT NULL,
    pid       bigint       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name),
    UNIQUE (pid)
);

DROP TABLE IF EXISTS phonecast_configuration;
CREATE TABLE phonecast_configuration
(
    id           varchar(32) NOT NULL,
    VERSION      int         NOT NULL,
    prefix       varchar(64)  DEFAULT NULL,
    config_key   varchar(255) DEFAULT NULL,
    config_value varchar(255) DEFAULT NULL,
    user_type_id varchar(32)  DEFAULT NULL,
    lastUpdate   datetime     DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (config_key)
);

DROP TABLE IF EXISTS site_blocked_ads;
CREATE TABLE site_blocked_ads
(
    site_id        varchar(32) NOT NULL,
    screened_ad_id varchar(32) NOT NULL,
    blocked_on     datetime DEFAULT NULL,
    blocked_by     bigint   DEFAULT NULL,
    site_pid       bigint   DEFAULT NULL,
    PRIMARY KEY (site_id, screened_ad_id)
);

DROP TABLE IF EXISTS global_config;
CREATE TABLE global_config
(
    pid         bigint       NOT NULL,
    property    varchar(100) DEFAULT NULL,
    value       varchar(100) DEFAULT NULL,
    description varchar(255) DEFAULT NULL,
    updated_on  datetime     DEFAULT NULL,
    PRIMARY KEY (pid)
);


INSERT INTO phonecast_configuration (id, VERSION, prefix, config_key, config_value, user_type_id,
                                     lastUpdate)
VALUES ('8a858af801353520f2eb20f2f2bb3fe2', 1, '', 'valid.rtb.ids', 2160, null, null),
       ('fb8b0cb4f75111e5a8d900219ba53ba7', 0, NULL, 'valid.mm.buyer.pids', '6644', NULL, NULL);

INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640046', 0, 33, 'FRANCE', 'FRA', 'FR', '.fr', 1);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640047', 0, 33, 'IRAN (Islamic Republic of Iran)', 'IRN', 'IR',
        '.ir', 2);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640048', 0, 33, 'UNITED STATES', 'USA', 'US', '.us', 3);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640049', 0, 33, 'GREAT BRITAIN', 'GBR', 'GB', '.uk', 4);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640040', 0, 33, 'UNITED KINGDOM (Great Britain)', 'GBR', 'GB',
        '.uk', 5);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d14851552011485156364004a', 0, 33, 'CZECH REPUBLIC', 'CZE', 'CZ', '.cz', 6);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d14851552011485156364004b', 0, 33, 'TUNISIA', 'TUN', 'TN', '.tn', 7);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d14851552011485156364004c', 0, 33, 'CANADA', 'CAN', 'CA', '.ca', 8);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d14851552011485156364004d', 0, 33, 'COSTA RICA', 'CRI', 'CR', '.cr', 9);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d14851552011485156364004e', 0, 33, 'INDIA', 'IND', 'IN', '.in', 10);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d14851552011485156364004f', 0, 33, 'NIGERIA', 'NGA', 'NG', '.ng', 11);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640043', 0, 33, 'SOUTH AFRICA (Zuid Afrika)', 'ZAF', 'ZA', '.za',
        12);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640042', 0, 33, 'SRI LANKA (formerly Ceylon)', 'LKA', 'LK',
        '.lk', 14);
INSERT INTO country (id, VERSION, dialCode, name, shortName, iso2Alpha, domain, pid)
VALUES ('4028811d148515520114851563640041', 0, 33, 'SWITZERLAND (Confederation of Helvetia)', 'CHE',
        'CH', '.ch', 15);

INSERT INTO global_config (pid, property, value, description, updated_on)
VALUES (32, 'images_base_url', 'http://localhost/ads/uploaded/',
        'Ad screening image server base url', '2013-12-16 11:31:34');
INSERT INTO global_config (pid, property, value, description, updated_on)
VALUES (33, 'blocked_ads_interval', '30', 'Ad screening blocked ads configuration in days',
        '2013-12-16 11:31:34');
INSERT INTO global_config (pid, property, value, description, updated_on)
VALUES (34, 'allowed_ads_interval', '2', 'Ad screening allowed ads configuration in days',
        '2013-12-16 11:31:34');
INSERT INTO global_config (pid, property, value, description, updated_on)
VALUES (35, 'screening_ads_max_limit', '5000', 'Ad screening maximum ads display limit',
        '2013-12-16 11:31:34');

insert into site_blocked_ads(site_id, screened_ad_id)
values ('s3', 'ad_id6');
insert into site_blocked_ads(site_id, screened_ad_id)
values ('s3', 'ad_id7');
insert into site_blocked_ads(site_id, screened_ad_id)
values ('s3', 'ad_id3');

drop table IF EXISTS deal;
CREATE TABLE deal
(
    pid           bigint      NOT NULL,
    version       int         NOT NULL,
    id            varchar(40) NOT NULL,
    floor         decimal,
    auction_type  tinyint,
    status        tinyint     NOT NULL,
    description   varchar(255) DEFAULT NULL,
    start         datetime     DEFAULT NULL,
    stop          datetime     DEFAULT NULL,
    created_by    bigint      NOT NULL,
    creation_date datetime     DEFAULT NULL,
    updated_on    datetime     DEFAULT NULL,
    visibility    tinyint      DEFAULT 0
);


