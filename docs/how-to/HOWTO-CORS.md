# How-To: CORS

This How-To explains some concepts related to [Cross-origin resource sharing (CORS)](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing).

## Configurable Parameters

The following is a list of available configurable parameters. Those parameters can be modified per environment.

| Key |       Value      | Optional |          Description         |
|---------|:-----------------:|:--------:|:----------------------------:|
|   `management.endpoints.web.cors.allowed-origins`   | `List<String>` |   false   |  Allowed Origins |
|   `management.endpoints.web.cors.allowed-methods`   | `List<String>` |   false   |  Allowed Methods |
|   `management.endpoints.web.cors.allowed-headers`   | `List<String>` |   false   |  Allowed Headers |

### Allowed Origins

- Key: `management.endpoints.web.cors.allowed-origins`
- Description: It defines the accepted origins. Example: *ssp.yahooinc.com*

```java
management.endpoints.web.cors.allowed-origins=https://ssp.yahooinc.com
```

This feature uses **regex** to identify domains.

- Any domain without `https` is evaluated on loose mode.
- Domains in loose mode only get matched against `https` protocol.

Example:

Value: `ssp.yahooinc.com`

| Origin Domain                                     |    Allowed     |
|---------------------------------------------------|:--------------:|
|   `https://ssp.yahooinc.com`                      | True           |
|   `https://uat.ssp.yahooinc.com`                  | True           |
|   `https://sspyahooinc.com`                       | False          |
|   `http://ssp.yahooinc.com`                       | False          |

Value: `https://www.yahooinc.com`

| Origin Domain                                     |    Allowed     |
|---------------------------------------------------|:--------------:|
|   `https://ssp.yahooinc.com`                      | True           |
|   `https://qa.yahooinc.com`                       | False          |
|   `http://www.yahooinc.com`                       | False          |

### Allowed Methods

- Key: `management.endpoints.web.cors.allowed-methods`
- Description: It defines the accepted HTTP methods. Example: *PUT*, *GET*...

```java
management.endpoints.web.cors.allowed-methods=GET,POST,DELETE,PUT,PATCH,OPTIONS,HEAD
```

### Allowed Headers

- Key: `management.endpoints.web.cors.allowed-headers`
- Description: It defines the accepted HTTP Headers. Example: *Content-Type*, *Cookie*...

```java
management.endpoints.web.cors.allowed-headers=Content-Type
```

## Error

- Error type: `403 Forbidden`
- Error Message: `Invalid CORS request`


