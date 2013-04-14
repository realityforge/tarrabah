package org.realityforge.tarrabah;

import com.google.gson.JsonObject;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.SimpleChannelHandler;

public abstract class BaseInputHandler
  extends SimpleChannelHandler
{
  private boolean _dnsLookup;

  protected void setDnsLookup( final boolean dnsLookup )
  {
    _dnsLookup = dnsLookup;
  }

  protected final JsonObject createBaseMessage( final InetSocketAddress remoteAddress, final String source )
  {
    final String hostName;
    if ( _dnsLookup )
    {
      hostName = remoteAddress.getAddress().getCanonicalHostName();
    }
    else
    {
      hostName = remoteAddress.getHostName();
    }

    final long currentTime = System.currentTimeMillis();

    final JsonObject object = new JsonObject();
    object.addProperty( "@source", source );
    object.addProperty( "@receive_host", hostName );
    object.addProperty( "@receive_port", remoteAddress.getPort() );
    object.addProperty( "@receive_time", currentTime );
    return object;
  }
}
