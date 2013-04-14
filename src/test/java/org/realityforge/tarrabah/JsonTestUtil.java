package org.realityforge.tarrabah;

import com.google.gson.JsonObject;

public final class JsonTestUtil
{
  private JsonTestUtil()
  {
  }

  public static long getAsLong( final JsonObject message, final String memberName )
  {
    return message.getAsJsonPrimitive( memberName ).getAsLong();
  }

  public static int getAsInt( final JsonObject message, final String memberName )
  {
    return message.getAsJsonPrimitive( memberName ).getAsInt();
  }

  public static String getAsString( final JsonObject message, final String memberName )
  {
    return message.getAsJsonPrimitive( memberName ).getAsString();
  }
}
