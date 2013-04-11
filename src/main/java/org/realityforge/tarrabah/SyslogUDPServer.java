package org.realityforge.tarrabah;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.realityforge.tarrabah.cdi.ext.pipeline.PipelineScoped;

@PipelineScoped
public class SyslogUDPServer
{
  @Inject
  private Logger _logger;

  private int _port = 8080;

  @Nullable
  private ConnectionlessBootstrap _bootstrap;
  private ExecutorService _executorService;

  @PostConstruct
  public void postConstruct()
  {
    _executorService = Executors.newCachedThreadPool();
    _bootstrap = new ConnectionlessBootstrap( new NioDatagramChannelFactory( _executorService ) );

    _bootstrap.setOption( "receiveBufferSize", 1048576 );
    _bootstrap.setOption( "receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory( 1024 * 8 ) );

    // Set up the pipeline factory.
    _bootstrap.setPipelineFactory( new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        return Channels.pipeline( new SyslogHandler() );
      }
    } );

    if ( _logger.isLoggable( Level.INFO ) )
    {
      _logger.info( "Binding UDP Server to port " + _port );
    }
    // Bind and start to accept incoming connections.
    _bootstrap.bind( new InetSocketAddress( _port ) );
  }

  @PreDestroy
  public void preDestroy()
  {
    if ( null != _bootstrap )
    {
      if ( _logger.isLoggable( Level.INFO ) )
      {
        _logger.info( "Shutting down UDP server on port " + _port );
      }

      _bootstrap.shutdown();
      _bootstrap = null;
    }
    if ( null != _executorService )
    {
      _executorService.shutdown();
      _executorService = null;
    }
  }
}
