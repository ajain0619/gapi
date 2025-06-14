# How-To: Credentials

This How-To explains some concepts related to Credentials.

- `b2b` credentials.
- `coredb` credentials.
- `dwdb` credentials.

## b2b

These credentials are used to communicate with our _B2B group_ for authentication purposes.

As `properties` within the app:

| Key                                     |    Value   |
|-----------------------------------------|:----------:|
|   `geneva.sso.oidc.client.id`           | `<String>` |
|   `geneva.sso.oidc.client.secret`       | `<String>` |

As `yaml` _ansible_ properties substitutions:

| Key                                     |    Value   |
|-----------------------------------------|:----------:|
|   `geneva_sso_oidc_client_id`           | `<String>` |
|   `geneva_sso_oidc_client_secret`       | `<String>` |

## coredb

These credentials are used to communicate with our _core_ datasource.

As `properties` within the app:


| Key                                     |    Value   |
|-----------------------------------------|:----------:|
|   `core.datasource.username`            | `<String>` |
|   `core.datasource.password`            | `<String>` |

As `yaml` _ansible_ properties substitutions:

| Key                                     |    Value   |
|-----------------------------------------|:----------:|
|   `geneva_server_core_db_username`      | `<String>` |
|   `geneva_server_core_db_password`      | `<String>` |


## dwdb

These credentials are used to communicate with our _datawarehouse_ datasource.

As `properties` within the app:

| Key                                     |    Value     |
|-----------------------------------------|:------------:|
|   `dw.datasource.username`              | <String>     |
|   `dw.datasource.password`              | <String>     |

As `yaml` _ansible_ properties substitutions:

| Key                                     |    Value     |
|---------------------------------------------------|:--------------:|
|   `geneva_server_dw_db_username`                  | <String> |
|   `geneva_server_dw_db_password`                  | <String> |

## Rotation

By `Paranoids` request, password expires every __90 days__. To avoid taking the app down completely 
during the time the user expires we are going to perform an user rotation.

For that purpose there are two different credentials to be used: `primary` & `secondary`. 

Both credentials can be found into CKMS following `yaml` ansible format:

```yaml
geneva_server_dw_db_username: <USER>
geneva_server_dw_db_password: <PASS>
```

> NOTE: Currently geneva-api does not obtain running time the config from CKMS. 

* If `primary` user is currently active, please be sure `secondary` user has its password updated. Same, the other way around.
* Update _Parameter Store_ of the environment to be updated to override credential with the new one to be consumed.
* Use [AWS Auto Scaling](https://aws.amazon.com/autoscaling/) to bring down/up one instance at a time. New instances will obtain the new data from the _Parameter Store_.
* Be sure you renew the expired password, updating CKMS key. 

### CKSM 

`one-mobile.dev`

- [one-mobile.dev/geneva-platform.geneva-api.dwdb.user.primary](https://ui.ckms.ouroath.com/aws/view-keygroup/one-mobile.dev/view-key/geneva-platform.geneva-api.dwdb.user.primary)
- [one-mobile.dev/geneva-platform.geneva-api.dwdb.user.secondary](https://ui.ckms.ouroath.com/aws/view-keygroup/one-mobile.dev/view-key/geneva-platform.geneva-api.dwdb.user.secondary)



