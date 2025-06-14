package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.VideoSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoSupportValidator {

  /**
   * Finds if we placement is changing from video placement (VIDEO/VIDEO_AND_BANNER) to non video
   * placement (BANNER/NATIVE)
   *
   * @param prevVideoSupport {@link VideoSupport}
   * @param newVideoSupport {@link VideoSupport}
   * @return {@link boolean}
   */
  public static boolean isChangingFromVideoPosition(
      VideoSupport prevVideoSupport, VideoSupport newVideoSupport) {
    boolean isUpdateToNonvideo =
        newVideoSupport != null
            && (newVideoSupport == VideoSupport.BANNER || newVideoSupport == VideoSupport.NATIVE);
    boolean isPositionCurrentlyVideo =
        prevVideoSupport != null
            && (prevVideoSupport == VideoSupport.VIDEO
                || prevVideoSupport == VideoSupport.VIDEO_AND_BANNER);
    return isPositionCurrentlyVideo && isUpdateToNonvideo;
  }

  /**
   * Finds if we placement is changing to video placement (VIDEO/VIDEO_AND_BANNER) from non video
   * placement (BANNER/NATIVE)
   *
   * @param prevVideoSupport {@link VideoSupport}
   * @param newVideoSupport {@link VideoSupport}
   * @return {@link boolean}
   */
  public static boolean isChangingToVideoPosition(
      VideoSupport prevVideoSupport, VideoSupport newVideoSupport) {
    boolean isUpdateToVideo =
        newVideoSupport != null
            && (newVideoSupport == VideoSupport.VIDEO
                || newVideoSupport == VideoSupport.VIDEO_AND_BANNER);
    boolean isPositionCurrentlyNotVideo =
        prevVideoSupport != null
            && (prevVideoSupport == VideoSupport.BANNER || prevVideoSupport == VideoSupport.NATIVE);
    return isPositionCurrentlyNotVideo && isUpdateToVideo;
  }
}
