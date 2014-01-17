package org.realityforge.tarrabah;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.realityforge.tarrabah.cdi.ext.pipeline.Pipeline;
import org.realityforge.tarrabah.cdi.ext.pipeline.PipelineScoped;

public class MainApp
{
  static class Foo
  {
    @Inject
    private SyslogTCPServer _s;

    @Inject
    private GelfUDPServer _gelfUDPServer;
  }
  public static void main( final String[] args )
  {
    final CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
    cdiContainer.boot();

    // Starting the application-context allows to use @PipelineScoped beans
    final ContextControl contextControl = cdiContainer.getContextControl();
    contextControl.startContext( ApplicationScoped.class );
    //contextControl.startContext( PipelineScoped.class );
    contextControl.startContexts();

    final Pipeline foo = new Pipeline( "foo" );
    Pipeline.activate( foo );

    final BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
    final CreationalContext<Foo> context = bm.createCreationalContext( null );
    final InjectionTarget<Foo> injectionTarget = bm.createInjectionTarget( bm.createAnnotatedType( Foo.class ) );
    final Foo instance = new Foo();
    //final Foo instance = injectionTarget.produce( context );
    injectionTarget.inject( instance, context );
    injectionTarget.postConstruct( instance );
    //final SyslogTCPServer tcpServer = BeanProvider.getContextualReference( SyslogTCPServer.class );

    System.err.println( "Active" );

    for ( int i = 0; i < 2000; i++ )
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

    final ObjectMapper mapper = new ObjectMapper();
    try
    {
      mapper.writeValue( System.out, instance._s );
    }
    catch ( IOException e )
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    contextControl.stopContext( PipelineScoped.class );
    Pipeline.deactivate();

    context.release();
    //injectionTarget.preDestroy( instance );
    //injectionTarget.dispose( instance );

    //contextControl.stopContexts();
    cdiContainer.shutdown();
    System.err.println( "Inactive" );
  }
}
