# How-To: SDK

This How-To explains some concepts related to Geneva SDKs and how to build them.

## Principles

Building a _geneva-sdk_ module must be based on the following clear principles:

- An SDK is the minimal software representation on how-to communicate with the downstream service.
- An SDK is a shareable concept. It could be extracted and be consumed by other services without geneva dependency.
- An SDK must not consume another SDK. If an SDK consumes another SDK, it is not an SDK but common functionality.
- An SDK should only import the minimal dependencies to establish communication, but never another SDK.
- A geneva service must consume an SDK, not the other way around.
- Any common internal information shared across different SDKs must be not tight to a specific SDK or service.
- An SDK must provide a Client facade to allow clients to consume the service.
- An SDK must provide a Configuration facade to decouple the implementation from the configuration.
- When an SDK is shareable across different stacks it must not tight the consumer to an specific library and must allow consumers to build its use.

## Requirements

It is important to collect certain information before an SDK is built:

- How many environments the new service has? How many environments is geneva going to consume?
- How authentication works to communicate with the new service?
- How authorization works to communicate with the new service?
- What type of communication is established?
- What is the error handling mechanism provided by the new service?
- What is the request model?
- What is the response model?
- What is the required configuration per environment?
- How is my app going to response when there is a communication issue with the new service?
- What is the Tier level between my app and the new service?

Please be sure all those answers are included into your design documentation.

## Naming

### Module

- `geneva`-`sdk`-`<SERVICE-NAME>`

Example: _geneva-sdk-ckms_

### Packages

It is important we keep consistency on the skeleton of each SDK.

Base Package:

- `com.ssp.geneva.sdk.<SERVICE-NAME>`

Example:

```java
package com.ssp.geneva.sdk.ckms;
```

## FAQ

> Why do I need a new geneva-sdk module if I can store the code within geneva-server? Can I use packages instead?

Why don't we have all code in a single java class? It is a matter of software architecture and decoupling. Originally current sdks code was inside _geneva-server_.

That was creating twisted code, more difficult to maintain. It was making also the code more difficult to read or upgrade.

Extracting the code to its respective module makes it more readable, easier to maintain, test and upgrade. It also brings layers of separations between code.

It also allows different geneva servers to consume same sdk.

> Is it ok to put geneva business logic into my sdk, so it is closer to its consumption?

No. That breaks the decoupling and the Single Responsibility Principle. An SDK has one job: Provide communication layer with a downstream service.

An SDK is only focused on the downstream, never the upstream. So it only knows down, never up. It is the consumer who must keep the _Why_.
