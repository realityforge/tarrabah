package org.realityforge.tarrabah;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GelfMessageChunkTest
{
  @Test
  public void basicParse()
    throws Exception
  {
    final byte[] payload = new byte[]
      {
        (byte)GelfHandler.CHUNKED_BYTE_PREFIX[0],
        (byte)GelfHandler.CHUNKED_BYTE_PREFIX[1],
        1, 0, 0, 0, 0, 0, 0, 0, // Message ID 1
        0, 2, //Chunk 1 of 2
        'a' // Data
      };
    final GelfMessageChunk chunk = new GelfMessageChunk( payload );

    assertEquals( chunk.getMessageID().longValue(), 1L );
    assertEquals( chunk.getChunkSequence(), 0 );
    assertEquals( chunk.getChunkCount(), 2 );
  }
}
