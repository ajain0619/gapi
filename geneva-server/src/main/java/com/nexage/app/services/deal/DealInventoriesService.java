package com.nexage.app.services.deal;

import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.dto.deals.DealInventoryDownloadResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface DealInventoriesService {

  /**
   * Saves a {@link MultipartFile} CSV/XLS/XLSX file containing Domain/App bundles/AppAlias to S3
   * Bucket - Will be processed by Lambda
   *
   * @param fileName {@link String}
   * @param fileType {@link DealInventoryType}
   * @param inventoriesFile {@link MultipartFile} CSV or XLS or XLSX File of Domain/App
   *     bundles/AppAlias
   * @param dealId {@link Long}
   * @return {@link DealInventoriesDTO}of type {@link Page} {@link DealInventoriesDTO}.
   */
  DealInventoriesDTO uploadDealInventories(
      String fileName, DealInventoryType fileType, MultipartFile inventoriesFile, Long dealId);

  /**
   * Returns a {@link DealInventoryDownloadResponseDTO} having XLS file containing Domain or App
   * Alias or App Bundle
   *
   * @param filePid {@link long}
   * @param dealPid {@link long}
   * @return {@link DealInventoryDownloadResponseDTO} of type.
   */
  DealInventoryDownloadResponseDTO downloadDealInventories(long filePid, long dealPid);

  /**
   * Delete a CSV/XLS/XLSX file containing Domain/App bundles/AppAlias
   *
   * @param filePid {@link long}
   * @param dealId {@link long}
   */
  void deleteDealInventories(long filePid, long dealId);
}
