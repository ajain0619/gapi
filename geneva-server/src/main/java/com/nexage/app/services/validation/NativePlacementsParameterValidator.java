package com.nexage.app.services.validation;

import com.nexage.app.dto.CrudOperation;
import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class NativePlacementsParameterValidator {

  public void validateByOperation(
      CrudOperation crudOperation, NativePlacementRequestParamsDTO nativePlacementRequestParams) {
    if ((nativePlacementRequestParams.getSellerId() == null)
        || (nativePlacementRequestParams.getSiteId() == null)
        || (nativePlacementRequestParams.getNativePlacement() == null)) {
      String formattedError =
          String.format(
              "Failed to %s due to illegal argument in request param : %s",
              crudOperation, nativePlacementRequestParams);
      log.error(formattedError);
      throw new IllegalArgumentException(formattedError);
    }
    verifySiteIsValid(nativePlacementRequestParams);
    if (CrudOperation.UPDATE.equals(crudOperation)) {
      updateOperationValidation(nativePlacementRequestParams);
    }
  }

  private void updateOperationValidation(
      NativePlacementRequestParamsDTO nativePlacementRequestParams) {
    Long pid = nativePlacementRequestParams.getNativePlacement().getPid();
    if ((nativePlacementRequestParams.getPlacementId() == null)
        || !(nativePlacementRequestParams.getPlacementId().equals(pid))) {
      log.error(
          "given dto pid [{}] and url placementId [{}] are different",
          pid,
          nativePlacementRequestParams.getPlacementId());
      throw new IllegalArgumentException("given dto and url placementId are different");
    }
  }

  private void verifySiteIsValid(NativePlacementRequestParamsDTO nativePlacementRequestParams) {
    Long sitePid = nativePlacementRequestParams.getNativePlacement().getSitePid();
    if (!sitePid.equals(nativePlacementRequestParams.getSiteId())) {
      log.error(
          "given dto sitePid [{}] and url siteId [{}] are different",
          sitePid,
          nativePlacementRequestParams.getSiteId());
      throw new IllegalStateException("given dto and url siteId's are different");
    }
  }
}
