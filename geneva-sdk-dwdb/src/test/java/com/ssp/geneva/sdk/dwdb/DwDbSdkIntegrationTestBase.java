package com.ssp.geneva.sdk.dwdb;

import com.ssp.geneva.sdk.dwdb.config.DwDbSdkConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles({"test", "debug"})
@DirtiesContext
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
@ContextConfiguration(classes = {DwDbSdkConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class DwDbSdkIntegrationTestBase {}
