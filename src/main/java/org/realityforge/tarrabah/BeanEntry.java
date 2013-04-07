package org.realityforge.tarrabah;

import javax.annotation.Nonnull;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

class BeanEntry<T>
{
  @Nonnull
  private final Contextual<T> _contextual;
  @Nonnull
  private final CreationalContext<T> _creationalContext;
  @Nonnull
  private final T _instance;

  BeanEntry( @Nonnull final Contextual<T> contextual,
             @Nonnull final CreationalContext<T> creationalContext,
             @Nonnull final T instance )
  {
    _contextual = contextual;
    _creationalContext = creationalContext;
    _instance = instance;
  }

  @Nonnull
  Contextual<T> getContextual()
  {
    return _contextual;
  }

  @Nonnull
  CreationalContext<T> getCreationalContext()
  {
    return _creationalContext;
  }

  @Nonnull
  T getInstance()
  {
    return _instance;
  }
}
