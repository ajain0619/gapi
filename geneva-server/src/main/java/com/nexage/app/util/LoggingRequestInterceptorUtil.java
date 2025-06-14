package com.nexage.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/*
* So as to use the Interceptor, Construct your restTemplate with BufferingClientHttpRequestFactory. The following is sample code for it!
*
*
	ClientHttpRequestInterceptor ci = new LoggingRequestInterceptor();
	List<ClientHttpRequestInterceptor> ciList = new ArrayList<ClientHttpRequestInterceptor>();
	ciList.add(ci);
	s2sTemplate.setInterceptors(ciList);
	s2sTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
*/

@Log4j2
public class LoggingRequestInterceptorUtil implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    logResponse(response);
    return response;
  }

  private void logRequest(HttpRequest request, byte[] body) throws IOException {
    log.debug("===========================request begin====>");
    log.debug("URI         : {}", request.getURI());
    log.debug("Method      : {}", request.getMethod());
    log.debug("Headers     : {}", request.getHeaders());
    log.debug("Request body: {}", new String(body, "UTF-8"));
    log.debug("===========================request end======>");
  }

  private void logResponse(ClientHttpResponse response) throws IOException {
    StringBuilder inputStringBuilder = new StringBuilder();
    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"))) {
      String line = bufferedReader.readLine();
      while (line != null) {
        inputStringBuilder.append(line);
        inputStringBuilder.append('\n');
        line = bufferedReader.readLine();
      }
    }

    log.debug("============================response begin======>");
    log.debug("Status code  : {}", response.getStatusCode());
    log.debug("Status text  : {}", response.getStatusText());
    log.debug("Headers      : {}", response.getHeaders());
    log.debug("Response body: {}", inputStringBuilder.toString());
    log.debug("=======================response end==========>");
  }
}
