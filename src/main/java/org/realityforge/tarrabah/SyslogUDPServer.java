package org.realityforge.tarrabah;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

@ApplicationScoped
public class SyslogUDPServer
  extends AbstractSyslogServer
{
  private final int _port = 8080;

  @Nullable
  private ConnectionlessBootstrap _bootstrap;

  @PostConstruct
  public void postConstruct()
  {
    System.out.println( "Starting syslog server..." );
    final ExecutorService executorService = Executors.newCachedThreadPool();
    _bootstrap = new ConnectionlessBootstrap( new NioDatagramChannelFactory( executorService ) );

    _bootstrap.setOption( "receiveBufferSize", 1048576 );
    _bootstrap.setOption( "receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory( 1024 * 8 ) );

    // Set up the pipeline factory.
    _bootstrap.setPipelineFactory( new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        return Channels.pipeline( new Handler() );
      }
    } );

    // Bind and start to accept incoming connections.
    _bootstrap.bind( new InetSocketAddress( _port ) );
  }

  @PreDestroy
  public void preDestroy()
  {
    if ( null != _bootstrap )
    {
      _bootstrap.shutdown();
      _bootstrap = null;
    }
  }
}
