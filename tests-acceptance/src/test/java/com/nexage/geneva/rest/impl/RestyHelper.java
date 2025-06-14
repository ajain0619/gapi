package com.nexage.geneva.rest.impl;

import static us.monoid.web.Resty.content;
import static us.monoid.web.Resty.delete;
import static us.monoid.web.Resty.form;
import static us.monoid.web.Resty.put;

import com.nexage.geneva.rest.RestClient;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.Content;
import us.monoid.web.FormData;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import us.monoid.web.mime.MultipartContent;

@Service
public class RestyHelper implements RestClient {
  public static final Resty resty = createResty(true);

  private static final String AUTHORIZATION = "Authorization";
  private static final String BASIC = "Basic";
  private static final String BEARER = "Bearer";
  private static final String SPACE = " ";

  /**
   * Sets the Authorization header value. Required to pass Basic Authentication.
   *
   * @param username user name
   * @param password user password
   */
  public void setAuthorizationValue(final String username, final String password) {
    byte[] encoded = Base64.encodeBase64((username + ":" + password).getBytes());
    resty.withHeader(AUTHORIZATION, BASIC + SPACE + new String(encoded));
  }

  public void setBearerAuthorizationHeader(final String bearerToken) {
    resty.dontSend(AUTHORIZATION);
    resty.withHeader(AUTHORIZATION, BEARER + SPACE + bearerToken);
  }

  /** Removes the Authorization header value. */
  public void removeAuthorizationValue() {
    resty.dontSend(AUTHORIZATION);
  }

  /** {@inheritDoc} */
  @Override
  public JSONResource executeGetRequest(String url, Map<String, String> headers, boolean redirect)
      throws Throwable {

    addHeaders(headers);
    try {
      if (redirect) {
        return resty.json(url);
      }
      return createResty(false).json(url);
    } finally {
      removeHeaders(headers);
    }
  }

  /** {@inheritDoc} */
  @Override
  public JSONResource executePostRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable {
    addHeaders(headers);
    try {
      if (payload == null) {
        return executePostRequest(url, redirect);
      } else if (payload instanceof JSONObject) {
        return executePostRequest(url, (JSONObject) payload, redirect);
      } else if (payload instanceof JSONArray) {
        return executePostRequest(url, (JSONArray) payload, redirect);
      } else if (payload instanceof String) {
        return executePostRequest(url, (String) payload, redirect);
      } else if (payload instanceof FormData) {
        return executePostRequest(url, (FormData) payload, false);
      } else if (payload instanceof MultipartContent) {
        return executePostRequest(url, (MultipartContent) payload, false);
      } else {
        throw new RuntimeException("Unknown type of payload");
      }
    } finally {
      removeHeaders(headers);
    }
  }

  /** {@inheritDoc} */
  @Override
  public JSONResource executePutRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable {
    addHeaders(headers);
    try {
      if (payload instanceof JSONObject) {
        return executePutRequest(url, (JSONObject) payload, redirect);
      } else if (payload instanceof JSONArray) {
        return executePutRequest(url, (JSONArray) payload, redirect);
      } else if (payload instanceof String) {
        return executePutRequest(url, (String) payload, redirect);
      } else {
        throw new RuntimeException("Unknown type of payload");
      }
    } finally {
      removeHeaders(headers);
    }
  }

  /** {@inheritDoc} */
  @Override
  public JSONResource executeDeleteRequest(
      String url, Map<String, String> headers, boolean redirect) throws Throwable {
    addHeaders(headers);
    try {
      if (redirect) {
        return resty.json(url, delete());
      }
      return createResty(false).json(url, delete());
    } finally {
      removeHeaders(headers);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void authenticate(String url, String login, String password) {
    url = url.substring(0, url.length() - 1);
    resty.authenticate(url, login, password.toCharArray());
  }

  public JSONResource executePostRequest(String url, boolean redirect) throws Throwable {
    if (redirect) {
      return resty.json(url, content(""));
    }
    return createResty(false).json(url, content(""));
  }

  public JSONResource executePostRequest(String url, JSONObject jo, boolean redirect)
      throws Throwable {
    if (redirect) {
      return resty.json(url, content(jo));
    }
    return createResty(false).json(url, content(jo));
  }

  public JSONResource executePostRequest(String url, JSONArray ja, boolean redirect)
      throws Throwable {
    Content content =
        new Content(
            "application/json; charset=UTF-8", ja.toString().getBytes(StandardCharsets.UTF_8));
    if (redirect) {
      return resty.json(url, content);
    }
    return createResty(false).json(url, content);
  }

  public JSONResource executePostRequest(String url, String content, boolean redirect)
      throws Throwable {
    if (redirect) {
      return resty.json(url, form(content));
    }
    return createResty(false).json(url, form(content));
  }

  public JSONResource executePostRequest(String url, FormData formData, boolean redirect)
      throws Throwable {
    if (redirect) {
      return resty.json(url, form(formData));
    }
    return createResty(false).json(url, form(formData));
  }

  public JSONResource executePostRequest(
      String url, MultipartContent multipartContent, boolean redirect) throws Throwable {
    if (redirect) {
      return resty.json(url, multipartContent);
    }
    return createResty(false).json(url, multipartContent);
  }

  public JSONResource executePutRequest(String url, String content, boolean redirect)
      throws Throwable {
    if (redirect) {
      return resty.json(url, put(content(content)));
    }
    return createResty(false).json(url, put(content(content)));
  }

  public JSONResource executePutRequest(String url, JSONObject jo, boolean redirect)
      throws Throwable {
    if (redirect) {
      return resty.json(url, put(content(jo)));
    }
    return createResty(false).json(url, put(content(jo)));
  }

  public JSONResource executePutRequest(String url, JSONArray ja, boolean redirect)
      throws Throwable {
    Content content =
        new Content(
            "application/json; charset=UTF-8", ja.toString().getBytes(StandardCharsets.UTF_8));
    if (redirect) {
      return resty.json(url, put(content));
    }
    return createResty(false).json(url, put(content));
  }

  /**
   * Sets headers
   *
   * @param headers headers map
   */
  public void addHeaders(Map<String, String> headers) {
    if (headers != null) {
      for (String key : headers.keySet()) {
        resty.withHeader(key, headers.get(key));
      }
    }
  }

  /**
   * Removes headers
   *
   * @param headers headers map
   */
  public void removeHeaders(Map<String, String> headers) {
    if (headers != null) {
      headers.keySet().forEach(resty::dontSend);
    }
  }

  private static final Resty createResty(boolean redirect) {
    return new Resty(new Redirect(redirect)) {
      // this prevents Resty from adding two Accept headers
      @Override
      protected void addAdditionalHeaders(URLConnection con) {
        this.getAdditionalHeaders().forEach(con::setRequestProperty);
      }
    };
  }

  public static class Redirect extends Resty.Option {
    private boolean value;

    public Redirect(boolean value) {
      this.value = value;
    }

    public void apply(URLConnection urlConnection) {
      ((HttpURLConnection) urlConnection).setInstanceFollowRedirects(value);
    }
  }
}
