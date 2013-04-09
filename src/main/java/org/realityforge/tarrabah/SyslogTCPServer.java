package org.realityforge.tarrabah;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.realityforge.tarrabah.cdi.ext.pipeline.PipelineScoped;

@PipelineScoped
public class SyslogTCPServer
  extends AbstractSyslogServer
{
  private final int port = 8080;
  private boolean _nullTerminate;
  private ServerBootstrap _bootstrap;

  @PostConstruct
  public void postConstruct()
  {
    System.out.println( "SyslogTCPServer.postConstruct" );
    final ExecutorService bossThreadPool = Executors.newCachedThreadPool();
    final ExecutorService workerThreadPool = Executors.newCachedThreadPool();

    _bootstrap = new ServerBootstrap( new NioServerSocketChannelFactory( bossThreadPool, workerThreadPool ) );

    // Set up the pipeline factory.
    _bootstrap.setPipelineFactory( new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        final ChannelBuffer[] delimiter = _nullTerminate ? Delimiters.nulDelimiter() : Delimiters.lineDelimiter();
        return Channels.pipeline( new DelimiterBasedFrameDecoder( 2 * 1024 * 1024, delimiter ), new Handler() );
      }
    } );

    _bootstrap.bind( new InetSocketAddress( port ) );
  }

  @PreDestroy
  public void preDestroy()
  {
    System.out.println( "SyslogTCPServer.preDestroy" );
    if ( null != _bootstrap )
    {
      _bootstrap.shutdown();
      _bootstrap = null;
    }
  }
}
