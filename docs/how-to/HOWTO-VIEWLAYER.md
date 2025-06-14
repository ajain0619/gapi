# How-To: View Layer

This How-To explains some concepts related to View Layer.

## Naming convention

### API Versions 

All new endpoints have to include `versioning`. 

Example:

```bash
/v1/sellers
/v1/sellers/{sellerId}/sites
```

### URL 

All new endpoints have to include `versioning`. 

- URL Definition specification: [RFC3986](https://tools.ietf.org/html/rfc3986)
- Use `plurals`. 
- URLs are case sensitive: *spinal-case* approach over *camelCase*. 

Example:

```bash
/v1/sellers
/v1/seller-seats
```

> NOTE: Some examples: [REST Resource Naming Guide](https://restfulapi.net/resource-naming/)

#### Example: CRUD

|Operation | Key                                     | Return Entity Type                               |
|----------|-----------------------------------------|:------------------------------------------------:|
| GET      | `/v1/potatoes`                          | Collection                                       |
| GET      | `/v1/potatoes/{potatoPid}`              | Single entity (uniqueness comes from {potatoPid} |
| POST     | `/v1/potatoes`                          | Single entity (uniqueness comes from {potatoPid} |
| PUT      | `/v1/potatoes/{potatoPid}`              | Single entity (uniqueness comes from {potatoPid} |
| DELETE   | `/v1/potatoes/{potatoPid}`              | Nothing / Id of deleted entity                   |

## Responses

All responses should be wrapped by `ResponseEntity` Spring class.

## Pagination

All responses with a collection of uncertain amount of objects should be returned paginated, for performance purposes.

To help into that implementation, requests must include `Pageable` _springframework_ interface.

```java
@PageableDefault(sort = "XXX") Pageable pageable
```

### Sorting

When a controller request includes `@PageableDefault` it implements also `Sorting` _springframework_ interface.

### Searching

#### Single case

- `qf`: _query field_. Collections of elements where the search should be performed.
- `qt`: _query term_. The term to be found within the selected fields.

Example:

```bash
sellers?qf=name,description&qt=verizon
```

#### Multi case (deprecated)
For newer approach check [Enhanced Multi case](#enhanced-multi-case).\
Example:

- `qf`: _query field_. Map of field-value pairs.

```bash
sellers?qf={action=search, name=abc, memo=jqk}&anotherQueryParam=whatever
```

```java
// example decoding using Guava library.
Map map = Splitter.on(',').withKeyValueSeparator('=').split(qf.replaceAll("[\\[\\](){}]",""));
```

#### Enhanced Multi case

Example:

- `qf`: _query field_. Comma separated map of field-value pairs, multiple values for one field can be separated with `|` separator.
This separator should be translated to sql `OR` statement between given values of one field.
- `qo`: _query operator_. Optional, defines logic between given fields.
Defaults to `AND` when not provided in the request or value was malformed. Can be set to `OR` to mimic old multi case behaviour.
Values of this parameter are case insensitive so both `OR` and `or` will be parsed correctly.

```bash
#spaces between field-values pairs are optional
rules?qf={field1=val1|val2, field2=val3|val4, ...}[&qo=AND|OR]
```

```java
//example decoding using provided decoder
@Timed
@ExceptionMetered
@GetMapping
public ResponseEntity<SomeDTO> getSomeData(
    ..., @MultiValueSearchParams MultiValueQueryParams search, ...) {
  SomeDTO someDTO = ...
  // some implementation here
  return ResponseEntity.ok(someDTO);
}
```

```java
//example decoding using provided decoder and used OR operator
@Timed
@ExceptionMetered
@GetMapping
public ResponseEntity<SomeDTO> getSomeData(
    ..., @MultiValueSearchParams(operator = SearchQueryOperator.OR) MultiValueQueryParams search, ...) {
  SomeDTO someDTO = ...
  // some implementation here
  return ResponseEntity.ok(someDTO);
}
```


## Metrics

All _public_ methods implemented into a Controller class must include metrics annotation:

- `@Timed`.
- `@ExceptionMetered`.

Example:

```java
@Timed
@ExceptionMetered
@GetMapping()
public ResponseEntity<Page<SellerDTO>> findAll(...}
```

## Validations

Please take a look to dedicate page - [How-To: Bean Validations](./HOWTO-BEANVALIDATIONS.md)

