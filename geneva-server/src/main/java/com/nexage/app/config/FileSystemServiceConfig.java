package com.nexage.app.config;

import com.nexage.app.services.FileSystemService;
import com.nexage.app.services.impl.AWSFilesystemService;
import com.nexage.app.services.impl.PhysicalFilesystemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Log4j2
@Configuration
public class FileSystemServiceConfig {

  @Value("${ssp.geneva.data.bucket}")
  private String genevaDataBucket;

  @Value("${aws.localstack.access.key:#{''}}")
  private String accessKey;

  @Value("${aws.localstack.access.secret.key:#{''}}")
  private String secretAccessKey;

  @Value("${aws.localstack.region:#{''}}")
  private String region;

  @Value("${aws.localstack.endpoint:#{''}}")
  private String endpointUrl;

  @Profile("default")
  @Bean(name = {"fileSystemService", "genevaDataFileSystemService"})
  public FileSystemService physicalFilesystemService() {
    return new PhysicalFilesystemService();
  }

  @Profile("aws")
  @Bean("genevaDataFileSystemService")
  public FileSystemService awsGenevaDataFileSystemService() {
    log.info("Using {} for genevaDataBucket", genevaDataBucket);
    return new AWSFilesystemService(
        genevaDataBucket, accessKey, secretAccessKey, region, endpointUrl);
  }
}
