package com.nexage.app.config;

import org.springframework.cloud.aws.context.config.annotation.EnableContextRegion;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableContextRegion(autoDetect = true)
@Configuration
@Profile({"aws", "!messaging-local"})
public class AwsContextConfig {}
