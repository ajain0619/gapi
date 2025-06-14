package com.nexage.app.crs;

import com.aol.crs.cdk.cache.model.Creative;
import com.aol.crs.mock.MockHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class CrsMockHelper {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  private final String crsMockHost;
  private final String crsMockPort;

  public CrsMockHelper(String crsMockHost, String crsMockPort) {
    this.crsMockHost = crsMockHost;
    this.crsMockPort = crsMockPort;
    this.baseUrl = "http://" + crsMockHost + ":" + crsMockPort;
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(10000);
    factory.setReadTimeout(30000);
    restTemplate = new RestTemplate(factory);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    ObjectMapper objectMapper =
        new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    MappingJackson2HttpMessageConverter c = new MappingJackson2HttpMessageConverter();
    c.setObjectMapper(objectMapper);
    List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();
    list.add(c);

    restTemplate.setMessageConverters(list);
  }

  public void startCRSMocService() {
    String[] args = {crsMockHost, crsMockPort};
    MockHelper.startCrsService(args);
  }

  public void stopCRSMockService() throws InterruptedException {
    MockHelper.stopCrsService();
    Thread.sleep(2000);
  }

  public void reset() throws IOException {
    ResponseEntity<String> response =
        restTemplate.getForEntity(baseUrl + "/config/reset", String.class);
    if (response == null || !response.getStatusCode().is2xxSuccessful()) {
      throw new IOException("Failed to reset data");
    }
  }

  public void putToCreativeStore(Creative creative) throws IOException {
    ResponseEntity<String> response =
        restTemplate.postForEntity(baseUrl + "/config/creatives", creative, String.class);
    if (response == null || !response.getStatusCode().is2xxSuccessful()) {
      throw new IOException("Failed to put creative to store");
    }
  }

  public void deleteFromCreativeStore(
      String adSourceId, String buyerId, String campaignId, String buyerCreativeId)
      throws IOException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/config/creatives");
    if (adSourceId != null) {
      builder.queryParam("adSourceId", adSourceId);
    }
    if (buyerId != null) {
      builder.queryParam("buyerId", buyerId);
    }
    if (campaignId != null) {
      builder.queryParam("campaignId", campaignId);
    }
    if (buyerCreativeId != null) {
      builder.queryParam("buyerCreativeId", buyerCreativeId);
    }

    restTemplate.delete(builder.build().encode().toUri());
  }

  public List<Creative> showCreativeStore() throws IOException {
    ResponseEntity<List<Creative>> response =
        restTemplate.exchange(
            baseUrl + "/config/data/creatives",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Creative>>() {});

    if (response == null || !response.getStatusCode().is2xxSuccessful()) {
      throw new IOException("Failed to fetch data");
    }
    return response.getBody();
  }
}
