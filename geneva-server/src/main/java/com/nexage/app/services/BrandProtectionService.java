package com.nexage.app.services;

import com.nexage.app.dto.CrsTagMappingDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionCategoryDto;
import com.nexage.app.dto.brand.protection.BrandProtectionTagDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionTagValuesDTO;
import java.util.List;

/**
 * @author rampatra
 * @since 2019-01-23
 */
public interface BrandProtectionService {

  BrandProtectionTagDTO createBrandProtectionTag(BrandProtectionTagDTO tagDto);

  BrandProtectionTagDTO updateBrandProtectionTag(BrandProtectionTagDTO tagDto);

  void deleteBrandProtectionTag(Long pid);

  BrandProtectionTagDTO getBrandProtectionTag(Long pid);

  List<BrandProtectionTagDTO> getAllBrandProtectionTags();

  BrandProtectionTagValuesDTO createBrandProtectionTagValues(
      BrandProtectionTagValuesDTO tagValuesDto);

  BrandProtectionTagValuesDTO updateBrandProtectionTagValues(
      BrandProtectionTagValuesDTO tagValuesDto);

  void deleteBrandProtectionTagValues(Long pid);

  BrandProtectionTagValuesDTO getBrandProtectionTagValues(Long pid);

  List<BrandProtectionTagValuesDTO> getAllBrandProtectionTagValues();

  BrandProtectionCategoryDto createBrandProtectionCategory(BrandProtectionCategoryDto categoryDto);

  BrandProtectionCategoryDto updateBrandProtectionCategory(BrandProtectionCategoryDto categoryDto);

  void deleteBrandProtectionCategory(Long pid);

  BrandProtectionCategoryDto getBrandProtectionCategory(Long pid);

  List<BrandProtectionCategoryDto> getAllBrandProtectionCategories();

  CrsTagMappingDTO createCrsTagMapping(CrsTagMappingDTO tagMappingDto);

  CrsTagMappingDTO updateCrsTagMapping(CrsTagMappingDTO tagMappingDto);

  void deleteCrsTagMapping(Long pid);

  CrsTagMappingDTO getCrsTagMapping(Long pid);

  List<CrsTagMappingDTO> getAllCrsTagMappings();
}
