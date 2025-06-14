package com.nexage.app.dto.seller.nativeads;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.admin.core.enums.nativeads.NativeAssetType;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.AssetImageType;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeDataAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeImageAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeTitleAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeVideoAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.VideoProtocols;
import com.nexage.app.dto.seller.nativeads.enums.DataType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NativeTestUtils {

  private static NativeAssetSetDTO getNativeAssetSetDTO(
      NativeAssetType type, NativeAssetRule assetRule) {
    NativeAssetDTO assetDTO = null;

    switch (type) {
      case TITLE:
        assetDTO = getNativeTitleAssetDTO();
        break;
      case IMAGE:
        assetDTO = getNativeImageAssetDTO();
        break;
      case VIDEO:
        assetDTO = getNativeVideoAssetDTO();
        break;
      case DATA:
        assetDTO = getNativeDataAssetDTO();
        break;
    }

    Set<NativeAssetDTO> assets = ImmutableSet.of(assetDTO);
    NativeAssetSetDTO nativeAssetSetDTO = new NativeAssetSetDTO();
    nativeAssetSetDTO.setAssets(assets);
    nativeAssetSetDTO.setRule(assetRule);
    return nativeAssetSetDTO;
  }

  public static NativeAssetSetDTO getNativeImageAssetSetDTO() {
    return getNativeAssetSetDTO(NativeAssetType.IMAGE, NativeAssetRule.REQ_ONE_PLUS);
  }

  public static NativeAssetSetDTO getNativeTitleAssetSetDTO() {
    return getNativeAssetSetDTO(NativeAssetType.TITLE, NativeAssetRule.REQ_ALL);
  }

  public static NativeAssetSetDTO getNativeVideoAssetSetDTO() {
    return getNativeAssetSetDTO(NativeAssetType.VIDEO, NativeAssetRule.REQ_ONE_PLUS);
  }

  public static NativeAssetSetDTO getNativeDataAssetSetDTO() {
    return getNativeAssetSetDTO(NativeAssetType.DATA, NativeAssetRule.REQ_NONE);
  }

  public static NativeTitleAssetDTO getNativeTitleAssetDTO() {
    NativeTitleAssetDTO assetDTO = new NativeTitleAssetDTO();
    assetDTO.setKey("myTitle");
    assetDTO.setType(NativeAssetType.TITLE);
    assetDTO.getTitle().setMaxLength(75);
    return assetDTO;
  }

  public static NativeDataAssetDTO getNativeDataAssetDTO() {
    NativeDataAssetDTO assetDTO = new NativeDataAssetDTO();
    assetDTO.setKey("mySalesPriceData");
    assetDTO.setType(NativeAssetType.DATA);
    assetDTO.getData().setType(DataType.SALE_PRICE);
    assetDTO.getData().setMaxLength(120);
    return assetDTO;
  }

  public static NativeImageAssetDTO getNativeImageAssetDTO() {
    NativeImageAssetDTO assetDTO = new NativeImageAssetDTO();
    assetDTO.setKey("myImage");
    assetDTO.setType(NativeAssetType.IMAGE);
    assetDTO.getImage().setType(AssetImageType.MAIN);
    assetDTO.getImage().setWidth(200);
    assetDTO.getImage().setWidthMinimum(150);
    assetDTO.getImage().setHeight(300);
    assetDTO.getImage().setHeightMinimum(280);
    return assetDTO;
  }

  public static NativeVideoAssetDTO getNativeVideoAssetDTO() {
    NativeVideoAssetDTO assetDTO = new NativeVideoAssetDTO();
    assetDTO.setKey("myVideo");
    assetDTO.setType(NativeAssetType.VIDEO);
    assetDTO.getVideo().setMinDuration(10);
    assetDTO.getVideo().setMaxDuration(25);
    assetDTO
        .getVideo()
        .setProtocols(
            new HashSet<>(Arrays.asList(VideoProtocols.VAST_3_0, VideoProtocols.VAST_3_0_WRAPPER)));
    return assetDTO;
  }
}
