/**
 * OAuth 2.0 is a standard that apps use to provide client applications with access. If you would
 * like to grant access to your application data in a secure way, then you want to use the OAuth 2.0
 * protocol.
 *
 * <p>The OAuth 2.0 spec has four important roles:
 *
 * <p>The "authorization server" — The server that issues the access token. In this case Okta is the
 * authorization server.
 *
 * <p>The "resource owner" — Normally your application's end user that grants permission to access
 * the resource server with an access token
 *
 * <p>The "client" — The application that requests the access token from Okta and then passes it to
 * the resource server
 *
 * <p>The "resource server" — Accepts the access token and must verify that it's valid. In this case
 * this is your application.
 *
 * <p>Other important terms:
 *
 * <p>An OAuth 2.0 "grant" is the authorization given (or "granted") to the client by the user.
 * Examples of grants are "authorization code" and "client credentials". Each OAuth grant has a
 * corresponding flow, explained below. The "access token" is issued by the authorization server
 * (Okta) in exchange for the grant. The "refresh token" is an optional token that is exchanged for
 * a new access token if the access token has expired.
 *
 * <p>The usual OAuth 2.0 grant flow looks like this:
 *
 * <p>Client requests authorization from the resource owner (usually the user).
 *
 * <p>If the user gives authorization, the client passes the authorization grant to the
 * authorization server (in this case Okta).
 *
 * <p>If the grant is valid, the authorization server returns an access token, possibly alongside a
 * refresh and/or ID token.
 *
 * <p>The client now uses that access token to access the resource server.
 */
package com.ssp.geneva.common.security.oauth2;
