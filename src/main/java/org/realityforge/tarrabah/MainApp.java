package org.realityforge.tarrabah;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;

public class MainApp
{
  public static void main( final String[] args )
  {
    final CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
    cdiContainer.boot();

    // Starting the application-context allows to use @PipelineScoped beans
    final ContextControl contextControl = cdiContainer.getContextControl();
    contextControl.startContext( PipelineScoped.class );
    contextControl.startContexts();

    Pipeline.activate( new Pipeline( "foo" ) );

    final SyslogUDPServer udpServer = BeanProvider.getContextualReference( SyslogUDPServer.class );

    System.out.println( "Active" );

    /*
    for ( int i = 0; i < 2; i++ )
    {
      try
      {
        Thread.sleep( 1000 );
      }
      catch ( final InterruptedException ie )
      {
        //ignored
      }
    }
    */

    contextControl.stopContext( PipelineScoped.class );
    Pipeline.deactivate();

    //contextControl.stopContexts();
    cdiContainer.shutdown();
    System.out.println( "Inactive" );
  }
}