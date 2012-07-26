package org.realityforge.tarrabah;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.realityforge.jsyslog.message.Facility;
import org.realityforge.jsyslog.message.Severity;
import org.realityforge.jsyslog.message.StructuredDataParameter;
import org.realityforge.jsyslog.message.SyslogMessage;

public abstract class AbstractSyslogServer
{
  private static final Logger LOG = Logger.getLogger( AbstractSyslogServer.class.getName() );

  private boolean _dnsLookup;

  void processSyslogMessage( final InetSocketAddress remoteAddress, final SocketAddress localAddress, final String rawMessage )
  {
    final SyslogMessage message = parseSyslogMessage( rawMessage );
    final String source = "syslog:" + localAddress;

    final JSONObject object = createBaseMessage( remoteAddress, source );
    mergeSyslogFields( message, object );
    System.out.println( "Message: " + object.toJSONString() );
  }

  private JSONObject createBaseMessage( final InetSocketAddress remoteAddress, final String source )
  {
    final String hostName;
    if( _dnsLookup )
    {
      hostName = remoteAddress.getAddress().getCanonicalHostName();
    }
    else
    {
      hostName = remoteAddress.getHostName();
    }

    final long currentTime = System.currentTimeMillis();


    final JSONObject object = new JSONObject();
    object.put( "@source", source );
    object.put( "@receive_host", hostName );
    object.put( "@receive_port", remoteAddress.getPort() );
    object.put( "@receive_time", currentTime );
    return object;
  }

  private void mergeSyslogFields( final SyslogMessage syslogMessage, final JSONObject object )
  {
    final String hostname = syslogMessage.getHostname();
    if( null != hostname )
    {
      object.put( "appName", hostname );
    }
    final String appName = syslogMessage.getAppName();
    if( null != appName )
    {
      object.put( "appName", appName );
    }
    final String message = syslogMessage.getMessage();
    if( null != message )
    {
      object.put( "message", message );
    }
    final String msgId = syslogMessage.getMsgId();
    if( null != msgId )
    {
      object.put( "msgId", msgId );
    }
    final String procId = syslogMessage.getProcId();
    if( null != procId )
    {
      object.put( "procId", procId );
    }
    final Facility facility = syslogMessage.getFacility();
    if( null != facility )
    {
      object.put( "facility", facility.name().toLowerCase() );
    }
    final Severity severity = syslogMessage.getLevel();
    if( null != severity )
    {
      object.put( "severity", severity.name().toLowerCase() );
    }
    final DateTime timestamp = syslogMessage.getTimestamp();
    if( null != timestamp )
    {
      object.put( "timestamp", timestamp.toString() );
      object.put( "timestamp_epoch", timestamp.toDate().getTime() / 1000 );
    }
    final Map<String, List<StructuredDataParameter>> structuredData = syslogMessage.getStructuredData();
    if( null != structuredData )
    {
      //TODO: Insert injection of structuredData here...
    }
  }


  private SyslogMessage parseSyslogMessage( final String rawMessage )
  {
    try
    {
      return SyslogMessage.parseStructuredSyslogMessage( rawMessage );
    }
    catch( final Exception e )
    {
      return SyslogMessage.parseRFC3164SyslogMessage( rawMessage );
    }
  }


  protected final class Handler
    extends SimpleChannelHandler
  {
    @Override
    public void messageReceived( final ChannelHandlerContext context,
                                 final MessageEvent e )
      throws Exception
    {
      final InetSocketAddress remoteAddress = (InetSocketAddress) e.getRemoteAddress();

      final ChannelBuffer buffer = (ChannelBuffer) e.getMessage();

      final byte[] readable = new byte[ buffer.readableBytes() ];
      buffer.toByteBuffer().get( readable, buffer.readerIndex(), buffer.readableBytes() );

      final SocketAddress localAddress = context.getChannel().getLocalAddress();
      final String rawMessage = new String( readable );
      processSyslogMessage( remoteAddress, localAddress, rawMessage );
    }

    @Override
    public void exceptionCaught( final ChannelHandlerContext context, final ExceptionEvent e )
      throws Exception
    {
      LOG.log( Level.WARNING, "Problem receiving syslog packet.", e.getCause() );
    }
  }
}
