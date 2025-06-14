package com.nexage.admin.core.batch;

@FunctionalInterface
public interface CheckedBiConsumer<T, U, E extends Exception> {

  /**
   * Consumer method that throws a Checked Exception
   *
   * @param t first parameter
   * @param u second parameter
   * @throws E Checked Exception to be caught
   */
  void accept(T t, U u) throws E;
}
