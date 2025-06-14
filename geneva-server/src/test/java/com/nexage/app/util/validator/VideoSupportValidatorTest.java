package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.VideoSupport;
import org.junit.jupiter.api.Test;

class VideoSupportValidatorTest {

  @Test
  void shouldTestIsChangingFromVideoPosition() {
    VideoSupport prevVideoSupport = VideoSupport.VIDEO;
    VideoSupport newVideoSupport = null;
    assertFalse(
        VideoSupportValidator.isChangingFromVideoPosition(prevVideoSupport, newVideoSupport));

    newVideoSupport = VideoSupport.BANNER;
    assertTrue(
        VideoSupportValidator.isChangingFromVideoPosition(prevVideoSupport, newVideoSupport));

    newVideoSupport = VideoSupport.NATIVE;
    assertTrue(
        VideoSupportValidator.isChangingFromVideoPosition(prevVideoSupport, newVideoSupport));

    prevVideoSupport = null;
    newVideoSupport = VideoSupport.BANNER;
    assertFalse(
        VideoSupportValidator.isChangingFromVideoPosition(prevVideoSupport, newVideoSupport));

    prevVideoSupport = VideoSupport.VIDEO;
    assertTrue(
        VideoSupportValidator.isChangingFromVideoPosition(prevVideoSupport, newVideoSupport));

    prevVideoSupport = VideoSupport.VIDEO_AND_BANNER;
    assertTrue(
        VideoSupportValidator.isChangingFromVideoPosition(prevVideoSupport, newVideoSupport));
  }

  @Test
  void shouldTestIsChangingToVideoPosition() {
    VideoSupport prevVideoSupport = VideoSupport.BANNER;
    VideoSupport newVideoSupport = null;

    assertFalse(VideoSupportValidator.isChangingToVideoPosition(prevVideoSupport, newVideoSupport));

    newVideoSupport = VideoSupport.VIDEO;
    assertTrue(VideoSupportValidator.isChangingToVideoPosition(prevVideoSupport, newVideoSupport));

    newVideoSupport = VideoSupport.VIDEO_AND_BANNER;
    assertTrue(VideoSupportValidator.isChangingToVideoPosition(prevVideoSupport, newVideoSupport));

    prevVideoSupport = null;
    assertFalse(VideoSupportValidator.isChangingToVideoPosition(prevVideoSupport, newVideoSupport));

    prevVideoSupport = VideoSupport.BANNER;
    assertTrue(VideoSupportValidator.isChangingToVideoPosition(prevVideoSupport, newVideoSupport));

    prevVideoSupport = VideoSupport.NATIVE;
    assertTrue(VideoSupportValidator.isChangingToVideoPosition(prevVideoSupport, newVideoSupport));
  }
}
