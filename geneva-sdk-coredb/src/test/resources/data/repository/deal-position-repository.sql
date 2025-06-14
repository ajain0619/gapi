
INSERT INTO position(pid, name,site_pid, status, mraid_support,video_support,screen_location, mraid_adv_tracking, version) VALUES(2,'footer',1,1,1,1,0,false,1);
INSERT INTO position(pid, name,site_pid, status, mraid_support,video_support,screen_location, mraid_adv_tracking, version) VALUES(3,'header',1,1,1,1,0,false,1);

INSERT INTO deal_position(pid, deal_pid, position_pid, version ) VALUES (1, 1, 2,0);
INSERT INTO deal_position(pid, deal_pid, position_pid, version ) VALUES (2, 1, 3,0);
INSERT INTO deal_position(pid, deal_pid, position_pid, version ) VALUES (3, 2, 2,0);
