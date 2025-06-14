package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.BrandProtectionIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Disclaimer: This class is NOT thread-safe as it is using the Request bean which is autowired and
 * therefore, is a singleton.
 */
@Component
public class BrandProtectionRequests {

  @Autowired private Request request;

  public Request getCreateBrandProtectionTagRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setActualObjectIgnoredKeys(BrandProtectionIgnoredKeys.ignoredKeys)
        .setExpectedObjectIgnoredKeys(BrandProtectionIgnoredKeys.ignoredKeys)
        .setUrlPattern("/brandprotection/tag");
  }

  public Request getUpdateBrandProtectionTagRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(BrandProtectionIgnoredKeys.ignoredKeys)
        .setExpectedObjectIgnoredKeys(BrandProtectionIgnoredKeys.ignoredKeys)
        .setUrlPattern("/brandprotection/tag");
  }

  public Request getDeleteBrandProtectionTagRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/brandprotection/tag/" + RequestParams.TAG_PID);
  }

  public Request getBrandProtectionTagRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/brandprotection/tag/" + RequestParams.TAG_PID);
  }

  public Request getAllBrandProtectionTagsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/brandprotection/tags");
  }

  public Request getCreateBrandProtectionTagValuesRequest() {
    return request.clear().setPostStrategy().setUrlPattern("/brandprotection/tag-values");
  }

  public Request getUpdateBrandProtectionTagValuesRequest() {
    return request.clear().setPutStrategy().setUrlPattern("/brandprotection/tag-values");
  }

  public Request getDeleteBrandProtectionTagValuesRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/brandprotection/tag-values/" + RequestParams.TAG_VALUES_PID);
  }

  public Request getBrandProtectionTagValuesRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/brandprotection/tag-values/" + RequestParams.TAG_VALUES_PID);
  }

  public Request getAllBrandProtectionTagValuesRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/brandprotection/tag-values");
  }

  public Request getCreateBrandProtectionCategoryRequest() {
    return request.clear().setPostStrategy().setUrlPattern("/brandprotection/category");
  }

  public Request getUpdateBrandProtectionCategoryRequest() {
    return request.clear().setPutStrategy().setUrlPattern("/brandprotection/category");
  }

  public Request getDeleteBrandProtectionCategoryRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/brandprotection/category/" + RequestParams.CATEGORY_PID);
  }

  public Request getBrandProtectionCategoryRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/brandprotection/category/" + RequestParams.CATEGORY_PID);
  }

  public Request getAllBrandProtectionCategoriesRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/brandprotection/categories");
  }

  public Request getCreateCrsTagMappingsRequest() {
    return request.clear().setPostStrategy().setUrlPattern("/brandprotection/tag-mappings");
  }

  public Request getUpdateCrsTagMappingsRequest() {
    return request.clear().setPutStrategy().setUrlPattern("/brandprotection/tag-mappings");
  }

  public Request getDeleteCrsTagMappingsRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/brandprotection/tag-mappings/" + RequestParams.TAG_MAPPINGS_PID);
  }

  public Request getCrsTagMappingsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/brandprotection/tag-mappings/" + RequestParams.TAG_MAPPINGS_PID);
  }

  public Request getAllCrsTagMappingsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/brandprotection/tag-mappings");
  }
}
