package com.nexage.app.web;

import com.nexage.app.dto.CrsTagMappingDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionCategoryDto;
import com.nexage.app.dto.brand.protection.BrandProtectionTagDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionTagValuesDTO;
import com.nexage.app.services.BrandProtectionService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rampatra
 * @since 2019-01-23
 */
@Legacy
@RestController
@RequestMapping(value = "/brandprotection")
public class BrandProtectionController {

  private final BrandProtectionService brandProtectionService;

  BrandProtectionController(BrandProtectionService brandProtectionService) {
    this.brandProtectionService = brandProtectionService;
  }

  // brand protection tags
  @PostMapping(path = "/tag")
  public BrandProtectionTagDTO createBrandProtectionTag(@RequestBody BrandProtectionTagDTO tagDto) {
    return brandProtectionService.createBrandProtectionTag(tagDto);
  }

  @PutMapping(path = "/tag")
  public BrandProtectionTagDTO updateBrandProtectionTag(@RequestBody BrandProtectionTagDTO tagDto) {
    return brandProtectionService.updateBrandProtectionTag(tagDto);
  }

  @DeleteMapping(path = "/tag/{tag_pid}")
  public ResponseEntity<String> deleteBrandProtectionTag(
      @PathVariable(value = "tag_pid") Long pid) {
    brandProtectionService.deleteBrandProtectionTag(pid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping(path = "/tag/{tag_pid}")
  public BrandProtectionTagDTO getBrandProtectionTag(@PathVariable(value = "tag_pid") Long pid) {
    return brandProtectionService.getBrandProtectionTag(pid);
  }

  @GetMapping(path = "/tags")
  public List<BrandProtectionTagDTO> getAllBrandProtectionTags() {
    return brandProtectionService.getAllBrandProtectionTags();
  }

  // brand protection tag values
  @PostMapping(path = "/tag-values")
  public BrandProtectionTagValuesDTO createBrandProtectionTagValues(
      @RequestBody BrandProtectionTagValuesDTO tagValuesDto) {
    return brandProtectionService.createBrandProtectionTagValues(tagValuesDto);
  }

  @PutMapping(path = "/tag-values")
  public BrandProtectionTagValuesDTO updateBrandProtectionTagValues(
      @RequestBody BrandProtectionTagValuesDTO tagValuesDto) {
    return brandProtectionService.updateBrandProtectionTagValues(tagValuesDto);
  }

  @DeleteMapping(path = "/tag-values/{tag_values_pid}")
  public ResponseEntity<String> deleteBrandProtectionTagValues(
      @PathVariable(value = "tag_values_pid") Long pid) {
    brandProtectionService.deleteBrandProtectionTagValues(pid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping(path = "/tag-values/{tag_values_pid}")
  public BrandProtectionTagValuesDTO getBrandProtectionTagValues(
      @PathVariable(value = "tag_values_pid") Long pid) {
    return brandProtectionService.getBrandProtectionTagValues(pid);
  }

  @GetMapping(path = "/tag-values")
  public List<BrandProtectionTagValuesDTO> getAllBrandProtectionTagValues() {
    return brandProtectionService.getAllBrandProtectionTagValues();
  }

  // brand protection category
  @PostMapping(path = "/category")
  public BrandProtectionCategoryDto createBrandProtectionCategory(
      @RequestBody BrandProtectionCategoryDto categoryDto) {
    return brandProtectionService.createBrandProtectionCategory(categoryDto);
  }

  @PutMapping(path = "/category")
  public BrandProtectionCategoryDto updateBrandProtectionCategory(
      @RequestBody BrandProtectionCategoryDto categoryDto) {
    return brandProtectionService.updateBrandProtectionCategory(categoryDto);
  }

  @DeleteMapping(path = "/category/{category_pid}")
  public ResponseEntity<String> deleteBrandProtectionCategory(
      @PathVariable(value = "category_pid") Long pid) {
    brandProtectionService.deleteBrandProtectionCategory(pid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping(path = "/category/{category_pid}")
  public BrandProtectionCategoryDto getBrandProtectionCategory(
      @PathVariable(value = "category_pid") Long pid) {
    return brandProtectionService.getBrandProtectionCategory(pid);
  }

  @GetMapping(path = "/categories")
  public List<BrandProtectionCategoryDto> getAllBrandProtectionCategories() {
    return brandProtectionService.getAllBrandProtectionCategories();
  }

  // crs tag mapping
  @PostMapping(path = "/tag-mappings")
  public CrsTagMappingDTO createCrsTagMapping(@RequestBody CrsTagMappingDTO tagMappingDto) {
    return brandProtectionService.createCrsTagMapping(tagMappingDto);
  }

  @PutMapping(path = "/tag-mappings")
  public CrsTagMappingDTO updateCrsTagMapping(@RequestBody CrsTagMappingDTO tagMappingDto) {
    return brandProtectionService.updateCrsTagMapping(tagMappingDto);
  }

  @DeleteMapping(path = "/tag-mappings/{tag_mapping_pid}")
  public ResponseEntity<String> deleteCrsTagMapping(
      @PathVariable(value = "tag_mapping_pid") Long pid) {
    brandProtectionService.deleteCrsTagMapping(pid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping(path = "/tag-mappings/{tag_mapping_pid}")
  public CrsTagMappingDTO getCrsTagMapping(@PathVariable(value = "tag_mapping_pid") Long pid) {
    return brandProtectionService.getCrsTagMapping(pid);
  }

  @GetMapping(path = "/tag-mappings")
  public List<CrsTagMappingDTO> getAllCrsTagMappings() {
    return brandProtectionService.getAllCrsTagMappings();
  }
}
