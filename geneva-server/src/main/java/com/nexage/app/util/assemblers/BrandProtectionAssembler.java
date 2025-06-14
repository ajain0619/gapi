package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTagValues;
import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.app.dto.CrsTagMappingDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionCategoryDto;
import com.nexage.app.dto.brand.protection.BrandProtectionTagDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionTagValuesDTO;

public class BrandProtectionAssembler extends NoContextAssembler {

  public static BrandProtectionTagDTO makeDtoFrom(BrandProtectionTag tag) {
    if (tag == null) {
      return null;
    }
    BrandProtectionTagDTO tagDto = new BrandProtectionTagDTO();
    tagDto.setPid(tag.getPid());
    BrandProtectionTag parent = tag.getParentTag();
    if (parent != null) {
      tagDto.setParentTagPid(parent.getPid());
    }
    BrandProtectionCategory category = tag.getCategory();
    if (category != null) {
      tagDto.setCategoryPid(category.getPid());
    }
    tagDto.setName(tag.getName());
    tagDto.setFreeTextTag(tag.getFreeTextTag());
    tagDto.setRtbId(tag.getRtbId());
    tagDto.setUpdateDate(tag.getUpdateDate());
    return tagDto;
  }

  public static BrandProtectionTagValuesDTO makeDtoFrom(BrandProtectionTagValues tagValues) {
    if (tagValues == null) {
      return null;
    }
    BrandProtectionTagValuesDTO tagValuesDto = new BrandProtectionTagValuesDTO();
    tagValuesDto.setPid(tagValues.getPid());
    if (tagValues.getTag() != null) {
      tagValuesDto.setBrandProtectionTagPid(tagValues.getTag().getPid());
    }
    tagValuesDto.setName(tagValues.getName());
    tagValuesDto.setValue(tagValues.getValue());
    tagValuesDto.setUpdateDate(tagValues.getUpdateDate());
    return tagValuesDto;
  }

  public static BrandProtectionCategoryDto makeDtoFrom(BrandProtectionCategory category) {
    if (category == null) {
      return null;
    }
    BrandProtectionCategoryDto categoryDto = new BrandProtectionCategoryDto();
    categoryDto.setPid(category.getPid());
    categoryDto.setName(category.getName());
    categoryDto.setUpdateDate(category.getUpdateDate());
    return categoryDto;
  }

  public static CrsTagMappingDTO makeDtoFrom(CrsTagMapping tagMapping) {
    if (tagMapping == null) {
      return null;
    }
    CrsTagMappingDTO tagMappingDto = new CrsTagMappingDTO();
    tagMappingDto.setPid(tagMapping.getPid());
    if (tagMapping.getTag() != null) {
      tagMappingDto.setBrandProtectionTagPid(tagMapping.getTag().getPid());
    }
    tagMappingDto.setCrsTagId(tagMapping.getCrsTagId());
    tagMappingDto.setCrsTagAttributeId(tagMapping.getCrsTagAttributeId());
    tagMappingDto.setUpdateDate(tagMapping.getUpdateDate());
    return tagMappingDto;
  }
}
