package org.realityforge.tarrabah;

import com.google.gson.JsonObject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.inject.Inject;
import org.testng.annotations.Test;
import static org.realityforge.tarrabah.JsonTestUtil.*;
import static org.testng.Assert.*;

public class BaseInputHandlerTest
  extends AbstractContainerTest
{
  @Inject
  private SyslogHandler _handler;

  @Test
  public void createBaseMessage()
    throws Exception
  {
    final InetAddress localHost = InetAddress.getLocalHost();
    final int port = 2002;
    final InetSocketAddress remoteAddress = new InetSocketAddress( localHost, port );
    final String source = "X";
    final JsonObject message = _handler.createBaseMessage( remoteAddress, source );

    assertEquals( getAsString( message, "@source" ), source );
    assertEquals( getAsInt( message, "@receive_port" ), port );
    assertEquals( getAsString( message, "@receive_host" ), localHost.getHostName() );
    final long receiveTime = getAsLong( message, "@receive_time" );
    assertTrue( System.currentTimeMillis() - receiveTime < 1000 );
  }

  @Test
  public void createBaseMessage_dnsLookup_isTrue()
    throws Exception
  {
    final InetSocketAddress remoteAddress = new InetSocketAddress( InetAddress.getLocalHost(), 2002 );
    final JsonObject message = _handler.createBaseMessage( remoteAddress, "X" );

    assertEquals( JsonTestUtil.getAsString( message, "@receive_host" ),
                  remoteAddress.getAddress().getCanonicalHostName() );
  }

}
