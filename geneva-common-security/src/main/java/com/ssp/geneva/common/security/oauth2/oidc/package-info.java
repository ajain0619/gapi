/**
 * OpenID Connect is an authentication standard built on top of OAuth 2.0. It adds an additional
 * token called an ID token. OpenID Connect also standardizes areas that OAuth 2.0 leaves up to
 * choice, such as scopes, endpoint discovery, and dynamic registration of clients.
 *
 * <p>Although OpenID Connect is built on top of OAuth 2.0, the OpenID Connect specification (opens
 * new window) uses slightly different terms for the roles in the flows:
 *
 * <p>The "OpenID provider" — The authorization server that issues the ID token. In this case Okta
 * is the OpenID provider.
 *
 * <p>The "end user" — Whose information is contained in the ID token
 *
 * <p>The "relying party" — The client application that requests the ID token from Okta
 *
 * <p>The "ID token" is issued by the OpenID Provider and contains information about the end user in
 * the form of claims.
 *
 * <p>A "claim" is a piece of information about the end user.
 *
 * <p>The high-level flow looks the same for both OpenID Connect and regular OAuth 2.0 flows. The
 * primary difference is that an OpenID Connect flow results in an ID token, in addition to any
 * access or refresh tokens.
 */
package com.ssp.geneva.common.security.oauth2.oidc;
