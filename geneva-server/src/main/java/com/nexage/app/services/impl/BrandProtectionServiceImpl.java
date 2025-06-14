package com.nexage.app.services.impl;

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
import com.nexage.app.services.BrandProtectionService;
import com.nexage.app.util.assemblers.BrandProtectionAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rampatra
 * @since 2019-01-23
 */
@Service("brandProtectionService")
@PreAuthorize(
    "@loginUserContext.isOcAdminNexage() "
        + "or @loginUserContext.isOcManagerNexage() "
        + "or @loginUserContext.isOcManagerYieldNexage() "
        + "or @loginUserContext.isOcManagerSmartexNexage()")
@Transactional
public class BrandProtectionServiceImpl implements BrandProtectionService {

  private final BrandProtectionTagRepository tagRepository;

  private final BrandProtectionTagValuesRepository tagValuesRepository;

  private final BrandProtectionCategoryRepository brandProtectionCategoryRepository;

  private final CrsTagMappingRepository tagMappingRepository;

  @Autowired
  public BrandProtectionServiceImpl(
      BrandProtectionTagRepository tagRepository,
      BrandProtectionTagValuesRepository tagValuesRepository,
      BrandProtectionCategoryRepository brandProtectionCategoryRepository,
      CrsTagMappingRepository tagMappingRepository) {
    this.tagRepository = tagRepository;
    this.tagValuesRepository = tagValuesRepository;
    this.brandProtectionCategoryRepository = brandProtectionCategoryRepository;
    this.tagMappingRepository = tagMappingRepository;
  }

  @Override
  public BrandProtectionTagDTO createBrandProtectionTag(BrandProtectionTagDTO tagDto) {
    Long categoryPid = tagDto.getCategoryPid();
    if (categoryPid == null) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    BrandProtectionTag parentTag =
        Optional.ofNullable(tagDto.getParentTagPid()).flatMap(tagRepository::findById).orElse(null);

    BrandProtectionCategory category =
        brandProtectionCategoryRepository
            .findById(categoryPid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setParentTag(parentTag);
    tag.setCategory(category);
    setTagFromDto(tag, tagDto);

    return BrandProtectionAssembler.makeDtoFrom(tagRepository.save(tag));
  }

  @Override
  public BrandProtectionTagDTO updateBrandProtectionTag(BrandProtectionTagDTO tagDto) {
    Long pid = tagDto.getPid();
    Long categoryPid = tagDto.getCategoryPid();
    if (pid == null || categoryPid == null) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    BrandProtectionTag tag =
        tagRepository
            .findById(pid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    BrandProtectionTag parentTag =
        Optional.ofNullable(tagDto.getParentTagPid()).flatMap(tagRepository::findById).orElse(null);
    BrandProtectionCategory category =
        brandProtectionCategoryRepository
            .findById(categoryPid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    tag.setParentTag(parentTag);
    tag.setCategory(category);
    setTagFromDto(tag, tagDto);

    return BrandProtectionAssembler.makeDtoFrom(tagRepository.save(tag));
  }

  @Override
  public void deleteBrandProtectionTag(Long pid) {
    tagRepository.deleteById(pid);
  }

  @Override
  @PreAuthorize("@loginUserContext.isNexageUser() or @loginUserContext.isOcUserSeller()")
  public BrandProtectionTagDTO getBrandProtectionTag(Long pid) {
    return BrandProtectionAssembler.makeDtoFrom(tagRepository.findById(pid).orElse(null));
  }

  @Override
  @PreAuthorize("@loginUserContext.isNexageUser() or @loginUserContext.isOcUserSeller()")
  public List<BrandProtectionTagDTO> getAllBrandProtectionTags() {
    return tagRepository.findAll().stream()
        .map(BrandProtectionAssembler::makeDtoFrom)
        .collect(Collectors.toList());
  }

  @Override
  public BrandProtectionTagValuesDTO createBrandProtectionTagValues(
      BrandProtectionTagValuesDTO tagValuesDto) {
    BrandProtectionTag tag =
        tagRepository
            .findById(tagValuesDto.getBrandProtectionTagPid())
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND));
    BrandProtectionTagValues tagValues = new BrandProtectionTagValues();
    setTagValuesFromDto(tagValues, tagValuesDto);
    tagValues.setTag(tag);

    return BrandProtectionAssembler.makeDtoFrom(tagValuesRepository.save(tagValues));
  }

  @Override
  public BrandProtectionTagValuesDTO updateBrandProtectionTagValues(
      BrandProtectionTagValuesDTO tagValuesDto) {
    Long pid = tagValuesDto.getPid();
    if (pid == null) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    BrandProtectionTag tag =
        tagRepository
            .findById(tagValuesDto.getBrandProtectionTagPid())
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    BrandProtectionTagValues tagValues =
        tagValuesRepository
            .findById(pid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    setTagValuesFromDto(tagValues, tagValuesDto);
    tagValues.setTag(tag);

    return BrandProtectionAssembler.makeDtoFrom(tagValuesRepository.save(tagValues));
  }

  @Override
  public void deleteBrandProtectionTagValues(Long pid) {
    tagValuesRepository.deleteById(pid);
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or @loginUserContext.isOcUserSeller()")
  public BrandProtectionTagValuesDTO getBrandProtectionTagValues(Long pid) {
    return BrandProtectionAssembler.makeDtoFrom(tagValuesRepository.findById(pid).orElse(null));
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or @loginUserContext.isOcUserSeller()")
  public List<BrandProtectionTagValuesDTO> getAllBrandProtectionTagValues() {
    return tagValuesRepository.findAll().stream()
        .map(BrandProtectionAssembler::makeDtoFrom)
        .collect(Collectors.toList());
  }

  @Override
  public BrandProtectionCategoryDto createBrandProtectionCategory(
      BrandProtectionCategoryDto categoryDto) {
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setName(categoryDto.getName());
    category.setUpdateDate(new Date());

    return BrandProtectionAssembler.makeDtoFrom(brandProtectionCategoryRepository.save(category));
  }

  @Override
  public BrandProtectionCategoryDto updateBrandProtectionCategory(
      BrandProtectionCategoryDto categoryDto) {
    Long pid = categoryDto.getPid();
    BrandProtectionCategory category =
        brandProtectionCategoryRepository
            .findById(pid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    category.setName(categoryDto.getName());
    category.setUpdateDate(new Date());

    return BrandProtectionAssembler.makeDtoFrom(brandProtectionCategoryRepository.save(category));
  }

  @Override
  public void deleteBrandProtectionCategory(Long pid) {
    brandProtectionCategoryRepository.deleteById(pid);
  }

  @Override
  public BrandProtectionCategoryDto getBrandProtectionCategory(Long pid) {
    BrandProtectionCategory category =
        brandProtectionCategoryRepository
            .findById(pid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    return BrandProtectionAssembler.makeDtoFrom(category);
  }

  @Override
  public List<BrandProtectionCategoryDto> getAllBrandProtectionCategories() {
    return brandProtectionCategoryRepository.findAll().stream()
        .map(BrandProtectionAssembler::makeDtoFrom)
        .collect(Collectors.toList());
  }

  @Override
  public CrsTagMappingDTO createCrsTagMapping(CrsTagMappingDTO tagMappingDto) {
    Long tagPid = tagMappingDto.getBrandProtectionTagPid();
    if (tagPid == null) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    BrandProtectionTag tag =
        tagRepository
            .findById(tagPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND));

    CrsTagMapping tagMapping = new CrsTagMapping();
    tagMapping.setTag(tag);
    setTagMappingFromDto(tagMapping, tagMappingDto);

    return BrandProtectionAssembler.makeDtoFrom(tagMappingRepository.save(tagMapping));
  }

  @Override
  public CrsTagMappingDTO updateCrsTagMapping(CrsTagMappingDTO tagMappingDto) {
    Long pid = tagMappingDto.getPid();
    Long tagPid = tagMappingDto.getBrandProtectionTagPid();
    if (pid == null || tagPid == null) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    CrsTagMapping tagMapping =
        tagMappingRepository
            .findById(pid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    BrandProtectionTag tag =
        tagRepository
            .findById(tagPid)
            .orElseThrow(() -> new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    tagMapping.setTag(tag);
    setTagMappingFromDto(tagMapping, tagMappingDto);

    return BrandProtectionAssembler.makeDtoFrom(tagMappingRepository.save(tagMapping));
  }

  @Override
  public void deleteCrsTagMapping(Long pid) {
    tagMappingRepository.deleteByPid(pid);
  }

  @Override
  public CrsTagMappingDTO getCrsTagMapping(Long pid) {
    return BrandProtectionAssembler.makeDtoFrom(tagMappingRepository.findById(pid).orElse(null));
  }

  @Override
  public List<CrsTagMappingDTO> getAllCrsTagMappings() {
    return tagMappingRepository.findAll().stream()
        .map(BrandProtectionAssembler::makeDtoFrom)
        .collect(Collectors.toList());
  }

  private void setTagFromDto(BrandProtectionTag tag, BrandProtectionTagDTO tagDto) {
    tag.setName(tagDto.getName());
    tag.setRtbId(tagDto.getRtbId());
    tag.setFreeTextTag(tagDto.isFreeTextTag());
    tag.setUpdateDate(new Date());
  }

  private void setTagValuesFromDto(
      BrandProtectionTagValues tagValues, BrandProtectionTagValuesDTO tagValuesDto) {
    tagValues.setName(tagValuesDto.getName());
    tagValues.setValue(tagValuesDto.getValue());
    tagValues.setUpdateDate(new Date());
  }

  private void setTagMappingFromDto(CrsTagMapping tagMapping, CrsTagMappingDTO tagMappingDto) {
    tagMapping.setCrsTagId(tagMappingDto.getCrsTagId());
    tagMapping.setCrsTagAttributeId(tagMappingDto.getCrsTagAttributeId());
    tagMapping.setUpdateDate(new Date());
  }
}
