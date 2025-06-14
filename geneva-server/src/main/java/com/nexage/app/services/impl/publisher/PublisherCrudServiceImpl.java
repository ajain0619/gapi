package com.nexage.app.services.impl.publisher;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerCompanyService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.impl.BaseCrudService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.publisher.PublisherAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Objects;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/** Base implementation of the publisher CRUD service. */
@Transactional
public abstract class PublisherCrudServiceImpl extends BaseCrudService<PublisherDTO, Long> {

  private final CompanyService companyService;
  private final RtbProfileGroupService rtbProfileGroupService;
  private final TransparencyService transparencyService;
  private final UserContext userContext;
  private final RTBProfileService rtbProfileService;
  private final HbPartnerCompanyService hbPartnerCompanyService;
  private final CompanyRepository companyRepository;
  private final RevenueShareUpdateValidator revenueShareUpdateValidator;

  protected PublisherCrudServiceImpl(
      CompanyService companyService,
      RtbProfileGroupService rtbProfileGroupService,
      TransparencyService transparencyService,
      UserContext userContext,
      RTBProfileService rtbProfileService,
      HbPartnerCompanyService hbPartnerCompanyService,
      CompanyRepository companyRepository,
      RevenueShareUpdateValidator revenueShareUpdateValidator) {
    this.companyService = companyService;
    this.rtbProfileGroupService = rtbProfileGroupService;
    this.transparencyService = transparencyService;
    this.userContext = userContext;
    this.rtbProfileService = rtbProfileService;
    this.hbPartnerCompanyService = hbPartnerCompanyService;
    this.companyRepository = companyRepository;
    this.revenueShareUpdateValidator = revenueShareUpdateValidator;
  }

  protected abstract PublisherAssembler getAssembler();

  @Override
  public PublisherDTO create(PublisherDTO inPublisher) {
    Company company = getAssembler().applyTransparencySettings(new Company(), inPublisher);
    transparencyService.validateTransparencyMgmtChangingByRole(company.getSellerAttributes(), null);

    boolean canUpdateTransparency = false;
    if (inPublisher.getAttributes() != null) {
      rtbProfileGroupService.mergeIndividualsGroups(inPublisher.getAttributes());
      if (TransparencyMgmtEnablement.ENABLED.equals(
          inPublisher.getAttributes().getDefaultTransparencyMgmtEnablement())) {
        canUpdateTransparency = true;
      }
    }
    company = getAssembler().applyHbPartnerAttributes(company, inPublisher);

    getAssembler().apply(NullableContext.nullableContext, company, inPublisher);

    applyTransarencySettings(inPublisher, canUpdateTransparency, company);
    companyService.addContact(company, company.getContactUserPid());
    if (company.getSellerAttributes() != null && inPublisher.getAttributes() != null) {
      company
          .getSellerAttributes()
          .setDefaultRtbProfile(
              rtbProfileService.processCompanyDefaultRtbProfile(
                  null,
                  inPublisher.getAttributes().getDefaultRtbProfile(),
                  company.getSellerAttributes().getDefaultRtbProfile(),
                  company));
    }
    company = companyService.createCompany(company);
    PublisherDTO publisher = getAssembler().make(NullableContext.nullableContext, company);
    if (inPublisher.getAttributes() != null) {
      rtbProfileGroupService.separateIndividualsGroups(publisher.getAttributes());
    }
    return publisher;
  }

  @Override
  public PublisherDTO read(Long entityIdentifier) {
    Company company = companyService.getCompany(entityIdentifier);
    if (company == null) {
      throw new RuntimeException(String.format("No company found with pid=%d", entityIdentifier));
    }

    PublisherDTO publisher = getAssembler().make(NullableContext.nullableContext, company);

    if (publisher.getAttributes() != null) {
      rtbProfileGroupService.separateIndividualsGroups(publisher.getAttributes());
    }
    return publisher;
  }

  @Override
  public PublisherDTO update(PublisherDTO inPublisher, Long publisherId) {
    long entityIdentifier = inPublisher.getPid();
    Company companyDB = companyService.getCompany(entityIdentifier);

    if (companyDB == null) {
      throw new RuntimeException(String.format("No company found with pid=%d", entityIdentifier));
    }

    if (!companyDB.getVersion().equals(inPublisher.getVersion())) {
      throw new StaleStateException("Publisher has a different version than the input data.");
    }
    if (!Objects.equals(
        companyDB.getCurrency(),
        ObjectUtils.defaultIfNull(
            inPublisher.getCurrency(), companyService.getDefaultCurrencyCode()))) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    if (companyDB.getSellerAttributes() != null) {
      if (inPublisher.getAttributes() == null) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_IS_EMPTY);
      } else if (companyDB.getSellerAttributes().getVersion()
          != inPublisher.getAttributes().getVersion()) {
        throw new StaleStateException(
            "Publisher Seller Attributes has a different version than the input data.");
      }

      if (revenueShareUpdateValidator.isRevenueShareUpdate(
              companyDB.getSellerAttributes(), inPublisher.getAttributes())
          && !userContext.isOcManagerYieldNexage()) {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }

      checkSmartExchangeAttributesVersion(
          companyDB.getSellerAttributes().getSmartExchangeAttributes(),
          inPublisher.getAttributes().getSmartExchangeAttributes());
    }

    if (companyDB.isDefaultRtbProfilesEnabled() != inPublisher.getDefaultRtbProfilesEnabled()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_COMPANY_DEFAULT_RTB_PROFILE_ENABLED_FLAG_CANT_BE_UPDATED);
    }

    if (!StringUtils.defaultString(companyDB.getName()).equalsIgnoreCase(inPublisher.getName())) {
      checkDuplicatedCompanyName(inPublisher.getName(), inPublisher.getType());
    }

    if (inPublisher.getAttributes() != null) {
      rtbProfileGroupService.mergeIndividualsGroups(inPublisher.getAttributes());
    }

    // validate incoming transparency settings with data stored into DB
    Company company = getAssembler().applyTransparencySettings(new Company(), inPublisher);

    boolean updatePfo = false;
    boolean canUpdateTransparency =
        companyDB.getSellerAttributes() != null
            && (userContext.isNexageUser()
                || companyDB.getSellerAttributes() == null
                || companyDB.getSellerAttributes().getTransparencyMgmtEnablement()
                    == TransparencyMgmtEnablement.ENABLED.getId());
    // override Transparency Settings for non nexage and transparency is disabled and income
    // transparency settings is null

    if (inPublisher.getAttributes() != null
        && (companyDB.getSellerAttributes() == null
            || !Objects.equals(
                inPublisher.getAttributes().getPfoEnabled(),
                companyDB.getSellerAttributes().isPfoEnabled()))) {
      updatePfo = true;
    }

    transparencyService.validateTransparencyMgmtChangingByRole(
        company.getSellerAttributes(), companyDB.getSellerAttributes());

    company = getAssembler().apply(NullableContext.nullableContext, companyDB, inPublisher);

    applyTransarencySettings(inPublisher, canUpdateTransparency, company);
    companyService.addContact(company, company.getContactUserPid());

    if (company.getSellerAttributes() != null) {
      RTBProfile rtbProfile =
          rtbProfileService.processCompanyDefaultRtbProfile(
              entityIdentifier,
              inPublisher.getAttributes().getDefaultRtbProfile(),
              company.getSellerAttributes().getDefaultRtbProfile(),
              company);
      company.getSellerAttributes().setDefaultRtbProfile(rtbProfile);
    }

    hbPartnerCompanyService.validateHbPartnerAssociations(company, inPublisher);
    company = getAssembler().applyHbPartnerAttributes(company, inPublisher);

    company = companyService.updateCompanyAndReload(company);
    if (updatePfo) {
      companyService.togglePfo(company.getPid(), company.getSellerAttributes().isPfoEnabled());
    }

    PublisherDTO publisher = getAssembler().make(NullableContext.nullableContext, company);
    if (inPublisher.getAttributes() != null) {
      rtbProfileGroupService.separateIndividualsGroups(publisher.getAttributes());
    }
    return publisher;
  }

  private void checkSmartExchangeAttributesVersion(
      SmartExchangeAttributes smartExchangeAttributes,
      SmartExchangeAttributesDTO smartExchangeAttributesDTO) {
    if (smartExchangeAttributes != null
        && smartExchangeAttributesDTO != null
        && !smartExchangeAttributes.getVersion().equals(smartExchangeAttributesDTO.getVersion())) {
      throw new StaleStateException(
          "Smart Exchange Attributes has a different version than the input data.");
    }
  }

  private void checkDuplicatedCompanyName(String companyName, CompanyType companyType) {
    if (companyRepository.existsByNameAndType(companyName, companyType)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_COMPANY_NAME);
    }
  }

  private void applyTransarencySettings(
      PublisherDTO inPublisher, boolean canUpdateTransparency, Company outCompany) {
    // if regenerate flag is true OR transparency mode is in [Aliases, None] and aliasId is empty -
    // regenerate aliasId

    if (canUpdateTransparency) {
      TransparencyMode mode =
          TransparencyMode.fromInt(outCompany.getSellerAttributes().getIncludePubName());
      if (mode != null) {
        if (inPublisher.getAttributes() != null
            && inPublisher.getAttributes().getDefaultTransparencySettings() != null) {
          switch (mode) {
            case RealName:
              {
                if (inPublisher.getAttributes().getDefaultTransparencySettings().getIdAlias()
                    == null) {
                  outCompany.getSellerAttributes().setPubAliasId(null);
                } else {
                  throw new GenevaValidationException(
                      ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
                }
                break;
              }
            case None:
            case Aliases:
              {
                if (BooleanUtils.isTrue(
                        inPublisher
                            .getAttributes()
                            .getDefaultTransparencySettings()
                            .getRegenerateIdAlias())
                    || inPublisher.getAttributes().getDefaultTransparencySettings().getIdAlias()
                        == null) {
                  outCompany
                      .getSellerAttributes()
                      .setPubAliasId(transparencyService.generateIdAlias());
                } else if (BooleanUtils.isFalse(
                        inPublisher
                            .getAttributes()
                            .getDefaultTransparencySettings()
                            .getRegenerateIdAlias())
                    && inPublisher.getAttributes().getDefaultTransparencySettings().getIdAlias()
                        != null) {
                  outCompany
                      .getSellerAttributes()
                      .setPubAliasId(
                          inPublisher
                              .getAttributes()
                              .getDefaultTransparencySettings()
                              .getIdAlias());
                }
                break;
              }
          }
        } else {
          if (inPublisher.getAttributes() != null
              && inPublisher.getAttributes().getDefaultTransparencySettings() != null) {
            if (inPublisher.getAttributes().getDefaultTransparencySettings().getIdAlias() == null) {
              outCompany.getSellerAttributes().setPubAliasId(null);
            } else {
              throw new GenevaValidationException(
                  ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
            }
          }
        }
        transparencyService.validateTransparencySettings(outCompany.getSellerAttributes());
      }
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcAdminNexage()")
  public void delete(Long entityIdentifier) {
    companyService.softDeleteCompany(entityIdentifier);
  }
}
