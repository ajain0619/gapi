INSERT INTO dim_company (id, name, status, type)
VALUES (1, 'Nexage Inc', 1, 'NEXAGE');
INSERT INTO dim_company (id, name, status, type)
VALUES (2, 'foo.com', 1, 'PUB');
INSERT INTO dim_company (id, name, status, type)
VALUES (3, 'bar.com', 1, 'PUB');
INSERT INTO dim_company (id, name, status, type, deleted)
VALUES (4, 'bar.com', 1, 'PUB', STR_TO_DATE('Jan 5, 2011', '%M %d, %Y'));
INSERT INTO dim_company (id, name, status, type)
VALUES (5, 'Acme', 1, 'PUB');
INSERT INTO dim_company (id, name, status, type)
VALUES (6, 'United', 1, 'PUB');
INSERT INTO dim_company (id, name, status, type)
VALUES (7, 'International', 1, 'PUB');
INSERT INTO dim_company (id, name, status, type, bidder_id)
VALUES (9, 'Bidder1', 1, 'BUYER', 10);
INSERT INTO dim_company (id, name, status, type, bidder_id)
VALUES (10, 'Bidder2', 1, 'BUYER', 13);

INSERT INTO dim_site (id, name, status, dcn)
VALUES (1, 'Def Jam Mobile', 1, 'dcn1');
INSERT INTO dim_site (id, name, status, dcn)
VALUES (2, 'CBS Sports', 1, 'dcn2');
INSERT INTO dim_site (id, name, status, dcn)
VALUES (3, 'mbc.net.dubai', 1, 'dcn3');
INSERT INTO dim_site (id, name, status, dcn, deleted)
VALUES (4, 'Fox Mobile', 1, 'dcn4', STR_TO_DATE('Jan 6, 2011', '%M %d, %Y'));
INSERT INTO dim_site (id, name, status, dcn)
VALUES (5, 'Discovery', 1, 'dcn5');

INSERT INTO dim_tag (id, name, status, tag_owner, site_id, adnet_id)
VALUES (1, 'Americas Next Top Model - Cycle 10', 0, 0, 1, 11);
INSERT INTO dim_tag (id, name, status, tag_owner, site_id, adnet_id)
VALUES (2, 'Aliens in America', 0, 0, 1, 13);
INSERT INTO dim_tag (id, name, status, tag_owner, site_id, adnet_id)
VALUES (3, '90210', 0, 0, 1, 17);
INSERT INTO dim_tag (id, name, status, tag_owner, site_id, adnet_id)
VALUES (4, 'NewAdMob', 0, 0, 0, 0);
INSERT INTO dim_tag (id, name, status, tag_owner, site_id, adnet_id, deleted)
VALUES (5, 'Aliens in America', 0, 0, 0, 0, STR_TO_DATE('Jan 6, 2011', '%M %d, %Y'));
INSERT INTO dim_tag (id, name, status, tag_owner, site_id, adnet_id)
VALUES (6, 'SomaBeauty', 0, 0, 0, 0);

INSERT INTO dim_adnet (id, name, status)
VALUES (1, 'Verisign Messaging Partner', 0);
INSERT INTO dim_adnet (id, name, status)
VALUES (2, 'ThirdScreenMedia', 1);
INSERT INTO dim_adnet (id, name, status)
VALUES (3, 'MilleniaMedia', 1);
INSERT INTO dim_adnet (id, name, status, deleted)
VALUES (4, 'AdMax Loopback Network1', 0, STR_TO_DATE('Jan 6, 2011', '%M %d, %Y'));
INSERT INTO dim_adnet (id, name, status)
VALUES (5, 'AdModa', 1);
INSERT INTO dim_adnet (id, name, status)
VALUES (6, 'AdFonic', 1);
INSERT INTO dim_adnet (id, name, status)
VALUES (7, 'iPromote', 1);

INSERT INTO dim_adnet (id, name, status, company_id)
VALUES (8, 'JumpTap', 0, 2004);


INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, buyer_id, first_seen,
                            creative_file_name, markup_file_name, ad_text, ad_creative_url,
                            ad_click_url)
VALUES ('ad_id1', 1, 1, 1, 10, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'pvol/a/b/c/test.jpg',
        'pvol/a/b/c/test.html', 'text', 'http://ad.creative.url', 'http://ad.click.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, first_seen, ad_text,
                            ad_creative_url)
VALUES ('ad_id2', 2, 1, 2, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'text',
        'http://ad.creative.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, buyer_id, first_seen,
                            creative_file_name, markup_file_name, ad_text, ad_creative_url,
                            ad_click_url)
VALUES ('ad_id1', 3, 3, 1, null, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'pvol/a/b/c/test.jpg',
        'pvol/a/b/c/test.html', 'text', 'http://ad.creative.url', 'http://ad.click.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, buyer_id, first_seen,
                            creative_file_name, markup_file_name, ad_text, ad_creative_url,
                            ad_click_url)
VALUES ('ad_id3', 4, 3, 2, 13, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'pvol/a/b/c/test.jpg',
        'pvol/a/b/c/test.html', 'text', 'http://ad.creative.url', 'http://ad.click.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, first_seen, ad_text,
                            ad_creative_url)
VALUES ('ad_id2', 5, 2, 2, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'text',
        'http://ad.creative.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, first_seen, ad_text,
                            ad_creative_url)
VALUES ('ad_id2', 6, 2, 3, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'text',
        'http://ad.creative.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, first_seen, ad_text,
                            ad_creative_url, ad_click_url)
VALUES ('ad_id1000', 1000, 1000, 2, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'text', 'rl',
        'ad.click.url');
INSERT INTO dim_screened_ad(screened_ad_id, pid, site_id, adnet_id, first_seen, ad_text,
                            ad_creative_url, ad_click_url)
VALUES ('ad_id1001', 1001, 1000, 2, STR_TO_DATE('Feb 27, 2012', '%M %d, %Y'), 'text', 'rl',
        'ad.click.url');

INSERT INTO dim_advertiser (id, name, status, company_id)
VALUES (1, 'advertiser1', 0, 2);
INSERT INTO dim_advertiser (id, name, status, company_id, deleted)
VALUES (2, 'advertiser2', 1, 2, STR_TO_DATE('Jan 6, 2011', '%M %d, %Y'));
INSERT INTO dim_advertiser (id, name, status, company_id)
VALUES (3, 'advertiser3', 2, 3);


INSERT INTO dim_campaign (id, name, status, ext_id, type, advertiser_id, company_id)
VALUES (1, 'campaign1', 0, 'jfkdjsajlkjsfkjej3f', 1, 2, 3);
INSERT INTO dim_campaign (id, name, status, ext_id, type, advertiser_id, company_id, deleted)
VALUES (2, 'campaign2', 1, 'jfkuureuioiowjfdsej3', 2, 3, 4,
        STR_TO_DATE('Jan 6, 2011', '%M %d, %Y'));
INSERT INTO dim_campaign (id, name, status, ext_id, type, advertiser_id, company_id)
VALUES (3, 'campaign3', 0, 'jmkjfdjsfjksaliuiojf', 5, 6, 7);

INSERT INTO dim_creative (id, name, status, advertiser_id, company_id)
VALUES (1, 'creative1', 0, 2, 3);
INSERT INTO dim_creative (id, name, status, advertiser_id, company_id, deleted)
VALUES (2, 'creative2', 1, 3, 4, STR_TO_DATE('Jan 6, 2011', '%M %d, %Y'));
INSERT INTO dim_creative (id, name, status, advertiser_id, company_id)
VALUES (3, 'creative3', 0, 6, 7);


