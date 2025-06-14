package com.nexage.admin.core.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum VideoSupport {
  BANNER(0),
  VIDEO(1),
  VIDEO_AND_BANNER(2),
  NATIVE(3);

  private int value;

  VideoSupport(int value) {
    this.value = value;
  }

  public static VideoSupport fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, VideoSupport> fromIntMap = new HashMap<>();

  static {
    for (VideoSupport protocol : VideoSupport.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }

  public static final List<VideoSupport> WITH_VIDEO = Arrays.asList(VIDEO, VIDEO_AND_BANNER);
}
