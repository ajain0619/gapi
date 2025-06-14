INSERT INTO company (id, pid, version, name, type, url, description, enable_cpi_tracking, enable_rtb,
            enable_mediation, rtb_revenue_report_enabled, selfserve_allowed,default_rtb_profiles_enabled,
            dynamic_buyer_registration_enabled,brxd_buyer_id_enabled_on_bid_request, status, disable_ad_feedback,
            external_ad_verification_enabled, third_party_fraud_detection_enabled, fraud_detection_javascript_enabled)
VALUES ('8a858acb012c2c608ee1608ee8cb3015', 1, 1, 'Nexage Inc', 'NEXAGE', 'www.nexage.com',
        'Mobile Ad Mediation', 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0);

INSERT INTO site (pid, ad_screening, consumer_profile_contributed, consumer_profile_used, creation_date, days_free, dcn,
            description, domain, filter_bots ,  input_date_format , last_update,
            live ,  name ,  override_ip ,  platform ,  report_batch_size ,  report_frequency ,  revenue_launch_date ,
            rules_update_frequency, status ,  total_timeout ,  traffic_throttle ,  type ,  url ,  version,
            company_pid ,  buyer_timeout ,  id ,  enable_groups ,  coppa_restricted ,  ad_truth)
VALUES (1, 0, 1, 1, '2018-06-10 12:25:18', '0', 'vmbc', 'Site description with name-1', 'site.com', 1, 'yyyyMMdd',
        '2013-04-18 10:44:35', 1, 'Site with name-1', 0, 'OTHER', '180000', '180000', '2018-06-10 12:25:18',
        '1800000', '1', '5000', '0', 'MOBILE_WEB', 'ssfl', '0', '1', '1000', '4028811s1a242984011a74276dd12eee',
        0, 0, 0);

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (1, 'position1', 1, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position1');

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (2, 'position2', 1, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position2');

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (3, 'position3', 1, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position3');

INSERT INTO position (pid, name, site_pid, status, mraid_adv_tracking, screen_location,
                      video_support, mraid_support, version, placement_type, ad_size, height, width,
                      memo)
VALUES (4, 'position4', 1, 1, 1, 1, 1, 1, 1, 4, '400x800', 400, 800, 'position4');

INSERT INTO placement_video (pid, version, vast_version, vpaid_support, wrapper_support, failover_support,
            file_formats, player_required, longform)
VALUES (1, 1, '0', 0, 1, 0, '0', 0, false);

INSERT INTO placement_video (pid, version, vast_version, vpaid_support, wrapper_support, failover_support,
            file_formats, player_required, longform)
VALUES (3, 1, '2', 0, 1, 0, '0', 0, false);

INSERT INTO placement_video (pid, version, vast_version, vpaid_support, wrapper_support, failover_support,
            file_formats, player_required, player_height, player_width, longform, stream_type, player_brand, ssai, multi_impression_bid, competitive_separation)
VALUES (4, 1, '2', 0, 1, 0, '0', 1, 320, 480, true, 0, 'test_player', 2, true, false);

INSERT INTO placement_video_companion (pid, version, placement_video_pid, height, width )
VALUES (10, 1, 3, 320, 480);

INSERT INTO placement_video_companion (pid, version, placement_video_pid, height, width )
VALUES (11, 1, 4, 480, 640);

INSERT INTO placement_video_companion (pid, version, placement_video_pid, height, width )
VALUES (12, 1, 4, 480, 640);
