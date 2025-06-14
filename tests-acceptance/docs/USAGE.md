# Usage

## Local development

### Dependencies

Please check the [Requirements](../../docs/REQUIREMENTS.md) page.

### Pre-configuration

Before running any `make` command, be sure you configure the app:

```bash
./configure
```

> NOTE:This step is not mandatory for `tests-acceptance`, but recommended.

### Running Test Suite

Run this command in the `tests-acceptance` folder:

**Start**:

```bash
make start <PARAMETERS>
```

| Parameter |       Values      | Optional | Default |        Description       |
|:---------:|:-----------------:|:--------:|:-------:|:------------------------:|
|   SUITE   | `crud`            |   true   |  `crud` | Define test suite to run |

Example:

- Run `crud` suite.

```bash
make start SUITE=crud
```

```bash
make start
```

#### Tweak Test Suite

You can change which tests you want to run by changing the path of the feature selectFile into the runners.

Example:

[CustomTestRunner](../src/test/java/com/nexage/geneva/CustomTestRunner.java:25)

- and run

```bash
make start-custom
```

### Code Style Operations

Please check the [Code Style](../../docs/CODESTYLE.md) page.

#### Terminal

**Check Code Style**: This will run an analysis on the maven project.

```bash
make style-check
```

**Apply Code Style**: This will format the maven project, following the code style rules.

```bash
make style-apply
```

### Cucumber Test Results from SD pipeline

**AWS CLI Command**

```bash
VERSION=dc4db64 PR=640 aws s3 sync s3://geneva-apps-dev-us-east-1/geneva-api/${PR}/${VERSION} /tmp/${PR}/${VERSION}
open /tmp/${PR}/${VERSION}/tests/acceptance/crud/cucumber-html-reports/overview-features.html
```

This will pull down the folder published by the SD pipeline to your local temp folder.

> Note: You must be authenticated with the AWS DEV federate in order to run AWS CLI commands.
