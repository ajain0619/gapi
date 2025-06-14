package com.nexage.app.services.impl.filter;

import static com.nexage.admin.core.model.filter.FilterList_.NAME;
import static com.nexage.admin.core.specification.FilterListSpecification.withCompanyId;
import static com.nexage.admin.core.specification.FilterListSpecification.withCompanyIdAndNameLike;
import static com.nexage.admin.core.specification.FilterListSpecification.withCompanyIdAndPid;

import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.admin.core.repository.BidderConfigDenyAllowFilterListRepository;
import com.nexage.admin.core.repository.FilterListRepository;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.FilterListMapper;
import com.nexage.app.mapper.FilterListTypeMapper;
import com.nexage.app.mapper.FilterListUploadStatusMapper;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.services.filter.FilterListService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@Transactional
public class FilterListServiceImpl implements FilterListService {

  private final FilterListRepository filterListRepository;

  private final FileSystemService fileSystemService;

  private final BidderConfigDenyAllowFilterListRepository bidderConfigDenyAllowFilterListRepository;

  public FilterListServiceImpl(
      FilterListRepository filterListRepository,
      @Qualifier(value = "genevaDataFileSystemService") FileSystemService fileSystemService,
      BidderConfigDenyAllowFilterListRepository bidderConfigDenyAllowFilterListRepository) {
    this.filterListRepository = filterListRepository;
    this.fileSystemService = fileSystemService;
    this.bidderConfigDenyAllowFilterListRepository = bidderConfigDenyAllowFilterListRepository;
  }

  private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");

  private static final String FILTERLIST_DOMAIN_CSV_FILE_PREFIX = "filterList/csv/";
  private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

  /** {@inheritDoc} */
  @Override
  public FilterListDTO createFilterList(FilterListDTO filterListDTO) {
    FilterList filterList =
        initializeFilterListForCreation(FilterListMapper.MAPPER.map(filterListDTO));
    FilterList createdFilterList;
    try {
      createdFilterList = filterListRepository.save(filterList);
    } catch (DataIntegrityViolationException constraintEx) {
      log.warn("Error creating filterList: {}", constraintEx.getMessage());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_FILTER_LIST_DUPLICATE_NAME);
    }
    return FilterListMapper.MAPPER.map(createdFilterList);
  }

  /** {@inheritDoc} */
  @Override
  public FilterListDTO getFilterList(Long buyerId, Integer pid) {
    return FilterListMapper.MAPPER.map(getFilterListEntity(buyerId, pid));
  }

  /** {@inheritDoc} */
  @Override
  public Page<FilterListDTO> getFilterLists(
      Long buyerId,
      Pageable pageable,
      Optional<Set<String>> qf,
      Optional<String> qt,
      Optional<FilterListTypeDTO> filterListTypeDTO,
      Optional<FilterListUploadStatusDTO> filterListUploadStatusDTO) {
    Optional<String> filterListName =
        qf.filter(fields -> fields.contains(NAME)).map(fields -> qt.orElse(""));
    Optional<FilterListType> filterListType =
        filterListTypeDTO.map(FilterListTypeMapper.MAPPER::map);
    Optional<FilterListUploadStatus> filterListUploadStatus =
        filterListUploadStatusDTO.map(FilterListUploadStatusMapper.MAPPER::map);
    return filterListRepository
        .findAll(
            filterListName
                .filter(StringUtils::isNotEmpty)
                .map(
                    name ->
                        withCompanyIdAndNameLike(
                            buyerId, name, filterListType, filterListUploadStatus))
                .orElseGet(() -> withCompanyId(buyerId, filterListType, filterListUploadStatus)),
            pageable)
        .map(FilterListMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public FilterListDTO deleteFilterList(Long buyerId, Integer pid) {
    FilterList filterList = getFilterListEntity(buyerId, pid);
    try {
      filterListRepository.save(prepareFilterListForSoftDelete(filterList));
      Optional.ofNullable(bidderConfigDenyAllowFilterListRepository.findAllByFilterListPid(pid))
          .ifPresent(
              bidderConfigFilterListAssignments ->
                  bidderConfigDenyAllowFilterListRepository.deleteInBatch(
                      bidderConfigFilterListAssignments));
    } catch (Exception exception) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_FILTER_LIST_DELETE_ERROR);
    }
    return FilterListDTO.builder().pid(pid).build();
  }

  /** {@inheritDoc} */
  @Override
  public FilterListDTO addFilterListCsv(Long buyerId, Integer pid, MultipartFile filterListCsv) {
    FilterList filterList = getFilterListEntity(buyerId, pid);
    try {
      writeFilterListCSVFile(buyerId, pid, filterListCsv, filterList.getType());
    } catch (Exception exception) {
      log.warn("Exception writing filterListCsv file for filterList pid: {}", pid, exception);
      throw new GenevaValidationException(
          FilterListType.DOMAIN.equals(filterList.getType())
              ? ServerErrorCodes.SERVER_FILTER_LIST_ADD_DOMAIN_ERROR
              : ServerErrorCodes.SERVER_FILTER_LIST_ADD_APP_ERROR);
    }
    return FilterListMapper.MAPPER.map(
        updateFilterListUploadStatus(filterList, FilterListUploadStatus.PENDING));
  }

  private FilterList prepareFilterListForSoftDelete(FilterList filterList) {
    filterList.setActive(false);
    filterList.setName(filterList.getName() + System.currentTimeMillis());
    return filterList;
  }

  private void writeFilterListCSVFile(
      Long buyerId,
      Integer filterListId,
      MultipartFile filterListCsv,
      FilterListType filterListType)
      throws IOException {
    try (InputStream filterListCsvStream = filterListCsv.getInputStream()) {
      fileSystemService.write(
          "",
          createFileName(buyerId, filterListId, filterListType),
          IOUtils.toByteArray(filterListCsvStream));
    } catch (Exception exception) {
      log.warn("Exception writing CSV to file system {}", exception);
      throw exception;
    }
  }

  private String createFileName(Long buyerId, Integer filterListId, FilterListType filterListType) {
    String type = FilterListType.DOMAIN.equals(filterListType) ? "domains" : "apps";
    return String.format(
        "%s%d_%d_%s_%s.csv",
        FILTERLIST_DOMAIN_CSV_FILE_PREFIX,
        buyerId,
        filterListId,
        dateFormat.format(new Date()),
        type);
  }

  private FilterList updateFilterListUploadStatus(
      FilterList filterList, FilterListUploadStatus uploadStatus) {
    filterList.setUploadStatus(uploadStatus);
    filterList.setUpdated(new Date(Instant.now(Clock.system(EST_ZONE)).toEpochMilli()));
    return filterListRepository.save(filterList);
  }

  private FilterList getFilterListEntity(Long buyerId, Integer pid) {
    return filterListRepository
        .findOne(withCompanyIdAndPid(buyerId, pid))
        .orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_FILTER_LIST_NOT_FOUND));
  }

  private FilterList initializeFilterListForCreation(FilterList filterList) {
    filterList.setUploadStatus(FilterListUploadStatus.READY);
    filterList.setInvalid(0);
    filterList.setDuplicate(0);
    filterList.setError(0);
    filterList.setTotal(0);
    return filterList;
  }
}
