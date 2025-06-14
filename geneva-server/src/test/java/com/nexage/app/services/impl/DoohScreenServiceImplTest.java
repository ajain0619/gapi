package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.model.DoohScreen;
import com.nexage.admin.core.repository.DoohScreenRepository;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenDeleteBatchCallback;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenInsertBatchCallback;
import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import org.apache.tika.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DoohScreenServiceImplTest {
  private static final String VALID_SCREENS_JSON_NAME = "/schema/dooh/valid-screens.json";
  private static final String MAX_SCREENS_JSON_NAME = "/schema/dooh/max-limit-screens.json";

  @Mock private DoohScreenInsertBatchCallback doohScreenInsertBatchCallback;

  @Mock private DoohScreenDeleteBatchCallback doohScreenDeleteBatchCallback;
  @Mock private DoohScreenRepository doohScreenRepository;
  @Mock private BeanValidationService validationService;
  @InjectMocks private DoohScreenServiceImpl screenService;

  @BeforeEach
  public void setUp() {
    screenService =
        new DoohScreenServiceImpl(
            doohScreenInsertBatchCallback,
            doohScreenDeleteBatchCallback,
            validationService,
            doohScreenRepository);
    ReflectionTestUtils.setField(screenService, "doohScreenCreateLimit", 5);
  }

  @Test
  void shouldReturnCountWhenCreatingScreens() throws IOException {
    var sellerPid = 123L;
    InputStream inputStream = ResourceLoader.class.getResourceAsStream(VALID_SCREENS_JSON_NAME);
    MultipartFile screensFile = new MockMultipartFile("screensFile", inputStream);

    when(doohScreenInsertBatchCallback.setDoohScreens(any(List.class)))
        .thenReturn(doohScreenInsertBatchCallback);
    when(doohScreenInsertBatchCallback.execute(anyLong())).thenReturn(2);

    int count = screenService.replaceDoohScreens(sellerPid, screensFile);
    verify(doohScreenInsertBatchCallback).execute(anyLong());
    assertEquals(2, count);
  }

  @Test
  void shouldThrowExceptionWhenValidating() throws IOException {
    MultipartFile file = new MockMultipartFile("invalidScreens", IOUtils.toInputStream("[{}]"));
    doThrow(EntityConstraintViolationException.class)
        .when(validationService)
        .validate(any(DoohScreenDTO.class));
    assertThrows(
        EntityConstraintViolationException.class,
        () -> screenService.replaceDoohScreens(16L, file));
  }

  @Test
  void shouldThrowExceptionWhenCreatingScreensAndFileIsMalformed() {
    MultipartFile file = new MockMultipartFile("screensFile", new byte[0]);
    assertThrows(
        GenevaValidationException.class, () -> screenService.replaceDoohScreens(123L, file));
  }

  @Test
  void shouldReturn0AndNotDeleteWhenCreatingScreensWithEmptyFile() throws IOException {
    assertEquals(
        0,
        screenService.replaceDoohScreens(
            123L, new MockMultipartFile("screensFile", IOUtils.toInputStream("[]"))));

    verify(doohScreenDeleteBatchCallback, never()).execute(anyLong());
  }

  @Test
  void shouldThrowExceptionWhenNumberOfCreatedScreensExceedsLimit() throws IOException {
    InputStream inputStream = ResourceLoader.class.getResourceAsStream(MAX_SCREENS_JSON_NAME);
    MultipartFile screensFile = new MockMultipartFile("screensFile", inputStream);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> screenService.replaceDoohScreens(123L, screensFile));
    assertEquals(ServerErrorCodes.SERVER_DOOH_SCREENS_MAX_LIMIT, exception.getErrorCode());
  }

  @Test
  void shouldReturnAllScreensWhenGetDoohScreensIsCalled() {
    Long sellerPid = 812L;
    Pageable pageable = PageRequest.of(0, 10);

    ImmutableList<DoohScreen> doohScreens =
        ImmutableList.of(
            new DoohScreen() {
              {
                this.setPid(1L);
                this.setSellerPid(812L);
                this.setSellerScreenId("812-screen-id-1");
                this.setSellerScreenName("Billboard");
                this.setFloorPrice(BigDecimal.valueOf(5.6));
              }
            },
            new DoohScreen() {
              {
                this.setPid(2L);
                this.setSellerPid(812L);
                this.setSellerScreenId("812-screen-id-2");
                this.setSellerScreenName("NY-Metro-Billboard");
                this.setFloorPrice(BigDecimal.valueOf(3.4));
              }
            });

    given(doohScreenRepository.findAll(any(Specification.class), eq(pageable)))
        .willReturn(new PageImpl<>(doohScreens, pageable, doohScreens.size()));

    Page<DoohScreenDTO> dtos = screenService.getDoohScreens(pageable, sellerPid);

    assertEquals(2, dtos.getContent().size());
    assertEquals(doohScreens.get(0).getPid(), dtos.getContent().get(0).getPid());
    assertEquals(doohScreens.get(1).getPid(), dtos.getContent().get(1).getPid());
    assertEquals(doohScreens.get(1).getFloorPrice(), dtos.getContent().get(1).getFloorPrice());
  }
}
