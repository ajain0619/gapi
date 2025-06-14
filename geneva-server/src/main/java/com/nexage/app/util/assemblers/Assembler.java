package com.nexage.app.util.assemblers;

import com.nexage.app.util.assemblers.context.AssemblerContext;
import java.util.Set;

/**
 * Bean mapping base class. This is considered Legacy and we are trying to avoid its use.
 *
 * @param <T>
 * @param <S>
 * @param <U> of type {@link AssemblerContext}
 * @deprecated Please use {@link org.mapstruct} instead.
 */
@Deprecated
public abstract class Assembler<T, S, U extends AssemblerContext> {

  public abstract T make(U context, final S model);

  public abstract T make(U context, final S model, final Set<String> fields);

  public abstract S apply(U context, S model, final T dto);
}
