package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.dto.transparency.TransparencySettingsEntity;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.TransparencyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.ErrorCode;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class TransparencyServiceImpl implements TransparencyService {

  private final UserContext userContext;
  private final CompanyRepository companyRepository;

  public void validateTransparencySettingsForSite(
      Long publisherId,
      Site siteDTO,
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity) {
    boolean transparencyManagmentEnabled = isTransparencyManagmentEnabled(publisherId);
    if (!transparencyManagmentEnabled) {
      boolean notValid =
          validateTransparencySettingsWhenTransparencySettingsDisabled(
              siteDTO, transparencySettings, transparencySettingsEntity);
      if (notValid) {
        throw new GenevaSecurityException(ServerErrorCodes.SERVER_TRANSPARENCY_SETTINGS_DISABLED);
      }
    }

    validateTransparencySettings(transparencySettings, transparencySettingsEntity);
  }

  public void validateTransparencySettingsForRTBProfile(
      Long publisherId,
      RTBProfile rtbProfile,
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity) {
    boolean transparencyManagmentEnabled = isTransparencyManagmentEnabled(publisherId);
    if (!transparencyManagmentEnabled) {
      boolean notValid =
          validateTransparencySettingsWhenTransparencySettingsDisabled(
              rtbProfile, transparencySettings, transparencySettingsEntity);
      if (notValid) {
        throw new GenevaSecurityException(ServerErrorCodes.SERVER_TRANSPARENCY_SETTINGS_DISABLED);
      }
    }

    validateTransparencySettings(transparencySettings, transparencySettingsEntity);
  }

  private void validateTransparencySettings(
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity) {
    if (transparencySettings != null) {
      TransparencyMode transparencyMode = transparencySettings.getTransparencyMode();
      if (transparencyMode == null) {
        throw new GenevaValidationException(
            getTransparencyModeErrorMessage(transparencySettingsEntity));
      } else {
        switch (transparencyMode) {
          case None:
            {
              if (StringUtils.isNotBlank(transparencySettings.getNameAlias())) {
                throw new GenevaValidationException(
                    getNameAliasErrorMessage(transparencySettingsEntity));
              }
              break;
            }
          case Aliases:
            {
              if (StringUtils.isBlank(transparencySettings.getNameAlias())) {
                throw new GenevaValidationException(
                    getNameAliasErrorMessage(transparencySettingsEntity));
              }
              break;
            }
          case RealName:
            {
              if (StringUtils.isNotBlank(transparencySettings.getNameAlias())) {
                throw new GenevaValidationException(
                    getNameAliasErrorMessage(transparencySettingsEntity));
              }
            }
        }
      }
    }
  }

  public boolean isTransparencyManagmentEnabled(Long publisherId) {
    boolean transparencyManagmentEnabled = userContext.isNexageUser();

    if (!transparencyManagmentEnabled) {
      Company publisherCompany =
          companyRepository
              .findById(publisherId)
              .orElseThrow(
                  () -> {
                    log.info("Company not found in database: " + publisherId);
                    return new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
                  });
      if (publisherCompany.getSellerAttributes() != null
          && publisherCompany.getSellerAttributes().getTransparencyMgmtEnablement()
              == TransparencyMgmtEnablement.ENABLED.getId()) {
        transparencyManagmentEnabled = true;
      }
    }
    return transparencyManagmentEnabled;
  }

  public Long generateIdAlias() {
    return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
  }

  private boolean validateTransparencySettingsWhenTransparencySettingsDisabled(
      Site siteDTO,
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity) {
    boolean hasErrors = false;
    switch (transparencySettingsEntity) {
      case SITE:
        {
          hasErrors =
              isTransparencySettingsEqualsPrevValues(
                  transparencySettings,
                  siteDTO.getIncludeSiteName(),
                  siteDTO.getSiteAliasId(),
                  siteDTO.getSiteNameAlias());
          break;
        }
      case PUBLISHER:
        {
          hasErrors =
              isTransparencySettingsEqualsPrevValues(
                  transparencySettings,
                  siteDTO.getIncludePubName(),
                  siteDTO.getPubAliasId(),
                  siteDTO.getPubNameAlias());
          break;
        }
    }
    return hasErrors;
  }

  private boolean validateTransparencySettingsWhenTransparencySettingsDisabled(
      RTBProfile rtbProfile,
      TransparencySettingsDTO transparencySettings,
      TransparencySettingsEntity transparencySettingsEntity) {
    boolean hasErrors = false;
    switch (transparencySettingsEntity) {
      case SITE:
        {
          hasErrors =
              isTransparencySettingsEqualsPrevValues(
                  transparencySettings,
                  rtbProfile.getIncludeSiteName(),
                  rtbProfile.getSiteAlias(),
                  rtbProfile.getSiteNameAlias());
          break;
        }
      case PUBLISHER:
        {
          hasErrors =
              isTransparencySettingsEqualsPrevValues(
                  transparencySettings,
                  rtbProfile.getIncludePubName(),
                  rtbProfile.getPubAlias(),
                  rtbProfile.getPubNameAlias());
          break;
        }
    }
    return hasErrors;
  }

  public boolean isTransparencySettingsEqualsPrevValues(
      TransparencySettingsDTO transparencySettings,
      Integer prevTransparencyModeId,
      Long prevIdAlias,
      String prevNameAlias) {
    boolean hasErrors = false;
    if (transparencySettings == null
        && (prevTransparencyModeId != null || prevIdAlias != null || prevNameAlias != null)) {
      hasErrors = true;
    } else if (transparencySettings != null) {
      if ((transparencySettings.getTransparencyMode() == null && prevTransparencyModeId != null)
          || (transparencySettings.getTransparencyMode() != null
              && (prevTransparencyModeId == null
                  || transparencySettings.getTransparencyMode().asInt()
                      != prevTransparencyModeId))) {
        hasErrors = true;
      } else if ((transparencySettings.getIdAlias() == null && prevIdAlias != null)
          || (transparencySettings.getIdAlias() != null
              && (prevIdAlias == null
                  || transparencySettings.getIdAlias().longValue() != prevIdAlias))) {
        hasErrors = true;
      } else if ((transparencySettings.getNameAlias() == null && prevNameAlias != null)
          || (transparencySettings.getNameAlias() != null
              && (prevNameAlias == null
                  || !StringUtils.equals(transparencySettings.getNameAlias(), prevNameAlias)))) {
        hasErrors = true;
      }
    }
    return hasErrors;
  }

  private ServerErrorCodes getTransparencyModeErrorMessage(
      TransparencySettingsEntity transparencySettingsEntity) {
    ServerErrorCodes errorMessages = null;
    switch (transparencySettingsEntity) {
      case SITE:
        {
          errorMessages = ServerErrorCodes.SERVER_TRANSPARENCY_SITE_MODE;
          break;
        }
      case PUBLISHER:
        {
          errorMessages = ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_MODE;
          break;
        }
    }
    return errorMessages;
  }

  private ServerErrorCodes getNameAliasErrorMessage(
      TransparencySettingsEntity transparencySettingsEntity) {
    ServerErrorCodes errorMessages = null;
    switch (transparencySettingsEntity) {
      case SITE:
        {
          errorMessages = ServerErrorCodes.SERVER_TRANSPARENCY_SITE_NAME_ALIAS_ERROR;
          break;
        }
      case PUBLISHER:
        {
          errorMessages = ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_NAME_ALIAS_ERROR;
          break;
        }
    }
    return errorMessages;
  }

  public void validateTransparencySettings(SellerAttributes sellerAttributes) {
    TransparencyMode transparencyMode =
        TransparencyMode.fromInt(sellerAttributes.getIncludePubName());
    if (transparencyMode != null) {
      switch (transparencyMode) {
        case None:
          validateIsNotEmpty(
              sellerAttributes.getPubAliasId(),
              ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
          validateIsEmpty(
              sellerAttributes.getPubNameAlias(),
              ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_NAME_ALIAS_ERROR);
          break;
        case RealName:
          validateIsEmpty(
              sellerAttributes.getPubNameAlias(),
              ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_NAME_ALIAS_ERROR);
          validateIsEmpty(
              sellerAttributes.getPubAliasId(),
              ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
          break;
        case Aliases:
          validateIsNotEmpty(
              sellerAttributes.getPubNameAlias(),
              ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_NAME_ALIAS_ERROR);
          validateIsNotEmpty(
              sellerAttributes.getPubAliasId(),
              ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
          break;
        default:
      }
    } else {
      validateIsEmpty(
          sellerAttributes.getIncludePubName(),
          ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_MODE);
      validateIsEmpty(
          sellerAttributes.getPubNameAlias(),
          ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_NAME_ALIAS_ERROR);
      validateIsEmpty(
          sellerAttributes.getPubAliasId(),
          ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
    }
  }

  private void validateIsNotEmpty(Object parameter, ServerErrorCodes errorMessage) {
    if (parameter == null) {
      throw new GenevaValidationException(errorMessage);
    }
  }

  private void validateIsEmpty(Object parameter, ErrorCode errorCode) {
    if (parameter != null) {
      throw new GenevaSecurityException(errorCode);
    }
  }

  /**
   * validate transparency setting changing for non nexage user.
   *
   * @param inSellerAttributes - seller attributes of the company.
   * @param dbSellerAttributes - seller attributes of the company from db.
   * @throws GenevaValidationException
   */
  public void validateTransparencyMgmtChangingByRole(
      SellerAttributes inSellerAttributes, SellerAttributes dbSellerAttributes) {
    if (!userContext.isNexageUser()) {
      if (dbSellerAttributes == null) {
        if (inSellerAttributes.getTransparencyMgmtEnablement()
            != TransparencyMgmtEnablement.DISABLED.getId()) {
          log.error("Cannot update transparency mgmt flag for non nexage user");
          throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
        }
        validateIsEmpty(
            inSellerAttributes.getIncludePubName(), SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
        validateIsEmpty(
            inSellerAttributes.getPubAliasId(), SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
        validateIsEmpty(
            inSellerAttributes.getPubNameAlias(), SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      } else {
        if (inSellerAttributes.getTransparencyMgmtEnablement()
            != dbSellerAttributes.getTransparencyMgmtEnablement()) {
          log.error("Cannot update transparency mgmt flag for non nexage user");
          throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
        }
        if (dbSellerAttributes.getTransparencyMgmtEnablement()
            != TransparencyMgmtEnablement.ENABLED.getId()) {
          if (!(dbSellerAttributes.getIncludePubName() == null
                  ? inSellerAttributes.getIncludePubName() == null
                  : dbSellerAttributes
                      .getIncludePubName()
                      .equals(inSellerAttributes.getIncludePubName()))
              || !StringUtils.equals(
                  dbSellerAttributes.getPubNameAlias(), inSellerAttributes.getPubNameAlias())) {
            throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
          }
        }
      }
    }
  }

  /**
   * Generate alias id for seller attributes if TransparencyMode = Alias
   *
   * @param sellerAttributes - target seller attributes
   */
  public void regenerateAliasIdForBlindAndAlias(SellerAttributes sellerAttributes) {
    TransparencyMode mode = TransparencyMode.fromInt(sellerAttributes.getIncludePubName());
    if (TransparencyMode.Aliases.equals(mode) || TransparencyMode.None.equals(mode)) {
      sellerAttributes.setPubAliasId(generateIdAlias());
    }
  }
}
