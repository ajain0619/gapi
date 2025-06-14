package com.nexage.app.dto.seller;

import com.nexage.admin.core.enums.VideoLinearity;

public interface PlacementCommonDTO {
  VideoLinearity getVideoLinearity();

  void setVideoLinearity(VideoLinearity videoLinearity);

  PlacementVideoDTO getPlacementVideo();
}
