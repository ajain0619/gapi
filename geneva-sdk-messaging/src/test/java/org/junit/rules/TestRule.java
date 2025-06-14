package org.junit.rules;

/*This interface was added because testcontainers library was preventing us to totally remove junit4 from the classpath
Solution was described here: https://github.com/testcontainers/testcontainers-java/issues/970
This interface should be deleted when the testcontainers library is updated */
@SuppressWarnings("unused")
public interface TestRule {}
