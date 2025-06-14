package com.nexage.admin.core;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles({"test", "debug"})
@DirtiesContext
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(classes = {CoreDbSdkConfig.class})
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class CoreDbSdkIntegrationTestBase {}
