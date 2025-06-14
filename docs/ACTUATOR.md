# Actuator

## Information

New implementation creates a new Servlet to allocate different metrics associated to the API. They could be categorized in two blocks:

* API Health / System Information:
  * `/actuator/info`
  * `/actuator/env`
* Requests Metrics:
  * `/actuator/metrics`
  * `/actuator/metrics/extra`

More information can be found at:

* [spring-boot-2.1.12.RELEASE](https://docs.spring.io/spring-boot/docs/2.1.17.RELEASE/reference/html/production-ready-monitoring.html)
* [https://metrics.dropwizard.io](https://metrics.dropwizard.io)

## Actuator Servlet

### Endpoints

#### Info

Provides information about current build, version and branch origin.

```sh
{{host-geneva-server}}/geneva/actuator/info
```

```json
{
  "git": {
    "branch": "master",
    "commit": {
      "id": "acfc367",
      "time": {
        "nano": 0,
        "epochSecond": 1600277123
      }
    }
  },
  "build": {
    "artifact": "geneva-server",
    "name": "geneva-server",
    "time": {
      "nano": 623000000,
      "epochSecond": 1600277713
    },
    "version": "1.934.0",
    "group": "com.ssp.geneva"
  }
}
```

#### Metrics

Provides generic jvm details and metrics on each annotated endpoint. This endpoint collects spring-boot-actuator details and dropwizard metrics to present all the information in a single request.

```sh
{{host-geneva-server}}/geneva/actuator/metrics
```

Extra endpoint shows only the currently annotated methods at controller level using dropwizard servlet.

```sh
{{host-geneva-server}}/geneva/actuator/metrics/extra
```

#### Environment

Provides information about environment variables.

```sh
{{host-geneva-server}}/geneva/actuator/env
```
