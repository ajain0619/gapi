package com.nexage.app.util;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.admin.core.model.Audit;
import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.AuditRepository;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditListenerTest {

  @Mock private AuditRepository auditRepository;

  private AuditListener<Campaign> auditListener;
  private Campaign campaign;
  private User user;
  private static final Long CAMPAIGN_PID = 1L;
  private static final Long USER_PID = 2L;

  @BeforeEach
  void setUp() {
    campaign = new Campaign();
    campaign.setPid(CAMPAIGN_PID);
    user = new User();
    user.setPid(USER_PID);
    auditListener = new AuditListener<>(campaign);
  }

  @Test
  void shouldSaveTheRightAuditObject() {
    // given
    campaign.setStatus(Campaign.CampaignStatus.PAUSED);
    Audit a =
        new Audit(
            new Date(),
            USER_PID,
            Audit.AuditProduct.ADSERVER,
            Audit.AuditEntity.CAMPAIGN,
            CAMPAIGN_PID,
            Audit.AuditProperty.STATUS,
            Campaign.CampaignStatus.INACTIVE.toString(),
            Campaign.CampaignStatus.PAUSED.toString());

    // when
    auditListener.notifyModification(user, campaign, auditRepository);

    // then
    verify(auditRepository, times(1)).save(refEq(a, "modifiedDate"));
  }

  @Test
  void shouldDoNothingWhenEntitiesPidsDoesNotMatch() {
    // given
    campaign.setPid(campaign.getPid() + 1);

    // when
    auditListener.notifyModification(user, campaign, auditRepository);

    // then
    verifyNoInteractions(auditRepository);
  }

  @Test
  void shouldDoNothingWhenUserIsNull() {
    // given
    // when
    auditListener.notifyModification(null, campaign, auditRepository);

    // then
    verifyNoInteractions(auditRepository);
  }
}
