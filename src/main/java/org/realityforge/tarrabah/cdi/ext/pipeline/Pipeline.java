package org.realityforge.tarrabah.cdi.ext.pipeline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Pipeline
{
  private static InheritableThreadLocal<Pipeline> c_current = new InheritableThreadLocal<Pipeline>();

  @Nonnull
  private final String _name;
  @Nullable
  private PipelineEntry _entry;

  public synchronized static Pipeline current()
  {
    return c_current.get();
  }

  public synchronized static void activate( @Nonnull final Pipeline pipeline )
  {
    c_current.set( pipeline );
  }

  public synchronized static void deactivate()
  {
    final Pipeline pipeline = current();
    if( null != pipeline && null != pipeline._entry )
    {
      pipeline._entry.detach();
    }
    c_current.set( null );
  }

  public Pipeline( @Nonnull final String name )
  {
    _name = name;
  }

  @Nonnull
  public String getName()
  {
    return _name;
  }

  final void setEntry( @Nullable final PipelineEntry entry )
  {
    _entry = entry;
  }
}
