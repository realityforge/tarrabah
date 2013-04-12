package org.realityforge.tarrabah;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import javax.inject.Inject;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class GelfHandler
  extends SimpleChannelHandler
{
  @Inject
  private Logger _logger;

  @Override
  public void messageReceived( final ChannelHandlerContext context,
                               final MessageEvent e )
    throws Exception
  {
    //final InetSocketAddress remoteAddress = (InetSocketAddress) e.getRemoteAddress();
    final ChannelBuffer buffer = (ChannelBuffer) e.getMessage();

    final byte[] readable = new byte[ buffer.readableBytes() ];
    buffer.toByteBuffer().get( readable, buffer.readerIndex(), buffer.readableBytes() );

    //final SocketAddress localAddress = context.getChannel().getLocalAddress();
    if ( readable.length > 2 && 0x78 == readable[ 0 ] && 0x9C == readable[ 1 ] )
    {
      // ZLIB'd
      processJsonMessage( new DeflaterInputStream( new ByteArrayInputStream( readable ) ) );
    }
    else if ( readable.length > 2 && 0x1F == readable[ 0 ] && 0x8B == readable[ 1 ] )
    {
      //GZIP'd
      processJsonMessage( new GZIPInputStream( new ByteArrayInputStream( readable ) ) );
    }
    else if ( readable.length > 2 && 0x1E == readable[ 0 ] && 0x0F == readable[ 1 ] )
    {
      //Chunked
    }
    else
    {
      //plain
      processJsonMessage( new ByteArrayInputStream( readable ) );
    }
  }

  private void processJsonMessage( final InputStream input )
  {
    final JsonParser parser = new JsonParser();
    final JsonElement element = parser.parse( new InputStreamReader( input ) );
    if ( !( element instanceof JsonObject ) )
    {
      //Error
    }
    final JsonObject object = (JsonObject) element;
  }

  @Override
  public void exceptionCaught( final ChannelHandlerContext context, final ExceptionEvent e )
    throws Exception
  {
    _logger.log( Level.WARNING, "Problem receiving gelf packet.", e.getCause() );
  }
}
