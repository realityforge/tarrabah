package org.realityforge.tarrabah;

import javax.inject.Inject;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.realityforge.tarrabah.cdi.ext.pipeline.PipelineScoped;

@PipelineScoped
public class GelfUDPServer
  extends AbstractUDPServer
{
  @Inject
  private GelfHandler _handler;

  protected ChannelPipelineFactory newPipelineFactory()
  {
    return new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline()
        throws Exception
      {
        return Channels.pipeline( _handler );
      }
    };
  }
}
