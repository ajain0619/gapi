package com.nexage.geneva.request;

import static net.jodah.failsafe.Failsafe.with;

import com.nexage.geneva.config.TestConfiguration;
import com.nexage.geneva.requeststrategy.RequestStrategy;
import com.nexage.geneva.requeststrategy.impl.DeleteStrategy;
import com.nexage.geneva.requeststrategy.impl.GetStrategy;
import com.nexage.geneva.requeststrategy.impl.PostStrategy;
import com.nexage.geneva.requeststrategy.impl.PutStrategy;
import com.nexage.geneva.util.TestUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.jodah.failsafe.RetryPolicy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.monoid.json.JSONObject;
import us.monoid.web.AbstractContent;
import us.monoid.web.FormData;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

@Log4j2
@Component
@NoArgsConstructor
public class Request {

  private static final String QUERY_PARAMS_SEPARATOR = "?";
  @Autowired private GetStrategy getStrategy;
  @Autowired private PutStrategy putStrategy;
  @Autowired private PostStrategy postStrategy;
  @Autowired private DeleteStrategy deleteStrategy;
  @Autowired private TestConfiguration testConfiguration;
  private RequestStrategy requestStrategy;
  private String url, urlPattern, urlPathPattern, urlQueryPattern;
  private String[] expectedObjectIgnoredKeys, actualObjectIgnoredKeys;
  private Map<String, String> requestParams = new HashMap<>();
  private boolean removeOptionalQueryParams = true;
  private boolean followRedirects = true;

  public Request setFollowRedirects(boolean followRedirects) {
    this.followRedirects = followRedirects;
    return this;
  }

  public Request setRequestHeaders(Map<String, String> requestHeaders) {
    this.requestHeaders =
        requestHeaders == null ? null : Collections.unmodifiableMap(requestHeaders);
    return this;
  }

  private Map<String, String> requestHeaders = Map.of();
  private Object requestPayload;

  public void authenticate(String login, String password) throws Throwable {
    buildUrl();
    requestStrategy.authenticate(url, login, password);
  }

  private void buildUrl() throws Throwable {
    setPathAndQueryPatterns();
    buildRequestPath(requestParams);
    url = buildRequestUrl();
    log.info("url={}", url);
  }

  public String executeSsoLogin() throws Throwable {
    setPathAndQueryPatterns();
    buildRequestPath(requestParams);
    String ssoUrl = buildRequestUrl();
    log.info("ssoUrl={}", ssoUrl);
    RetryPolicy<Object> retryPolicy =
        new RetryPolicy<>()
            .handle(UnknownHostException.class)
            .withDelay(Duration.ofSeconds(30))
            .withMaxRetries(10);

    Document doc =
        with(retryPolicy)
            .get(() -> Jsoup.connect(ssoUrl).ignoreHttpErrors(true).ignoreContentType(true).get());
    String baseUri = doc.baseUri();
    log.info("baseUri={}", baseUri);
    return baseUri;
  }

  public JSONResource execute() throws Throwable {
    buildUrl();
    log.info("requestPayload={}", requestPayload);
    return requestStrategy.executeRequest(url, requestPayload, requestHeaders, followRedirects);
  }

  public Request disableOptionalQueryParamsRemoval() {
    removeOptionalQueryParams = false;
    return this;
  }

  public int executeGetAndGetResponseCode() throws Throwable {
    buildUrl();
    URL jUrl = new URL(url);
    HttpURLConnection con = (HttpURLConnection) jUrl.openConnection();
    con.setRequestMethod("GET");
    return con.getResponseCode();
  }

  public Request clear() {
    if (requestParams != null) {
      requestParams.clear();
    }
    if (requestHeaders != null) {
      requestHeaders = new HashMap<>();
    }
    url = null;
    actualObjectIgnoredKeys = null;
    expectedObjectIgnoredKeys = null;
    requestPayload = new JSONObject();
    removeOptionalQueryParams = true;
    followRedirects = true;
    return this;
  }

  private void buildRequestPath(Map<String, String> requestQueryAndPathParams) {
    StringBuilder exceptionMessage = new StringBuilder();
    if (requestQueryAndPathParams != null) {
      for (String key : requestQueryAndPathParams.keySet()) {
        if (urlPathPattern.contains(key)) {
          urlPathPattern = replaceKeyWithValue(requestQueryAndPathParams, key, urlPathPattern);
        } else if (urlQueryPattern.contains(key)) {
          urlQueryPattern = replaceKeyWithValue(requestQueryAndPathParams, key, urlQueryPattern);
        } else {
          exceptionMessage.append(key).append(", ");
        }
      }
    }
    validateReplacingUrl(exceptionMessage.toString());
    if (removeOptionalQueryParams) {
      removeOptionalQueryParams();
    } else {
      log.warn("Optional query params removal was disabled");
    }
  }

  private String replaceKeyWithValue(
      Map<String, String> requestQueryAndPathParams, String key, String pattern) {
    String value = requestQueryAndPathParams.get(key);
    pattern = pattern.replace(key, value);
    return pattern;
  }

  private void validateReplacingUrl(String exceptionMessage) throws RuntimeException {
    if (!exceptionMessage.isEmpty()) {
      exceptionMessage = "Redundant request params: " + exceptionMessage;
      throw new RuntimeException(exceptionMessage);
    }
  }

  private void removeOptionalQueryParams() {
    String unsetedQueryParams = "[^&]*=?\\{(.+?)}&*";
    urlQueryPattern = urlQueryPattern.replaceAll(unsetedQueryParams, "");
  }

  private String buildRequestUrl() throws URISyntaxException {
    java.net.URI uri =
        new URI(
            testConfiguration.getTestSuiteProperties().getSchema(),
            null,
            testConfiguration.getTestSuiteProperties().getHost(),
            Integer.parseInt(testConfiguration.getTestSuiteProperties().getPort()),
            testConfiguration.getTestSuiteProperties().getContext() + urlPathPattern,
            urlQueryPattern,
            null);
    return uri.toASCIIString();
  }

  public Request setRequestParams(Map<String, String> requestParams) {
    this.requestParams = requestParams;
    return this;
  }

  public Request setContentTypeRequestHeaders() {
    this.requestHeaders =
        new HashMap<String, String>() {
          {
            put("Content-type", "application/json");
          }
        };
    return this;
  }

  public Object getRequestPayload() {
    return this.requestPayload;
  }

  public Request setRequestPayload(Object requestPayload) {
    this.requestPayload = requestPayload;
    return this;
  }

  public String[] getExpectedObjectIgnoredKeys() {
    return expectedObjectIgnoredKeys;
  }

  protected Request setExpectedObjectIgnoredKeys(String[] ignoredKeys) {
    this.expectedObjectIgnoredKeys = ignoredKeys;
    return this;
  }

  public String[] getActualObjectIgnoredKeys() {
    return actualObjectIgnoredKeys;
  }

  protected Request setActualObjectIgnoredKeys(String[] ignoredKeys) {
    this.actualObjectIgnoredKeys = ignoredKeys;
    return this;
  }

  protected Request setUrlPattern(String urlPattern) {
    this.urlPattern = urlPattern;
    return this;
  }

  protected Request setDeleteStrategy() {
    this.requestStrategy = deleteStrategy;
    return this;
  }

  protected Request setGetStrategy() {
    this.requestStrategy = getStrategy;
    return this;
  }

  protected Request setPostStrategy() {
    this.requestStrategy = postStrategy;
    return this;
  }

  protected Request setPutStrategy() {
    this.requestStrategy = putStrategy;
    return this;
  }

  protected Request setMultipartFile(String controlName, String filename, byte[] data) {
    this.requestPayload = new FormData(controlName, filename, Resty.content(data));
    return this;
  }

  protected Request prepareMultipartRequest(
      String file, String fileName, String fileType, String dealId) throws Throwable {
    byte[] fileData = TestUtils.getResourceAsInputStream(file).readAllBytes();
    String fileString = new String(fileData, StandardCharsets.UTF_8).replaceAll("\\s+", "");
    byte[] processedFileData = fileString.getBytes(StandardCharsets.UTF_8);

    CustomFileContent fileContent = new CustomFileContent(processedFileData, fileName, "text/csv");

    us.monoid.web.mime.MultipartContent multipartContent =
        Resty.form(
            Resty.data("fileType", fileType),
            Resty.data("fileName", fileName),
            Resty.data("dealId", dealId),
            Resty.data("inventoriesFile", fileContent));

    this.requestPayload = multipartContent;
    return this;
  }

  private void setPathAndQueryPatterns() {
    int queryParamsSeparatorIndex = urlPattern.indexOf(QUERY_PARAMS_SEPARATOR);
    if (queryParamsSeparatorIndex != -1) {
      this.urlPathPattern = urlPattern.substring(0, queryParamsSeparatorIndex);
      this.urlQueryPattern = urlPattern.substring(queryParamsSeparatorIndex + 1);
    } else {
      this.urlPathPattern = urlPattern;
      this.urlQueryPattern = "";
    }
  }

  public String getUrl() {
    return url;
  }

  public class CustomFileContent extends AbstractContent {
    private byte[] data;
    private String fileName;
    private String mimeType;

    public CustomFileContent(byte[] data, String fileName, String mimeType) {
      this.data = data;
      this.fileName = fileName;
      this.mimeType = mimeType;
    }

    @Override
    public void writeHeader(OutputStream os) throws IOException {
      os.write(
          ("Content-Disposition: form-data; name=\"inventoriesFile\"; filename=\""
                  + fileName
                  + "\"\r\n")
              .getBytes());
      os.write(("Content-Type: " + mimeType + "\r\n").getBytes());
    }

    @Override
    public void writeContent(OutputStream os) throws IOException {
      os.write(data);
    }

    @Override
    protected void addContent(URLConnection urlConnection) throws IOException {
      return;
    }
  }
}
