package org.realityforge.tarrabah.cdi.ext.pipeline;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.enterprise.inject.spi.Bean;

final class PipelineEntry
{
  @Nonnull
  private final Pipeline _pipeline;
  private final PipelineContext _context;
  private Map _store;

  PipelineEntry( @Nonnull final Pipeline pipeline, final PipelineContext context )
  {
    _pipeline = pipeline;
    _context = context;
    _pipeline.setEntry( this );
  }

  @SuppressWarnings( "unchecked" )
  @Nonnull
  final <T> Map<Bean<T>, BeanEntry<T>> getStore()
  {
    if ( null == _store )
    {
      _store = new HashMap();
    }
    return (Map<Bean<T>, BeanEntry<T>>) _store;
  }

  final void detach()
  {
    _context.unloadPipeline( _pipeline );
    _store = null;
  }
}
