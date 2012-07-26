package org.realityforge.tarrabah;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

@ApplicationScoped
public class SyslogUDPServer
  extends AbstractSyslogServer
{
  private final int port = 8080;

  @Inject
  @Parameters
  private List<String> validParams;

  public void init( @Observes ContainerInitialized init )
  {
    System.out.println( "Starting syslog server..." );
    final ExecutorService executorService = Executors.newCachedThreadPool();
    final ConnectionlessBootstrap bootstrap =
      new ConnectionlessBootstrap( new NioDatagramChannelFactory( executorService ) );

    bootstrap.setOption( "receiveBufferSize", 1048576 );
    bootstrap.setOption( "receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory( 1024 * 8 ) );

    // Set up the pipeline factory.
    bootstrap.setPipelineFactory( new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        return Channels.pipeline( new Handler() );
      }
    } );

    // Bind and start to accept incoming connections.
    bootstrap.bind( new InetSocketAddress( port ) );
  }
}
