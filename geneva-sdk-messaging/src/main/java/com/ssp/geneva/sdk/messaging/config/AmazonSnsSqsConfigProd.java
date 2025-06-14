package com.ssp.geneva.sdk.messaging.config;

import org.springframework.cloud.aws.messaging.config.annotation.EnableSns;
import org.springframework.cloud.aws.messaging.config.annotation.SqsClientConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableSns
@Import(SqsClientConfiguration.class)
@Profile("!messaging-local")
public class AmazonSnsSqsConfigProd {}
