package com.nexage.geneva.rest;

import java.util.Map;
import us.monoid.web.JSONResource;

public interface RestClient {

  /**
   * @param url
   * @param headers
   * @param redirect
   * @return
   * @throws Throwable
   */
  JSONResource executeGetRequest(String url, Map<String, String> headers, boolean redirect)
      throws Throwable;

  /**
   * Executes post request by url
   *
   * @param url url
   * @param payload request sending object
   * @param headers request headers
   * @param redirect stop the follow through
   * @return JsonResource response
   * @throws Throwable
   */
  JSONResource executePostRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable;

  /**
   * Executes put request by url
   *
   * @param url url
   * @param payload request sending object
   * @param headers request headers
   * @param redirect stop the follow through
   * @return JsonResource response
   * @throws Throwable
   */
  JSONResource executePutRequest(
      String url, Object payload, Map<String, String> headers, boolean redirect) throws Throwable;

  /**
   * Executes delete request by url
   *
   * @param url url
   * @param headers request headers
   * @param redirect stop the follow through
   * @return JsonResource response
   * @throws Throwable
   */
  JSONResource executeDeleteRequest(String url, Map<String, String> headers, boolean redirect)
      throws Throwable;

  /**
   * Authenticate user using url, login and password
   *
   * @param url request url
   * @param login login
   * @param password password
   */
  void authenticate(String url, String login, String password);
}
