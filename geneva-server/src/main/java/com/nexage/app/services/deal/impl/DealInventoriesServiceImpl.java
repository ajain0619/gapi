package com.nexage.app.services.deal.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nexage.admin.core.model.DealInventory;
import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.admin.core.repository.DealAppAliasRepository;
import com.nexage.admin.core.repository.DealAppBundleDataRepository;
import com.nexage.admin.core.repository.DealDomainRepository;
import com.nexage.admin.core.repository.DealInventoryRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.dto.deals.DealInventoryDownloadResponseDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DealInventoriesDTOMapper;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.services.deal.DealInventoriesService;
import com.nexage.app.util.XlsUtils;
import com.nexage.app.util.validator.deals.DealInventoriesFileValidator;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
@Transactional
public class DealInventoriesServiceImpl implements DealInventoriesService {

  private static final String BASE_FILE_NAME_FORMAT = "deal-inventory-files/%s";

  private final FileSystemService fileSystemService;
  private final DealInventoryRepository dealInventoryRepository;
  private final DealAppAliasRepository dealAppAliasRepository;
  private final DealAppBundleDataRepository dealAppBundleDataRepository;
  private final DealDomainRepository dealDomainRepository;
  private final DealInventoriesFileValidator dealInventoriesFileValidator;
  private final DirectDealRepository directDealRepository;

  public DealInventoriesServiceImpl(
      @Qualifier(value = "genevaDataFileSystemService") FileSystemService fileSystemService,
      DealInventoryRepository dealInventoryRepository,
      DealAppAliasRepository dealAppAliasRepository,
      DealAppBundleDataRepository dealAppBundleDataRepository,
      DealDomainRepository dealDomainRepository,
      DealInventoriesFileValidator dealInventoriesFileValidator,
      DirectDealRepository directDealRepository) {
    this.fileSystemService = fileSystemService;
    this.dealInventoryRepository = dealInventoryRepository;
    this.dealAppAliasRepository = dealAppAliasRepository;
    this.dealAppBundleDataRepository = dealAppBundleDataRepository;
    this.dealDomainRepository = dealDomainRepository;
    this.dealInventoriesFileValidator = dealInventoriesFileValidator;
    this.directDealRepository = directDealRepository;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() OR @loginUserContext.isOcManagerNexage() "
          + "OR @loginUserContext.isOcManagerYieldNexage() "
          + "OR @loginUserContext.isOcManagerSmartexNexage() "
          + "OR @loginUserContext.isOcUserNexage()"
          + "OR @loginUserContext.isOcAdminSeller()")
  public DealInventoriesDTO uploadDealInventories(
      String fileName, DealInventoryType fileType, MultipartFile inventoriesFile, Long dealId) {

    return uploadFile(fileName, fileType, inventoriesFile, dealId);
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminSeller()")
  public DealInventoryDownloadResponseDTO downloadDealInventories(long filePid, long dealPid) {

    return downloadInventoriesFile(filePid, dealPid);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() OR @loginUserContext.isOcManagerNexage() "
          + "OR @loginUserContext.isOcManagerYieldNexage() "
          + "OR @loginUserContext.isOcManagerSmartexNexage() "
          + "OR @loginUserContext.isOcUserNexage()"
          + "OR @loginUserContext.isOcAdminSeller()")
  public void deleteDealInventories(long filePid, long dealId) {
    var dealInventoryOptional = dealInventoryRepository.findByPid(filePid);
    if (dealInventoryOptional.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND);
    }

    dealInventoryRepository.deleteByPid(filePid);

    var directDealOptional = directDealRepository.findByDealId(Long.toString(dealId));
    if (directDealOptional.isPresent()) {
      var directDeal = directDealOptional.get();
      String placementFormulaJson = directDeal.getPlacementFormula();

      if (placementFormulaJson != null) {
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode placementFormulaNode = objectMapper.readTree(placementFormulaJson);
          ArrayNode formulaGroups = (ArrayNode) placementFormulaNode.get("formulaGroups");
          ArrayNode formulaRules = (ArrayNode) formulaGroups.get(0).get("formulaRules");

          for (int i = 0; i < formulaRules.size(); i++) {
            JsonNode rule = formulaRules.get(i);
            String ruleData = rule.get("ruleData").asText();
            if (ruleData.contains("fileName")) {
              JsonNode ruleDataNode = objectMapper.readTree(ruleData);
              if (ruleDataNode.get("pid").asLong() == filePid) {
                formulaRules.remove(i);
                break;
              }
            }
          }

          if (formulaRules.size() == 0) {
            directDeal.setPlacementFormula(null);
          } else {
            directDeal.setPlacementFormula(objectMapper.writeValueAsString(placementFormulaNode));
          }
        } catch (Exception e) {
          throw new GenevaAppRuntimeException(
              ServerErrorCodes.SERVER_DEAL_MEDIA_FILE_UPLOAD_INTERNAL_ERROR);
        }
      }
      directDealRepository.save(directDeal);
    }
  }

  private DealInventoriesDTO uploadFile(
      String fileName, DealInventoryType fileType, MultipartFile inventoriesFile, Long dealId) {

    dealInventoriesFileValidator.validateDealInventoriesFile(fileName, fileType, inventoriesFile);

    var uuidGen = new UUIDGenerator();
    var fileId = uuidGen.generateUniqueId() + "_" + dealId;

    boolean alreadyUploaded =
        dealInventoryRepository.existsByFileTypeAndDealIdAppended(fileType, dealId);
    if (alreadyUploaded) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_DEAL_INVENTORY_LIST_EXISTS);
    }

    try (var inventoriesFileInputStream = inventoriesFile.getInputStream()) {
      fileSystemService.write(
          "",
          String.format(BASE_FILE_NAME_FORMAT, fileId),
          IOUtils.toByteArray(inventoriesFileInputStream));

    } catch (Exception exception) {
      throw new GenevaAppRuntimeException(
          ServerErrorCodes.SERVER_DEAL_MEDIA_FILE_UPLOAD_INTERNAL_ERROR);
    }

    var dealInventory = new DealInventory();
    dealInventory.setFileName(fileName);
    dealInventory.setFileId(fileId);
    dealInventory.setFileType(fileType);

    return DealInventoriesDTOMapper.MAPPER.map(dealInventoryRepository.saveAndFlush(dealInventory));
  }

  private DealInventoryDownloadResponseDTO downloadInventoriesFile(long filePid, Long dealPid) {

    List<String> inventoryData = null;

    if (!directDealRepository.existsByPid(dealPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND);
    }

    var dealInventory =
        dealInventoryRepository
            .findByPid(filePid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND));

    var fileType = dealInventory.getFileType();
    var fileName = dealInventory.getFileName();

    if (StringUtils.isEmpty(fileName)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND);
    }

    if (fileName.endsWith(".csv")) {
      fileName = StringUtils.removeEnd(fileName, ".csv") + ".xls";
    } else if (fileName.endsWith(".xlsx")) {
      fileName = StringUtils.removeEnd(fileName, "x");
    }

    if (fileType == DealInventoryType.DOMAIN) {
      inventoryData = dealDomainRepository.getDealDomainsByDealPid(dealPid);
    } else if (fileType == DealInventoryType.APP_ALIAS) {
      inventoryData = dealAppAliasRepository.getDealAppAliasesByDealPid(dealPid);
    } else { // APP_BUNDLE
      inventoryData = dealAppBundleDataRepository.getDealAppBundleDataByDealPid(dealPid);
    }

    if (CollectionUtils.isEmpty(inventoryData)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND);
    }

    var xlsFile = XlsUtils.writeXlsFile(inventoryData, fileType.toString());
    var dealInventoryXlsFileByteResource = new ByteArrayResource(xlsFile.toByteArray());

    var response = new DealInventoryDownloadResponseDTO();
    response.setInventoryFileByteResource(dealInventoryXlsFileByteResource);
    response.setFileName(fileName);

    return response;
  }
}
