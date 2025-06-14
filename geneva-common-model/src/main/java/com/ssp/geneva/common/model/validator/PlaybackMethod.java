package com.ssp.geneva.common.model.validator;

public enum PlaybackMethod {
  AUTOPLAY_SOUND_ON(1),
  AUTOPLAY_SOUND_OFF(2),
  CLICK_TO_PLAY(3),
  MOUSE_OVER(4);

  private final int method;

  PlaybackMethod(int method) {
    this.method = method;
  }

  public static boolean isValidValue(int playbackMethod) {
    return playbackMethod >= PlaybackMethod.AUTOPLAY_SOUND_ON.getValue()
        && playbackMethod <= PlaybackMethod.MOUSE_OVER.getValue();
  }

  public int getValue() {
    return method;
  }
}
