# How-To: Bean Validations

This How-To explains some concepts related to Entity & DTO validations.

## Information

### Specification

- Bean Validation: [https://beanvalidation.org](https://beanvalidation.org/)
- Specification: 
    - Bean Validation 1.0: [`JSR-303`](https://beanvalidation.org/1.0/)
    - Bean Validation 1.1: [`JSR-349`](https://beanvalidation.org/1.1/)
    - Bean Validation 2.0: [`JSR-380`](https://beanvalidation.org/2.0/)

### Current Status

#### Dependencies

- Spring Framework `5.1.X`:
    - Compatible with `JSR-303`, `JSR-349` & `JSR-380`
    - More info: [link](https://docs.spring.io/spring/docs/5.1.x/spring-framework-reference/core.html#validation)
- Hibernate `5.3.X`:
    - Compatible with `JSR-303`, `JSR-349` & `JSR-380`
    - More info: [link](https://docs.jboss.org/hibernate/orm/5.3/userguide/html_single/Hibernate_User_Guide.html#_bean_validation_options)
- Hibernate Validator `6.0.`:
    - Compatible with `JSR-380`
    - More info: [link](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/)

#### Legacy Context

- `UI` & `API` were bundled into the same package and never deployed independently so all validations were happening at `UI` side.
- `API` validations were mostly at `entity` level. 

> NOTE: This context represents the old behaviour. For new expected behavior please check the [Requirements](#Requirements) section.

## Requirements

### DTO Validations

- All **DTO** classes must include `javax.validation` annotations following the specification.
- Back-end developers should sync up with Product & Front-end ones to define validation contract between external consumers and the `API`.

> NOTE: UI is just another client, this implementation should mimic UI validations for external (non UI) consumers. 

### Entity Validations

- All **Entity** classes must include `javax.validation` annotations following the specification.
- Back-end developers must understand requirements at persistence level to put proper validations in place. 

> NOTE: DTO/Entity validations are not always 1:1. It depends on bean mappings. 

## How does it work? 

More [info](https://docs.spring.io/spring/docs/5.1.x/spring-framework-reference/core.html#validation)

- How to enable spring validation:

```xml
<bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean" />
```

- How to force a validation:

| Mechanism               | Scope                                           | Description                                               |
|-------------------------|-------------------------------------------------|-----------------------------------------------------------|
| `@Valid`                | Controller layer.                               | Marks request body parameters to be validated.            |
| `BeanValidationService` | Injectable, explicit use. No scope restriction. | Requires execution of given method to perform validation. |

> NOTE: Best approach: Combine both: Use `@Valid` at Controller level and add `BeanValidationService` as extra security layer at Service scope.

### Exceptions

When an exception is captured due to any validation error, it will end translated into a response.

Please take a look to dedicate page - [How-To: Exceptions](./HOWTO-EXCEPTIONS.md)

## Example: Validation Triggering

### DTO Level

When a `CREATE` or `UPDATE` operations are implemented we must validate the external consumer input of the request body. 
The first layer of security for it is the Controller (View) layer.

Example DTOs

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildDTO extends ParentDTO {

  @Valid private FieldDTO field;
}
```

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDTO {

  private Long pid;

  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)  
  private String whatever;
}
```

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDTO {

  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private String name;
}
```

Example Request:

```json
{
  "whatever": null,
  "field": {
    "name": null
  }
}
```

Controller level through `@Valid` annotation (recommended).

```java
@PutMapping(value = "/{pid}", consumes = NATIVE_HEADER)
public ResponseEntity<ChildDTO> update(@RequestBody @Valid ChildDTO child) {
  return ResponseEntity.ok(service.update(child));
}
```

Controller level through `BeanValidationService`.

```java
class Controller {
  private final BeanValidationService beanValidationService;
...
  @PutMapping(value = "/{pid}")
  public ResponseEntity<ChildDTO> update(@RequestBody ChildDTO child) {
    beanValidationService.validate(child);
    return ResponseEntity.ok(service.update(child));
  }
...
}
```

Example Response:

```json
{
  "httpResponse": 400,
  "errorCode": 2004,
  "errorMessage": "Bad Request. Check your request parameters (json format, type..)",
  "guid": "c3016e19-792f-42c4-b38f-cf1e64f10cf0",
  "fieldErrors": {
    "whatever": "Value should not be empty",
    "field.name": "Value should not be empty"
  }
}
```

### Entity Level

```java
16:31:58.072 [qtp136393487-79] ERROR c.n.a.e.ExceptionLogger [15619884-ba23-45fe-b58a-f1fab2198e19] - 
javax.validation.ConstraintViolationException: Validation failed for classes [com.nexage.admin.core.model.Position] during persist time for groups [javax.validation.groups.Default, com.nexage.admin.core.validator.CheckUniqueGroup, ]
List of constraint violations:[
	ConstraintViolationImpl{interpolatedMessage='must not be null', propertyPath=mraidSupport, rootBeanClass=class com.nexage.admin.core.model.Position, messageTemplate='{javax.validation.constraints.NotNull.message}'}
	ConstraintViolationImpl{interpolatedMessage='must not be null', propertyPath=memo, rootBeanClass=class com.nexage.admin.core.model.Position, messageTemplate='{javax.validation.constraints.NotNull.message}'}
	ConstraintViolationImpl{interpolatedMessage='must not be null', propertyPath=screenLocation, rootBeanClass=class com.nexage.admin.core.model.Position, messageTemplate='{javax.validation.constraints.NotNull.message}'}
]
```

Example Response:

```java
{
  "httpResponse": 400,
  "errorCode": 2002,
  "errorMessage": "Constraint violation error",
  "guid": "ddf3a48d-64b0-4449-a085-679641c2156a"
}
```

> Note: Currently there is a story to provide more precise information about fields affected: [MX-11133](https://jira.vzbuilders.com/browse/MX-11133)

## Example: Custom Constraint Validation

Apart from the use of predefined simple validations, custom constraint validations can be created for more complex criterias.

Please take a look to examples under `com.nexage.app.util.validator` package.

Those validations are triggered automatically by our system during a bean validation.

```java
@Documented
@Constraint(validatedBy = {MyCustomValidator.class})
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface MyCustomConstraint {

  String message() default "{com.nexage.admin.validator.MyCustomConstraint.message}";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
```

```java
public class MyCustomValidator extends BaseValidator<MyCustomConstraint, String> {

  @Override
  public boolean isValid(String whatever, ConstraintValidatorContext constraintValidatorContext) {
    return !Strings.isEmpty(whatever);
  }
}
```

