package com.nexage.app.services.impl;

import static com.nexage.admin.core.specification.DoohScreenSpecification.withSellerPid;

import com.nexage.admin.core.model.DoohScreen;
import com.nexage.admin.core.repository.DoohScreenRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.DoohScreenDTOMapper;
import com.nexage.app.metric.FileUploadErrorMetered;
import com.nexage.app.metric.FileUploadSuccessMetered;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.DoohScreenService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenDeleteBatchCallback;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenInsertBatchCallback;
import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j
public class DoohScreenServiceImpl implements DoohScreenService {

  @Value("${geneva.server.doohscreen.create.limit}")
  private int doohScreenCreateLimit;

  private final DoohScreenInsertBatchCallback doohScreenInsertBatchCallback;

  private final DoohScreenDeleteBatchCallback doohScreenDeleteBatchCallback;
  private final BeanValidationService beanValidationService;
  private final DoohScreenRepository doohScreenRepository;

  public DoohScreenServiceImpl(
      DoohScreenInsertBatchCallback doohScreenInsertBatchCallback,
      DoohScreenDeleteBatchCallback doohScreenDeleteBatchCallback,
      BeanValidationService beanValidationService,
      DoohScreenRepository doohScreenRepository) {
    this.doohScreenInsertBatchCallback = doohScreenInsertBatchCallback;
    this.doohScreenDeleteBatchCallback = doohScreenDeleteBatchCallback;
    this.beanValidationService = beanValidationService;
    this.doohScreenRepository = doohScreenRepository;
  }

  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#sellerPid) and (@loginUserContext.isOcAdminNexage() or "
          + "@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller() "
          + "or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller())")
  @FileUploadErrorMetered
  @FileUploadSuccessMetered
  /** {@inheritDoc} */
  public int replaceDoohScreens(Long sellerPid, MultipartFile screens) {
    List<DoohScreenDTO> doohScreenDtos;

    try {
      doohScreenDtos = DoohScreenDTOMapper.MAPPER.map(screens);

    } catch (IOException ioException) {
      log.error("Error reading/mapping Dooh Screens file", ioException);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_FORMAT);
    }
    if (doohScreenDtos.size() > doohScreenCreateLimit) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DOOH_SCREENS_MAX_LIMIT);
    } else if (CollectionUtils.isEmpty(doohScreenDtos)) {
      return 0;
    }
    doohScreenDtos.forEach(setAndValidate(sellerPid));

    doohScreenDeleteBatchCallback.execute(sellerPid);
    return doohScreenInsertBatchCallback
        .setDoohScreens(DoohScreenDTOMapper.MAPPER.map(doohScreenDtos))
        .execute(sellerPid);
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#sellerPid) and (@loginUserContext.isOcAdminNexage() or "
          + "@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller() "
          + "or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller())")
  /** {@inheritDoc} */
  public Page<DoohScreenDTO> getDoohScreens(Pageable pageable, Long sellerPid) {
    Page<DoohScreen> screens =
        doohScreenRepository.findAll(Specification.where(withSellerPid(sellerPid)), pageable);
    return screens.map(DoohScreenDTOMapper.MAPPER::map);
  }

  private Consumer<DoohScreenDTO> setAndValidate(Long sellerPid) {
    return doohScreenDTO -> {
      doohScreenDTO.setSellerPid(sellerPid);
      doohScreenDTO.setSspScreenId(sellerPid + "-" + doohScreenDTO.getSellerScreenId());
      beanValidationService.validate(doohScreenDTO);
    };
  }
}
