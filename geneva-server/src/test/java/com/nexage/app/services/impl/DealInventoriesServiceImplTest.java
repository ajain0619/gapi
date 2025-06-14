package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DealInventory;
import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DealAppAliasRepository;
import com.nexage.admin.core.repository.DealAppBundleDataRepository;
import com.nexage.admin.core.repository.DealDomainRepository;
import com.nexage.admin.core.repository.DealInventoryRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.dto.deals.DealInventoryDownloadResponseDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.services.deal.impl.DealInventoriesServiceImpl;
import com.nexage.app.util.XlsUtils;
import com.nexage.app.util.validator.deals.DealInventoriesFileValidator;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DealInventoriesServiceImplTest {

  @InjectMocks private DealInventoriesServiceImpl dealMediaFilesServiceImpl;

  @Mock private DealInventoryRepository dealInventoryRepository;
  @Mock private DealInventoriesFileValidator dealInventoriesFileValidator;
  @Mock private MultipartFile csvFile;
  @Mock private FileSystemService fileSystemService;
  @Mock private DealDomainRepository dealDomainRepository;
  @Mock private DealAppBundleDataRepository dealAppBundleDataRepository;
  @Mock private DealAppAliasRepository dealAppAliasRepository;
  @Mock private DirectDealRepository directDealRepository;
  private static final long FILE_ID = 123;
  private static final long DEAL_PID = 12;

  @Test
  void shouldVerifyCorrectFileLocationFolderForUploadDomainFile() throws IOException {

    String fileName = "Domains.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "yahooinc.com,aol.com";
    Long dealId = 1L;

    when(csvFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    dealMediaFilesServiceImpl.uploadDealInventories(fileName, fileType, csvFile, dealId);
    verify(fileSystemService)
        .write(eq(""), startsWith("deal-inventory-files"), eq(data.getBytes()));
  }

  @Test
  void shouldReturnExceptionWhileUploadingFile() throws IOException {

    String fileName = "Domains.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    Long dealId = 1L;

    var thrown =
        assertThrows(
            GenevaAppRuntimeException.class,
            () ->
                dealMediaFilesServiceImpl.uploadDealInventories(
                    fileName, fileType, csvFile, dealId));

    assertEquals(
        ServerErrorCodes.SERVER_DEAL_MEDIA_FILE_UPLOAD_INTERNAL_ERROR, thrown.getErrorCode());
  }

  @Test
  void shouldReturnDealMediaDTOForUploadingDomainFile() throws IOException {

    String fileName = "Domains.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "yahooinc.com,aol.com";
    Long dealId = 1L;

    DealInventory dealInventory = new DealInventory();
    dealInventory.setFileName(fileName);
    dealInventory.setFileType(fileType);

    when(dealInventoryRepository.saveAndFlush(any())).thenReturn(dealInventory);
    when(csvFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    DealInventoriesDTO dealInventoriesDTO =
        dealMediaFilesServiceImpl.uploadDealInventories(fileName, fileType, csvFile, dealId);

    assertEquals(fileName, dealInventoriesDTO.getFileName());
    assertEquals(fileType, dealInventoriesDTO.getFileType());
  }

  @Test
  void shouldReturnXlsFileByteResourceForDownloadTheDomainInventoryFile() {
    String fileName = "domain.csv";
    String expectedFileName = "domain.xls";
    List<String> dealDomains = List.of("yahoo.com", "aol.com", "vvk.com");
    ByteArrayOutputStream expectedXlsFile =
        XlsUtils.writeXlsFile(dealDomains, DealInventoryType.DOMAIN.toString());
    ByteArrayResource expectedXlsFileByteResource =
        new ByteArrayResource(expectedXlsFile.toByteArray());

    createDownloadInventoryMock(fileName, DealInventoryType.DOMAIN);

    when(dealDomainRepository.getDealDomainsByDealPid(DEAL_PID)).thenReturn(dealDomains);

    validateDownloadInventoryResponse(expectedFileName, expectedXlsFileByteResource);
  }

  @Test
  void
      shouldReturnXlsExtensionForDownloadTheDomainInventoryFileWithSameFileExtensionForXlsUploadedFile() {
    String fileName = "domain_excel.xls";
    List<String> dealDomains = List.of("yahoo.com", "aol.com", "vvk.com");
    ByteArrayOutputStream expectedXlsFile =
        XlsUtils.writeXlsFile(dealDomains, DealInventoryType.DOMAIN.toString());
    ByteArrayResource expectedXlsFileByteResource =
        new ByteArrayResource(expectedXlsFile.toByteArray());

    createDownloadInventoryMock(fileName, DealInventoryType.DOMAIN);

    when(dealDomainRepository.getDealDomainsByDealPid(DEAL_PID)).thenReturn(dealDomains);

    validateDownloadInventoryResponse(fileName, expectedXlsFileByteResource);
  }

  @Test
  void
      shouldReturnXlsExtensionForDownloadTheDomainInventoryFileWithXlsxFileExtensionForUploadedFile() {
    String fileName = "domain.xlsx";
    String expectedFileName = "domain.xls";
    List<String> dealDomains = List.of("yahoo.com", "aol.com", "vvk.com");
    ByteArrayOutputStream expectedXlsFile =
        XlsUtils.writeXlsFile(dealDomains, DealInventoryType.DOMAIN.toString());
    ByteArrayResource expectedXlsFileByteResource =
        new ByteArrayResource(expectedXlsFile.toByteArray());

    createDownloadInventoryMock(fileName, DealInventoryType.DOMAIN);

    when(dealDomainRepository.getDealDomainsByDealPid(DEAL_PID)).thenReturn(dealDomains);

    validateDownloadInventoryResponse(expectedFileName, expectedXlsFileByteResource);
  }

  @Test
  void shouldReturnXlsFileByteResourceForDownloadTheAppAliasInventoryFile() {
    String fileName = "appNames.csv";
    String expectedFileName = "appNames.xls";
    List<String> dealAppAliases = List.of("Samsung TV Plus", "Pluto TV", "Paramount Plus");
    ByteArrayOutputStream expectedXlsFile =
        XlsUtils.writeXlsFile(dealAppAliases, DealInventoryType.APP_ALIAS.toString());
    ByteArrayResource expectedXlsFileByteResource =
        new ByteArrayResource(expectedXlsFile.toByteArray());

    createDownloadInventoryMock(fileName, DealInventoryType.APP_ALIAS);

    when(dealAppAliasRepository.getDealAppAliasesByDealPid(DEAL_PID)).thenReturn(dealAppAliases);

    validateDownloadInventoryResponse(expectedFileName, expectedXlsFileByteResource);
  }

  @Test
  void shouldReturnXlsFileByteResourceForDownloadTheAppBundleIdInventoryFile() {
    String fileName = "appBundles.csv";
    String expectedFileName = "appBundles.xls";
    List<String> dealAppBundleIds =
        List.of("com.roku.sportsmax", "com.wurl.samsungtvplus.absolutereality", "tv.pluto.freebox");
    ByteArrayOutputStream xlsFile =
        XlsUtils.writeXlsFile(dealAppBundleIds, DealInventoryType.APP_BUNDLE.toString());
    ByteArrayResource expectedXlsFileByteResource = new ByteArrayResource(xlsFile.toByteArray());

    createDownloadInventoryMock(fileName, DealInventoryType.APP_BUNDLE);

    when(dealAppBundleDataRepository.getDealAppBundleDataByDealPid(DEAL_PID))
        .thenReturn(dealAppBundleIds);

    validateDownloadInventoryResponse(expectedFileName, expectedXlsFileByteResource);
  }

  @Test
  void shouldReturnExceptionForDownloadInventoryFileWhenDealPidIsInvalid() {
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealMediaFilesServiceImpl.downloadDealInventories(FILE_ID, DEAL_PID));

    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  void shouldReturnExceptionForDownloadInventoryFileWhenFileIdIsInvalid() {
    when(directDealRepository.existsByPid(DEAL_PID)).thenReturn(true);

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealMediaFilesServiceImpl.downloadDealInventories(FILE_ID, DEAL_PID));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  void shouldReturnExceptionForDownloadInventoryFileWhenFileNameIsEmpty() {
    when(directDealRepository.existsByPid(DEAL_PID)).thenReturn(true);
    when(dealInventoryRepository.findByPid(any())).thenReturn(Optional.of(new DealInventory()));

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealMediaFilesServiceImpl.downloadDealInventories(FILE_ID, DEAL_PID));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  void shouldReturnExceptionForDownloadInventoryFileWhenEmptyFilename() {
    createDownloadInventoryMock(" ", DealInventoryType.DOMAIN);

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealMediaFilesServiceImpl.downloadDealInventories(FILE_ID, DEAL_PID));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  void shouldReturnExceptionForDownloadInventoryFileWhenNoInventoryDataInCoreDB() {
    createDownloadInventoryMock("Domains.csv", DealInventoryType.DOMAIN);

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealMediaFilesServiceImpl.downloadDealInventories(FILE_ID, DEAL_PID));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenFileTypeAlreadyUploaded() {
    String fileName = "Domains.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    Long dealId = 1L;

    when(dealInventoryRepository.existsByFileTypeAndDealIdAppended(fileType, dealId))
        .thenReturn(true);

    var exception =
        assertThrows(
            GenevaAppRuntimeException.class,
            () ->
                dealMediaFilesServiceImpl.uploadDealInventories(
                    fileName, fileType, csvFile, dealId));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_LIST_EXISTS, exception.getErrorCode());
    verify(dealInventoryRepository).existsByFileTypeAndDealIdAppended(fileType, dealId);
  }

  @Test
  void shouldAllowUploadWhenFileTypeNotYetUploaded() throws IOException {
    String fileName = "Domains.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "example.com,anotherexample.com";
    Long dealId = 1L;

    DealInventory dealInventory = new DealInventory();
    dealInventory.setFileName(fileName);
    dealInventory.setFileType(fileType);

    when(dealInventoryRepository.existsByFileTypeAndDealIdAppended(fileType, dealId))
        .thenReturn(false);
    when(csvFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));
    when(dealInventoryRepository.saveAndFlush(any(DealInventory.class))).thenReturn(dealInventory);

    DealInventoriesDTO result =
        dealMediaFilesServiceImpl.uploadDealInventories(fileName, fileType, csvFile, dealId);

    assertEquals(fileName, result.getFileName());
    assertEquals(fileType, result.getFileType());
    verify(dealInventoryRepository).existsByFileTypeAndDealIdAppended(fileType, dealId);
    verify(dealInventoryRepository).saveAndFlush(any(DealInventory.class));
  }

  @Test
  void shouldDeleteDealInventoriesWithSingleFormulaRule() {
    long fileId = 123L;
    long dealId = 1L;

    DealInventory dealInventory = new DealInventory();
    dealInventory.setPid(fileId);
    dealInventory.setFileId("fileId");

    DirectDeal directDeal = new DirectDeal();
    directDeal.setDealId(Long.toString(dealId));
    directDeal.setPlacementFormula(
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"DOMAIN\",\"operator\":\"EQUALS\",\"ruleData\":\"{\\\"pid\\\":123,\\\"fileName\\\":\\\"test.csv\\\",\\\"fileType\\\":\\\"DOMAIN\\\"}\",\"attributePid\":null}]}]}");

    when(dealInventoryRepository.findByPid(fileId)).thenReturn(Optional.of(dealInventory));
    when(directDealRepository.findByDealId(Long.toString(dealId)))
        .thenReturn(Optional.of(directDeal));

    dealMediaFilesServiceImpl.deleteDealInventories(fileId, dealId);

    verify(dealInventoryRepository).deleteByPid(fileId);
    verify(directDealRepository).findByDealId(Long.toString(dealId));

    ArgumentCaptor<DirectDeal> directDealArgumentCaptor = ArgumentCaptor.forClass(DirectDeal.class);
    verify(directDealRepository).save(directDealArgumentCaptor.capture());

    DirectDeal savedDirectDeal = directDealArgumentCaptor.getValue();
    assertNull(savedDirectDeal.getPlacementFormula());
  }

  @Test
  void shouldDeleteDealInventoriesWithMultipleFormulaRules() {
    long fileId = 123L;
    long dealId = 1L;

    DealInventory dealInventory = new DealInventory();
    dealInventory.setPid(fileId);
    dealInventory.setFileId("fileId");

    DirectDeal directDeal = new DirectDeal();
    directDeal.setDealId(Long.toString(dealId));
    directDeal.setPlacementFormula(
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"DOMAIN\",\"operator\":\"EQUALS\",\"ruleData\":\"{\\\"pid\\\":123,\\\"fileName\\\":\\\"test.csv\\\",\\\"fileType\\\":\\\"DOMAIN\\\"}\",\"attributePid\":null},{\"attribute\":\"APP_BUNDLE\",\"operator\":\"EQUALS\",\"ruleData\":\"{\\\"pid\\\":52,\\\"fileName\\\":\\\"test.csv\\\",\\\"fileType\\\":\\\"APP_BUNDLE\\\"}\",\"attributePid\":null}]}]}");

    when(dealInventoryRepository.findByPid(fileId)).thenReturn(Optional.of(dealInventory));
    when(directDealRepository.findByDealId(Long.toString(dealId)))
        .thenReturn(Optional.of(directDeal));

    dealMediaFilesServiceImpl.deleteDealInventories(fileId, dealId);

    verify(dealInventoryRepository).deleteByPid(fileId);
    verify(directDealRepository).findByDealId(Long.toString(dealId));

    ArgumentCaptor<DirectDeal> directDealArgumentCaptor = ArgumentCaptor.forClass(DirectDeal.class);
    verify(directDealRepository).save(directDealArgumentCaptor.capture());

    DirectDeal savedDirectDeal = directDealArgumentCaptor.getValue();
    String expectedPlacementFormula =
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"APP_BUNDLE\",\"operator\":\"EQUALS\",\"ruleData\":\"{\\\"pid\\\":52,\\\"fileName\\\":\\\"test.csv\\\",\\\"fileType\\\":\\\"APP_BUNDLE\\\"}\",\"attributePid\":null}]}]}";
    assertEquals(expectedPlacementFormula, savedDirectDeal.getPlacementFormula());
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistingDealInventories() {
    long fileId = 123L;
    long dealId = 1L;

    when(dealInventoryRepository.findByPid(fileId)).thenReturn(Optional.empty());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dealMediaFilesServiceImpl.deleteDealInventories(fileId, dealId));

    assertEquals(ServerErrorCodes.SERVER_DEAL_INVENTORY_FILE_NOT_FOUND, exception.getErrorCode());
    verify(dealInventoryRepository, never()).deleteByPid(any());
  }

  private void createDownloadInventoryMock(String fileName, DealInventoryType inventoryType) {
    DealInventory dealInventory = new DealInventory();
    dealInventory.setPid(FILE_ID);
    dealInventory.setFileType(inventoryType);
    dealInventory.setFileName(fileName);

    when(directDealRepository.existsByPid(DEAL_PID)).thenReturn(true);
    when(dealInventoryRepository.findByPid(FILE_ID)).thenReturn(Optional.of(dealInventory));
  }

  private void validateDownloadInventoryResponse(
      String expectedFileName, ByteArrayResource expectedXlsFileByteResource) {
    DealInventoryDownloadResponseDTO dealInventoryDownloadResponse =
        dealMediaFilesServiceImpl.downloadDealInventories(FILE_ID, DEAL_PID);

    assertEquals(
        expectedXlsFileByteResource,
        dealInventoryDownloadResponse.getInventoryFileByteResource(),
        "Expected XLS File data is not same as the downloaded inventory file data");

    assertEquals(
        expectedFileName,
        dealInventoryDownloadResponse.getFileName(),
        "Expected file name is not same as downloaded inventory file name");
  }
}
