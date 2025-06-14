# Testing

> “What is the meaning of a life without risk" by @martoc.

## Requirements

* All new functionality/change **must** include Unit Tests.

## Tests as Documentation

Since we don't keep any separate documentation for the code base, the tests serve this purpose. This is why it's important to maintain tests that effectively document what the code is designed for and what behavior is expected from it. The added benefit is that such documentation rarely manages to get out of sync with the prod code.

### Test Naming

In order to achieve the above goal, the preferred test naming scheme is `should<behavior>`. The name refers to the production code under test and describes what behavior we expect from it given certain input is passed in or given certain preconditions are met.

Examples of valid test method names:

* `shouldReturnFalseForInvalidInput`
* `shouldCorrectlyValidateNonexistentParamName`
* `shouldThrowWhenUserIsNotFound`
* `shouldProduceEmptyDTOFromNullInput`
* `shouldFailToUpdateUserWhenIdIsWrong`

### Test Naming: Do

* Describe the tested behavior. Think about the unit under test. What is the input? What is the expected output? What are the preconditions or side effects?
* Make the test names descriptive, but also as brief as possible. It's often more readable to refer to "valid input" than e.g. "all lowercase letters and underscores"

### Test Naming: Don't

* Include the tested method name. Method names are subject to change and thus there's a risk the documentation will fall out of sync with the code.
* Include the word `test` in the method name; it's redundant information.
* Describe the exact set of complex preconditions and input. Instead, it's better to focus on what makes the test different from the others to accurately summarize the test's preconditions.

### Nice to Have

Since tests predominantly use the BDD approach to testing, they follow the "given, when, then" structure, where:

* `given` corresponds to the preconditions setup block
* `when` is the tested method call
* `then` is the assertions block.

For readability, it is therefore a welcome addition to actually delimit the code blocks in the test method code with the corresponding comments, e.g.:

```java
@Test
void shouldMapUserToDTO() {
  // given
  User input = makeUser();

  // when
  UserDTO output = userDtoMapper.map(input);

  // then
  assertEquals(input.getName(), output.getName(), "User names should match.");
}
```

## Tests Type

### Unit Tests

Unit tests are managed by [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/).

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>${maven-surefire-plugin-version}</version>
</plugin>
```

* Extension: `*Test.java`.
* Speed: *fast*.  
* Scope: 1 single class under test.

#### Description

* Unit tests are the basic test unit.
* Main goal: inputs/outputs of public methods. Complexity.
* No application context should be used on unit tests.
* Fields are mocked. Use `@ExtendWith(MockitoExtension.class)`.

**NOTE:** Geneva currently uses [JUnit5](https://junit.org/junit5/), no new JUnit4 tests allowed.

#### Junit5 migration guidelines

* Now Annotations reside in the org.junit.jupiter.api.

* Now Assertions reside in org.junit.jupiter.api.Assertions.

* Replace @RunWith(SpringJUnit4ClassRunner.class) with @ExtendWith(SpringExtension.class)

* Replace  @RunWith(MockitoJUnitRunner.class) with @ExtendWith(MockitoExtension.class)

> @Rule and @ClassRule no longer exist; superseded by @ExtendWith and @RegisterExtension

> As in JUnit 4, Rule-annotated fields as well as methods are supported. By using these class-level extensions on a test class such Rule implementations in legacy code bases can be left unchanged including the JUnit 4 rule import statements.

This limited form of Rule support can be switched on by the class-level annotation @EnableRuleMigrationSupport.

#### Annotation migration table

`JUnit 5` | `JUnit 4`
--- | ---
@BeforeEach   | @Before       |
@AfterEach    | @After        | 
@BeforeAll    | @BeforeClass  |
@AfterAll     | @AfterClass   |
@Disabled     | @Ignore       |
@Tag          | @Category     |
@ExtendWith   | @RunWith      |

### Integration Tests

Integration tests are managed by [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/).

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>${maven-surefire-plugin-version}</version>
</plugin>
```

> Note: Failsafe plugin version follows same cycle as Surefire one. They are bounded together.

* Extension: `*IT.java`
* Speed: *normal*.
* Scope: Multiple classes.
* Application context is used.
* Fields are mocked/autowired. Use `@ExtendWith(SpringExtension.class)`.

### Acceptance Tests

Acceptance tests are inherited from the old cucumber repository. Those tests live in a different sub-module.

Due to their high cost in terms of time and efficiency we would like to reduce them to the minimum.

* Location: `<ROOT>/tests-acceptance`
* Speed: *slow*.

#### Running Acceptance Tests

* Run a `make start` in the geneva-api dir and ensure this is up and running before proceeding
* Then navigate to the cucumber repo (`.../geneva-api/tests-acceptance`) and run a `make start`

For running a single test instead all suite:

* Run a `make start` in the geneva-api dir and ensure this is up and running before proceeding
* Edit the `CustomTestRunner` on runSingleTest method and change the selectFile method to the test you want to test i.e:
```aidl
selectFile("src/test/resources/com/nexage/geneva/deviceos/1_device_os.feature"))
```
* Then navigate to the cucumber repo (`.../geneva-api/tests-acceptance`) and run a `make start-custom`

#### Role Testing

##### Old
Geneva uses the usernames to designate what role a user has for testing. This is very non flexible and will not be feasible in the long term especially when we are adding new roles.

>Example
>
```Given the user "admin1c" has logged in```

##### New
Geneva has a step [method](https://git.ouryahoo.com/SSP/geneva-api/blob/master/tests-acceptance/src/test/java/com/nexage/geneva/step/AccountServiceSteps.java#L56) now that allows you to pass the role that you need to test.  This will automatically allocate entitlements to this user through wiremock so you can test authorization through the acceptance tests. Please refer to the wiremock method [here](https://git.ouryahoo.com/SSP/geneva-api/blob/master/tests-acceptance/src/test/java/com/nexage/geneva/util/SetUpWiremockStubs.java#L107).

>Example
>
```Given the user "admin1c" has logged in with role "AdminNexage"```

#### Testing container versions

For running acceptance tests locally and within build pipeline, we use specific versions of the following containers:

* [`db-core`](https://git.ouryahoo.com/SSP/db-core)
* [`dwdb`](https://git.ouryahoo.com/SSP/docker-dwdb)
* [`sso-mock`](https://git.ouryahoo.com/SSP/sso-mock)

Versions are specified in `./env` file in projecr root directory.

Test data for coredb changes is at https://git.ouryahoo.com/SSP/geneva-api/blob/master/tests-acceptance/src/test/resources/data/db_core.sql location.

In general, you will only need to touch these versions if you update DB schema or change golden data for vertica.

##### Changing dwdb versions

If you change golden-data for the feature you work on, you will generally need to update coredb and dwdb versions.
These are the steps you need to take:

1. Create pull request to update golden-data repository.
2. Once this pull request is merged, it will trigger automatic build of `dwdb`. This will produce new Docker images with updated versions
3. Update values for `DWDB_VERSION` to new versions produced by its docker-dwdb builds.


##### Changing coredb versions

If you change db-core for the feature you work on, you will generally need to update dbcore versions.
These are the steps you need to take:

1. Create pull request to update `db-core` repository.
2. Once this pull request is merged, it will create a docker image based on the new tag created by your PR merge.
3. Update values for `DBCORE_VERSION` to new versions produced by its db core builds.

##### Changing sso-mock version

In general, `sso-mock` version will rarely change, but in case when it gets upgraded, you will just 
need to set new value for `SSO_MOCK_VERSION` in `.env` file.

### Manual Deployment

If all else fails and the developer is not able to test the new functionality with either unit tests, integration tests, or acceptance tests, the developer can put in a request to deploy out their specific PR to the PERF environment. This is only a last resort.

This can be done by creating a PR in the [Release-Management](https://git.ouryahoo.com/SSP/release-management) project and setting the version of this specific [file](https://git.ouryahoo.com/SSP/release-management/blob/master/src/one-mobile.dev/us-east-1/perf/geneva-api) to your PR version.

>Note: This will only temporarily set the version to the developers PR.  If a merge happens the version will be overwritten with the next PROD canidate. Please delete your stack after you are done testing. The stack can be deleted [here](https://console.aws.amazon.com/cloudformation/home?region=us-east-1#), please look for the specific version of your pr. DO NOT delete PERF, UAT, or QA.

