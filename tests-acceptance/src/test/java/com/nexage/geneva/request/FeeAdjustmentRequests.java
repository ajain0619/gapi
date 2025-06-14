package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.FeeAdjustmentIgnoreKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeeAdjustmentRequests {

  @Autowired private Request request;

  public Request getCreateFeeAdjustmentRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/v1/fee-adjustments")
        .setExpectedObjectIgnoredKeys(
            FeeAdjustmentIgnoreKeys.expectedObjectCreateOrUpdateIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectCreateOrUpdateIgnoreKeys);
  }

  public Request getUpdateFeeAdjustmentRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(String.format("/v1/fee-adjustments/%s", RequestParams.FEE_ADJUSTMENT_PID))
        .setExpectedObjectIgnoredKeys(
            FeeAdjustmentIgnoreKeys.expectedObjectCreateOrUpdateIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectCreateOrUpdateIgnoreKeys);
  }

  public Request getGetFeeAdjustmentRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(String.format("/v1/fee-adjustments/%s", RequestParams.FEE_ADJUSTMENT_PID))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getGetAllFeeAdjustmentsRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/v1/fee-adjustments");
  }

  public Request getGetAllPagedQfQtEnabledFeeAdjustmentsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/fee-adjustments?page=%s&size=%s&qf=%s&qt=%s&enabled=%s",
                RequestParams.PAGE,
                RequestParams.SIZE,
                RequestParams.QF,
                RequestParams.QT,
                RequestParams.FEE_ADJUSTMENT_ENABLED))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getGetAllPagedQfQtFeeAdjustmentsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/fee-adjustments?page=%s&size=%s&qf=%s&qt=%s",
                RequestParams.PAGE, RequestParams.SIZE, RequestParams.QF, RequestParams.QT))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getGetAllPagedFeeAdjustmentsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/fee-adjustments?page=%s&size=%s", RequestParams.PAGE, RequestParams.SIZE))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getGetAllQfQtEnabledFeeAdjustmentsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/fee-adjustments?qf=%s&qt=%s&enabled=%s",
                RequestParams.QF, RequestParams.QT, RequestParams.FEE_ADJUSTMENT_ENABLED))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getGetAllQfQtFeeAdjustmentsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format("/v1/fee-adjustments?qf=%s&qt=%s", RequestParams.QF, RequestParams.QT))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getGetAllEnabledFeeAdjustmentsRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format("/v1/fee-adjustments?enabled=%s", RequestParams.FEE_ADJUSTMENT_ENABLED))
        .setExpectedObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.expectedObjectGetOrGetAllIgnoreKeys)
        .setActualObjectIgnoredKeys(FeeAdjustmentIgnoreKeys.actualObjectGetOrGetAllIgnoreKeys);
  }

  public Request getDeleteFeeAdjustmentRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(String.format("/v1/fee-adjustments/%s", RequestParams.FEE_ADJUSTMENT_PID));
  }
}
