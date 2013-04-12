package org.realityforge.tarrabah;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.realityforge.tarrabah.cdi.ext.pipeline.PipelineScoped;

@PipelineScoped
public class GelfUDPServer
  extends AbstractUDPServer
{
  protected ChannelPipelineFactory newPipelineFactory()
  {
    return new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        return Channels.pipeline( new GelfHandler() );
      }
    };
  }
}
