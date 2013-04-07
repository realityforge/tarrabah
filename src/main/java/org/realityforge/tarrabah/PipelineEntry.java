package org.realityforge.tarrabah;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

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
  final <T> Map<String, BeanEntry<T>> getStore()
  {
    if ( null == _store )
    {
      _store = new HashMap();
    }
    return (Map<String, BeanEntry<T>>) _store;
  }

  final void detach()
  {
    System.out.println( "PipelineEntry.detach" );
    _context.unloadPipeline( _pipeline );
    _store = null;
  }
}
