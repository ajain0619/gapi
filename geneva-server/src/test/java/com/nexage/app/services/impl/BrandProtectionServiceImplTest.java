package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTagValues;
import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.admin.core.repository.BrandProtectionCategoryRepository;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.admin.core.repository.BrandProtectionTagValuesRepository;
import com.nexage.admin.core.repository.CrsTagMappingRepository;
import com.nexage.app.dto.CrsTagMappingDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionCategoryDto;
import com.nexage.app.dto.brand.protection.BrandProtectionTagDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionTagValuesDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrandProtectionServiceImplTest {

  @Mock private BrandProtectionTagRepository tagRepository;
  @Mock private BrandProtectionCategoryRepository categoryRepository;
  @Mock private BrandProtectionTagValuesRepository tagValuesRepository;
  @Mock private CrsTagMappingRepository tagMappingRepository;
  @InjectMocks private BrandProtectionServiceImpl brandProtectionService;

  private static final boolean FREE_TEXT_TAG = false;
  private static final String TAG_NAME = "tag-name";
  private static final String CATEGORY_NAME = "category-name";
  private static final Date UPDATE_DATE = new Date();
  private static final long PARENT_TAG_PID = 100L;
  private static final String RTB_ID = "rtb-id";
  private static final long CATEGORY_PID = 200L;
  private static final long TAG_PID = 1L;
  private static final String TAG_VALUE_NAME = "tag-value-name";
  private static final String TAG_VALUE_VALUE = "tag-value-value";

  @Test
  void shouldCreateBrandProtectionTag() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    BrandProtectionTag parentTag = getParentTag();
    BrandProtectionCategory category = getCategory();
    BrandProtectionTag saved = getSavedEntity(input, parentTag, category);
    String[] excludedFields = {"pid", "updateDate"};

    when(tagRepository.findById(PARENT_TAG_PID)).thenReturn(Optional.of(parentTag));
    when(tagRepository.save(refEq(saved, excludedFields))).thenReturn(saved);
    given(categoryRepository.findById(CATEGORY_PID)).willReturn(Optional.of(category));

    // when
    BrandProtectionTagDTO result = brandProtectionService.createBrandProtectionTag(input);

    // then
    verifyCreatedOrUpdatedTag(input, result, saved, excludedFields);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenCreatingTagWithCategoryPidNull() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    input.setCategoryPid(null);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.createBrandProtectionTag(input));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verifyNoInteractions(tagRepository);
    verifyNoInteractions(categoryRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenCreatingTagWithUnknownCategory() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    given(categoryRepository.findById(CATEGORY_PID)).willReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.createBrandProtectionTag(input));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verify(categoryRepository).findById(CATEGORY_PID);
    verify(tagRepository).findById(PARENT_TAG_PID);
    verifyNoMoreInteractions(categoryRepository);
    verifyNoMoreInteractions(tagRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingTagWithPidNull() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTag(input));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verifyNoInteractions(tagRepository);
    verifyNoInteractions(categoryRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingTagWithCategoryPidNull() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    input.setPid(TAG_PID);
    input.setCategoryPid(null);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTag(input));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verifyNoInteractions(tagRepository);
    verifyNoInteractions(categoryRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingNonExistingTag() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    input.setPid(TAG_PID);
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTag(input));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verify(tagRepository).findById(TAG_PID);
    verifyNoMoreInteractions(tagRepository);
    verifyNoMoreInteractions(categoryRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingTagWithUnknownCategory() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    input.setPid(TAG_PID);
    BrandProtectionTag existingTag = new BrandProtectionTag();
    existingTag.setPid(TAG_PID);
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.of(existingTag));
    when(categoryRepository.findById(input.getCategoryPid())).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTag(input));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verify(tagRepository).findById(TAG_PID);
    verify(tagRepository).findById(PARENT_TAG_PID);
    verify(categoryRepository).findById(CATEGORY_PID);
    verifyNoMoreInteractions(tagRepository);
    verifyNoMoreInteractions(categoryRepository);
  }

  @Test
  void shouldUpdateBrandProtectionTag() {
    // given
    BrandProtectionTagDTO input = getBrandProtectionTagDTO();
    input.setPid(TAG_PID);
    BrandProtectionTag parentTag = getParentTag();
    BrandProtectionCategory category = getCategory();

    BrandProtectionTag existingTag = new BrandProtectionTag();
    existingTag.setPid(TAG_PID);

    BrandProtectionTag updated = getSavedEntity(input, parentTag, category);
    updated.setPid(TAG_PID);

    String[] excludedFields = {"updateDate"};
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.of(existingTag));
    when(tagRepository.findById(PARENT_TAG_PID)).thenReturn(Optional.of(parentTag));
    when(tagRepository.save(refEq(updated, excludedFields))).thenReturn(updated);
    when(categoryRepository.findById(CATEGORY_PID)).thenReturn(Optional.of(category));

    // when
    BrandProtectionTagDTO result = brandProtectionService.updateBrandProtectionTag(input);

    // then
    verifyCreatedOrUpdatedTag(input, result, updated, excludedFields);
  }

  @Test
  void shouldDeleteTag() {
    // given
    doNothing().when(tagRepository).deleteById(TAG_PID);

    // when
    brandProtectionService.deleteBrandProtectionTag(TAG_PID);

    // then
    verify(tagRepository).deleteById(TAG_PID);
    verifyNoMoreInteractions(tagRepository);
  }

  @Test
  void shouldCreateBrandProtectionCategory() {
    // given
    BrandProtectionCategory brandProtectionCategory = new BrandProtectionCategory();
    brandProtectionCategory.setName(CATEGORY_NAME);
    given(categoryRepository.save(any())).willReturn(brandProtectionCategory);

    // when
    BrandProtectionCategoryDto result =
        brandProtectionService.createBrandProtectionCategory(new BrandProtectionCategoryDto());

    // then
    assertEquals(CATEGORY_NAME, result.getName());
  }

  @Test
  void shouldGetBrandProtectionTagByPid() {
    // given
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(TAG_PID);
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.of(tag));

    // when
    BrandProtectionTagDTO result = brandProtectionService.getBrandProtectionTag(TAG_PID);

    // then
    assertEquals(TAG_PID, result.getPid());
    verify(tagRepository).findById(TAG_PID);
    verifyNoMoreInteractions(tagRepository);
  }

  @Test
  void shouldReturnNullWhenBrandProtectionTagNotFound() {
    // given
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.empty());

    // when
    BrandProtectionTagDTO result = brandProtectionService.getBrandProtectionTag(TAG_PID);

    // then
    assertNull(result);
    verify(tagRepository).findById(TAG_PID);
    verifyNoMoreInteractions(tagRepository);
  }

  @Test
  void shouldGetAllBrandProtectionTags() {
    // given
    List<BrandProtectionTag> tags = List.of(new BrandProtectionTag(), new BrandProtectionTag());
    when(tagRepository.findAll()).thenReturn(tags);

    // when
    List<BrandProtectionTagDTO> result = brandProtectionService.getAllBrandProtectionTags();

    // then
    assertEquals(2, result.size());
    verify(tagRepository).findAll();
    verifyNoMoreInteractions(tagRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenCreatingValuesWithUnknownTag() {
    // given
    BrandProtectionTagValuesDTO tagValuesDto = new BrandProtectionTagValuesDTO();
    tagValuesDto.setBrandProtectionTagPid(TAG_PID);
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.empty());

    // when
    var result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.createBrandProtectionTagValues(tagValuesDto));

    // then
    assertEquals(ServerErrorCodes.SERVER_TAG_NOT_FOUND, result.getErrorCode());
    verify(tagRepository).findById(TAG_PID);
    verifyNoMoreInteractions(tagRepository);
    verifyNoInteractions(tagValuesRepository);
  }

  @Test
  void shouldThrowNotFoundWhenBrandProtectionTagDoesNotExist() {
    // when
    when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
    BrandProtectionTagValuesDTO tagValuesDto = new BrandProtectionTagValuesDTO();
    tagValuesDto.setBrandProtectionTagPid(TAG_PID);

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.createBrandProtectionTagValues(tagValuesDto));
    assertEquals(ServerErrorCodes.SERVER_TAG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingValuesWithUnknownTag() {
    // given
    BrandProtectionTagValuesDTO tagValuesDto = new BrandProtectionTagValuesDTO();
    tagValuesDto.setPid(2L);
    tagValuesDto.setBrandProtectionTagPid(TAG_PID);
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTagValues(tagValuesDto));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verify(tagRepository).findById(TAG_PID);
    verifyNoMoreInteractions(tagRepository);
    verifyNoInteractions(tagValuesRepository);
  }

  @Test
  void shouldUpdateBrandProtectionCategory() {
    // given
    BrandProtectionCategory brandProtectionCategory = new BrandProtectionCategory();
    brandProtectionCategory.setPid(CATEGORY_PID);
    BrandProtectionCategoryDto brandProtectionCategoryDto = new BrandProtectionCategoryDto();
    brandProtectionCategoryDto.setPid(CATEGORY_PID);
    given(categoryRepository.save(any())).willReturn(brandProtectionCategory);
    given(categoryRepository.findById(CATEGORY_PID))
        .willReturn(Optional.of(brandProtectionCategory));

    // when
    BrandProtectionCategoryDto result =
        brandProtectionService.updateBrandProtectionCategory(brandProtectionCategoryDto);

    // then
    assertEquals(CATEGORY_PID, result.getPid());
  }

  @Test
  void shouldGetBrandProtectionCategory() {
    // given
    BrandProtectionCategory brandProtectionCategory = new BrandProtectionCategory();
    brandProtectionCategory.setPid(CATEGORY_PID);
    given(categoryRepository.findById(CATEGORY_PID))
        .willReturn(Optional.of(brandProtectionCategory));

    // when
    BrandProtectionCategoryDto result =
        brandProtectionService.getBrandProtectionCategory(CATEGORY_PID);

    // then
    assertEquals(CATEGORY_PID, result.getPid());
  }

  @Test
  void shouldGetAllBrandProtectionCategories() {
    // given
    BrandProtectionCategory brandProtectionCategory = new BrandProtectionCategory();
    brandProtectionCategory.setPid(CATEGORY_PID);
    given(categoryRepository.findAll()).willReturn(List.of(brandProtectionCategory));

    // when
    List<BrandProtectionCategoryDto> result =
        brandProtectionService.getAllBrandProtectionCategories();

    // then
    assertEquals(1, result.size());
    assertEquals(CATEGORY_PID, result.get(0).getPid());
  }

  void shouldCreateBrandProtectionTagValues() {
    // given
    var tagPid = 1L;
    BrandProtectionTagValuesDTO tagValuesDTO = getBrandProtectionTagValuesDTO(null, tagPid);
    BrandProtectionTagValues tagValues = getBrandProtectionTagValues(null);
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(tagPid);
    when(tagRepository.findById(tagPid)).thenReturn(Optional.of(tag));
    when(tagValuesRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // when
    var returnedTagValuesDTO = brandProtectionService.createBrandProtectionTagValues(tagValuesDTO);

    // then
    verify(tagValuesRepository).save(tagValues);
    verifyUpdatedOrCreatedTagValues(tagValuesDTO, returnedTagValuesDTO);
  }

  @Test
  void shouldUpdateBrandProtectionTagValues() {
    // given
    var tagPid = 1L;
    var tagValuesPid = 2L;
    var updatedValue = "updated-value";
    BrandProtectionTagValuesDTO updatedTagValuesDTO =
        getBrandProtectionTagValuesDTO(tagValuesPid, tagPid);
    updatedTagValuesDTO.setValue(updatedValue);
    BrandProtectionTagValues tagValues = getBrandProtectionTagValues(tagValuesPid);
    BrandProtectionTagValues updatedTagValues = getBrandProtectionTagValues(tagValuesPid);
    updatedTagValues.setValue(updatedValue);
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(tagPid);
    when(tagRepository.findById(tagPid)).thenReturn(Optional.of(tag));
    when(tagValuesRepository.findById(tagValuesPid)).thenReturn(Optional.of(tagValues));
    when(tagValuesRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // when
    var returnedTagValuesDTO =
        brandProtectionService.updateBrandProtectionTagValues(updatedTagValuesDTO);

    // then
    verify(tagValuesRepository).save(updatedTagValues);
    verifyUpdatedOrCreatedTagValues(updatedTagValuesDTO, returnedTagValuesDTO);
  }

  @Test
  void shouldThrowExceptionOnUpdateTagValuesWhenTagValuesNotFound() {
    // given
    var tagPid = 1L;
    var tagValuesPid = 2L;
    var updatedValue = "updated-value";
    BrandProtectionTagValuesDTO updatedTagValuesDTO =
        getBrandProtectionTagValuesDTO(tagValuesPid, tagPid);
    updatedTagValuesDTO.setValue(updatedValue);
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(tagPid);
    when(tagRepository.findById(tagPid)).thenReturn(Optional.of(tag));
    when(tagValuesRepository.findById(tagValuesPid)).thenReturn(Optional.empty());

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTagValues(updatedTagValuesDTO));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
    verify(tagValuesRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionOnUpdateBrandProtectionTagValuesWhenPidIsNull() {
    var dto = new BrandProtectionTagValuesDTO();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateBrandProtectionTagValues(dto));

    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldDeleteBrandProtectionTagValues() {
    var pid = 1L;

    brandProtectionService.deleteBrandProtectionTagValues(pid);

    verify(tagValuesRepository).deleteById(pid);
  }

  @Test
  void shouldGetBrandProtectionTagValuesWhenExist() {
    var pid = 1L;
    BrandProtectionTagValues tagValues = getBrandProtectionTagValues(pid);
    BrandProtectionTagValuesDTO dto = getBrandProtectionTagValuesDTO(pid, null);
    when(tagValuesRepository.findById(pid)).thenReturn(Optional.of(tagValues));

    BrandProtectionTagValuesDTO returnedDto =
        brandProtectionService.getBrandProtectionTagValues(pid);

    verifyTagValues(dto, returnedDto);
  }

  @Test
  void shouldReturnNullOnGetBrandProtectionTagValuesWhenNotFound() {
    var pid = 1L;
    when(tagValuesRepository.findById(pid)).thenReturn(Optional.empty());

    BrandProtectionTagValuesDTO returnedDto =
        brandProtectionService.getBrandProtectionTagValues(pid);

    assertNull(returnedDto);
  }

  @Test
  void shouldGetAllBrandProtectionTagValues() {
    // given
    Supplier<LongStream> pids = () -> LongStream.range(0, 3);
    List<BrandProtectionTagValues> tagValues =
        pids.get().mapToObj(this::getBrandProtectionTagValues).collect(Collectors.toList());
    List<BrandProtectionTagValuesDTO> dtos =
        pids.get()
            .mapToObj(pid -> getBrandProtectionTagValuesDTO(pid, null))
            .collect(Collectors.toList());
    when(tagValuesRepository.findAll()).thenReturn(tagValues);

    // when
    List<BrandProtectionTagValuesDTO> returnedDtos =
        brandProtectionService.getAllBrandProtectionTagValues();

    // then
    for (int i = 0; i < tagValues.size(); i++) {
      verifyTagValues(dtos.get(i), returnedDtos.get(i));
    }
  }

  @Test
  void shouldCreateCrsTagMapping() {
    // given
    CrsTagMappingDTO dto = new CrsTagMappingDTO();
    dto.setBrandProtectionTagPid(TAG_PID);
    dto.setCrsTagId(100L);
    dto.setCrsTagAttributeId(200L);

    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(TAG_PID);

    CrsTagMapping tagMapping = new CrsTagMapping();
    tagMapping.setTag(tag);
    tagMapping.setCrsTagId(100L);
    tagMapping.setCrsTagAttributeId(200L);
    tagMapping.setPid(300L);

    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.of(tag));
    when(tagMappingRepository.save(refEq(tagMapping, "pid", "updateDate"))).thenReturn(tagMapping);

    // when
    CrsTagMappingDTO savedDto = brandProtectionService.createCrsTagMapping(dto);

    // then
    assertEquals(300L, savedDto.getPid());
    assertEquals(dto.getBrandProtectionTagPid(), savedDto.getBrandProtectionTagPid());
    assertEquals(dto.getCrsTagId(), savedDto.getCrsTagId());
    assertEquals(dto.getCrsTagAttributeId(), savedDto.getCrsTagAttributeId());
    verify(tagRepository).findById(TAG_PID);
    verify(tagMappingRepository).save(refEq(tagMapping, "pid", "updateDate"));
  }

  @Test
  void shouldThrowExceptionWhenCreatingCrsTagMappingWithUnknownTag() {
    // given
    CrsTagMappingDTO dto = new CrsTagMappingDTO();
    dto.setBrandProtectionTagPid(TAG_PID);
    when(tagRepository.findById(TAG_PID)).thenReturn(Optional.empty());

    // when
    var result =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionService.createCrsTagMapping(dto));

    // then
    assertEquals(ServerErrorCodes.SERVER_TAG_NOT_FOUND, result.getErrorCode());
    verify(tagRepository).findById(TAG_PID);
    verifyNoInteractions(tagMappingRepository);
  }

  @Test
  void shouldThrowExceptionWhenCreatingCrsTagMappingWithNullTagPid() {
    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.createCrsTagMapping(new CrsTagMappingDTO()));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verifyNoInteractions(tagRepository);
    verifyNoInteractions(tagMappingRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingCrsTagMappingWithUnknownTag() {
    // given
    CrsTagMappingDTO dto = new CrsTagMappingDTO();
    dto.setBrandProtectionTagPid(1L);
    dto.setPid(2L);
    when(tagMappingRepository.findById(2L)).thenReturn(Optional.of(new CrsTagMapping()));
    when(tagRepository.findById(1L)).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionService.updateCrsTagMapping(dto));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verify(tagRepository).findById(1L);
    verify(tagMappingRepository).findById(2L);
    verifyNoMoreInteractions(tagRepository);
    verifyNoMoreInteractions(tagMappingRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingAnUnknownCrsTagMapping() {
    // given
    CrsTagMappingDTO dto = new CrsTagMappingDTO();
    dto.setBrandProtectionTagPid(1L);
    dto.setPid(2L);
    when(tagMappingRepository.findById(2L)).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionService.updateCrsTagMapping(dto));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verify(tagMappingRepository).findById(2L);
    verifyNoInteractions(tagRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingCrsTagMappingWithNullTagPid() {
    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> brandProtectionService.updateCrsTagMapping(new CrsTagMappingDTO()));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verifyNoInteractions(tagRepository);
    verifyNoInteractions(tagMappingRepository);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenUpdatingCrsTagMappingWithNullPid() {
    // given
    CrsTagMappingDTO dto = new CrsTagMappingDTO();
    dto.setPid(2L);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionService.updateCrsTagMapping(dto));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, result.getErrorCode());
    verifyNoInteractions(tagRepository);
    verifyNoInteractions(tagMappingRepository);
  }

  @Test
  void shouldUpdateCrsTagMapping() {
    // given
    CrsTagMappingDTO dto = new CrsTagMappingDTO();
    dto.setBrandProtectionTagPid(TAG_PID + 1);
    dto.setCrsTagId(100L);
    dto.setCrsTagAttributeId(200L);
    dto.setPid(31L);
    BrandProtectionTag newTag = new BrandProtectionTag();
    newTag.setPid(TAG_PID + 1);

    // existing entity
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(TAG_PID);
    CrsTagMapping existingTagMapping = new CrsTagMapping();
    existingTagMapping.setTag(tag);
    existingTagMapping.setCrsTagId(123L);
    existingTagMapping.setCrsTagAttributeId(456L);
    existingTagMapping.setPid(31L);

    when(tagMappingRepository.findById(31L)).thenReturn(Optional.of(existingTagMapping));

    when(tagRepository.findById(TAG_PID + 1)).thenReturn(Optional.of(newTag));

    // new stuff
    CrsTagMapping updatedTagMapping = new CrsTagMapping();
    updatedTagMapping.setTag(newTag);
    updatedTagMapping.setCrsTagId(100L);
    updatedTagMapping.setCrsTagAttributeId(200L);
    updatedTagMapping.setPid(31L);

    when(tagMappingRepository.save(refEq(updatedTagMapping, "updateDate")))
        .thenReturn(updatedTagMapping);

    // when
    CrsTagMappingDTO updatedDto = brandProtectionService.updateCrsTagMapping(dto);

    // then
    assertEquals(dto.getPid(), updatedDto.getPid());
    assertEquals(dto.getBrandProtectionTagPid(), updatedDto.getBrandProtectionTagPid());
    assertEquals(dto.getCrsTagId(), updatedDto.getCrsTagId());
    assertEquals(dto.getCrsTagAttributeId(), updatedDto.getCrsTagAttributeId());
    verify(tagRepository).findById(TAG_PID + 1);
    verify(tagMappingRepository).save(refEq(updatedTagMapping, "updateDate"));
  }

  @Test
  void shouldDeleteCrsTagMapping() {
    // given
    long crsTagMapingPid = 1L;
    doNothing().when(tagMappingRepository).deleteByPid(crsTagMapingPid);

    // when
    brandProtectionService.deleteCrsTagMapping(crsTagMapingPid);

    // then
    verify(tagMappingRepository).deleteByPid(crsTagMapingPid);
  }

  @Test
  void shouldGetCrsTagMappingByPid() {
    // given
    long crsTagMappingPid = 1L;
    CrsTagMapping tagMapping = new CrsTagMapping();
    tagMapping.setPid(crsTagMappingPid);
    when(tagMappingRepository.findById(crsTagMappingPid)).thenReturn(Optional.of(tagMapping));

    // when
    CrsTagMappingDTO dto = brandProtectionService.getCrsTagMapping(crsTagMappingPid);

    // then
    assertEquals(crsTagMappingPid, dto.getPid());
    verify(tagMappingRepository).findById(crsTagMappingPid);
  }

  @Test
  void shouldReturnNullWhenGettingUnknownCrsTagMapping() {
    // given
    long crsTagMappingPid = 1L;
    when(tagMappingRepository.findById(crsTagMappingPid)).thenReturn(Optional.empty());

    // when
    CrsTagMappingDTO dto = brandProtectionService.getCrsTagMapping(crsTagMappingPid);

    // then
    assertNull(dto);
    verify(tagMappingRepository).findById(crsTagMappingPid);
  }

  @Test
  void shouldReturnAllCrsTagMappings() {
    // given
    long pid1 = 1L;
    long pid2 = 2L;
    CrsTagMapping tagMapping1 = new CrsTagMapping();
    tagMapping1.setPid(pid1);
    CrsTagMapping tagMapping2 = new CrsTagMapping();
    tagMapping2.setPid(pid2);
    when(tagMappingRepository.findAll()).thenReturn(List.of(tagMapping1, tagMapping2));

    // when
    List<CrsTagMappingDTO> result = brandProtectionService.getAllCrsTagMappings();

    // then
    assertEquals(2, result.size());
    assertEquals(pid1, result.get(0).getPid());
    assertEquals(pid2, result.get(1).getPid());
  }

  private BrandProtectionTagValuesDTO getBrandProtectionTagValuesDTO(Long pid, Long tagPid) {
    var dto = new BrandProtectionTagValuesDTO();
    dto.setPid(pid);
    dto.setBrandProtectionTagPid(tagPid);
    dto.setName(TAG_VALUE_NAME);
    dto.setValue(TAG_VALUE_VALUE);
    return dto;
  }

  private BrandProtectionTagValues getBrandProtectionTagValues(Long pid) {
    var tagValues = new BrandProtectionTagValues();
    tagValues.setPid(pid);
    tagValues.setName(TAG_VALUE_NAME);
    tagValues.setValue(TAG_VALUE_VALUE);
    return tagValues;
  }

  private BrandProtectionTagDTO getBrandProtectionTagDTO() {
    BrandProtectionTagDTO dto = new BrandProtectionTagDTO();
    dto.setFreeTextTag(FREE_TEXT_TAG);
    dto.setName(TAG_NAME);
    dto.setUpdateDate(UPDATE_DATE);
    dto.setParentTagPid(PARENT_TAG_PID);
    dto.setRtbId(RTB_ID);
    dto.setCategoryPid(CATEGORY_PID);
    return dto;
  }

  private BrandProtectionTag getParentTag() {
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(PARENT_TAG_PID);
    return tag;
  }

  private BrandProtectionCategory getCategory() {
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setPid(CATEGORY_PID);
    return category;
  }

  private BrandProtectionTag getSavedEntity(
      BrandProtectionTagDTO dto, BrandProtectionTag parentTag, BrandProtectionCategory category) {
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setParentTag(parentTag);
    tag.setCategory(category);
    tag.setName(dto.getName());
    tag.setRtbId(dto.getRtbId());
    tag.setFreeTextTag(dto.isFreeTextTag());
    tag.setUpdateDate(new Date());
    tag.setPid(TAG_PID);
    return tag;
  }

  private void verifyUpdatedOrCreatedTagValues(
      BrandProtectionTagValuesDTO expectedDTO, BrandProtectionTagValuesDTO actualDTO) {
    verifyTagValues(expectedDTO, actualDTO);
    assertFalse(actualDTO.getUpdateDate().before(UPDATE_DATE));
  }

  private void verifyTagValues(
      BrandProtectionTagValuesDTO expectedDTO, BrandProtectionTagValuesDTO actualDTO) {
    assertEquals(expectedDTO.getBrandProtectionTagPid(), actualDTO.getBrandProtectionTagPid());
    assertEquals(expectedDTO.getName(), actualDTO.getName());
    assertEquals(expectedDTO.getValue(), actualDTO.getValue());
  }

  private void verifyCreatedOrUpdatedTag(
      BrandProtectionTagDTO input,
      BrandProtectionTagDTO result,
      BrandProtectionTag dbTag,
      String[] excludedFields) {
    assertNotNull(result.getUpdateDate());
    assertEquals(input.getParentTagPid(), result.getParentTagPid());
    assertEquals(input.getCategoryPid(), result.getCategoryPid());
    assertEquals(input.getName(), result.getName());
    assertEquals(input.getRtbId(), result.getRtbId());
    assertEquals(input.isFreeTextTag(), result.isFreeTextTag());
    assertEquals(dbTag.getPid(), result.getPid());
    verify(tagRepository).findById(input.getParentTagPid());
    if (input.getPid() != null) {
      verify(tagRepository).findById(input.getPid());
    }
    verify(tagRepository).save(refEq(dbTag, excludedFields));
    verifyNoMoreInteractions(tagRepository);
    verifyNoMoreInteractions(categoryRepository);
  }
}
