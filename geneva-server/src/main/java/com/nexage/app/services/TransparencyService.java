package com.nexage.app.services;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.dto.transparency.TransparencySettingsEntity;

public interface TransparencyService {

  void validateTransparencySettingsForSite(
      Long publisherId,
      Site site,
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity);

  void validateTransparencySettingsForRTBProfile(
      Long publisherId,
      RTBProfile rtbProfile,
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity);

  Long generateIdAlias();

  boolean isTransparencyManagmentEnabled(Long publisherId);

  void validateTransparencySettings(SellerAttributes sellerAttributes);

  void validateTransparencyMgmtChangingByRole(
      SellerAttributes inSellerAttributes, SellerAttributes dbSellerAttributes);

  void regenerateAliasIdForBlindAndAlias(SellerAttributes sellerAttributes);
}
