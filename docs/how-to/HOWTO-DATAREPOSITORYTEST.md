# How-To: Data Repository Test

This How-To explains some concepts related to test Spring Data Repositories.

## Information

Spring has a `@Sql` annotation to load sql scripts from the context. That approach is also the recommended one for future spring-boot testing.

The idea would be to use that approach to:

- Reduce test classes size.
- Reduce possible issues due to escaped strings on difficult to maintain java sentences.
- Facilitate tests maintenance through external sql formatted files.

## Requirements

- All repositories must be tested.
- All custom queries & native queries must be tested.

## Example: Integration Test

> NOTE: The following is just a basic example. More complex scenarios should be tested as well.

### Production Code

- Repository `**/repository/WhateverRepository.java` for entity `Whatever.java`:

```java
...
@Repository
public interface WhateverRepository extends JpaRepository<Whatever, Long>, JpaSpecificationExecutor<User> {}
```

- *SQL* file at `/data/repository/**`:

```sql
INSERT INTO whatever (pid, name, description, version)
VALUES (1, 'Foo', 'Bar', true, 0);
INSERT INTO whatever (pid, name, description, version)
VALUES (2, 'Hello', 'World', true, 0);
```

### Test Code

- SQL file `/data/repository/whatever-repository.sql`:

```sql
INSERT INTO whatever (pid, name, description, version)
VALUES (1, 'Foo', 'Bar', true, 0);
INSERT INTO whatever (pid, name, description, version)
VALUES (2, 'Hello', 'World', true, 0);
```

- Integration Test (IT) `**/repository/WhateverRepositoryIT.java`:

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    locations = {"classpath:applicationContext-test.xml", "classpath:applicationContext-core.xml"})
@Transactional(propagation = Propagation.REQUIRED)
@Sql(scripts = "/data/repository/whatever-repository.sql",config = @SqlConfig(encoding = "utf-8"))
public class WhateverRepositoryIT {
  @Autowired WhateverRepository whateverRepository;

  @Test
  public void shouldFindAll() {
    List<Whatever> result = whateverRepository.findAll();
    assertNotNull(result);
    assertFalse(result.isEmpty());
    // add some more verifications
  }

  @Test
  public void shouldFindAllById() {
    List<Whatever> result = whateverRepository.findAllById(1L);
    assertNotNull(result);
    // add some more verifications
  }
  
  ...

}
