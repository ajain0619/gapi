package com.ssp.geneva.server.screenmanagement.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import com.nexage.admin.core.model.DoohScreen;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenInsertBatchCallback;
import com.ssp.geneva.server.screenmanagement.config.GenevaServerScreenManagementConfig;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles({"test", "debug"})
@DirtiesContext
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(classes = {CoreDbSdkConfig.class, GenevaServerScreenManagementConfig.class})
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/dooh-screen.sql", config = @SqlConfig(encoding = "utf-8"))
class DoohScreenInsertBatchCallbackIT {

  @Autowired private DoohScreenInsertBatchCallback doohScreenInsertBatchCallback;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @Transactional
  void shouldBatchInsertDoohScreens() {
    assertEquals(
        2,
        doohScreenInsertBatchCallback
            .setDoohScreens(
                List.of(createDoohScreen("abcdefg", 812L), createDoohScreen("abcdefgh", 812L)))
            .execute(812L));

    assertDoesNotThrow(
        () ->
            AuditReaderFactory.get(entityManager)
                .getRevisionNumberForDate(Date.from(Instant.now())),
        "Audit record does not exist for inserted records");
  }

  private DoohScreen createDoohScreen(String screenId, Long sellerId) {
    DoohScreen screen = new DoohScreen();
    screen.setSspScreenId(screenId);
    screen.setSellerPid(sellerId);
    screen.setSellerScreenId(sellerId + "-" + screenId);
    screen.setSellerScreenName("test-name");
    screen.setVenueTypeId(101);
    screen.setLatitude(32d);
    screen.setLongitude(32d);
    screen.setCountry("USA");
    screen.setState("NJ");
    screen.setCity("Te");
    screen.setAddress("sdfds");
    screen.setAdTypes("dsfs");
    screen.setMinAdDuration(1);
    screen.setMaxAdDuration(4);
    screen.setResolution("1080");
    screen.setAvgImpressionMultiplier(12.3);
    screen.setAvgWeeklyImpressions(12.3);
    screen.setFloorPrice(BigDecimal.valueOf(3.67));
    return screen;
  }
}
