package com.nexage.app.services.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.FileSystemService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.io.File;
import java.io.InputStream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Log4j2
@Primary
@Service
@Profile("aws")
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeller()"
        + " OR @loginUserContext.isOcUserBuyer() OR @loginUserContext.isOcUserSeatHolder()")
public class AWSFilesystemService implements FileSystemService {

  public static final String DISABLE_CERT_CHECK = "com.amazonaws.sdk.disableCertChecking";
  public static final String DISABLE_MD5_VALIDATION =
      "com.amazonaws.services.s3.disableGetObjectMD5Validation";
  public static final String TRUE = "true";
  private static final String TEMP_FILE_PREFIX = "TMPFILE";
  private static final String CONTENT_DISPOSITION = "inline";
  private static final String AWS_ENDPOINT = "aws.endpoint";
  private static final String AWS_MAX_RETRY = "aws.maxRetry";
  private static final String DEFAULT_MAX_RETRY = "3";
  private AmazonS3 s3;
  private String bucket;

  @Autowired
  public AWSFilesystemService(
      @Value("${ssp.geneva.static.bucket}") String bucket,
      @Value("${aws.localstack.access.key:#{''}}") String accessKey,
      @Value("${aws.localstack.access.secret.key:#{''}}") String secretAccessKey,
      @Value("${aws.localstack.region:#{''}}") String region,
      @Value("${aws.localstack.endpoint:#{''}}") String endpointUrl) {
    this.bucket = bucket;
    if (!Strings.isEmpty(accessKey)) {
      setupLocalStackS3(accessKey, secretAccessKey, region, endpointUrl);
    } else {
      setupS3();
    }
  }

  private void setupS3() {
    try {
      int maxRetry = Integer.valueOf(System.getProperty(AWS_MAX_RETRY, DEFAULT_MAX_RETRY));
      s3 =
          AmazonS3ClientBuilder.standard()
              .withCredentials(new InstanceProfileCredentialsProvider(true))
              .withClientConfiguration(new ClientConfiguration().withMaxErrorRetry(maxRetry))
              .build();
    } catch (AmazonClientException e) {
      throw new RuntimeException("Unable to get credentials", e);
    }
  }

  private void setupLocalStackS3(
      String accessKey, String secretAccessKey, String region, String endpointUrl) {
    System.setProperty(DISABLE_CERT_CHECK, TRUE);
    System.setProperty(DISABLE_MD5_VALIDATION, TRUE);
    String endpoint = System.getProperty(AWS_ENDPOINT, endpointUrl);
    AWSCredentials TEST_CREDENTIALS = new BasicAWSCredentials(accessKey, secretAccessKey);
    AmazonS3ClientBuilder builder =
        AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(new AWSStaticCredentialsProvider(TEST_CREDENTIALS));
    builder.setPathStyleAccessEnabled(true);
    builder.disableChunkedEncoding();
    s3 = builder.build();
  }

  @Override
  public void write(String dir, String fileName, byte[] data) {
    File tempFile = null;
    try {
      tempFile = File.createTempFile(TEMP_FILE_PREFIX, "." + FilenameUtils.getExtension(fileName));
      FileUtils.writeByteArrayToFile(tempFile, data);
      ObjectMetadata metaData = new ObjectMetadata();
      metaData.setContentDisposition(CONTENT_DISPOSITION);
      metaData.setContentType(new Tika().detect(tempFile.getPath()));
      metaData.setContentLength(data.length);
      metaData.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
      s3.putObject(
          new PutObjectRequest(bucket, fileName, tempFile)
              .withMetadata(metaData)
              .withCannedAcl(CannedAccessControlList.PublicRead));
      log.info("{} has been written to the bucket {}", fileName, bucket);
    } catch (Exception e) {
      log.info("Failed to save data for {}", fileName, e);
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_FILE_SYSTEM_WRITE_ERROR);
    } finally {
      if (tempFile != null) {
        tempFile.delete();
      }
    }
  }

  @Override
  public byte[] read(String fileName) {
    log.info("Trying to retrieve: {}/{}", bucket, fileName);
    try (S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, fileName));
        InputStream inputStream = s3Object.getObjectContent()) {
      return IOUtils.toByteArray(inputStream);
    } catch (Exception e) {
      log.error("Failed to retrieve data for {}/{}", bucket, fileName, e);
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_FILE_SYSTEM_READ_ERROR);
    }
  }

  public AmazonS3 getS3() {
    return s3;
  }
}
