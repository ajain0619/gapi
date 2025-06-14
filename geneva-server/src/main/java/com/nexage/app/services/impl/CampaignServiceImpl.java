package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.Target;
import com.nexage.admin.core.model.Target.TargetType;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.AuditRepository;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CampaignService;
import com.nexage.app.util.AuditListener;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class CampaignServiceImpl implements CampaignService {

  private final UserContext userContext;
  private final UserRepository userRepository;
  private final CampaignRepository campaignRepository;
  private final AuditRepository auditRepository;

  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.canAccessSite(#sitePid) AND (@loginUserContext.isOcManagerNexage() "
          + "OR @loginUserContext.isOcUserSeller())")
  public void removePositionFromCampaignTargets(long sitePid, String positionName) {
    User user =
        userRepository
            .findByUserName(userContext.getUserId())
            .orElseThrow(
                () -> new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND));

    String positionFilter = sitePid + Target.LEVEL_DELIMITER + positionName;
    List<Campaign> campaigns =
        campaignRepository.findByTargets_TypeAndTargets_FilterLike(
            TargetType.ZONE, "%" + positionFilter + "%");

    for (Campaign campaign : campaigns) {
      AuditListener<Campaign> auditListener = null;
      for (Target target : campaign.getTargets()) {
        if (target.getType() == TargetType.ZONE && target.getFilter() != null) {
          // Reconstruct the filter string while trying to remove the specified position
          boolean found = false;
          StringBuilder sb = new StringBuilder();
          for (String p : target.getFilter().split(Target.TARGET_DELIMITER)) {
            if (positionFilter.equals(p)) {
              found = true;
            } else {
              if (sb.length() > 0) {
                sb.append(Target.TARGET_DELIMITER);
              }
              sb.append(p);
            }
          }
          if (found) {
            if (auditListener == null) {
              auditListener = new AuditListener<>(campaign);
            }
            target.setFilter(sb.toString());
          }
        }
      }

      if (auditListener != null) {
        // At least some targets have been updated.
        auditListener.notifyModification(user, campaign, auditRepository);
        campaignRepository.save(campaign);
      }
    }
  }
}
