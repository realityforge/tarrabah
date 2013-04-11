package org.realityforge.tarrabah;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.realityforge.tarrabah.cdi.ext.pipeline.PipelineScoped;

@PipelineScoped
public class SyslogTCPServer
  extends AbstractTCPServer
{
  private boolean _nullTerminate;

  protected ChannelPipelineFactory newPipelineFactory()
  {
    return new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        final ChannelBuffer[] delimiter = _nullTerminate ? Delimiters.nulDelimiter() : Delimiters.lineDelimiter();
        return Channels.pipeline( new DelimiterBasedFrameDecoder( 2 * 1024 * 1024, delimiter ), new SyslogHandler() );
      }
    };
  }
}
