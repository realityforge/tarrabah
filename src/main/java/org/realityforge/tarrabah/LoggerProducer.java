package org.realityforge.tarrabah;

import java.util.logging.Logger;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Logging producer for injectable logger.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class LoggerProducer
{
  @Produces
  public Logger produceLogger( final InjectionPoint injectionPoint )
  {
    return Logger.getLogger( injectionPoint.getMember().getDeclaringClass().getName() );
  }
}
