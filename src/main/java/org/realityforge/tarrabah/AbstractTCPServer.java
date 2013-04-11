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
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public abstract class AbstractTCPServer
{
  @Inject
  private Logger _logger;

  private int _port = 8080;

  @Nullable
  private ServerBootstrap _bootstrap;
  @Nullable
  private ExecutorService _executorService;
  private ExecutorService _bossExecutorService;

  @PostConstruct
  public void postConstruct()
  {
    _bossExecutorService = Executors.newCachedThreadPool();
    _executorService = Executors.newCachedThreadPool();

    _bootstrap =
      new ServerBootstrap( new NioServerSocketChannelFactory( _bossExecutorService, _executorService ) );

    // Set up the pipeline factory.
    _bootstrap.setPipelineFactory( newPipelineFactory() );

    if ( _logger.isLoggable( Level.INFO ) )
    {
      _logger.info( "Binding TCP Server to port " + _port );
    }

    _bootstrap.bind( new InetSocketAddress( _port ) );
  }

  protected abstract ChannelPipelineFactory newPipelineFactory();

  @PreDestroy
  public void preDestroy()
  {
    if ( null != _bootstrap )
    {
      if ( _logger.isLoggable( Level.INFO ) )
      {
        _logger.info( "Shutting down TCP server on port " + _port );
      }

      _bootstrap.shutdown();
      _bootstrap = null;
    }
    if ( null != _executorService )
    {
      _executorService.shutdown();
      _executorService = null;
    }
    if( null != _bossExecutorService )
    {
      _bossExecutorService.shutdown();
      _bossExecutorService = null;
    }
  }
}
