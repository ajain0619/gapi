package com.nexage.admin.core.dto;

public interface TagHierarchyDto {
  Long getTagPid();

  String getTagName();

  Integer getTierLevel();

  Long getTierPid();

  Short getTierType();

  Integer getBelongsToRtbGroup();

  @Deprecated(since = "SSP-22461", forRemoval = true)
  Boolean getFilterBiddersWhitelist();

  Boolean getFilterBiddersAllowlist();

  Boolean getUseDefaultBlock();

  Boolean getUseDefaultBidders();
}
