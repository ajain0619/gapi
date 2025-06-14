package com.nexage.geneva;

import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;

@Log4j2
@Cucumber
@CucumberContextConfiguration
@ContextConfiguration("classpath:cucumber.xml")
@Profile({"global-runner", "!custom-runner"})
public class GlobalTestRunner {}
