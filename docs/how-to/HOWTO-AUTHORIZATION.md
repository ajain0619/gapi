# How-To: Authorization

This How-To explains some concepts related to authorization in geneva-api.

## Information

#### Dependencies

- Spring Framework `5.1.X`:
  - Compatible with `JSR-303`, `JSR-349` & `JSR-380`
  - More
    info: [link](https://docs.spring.io/spring/docs/5.1.x/spring-framework-reference/core.html#validation)

#### Legacy Context

- `API` was using `@Secured` and `@PreAuthorize` with geneva roles in order to secure most
  serviceImpl classes. Here are examples in the screenshots below.

```
@Secured(value = {"ROLE_MANAGER_NEXAGE", "ROLE_ADMIN_NEXAGE"})
```

```
@PreAuthorize("@loginUserContext.isNexageAdminOrManager() or @loginUserContext.isSellerAdmin())
```

#### Entitlements

- The `API` is using just `@PreAuthorize` using 1C entitlements as the backbone for
  authorization. Here we have
  a [link](https://docs.google.com/document/d/131Pezku8AvreIXxNr8prXZ0X39S2Zo7MgmfLZMgIBio/edit#heading=h.geadxmwzly6e)
  that fully describes where we are headed. This document describes the new entitlements based off
  the current geneva roles. Please refer to the link to see which entitlements you need to check for
  in your implementation. We will provide an example below of the conversion.

##### Secured

###### Old

```
@Secured(value = {"ROLE_MANAGER_NEXAGE", "ROLE_ADMIN_NEXAGE"})
```

###### New

```
@PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOCAdminNexage())
```

##### PreAuthorize

###### Old

```
@PreAuthorize("@loginUserContext.isNexageAdminOrManager() or @loginUserContext.isSellerAdmin())
```

###### New

```
@PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminNexage() or @loginUserContext.isOcAdminSeller()
```

#### Testing

Please refer to the role testing portion of the testing [documentation](/docs/TESTING.md#role-testing).
