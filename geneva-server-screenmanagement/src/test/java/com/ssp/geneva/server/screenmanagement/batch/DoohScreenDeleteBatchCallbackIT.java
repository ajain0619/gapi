package com.ssp.geneva.server.screenmanagement.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenDeleteBatchCallback;
import com.ssp.geneva.server.screenmanagement.config.GenevaServerScreenManagementConfig;
import java.time.Instant;
import java.util.Date;
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
class DoohScreenDeleteBatchCallbackIT {

  @Autowired private DoohScreenDeleteBatchCallback doohScreenDeleteBatchCallback;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @Transactional
  void shouldBatchDeleteDoohScreens() {
    assertEquals(2, doohScreenDeleteBatchCallback.execute(812L));
    assertDoesNotThrow(
        () ->
            AuditReaderFactory.get(entityManager)
                .getRevisionNumberForDate(Date.from(Instant.now())),
        "Audit record does not exist for deleted records");
  }
}
