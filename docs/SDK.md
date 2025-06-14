# SDK

## Information

On geneva Software development kit (SDK) represents a module that encloses all logic required to establish communication with a different service.

Normally an API (or even any kind of app) is integrated with other services somehow, like via REST operations (another API), or JDBC connections (a database) for example.

On a well software architecture stack, those downstream services would provide a CDK or SDK to explain a consumer or client how to interact with that service. That information includes things like:

- The downstream service configuration requirements.
- The downstream service requests model.
- The downstream service responses model.
- The downstream service authentication/authorization mechanisms.
- The downstream service error handling.
- The downstream service directions (paths) for each petition.

What happens when a service does not provide a CDK/SDK? You must build it yourself. This is where geneva-sdk concept comes in. The list of services included in this repository do not provide such SDK.

## Geneva SDKs

The following modules list represent all services geneva-api communicates with.

Each of those modules have only the code portion of the service that geneva consumes, although each service could provide more capabilities.

- `geneva-sdk-ckms` - SDK to communicate with CKMS Service API. More information [geneva-sdk-ckms](../geneva-sdk-ckms/README.md).
- `geneva-sdk-coredb` - SDK to communicate with MySQL Core Database. Legacy name: `geneva-core`. More information [geneva-sdk-coredb](../geneva-sdk-coredb/README.md).
- `geneva-sdk-dv360-seller` - SDK to communicate with Google DV360 Seller API. More information [geneva-sdk-dv360-seller](../geneva-sdk-dv360-seller/README.md).
- `geneva-sdk-dwdb` - SDK to communicate with Vertica Data Warehouse Database. Legacy name: `geneva-dw`. More information [geneva-sdk-dwdb](../geneva-sdk-dwdb/README.md).
- `geneva-sdk-identityb2b` - SDK to communicate with Identity B2B API. More information [geneva-sdk-identityb2b](../geneva-sdk-identityb2b/README.md).
- `geneva-sdk-messaging` - SDK to communicate with AWS Messaging. More information [geneva-sdk-messaging](../geneva-sdk-messaging/README.md).
- `geneva-sdk-onecentral` - SDK to communicate with One Central Rest API. More information [geneva-sdk-onecentral](../geneva-sdk-onecentral/README.md).
- `geneva-sdk-notification` - SDK to communicate with Notification Center Rest API. More information [geneva-sdk-notification](../geneva-sdk-notification/README.md).

## Build your own

Please read the information included on More information [How-To: SDK](./how-to/HOWTO-SDK.md) for further details.
