package com.nexage.app.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RtbProfileLibsAndTagsDTO implements Serializable {

  private static final long serialVersionUID = 42L;

  @Builder.Default private List<Long> tagPid = new ArrayList<>();
  @Builder.Default private List<Long> rtbProfileLibPid = new ArrayList<>();
  @Builder.Default private List<Long> removedTagPid = new ArrayList<>();
}
