# How-To: Dependency Injection (DI)

This How-To explains some concepts related to Dependency Injection (DI).

## Strategies

### Information

Current code has passed through different iterations. It means that same strategies can be found within the code.

This guide tries to analyze the different strategies, and assign a recommended/expected candidate.

- [Spring Framework Documentation](https://docs.spring.io/spring/docs/5.1.15.RELEASE/spring-framework-reference/core.html#beans-dependencies)
- [Spring DI Patterns: The Good, The Bad, and The Ugly](https://dzone.com/articles/spring-di-patterns-the-good-the-bad-and-the-ugly)

The expected (and recommended) strategy is **Constructor Level DI**. Said that, there are cases where the number of fields make code style checkers to complain about the number of fields.
That denotes a bad `class` design where the [Single Responsability Principle](https://en.wikipedia.org/wiki/Single-responsibility_principle) is broken.

> NOTE: Some `*Assembler.java` classes have a circular dependency. Due to tight relationships, difficult to decouple at this time, setter level strategy has been put in place only there as an exception.

#### Constructor Level (The Good)

```java
@Service
public class WhateverService {

  private final AnotherService anotherService;
  
  @Autowired
  public WhateverService(AnotherService anotherService) {
    this.anotherService = anotherService;
  }

}
```

*Pros*

- Dependencies can be immutable.
- Recommended by Spring.
- Easiest to test out of all the patterns.
- Highly coupled classes are easily identified as constructor parameters grow.
- Familiar to developers coming from other platforms.
- No dependency on the `@Autowired` annotation(i).

*Cons*

- Constructors trickle down to subclasses.
- Prone to circular dependency issues.

> (i) NOTE: Since Spring 4.3 annotation is not necessary. More information [here](https://spring.io/blog/2016/03/04/core-container-refinements-in-spring-framework-4-3).

#### Setter Level (The Ugly)

```java
@Service
public class WhateverService {

  private AnotherService anotherService;
  
  @Autowired
  public void setAnotherService(AnotherService anotherService) {
    this.anotherService = anotherService;
  }

}
```

*Pros*

- Immune to circular dependency issues.
- Highly coupled classes are easily identified as setters are added.

*Cons*

- Violates encapsulation.
- Circular dependencies are hidden.
- The most boilerplate code of the three patterns.
- Dependencies are unnecessarily mutable.

#### Field Level (The Bad)

```java
@Service
public class WhateverService {

  @Autowired private AnotherService anotherService;

}
```

*Pros*

- The tersest of all the patterns.
- Most Java developers are aware of this pattern.

*Cons*

- Convenience tends to hide code design red flags.
- Hard to test.
- Dependencies are unnecessarily mutable.
- Prone to circular dependency issues.
- Requires the use of (multiple) Spring or Java EE annotations.

### Spring

Spring Framework recommends the use of `Constructor Level` strategy. Please read carefully the following information obtained from the official documentation.

#### Constructor-based or setter-based DI

> Since you can mix constructor-based and setter-based DI, it is a good rule of thumb to use constructors for mandatory dependencies and setter methods or configuration methods for optional dependencies.
> Note that use of the @Required annotation on a setter method can be used to make the property be a required dependency; however, constructor injection with programmatic validation of arguments is preferable.
> The Spring team generally advocates constructor injection, as it lets you implement application components as immutable objects and ensures that required dependencies are not null.
> Furthermore, constructor-injected components are always returned to the client (calling) code in a fully initialized state.
> As a side note, a large number of constructor arguments is a bad code smell, implying that the class likely has too many responsibilities and should be refactored to better address proper separation of concerns.
> Setter injection should primarily only be used for optional dependencies that can be assigned reasonable default values within the class.
> Otherwise, not-null checks must be performed everywhere the code uses the dependency. One benefit of setter injection is that setter methods make objects of that class amenable to reconfiguration or re-injection later.
> Management through JMX MBeans is therefore a compelling use case for setter injection.
> Use the DI style that makes the most sense for a particular class. Sometimes, when dealing with third-party classes for which you do not have the source, the choice is made for you.
> For example, if a third-party class does not expose any setter methods, then constructor injection may be the only available form of DI.

#### Circular dependencies

> If you use predominantly constructor injection, it is possible to create an unresolvable circular dependency scenario.
> For example: Class A requires an instance of class B through constructor injection, and class B requires an instance of class A through constructor injection.
> If you configure beans for classes A and B to be injected into each other, the Spring IoC container detects this circular reference at runtime, and throws a BeanCurrentlyInCreationException.
> One possible solution is to edit the source code of some classes to be configured by setters rather than constructors. Alternatively, avoid constructor injection and use setter injection only.
> In other words, although it is not recommended, you can configure circular dependencies with setter injection.
> Unlike the typical case (with no circular dependencies), a circular dependency between bean A and bean B forces one of the beans to be injected into the other prior to being fully initialized itself (a classic chicken-and-egg scenario).
