# How-To: Model Layer

## Domain Classes Naming

While building an API, there will be an overlap between the model classes sent to downstream 
services and the ones used to communicate with the clients.

In Java, classes with same name cannot be used within the same class, without one being referenced by its package.
That tends to cause confusion and spaghetti code. So, although downstream and upstream domain classes are both DTO (Data Transfer Objects)
we have decided to differentiate them within the name of the class/file:

- Simple class: Entity level. Example: `Company.java`
- DTO class: Client level. Example: `SellerDTO.java`
 
Workflow:

```

(Client) <--> SellerDTO.java <--> SellerDTOMapper.java <--> Company.java <--> (DataSource) 

```

> NOTE: Current code includes different strategies which potentially could drive to confusion. 
> Please use this guide as expected functionality.

### Client DTO Classes

`DTO` subfix is not included on fields names, only classes ones.

Example: 

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class WhateverDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include @ToString.Include private Long pid;
  @ToString.Include private FieldDTO field;
}
``` 

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class FieldDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include @ToString.Include private Long pid;
  @EqualsAndHashCode.Include @ToString.Include private String name;
  @ToString.Include private String description;
}
``` 

Request/Response:

```json
{
  "field": {
    "pid": 1,
    "name": "hello",
    "description": "whatever description"
  }
}
```

- `FieldDTO` instance does not include *DTO* within the name `field` at `WhateverDTO` class.
- Request/Response contract do not include *DTO* within the name.
- Be sure a DTO implements `Serializable` interface.
- Be sure a number is given as [serialVersionUID](https://www.baeldung.com/java-serial-version-uid).
- Be sure you understand what makes an instance unique. `equals/hashCode` methods are going to rely on that for comparison.

#### Lombok

When using Lombok annotations, you must understand what makes an instance of the class unique. See the __Entity Classes__ section below for more details.

### Entity Classes

#### Lombok: Data

When using `@Data` into classes to remove boilerplate, the library will generate:
- getters
- setters
- toString
- equals and hashCode.

Using `@Data` at entity level could sound like a good idea, but be careful. 
Although it is really important to have our classes defined accordingly and override those 
methods with our proper expected business logic this could be risky: JPA classes normally include circular dependencies due to entity mappings:

Example:

```java
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SiteMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private String pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  private Site site;
}
``` 

```java
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Site implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include 
  @ToString.Include 
  private String pid;

  @OneToMany(
    fetch = FetchType.LAZY,
    mappedBy = "site",
    cascade = {CascadeType.ALL})
  @ToString.Exclude // <-- this will avoid a StackOverflowError 
  private Set<SiteMetrics> metrics = new HashSet<>();
}
```

##### Lombok for `toString()`

- If use `@Data` but not `@ToString(onlyExplicitlyIncluded = true)`:
  - `@ToString.Exclude` on possible circular dependencies. Recommended.
  - Or override your own `toString()` method.
- Include unique parameters. At database level, unique parameters define the uniqueness of an entry, same at JPA level.
- Avoid non-sensitive parameters (passwords, credentials, credit cards...).
- Do not include relationships to avoid circular dependencies and memory overflow.

##### Lombok for `hashCode()` & `equals()`

- If use `@Data` and `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`:
  - `@EqualsAndHashCode.Include` on fields that you want to print. Don't do it on relations. Recommended.
  - Or override your own `hashCode()` & `equals()` methods.
- Include unique parameters. At database level, unique parameters define the uniqueness of an entry, same at JPA level.
- Do not include relationships to avoid circular dependencies and memory overflow.

> NOTE: Be sure you understand how POJOs work, and the importance of hashCode/equals methods on classes, with an extra attention to Collections.
> More info [here](https://vladmihalcea.com/hibernate-facts-equals-and-hashcode/).

## Bean Mapping

To facilitate inter-bean mappings we provide two solutions:

- [MapStruct](http://mapstruct.org/) (_Recommended_).
- Custom Assemblers (When MapStruct does not cover the needs).

## Nested Collections

A common pattern in hibernate is that both **create** and **update** endpoints will follow a similar flow where the **update** endpoint will create a new instance of the model class and then hibernate will automatically replace the existing database entity (identified by the primary key) with the updated contents.
Typically, this approach is preferred because it allows the **update** endpoint to function without querying the current state of the database.
> Note: The *CrudRepository<>.existsById(...)* method can be used to validate if entity exists without querying all of its attributes.

### Exceptions

It is not always possible to follow this pattern. 
One instance where this pattern does not work is when your model entity has a nested collection (e.g. see the **FeeAdjustment** endpoint) which has a uniqueness constraint enforced at the database level. 

In this instance, the recommended approach is to query the existing database entity by calling *CrudRepository<>.findById(...)* and modifying the existing entity directly.
This will cause the collections to be populated with the contents from the database which will allow hibernate to avoid reinserting the existing entities.
